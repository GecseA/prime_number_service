package hu.gecsevar.prime_number_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrimeNumberServiceApplication

fun main(args: Array<String>) {
    runApplication<PrimeNumberServiceApplication>(*args)
}
