package hu.gecsevar.primeNumberService.rpc

import hu.gecsevar.primeNumberService.model.PrimeModel
import kotlin.math.sqrt

class Worker : Thread() {

    private var running = true

    override fun run() {
        while (running) {
            val primes = mutableSetOf<Int>()

            val range = PrimeModel.getNextRange().range * 1_000
            for (i in range..range + 999) {
                if (isPrime(i)) {
                    primes.add(i)
                }
            }

            PrimeModel.addPrimes(primes, range)
        }
    }

    override fun interrupt() {
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