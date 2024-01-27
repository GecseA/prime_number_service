package hu.gecsevar.prime_number_service.controller

import hu.gecsevar.prime_number_service.model.PrimeModel
import hu.gecsevar.prime_number_service.rpc.PrimeNumberCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {
    @GetMapping("/{name}/{age}")
    fun index(@PathVariable("name") name: String, @PathVariable("age") age: Int) = "Hello, $name! Your age is $age?"

    @GetMapping("/from/{from}/to/{to}")
    fun index2(@PathVariable("from") from: Int, @PathVariable("to") to: Int) = "Range: $from to $to!"

    // TODO start backend process
    @GetMapping("/run")
    fun runService() {
        PrimeNumberCalculator.start()
    }

    // TODO stop backend process
    @GetMapping("/stop")
    fun stopService() {
        PrimeNumberCalculator.stop()
    }
    // TODO get range specified prime numbers
    @GetMapping("/get-prime-numbers")
    fun getPrimeNumbers(): String {

        return synchronized(PrimeModel.numbers) {
            PrimeModel.numbers.joinToString {
                it.toString()
            }
        }
    }
}

