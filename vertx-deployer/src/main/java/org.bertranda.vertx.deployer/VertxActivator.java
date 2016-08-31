/*
* Copyright 2016 Bertrand Antoine
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.bertranda.vertx.deployer;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bundle activator registering the Vert.x instance and the event bus as OSGi service.
 */
public class VertxActivator implements BundleActivator {

    private final static Logger LOG = LoggerFactory.getLogger(VertxActivator.class);

    private ServiceRegistration<Vertx> vertxRegistration;
    private ServiceRegistration<EventBus> ebRegistration;
    private ServiceTracker<Verticle, Verticle> tracker;

    @Override
    public void start(BundleContext context) throws Exception {
        LOG.info("Creating Vert.x instance");
        Vertx vertx = Vertx.vertx();
        vertxRegistration = context.registerService(Vertx.class, vertx, null);
        LOG.info("Vert.x service registered");
        ebRegistration = context.registerService(EventBus.class, vertx.eventBus(), null);
        LOG.info("Vert.x Event Bus service registered");

        this.tracker = new ServiceTracker<>(context, Verticle.class.getName(), new VerticleTrackerCustomizer(context, vertx));
        this.tracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
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
}
