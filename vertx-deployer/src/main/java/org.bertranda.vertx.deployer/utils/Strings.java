package org.bertranda.vertx.deployer.utils;

/**
 * Created by antoine on 7/11/16.
 */
public final class Strings {

    public static String requireNonNullOrEmpty(String str) {
        if (str == null) {
            throw new NullPointerException();
        }

        if (str.isEmpty()) {
            throw new IllegalStateException("Empty");
        }

        return str;
    }

    public static String requireNonNullOrEmpty(String str, String message) {
        if (str == null) {
            throw new NullPointerException(message);
        }

        if (str.isEmpty()) {
            throw new IllegalStateException("Empty");
        }

        return str;
    }
}
