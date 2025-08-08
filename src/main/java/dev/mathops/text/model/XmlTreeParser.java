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
import dev.mathops.text.builder.HtmlBuilder;
import dev.mathops.text.parser.LineOrientedParserInput;
import dev.mathops.text.parser.ParsingException;
import dev.mathops.text.parser.ParsingLog;
import dev.mathops.text.parser.ParsingLogEntryType;
import dev.mathops.text.parser.xml.CData;
import dev.mathops.text.parser.xml.EmptyElement;
import dev.mathops.text.parser.xml.IElement;
import dev.mathops.text.parser.xml.INode;
import dev.mathops.text.parser.xml.NonemptyElement;
import dev.mathops.text.parser.xml.XmlChars;
import dev.mathops.text.parser.xml.XmlContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser that builds a model tree from an XML file.  This class has no information about the expected data types of
 * attributes, so all attributes are loaded with String type.  Consumers of the model can install typed versions of
 * attribute keys and convert the String values as needed (in the spirit of the CSS cascade, the string values would be
 * cascaded, then converted to typed values when needed).
 *
 * <p>
 * The parser supports a limited subset of the XML 1.0 grammar:
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
 *
 * <p>
 * The parser is permissive and does minimal on validation, since it will tend to be used in settings where the XML data
 * is fairly well controlled. The <code>XMLDecl</code> is not validated at all - the parser simply scans for the closing
 * "?&gt;" and then moves on. This allows XML files with such a declaration to be parsed without error, but we do
 * nothing with that data.  The parser will also allow multiple XMLDecl structures - they are all ignored.  We also
 * allow the "--" sequence to occur within comments.
 */
public enum XmlTreeParser {
    ;

    /**
     * Attempts to parse XML content into a model tree node.
     *
     * @param input            the input to parse
     * @param ignoreWhitespace {@code true} to ignore inter-element whitespace; {@code false} to parse inter-element
     *                         whitespace as CDATA objects
     * @param preserveComments {@code true} to preserve comments (comments found are associated with the element found
     *                         after the comment and are stored in that element object; comments after the last element
     *                         in the document will be discarded); {@code false} to ignore comments;
     * @param log              a log to which to add parsing errors, warnings, and messages
     * @return the root element if parsing was successful; {@code null} if not
     */
    static ModelTreeNode parse(final LineOrientedParserInput input, final boolean ignoreWhitespace,
                               final boolean preserveComments, final ParsingLog log) {

        final int numLines = input.getNumLines();
        final Parser state = new Parser();

        outer:
        for (int l = 0; l < numLines; ++l) {
            final String line = input.getLine(l);

            final int last = line.length() - 1;
            for (int c = 0; c <= last; ++c) {
                final int ch = (int) line.charAt(c);
                if (state.processCharacter(l, c, ch, c == last, ignoreWhitespace, preserveComments, log)) {
                    // Parser cannot recover from error - abort parsing
                    break outer;
                }
            }
        }

        return state.getRootNode();
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

    /**
     * States of the parser.
     */
    enum EParseState {
        /** At the start of the prolog; */
        PROLOG_START,
        /** In prolog, found a '&lt;' character. */
        PROLOG_OPEN_ANGLE,
        /** Within the XMLDecl, scanning for the closing "?&gt;". */
        XML_DECL,
        /** Found a '?' within an XMLDecl, testing for '&gt;'. */
        XML_DECL_CLOSE,
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
    }

    /** A parser with state. */
    static class Parser {

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

        /** The current state. */
        private EParseState state;

        /** An accumulator for text. */
        private final HtmlBuilder accumulator;

        /** Comments pending attachment to the next element found. */
        private final List<String> pendingComments;

        /** The root node of the successfully parsed model tree. */
        private ModelTreeNode root = null;

        /**
         * Constructs a new {@code ParseState}.
         */
        Parser() {

            this.state = EParseState.PROLOG_START;
            this.accumulator = new HtmlBuilder(30);
            this.pendingComments = new ArrayList<>(3);
        }

        /**
         * Processes a single character of input.
         *
         * @param line             the line number
         * @param ch               the character
         * @param lastInLine       true if the character is the last in a line of input text
         * @param ignoreWhitespace {@code true} to ignore inter-element whitespace; {@code false} to parse inter-element
         *                         whitespace as CDATA objects
         * @param preserveComments {@code true} to preserve comments (comments found are associated with the element
         *                         found after the comment and are stored in that element object; comments after the
         *                         last element in the document will be discarded); {@code false} to ignore comments;
         * @param log              a log to which to add parsing errors, warnings, and messages
         * @return {@code true} to abort the parsing process (an error will have been added to the log);{@code false} if
         *         parsing can continue
         */
        boolean processCharacter(final int line, final int col, final int ch, final boolean lastInLine,
                                 final boolean ignoreWhitespace, final boolean preserveComments, final ParsingLog log) {

            boolean abort = false;

            switch (this.state) {
                case PROLOG_START -> abort = doPrologStart(line, col, ch, lastInLine, log);
                case PROLOG_OPEN_ANGLE -> abort = doPrologOpenAngle(line, col, ch, lastInLine, log);
                case XML_DECL -> doXmlDecl(ch, lastInLine);
                case XML_DECL_CLOSE -> doXmlDeclClose(ch);
                case START_COMMENT -> abort = doStartComment(line, col, ch, lastInLine, log);
                case COMMENT_DASH -> abort = doCommentDash(line, col, ch, log);
                case COMMENT -> doComment(ch, lastInLine);
                case COMMENT_END1 -> doCommentEnd1(ch, lastInLine);
                case COMMENT_END2 -> abort = doCommentEnd2(line, col, ch, log);
                case ELEMENT_NAME -> {
                }
            }

            return abort;
        }

        /**
         * Processes a character in the PROLOG_START state.  Valid characters are whitespace (which does not change
         * state), or '<' that is not the last character in a line, which could be the start of an XMLDecl, a comment,
         * or an element, and which changes state to PROLOG_OPEN_ANGLE.
         *
         * @param line       the line index
         * @param col        the column index
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         * @param log        a log to which to write messages
         */
        private boolean doPrologStart(final int line, final int col, final int ch, final boolean lastInLine,
                                      final ParsingLog log) {

            boolean abort = false;

            if (ch == LEFT_ANGLE_BRACKET) {
                if (lastInLine) {
                    log.add(ParsingLogEntryType.ERROR, line, col,
                            "'<' must be followed by '?', '!', or an element name.");
                    abort = true;
                } else {
                    this.state = EParseState.PROLOG_OPEN_ANGLE;
                }
            } else if (!XmlChars.isWhitespace(ch)) {
                log.add(ParsingLogEntryType.ERROR, line, col, "Unexpected character, expecting whitespace or '<'.");
                abort = true;
            }

            return abort;
        }

        /**
         * Processes a character in the PROLOG_OPEN_ANGLE state.  Valid characters are '?' (starts an XMLDecl, and
         * changes state to XML_DECL), '!' (starts a comment, and changes state to PROLOG_START_COMMENT) or a Name Start
         * character (which starts an element, and changes state to ELEMENT_NAME).
         *
         * @param line       the line index
         * @param col        the column index
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         * @param log        a log to which to write messages
         */
        private boolean doPrologOpenAngle(final int line, final int col, final int ch, final boolean lastInLine,
                                          final ParsingLog log) {

            boolean abort = false;

            if (ch == QUESTION_MARK) {
                // NOTE: This can fall at the end of a line, and we still scan for the closure.
                this.state = EParseState.XML_DECL;
            } else if (ch == BANG) {
                if (lastInLine) {
                    log.add(ParsingLogEntryType.ERROR, line, col, "'<!' must be followed by '--', to start a comment.");
                    abort = true;
                } else {
                    this.state = EParseState.START_COMMENT;
                }
            } else if (XmlChars.isNameStartChar(ch)) {
                this.accumulator.appendChar((char) ch);
                if (lastInLine) {
                    // The linefeed counts as whitespace to close the element name
                    // TODO: The state after an element name is accumulated, looking for attributes or '>'
                } else {
                    this.state = EParseState.ELEMENT_NAME;
                }
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col,
                        "Unexpected character, expecting '?', '!', or an element name.");
                abort = true;
            }

            return abort;
        }

        /**
         * Processes a character in the XML_DECL state.  Any character is valid here, but '?' (not at the end of a line)
         * moves to the XML_DECL_CLOSE state.
         *
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         */
        private void doXmlDecl(final int ch, final boolean lastInLine) {

            if (ch == QUESTION_MARK && !lastInLine) {
                this.state = EParseState.XML_DECL_CLOSE;
            }
        }

        /**
         * Processes a character in the XML_DECL_CLOSE state.  If the character is '>', the XMLDecl is closed, and we
         * return to the PROLOG_START state; otherwise, we return to the XML_DECL state.
         *
         * @param ch the character
         */
        private void doXmlDeclClose(final int ch) {

            // NOTE: The '?' that got us to this state was not allowed to be the last character in its line, so we do
            // not need to test whether we are looking at the first character in a line.
            this.state = ch == RIGHT_ANGLE_BRACKET ? EParseState.PROLOG_START : EParseState.XML_DECL;
        }

        /**
         * Processes a character in the START_COMMENT state.  The only valid character is a '-', which moves to the
         * COMMENT_DASH state.
         *
         * @param line       the line index
         * @param col        the column index
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         * @param log        a log to which to write messages
         */
        private boolean doStartComment(final int line, final int col, final int ch, final boolean lastInLine,
                                       final ParsingLog log) {

            boolean abort = false;

            // NOTE: The '!' that got us to this state was not allowed to be the last character in its line, so we do
            // not need to test whether we are looking at the first character in a line.
            if (ch == DASH) {
                if (lastInLine) {
                    // Found "<!-" at the end of a line.
                    log.add(ParsingLogEntryType.ERROR, line, col - 2, line, col,
                            "Start of XML comment is missing second '-' character.");
                    abort = true;
                } else {
                    this.state = EParseState.COMMENT_DASH;
                }
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col,
                        "Unexpected character, expecting '-' to begin a comment.");
                abort = true;
            }

            return abort;
        }

        /**
         * Processes a character in the COMMENT_DASH state.  The only valid character is a '-', which moves to the
         * COMMENT state.
         *
         * @param line the line index
         * @param col  the column index
         * @param ch   the character
         * @param log  a log to which to write messages
         */
        private boolean doCommentDash(final int line, final int col, final int ch, final ParsingLog log) {

            boolean abort = false;

            // NOTE: The '-' that got us to this state was not allowed to be the last character in its line, so we do
            // not need to test whether we are looking at the first character in a line.

            if (ch == DASH) {
                this.state = EParseState.COMMENT;
                this.accumulator.reset();
            } else {
                log.add(ParsingLogEntryType.ERROR, line, col,
                        "Unexpected character, expecting '-' to begin a comment.");
                abort = true;
            }

            return abort;
        }

        /**
         * Processes a character in the COMMENT state.  Comment characters are accumulated until we find "-->".  When we
         * find the first '-' (not at the end of its line), we move to the COMMEND_END1 state.
         *
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         */
        private void doComment(final int ch, final boolean lastInLine) {

            if (ch == DASH && !lastInLine) {
                this.state = EParseState.COMMENT_END1;
                this.accumulator.appendChar((char) DASH);
            }
        }

        /**
         * Processes a character in the COMMENT_END1 state, in which we have found one '-' within a comment and are
         * checking for the "-->" closure. If the character is '-' (not at the end of its line), we move to the
         * COMMEND_END2 state.  Otherwise, we move back to the COMMENT state.
         *
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         */
        private void doCommentEnd1(final int ch, final boolean lastInLine) {

            this.accumulator.appendChar((char) ch);
            this.state = ch == DASH && !lastInLine ? EParseState.COMMENT_END2 : EParseState.COMMENT;
        }

        /**
         * Processes a character in the COMMENT_END2 state.  If the character is '>', the comment is complete, and we
         * cache it so it can be associated with the next element found.
         *
         * @param ch         the character
         * @param lastInLine true if the character is the last character in its line
         */
        private void doCommentEnd2(final int ch, final boolean lastInLine) {

            if (ch == RIGHT_ANGLE_BRACKET) {
                final String commentTextWithDashes = this.accumulator.toString();
                final int len = commentTextWithDashes.length();
                final String commentText = commentTextWithDashes.substring(0, len - 2).trim();
                this.pendingComments.add(commentText);

                // FIXME: DO we return to the "PROLOG" state or some other state?
                this.state = EParseState.PROLOG_START;

            } else {
                this.state = EParseState.COMMENT;
            }
        }

        /**
         * Gets the root node of the successfully parsed document.
         *
         * @return the root node
         */
        ModelTreeNode getRootNode() {

            return this.root;
        }
    }
}