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

package dev.mathops.text.model;

/**
 * States of the parser.
 */
enum EParseState {

    /** Waiting for the '&lt;' to start an element. */
    AWAITING_ELEMENT,

    /** Awaiting element, found a '&lt;' character. */
    AWAITING_ELEMENT_OPEN_ANGLE,

    /** Within a "&lt;? ... " block, scanning for the closing "?&gt;". */
    PI,

    /** Within a "&lt;? ... " block, found '?' testing for '&gt;'. */
    PI_CLOSE,

    /** Found a "&lt;!" that marks the start of a comment. */
    START_COMMENT,

    /** Found the first dash after "&lt;!" at the start of a comment. */
    COMMENT_DASH,

    /** Accumulating comment, awaiting the '--&gt;' to end the comment. */
    COMMENT,

    /** Accumulating comment, found a single '-' within comment text that is not at line end. */
    COMMENT_END1,

    /** Accumulating comment, found "--" within comment text that is not at line end. */
    COMMENT_END2,

    /** Accumulating an element name. */
    ELEMENT_NAME,

    /** In an element opening tag, scanning for attribute name or '&gt;'. */
    ELEMENT_AWAITING_ATTR,

    /** In an element opening tag, found '/' scanning for '&gt;'. */
    CLOSING_EMPTY_ELEMENT,

    /** Accumulating an attribute name. */
    ATTR_NAME,

    /** Scanning for the '=' after an attribute name. */
    AWAIT_ATTR_EQ,

    /** Scanning for the opening quote for the attribute value. */
    AWAIT_ATTR_VALUE,

    /** Reading attribute value. */
    ATTR_VALUE,

    /** Accumulating CDATA. */
    CDATA,

    /** In the element end tag. */
    ELEMENT_END_TAG,

    /** Scanning for the '>' to finish an end tag. */
    ELEMENT_END_TAG_CLOSE,
}