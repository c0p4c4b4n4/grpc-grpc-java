package com.example.grpc

import java.util.concurrent.TimeUnit

object  /*TODO*/ Delays {
    fun sleep(seconds: Int) {
        try {
            TimeUnit.SECONDS.sleep(seconds.toLong())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException(e)
        }
    }
}
