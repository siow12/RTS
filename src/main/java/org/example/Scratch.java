package org.example;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scratch {
    public static void main(String[] args) throws InterruptedException {

        CountDownLatch lock = new CountDownLatch((int) Constants.totalTime);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime()
                .availableProcessors());

        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                lock.countDown();
            }
        }, 0, Constants.interval);
        var task = executor.scheduleAtFixedRate(new Task(lock), 0, Constants.interval, TimeUnit.MILLISECONDS);

        lock.await();
        timer.cancel();
        task.cancel(true);
        executor.shutdown();
    }
}


class Constants {
    public static final long takeOff = 10;
    public static final long cruising = 10;
    public static final long postLanding = 5;
    public static final long landing = 5;
    public static final long interval = 1000;
    public static final long totalTime = takeOff + cruising + postLanding + landing;
    public static final long takeOffState = totalTime - takeOff;
    public static final long cruisingState = totalTime - takeOff - cruising;
    public static final long postLandingState = totalTime - takeOff - cruising - postLanding;
    public static final long landingState = totalTime - takeOff - cruising - postLanding - postLandingState;

}

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
class Task implements Runnable {

    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        long count = countDownLatch.getCount();
        if (countDownLatch.getCount() > Constants.takeOffState) {

            System.out.println(count + ": " + (Constants.totalTime - count));
        } else if (countDownLatch.getCount() > Constants.cruisingState) {
            System.out.println(count);
        } else if (countDownLatch.getCount() > Constants.postLandingState) {
            System.out.println(count);
        } else if (countDownLatch.getCount() > Constants.landingState) {
            System.out.println(count);
        }
    }
}
