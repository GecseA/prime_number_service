package hu.gecsevar.primeNumberService.controller

import hu.gecsevar.primeNumberService.model.PrimeModel
import hu.gecsevar.primeNumberService.properties.AppEnvironment
import hu.gecsevar.primeNumberService.rpc.AlreadyRunningException
import hu.gecsevar.primeNumberService.rpc.ExceededMaxThreadCountException
import hu.gecsevar.primeNumberService.rpc.PrimeNumberCalculator
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

@RestController
class MessageController {

    @GetMapping("/run/{thread_count}")
    fun runService(@PathVariable("thread_count") threadCount: Int): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        try {
            PrimeNumberCalculator.start(threadCount)
            result.setResult(ResponseEntity.ok("Engine started successfully"))
        } catch (ex: Exception) {
            when (ex) {
                is AlreadyRunningException -> result.setResult(ResponseEntity.status(METHOD_NOT_ALLOWED).body("Engine already running!"))
                is ExceededMaxThreadCountException -> result.setResult(
                    ResponseEntity.status(METHOD_NOT_ALLOWED).body("Exceeded preferred engine thread count!")
                )

                else
                -> result.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.message))
            }
        }
        return result
    }

    @GetMapping("/stop")
    fun stopService() {
        // TODO handle error
        PrimeNumberCalculator.stop()
    }

    @GetMapping("/get-prime-numbers/from/{from}/to/{to}")
    fun getPrimeNumbers(@PathVariable("from") from: Int, @PathVariable("to") to: Int): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        if (to - from > AppEnvironment.restGetNumbersRange) {
            result.setResult(ResponseEntity.status(METHOD_NOT_ALLOWED).body("Exceeded range limit!"))
        } else {
            result.setResult(ResponseEntity.ok(PrimeModel.getPrimes(from, to)))
        }
        return result
    }
}

