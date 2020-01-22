package co.com.psl.estesi

import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {
    println("*** start - Start ***")
    val port = 8888
    val host = "0.0.0.0"

    val router = initializeRouter()

    vertx.createHttpServer().requestHandler(router).listen(port, host)
    println("*** start - End ***")
  }

  private fun initializeRouter(): Router {
    println("*** initializeRouter - Start ***")
    val router = Router.router(vertx)



    router.get("/anvorguesa1").handler { routingContext ->
      println("*** /anvorguesa1 - Start/End ***")
      routingContext.response().end("hello world")
    }

    router.get("/anvorguesa2").handler { routingContext ->
      println("*** /anvorguesa2 - Start/End ***")
      routingContext.response().end("ola mundo")
    }

    router.get("/anvorguesa3").handler { routingContext ->
      println("*** /anvorguesa3 - Start/End ***")
      routingContext.response().end("ola ke ase")
    }

    router.get("/health").handler(this::health)

    println("*** initializeRouter - End ***")
    return router
  }

  private fun health(routingContext: RoutingContext) {
    println("*** /health - Start/End ***")
    routingContext.response().setStatusCode(200).end("Healthy")
  }
}
