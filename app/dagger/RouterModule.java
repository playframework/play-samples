package dagger;

import play.api.routing.Router;

/**
 * Binds directly to the generated router created from /conf/routes
 */
@Module
public abstract class RouterModule {

    @Binds
    public abstract Router providesRouter(router.Routes routes);

}
