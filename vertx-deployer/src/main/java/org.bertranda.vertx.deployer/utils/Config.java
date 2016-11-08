package org.bertranda.vertx.deployer.utils;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by antoine on 7/11/16.
 */
public final class Config {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    static String resolveEnv(String templ) {
        StringReader reader = new StringReader(templ);
        StringBuilder b = new StringBuilder();

        try {
            int c;
            while ((c = reader.read()) != -1) {

                // check for var start
                if (c == '$') {
                    reader.mark(1);
                    // var start
                    if (reader.read() == '{') {

                    }

                    while ((c = reader.read()) != -1) {

                    }
                }
            }
        } catch (IOException ex) {
            // noop
        }

        return b.toString();
    }
}
