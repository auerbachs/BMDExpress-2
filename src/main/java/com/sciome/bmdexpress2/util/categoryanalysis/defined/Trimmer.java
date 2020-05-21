/*
 * Trimmer.java
 *
 * Create 7/25/2006
 * Author: Longlong Yang
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

public class Trimmer {
    public static String trimEnds(String st, String ends) {
        if (st.endsWith(ends)) {
            int idx = st.length() - ends.length();
            st = st.substring(0, idx);
        }

        return st;
    }
}
