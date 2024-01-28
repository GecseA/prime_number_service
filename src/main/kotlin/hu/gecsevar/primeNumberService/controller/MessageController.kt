package hu.gecsevar.primeNumberService.controller

import hu.gecsevar.primeNumberService.model.PrimeModel
import hu.gecsevar.primeNumberService.model.RangeNotProcessedException
import hu.gecsevar.primeNumberService.rpc.AlreadyRunningException
import hu.gecsevar.primeNumberService.rpc.ExceededMaxThreadCountException
import hu.gecsevar.primeNumberService.rpc.ThreadExecutor
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult

@Configuration
@OpenAPIDefinition(info = Info(title = "Prime number service", description = "Simple backend app which collection prime numbers, store and could response a ranged list", version = "1.0.0"))
class OpenAPIConfig

@RestController
class MessageController {

    @Operation(summary = "Hello word", description = "The service running and healthy")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
        ]
    )
    @GetMapping("/healthy")
    fun index()= "OK"

    @Operation(summary = "Start the background engine", description = "Start number of thread_count different workers and collecting prime numbers")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "400", description = "Engine already started OR request thread count is to high"),
        ]
    )
    @GetMapping("/start/{thread_count}")
    fun startService(@PathVariable("thread_count") threadCount: Int): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        try {
            ThreadExecutor.start(threadCount)
            result.setResult(ResponseEntity.ok("Engine started successfully!"))
        } catch (ex: Exception) {
            when (ex) {
                is AlreadyRunningException -> result.setResult(ResponseEntity.status(BAD_REQUEST).body("Engine already running!"))
                is ExceededMaxThreadCountException -> result.setResult(
                    ResponseEntity.status(BAD_REQUEST).body("Exceeded engine max thread count!")
                )
                else -> result.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.message))
            }
        }
        return result
    }

    @Operation(summary = "Stop the background engine", description = "Stop the search but keep the founded numbers")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
        ]
    )
    @GetMapping("/stop")
    fun stopService(): DeferredResult<ResponseEntity<*>> {
        val result = DeferredResult<ResponseEntity<*>>()
        ThreadExecutor.stop()
        result.setResult(ResponseEntity.ok("Engine stopped!"))
        return result
    }

    @Operation(summary = "Get the prime numbers", description = "Get the prime numbers. The result set is limited by environment variable")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "400", description = "The specified range is not currently processed"),
        ]
    )
    @GetMapping("/get-prime-numbers/from/{from}/to/{to}")
    fun getPrimeNumbers(@PathVariable("from") from: Int, @PathVariable("to") to: Int): DeferredResult<ResponseEntity<*>> {
        // TODO add paging
        val result = DeferredResult<ResponseEntity<*>>()
        try {
            result.setResult(ResponseEntity.ok(PrimeModel.getPrimes(from, to)))
        } catch (ex: Exception) {
            when (ex) {
                is RangeNotProcessedException -> result.setResult(ResponseEntity.status(BAD_REQUEST).body("Range not processed yet!"))
                else -> result.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.message))
            }
        }
        return result
    }
}

