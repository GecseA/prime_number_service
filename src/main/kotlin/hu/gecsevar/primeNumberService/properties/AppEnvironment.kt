package hu.gecsevar.primeNumberService.properties

object AppEnvironment {

    val maxNumbersPerPage = System.getenv("max_numbers_per_page").toInt()
    val engineMaxThreadCount = if (System.getenv("engine_max_thread_count").toInt() !in 1..5) 1 else System.getenv("engine_max_thread_count").toInt()
    val workerCalculationRange = 1_000
}