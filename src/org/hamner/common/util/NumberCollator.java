/**
 * NumberCollator.java
 *
 * Compare two String values
 */

package org.hamner.common.util;

import java.text.Collator;
import java.text.CollationKey;

public class NumberCollator extends Collator {
    public NumberCollator() {
        super();
    }

    /**
     * Try to compare the two String values as Integer numbers,
     *     If failed then as Double values
     *         If failed again, then as two String values
     */
    public int compare(String source, String target) {
        try {
            Integer src1 = Integer.valueOf(source);
            Integer src2 = Integer.valueOf(target);

            if (src1 > src2) {
                return 1;
            } else if (src1 < src2) {
                return -1;
            }

            return 0;
        } catch (Exception e) {
            try {
                Double src1 = Double.valueOf(source);
                Double src2 = Double.valueOf(target);

                //return super.compare(src1, src2);
                if (src1 > src2) {
                    return 1;
                } else if (src1 < src2) {
                    return -1;
                }

                return 0;
            } catch (Exception e2) {
            }
        }

        return super.getInstance().compare(source, target);
    }

    /**
     * Override abstract method
     */
    public CollationKey getCollationKey(String source) {
        return super.getInstance().getCollationKey(source);
    }


    /**
     * Override abstract method
     */
    public int hashCode() {
        return super.getInstance().hashCode();
    }
}
