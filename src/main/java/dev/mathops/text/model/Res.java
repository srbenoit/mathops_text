/*
 * Copyright (C) 2022 Steve Benoit
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the  License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  General Public License for more
 * details.
 *
 *  You should have received a copy of the GNU General Public License along with this program. If  not, see
 * <https://www.gnu.org/licenses/>.
 */

package dev.mathops.text.model;

import dev.mathops.commons.res.ResBundle;

import java.util.Locale;

/**
 * Localized resources.
 */
final class Res extends ResBundle {

    /** A resource key. */
    static final String NULL_NODE;

    /** A resource key. */
    static final String NULL_BUILDER;

    /** A resource key. */
    static final String UNSUPPORTED_NODE_TYPE;

    // Used by BranchTreeNode

    static {
        int index = 1;
        NULL_NODE = key(index);
        ++index;
        NULL_BUILDER = key(index);
        ++index;
        UNSUPPORTED_NODE_TYPE = key(index);
        ++index;
    }

    //

    /** The resources - an array of key-values pairs. */
    private static final String[][] EN_US = { //
            {NULL_NODE, "Node to write may not be null"},
            {NULL_BUILDER, "Builder may not be null"},
            {UNSUPPORTED_NODE_TYPE, "Unsupported tree node type"},

            //
    };

    /** The singleton instance. */
    private static final Res instance = new Res();

    /**
     * Private constructor to prevent direct instantiation.
     */
    private Res() {

        super(Locale.US, EN_US);
    }

    /**
     * Gets the message with a specified key using the current locale.
     *
     * @param key the message key
     * @return the best-matching message, an empty string if none is registered (never {@code null})
     */
    static String get(final String key) {

        return instance.getMsg(key);
    }

//    /**
//     * Retrieves the message with a specified key, then uses a {@code MessageFormat} to format that message pattern with
//     * a collection of arguments.
//     *
//     * @param key       the message key
//     * @param arguments the arguments, as for {@code MessageFormat}
//     * @return the formatted string (never {@code null})
//     */
//    static String fmt(final String key, final Object... arguments) {
//
//        return instance.formatMsg(key, arguments);
//    }
}
