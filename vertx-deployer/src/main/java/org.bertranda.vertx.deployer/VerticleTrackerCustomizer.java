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
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.bertranda.vertx.deployer.utils.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
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
    private JsonObject config;

    public VerticleTrackerCustomizer(BundleContext context, Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.context = context;
        this.config = config;
    }

    @Override
    public Verticle addingService(ServiceReference<Verticle> reference) {
        String verticleName = (String) reference.getProperty(ComponentConstants.COMPONENT_NAME);

        LOG.info("Deploying Verticle {}", verticleName);

        Verticle v = this.context.getService(reference);

        DeploymentOptions opts = new DeploymentOptions();

        if (this.config != null && this.config.containsKey(verticleName)) {
            opts.setConfig(config.getJsonObject(verticleName));
        } else {
            LOG.warn("No configuration found for verticle {}", verticleName);
            opts.setConfig(new JsonObject());
        }

        this.vertx.deployVerticle(v, opts, r -> {
            if (r.succeeded()) {
                LOG.info("Verticle {} deployed with id: {}", verticleName, r.result());
            } else {
                LOG.error("Unable to deploy Verticle {}: {}", verticleName, r.cause().getMessage(), r.cause());
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
        final String verticleName = (String) reference.getProperty(ComponentConstants.COMPONENT_NAME);
        if (service instanceof AbstractVerticle) {
            LOG.info("Undeploying Verticle {}", verticleName);
            String id = ((AbstractVerticle) service).deploymentID();

            this.vertx.undeploy(id, r -> {
                if (r.succeeded()) {
                    LOG.info("Verticle {}:{} undeployed", verticleName, id);
                } else {
                    LOG.error("Unable to undeploy Verticle {}:{} : {}", verticleName, id, r.cause().getMessage(), r.cause());
                }
            });
        } else {
            LOG.error("Verticle {} is not an AbstractVerticle: unable to undeploy", verticleName);
        }
    }
}
