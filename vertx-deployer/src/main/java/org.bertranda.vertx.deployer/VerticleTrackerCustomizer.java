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

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by antoine on 13/07/16.
 */
public class VerticleTrackerCustomizer implements ServiceTrackerCustomizer<Verticle, Verticle> {
    private static final Logger LOG = LoggerFactory.getLogger(VerticleTrackerCustomizer.class);

    private Vertx vertx;
    private BundleContext context;

    public VerticleTrackerCustomizer(BundleContext context, Vertx vertx) {
        this.vertx = vertx;
        this.context = context;
    }

    @Override
    public Verticle addingService(ServiceReference<Verticle> reference) {
        LOG.info("Adding a new Verticle: {}", reference);
        Verticle v = this.context.getService(reference);

        this.vertx.deployVerticle(v, r -> {
            if (r.succeeded()) {
                LOG.info("Verticle deployed with id: {}", r.result());
            } else {
                LOG.error("Unable to deploy Verticle: {}", r.cause().getMessage(), r.cause());
            }
        });

        return v;
    }

    @Override
    public void modifiedService(ServiceReference<Verticle> reference, Verticle service) {
        LOG.info("Modified service");
    }

    @Override
    public void removedService(ServiceReference<Verticle> reference, Verticle service) {
        LOG.info("Removing a Verticle: {}", reference);

        if (service instanceof AbstractVerticle) {
            String id = ((AbstractVerticle) service).deploymentID();

            this.vertx.undeploy(id, r -> {
                if (r.succeeded()) {
                    LOG.info("Verticle {} undeployed", id);
                } else {
                    LOG.error("Unable to undeploy Verticle {} : {}", id, r.cause().getMessage(), r.cause());
                }
            });
        }


    }
}
