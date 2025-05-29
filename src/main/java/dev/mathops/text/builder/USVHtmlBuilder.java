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
 * Supplements the methods of {@code USVXmlBuilder} with convenience methods for creating common HTML structures.
 */
public final class USVHtmlBuilder extends USVXmlBuilder {

    /**
     * Constructs a new {@code USVHtmlBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    public USVHtmlBuilder(final int capacity) {

        super(capacity);
    }

    /**
     * Emits the start of a "p" tag.
     */
    public void sP() {

        addString(CharHtmlBuilder.P);
    }

    /**
     * Emits the start of a "p" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sP(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_P);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "p" tag.
     */
    public void eP() {

        addString(CharHtmlBuilder.CLOSE_P);
    }

    /**
     * Emits the start of a "span" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sSpan(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_SPAN);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "span" tag.
     */
    public void eSpan() {

        addString(CharHtmlBuilder.CLOSE_SPAN);
    }

    /**
     * Emits the start of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     */
    public void sH(final int level) {

        addString(CharHtmlBuilder.OPEN_H);
        addInt(level);
        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the start of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     * @param cls   a class
     */
    public void sH(final int level, final String cls) {

        addString(CharHtmlBuilder.OPEN_H);
        addInt(level);
        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }
        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "h?" tag, where ? is an integer from 1 to 6.
     *
     * @param level the heading level (1 to 6)
     */
    public void eH(final int level) {

        addString(CharHtmlBuilder.CLOSE_H);
        addInt(level);
        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits an empty "br" tag.
     */
    public void br() {

        addString(CharHtmlBuilder.BR);
    }

    /**
     * Emits an empty "hr" tag.
     */
    public void hr() {

        addlnString(CharHtmlBuilder.HR);
    }

    /**
     * Emits an empty "hr" tag.
     *
     * @param cls the class name
     */
    public void hr(final String cls) {

        addlnStrings(CharHtmlBuilder.OPEN_HR, CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
    }

    /**
     * Emits the start of a "div" tag.
     */
    public void sDiv() {

        addString(CharHtmlBuilder.DIV);
    }

    /**
     * Emits the start of a "div" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sDiv(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_DIV);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "div" tag.
     */
    public void eDiv() {

        addString(CharHtmlBuilder.CLOSE_DIV);
    }

    /**
     * Emits a start and end of a "div" tag, with no contents.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void div(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_DIV);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
        addString(CharHtmlBuilder.CLOSE_DIV);
    }

    /**
     * Emits the start of a "tr" tag.
     */
    public void sTr() {

        addString(CharHtmlBuilder.TR);
    }

    /**
     * Emits the start of a "tr" tag.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTr(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_TR);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "tr" tag.
     */
    public void eTr() {

        addString(CharHtmlBuilder.CLOSE_TR);
    }

    /**
     * Emits the start of a "table" tag.
     */
    public void sTable() {

        addString(CharHtmlBuilder.TABLE);
    }

    /**
     * Emits the start of a "table" tag, with an optional CSS class.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTable(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_TABLE);

        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "table" tag.
     */
    public void eTable() {

        addString(CharHtmlBuilder.CLOSE_TABLE);
    }

    /**
     * Emits the start of a "th" tag.
     */
    public void sTh() {

        addString(CharHtmlBuilder.TH);
    }

    /**
     * Emits the start of a "th" tag.
     *
     * @param cls the CSS class; {@code null} to omit
     */
    public void sTh(final String cls) {

        if (cls == null) {
            addString(CharHtmlBuilder.TH);
        } else {
            addStrings(CharHtmlBuilder.OPEN_TH, CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharXmlBuilder.TICK_CLOSE);
        }
    }

    /**
     * Emits the end of a "th" tag.
     */
    public void eTh() {

        addString(CharHtmlBuilder.CLOSE_TH);
    }

    /**
     * Emits the start of a "td" tag.
     */
    public void sTd() {

        addString(CharHtmlBuilder.TD);
    }

    /**
     * Emits the start of a "td" tag.
     *
     * @param cls the CSS class; {@code null} to omit
     */
    public void sTd(final String cls) {

        if (cls == null) {
            addString(CharHtmlBuilder.TD);
        } else {
            addStrings(CharHtmlBuilder.OPEN_TD, CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharXmlBuilder.TICK_CLOSE);
        }
    }

    /**
     * Emits the start of a "td" tag.
     *
     * @param cls        the CSS class; {@code null} to omit
     * @param attributes additional attributes to add
     */
    public void sTd(final String cls, final String... attributes) {

        addString(CharHtmlBuilder.OPEN_TD);
        if (cls != null) {
            addStrings(CharHtmlBuilder.SP_CLASS_EQ_TICK, cls, CharHtmlBuilder.TICK);
        }

        for (final String attr : attributes) {
            addChar(CharBuilder.SPC);
            addString(attr);
        }

        addChar(CharXmlBuilder.GT_CHAR);
    }

    /**
     * Emits the end of a "td" tag.
     */
    public void eTd() {

        addString(CharHtmlBuilder.CLOSE_TD);
    }
}
