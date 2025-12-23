package echo

import io.grpc.Server
import java.util.concurrent.TimeUnit

object Shutdown {
    fun init(server: Server) {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            System.err.println("server is shutting down")
            try {
                server.shutdown().awaitTermination(10, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                System.err.println("server shutdown was interrupted")
                server.shutdownNow()
            }
            System.err.println("server has been shut down")
        }))
    }
}
