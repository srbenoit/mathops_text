package dev.mathops.text.model;

import dev.mathops.commons.model.AttrKey;

import java.util.HashMap;
import java.util.Map;

/**
 * A container for the set of allowed attributes for an element.
 */
public final class AllowedAttributes {

    /** A map from attribute name to the attribute key. */
    private final Map<String, AttrKey<?>> attributeKeys;

    /**
     * Constructs a new {@code AllowedAttributes}.
     */
    public AllowedAttributes() {

        this.attributeKeys = new HashMap<>(10);
    }

    /**
     * Adds attribute keys.
     *
     * @param keys the attribute keys
     */
    public void add(final AttrKey<?>... keys) {

        if (keys != null) {
            for (final AttrKey<?> key : keys) {
                final String name = key.getName();
                this.attributeKeys.put(name, key);
            }
        }
    }

    /**
     * Tests whether the set is empty.
     *
     * @return true if the set is empty
     */
    public boolean isEmpty() {

        return this.attributeKeys.isEmpty();
    }

    /**
     * Gets the attribute key with a specified name.
     *
     * @param name the attribute name
     * @return the key
     */
    public AttrKey<?> get(final String name) {

        return this.attributeKeys.get(name);
    }
}
