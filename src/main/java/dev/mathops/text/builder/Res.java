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

package dev.mathops.text.builder;

import dev.mathops.commons.res.ResBundle;

import java.util.Locale;

/**
 * Localized resources.
 */
final class Res extends ResBundle {

    // Used by CharBuilder and USVBuilder

    /** A resource key. */
    static final String B_ADD_NEGATIVE_CODE_POINT;

    /** A resource key. */
    static final String B_ADD_SURROGATE_CODE_POINT;

    /** A resource key. */
    static final String B_ADD_SURROGATE_CODE_POINT_2;

    /** A resource key. */
    static final String B_ADD_NON_CHARACTER;

    /** A resource key. */
    static final String B_ADD_OUT_OF_RANGE;

    /** A resource key. */
    static final String B_HIGH_SURROGATE_W_NO_LOW;

    /** A resource key. */
    static final String B_HIGH_SURROGATE_AT_END;

    static {
        int index = 1;
        B_ADD_NEGATIVE_CODE_POINT = key(index);
        ++index;
        B_ADD_SURROGATE_CODE_POINT = key(index);
        ++index;
        B_ADD_SURROGATE_CODE_POINT_2 = key(index);
        ++index;
        B_ADD_NON_CHARACTER = key(index);
        ++index;
        B_ADD_OUT_OF_RANGE = key(index);
        ++index;
        B_HIGH_SURROGATE_W_NO_LOW = key(index);
        ++index;
        B_HIGH_SURROGATE_AT_END = key(index);
    }

    //

    /** The resources - an array of key-values pairs. */
    private static final String[][] EN_US = { //
            {B_ADD_NEGATIVE_CODE_POINT, "Attempt to add negative code point (ignored)"},
            {B_ADD_SURROGATE_CODE_POINT, "Attempt to add Unicode surrogate code point U+{0} char array (ignored)"},
            {B_ADD_SURROGATE_CODE_POINT_2, "Attempt to add Unicode+ surrogate code point U+{0} char array (ignored)"},
            {B_ADD_NON_CHARACTER, "Attempt to add non-character code point 0x{0} (ignored)"},
            {B_ADD_OUT_OF_RANGE, "Attempt to add code point 0x{0} that is outside valid Unicode+ range (ignored)"},
            {B_HIGH_SURROGATE_W_NO_LOW, "High surrogate 0x{0} with no low surrogate (ignored)"},
            {B_HIGH_SURROGATE_AT_END, "High surrogate 0x{0} at end of character array (ignored)"},

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

    /**
     * Retrieves the message with a specified key, then uses a {@code MessageFormat} to format that message pattern with
     * a collection of arguments.
     *
     * @param key       the message key
     * @param arguments the arguments, as for {@code MessageFormat}
     * @return the formatted string (never {@code null})
     */
    static String fmt(final String key, final Object... arguments) {

        return instance.formatMsg(key, arguments);
    }
}
