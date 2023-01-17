package playground.ktor

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import java.time.Duration


fun main(args: Array<String>) {
    val server = embeddedServer(CIO, 4007, module = Application::socketAppModule)
    server.start(wait = true)
}

data class MessageSession(val id: Int, val username: String)

/**

    to send message via websocket first get a session and then send message.
    ```
        var ws = new WebSocket("ws://localhost:4007/message");
        ws.onmessage = function (evt) {
            console.log("response: " + evt.data);
        };
        ws.onerror  = function(err) {
            console.log("error: ", err);
        };
        ws.onclose = function() {
            console.log("Connection is closed.");
        };

        ws.send("test message");
    ```

 */
fun Routing.socketAppRouting() {

    route("/") {
        get("/session") {
            val session = call.sessions.get<MessageSession>()
            val name = call.request.queryParameters.getOrFail("name")
            if (session == null) {
                call.sessions.set(MessageSession(100, name))
            }

            call.respondText("Session Created for user $name", ContentType.Text.Html)
        }

        webSocket("/message") {
            try {
                println("accepting connection")
                val session = call.sessions.get<MessageSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                    return@webSocket
                }
                for (frame in incoming) {
                    println("received frame")
                    if (frame is Frame.Text) {
                        processMessage(frame.readText())
                        send(Frame.Text("all right"))
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                e.printStackTrace()
            }
        }
    }

}

suspend fun processMessage(text: String) {
    coroutineScope {
        println("received: $text")
    }
}

fun Application.socketAppModule() {

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<MessageSession>("message_session")
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        socketAppRouting()
    }
}
