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

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.EDebugLevel;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.model.DataKey;
import dev.mathops.commons.model.ModelTreeNode;
import dev.mathops.commons.model.StringParseException;
import dev.mathops.commons.model.TypedKey;
import dev.mathops.commons.model.TypedMap;
import dev.mathops.commons.model.codec.BooleanCodec;
import dev.mathops.commons.model.codec.StringCodec;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.parser.xml.XmlEscaper;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that writes a tree in XML format.
 *
 * <p>Element nodes must have a DATA value named "tag" with the XML element tag name.  Text nodes must have a
 * DATA value named "value" with a {@code String} value.  No other DATA values are persisted.  If a node has a DATA
 * value named "inline" with a Boolean value, that value controls whether the element is emitted without newlines and
 * indentation (such as &lt;b&gt;bold&lt;/b&gt;);
 *
 * <p>
 * PROPERTY values are not persisted, and ATTRIBUTE values are emitted as XML attributes.  If an ATTRIBUTE value is not
 * an implicitly supported type and does not have a translator to String type, it is not persisted.  When the resulting
 * XML is parsed, all attributes will be treated as {@code String}.
 *
 * <p>
 * Typically, if a model tree is being constructed with the intent to persist as XML, all attribute values will be
 * {@code String}, and the "tag" and "content" values are set (also {@code String}), and no other content or property
 * values are stored.
 */
public enum XmlTreeWriter {
    ;

    /** A commonly-used character. */
    private static final char SPC = ' ';

    /** The key of the content value that defines the XML tag. */
    public static final DataKey<String> TAG = new DataKey<>("tag", StringCodec.INST);

    /** The key of the content value that defines the text node content. */
    public static final DataKey<String> VALUE = new DataKey<>("value", StringCodec.INST);

    /** The key of the content value that defines a comment associated with an XML element. */
    public static final DataKey<String> COMMENT = new DataKey<>("comment", StringCodec.INST);

    /** A flag that causes an element to be emitted without newlines or indentation. */
    public static final DataKey<Boolean> INLINE = new DataKey<>("inline", BooleanCodec.INST);

    /**
     * Writes the XML representation of a tree node, with all of its attributes and descendants, to an output stream.
     *
     * @param root       the root node of the tree to write
     * @param writer     the {@code Writer} to which to write
     * @param indent     the number of spaces by which to indent
     * @param debugLevel the debug level that governs diagnostic output
     * @throws IllegalArgumentException if {@code root} is null, {@code out} is null,  or the node (or a descendant) is
     *                                  of an unsupported type
     * @throws IOException              if there is an error writing
     */
    public static void write(final ModelTreeNode root, final Writer writer, final int indent,
                             final EDebugLevel debugLevel) throws IllegalArgumentException, IOException {

        final HtmlBuilder builder = new HtmlBuilder(100);
        write(root, builder, indent, debugLevel, null);
        builder.add(CoreConstants.CRLF);

        final String str = builder.toString();
        writer.write(str);
    }

    /**
     * Writes the XML representation of a tree node to an output stream.  A newline is written after the element.
     *
     * @param root       the root node of the tree to write
     * @param builder    the {@code CharHtmlBuilder} to which to append
     * @param indent     the number of spaces by which to indent
     * @param debugLevel the debug level that governs diagnostic output
     * @param inline     the inline level of an ancestor node (null if none)
     * @throws IllegalArgumentException if {@code root} is null, {@code out} is null,  or the node (or a descendant) is
     *                                  of an unsupported type
     */
    public static void write(final ModelTreeNode root, final HtmlBuilder builder, final int indent,
                             final EDebugLevel debugLevel, final Boolean inline) {

        if (root == null) {
            final String msg = Res.get(Res.NULL_NODE);
            throw new IllegalArgumentException(msg);
        }
        if (builder == null) {
            final String msg = Res.get(Res.NULL_BUILDER);
            throw new IllegalArgumentException(msg);
        }

        final TypedMap rootMap = root.map();

        Boolean effectiveInline;
        try {
            final Boolean myInline = rootMap.get(INLINE);
            effectiveInline = myInline == null ? inline : myInline;
        } catch (final StringParseException ex) {
            Log.warning(ex);
            effectiveInline = inline;
        }

        try {
            final String comment = rootMap.get(COMMENT);
            if (comment != null) {
                if (!Boolean.TRUE.equals(inline) && indent > 0) {
                    doIndent(builder, indent);
                }
                builder.addln("<!-- ", comment, " -->");
            }
        } catch (final StringParseException ex) {
            Log.warning(ex);
        }

        if (!Boolean.TRUE.equals(inline) && indent > 0) {
            doIndent(builder, indent);
        }

        final String xmlTag = rootMap.getString(TAG);
        if (xmlTag == null) {
            // This is a text node - emit without a line break
            final String textValue = rootMap.getString(VALUE);
            if (textValue != null) {
                final String escaped = XmlEscaper.escape(textValue);
                builder.add(escaped);
            } else if (debugLevel.level < EDebugLevel.INFO.level) {
                Log.warning("Node with neither 'tag' nor 'value' data value - skipping.");
            }
        } else {
            // This is an element node.
            builder.add("<", xmlTag);

            // Write all attributes (key names are guaranteed to be valid XML attribute names)
            final List<TypedKey<?>> keyList = new ArrayList<>(10);
            rootMap.getAttributeKeys(keyList);
            for (final TypedKey<?> key : keyList) {
                final String value = rootMap.getString(key);
                if (value != null) {
                    final String escaped = XmlEscaper.escape(value);
                    builder.add(" ", key.getName(), "='", escaped, "'");
                }
            }

            // See of there are child nodes...
            ModelTreeNode child = rootMap.getNode(ModelTreeNode.FIRST_CHILD);
            if (child == null) {
                builder.add("/>");
            } else {
                builder.add(">");

                // Emit children
                while (child != null) {
                    // NOTE: We might want to test for child's inline state and skip the newline here if it's TRUE.
                    if (!Boolean.TRUE.equals(effectiveInline)) {
                        builder.add(CoreConstants.CRLF);
                    }
                    write(child, builder, indent + 1, debugLevel, effectiveInline);
                    child = child.getNextSibling();
                }

                if (!Boolean.TRUE.equals(effectiveInline)) {
                    builder.add(CoreConstants.CRLF);
                    if (indent > 0) {
                        doIndent(builder, indent);
                    }
                }

                builder.add("</", xmlTag, ">");
            }
        }
    }

    /**
     * Performs indentation.
     *
     * @param builder the {@code CharHtmlBuilder} to which to append
     * @param indent  the number of spaces by which to indent
     */
    private static void doIndent(final HtmlBuilder builder, final int indent) {

        final int count = 2 * indent;
        for (int i = 0; i < count; ++i) {
            builder.add(SPC);
        }
    }
}
