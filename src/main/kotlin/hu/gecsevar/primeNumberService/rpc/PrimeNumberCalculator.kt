package hu.gecsevar.primeNumberService.rpc

import hu.gecsevar.primeNumberService.properties.AppEnvironment
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExceededMaxThreadCountException : Exception() {}
class AlreadyRunningException : Exception() {}

/**
 * Thread pool
 */
object PrimeNumberCalculator {

    private lateinit var threadPool : ScheduledExecutorService
    private var callableTasks = mutableListOf<Thread>()

    @Throws
    fun start(threadCount: Int) {
        if (::threadPool.isInitialized && !threadPool.isTerminated) {
            throw AlreadyRunningException()
        }
        if (threadCount !in 1..AppEnvironment.engineMaxThreadCount) {
            throw ExceededMaxThreadCountException()
        }
        threadPool = Executors.newScheduledThreadPool(threadCount)
        threadPool.let { th ->
            repeat(threadCount) {_ ->
                val temp = Worker()
                callableTasks.add(temp)
                th.submit(
                    temp
                )
            }
        }
    }

    fun stop() {
        callableTasks.forEach {
            it.interrupt()
        }
        callableTasks.clear()
        threadPool.shutdownNow()
        threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)
    }
}
