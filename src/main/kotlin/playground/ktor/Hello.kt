package playground.ktor

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun main(args: Array<String>) {
    val server = embeddedServer(CIO, 4007, module = Application::helloModule)
    server.start(wait = true)
}

fun Routing.helloRouting() {
    route("/") {
        get() {
            call.respondText("Hello, world!", ContentType.Text.Html)
        }

        get("/greet") {
            val name = call.request.queryParameters["name"]
            call.respondText("Hello $name", ContentType.Text.Html)
        }

        get("/status") {
            call.respond(mapOf("status" to "OK"))
        }
    }
}


fun Application.helloModule() {

    install(ContentNegotiation) {
        json()
    }

    routing {
        helloRouting()
    }
}
