package hu.gecsevar.prime_number_service.model

import java.util.*

data class Range (
    val range : Int,
    var finished : Boolean
)

object PrimeModel {

    // TODO container
    val numbers = Collections.synchronizedSet<Int>(mutableSetOf())
    val ranges = Collections.synchronizedList<Range>(mutableListOf())
    var rangeMax = 0

    fun addPrimes(newSet: Set<Int>, range: Int) {
        synchronized(numbers) {
            numbers.addAll(newSet)
        }
        synchronized(ranges) {
            ranges.find {
                it.range == range
            }?.finished = true
        }
    }
    fun getNextRange() : Range {
        val res : Range
        synchronized(ranges) {
            res = Range(rangeMax, false)
            ranges.add(res)
            ++rangeMax
        }

        return res
    }
}