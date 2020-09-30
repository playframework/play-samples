package example.myapp.helloworld;

import akka.grpc.GrpcClientSettings;
import play.api.test.DefaultTestServerFactory;
import play.api.test.RunningServer;
import play.api.test.TestServerFactory;
import play.grpc.testkit.JavaAkkaGrpcClientHelpers;

import play.test.WithApplication;
import routers.HelloWorldRouter;
import example.myapp.helloworld.grpc.*;

import org.junit.*;

import play.*;
import play.api.routing.*;

import play.inject.guice.*;
import play.libs.ws.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static play.inject.Bindings.*;
import com.typesafe.config.ConfigFactory;

public final class HelloFunctionalTest extends WithApplication {

  private final TestServerFactory testServerFactory = new DefaultTestServerFactory();
  private RunningServer runningServer;

  @Override
  public Application provideApplication() {
    return new GuiceApplicationBuilder()
        .overrides(bind(Router.class).to(HelloWorldRouter.class))
        .configure(ConfigFactory.parseString("play.filters.hosts.allowed += 0.0.0.0").resolve())
        .build();
  }

  @Before
  public void startServer() {
    runningServer = testServerFactory.start(app.asScala());
  }

  @After
  public void stopServer() throws Exception {
    if (runningServer != null) {
      runningServer.stopServer().close();
      runningServer = null;
    }
  }

  private WSResponse wsGet(final String path) throws Exception {
    final WSClient wsClient = app.injector().instanceOf(WSClient.class);
    final String url = runningServer.endpoints().httpEndpoint().get().pathUrl(path);
    return wsClient.url(url).setContentType("application/grpc").get()
            .toCompletableFuture().get(30, TimeUnit.SECONDS);
  }

  private GreeterServiceClient newGreeterServiceClient() {
    
    final GrpcClientSettings grpcClientSettings =
        JavaAkkaGrpcClientHelpers
          .grpcClientSettings(runningServer)
          .withOverrideAuthority("localhost");

    return GreeterServiceClient.create(
        grpcClientSettings, app.asScala().actorSystem());
  }

  @Test public void returns404OnNonGrpcRequest() throws Exception {
    assertEquals(404, wsGet("/").getStatus());
  }

  @Test public void returns200OnNonExistentGrpcMethod() throws Exception {
    final WSResponse rsp = wsGet("/" + GreeterService.name + "/FooBar");
    assertEquals(200, rsp.getStatus());
  }

  @Test public void returns200OnEmptyRequestToAGrpcMethod() throws Exception {
    final WSResponse rsp = wsGet("/" + GreeterService.name + "/SayHello");
    assertEquals(200, rsp.getStatus());
  }

  @Test public void worksWithAGrpcClient() throws Exception {
    final GreeterServiceClient greeterServiceClient = newGreeterServiceClient();
    final HelloRequest req = HelloRequest.newBuilder().setName("Alice").build();
    try {
      final HelloReply helloReply = greeterServiceClient.sayHello(req).toCompletableFuture().get(30, TimeUnit.SECONDS);
      assertEquals("Hello Alice!", helloReply.getMessage());
    } finally {
      greeterServiceClient.close().toCompletableFuture().get(30, TimeUnit.SECONDS);
    }
  }

}
