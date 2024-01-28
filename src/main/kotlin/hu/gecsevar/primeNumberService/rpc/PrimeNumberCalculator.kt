package hu.gecsevar.primeNumberService.rpc

import hu.gecsevar.primeNumberService.model.PrimeModel
import hu.gecsevar.primeNumberService.properties.AppEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit


class ExceededMaxThreadCountException : Exception() {}
class AlreadyRunningException : Exception() {}

/**
 * Thread pool
 */
object PrimeNumberCalculator2 {

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
                //callableTasks.add(temp)
                th.submit(
                    temp
                )
            }
        }
    }

    fun stop() {
        if (::threadPool.isInitialized) {
            callableTasks.forEach {
                it.interrupt()
            }
            callableTasks.clear()
            threadPool.shutdownNow()
            threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)
        }
    }
}


@Component
object ThreadExecutor {

    @Autowired
    private lateinit var executor : ThreadPoolTaskExecutor

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
        executor.isDaemon = false
        executor.initialize()
        repeat(threadCount) {
            executor.execute {
                try {
                    while (!Thread.currentThread().isInterrupted) {
                        val primes = mutableSetOf<Int>()

                        val rangeBegin = PrimeModel.getNextRange()
                        for (i in rangeBegin..<rangeBegin + AppEnvironment.workerCalculationRange) {
                            if (isPrime(i)) {
                                primes.add(i)
                            }
                        }

                        PrimeModel.addPrimes(primes, rangeBegin)
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        executor.start()
    }

    fun stop() {
        if (::executor.isInitialized && executor.isRunning) {
            executor.initiateShutdown()
            executor.shutdown()
            executor.stop()
            executor.destroy()
        }
    }
}