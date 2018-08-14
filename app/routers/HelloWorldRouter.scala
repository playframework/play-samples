package routers

import akka.stream.Materializer
import example.myapp.helloworld.grpc.{HelloReply, HelloRequest}
import javax.inject.Inject

import scala.concurrent.Future

class HelloWorldRouter @Inject() (mat: Materializer) extends example.myapp.helloworld.grpc.AbstractGreeterServiceRouter(mat) {
  private implicit val ec = mat.executionContext

  override def sayHello(in: HelloRequest): Future[HelloReply] =
    Future {
      HelloReply(s"Hello, ${in.name}!")
    }
}
