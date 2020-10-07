package com.varaha.vertx.main


import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import java.util.*
import java.util.concurrent.TimeUnit



fun main(args: Array<String>) {
    initPermissions()
    initMemoryUsageProcess()
    initAutomationFunction()
}

fun initServer(vertx: Vertx) {
    val server = vertx.createHttpServer(HttpServerOptions().setMaxHeaderSize(50000))
    server.requestHandler { it.isExpectMultipart = true }
    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())

    handleCors(router)
    val provider: JWTAuth = initJwt(vertx)


    router.route("/rest/*").handler(JWTAuthHandler.create(provider, "/rest/user/login"))
    mountRoutes(router, vertx, provider)
    val port = 8080
    server.requestHandler { router.accept(it) }.listen(port)



    router.get("/live").handler { rc ->
        val response = rc?.response()

        response?.putHeader("content-type", "application/json")
        response?.end("{\"status\": \"live\"}")
    }
    router.get("/").handler { rc ->
        val response = rc?.response()

        response?.putHeader("content-type", "application/json")
        response?.end("{\"status\": \"live\"}")
    }


    router.route().handler(StaticHandler.create())
}

fun initVertx() {
    initServer(Vertx.vertx(VertxOptions().setWorkerPoolSize(200).setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS).setMaxEventLoopExecuteTime(10)))
}

private fun initPermissions() {

}

fun initBatchProcess() {

}

fun initMemoryUsageProcess() {

}

fun initAutomationFunction() {

}




private fun handleCors(router: Router) {
    val allowedHeaders = HashSet<String>()
    allowedHeaders.add("Access-Control-Allow-Origin")
    allowedHeaders.add("Authorization")
    allowedHeaders.add("origin")
    allowedHeaders.add("Content-Type")
    allowedHeaders.add("timezoneOffset")
    allowedHeaders.add("BotzAccountID")
    allowedHeaders.add("BotzProjectID")

    val allowedMethods = HashSet<HttpMethod>()
    allowedMethods.add(HttpMethod.GET)
    allowedMethods.add(HttpMethod.POST)
    allowedMethods.add(HttpMethod.DELETE)
    allowedMethods.add(HttpMethod.PUT)
    allowedMethods.add(HttpMethod.OPTIONS)

    router.route().handler(CorsHandler.create("*")
            .allowedMethods(allowedMethods)
            .allowedHeaders(allowedHeaders))
}

private fun initJwt(vertx: Vertx): JWTAuth {
    val options = PubSecKeyOptions()
    options.algorithm = "HS256"
    options.publicKey = "abc"
    options.isSymmetric = true

    val config = JWTAuthOptions().setPubSecKeys(listOf(options))
    return JWTAuth.create(vertx, config)
}

private fun mountRoutes(router: Router, vertx: Vertx, provider: JWTAuth) {

    router.get("/rest/other").handler { routingContext ->
        // This handler will be called for every request
        println("Hello")
    }


}
