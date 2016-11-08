package org.bertranda.vertx.deployer;

import org.bertranda.vertx.deployer.utils.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Watches for Vert.x configs in OSGi configuration admin and creates / destroys the trackers
 * for the Verticles
 */
public class VertxInstanceManager implements ManagedServiceFactory {

    private final static Logger LOG = LoggerFactory.getLogger(VertxInstanceManager.class);

    private BundleContext context;
    private Map<String, VertxInstance> instances;

    public VertxInstanceManager(BundleContext context) {
        this.context = context;
        this.instances = new HashMap<>();
    }

    @Override
    public String getName() {
        return "Vert.x";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        LOG.debug("updated {} with {}", pid, properties);
        deleted(pid);

        if (properties == null) {
            return;
        }

        try {
            VertxInstance instance = new VertxInstance(this.context, Properties.getValue(properties, "name"))
                .configuration(Properties.getValue(properties, "conf"));
            instance.start();
            this.instances.put(pid, instance);
        } catch (Exception e) {
            LOG.error("Unable to create Vert.x instance: {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleted(String pid) {
        LOG.info("deleted {}", pid);
        VertxInstance tracker = this.instances.get(pid);
        if (tracker != null) {
            tracker.close();
            this.instances.remove(pid);
        }
    }
}
