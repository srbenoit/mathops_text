package dev.mathops.text.model;

import dev.mathops.commons.model.AttrKey;
import dev.mathops.commons.model.ModelTreeNode;

import java.util.Map;

/**
 * A simple factory that constructs a {@code ModelTreeNode} (not a subclass) each time it is called.
 */
public final class SimpleModelTreeNodeFactory implements IModelTreeNodeFactory {

    /**
     * Constructs a new {@code SimpleModelTreeNodeFactory}.
     */
    public SimpleModelTreeNodeFactory() {

        // No action
    }

    /**
     * Constructs a new {@code ModelTreeNode} based on an XML tag and a parent node.  This method does not need to set
     * the parent of the new node to {@code parent} or add the new node to the parent's list of child nodes, nor does it
     * need to store the XML tag as a DATA value.  It simply performs construction of the new tree node.
     *
     * @param xmlTag        the XML tag
     * @param parent        the parent node (which should have all attributes set); null to create the root node
     * @param attributeKeys a map from key name to attribute key to which this method will add all supported attribute
     *                      keys for the new node (if no keys are added to this map, all attributes will be allowed and
     *                      will be stored as String type)
     * @return the newly constructed node (typically a subclass of {@code ModelTreeNode})
     * @throws TagNotAllowedException if the XML tag is not allowed within the provided parent
     */
    public ModelTreeNode construct(final String xmlTag, final ModelTreeNode parent,
                                   final Map<String, AttrKey<?>> attributeKeys) throws TagNotAllowedException {

        return new ModelTreeNode();
    }
}
