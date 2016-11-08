package org.bertranda.vertx.examples.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    name = SimpleHttpVerticle.COMPONENT_NAME,
    immediate = true,
    configurationPid = SimpleHttpVerticle.COMPONENT_CPID,
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    service = Verticle.class,
    property = "io.vertx.instance=examples"
)
public class SimpleHttpVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpVerticle.class);

    public static final String COMPONENT_NAME = "vertx-osgi-exmaple-simpleHttp";
    public static final String COMPONENT_CPID = "org.bertranda.vertx.example.simpleHttp";

    @Activate
    public void activate(ComponentContext cc) {
        LOG.debug("Activating component {}: {}", COMPONENT_NAME, cc.getProperties());
    }

    @Deactivate
    public void deactivate(ComponentContext cc) {
        LOG.debug("Deactivating component with config {}: {}", COMPONENT_NAME, cc.getProperties());
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        LOG.info("Starting verticle {}", COMPONENT_NAME);

        LOG.info("config: {}", config().encodePrettily());
        startFuture.complete();
    }

    public void stop(Future<Void> stopFuture) throws Exception {
        LOG.info("Stopping verticle {}", COMPONENT_NAME);
        stopFuture.complete();
    }
}
