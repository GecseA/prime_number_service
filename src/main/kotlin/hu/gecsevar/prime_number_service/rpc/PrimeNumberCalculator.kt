package hu.gecsevar.prime_number_service.rpc

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object PrimeNumberCalculator {

    private lateinit var threadPool : ScheduledExecutorService
    private var callableTasks = mutableListOf<Thread>()

    // TODO start max 5 thread
    fun start() {
        threadPool = Executors.newScheduledThreadPool(5)
        threadPool.let { th ->
            repeat(5) {_ ->
                val temp = Worker()
                callableTasks.add(temp)
                th.submit(
                    temp
                )
            }
        }
    }

    // TODO stop
    fun stop() {
        callableTasks.forEach {
            it.interrupt()
        }
        callableTasks.clear()
        threadPool.shutdownNow()
        threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)
    }
}
