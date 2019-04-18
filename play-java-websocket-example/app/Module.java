import actors.*;
import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;

@SuppressWarnings("unused")
public class Module extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(StocksActor.class, "stocksActor");
        bindActor(UserParentActor.class, "userParentActor");
        bindActorFactory(UserActor.class, UserActor.Factory.class);
    }
}
