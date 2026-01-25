package com.example.grpc.nameresolve

internal interface Settings {
  companion object {
    const val SCHEME: String = "example"
    const val SERVICE_NAME: String = "example.grpc.loadbalance"
    @JvmField
    val SERVER_PORTS: IntArray = intArrayOf(50051, 50052, 50053)
  }
}
