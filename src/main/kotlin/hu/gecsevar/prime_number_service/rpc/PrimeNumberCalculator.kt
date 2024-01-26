package hu.gecsevar.prime_number_service.rpc

import hu.gecsevar.prime_number_service.model.PrimeModel
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.sqrt

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

    private fun workerCalculator() {

        while (true) {
            val primes = mutableSetOf<Int>()

            // TODO get range
            val range = PrimeModel.getNextRange().range
            for (i in range..range + 999) {
                if (isPrime(i)) {
                    primes.add(i)
                }
            }

            // TODO write numbers / range
            PrimeModel.addPrimes(primes, range)
        }
    }
}

class Worker : Thread() {

    private var running = true

    override fun run() {
        while (running) {
            val primes = mutableSetOf<Int>()

            // TODO get range
            val range = PrimeModel.getNextRange().range
            for (i in range..range + 999) {
                if (isPrime(i)) {
                    primes.add(i)
                }
            }

            // TODO write numbers / range
            PrimeModel.addPrimes(primes, range)
        }
    }

    override fun interrupt() {
        running = false
    }
}

fun isPrime(n: Int): Boolean {

    if (n <= 1)
        return false
    if (n == 2 || n == 3)
        return true
    if (n % 2 == 0 || n % 3 == 0)
        return false

    var i = 5
    while (i <= sqrt(n.toDouble())) {
        if (n % i == 0 || n % (i + 2) == 0)
            return false
        i += 6
    }

    return true
}