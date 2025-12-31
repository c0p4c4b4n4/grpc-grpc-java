package com.example.grpc;

import java.util.concurrent.TimeUnit;

public final class Delays {

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
