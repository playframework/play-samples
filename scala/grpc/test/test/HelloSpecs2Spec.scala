package test

import example.myapp.helloworld.grpc.{ GreeterService, GreeterServiceClient, HelloRequest }
import io.grpc.Status
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{ WSClient, WSRequest }
import play.api.routing.Router
import play.api.test._
import play.grpc.specs2.ServerGrpcClient
import routers.HelloWorldRouter
import play.api.Configuration
import com.typesafe.config.ConfigFactory

class HelloSpecs2Spec extends ForServer with ServerGrpcClient with PlaySpecification with ApplicationFactories {

  protected def applicationFactory: ApplicationFactory =
    withGuiceApp(
      GuiceApplicationBuilder()
      .overrides(bind[Router].to[HelloWorldRouter])
      .configure(new Configuration(ConfigFactory.parseString("play.filters.hosts.allowed += 0.0.0.0").resolve()))
    )

  def wsUrl(path: String)(implicit running: RunningServer): WSRequest = {
    val ws = running.app.injector.instanceOf[WSClient]
    val url = running.endpoints.httpEndpoint.get.pathUrl(path)
    ws.url(url)
  }

  "A Play server bound to a gRPC router" should {
    "give a 404 when routing a non-gRPC request" >> { implicit rs: RunningServer =>
      val result = await(wsUrl("/").get())
      result.status must ===(404)
    }
    "give an Ok header when routing a non-existent gRPC method" >> { implicit rs: RunningServer =>
      val result = await(wsUrl(s"/${GreeterService.name}/FooBar")
        .addHttpHeaders("Content-Type" -> "application/grpc")
        .get())
      result.status must ===(200)
    }
    "give a 200 when routing an empty request to a gRPC method" >> { implicit rs: RunningServer =>
      val result = await(wsUrl(s"/${GreeterService.name}/SayHello")
        .addHttpHeaders("Content-Type" -> "application/grpc")
        .get())
      result.status must ===(200)
    }
    "work with a gRPC client" >> { implicit rs: RunningServer =>
      withGrpcClient[GreeterServiceClient] { (client: GreeterServiceClient) =>
        val reply = await(client.sayHello(HelloRequest("Alice")))
        reply.message must ===("Hello, Alice!")
      }
    }
  }

}
