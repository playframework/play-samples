package controllers;

import example.myapp.helloworld.grpc.GreeterServiceClient;
import example.myapp.helloworld.grpc.HelloReply;
import example.myapp.helloworld.grpc.HelloRequest;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class HomeController extends Controller {

    private final GreeterServiceClient greeterServiceClient;

    @Inject
    public HomeController(GreeterServiceClient greeterServiceClient) {
        this.greeterServiceClient = greeterServiceClient;
    }
    public CompletionStage<Result> index() {
        HelloRequest request = HelloRequest.newBuilder().setName("Caplin").build();
        CompletionStage<HelloReply> reply = greeterServiceClient.sayHello(request);

        return reply.thenApply(HelloReply::getMessage).thenApply(Results::ok);
    }

}
