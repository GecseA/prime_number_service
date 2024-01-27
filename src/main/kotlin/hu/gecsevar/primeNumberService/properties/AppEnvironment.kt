package hu.gecsevar.primeNumberService.properties

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
}