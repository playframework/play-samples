package routers;

import akka.actor.ActorSystem;
import example.myapp.helloworld.grpc.HelloReply;
import example.myapp.helloworld.grpc.HelloRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HelloWorldRouter extends example.myapp.helloworld.grpc.AbstractGreeterServiceRouter {

  @Inject
  public HelloWorldRouter(ActorSystem system) {
    super(system);
  }

  @Override
  public CompletionStage<HelloReply> sayHello(HelloRequest in) {
    String greeting = String.format("Hello %s!", in.getName());
    return CompletableFuture.completedFuture(HelloReply.newBuilder().setMessage(greeting).build());
  }

}
