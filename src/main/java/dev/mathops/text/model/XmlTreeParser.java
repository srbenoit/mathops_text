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
import dev.mathops.commons.ESuccessFailure;
import dev.mathops.commons.log.EDebugLevel;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.model.AttrKey;
import dev.mathops.commons.model.EAllowedChildren;
import dev.mathops.commons.model.ModelTreeNode;
import dev.mathops.commons.model.StringParseException;
import dev.mathops.commons.model.TypedMap;
import dev.mathops.commons.model.codec.StringCodec;
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.builder.SimpleBuilder;
import dev.mathops.text.parser.LineOrientedParserInput;
import dev.mathops.text.parser.ParsingException;
import dev.mathops.text.parser.xml.CData;
import dev.mathops.text.parser.xml.EmptyElement;
import dev.mathops.text.parser.xml.IElement;
import dev.mathops.text.parser.xml.INode;
import dev.mathops.text.parser.xml.NonemptyElement;
import dev.mathops.text.parser.xml.XmlChars;
import dev.mathops.text.parser.xml.XmlContent;
import dev.mathops.text.parser.xml.XmlEscaper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A parser that builds a model tree from an XML file.  This class has no information about the expected data types of
 * attributes, so all attributes are loaded with String type.  Consumers of the model can install typed versions of
 * attribute keys and convert the String values as needed (in the spirit of the CSS cascade, the string values would be
 * cascaded, then converted to typed values when needed).
 *
 * <p>
 * The parser supports a grammar of which XML 1.0 is a proper subset.  All valid XML 1.0 will be successfully parsed,
 * but this parser ignores any content that begins with "&lt;?" and ends with "?&gt;", and does not disallow "--" within
 * comments.
 *
 * <pre>
 * Char          ::= #x9 | #xA | #xD | [#x20-@xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
 * S             ::= (#x20 | #x9 | #xD | #xA)+
 * NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] |
 *                   [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] |
 *                   [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] |
 *                   [#x10000-#xEFFFF]
 * NameChar      ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
 *
 * Name          ::= NameStartChar (NameChar)*
 * document      ::= prolog element (Comment | S)*
 * prolog        ::= XMLDecl? (Comment | S)*
 * XMLDecl       ::= '&lt;?xml' VersionInfo EncodingDecl? SDDecl? S? '?&gt;'
 * VersionInfo   ::= S 'version' S? '=' S? ("'" VersionNum "'" | '"' VersionNum '"')
 * VersionNum    ::= '1.' [0-9]+
 * EncodingDecl  ::= S 'encoding' S? '=' S? ('"' EncName '"' | "'" EncName "'")
 * SDDecl        ::= S 'standalone' S? '=' S? (("'" ('yes' | 'no') "'") | ('"' ('yes' | 'no') '"'))
 * EncName       ::= [A-Za-z] ([A-Za-z0-9._) | '-')*
 * Comment       ::= '&lt;!--' ((Char - '-') | ('-' (Char - '-')))* '--&gt;'
 * doctypedecl   ::= '&lt;!DOCTYPE' (Char - '&gt;')* ('&lt;' (Char - '&gt;')* '&gt;' )*
 *                   (Char - '&gt;')* '&gt;'
 * element       ::= EmptyElemTag | STag content ETag
 * EmptyElemTag  ::= '&lt;' Name (S Name S? '=' S? AttValue)* S? '/&gt;'
 * STag          ::= '&lt;' Name (S Name S? '=' S? AttValue)* S? '&gt;'
 * ETag          ::= '&lt;/ Name S? '&gt;'
 * AttValue      ::= '"' ([^&lt;&"] | Reference)* '"' | "'" ([^&lt;&'] | Reference)* "'"
 * Reference     ::= '&' Name ';' | '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
 * content       ::= CharData? ((element | Reference | CDSect | Comment) CharData?)*
 * CDSect        ::= '&lt;![CDATA[' (Char* - (Char* ']]&gt;' Char*)) ']]&gt;'
 * CharData      ::= [^&lt;&]* - ([^&lt;&]* ']]&gt;' [^&lt;&]*)
 * </pre>
 */
public final class XmlTreeParser {

    /** A character used in XML. */
    private static final int LEFT_ANGLE_BRACKET = (int) '<';

    /** A character used in XML. */
    private static final int RIGHT_ANGLE_BRACKET = (int) '>';

    /** A character used in XML. */
    private static final int QUESTION_MARK = (int) '?';

    /** A character used in XML. */
    private static final int DASH = (int) '-';

    /** A character used in XML. */
    private static final int BANG = (int) '!';

    /** A character used in XML. */
    private static final int SLASH = (int) '/';

    /** A character used in XML. */
    private static final int EQ = (int) '=';

    /** A character used in XML. */
    private static final int TICK = (int) '\'';

    /** A character used in XML. */
    private static final int QUOTE = (int) '"';

    /** A character used in XML. */
    private static final int AND = (int) '&';

    /** The node factory. */
    private final IModelTreeNodeFactory factory;

    /** The current state. */
    private EParseState state;

    /** An accumulator for names, attribute values, comment content. */
    private final HtmlBuilder accumulator;

    /** An accumulator for CDATA. */
    private final HtmlBuilder cdata;

    /** A comment pending attachment to the next element found. */
    private String pendingComment = null;

    /** The name of the pending attribute. */
    private String pendingAttrName = null;

    /** The quote code point surrounding the current attribute value. */
    private int attrValueQuote = 0;

    /** A stack of elements from the root down to the currently open element. */
    private final List<ModelTreeNode> elementStack;

    private final AllowedAttributes allowedAttributes;

    /** The root element. */
    private ModelTreeNode root = null;

    /**
     * Constructs a new {@code XmlTreeParser}.
     *
     * @param theFactory the node factory
     */
    private XmlTreeParser(final IModelTreeNodeFactory theFactory) {

        if (theFactory == null) {
            throw new IllegalArgumentException("Factory may not be null");
        }

        this.factory = theFactory;
        this.state = EParseState.AWAITING_ELEMENT;
        this.accumulator = new HtmlBuilder(30);
        this.cdata = new HtmlBuilder(100);
        this.elementStack = new ArrayList<>(10);

        this.allowedAttributes = new AllowedAttributes();
    }

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
    @Deprecated
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
    @Deprecated
    private static ModelTreeNode nodeFromElement(final IElement elem, final ETreeParserMode mode,
                                                 final ModelTreeNode parent, final IModelTreeNodeFactory factory,
                                                 final EDebugLevel debugLevel) {

        ModelTreeNode node = null;

        final String tagName = elem.getTagName();

        try {
            final AllowedAttributes allowedAttributes = new AllowedAttributes();
            node = factory.construct(tagName, parent, allowedAttributes);

            final TypedMap map = node.map();
            map.put(XmlTreeWriter.TAG, tagName);

            // Extract all attributes and add to the model node (with String type)
            if (allowedAttributes.isEmpty()) {
                // Add all the attributes found, creating new String keys for each
                for (final String attrName : elem.attributeNames()) {
                    final String value = elem.getStringAttr(attrName);
                    final AttrKey<String> key = new AttrKey<>(attrName, StringCodec.INST);
                    map.put(key, value);
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
                        map.putString(key, value);
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
                            final TypedMap innerMap = innerNode.map();
                            innerMap.put(XmlTreeWriter.VALUE, content);
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

    /**
     * Parses the contents of an XML model tree node or stream into a tree.
     *
     * @param xmlTree    the model tree node root for the parsed XML document
     * @param factory    a factory object to create new model tree nodes
     * @param debugLevel the debug level that governs diagnostic output
     * @return the parsed tree
     * @throws ParsingException if the source XML could not be parsed
     */
    @Deprecated
    public static ModelTreeNode xmlToModel(final ModelTreeNode xmlTree, final IModelTreeNodeFactory factory,
                                           final EDebugLevel debugLevel) throws ParsingException {

        return nodeFromXmlNode(xmlTree, null, factory, debugLevel);
    }

    /**
     * Constructs a model tree node for a document defined by a supplied node factory from an XML tree node.
     *
     * @param xmlNode    the XML tree node
     * @param parent     the parent node (null if creating the root node)
     * @param factory    a factory object to create new model tree nodes
     * @param debugLevel the debug level that governs diagnostic output
     * @return the node
     */
    @Deprecated
    private static ModelTreeNode nodeFromXmlNode(final ModelTreeNode xmlNode, final ModelTreeNode parent,
                                                 final IModelTreeNodeFactory factory, final EDebugLevel debugLevel) {

        final TypedMap xmlNodeMap = xmlNode.map();
        final String tagName = xmlNodeMap.getString(XmlTreeWriter.TAG);
        final AllowedAttributes allowedAttributes = new AllowedAttributes();

        ModelTreeNode modelNode = null;
        try {
            modelNode = factory.construct(tagName, parent, allowedAttributes);
            final TypedMap modelNodeMap = modelNode.map();

            // Preserve the XML tag of the source XML node - we want the model node to be able to persist itself as XML
            modelNodeMap.put(XmlTreeWriter.TAG, tagName);

            // Extract all attributes and add to the model node (with String type)
            if (allowedAttributes.isEmpty()) {
                // Factory did not apply any restrictions, so add all the attributes found
                modelNodeMap.copyAllAttributesFrom(xmlNodeMap);
            } else {
                // Add only the attributes that match what the factory allows
                final Collection<AttrKey<?>> attrKeys = new ArrayList<>(10);
                xmlNodeMap.getAttributeKeys(attrKeys);

                for (final AttrKey<?> key : attrKeys) {
                    if (allowedAttributes.contains(key)) {
                        modelNodeMap.copyAttributeFrom(xmlNodeMap, key);
                    } else {
                        if (debugLevel.level < EDebugLevel.INFO.level) {
                            final String attrName = key.getName();
                            Log.warning("Attribute '", attrName, "' not supported in <", tagName,
                                    "> element - ignoring.");
                        }
                    }
                }
            }

            // Extract the lists of child nodes
            ModelTreeNode child = xmlNode.getFirstChild();

            while (child != null) {
                ModelTreeNode innerNode = null;

                // See if the XML element is a "CDATA" element
                final TypedMap childMap = child.map();
                final String cdata = childMap.getString(XmlTreeWriter.VALUE);
                if (cdata == null) {
                    innerNode = nodeFromXmlNode(child, modelNode, factory, debugLevel);
                } else {
                    final EAllowedChildren allowed = modelNode.getAllowedChildren();

                    if (allowed == EAllowedChildren.ELEMENT_AND_DATA || allowed == EAllowedChildren.DATA) {
                        innerNode = new ModelTreeNode();
                        final TypedMap innerMap = innerNode.map();
                        innerMap.put(XmlTreeWriter.VALUE, cdata);
                    } else if (debugLevel.level < EDebugLevel.INFO.level) {
                        if (!cdata.isBlank()) {
                            Log.warning("Character data not supported in <", tagName, "> element - ignoring.");
                        }
                    }
                }

                if (innerNode != null) {
                    innerNode.setParent(modelNode);
                    modelNode.addChild(innerNode);
                }

                child = child.getNextSibling();
            }
        } catch (final TagNotAllowedException ex) {
            Log.warning("Tree node factory could not construct root node with tag '", tagName, "'");
        }

        return modelNode;
    }

    /**
     * Attempts to parse XML content into a model tree node.
     *
     * @param xml     the input to parse
     * @param factory a factory used to create nodes based on XML tag name and parent node
     * @param log     a log to which to add parsing errors, warnings, and messages
     * @return the root element if parsing was successful; {@code null} if not
     */
    public static ModelTreeNode parseXml(final LineOrientedParserInput xml, final IModelTreeNodeFactory factory,
                                         final ParsingLog log) {

        final XmlTreeParser parser = new XmlTreeParser(factory);

        ESuccessFailure status = ESuccessFailure.SUCCESS;

        final int numLines = xml.getNumLines();
        outer:
        for (int line = 0; line < numLines; ++line) {
            final String content = xml.getLine(line);

            final int last = content.length() - 1;
            for (int col = 0; col <= last; ++col) {
                final int cp = (int) content.charAt(col);
                status = parser.processCharacter(line, col, cp, col == last, log);
                if (status == ESuccessFailure.FAILURE) {
                    // Parser cannot recover from error - abort parsing
                    break outer;
                }
            }
        }

        if (status == ESuccessFailure.SUCCESS) {
            if (parser.state != EParseState.AWAITING_ELEMENT) {
                final String last = xml.getLine(numLines - 1);
                final int len = last.length();
                final String msg = Res.get(Res.UNEXPECTED_EOF);
                log.add(ParsingLogEntryType.ERROR, numLines - 1, len, msg);
            } else if (parser.root == null) {
                log.add(ParsingLogEntryType.ERROR, 0, 0, "No element found in XML file");
            }
        }

        return parser.root;
    }

    /**
     * Processes a single character of input.
     *
     * @param line       the line number
     * @param cp         the code point
     * @param lastInLine true if the character is the last in a line of input text
     * @param log        a log to which to add parsing errors, warnings, and messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure processCharacter(final int line, final int col, final int cp, final boolean lastInLine,
                                             final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        switch (this.state) {
            case AWAITING_ELEMENT -> result = doAwaitingElement(line, col, cp, lastInLine, log);
            case AWAITING_ELEMENT_OPEN_ANGLE -> result = doAwaitingElementOpenAngle(line, col, cp, lastInLine, log);
            case PI -> doPI(cp, lastInLine);
            case PI_CLOSE -> doPIClose(cp);
            case START_COMMENT -> result = doStartComment(line, col, cp, lastInLine, log);
            case COMMENT_DASH -> result = doCommentDash(line, col, cp, log);
            case COMMENT -> doComment(cp, lastInLine);
            case COMMENT_END1 -> doCommentEnd1(cp, lastInLine);
            case COMMENT_END2 -> doCommentEnd2(cp);
            case ELEMENT_NAME -> result = doElementName(line, col, cp, lastInLine, log);
            case ELEMENT_AWAITING_ATTR -> result = doElementAwaitingAttr(line, col, cp, log);
            case CLOSING_EMPTY_ELEMENT -> result = doClosingEmptyElement(line, col, cp, log);
            case CDATA -> doCData(line, col, cp, lastInLine, log);
            case ATTR_NAME -> result = doAttrName(line, col, cp, lastInLine, log);
            case AWAIT_ATTR_EQ -> result = doAwaitAttrEq(line, col, cp, log);
            case AWAIT_ATTR_VALUE -> result = doAwaitAttrValue(line, col, cp, log);
            case ATTR_VALUE -> doAttrValue(line, col, cp, log);
            case ELEMENT_END_TAG -> result = doElementEndTag(line, col, cp, lastInLine, log);
            case ELEMENT_END_TAG_CLOSE -> result = doElementEndTagClose(line, col, cp, log);
        }

        return result;
    }

    /**
     * Processes a character in the AWAITING_ELEMENT state.  Valid characters are whitespace (which does not change
     * state), a '<' that is not the last character in a line, which could be the start of a PI, a comment, or an
     * element, and which changes state to AWAITING_ELEMENT_OPEN_ANGLE, or (if CDATA is allowed), the first character of
     * a CDATA run.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doAwaitingElement(final int line, final int col, final int cp, final boolean lastInLine,
                                              final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == LEFT_ANGLE_BRACKET) {
            if (lastInLine) {
                log.add(ParsingLogEntryType.ERROR, line, col, "'<' must be followed by '?', '!', or an element name.");
                result = ESuccessFailure.FAILURE;
            } else {
                this.state = EParseState.AWAITING_ELEMENT_OPEN_ANGLE;
            }
        } else if (!XmlChars.isWhitespace(cp)) {
            if (this.elementStack.isEmpty()) {
                // CDATA is not allowed outside the root element
                log.add(ParsingLogEntryType.ERROR, line, col, "Unexpected character, expecting whitespace or '<'.");
                result = ESuccessFailure.FAILURE;
            } else {
                this.cdata.reset();
                this.cdata.appendChar((char) cp);
                this.state = EParseState.CDATA;
            }
        }

        return result;
    }

    /**
     * Processes a character in the AWAITING_ELEMENT_OPEN_ANGLE state.  Valid characters are '?' (starts a PI, and
     * changes state to PI), '!' (starts a comment, and changes state to PROLOG_START_COMMENT) or a Name Start character
     * (which starts an element, and changes state to ELEMENT_NAME (or ELEMENT_AWAITING_ATTR if the name start character
     * was at the end of its line).  If any other character is found, the '&lt;' and that character are treated as CDATA
     * (if allowed) or an error (if not).
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doAwaitingElementOpenAngle(final int line, final int col, final int cp,
                                                       final boolean lastInLine, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == QUESTION_MARK) {
            // NOTE: This can fall at the end of a line, and we still scan for the closure.
            this.state = EParseState.PI;
        } else if (cp == BANG) {
            if (lastInLine) {
                if (this.elementStack.isEmpty()) {
                    // CDATA is not allowed outside the root element
                    log.add(ParsingLogEntryType.ERROR, line, col, "'<!' must be followed by '--', to start a comment.");
                    result = ESuccessFailure.FAILURE;
                } else {
                    this.cdata.reset();
                    this.cdata.add("<!");
                    this.state = EParseState.CDATA;
                    log.add(ParsingLogEntryType.WARNING, line, col - 1, "'<' should be escaped in character data.");
                }
            } else {
                this.state = EParseState.START_COMMENT;
            }
        } else if (cp == SLASH && !lastInLine) {
            // This is a "</..." closing tag of a nonempty element
            if (this.elementStack.isEmpty()) {
                log.add(ParsingLogEntryType.ERROR, line, col - 1, line, col, "No open element to close.");
                result = ESuccessFailure.FAILURE;
            } else {
                this.accumulator.reset();
                this.state = EParseState.ELEMENT_END_TAG;
            }
        } else if (XmlChars.isNameStartChar(cp)) {
            if (lastInLine) {
                // The linefeed counts as whitespace to close the element name
                final String tag = Character.toString(cp);
                try {
                    createElement(tag);
                } catch (final TagNotAllowedException ex) {
                    final String msg = SimpleBuilder.concat("The ", tag, " tag is not allowed here.");
                    // Continue parsing - maybe we can catch more errors
                    log.add(ParsingLogEntryType.ERROR, line, col, msg);
                }
                this.state = EParseState.ELEMENT_AWAITING_ATTR;
            } else {
                this.accumulator.appendChar((char) cp);
                this.state = EParseState.ELEMENT_NAME;
            }
        } else if (this.elementStack.isEmpty()) {
            // CDATA is not allowed outside the root element
            log.add(ParsingLogEntryType.ERROR, line, col,
                    "Unexpected character, expecting '?', '!', or an element name.");
            result = ESuccessFailure.FAILURE;
        } else {
            this.cdata.reset();
            this.cdata.appendChar((char) LEFT_ANGLE_BRACKET);
            this.cdata.appendChar((char) cp);
            this.state = EParseState.CDATA;
            log.add(ParsingLogEntryType.WARNING, line, col - 1, "'<' should be escaped in character data.");
        }

        return result;
    }

    /**
     * Creates a new element, adds it as a child to the currently active element, and makes the new element the
     * currently active element.
     *
     * @param tag the element's XML tag
     * @throws TagNotAllowedException of the tag is not allowed in this context by the factory
     */
    private void createElement(final String tag) throws TagNotAllowedException {

        this.allowedAttributes.reset();

        ModelTreeNode parent = null;
        if (!this.elementStack.isEmpty()) {
            parent = this.elementStack.getLast();
        }

        this.factory.construct(tag, parent, this.allowedAttributes);

        final ModelTreeNode newElement = new ModelTreeNode();
        final TypedMap map = newElement.map();
        map.put(XmlTreeWriter.TAG, tag);

        if (!this.elementStack.isEmpty()) {
            final ModelTreeNode activeElement = this.elementStack.getLast();
            activeElement.addChild(newElement);
        }

        if (this.pendingComment != null) {
            map.put(XmlTreeWriter.COMMENT, this.pendingComment);
            this.pendingComment = null;
        }

        this.elementStack.add(newElement);
    }

    /**
     * Adds an attribute to the active element using the stored pending attribute name.
     *
     * @param value the attribute value
     * @param log   a log to which to add errors
     */
    private void addAttr(final int line, final int col, final String value, final ParsingLog log) {

        final String unescaped = XmlEscaper.unescape(value);

        final ModelTreeNode activeElement = this.elementStack.getLast();

        if (this.allowedAttributes.isEmpty()) {
            final AttrKey<String> key = new AttrKey<>(this.pendingAttrName, StringCodec.INST);
            final TypedMap map = activeElement.map();
            map.putString(key, unescaped);
        } else {
            final AttrKey<?> key = this.allowedAttributes.get(this.pendingAttrName);
            if (key == null) {
                final String msg = SimpleBuilder.concat("'", this.pendingAttrName,
                        "' attribute is not supported - ignoring.");
                log.add(ParsingLogEntryType.ERROR, line, col - 2, line, col, msg);
            } else {
                final TypedMap map = activeElement.map();
                map.putString(key, unescaped);
            }
        }

        this.pendingAttrName = null;
    }

    /**
     * Processes a character in the PI (processing instruction) state.  Any character is valid here, but '?' (not at the
     * end of a line) moves to the PI_CLOSE state.
     *
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     */
    private void doPI(final int cp, final boolean lastInLine) {

        if (cp == QUESTION_MARK && !lastInLine) {
            this.state = EParseState.PI_CLOSE;
        }
    }

    /**
     * Processes a character in the PI_CLOSE state.  If the character is '&gt;', the PI is closed, and we return to the
     * AWAITING_ELEMENT state; otherwise, we return to the PI state.
     *
     * @param cp the code point
     */
    private void doPIClose(final int cp) {

        // NOTE: The '?' that got us to this state was not allowed to be the last character in its line, so we do
        // not need to test whether we are looking at the first character in a line.

        this.state = cp == RIGHT_ANGLE_BRACKET ? EParseState.AWAITING_ELEMENT : EParseState.PI;
    }

    /**
     * Processes a character in the START_COMMENT state.  The only valid character is a '-', which moves to the
     * COMMENT_DASH state.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doStartComment(final int line, final int col, final int cp, final boolean lastInLine,
                                           final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        // NOTE: The '!' that got us to this state was not allowed to be the last character in its line, so we do
        // not need to test whether we are looking at the first character in a line.

        if (cp == DASH) {
            if (lastInLine) {
                // Found "<!-" at the end of a line.
                if (this.elementStack.isEmpty()) {
                    // CDATA is not allowed outside the root element
                    log.add(ParsingLogEntryType.ERROR, line, col - 2, line, col,
                            "Start of XML comment is missing second '-' character.");
                    result = ESuccessFailure.FAILURE;
                } else {
                    this.cdata.reset();
                    this.cdata.add("<!-");
                    this.state = EParseState.CDATA;
                    log.add(ParsingLogEntryType.WARNING, line, col - 2, "'<' should be escaped in character data.");
                }
            } else {
                this.state = EParseState.COMMENT_DASH;
            }
        } else if (this.elementStack.isEmpty()) {
            // CDATA is not allowed outside the root element
            log.add(ParsingLogEntryType.ERROR, line, col - 2, line, col,
                    "Unexpected character, expecting '<!--' to begin a comment.");
            result = ESuccessFailure.FAILURE;
        } else {
            this.cdata.reset();
            this.cdata.add("<!");
            this.cdata.appendChar((char) cp);
            this.state = EParseState.CDATA;
            log.add(ParsingLogEntryType.WARNING, line, col - 2, "'<' should be escaped in character data.");
        }

        return result;
    }

    /**
     * Processes a character in the COMMENT_DASH state.  The only valid character is a '-', which moves to the COMMENT
     * state.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doCommentDash(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        // NOTE: The '-' that got us to this state was not allowed to be the last character in its line, so we do
        // not need to test whether we are looking at the first character in a line.

        if (cp == DASH) {
            this.state = EParseState.COMMENT;
            this.accumulator.reset();
        } else if (this.elementStack.isEmpty()) {
            // CDATA is not allowed outside the root element
            log.add(ParsingLogEntryType.ERROR, line, col - 3, line, col,
                    "Unexpected character, expecting '<!--' to begin a comment.");
            result = ESuccessFailure.FAILURE;
        } else {
            this.cdata.reset();
            this.cdata.add("<!-");
            this.cdata.appendChar((char) cp);
            this.state = EParseState.CDATA;
            log.add(ParsingLogEntryType.WARNING, line, col - 3, "'<' should be escaped in character data");
        }

        return result;
    }

    /**
     * Processes a character in the COMMENT state.  Comment characters are accumulated until we find "-->".  When we
     * find the first '-' (not at the end of its line), we move to the COMMEND_END1 state.
     *
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     */
    private void doComment(final int cp, final boolean lastInLine) {

        if (cp == DASH && !lastInLine) {
            this.state = EParseState.COMMENT_END1;
            this.accumulator.appendChar((char) DASH);
        } else {
            this.accumulator.appendChar((char) cp);
        }
    }

    /**
     * Processes a character in the COMMENT_END1 state, in which we have found one '-' within a comment and are checking
     * for the "-->" closure. If the character is '-' (not at the end of its line), we move to the COMMEND_END2 state.
     * Otherwise, we move back to the COMMENT state.
     *
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     */
    private void doCommentEnd1(final int cp, final boolean lastInLine) {

        this.accumulator.appendChar((char) cp);
        this.state = cp == DASH && !lastInLine ? EParseState.COMMENT_END2 : EParseState.COMMENT;
    }

    /**
     * Processes a character in the COMMENT_END2 state.  If the character is '&gt;', the comment is complete, and we
     * cache it so it can be associated with the next element found.
     *
     * @param cp the code point
     */
    private void doCommentEnd2(final int cp) {

        if (cp == RIGHT_ANGLE_BRACKET) {
            final String commentTextWithDashes = this.accumulator.toString();
            this.accumulator.reset();
            final int len = commentTextWithDashes.length();
            final String commentText = commentTextWithDashes.substring(0, len - 2).trim();
            final String unescaped = XmlEscaper.unescape(commentText);
            if (this.pendingComment == null) {
                this.pendingComment = unescaped;
            } else {
                this.pendingComment = SimpleBuilder.concat(this.pendingComment, " ", unescaped);
            }
            this.state = EParseState.AWAITING_ELEMENT;
        } else {
            this.state = EParseState.COMMENT;
        }
    }

    /**
     * Processes a character in the ELEMENT_NAME state.  If the character is a name character, we accumulate it and
     * remain in this state or if the character was at the end of its line, move to the ELEMENT_AWAITING_ATTR state.
     * Whitespace completes the name amd moves to the ELEMENT_AWAITING_ATTR state, a '>' character moves to the
     * AWAITING_ELEMENT state, and '/' moves to the CLOSING_EMPTY_ELEMENT state.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doElementName(final int line, final int col, final int cp, final boolean lastInLine,
                                          final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == RIGHT_ANGLE_BRACKET) {
            final String tag = this.accumulator.toString();
            this.accumulator.reset();
            try {
                createElement(tag);
                this.state = EParseState.AWAITING_ELEMENT;
            } catch (final TagNotAllowedException ex) {
                final String msg = SimpleBuilder.concat("The ", tag, " tag is not allowed here.");
                // Continue parsing - maybe we can catch more errors
                log.add(ParsingLogEntryType.ERROR, line, col, msg);
            }
        } else if (cp == SLASH) {
            if (lastInLine) {
                // Found '/' at the end of a line while parsing element name
                log.add(ParsingLogEntryType.ERROR, line, col - 2, line, col,
                        "Start of XML comment is missing second '-' character.");
                result = ESuccessFailure.FAILURE;
            } else {
                final String tag = this.accumulator.toString();
                this.accumulator.reset();
                try {
                    createElement(tag);
                    this.state = EParseState.AWAITING_ELEMENT;
                } catch (final TagNotAllowedException ex) {
                    final String msg = SimpleBuilder.concat("The ", tag, " tag is not allowed here.");
                    // Continue parsing - maybe we can catch more errors
                    log.add(ParsingLogEntryType.ERROR, line, col, msg);
                }
                this.state = EParseState.CLOSING_EMPTY_ELEMENT;
            }
        } else if (XmlChars.isNameChar(cp)) {
            this.accumulator.appendChar((char) cp);
            if (lastInLine) {
                final String tag = this.accumulator.toString();
                this.accumulator.reset();
                try {
                    createElement(tag);
                    this.state = EParseState.AWAITING_ELEMENT;
                } catch (final TagNotAllowedException ex) {
                    final String msg = SimpleBuilder.concat("The ", tag, " tag is not allowed here.");
                    // Continue parsing - maybe we can catch more errors
                    log.add(ParsingLogEntryType.ERROR, line, col, msg);
                }
                this.state = EParseState.ELEMENT_AWAITING_ATTR;
            }
        } else if (XmlChars.isWhitespace(cp)) {
            final String tag = this.accumulator.toString();
            this.accumulator.reset();
            try {
                createElement(tag);
                this.state = EParseState.AWAITING_ELEMENT;
            } catch (final TagNotAllowedException ex) {
                final String msg = SimpleBuilder.concat("The ", tag, " tag is not allowed here.");
                // Continue parsing - maybe we can catch more errors
                log.add(ParsingLogEntryType.ERROR, line, col, msg);
            }
            this.state = EParseState.ELEMENT_AWAITING_ATTR;
        } else {
            log.add(ParsingLogEntryType.ERROR, line, col, "Invalid character in element name.");
            // Continue parsing - maybe we can catch more errors
            this.accumulator.appendChar((char) cp);
        }

        return result;
    }

    /**
     * Processes a character in the ELEMENT_AWAITING_ATTR state.  Whitespace is ignored, and a name start character
     * moves to the ATTR_NAME state.  A '&gt;' character ends the element start tag and moves to the AWAITING_ELEMENT
     * state, and a '/' character moves to the CLOSING_EMPTY_ELEMENT state.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doElementAwaitingAttr(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == RIGHT_ANGLE_BRACKET) {
            // No more attributes - move inside the element that was just opened
            this.state = EParseState.AWAITING_ELEMENT;
        } else if (cp == SLASH) {
            this.state = EParseState.CLOSING_EMPTY_ELEMENT;
        } else if (XmlChars.isNameStartChar(cp)) {
            // Starting a new attribute
            this.accumulator.reset();
            this.accumulator.appendChar((char) cp);
            this.state = EParseState.ATTR_NAME;
        } else if (!XmlChars.isWhitespace(cp)) {
            log.add(ParsingLogEntryType.ERROR, line, col,
                    "Unexpected character, expecting attribute name, '>', or '/>'.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the CLOSING_EMPTY_ELEMENT state.  The only valid character here is '>'.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     */
    private ESuccessFailure doClosingEmptyElement(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == RIGHT_ANGLE_BRACKET) {
            result = closeElementAndStoreRoot(line, col, log);
            this.state = EParseState.AWAITING_ELEMENT;
        } else {
            log.add(ParsingLogEntryType.ERROR, line, col, "Unexpected character, expecting '>'.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the CDATA state.  If we encounter '&lt;' (that is not at the end of its line) we close
     * out the active CDATA block and move to the AWAITING_ELEMENT_OPEN_ANGLE state.  Otherwise, we keep accumulating
     * CDATA.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     */
    private void doCData(final int line, final int col, final int cp, final boolean lastInLine, final ParsingLog log) {

        if (cp == LEFT_ANGLE_BRACKET && !lastInLine) {
            final String content = this.cdata.toString();
            this.cdata.reset();
            final String unescaped = XmlEscaper.unescape(content);

            final ModelTreeNode dataNode = new ModelTreeNode();
            final TypedMap map = dataNode.map();
            map.put(XmlTreeWriter.VALUE, unescaped);

            final ModelTreeNode activeElement = this.elementStack.getLast();
            activeElement.addChild(dataNode);
            this.state = EParseState.AWAITING_ELEMENT_OPEN_ANGLE;
        } else {
            this.cdata.appendChar((char) cp);
        }
    }

    /**
     * Processes a character in the ATTR_NAME state.  If the character is a name character, we accumulate it and remain
     * in this state or if the character was at the end of its line, move to the AWAIT_ATTR_EQ state.  If the character
     * is '=', we move to the AWAIT_ATTR_VALUE state.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doAttrName(final int line, final int col, final int cp, final boolean lastInLine,
                                       final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == EQ) {
            this.pendingAttrName = this.accumulator.toString();
            this.accumulator.reset();
            this.state = EParseState.AWAIT_ATTR_VALUE;
        } else if (XmlChars.isWhitespace(cp)) {
            this.pendingAttrName = this.accumulator.toString();
            this.accumulator.reset();
            this.state = EParseState.AWAIT_ATTR_EQ;
        } else if (XmlChars.isNameChar(cp)) {
            this.accumulator.appendChar((char) cp);
            if (lastInLine) {
                this.pendingAttrName = this.accumulator.toString();
                this.accumulator.reset();
                this.state = EParseState.AWAIT_ATTR_EQ;
            }
        } else {
            log.add(ParsingLogEntryType.ERROR, line, col,
                    "Unexpected character in attribute name, expecting whitespace or '='.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the AWAIT_ATTR_EQ state.  If the character is '=', we move into the AWAIT_ATTR_VALUE
     * state.  If whitespace, we remain in this state.  All other characters are errors.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doAwaitAttrEq(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == EQ) {
            this.state = EParseState.AWAIT_ATTR_VALUE;
        } else if (!XmlChars.isWhitespace(cp)) {
            log.add(ParsingLogEntryType.ERROR, line, col, "Unexpected character, expecting whitespace or '='.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the AWAIT_ATTR_VALUE state.  If the character is a single or double quote, we store that
     * delimiter and move the ATTR_VALUE state.  If whitespace, we remain in this state.  All other characters are
     * errors.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doAwaitAttrValue(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == TICK || cp == QUOTE) {
            this.attrValueQuote = cp;
            this.accumulator.reset();
            this.state = EParseState.ATTR_VALUE;
        } else if (!XmlChars.isWhitespace(cp)) {
            log.add(ParsingLogEntryType.ERROR, line, col,
                    "Unexpected character, expecting attribute value in single or double quotes.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the ATTR_VALUE state.  If the character is the delimiter for the value, the value is
     * completed and stored in the element, and we move to the ELEMENT_AWAITING_ATTR state. If the character is
     * whitespace of any kind, we add a space character to the attribute value. Otherwise, we accumulate the character
     * to the attribute value.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     */
    private void doAttrValue(final int line, final int col, final int cp, final ParsingLog log) {

        if (cp == this.attrValueQuote) {
            final String value = this.accumulator.toString();
            this.accumulator.reset();
            addAttr(line, col, value, log);
            this.state = EParseState.ELEMENT_AWAITING_ATTR;
        } else if (cp == LEFT_ANGLE_BRACKET) {
            // This is bad XML, but we warn and accumulate it anyway
            log.add(ParsingLogEntryType.WARNING, line, col, "'<' should be escaped in attribute value.");
            this.accumulator.appendChar((char) LEFT_ANGLE_BRACKET);

        } else if (XmlChars.isWhitespace(cp)) {
            this.accumulator.appendChar(CoreConstants.SPC_CHAR);
        } else {
            this.accumulator.appendChar((char) cp);
        }
    }

    /**
     * Processes a character in the ELEMENT_END_TAG state.  The only valid character here is a Name start character for
     * the closing element name.
     *
     * @param line       the line index
     * @param col        the column index
     * @param cp         the code point
     * @param lastInLine true if the character is the last character in its line
     * @param log        a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doElementEndTag(final int line, final int col, final int cp, final boolean lastInLine,
                                            final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (this.accumulator.length() == 0) {
            // First character after '/' must be Name Start (which could be the whole name - the closing '>' is
            // allowed to be on a subsequent line)
            if (XmlChars.isNameStartChar(cp)) {
                this.accumulator.appendChar((char) cp);
                if (lastInLine) {
                    this.state = EParseState.ELEMENT_END_TAG_CLOSE;
                }
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col,
                        "Unexpected character, expecting name of element being closed.");
                result = ESuccessFailure.FAILURE;
            }
        } else if (cp == RIGHT_ANGLE_BRACKET) {
            result = processRightBracketAtEndOfEndTag(line, col, log);
        } else if (XmlChars.isNameChar(cp)) {
            this.accumulator.appendChar((char) cp);
            if (lastInLine) {
                this.state = EParseState.ELEMENT_END_TAG_CLOSE;
            }
        } else {
            log.add(ParsingLogEntryType.ERROR, line, col,
                    "Unexpected character in name of element being closed.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Processes a character in the ELEMENT_END_TAG_CLOSE state.  If character is whitespace, we remain in this state.
     * If '>', we process the closing tag (which generates an error if the tag name does not match the active element
     * tag name or if there is no active element).  All other characters are errors.
     *
     * @param line the line index
     * @param col  the column index
     * @param cp   the code point
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure doElementEndTagClose(final int line, final int col, final int cp, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        if (cp == RIGHT_ANGLE_BRACKET) {
            result = processRightBracketAtEndOfEndTag(line, col, log);
        } else if (!XmlChars.isWhitespace(cp)) {
            log.add(ParsingLogEntryType.ERROR, line, col, "Unexpected character, expecting whitespace or '>'.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Performs processing when a '&gt;' has been detected at the end of an "end tag" like "&lt;/p&gt;".
     *
     * @param line the line index
     * @param col  the column index
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure processRightBracketAtEndOfEndTag(final int line, final int col, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        final String tag = this.accumulator.toString();
        this.accumulator.reset();
        final ModelTreeNode activeElement = this.elementStack.getLast();
        final TypedMap map = activeElement.map();

        try {
            final String activeTag = map.get(XmlTreeWriter.TAG);

            if (tag.equals(activeTag)) {
                result = closeElementAndStoreRoot(line, col, log);
                this.state = EParseState.AWAITING_ELEMENT;
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col,
                        "Name in closing tag does not match name of active element (" + activeTag + ").");
                result = ESuccessFailure.FAILURE;
            }
        } catch (final StringParseException ex) {
            log.add(ParsingLogEntryType.ERROR, line, col, "Parser error retrieving tag of active element.");
            result = ESuccessFailure.FAILURE;
        }

        return result;
    }

    /**
     * Removes the active element from the element stack, and if that was the last element on the stack, stores the
     * removed element as the "root" element of the document.
     *
     * @param line the line index
     * @param col  the column index
     * @param log  a log to which to write messages
     * @return {@code FAILURE} to abort the parsing process (an error will have been added to the log);{@code SUCCESS}
     *         if parsing can continue
     */
    private ESuccessFailure closeElementAndStoreRoot(final int line, final int col, final ParsingLog log) {

        ESuccessFailure result = ESuccessFailure.SUCCESS;

        final ModelTreeNode finished = this.elementStack.removeLast();

        // If the element consists of only CDATA nodes, mark it as "inline"
        boolean cDataOnly = true;
        ModelTreeNode child = finished.getFirstChild();
        while (child != null) {
            final TypedMap childMap = child.map();
            if (childMap.containsKey(XmlTreeWriter.TAG)) {
                cDataOnly = false;
                break;
            }
            child = child.getNextSibling();
        }

        if (cDataOnly) {
            final TypedMap map = finished.map();
            map.put(XmlTreeWriter.INLINE, Boolean.TRUE);
        }

        if (this.elementStack.isEmpty()) {
            if (this.root == null) {
                this.root = finished;
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col, "Document may have only one root element.");
                result = ESuccessFailure.FAILURE;
            }
        }

        return result;
    }
}