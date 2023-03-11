package org.example;

import java.util.concurrent.atomic.AtomicReference;

public class SharedObject {
    private final AtomicReference<MyObject> reference = new AtomicReference<>(null);

    public void setValue(int value) {
        reference.updateAndGet(obj -> {
            obj.setIntValue(value);
            return obj;
        });
    }

    public int getValue() {
        return reference.get().getIntValue();
    }
}

class MyObject {
    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}





