package com.example.grpc.nameresolve

import io.grpc.NameResolver
import io.grpc.NameResolverProvider
import java.net.URI

internal class ExampleNameResolverProvider : NameResolverProvider() {
  override fun newNameResolver(targetUri: URI?, args: NameResolver.Args?): NameResolver {
    return ExampleNameResolver(targetUri)
  }

  override fun isAvailable(): Boolean {
    return true
  }

  override fun priority(): Int {
    return 5
  }

  override fun getDefaultScheme(): String {
    return Settings.SCHEME
  }
}
