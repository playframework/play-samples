package routers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import example.myapp.helloworld.grpc.HelloReply;
import example.myapp.helloworld.grpc.HelloRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class HelloWorldRouter extends example.myapp.helloworld.grpc.AbstractGreeterServiceRouter {

  @Inject
  public HelloWorldRouter(Materializer mat, ActorSystem system) {
    super(mat, system);
  }

  @Override
  public CompletionStage<HelloReply> sayHello(HelloRequest in) {
    String greeting = String.format("Hello %s!", in.getName());
    return CompletableFuture.completedFuture(HelloReply.newBuilder().setMessage(greeting).build());
  }

}
