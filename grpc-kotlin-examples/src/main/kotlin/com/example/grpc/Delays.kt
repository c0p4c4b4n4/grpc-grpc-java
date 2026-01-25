package com.example.grpc

import java.util.concurrent.TimeUnit

interface Delays {

  companion object {
    fun sleep(seconds: Int) {
      try {
        TimeUnit.SECONDS.sleep(seconds.toLong())
      } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        throw RuntimeException(e)
      }
    }
  }
}
