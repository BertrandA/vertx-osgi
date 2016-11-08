package org.bertranda.vertx.deployer.utils;

import io.vertx.core.json.JsonObject;

import java.util.Dictionary;

/**
 * Created by antoine on 7/11/16.
 */
@SuppressWarnings("unchecked")
public final class Properties {

    public static <T> T getValue(Dictionary props, String key) {
        return (T) props.get(key);
    }

    public static <T> T getValue(Dictionary props, String key, T defaultValue) {
        Object val = props.get(key);
        return (val != null)? (T) props.get(key) : defaultValue;
    }
}
