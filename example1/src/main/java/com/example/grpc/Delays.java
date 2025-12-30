package com.example.grpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class Delays {

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
