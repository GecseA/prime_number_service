package hu.gecsevar.primeNumberService.rpc

import hu.gecsevar.primeNumberService.model.PrimeModel
import hu.gecsevar.primeNumberService.properties.AppEnvironment
import kotlin.math.sqrt

class Worker : Runnable {

    private var running  = true

    override fun run() {
        while (running) {
            val rangeBegin = PrimeModel.getNextRange()
            if (rangeBegin == -1) {// don't eat more than we could ;)
                break
            }
            val primes = mutableSetOf<Int>()
            for (i in rangeBegin..<rangeBegin + AppEnvironment.workerCalculationRange) {
                if (isPrime(i)) {
                    primes.add(i)
                }
            }

            PrimeModel.addPrimes(primes, rangeBegin)
        }
    }

    fun stop() {
        running = false
    }

    private fun isPrime(n: Int): Boolean {

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
}