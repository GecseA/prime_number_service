package hu.gecsevar.primeNumberService.model

import hu.gecsevar.primeNumberService.properties.AppEnvironment
import java.util.*

class RangeNotProcessedException : Exception() {}

object PrimeModel {

    private val numbers = Collections.synchronizedSet<Int>(mutableSetOf())
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
        var result = 0
        synchronized(ranges) {
            result = ranges.size * AppEnvironment.workerCalculationRange
            ranges.add(false)
        }

        return result
    }
    @Throws
    fun getPrimes(from: Int, to: Int) : String {
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