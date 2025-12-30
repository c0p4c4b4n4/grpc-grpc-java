package com.example.grpc;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Loggers {

    public static void init() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s - %5$s%6$s%n");
    }

    public static void initWithGrpcLogs() {
        init();

        Logger grpcLogger = Logger.getLogger("io.grpc");
        grpcLogger.setLevel(Level.FINE);

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.FINE);
            }
        }
    }
}
