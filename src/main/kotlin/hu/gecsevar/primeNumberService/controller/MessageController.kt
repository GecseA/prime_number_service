package hu.gecsevar.primeNumberService.controller

import hu.gecsevar.primeNumberService.model.PrimeModel
import hu.gecsevar.primeNumberService.model.RangeNotProcessedException
import hu.gecsevar.primeNumberService.rpc.AlreadyRunningException
import hu.gecsevar.primeNumberService.rpc.ExceededMaxThreadCountException
import hu.gecsevar.primeNumberService.rpc.PrimeNumberCalculator
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

@RestController
class MessageController {

    @GetMapping("/")
    fun index()= "Hello World!"

    @GetMapping("/start/{thread_count}")
    fun startService(@PathVariable("thread_count") threadCount: Int): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        try {
            PrimeNumberCalculator.start(threadCount)
            result.setResult(ResponseEntity.ok("Engine started successfully!"))
        } catch (ex: Exception) {
            when (ex) {
                is AlreadyRunningException -> result.setResult(ResponseEntity.status(METHOD_NOT_ALLOWED).body("Engine already running!"))
                is ExceededMaxThreadCountException -> result.setResult(
                    ResponseEntity.status(METHOD_NOT_ALLOWED).body("Exceeded engine max thread count!")
                )
                else -> result.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.message))
            }
        }
        return result
    }

    @GetMapping("/stop")
    fun stopService(): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        PrimeNumberCalculator.stop()
        result.setResult(ResponseEntity.ok("Engine stopped!"))
        return result
    }

    @GetMapping("/get-prime-numbers/from/{from}/to/{to}")
    fun getPrimeNumbers(@PathVariable("from") from: Int, @PathVariable("to") to: Int): DeferredResult<ResponseEntity<*>> {
        // TODO add paging
        val result = DeferredResult<ResponseEntity<*>>()
        try {
            result.setResult(ResponseEntity.ok(PrimeModel.getPrimes(from, to)))
        } catch (ex: Exception) {
            when (ex) {
                is RangeNotProcessedException -> result.setResult(ResponseEntity.status(METHOD_NOT_ALLOWED).body("Range not processed yet!"))
                else -> result.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.message))
            }
        }
        return result
    }
}

