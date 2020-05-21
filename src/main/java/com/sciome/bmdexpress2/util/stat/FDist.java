
package com.sciome.bmdexpress2.util.stat;

/** Compute approximations to F distribution probabilities.

<p>Original programmer: Gary Perlman, Wang Institute, Tyngsboro, MA
01879

<p>From the |STAT collection of statistical programs (no
Copyright).

<p>Java port by H. Irtel.

@version 0.1.0

*/

public class FDist {

    /** Inverse of Pi */
    private static final double I_PI = 1.0/Math.PI;

    /** Accuracy of critf approximation */
    private static final double F_EPSILON = 0.000001;

    /* maximum F ratio */
    private static final double F_MAX = 9999.0;

    /** Compute probability of F ratio.  <p>Algorithm: Adapted from
    Collected Algorithms of the CACM, Algorithm 322 by Egon
    Dorrer. */
    public static double probabilityOf (double F, int df1, int df2) {
        int i, j;
        int a, b;
        double  w, y, z, d, p;

        if (F < F_EPSILON || df1 < 1 || df2 < 1) {
            return (1.0);
        }

        a = (df1%2 != 0) ? 1 : 2;
        b = (df2%2 != 0) ? 1 : 2;
        w = (F * df1) / df2;
        z = 1.0 / (1.0 + w);

        if (a == 1) {
            if (b == 1) {
                p = Math.sqrt (w);
                y = I_PI;
                d = y * z / p;
                p = 2.0 * y * Math.atan (p);
            } else {
                p = Math.sqrt (w * z);
                d = 0.5 * p * z / w;
            }
        } else if (b == 1) {
            p = Math.sqrt (z);
            d = 0.5 * z * p;
            p = 1.0 - p;
        } else {
            d = z * z;
            p = w * z;
        }

        y = 2.0 * w / z;

        for (j = b + 2; j <= df2; j += 2) {
            d *= (1.0 + a / (j - 2.0)) * z;
            p = (a == 1 ? p + d * y / (j - 1.0) : (p + w) * z);
        }

        y = w * z;
        z = 2.0 / z;
        b = df2 - 2;

        for (i = a + 2; i <= df1; i += 2) {
            j = i + b;
            d *= y * j / (i - 2.0);
            p -= z * d / j;
        }

        /* correction for approximation errors suggested in certification */
        if (p < 0.0) {
            p = 0.0;
        } else if (p > 1.0) {
            p = 1.0;
        }

        return (1.0-p);
    }

    /** Compute critical F value to produce given probability.
    <p>Algorithm: Begin with upper and lower limits for F values
    (maxf and minf) set to extremes.  Choose an f value (fval)
    between the extremes.  Compute the probability of the f value.
    Set minf or maxf, based on whether the probability is less
    than or greater than the desired p.  Continue adjusting the
    extremes until they are within F_EPSILON of each other. */
    public static double criticalF(double p, int df1, int df2) {
    double  fval;
    double  maxf = F_MAX;     /* maximum F ratio */
    double  minf = 0.0;       /* minimum F ratio */

    if (p <= 0.0 || p >= 1.0)
        return (0.0);

    /* the smaller the p, the larger the F */
    fval = 1.0 / p;

    while (Math.abs (maxf - minf) > F_EPSILON) {
        if (probabilityOf(fval, df1, df2) < p)
        /* F too large */
        maxf = fval;
        else
        /* F too small */
        minf = fval;
        fval = (maxf + minf) * 0.5;
    }

    return (fval);
    }
}
