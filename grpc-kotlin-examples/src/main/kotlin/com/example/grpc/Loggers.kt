package com.example.grpc

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

interface Loggers {

  companion object {
    fun init() {
      System.setProperty(
        "java.util.logging.SimpleFormatter.format",
        $$"%1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s - %5$s%6$s%n"
      )
    }
  }
}
