package org.bertranda.vertx.deployer;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.bertranda.vertx.deployer.utils.Strings;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by antoine on 28/10/16.
 */
public class VertxInstance implements Closeable {
    private final static Logger LOG = LoggerFactory.getLogger(VertxInstance.class);

    public static final String VERTX_INSTANCE = "io.vertx.instance";

    private BundleContext context;
    private String name;

    private VertxOptions vertxOptions;
    private JsonObject config;

    private ServiceRegistration<Vertx> vertxRegistration;
    private ServiceRegistration<EventBus> ebRegistration;
    private ServiceTracker<Verticle, Verticle> tracker;

    public VertxInstance(BundleContext context, String name) {
        this.context = Objects.requireNonNull(context, "Missing bundle context");
        this.name = Strings.requireNonNullOrEmpty(name, "Missing instance name");
    }

    public VertxInstance configuration(String config) {
        LOG.info("Trying to load configuration {}", config);
        JsonObject conf = null;
        if (config != null) {
            try (Scanner scanner = new Scanner(new File(config)).useDelimiter("\\A")) {
                String sconf = scanner.next();
                try {
                    conf = new JsonObject(sconf);
                } catch (DecodeException e) {
                    LOG.error("Configuration file {} does not contain a valid JSON object", sconf);
                }
            } catch (FileNotFoundException e) {
                try {
                    conf = new JsonObject(config);
                } catch (DecodeException e2) {
                    // The configuration is not printed for security purpose, it can contain sensitive data.
                    LOG.error("The provided configuration not point to an existing file or is not a valid JSON object: {}", e2.getMessage() );
                }
            }
        } else {
            LOG.warn("No configuration found for Vert.x instance  {}", this.name);
        }

        this.config = conf;

        return this;
    }

    public VertxInstance vertxOptions(JsonObject options) {
        this.vertxOptions = new VertxOptions(options);
        return this;
    }

    public void start() throws Exception {
        LOG.info("Creating Vert.x instance {}", this.name);
        Vertx vertx = Vertx.vertx(vertxOptions());

        Dictionary<String, Object> props = new Hashtable<>();
        props.put(VERTX_INSTANCE, this.name);

        vertxRegistration = context.registerService(Vertx.class, vertx, props);
        LOG.debug("Vert.x service registered");
        ebRegistration = context.registerService(EventBus.class, vertx.eventBus(), props);
        LOG.debug("Vert.x Event Bus service registered");

        Filter verticleFilter = this.createVerticleFilter();
        this.tracker = new ServiceTracker<>(context, verticleFilter, new VerticleTrackerCustomizer(context, vertx, this.config));
        this.tracker.open();
        LOG.info("Vert.x instance {} listening to verticle: {}", this.name, verticleFilter.toString());
    }

    @Override
    public void close() {
        LOG.info("Stopping Vert.x instance {}", this.name);
        if (vertxRegistration != null) {
            vertxRegistration.unregister();
            vertxRegistration = null;
        }

        if (ebRegistration != null) {
            ebRegistration.unregister();
            ebRegistration = null;
        }

        if (this.tracker != null) {
            this.tracker.close();
        }
    }

    private VertxOptions vertxOptions() {
        if (vertxOptions == null) {
            this.vertxOptions = new VertxOptions();
        }

        return this.vertxOptions;
    }

    private Filter createVerticleFilter() throws InvalidSyntaxException {
        String filterString = "(&("+ Constants.OBJECTCLASS +"="+ Verticle.class.getName() +")"+
                                "("+ VERTX_INSTANCE +"="+ this.name +"))";
        return this.context.createFilter(filterString);
    }
}
