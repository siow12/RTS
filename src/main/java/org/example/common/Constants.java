package org.example.common;

public class Constants {

    //Simulation Data
    public static final double cruisingHeightMin = 31000;
    public static final double cruisingHeightMax = 38000;
    public static final double LandingHeight = 1000;
    public static final double pressureMin = 400;
    public static final double pressureMax= 1013;

    //Simulation Config
    public static final long takeOff = 10;
    public static final long cruising = 10;
    public static final long postLanding = 5;
    public static final long landing = 5;
    public static final long interval = 1000;
    public static final long totalTime = takeOff+cruising+postLanding+landing;
    public static final long takeOffState = totalTime-takeOff;
    public static final long cruisingState = totalTime-takeOff-cruising;
    public static final long postLandingState = totalTime-takeOff-cruising-postLanding;
    public static final long landingState = totalTime-takeOff-cruising-postLanding-postLandingState;


    //public static final double
}
