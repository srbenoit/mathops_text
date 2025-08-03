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

import dev.mathops.commons.log.EDebugLevel;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.model.AttrKey;
import dev.mathops.commons.model.EAllowedChildren;
import dev.mathops.commons.model.ModelTreeNode;
import dev.mathops.commons.model.codec.StringCodec;
import dev.mathops.text.parser.ParsingException;
import dev.mathops.text.parser.xml.CData;
import dev.mathops.text.parser.xml.EXmlContentMode;
import dev.mathops.text.parser.xml.EmptyElement;
import dev.mathops.text.parser.xml.IElement;
import dev.mathops.text.parser.xml.INode;
import dev.mathops.text.parser.xml.NonemptyElement;
import dev.mathops.text.parser.xml.XmlContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A parser that builds a model tree from an XML file.  This class has no information about the expected data types of
 * attributes, so all attributes are loaded with String type.  Consumers of the model can install typed versions of
 * attribute keys and convert the String values as needed (in the spirit of the CSS cascade, the string values would be
 * cascaded, then converted to typed values when needed).
 */
public enum XmlTreeParser {
    ;

    /**
     * Parses the contents of an XML file or stream into a tree.
     *
     * @param xml        the source XML
     * @param mode       the parser mode
     * @param factory    a factory object to create new model tree nodes
     * @param debugLevel the debug level that governs diagnostic output
     * @return the parsed tree
     * @throws ParsingException if the source XML could not be parsed
     */
    public static ModelTreeNode parse(final char[] xml, final ETreeParserMode mode, final IModelTreeNodeFactory factory,
                                      final EDebugLevel debugLevel) throws ParsingException {

        final XmlContent content = new XmlContent(xml, mode == ETreeParserMode.ELEMENTS_ONLY, false);

        final IElement topLevel = content.getTopLevel();

        return topLevel == null ? null : nodeFromElement(topLevel, mode, null, factory, debugLevel);
    }

    /**
     * Constructs a node from an XML element.
     *
     * @param elem       the XML element
     * @param mode       the parser mode
     * @param parent     the parent node (null if creating the root node)
     * @param factory    a factory object to create new model tree nodes
     * @param debugLevel the debug level that governs diagnostic output
     * @return the node
     */
    private static ModelTreeNode nodeFromElement(final IElement elem, final ETreeParserMode mode,
                                                 final ModelTreeNode parent, final IModelTreeNodeFactory factory,
                                                 final EDebugLevel debugLevel) {

        ModelTreeNode node = null;

        final String tagName = elem.getTagName();

        try {
            final AllowedAttributes allowedAttributes = new AllowedAttributes();
            node = factory.construct(tagName, parent, allowedAttributes);
            node.map().put(XmlTreeWriter.TAG, tagName);

            // Extract all attributes and add to the model node (with String type)
            if (allowedAttributes.isEmpty()) {
                // Add all the attributes found, creating new String keys for each
                for (final String attrName : elem.attributeNames()) {
                    final String value = elem.getStringAttr(attrName);
                    final AttrKey<String> key = new AttrKey<>(attrName, StringCodec.INST);
                    node.map().put(key, value);
                }
            } else {
                // Add only the attributes that match what the factory allows
                for (final String attrName : elem.attributeNames()) {
                    final AttrKey<?> key = allowedAttributes.get(attrName);
                    if (key == null) {
                        if (debugLevel.level < EDebugLevel.INFO.level) {
                            Log.warning("Attribute '", attrName, "' not supported in <", tagName,
                                    "> element - ignoring.");
                        }
                    } else {
                        final String value = elem.getStringAttr(attrName);
                        node.map().putString(key, value);
                    }
                }
            }

            if (elem instanceof final NonemptyElement nonempty) {
                // Extract the lists of child nodes
                final List<INode> childNodes = nonempty.getChildrenAsList();

                for (final INode child : childNodes) {
                    ModelTreeNode innerNode = null;

                    if (child instanceof final IElement childElement) {
                        innerNode = nodeFromElement(childElement, mode, node, factory, debugLevel);
                    } else if (mode != ETreeParserMode.ELEMENTS_ONLY && child instanceof final CData cdata) {

                        final EAllowedChildren allowed = node.getAllowedChildren();

                        if (allowed == EAllowedChildren.ELEMENT_AND_DATA || allowed == EAllowedChildren.DATA) {
                            innerNode = new ModelTreeNode();
                            final String content = cdata.getContent();
                            innerNode.map().put(XmlTreeWriter.VALUE, content);
                        } else if (debugLevel.level < EDebugLevel.INFO.level) {
                            final String content = cdata.getContent();
                            if (content != null && !content.isBlank()) {
                                Log.warning("Character data not supported in <", tagName, "> element - ignoring.");
                            }
                        }
                    }

                    if (innerNode != null) {
                        innerNode.setParent(node);
                        node.addChild(innerNode);
                    }
                }
            } else if (!(elem instanceof EmptyElement)) {
                throw new IllegalArgumentException("Top level node is not an element");
            }
        } catch (final TagNotAllowedException ex) {
            Log.warning("Tree node factory could not construct root node with tag '", tagName, "'");
        }

        return node;
    }
}














