package controllers

import example.myapp.helloworld.grpc.GreeterServiceClient
import example.myapp.helloworld.grpc.HelloReply
import example.myapp.helloworld.grpc.HelloRequest
import javax.inject.Inject
import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.Future

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
class HomeController @Inject() (mat: Materializer, greeterServiceClient: GreeterServiceClient) extends InjectedController {
  implicit val ec = mat.executionContext

  /**
   * An action that renders an HTML page with a welcome message.
   * The configuration in the <code>routes</code> file means that
   * this method will be called when the application receives a
   * <code>GET</code> request with a path of <code>/</code>.
   */
  def index = Action.async {
    val request = HelloRequest("Caplin")
    val reply: Future[HelloReply] = greeterServiceClient.sayHello(request)

    reply.map(_.message).map(m ⇒ Ok(m))
  }
}
