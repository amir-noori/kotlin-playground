package playground.ktor

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HelloTest {

    @Test
    fun testStatus() = testApplication {

        application {
            helloModule()
        }

        val response = client.get("/status")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"status":"OK"}""", response.bodyAsText())
    }


}
