package com.example.grpc;

import java.util.concurrent.TimeUnit;

public final class /*TODO*/ Delays {

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
