package test

import example.myapp.helloworld.grpc.{ GreeterService, GreeterServiceClient, HelloRequest }
import org.scalatest.concurrent.{ IntegrationPatience, ScalaFutures }
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.routing.Router
import play.grpc.scalatest.ServerGrpcClient
import routers.HelloWorldRouter

class HelloScalaTestSpec extends PlaySpec with GuiceOneServerPerTest with ServerGrpcClient
    with ScalaFutures with IntegrationPatience {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder().overrides(bind[Router].to[HelloWorldRouter]).build()

  implicit def ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  "A Play server bound to a gRPC router" must {
    "give a 404 when routing a non-gRPC request" in {
      val result = wsUrl("/").get().futureValue
      result.status must be(404) // Maybe should be a 426, see #396
    }
    "give an Ok header (and hopefully a not implemented trailer) when routing a non-existent gRPC method" in {
      val result = wsUrl(s"/${GreeterService.name}/FooBar")
        .addHttpHeaders("Content-Type" -> "application/grpc")
        .get().futureValue
      result.status must be(200) // Maybe should be a 426, see #396
      // TODO: Test that trailer has a not implemented status
    }
    "give a 200 when routing an empty request to a gRPC method" in {
      val result = wsUrl(s"/${GreeterService.name}/SayHello")
        .addHttpHeaders("Content-Type" -> "application/grpc")
        .get().futureValue
      result.status must be(200) // Maybe should be a 426, see #396
    }
    "work with a gRPC client" in withGrpcClient[GreeterServiceClient] { (client: GreeterServiceClient) =>
      val reply = client.sayHello(HelloRequest("Alice")).futureValue
      reply.message must be("Hello, Alice!")
    }
  }
}
