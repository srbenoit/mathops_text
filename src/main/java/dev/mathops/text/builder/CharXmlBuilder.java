/*
 * Copyright (C) 2022 Steve Benoit
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the  License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If  not, see
 * <https://www.gnu.org/licenses/>.
 */

package dev.mathops.text.builder;

/**
 * Supplements the methods of {@code CharLineBuilder} with convenience methods for creating XML (or HTML, etc.) files.
 */
class CharXmlBuilder extends CharLineBuilder {

    /** A character that should be escaped in XML. */
    static final char QUOTE_CHAR = '"';

    /** A character that should be escaped in XML. */
    static final char APOS_CHAR = '\'';

    /** A character that should be escaped in XML. */
    static final char LT_CHAR = '<';

    /** A character that should be escaped in XML. */
    static final char GT_CHAR = '>';

    /** A character that should be escaped in XML. */
    static final char AMP_CHAR = '&';

    /** The quote escape. */
    public static final String QUOT = "&quot;";

    /** The apostrophe escape. */
    public static final String APOS = "&apos;";

    /** The less-than escape. */
    public static final String LEFT = "&lt;";

    /** The greater-than escape. */
    public static final String RIGHT = "&gt;";

    /** The ampersand escape. */
    static final String AMP = "&amp;";

    /** A commonly used string. */
    static final String TICK_CLOSE = "'/>";

    /** A commonly used string. */
    static final String EQUAL_TICK = "='";

    /** A commonly used string. */
    static final String LT = "<";

    /** A commonly used string. */
    static final String LT_SLASH = "</";

    /** A commonly used string. */
    static final String GT = ">";

    /** A commonly used string. */
    static final String SLASH_GT = "/>";

    /**
     * Constructs a new {@code CharXmlBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    CharXmlBuilder(final int capacity) {

        super(capacity);
    }

    /**
     * Escapes a {@code String} so that is suitable for use in an XML attribute, then appends the escaped {@code String}
     * to this sequence.
     *
     * @param value the {@code String} to escape and append
     */
    public final void addEscaped(final String value) {

        final char[] chars = value.toCharArray();

        for (final char chr : chars) {
            if ((int) chr == (int) QUOTE_CHAR) {
                addString(QUOT);
            } else if ((int) chr == (int) APOS_CHAR) {
                addString(APOS);
            } else if ((int) chr == (int) LT_CHAR) {
                addString(LEFT);
            } else if ((int) chr == (int) GT_CHAR) {
                addString(RIGHT);
            } else if ((int) chr == (int) AMP_CHAR) {
                addString(AMP);
            } else {
                addChar(chr);
            }
        }
    }

    /**
     * Appends an XML attribute in the form {@code name='value'}, where {@code value} is escaped as needed to comply
     * with XML, and with a leading space before the name.
     *
     * @param name  the attribute name
     * @param value the attribute value
     * @param drop  if nonzero, drops to a new line and indents attribute this number of spaces
     */
    public final void addAttribute(final String name, final Object value, final int drop) {

        if (value != null) {
            if (drop == 0) {
                addChar(SPC);
            } else {
                addln();
                indent(drop);
            }

            addString(name);
            addString(EQUAL_TICK);
            final String valueString = value.toString();
            addEscaped(valueString);
            addChar(APOS_CHAR);
        }
    }

    /**
     * Appends the opening of an element tag, writing "&lt;tag".
     *
     * @param indent the number of spaces to indent the tag
     * @param tag    the tag being opened
     */
    public final void openElement(final int indent, final String tag) {

        indent(indent);
        addString(LT);
        addString(tag);
    }

    /**
     * Appends a start tag (without attributes) for an empty element tag, writing "&lt;tag&gt;".
     *
     * @param indent  the number of spaces to indent the tag
     * @param tag     the tag being opened
     * @param newline {@code true} to add a newline after the tag
     */
    public final void startNonempty(final int indent, final String tag, final boolean newline) {

        indent(indent);
        addString(LT);
        addString(tag);
        addString(GT);
        if (newline) {
            addln();
        }
    }

    /**
     * Appends an end tag for the opening of a nonempty element tag, writing "&lt;/tag&gt;".
     *
     * @param indent  the number of spaces to indent the tag
     * @param tag     the tag being opened
     * @param newline {@code true} to add a newline after the tag
     */
    public final void endNonempty(final int indent, final String tag, final boolean newline) {

        indent(indent);
        addString(LT_SLASH);
        addString(tag);
        addString(GT);
        if (newline) {
            addln();
        }
    }

    /**
     * Appends the ending of the start tag of nonempty element tag, writing "&gt;[CRLF]".
     *
     * @param newline {@code true} to add a newline after the closure
     */
    public final void endOpenElement(final boolean newline) {

        addString(GT);
        if (newline) {
            addln();
        }
    }

    /**
     * Appends the closure of an empty element tag, writing "/>[CRLF]".
     *
     * @param newline {@code true} to add a newline after the tag
     */
    public final void closeEmptyElement(final boolean newline) {

        addString(SLASH_GT);
        if (newline) {
            addln();
        }
    }

    /**
     * Appends some number of spaces.
     *
     * @param count the number of spaces to append
     */
    public final void indent(final int count) {

        for (int i = 0; i < count; ++i) {
            addChar(SPC);
        }
    }
}
