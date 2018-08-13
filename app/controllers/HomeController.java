package controllers;

import example.myapp.helloworld.grpc.GreeterServiceClient;
import example.myapp.helloworld.grpc.HelloReply;
import example.myapp.helloworld.grpc.HelloRequest;
import play.mvc.*;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final GreeterServiceClient greeterServiceClient;

    @Inject
    public HomeController(GreeterServiceClient greeterServiceClient) {
        this.greeterServiceClient = greeterServiceClient;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index() {
        HelloRequest request = HelloRequest.newBuilder().setName("Caplin").build();
        CompletionStage<HelloReply> reply = greeterServiceClient.sayHello(request);

        return reply.thenApply(HelloReply::getMessage).thenApply(Results::ok);
    }

}
