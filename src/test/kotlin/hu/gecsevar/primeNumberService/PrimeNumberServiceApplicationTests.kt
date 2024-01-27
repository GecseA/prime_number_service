package hu.gecsevar.primeNumberService

import hu.gecsevar.primeNumberService.model.RangeNotProcessedException
import hu.gecsevar.primeNumberService.rpc.AlreadyRunningException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CheckHTTPResponse {
    @LocalServerPort
    private val port = 0

    @Autowired
    private val restTemplate: TestRestTemplate? = null

    @Test
    @Throws(Exception::class)
    fun shouldPassIfStringMatches() {
        assertThat(restTemplate!!.getForObject("http://localhost:$port/healthy",String::class.java))
            .contains("OK")
    }

    @Test
    @Throws(Exception::class)
    fun startService() {
        assertThat(restTemplate!!.getForObject("http://localhost:$port/start/1",String::class.java))
            .contains("Engine started successfully!")
    }
    @Test
    @Throws(AlreadyRunningException::class)
    fun reStartService_AlreadyRunningException() {
        restTemplate!!.getForObject("http://localhost:$port/start/1",String::class.java)
        assertThat(restTemplate.getForObject("http://localhost:$port/start/1",String::class.java))
            .contains("Engine already running!")
    }
    @Test
    @Throws(Exception::class)
    fun stopService() {
        assertThat(restTemplate!!.getForObject("http://localhost:$port/stop",String::class.java))
            .contains("Engine stopped!")
    }
    @Test
    @Throws(RangeNotProcessedException::class)
    fun getPrimeNumbers_RangeNotProcessedException() {
        assertThat(restTemplate!!.getForObject("http://localhost:$port/get-prime-numbers/from/10000000/to/11000000",String::class.java))
            .contains("Range not processed yet!")
    }
}

