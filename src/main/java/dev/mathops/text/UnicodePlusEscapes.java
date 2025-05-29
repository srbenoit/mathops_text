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

package dev.mathops.text;

import dev.mathops.commons.file.FileLoader;
import dev.mathops.commons.log.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A singleton utility class that loads the Unicode+ escapes database and supports lookups of escape sequences or
 * matching of escape sequences to determine USV values.
 */
public class UnicodePlusEscapes {

    /** The single instance. */
    public static final UnicodePlusEscapes INSTANCE = new UnicodePlusEscapes();

    /** The start of a comment line or delimiter between escape sequence and USV. */
    private static final int SPC = (int) ' ';

    /** Escape codes sorted by string. */
    private final List<Escape> escapesByString;

    /** Escape codes sorted by code. */
    private final List<Escape> escapesByUsv;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private UnicodePlusEscapes() {

        final String[] lines = FileLoader.loadFileAsLines(UnicodePlusEscapes.class, "UnicodePlusEscapes.txt", true);

        if (lines == null) {
            Log.warning(Res.get(Res.UNI_CANT_LOAD_ESC_DB));
            this.escapesByString = new ArrayList<>(0);
            this.escapesByUsv = new ArrayList<>(0);
        } else {
            this.escapesByString = new ArrayList<>(lines.length);
            this.escapesByUsv = new ArrayList<>(lines.length);

            final Set<Integer> usvs = new HashSet<>(lines.length);

            for (final String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                final int start = (int) line.charAt(0);

                if (start == SPC) {
                    continue;
                }

                final int spc = line.indexOf(SPC);
                if (spc == -1) {
                    final String msg = Res.fmt(Res.UNI_BAD_ESC_IN_DB, line);
                    Log.warning(msg);
                    continue;
                }

                final String usvStr = line.substring(spc + 1).trim();
                try {
                    final int usv = Integer.parseInt(usvStr);

                    final Escape esc = new Escape(line.substring(spc), usv);
                    this.escapesByString.add(esc);

                    // We track USVs we have found so far, and only add an escape to the USV-based list if this is
                    // the first instance - that allows multiple symbols to be mapped to one USV, but one USV will
                    // map to only one "canonical" escape (the first one found in the database file)

                    final Integer key = Integer.valueOf(usv);
                    if (!usvs.contains(key)) {
                        this.escapesByUsv.add(esc);
                        usvs.add(key);
                    }
                } catch (final NumberFormatException ex) {
                    final String msg = Res.fmt(Res.UNI_BAD_ESC_IN_DB, line);
                    Log.warning(msg);
                }
            }

            this.escapesByString.sort(null);
            this.escapesByUsv.sort(Comparator.comparingInt(o -> o.usv));

            final int size = this.escapesByString.size();
            final String sizeStr = Integer.toString(size);
            final String msg = Res.fmt(Res.UNI_LOADED_ESC_DB, sizeStr);
            Log.warning(msg);
        }
    }

    /**
     * Retrieves the escape for a particular USV code.
     *
     * @param usv the USV code
     * @return the escape; null if there is no escape for the USV
     */
    public String getEscapeFor(int usv) {

        String result = null;

        int low = 0;
        int high = this.escapesByUsv.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            final Escape element = this.escapesByUsv.get(mid);

            if (element.usv == usv) {
                result = element.sequence;
                break;
            }

            if (element.usv < usv) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    /**
     * Retrieves the USV for a particular escape sequence code.
     *
     * @param escapeSequence the escape sequence
     * @return the USV code; -1 if there is no escape for the USV
     */
    public int unescape(String escapeSequence) {

        int result = -1;

        int low = 0;
        int high = this.escapesByString.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            final Escape element = this.escapesByString.get(mid);

            final int comparison = element.sequence.compareTo(escapeSequence);

            if (comparison == 0) {
                result = element.usv;
                break;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    /**
     * A data object that represents a single escape sequence, sortable so we can sort the list and do a binary search
     * when looking up escapes.
     */
    private static class Escape implements Comparable<Escape> {

        /** The escape sequence, without the leading '\'. */
        public final String sequence;

        /** The USV to which that sequence maps. */
        public final int usv;

        /**
         * Constructs a new {@code Escape}.
         *
         * @param theSequence the escape sequence, without the leading '\'
         * @param theUsv      the USV to which that sequence maps
         */
        Escape(final String theSequence, final int theUsv) {
            this.sequence = theSequence;
            this.usv = theUsv;
        }

        /**
         * Compares two escapes for order.  Comparison is based on the escape sequence.
         *
         * @param o the object to be compared
         * @return the result of this.sequence.compareTo(o.sequence)
         */
        @Override
        public int compareTo(final Escape o) {

            return this.sequence.compareTo(o.sequence);
        }
    }
}
