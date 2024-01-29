package hu.gecsevar.primeNumberService.model

import hu.gecsevar.primeNumberService.properties.AppEnvironment
import java.util.*

class RangeNotProcessedException : Exception() {}

/**
 * Storage class for prime numbers and the workers search ranges
 */
object PrimeModel {

    /**
     * Thread safe collection for prime numbers
     */
    private val numbers = Collections.synchronizedSet<Int>(mutableSetOf())

    /**
     * Workers active/ready ranges
     */
    private val ranges = Collections.synchronizedList<Boolean>(mutableListOf())

    fun addPrimes(newSet: Set<Int>, range: Int) {
        synchronized(numbers) {
            synchronized(ranges) {
                ranges[range / AppEnvironment.workerCalculationRange] = true
            }
            numbers.addAll(newSet)
        }
    }
    fun getNextRange() : Int {
        var result : Int
        synchronized(ranges) {
            if (ranges.size == AppEnvironment.maxPrimeNumberRange) {
                return -1
            }
            result = ranges.size * AppEnvironment.workerCalculationRange
            ranges.add(false)
        }

        return result
    }
    @Throws
    fun getPrimes(from: Int, to: Int) : String {
        // TODO add page support
        synchronized(ranges) {
            if (ranges.isEmpty()) {
                throw RangeNotProcessedException()
            }
            val range = (from / AppEnvironment.workerCalculationRange).rangeTo(to / AppEnvironment.workerCalculationRange)

            for(i in range) {
                if (ranges.size > i) {
                    if (!ranges[i]) {
                        throw RangeNotProcessedException()
                    }
                } else {
                    throw RangeNotProcessedException()
                }
            }
        }
        return synchronized(numbers) {
            numbers.filter { value ->
                value in from..to
            }.take(AppEnvironment.maxNumbersPerPage).joinToString {
                it.toString()
            }
        }
    }
}