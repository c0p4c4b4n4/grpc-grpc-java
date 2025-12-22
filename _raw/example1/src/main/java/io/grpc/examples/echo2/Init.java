package io.grpc.examples.echo2;

public class Init {

    public static void loggingFormat() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS [%7$s] %4$s %2$s %5$s%6$s%n");
    }
}
