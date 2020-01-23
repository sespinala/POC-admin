package co.com.psl.estesi

import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.HttpResponse
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


class MainVerticle : CoroutineVerticle() {

  private lateinit var webClient: WebClient

  init {
  }

  override suspend fun start() {
    println("*** start - Start ***")
    val port = 8888
    val host = "0.0.0.0"
    val router = initializeRouter()
    webClient = WebClient.create(vertx)
    vertx.createHttpServer().requestHandler(router).listen(port, host)
    println("*** start - End ***")
  }

  private fun initializeRouter(): Router {
    println("*** initializeRouter - Start ***")
    val router = Router.router(vertx)

    router.route().pathRegex("^(?!\\/anvorguesa3).*$").coroutineHandler { routingContext ->
      println("*** router.route - Start ***")
      val request = routingContext.request()
      val token = request.getHeader("Authorization")// Now end the response
      print("token $token")

      if (token == "orion") {
        routingContext.next()
      } else {
        unauthorized(routingContext)
      }

      // var ldapId = validateToken(token)
      // print(ldapId)
      println("*** router.route - End ***")
    }

    router.get("/anvorguesa1").coroutineHandler { routingContext ->
      println("*** /anvorguesa1 - Start/End ***")
      routingContext.response().end("hello world")
    }

    router.get("/anvorguesa2").coroutineHandler { routingContext ->
      println("*** /anvorguesa2 - Start/End ***")
      routingContext.response().end("ola mundo")
    }

    router.get("/anvorguesa3").coroutineHandler { routingContext ->
      println("*** /anvorguesa3 - Start/End ***")
      routingContext.response().end("ola ke ase")
    }

    router.get("/health").coroutineHandler { health(it) }

    println("*** initializeRouter - End ***")
    return router
  }

  private fun health(routingContext: RoutingContext) {
    println("*** /health - Start/End ***")
    routingContext.response().setStatusCode(200).end("Healthy")
  }

  private suspend fun validateToken(token: String): JsonObject? {
    println("*** validateToken - Start ***")
    val response = withTimeout(1000) {
      awaitResult<HttpResponse<Buffer>> {
        webClient.getAbs("http://hxj8jecbmjbj5h7mh-mock.stoplight-proxy.io/validateToken/$token")
          .putHeader("accept", "application/json")
          .send(it)
      }
    }
    val body = response.bodyAsJsonObject()
    println("*** validateToken - End ***")
    return body
  }

  private fun unauthorized(routingContext: RoutingContext) {
    routingContext.response().setStatusCode(401).end("Unauthorized. You have no power here .l.")
  }
}


private val TEXT_HTML = "text/html"
private val UTF_8 = "utf-8"

fun Route.coroutineHandler(function: suspend (RoutingContext)->Unit): Route = handler{ routingContext->
  println("*** coroutineHandler - Start ***")
  GlobalScope.launch(routingContext.vertx().dispatcher()) {
    try{
      routingContext.response().putHeader(
        HttpHeaders.CONTENT_TYPE,
        // Content-Type = "application/json; text/html; charset=utf-8"
        "${HttpHeaderValues.APPLICATION_JSON}; ${TEXT_HTML}; ${HttpHeaderValues.CHARSET}=${UTF_8}")
      function(routingContext)
    }catch (e: Exception){
      routingContext.fail(e)
    }
  }
  println("*** coroutineHandler - End ***")
}
