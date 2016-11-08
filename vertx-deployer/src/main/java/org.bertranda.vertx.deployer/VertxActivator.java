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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * A bundle activator registering the Vert.x instance and the event bus as OSGi service.
 */
public class VertxActivator implements BundleActivator {

    private final static Logger LOG = LoggerFactory.getLogger(VertxActivator.class);

    private static final String FACTORY_PID = "io.vertx";

    @Override
    public void start(BundleContext context) throws Exception {
        Dictionary<String, String> props = new Hashtable<>();
        props.put(Constants.SERVICE_PID, FACTORY_PID);

        VertxInstanceManager manager = new VertxInstanceManager(context);
        context.registerService(ManagedServiceFactory.class, manager, props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
