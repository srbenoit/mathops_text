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
 * Supplements the methods of {@code CharXmlBuilder} with convenience methods for creating common HTML structures.
 */
public final class CharHtmlBuilder extends CharXmlBuilder {

    /** A common string. */
    static final String TICK = "'";

    /** A common string. */
    static final String P = "<p>";

    /** A common string. */
    static final String OPEN_P = "<p";

    /** A common string. */
    static final String CLOSE_P = "</p>";

    /** A common string. */
    static final String OPEN_SPAN = "<span";

    /** A common string. */
    static final String CLOSE_SPAN = "</span>";

    /** A common string. */
    static final String SP_CLASS_EQ_TICK = " class='";

    /** A common string. */
    static final String OPEN_H = "<h";

    /** A common string. */
    static final String CLOSE_H = "</h";

    /** A common string. */
    static final String BR = "<br/>";

    /** A common string. */
    static final String HR = "<hr/>";

    /** A common string. */
    static final String OPEN_HR = "<hr";

    /** A common string. */
    static final String DIV = "<div>";

    /** A common string. */
    static final String OPEN_DIV = "<div";

    /** A common string. */
    static final String CLOSE_DIV = "</div>";

    /** A common string. */
    static final String TR = "<tr>";

    /** A common string. */
    static final String OPEN_TR = "<tr";

    /** A common string. */
    static final String CLOSE_TR = "</tr>";

    /** A common string. */
    static final String TABLE = "<table>";

    /** A common string. */
    static final String OPEN_TABLE = "<table";

    /** A common string. */
    static final String CLOSE_TABLE = "</table>";

    /** A common string. */
    static final String TH = "<th>";

    /** A common string. */
    static final String OPEN_TH = "<th";

    /** A common string. */
    static final String CLOSE_TH = "</th>";

    /** A common string. */
    static final String TD = "<td>";

    /** A common string. */
    static final String OPEN_TD = "<td";

    /** A common string. */
    static final String CLOSE_TD = "</td>";

    /**
     * Constructs a new {@code CharHtmlBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    public CharHtmlBuilder(final int capacity) {

        super(capacity);
    }

    /**
     * Emits the start of a "p" tag.
     */
    public void sP() {

        addString(P);
    }

    /**
     * Emits the start of a "p" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sP(final String cls, final String... attributes) {

        addString(OPEN_P);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "p" tag.
     */
    public void eP() {

        addString(CLOSE_P);
    }

    /**
     * Emits the start of a "span" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sSpan(final String cls, final String... attributes) {

        addString(OPEN_SPAN);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "span" tag.
     */
    public void eSpan() {

        addString(CLOSE_SPAN);
    }

    /**
     * Emits the start of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     */
    public void sH(final int level) {

        addString(OPEN_H);
        addInt(level);
        addChar(GT_CHAR);
    }

    /**
     * Emits the start of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     * @param cls   a class
     */
    public void sH(final int level, final String cls) {

        addString(OPEN_H);
        addInt(level);
        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }
        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     */
    public void eH(final int level) {

        addString(CLOSE_H);
        addInt(level);
        addChar(GT_CHAR);
    }

    /**
     * Emits an empty "br" tag.
     */
    public void br() {

        addString(BR);
    }

    /**
     * Emits an empty "hr" tag.
     */
    public void hr() {

        addlnString(HR);
    }

    /**
     * Emits an empty "hr" tag.
     *
     * @param cls the class name
     */
    public void hr(final String cls) {

        addlnStrings(OPEN_HR, SP_CLASS_EQ_TICK, cls, TICK);
    }

    /**
     * Emits the start of a "div" tag.
     */
    public void sDiv() {

        addString(DIV);
    }

    /**
     * Emits the start of a "div" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sDiv(final String cls, final String... attributes) {

        addString(OPEN_DIV);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "div" tag.
     */
    public void eDiv() {

        addString(CLOSE_DIV);
    }

    /**
     * Emits a start and end of a "div" tag, with no contents.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void div(final String cls, final String... attributes) {

        addString(OPEN_DIV);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
        addString(CLOSE_DIV);
    }

    /**
     * Emits the start of a "tr" tag.
     */
    public void sTr() {

        addString(TR);
    }

    /**
     * Emits the start of a "tr" tag.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTr(final String cls, final String... attributes) {

        addString(OPEN_TR);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "tr" tag.
     */
    public void eTr() {

        addString(CLOSE_TR);
    }

    /**
     * Emits the start of a "table" tag.
     */
    public void sTable() {

        addString(TABLE);
    }

    /**
     * Emits the start of a "table" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTable(final String cls, final String... attributes) {

        addString(OPEN_TABLE);

        if (cls != null) {
            addStrings(OPEN_TABLE, SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "table" tag.
     */
    public void eTable() {

        addString(CLOSE_TABLE);
    }

    /**
     * Emits the start of a "th" tag.
     */
    public void sTh() {

        addString(TH);
    }

    /**
     * Emits the start of a "th" tag.
     *
     * @param cls the CSS class; {@code null} to omit
     */
    public void sTh(final String cls) {

        if (cls == null) {
            addString(TH);
        } else {
            addStrings(OPEN_TH, SP_CLASS_EQ_TICK, cls, TICK_CLOSE);
        }
    }

    /**
     * Emits the end of a "th" tag.
     */
    public void eTh() {

        addString(CLOSE_TH);
    }

    /**
     * Emits the start of a "td" tag.
     */
    public void sTd() {

        addString(TD);
    }

    /**
     * Emits the start of a "td" tag.
     *
     * @param cls the CSS class; {@code null} to omit
     */
    public void sTd(final String cls) {

        if (cls == null) {
            addString(TD);
        } else {
            addStrings(OPEN_TD, SP_CLASS_EQ_TICK, cls, TICK_CLOSE);
        }
    }

    /**
     * Emits the start of a "td" tag.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTd(final String cls, final String... attributes) {

        addString(OPEN_TD);

        if (cls != null) {
            addStrings(SP_CLASS_EQ_TICK, cls, TICK);
        }

        for (final String attr : attributes) {
            addChar(SPC);
            addString(attr);
        }

        addChar(GT_CHAR);
    }

    /**
     * Emits the end of a "td" tag.
     */
    public void eTd() {

        addString(CLOSE_TD);
    }
}
