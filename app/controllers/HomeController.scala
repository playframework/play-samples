package controllers

import com.typesafe.config.Config
import example.myapp.helloworld.grpc.{ GreeterServiceClient, HelloReply, HelloRequest }
import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class HomeController @Inject() (greeterServiceClient: GreeterServiceClient, config:Config)(implicit ec: ExecutionContext) extends InjectedController {

  def index = Action.async {
    val request = HelloRequest("Caplin")
    // create a gRPC request
    val reply: Future[HelloReply] = greeterServiceClient.sayHello(request)
    // forward the gRPC response back as a plain String on an HTTP response
    reply.map(_.message).map(m => Ok(m))
  }

}
