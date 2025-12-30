package com.example.grpc;

public final class Delays {

    public static void sleep(int seconds) {
        try {
            Thread.sleep(1000L *seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
