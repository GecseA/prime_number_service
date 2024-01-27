package hu.gecsevar.primeNumberService.properties

object AppEnvironment {

    val restGetNumbersRange = System.getenv("rest_get_numbers_range").toInt()
    val engineMaxThreadCount = if (System.getenv("engine_max_thread_count").toInt() !in 1..5) 1 else System.getenv("engine_max_thread_count").toInt()
}