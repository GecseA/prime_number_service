package hu.gecsevar.primeNumberService.properties

import kotlin.Int.Companion.MAX_VALUE

object AppEnvironment {

    /**
     * set the maximum numbers of listing prime numbers
     */
    val maxNumbersPerPage = System.getenv("max_numbers_per_page").toInt()
    val engineMaxThreadCount = if (System.getenv("engine_max_thread_count").toInt() !in 1..5) 1 else System.getenv("engine_max_thread_count").toInt()
    /**
     * in single/multi thread case the preferred range for each thread. Should be power of 10!
     */
    val workerCalculationRange = 1_000
    /**
     * Limited range for Prime number searching
     */
    val maxPrimeNumberRange = MAX_VALUE
}