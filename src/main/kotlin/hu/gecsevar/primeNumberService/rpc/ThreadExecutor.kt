package hu.gecsevar.primeNumberService.rpc

import hu.gecsevar.primeNumberService.properties.AppEnvironment
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component


class ExceededMaxThreadCountException : Exception() {}
class AlreadyRunningException : Exception() {}

/**
 * Thread pool
 */
@Component
object ThreadExecutor {

    private lateinit var executor : ThreadPoolTaskExecutor
    private var callableTasks = mutableListOf<Runnable>()

    @Throws
    fun start(threadCount: Int) {
        if (::executor.isInitialized && executor.isRunning) {
            throw AlreadyRunningException()
        }
        if (threadCount !in 1..AppEnvironment.engineMaxThreadCount) {
            throw ExceededMaxThreadCountException()
        }
        executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = threadCount
        executor.maxPoolSize = threadCount
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(1)
        executor.initialize()
        executor.start()
        repeat(threadCount) {
            executor.execute {
                callableTasks.add(Worker())
                callableTasks.last().run()
            }
        }
    }

    fun stop() {
        if (::executor.isInitialized && executor.isRunning) {
            callableTasks.forEach {
                (it as Worker).stop()
            }
            executor.shutdown()
            callableTasks.clear()
        }
    }
}