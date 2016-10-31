package dagger;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import play.api.OptionalSourceMapper;
import play.api.http.DefaultHttpFilters;
import play.api.http.HttpRequestHandler;
import play.api.http.JavaCompatibleHttpRequestHandler;
import play.api.i18n.Langs;
import play.api.i18n.MessagesApi;
import play.api.inject.SimpleInjector;
import play.api.mvc.EssentialFilter;
import play.api.routing.Router;
import play.core.j.DefaultJavaHandlerComponents;
import play.http.DefaultActionCreator;
import play.libs.Scala;
import scala.collection.Seq;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.List;

public class MyComponentsFromContext extends play.api.BuiltInComponentsFromContext {

    private final List<EssentialFilter> httpFilters;

    private final ActorSystem actorSystem;

    private final Materializer materializer;

    private final Provider<Router> routerProvider;

    private final MessagesApi messagesApi;

    private final Langs langs;

    @Inject
    public MyComponentsFromContext(play.ApplicationLoader.Context context,
                                   Provider<Router> routerProvider,
                                   ActorSystem actorSystem,
                                   Materializer materializer,
                                   List<EssentialFilter> httpFilters,
                                   MessagesApi messagesApi,
                                   Langs langs) {
        super(context.underlying());
        this.routerProvider = routerProvider;
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.httpFilters = httpFilters;
        this.messagesApi = messagesApi;
        this.langs = langs;
    }

    @Override
    public HttpRequestHandler httpRequestHandler() {
        return new JavaCompatibleHttpRequestHandler(
                router(),
                httpErrorHandler(),
                httpConfiguration(),
                new DefaultHttpFilters(httpFilters()),
                new DefaultJavaHandlerComponents(injector(), new DefaultActionCreator())
        );
    }

    @Override
    public play.api.routing.Router router() {
        return routerProvider.get();
    }

    @Override
    public play.api.inject.Injector injector() {
        // We need to add any Java actions and body parsers needed to the runtime injector
        return new SimpleInjector(super.injector(), Scala.asScala(new HashMap<Class<?>, Object>() {{
            put(play.mvc.BodyParser.Default.class, new play.mvc.BodyParser.Default(javaErrorHandler(), httpConfiguration()));
            put(play.api.i18n.MessagesApi.class, messagesApi);
            put(play.api.i18n.Langs.class, langs);
        }}));
    }

    play.http.HttpErrorHandler javaErrorHandler() {
        return new play.http.DefaultHttpErrorHandler(
                new play.Configuration(configuration().underlying()),
                new play.Environment(environment()),
                new OptionalSourceMapper(sourceMapper()),
                this::router
        );
    }

    @Override
    public Materializer materializer() {
        return materializer;
    }

    @Override
    public ActorSystem actorSystem() {
        return actorSystem;
    }

    @Override
    public Seq<EssentialFilter> httpFilters() {
        return Scala.asScala(httpFilters);
    }

}
