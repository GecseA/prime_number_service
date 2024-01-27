package hu.gecsevar.prime_number_service.controller

import hu.gecsevar.prime_number_service.model.PrimeModel
import hu.gecsevar.prime_number_service.model.Range
import hu.gecsevar.prime_number_service.rpc.PrimeNumberCalculator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

    @GetMapping("/run")
    fun runService() {
        // TODO start handle error
        PrimeNumberCalculator.start()
    }

    @GetMapping("/stop")
    fun stopService() {
        // TODO handle error
        PrimeNumberCalculator.stop()
    }

    @GetMapping("/get-prime-numbers/from/{from}/to/{to}")
    fun getPrimeNumbers(@PathVariable("from") from: Int, @PathVariable("to") to: Int): String {
        // TODO limit range
        return PrimeModel.getPrimes(from, to)
    }
}

