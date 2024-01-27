package hu.gecsevar.primeNumberService.model

import java.util.*

data class Range (
    val range : Int,
    var finished : Boolean
)

object PrimeModel {

    private val numbers = Collections.synchronizedSet<Int>(mutableSetOf())
    private val ranges = Collections.synchronizedList<Range>(mutableListOf())
    private var rangeMax = 0

    fun addPrimes(newSet: Set<Int>, range: Int) {
        synchronized(numbers) {
            synchronized(ranges) {
                ranges.find {
                    it.range == range
                }?.finished = true
            }
            numbers.addAll(newSet)
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
    fun getPrimes(from: Int, to: Int) : String {
        // TODO not already checked range selected!!!

        return synchronized(numbers) {
            numbers.filter { value ->
                value in from..to
            }.joinToString {
                it.toString()
            }
        }
    }
}