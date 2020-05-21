/**
 * ModelsConstants.java
 *
 * 3/23/2010, Longlong Yang
 *
 * Define constants objects used for Affymetrix Otholog project
 */

package org.ciit.bmds;

/**
 * Fields, values and parameters useful for BMDS analysis
 */
public class ModelsConstants {
    public static final String tool = "Benchmark Dose Analyses";

    public static final String DATASRC = "Data Source";

    public static final String WORKSRC = "Work Source";

    public static final String FILTERCTRL = "Filter Out Control Genes (i.e., AFFX-)";

    public static final String FITMODELS = "Models to fit";

    public static final String POLYTEST = "Best Poly Model Test";

    public static final String PCUTOFF = "Nested Chi Square p-value cutoff";

    public static final String FLAGHILL = "Flag Hill Model with 'k' Parameter <";

    public static final String HILLOPTION = "Best Model Selection with Flagged Hill Model";

    public static final String HIGHESTDOSE = "Highest Dose";

    public static final String LOWESTPDOSE = "Lowest Positive Dose";

    public static final String MINBESTBMD = "Minimum BMD of Best Models (excluding flagged Hill models)";

    public static final String MINBESTBMDL = "Minimum BMDL of Best Models (excluding flagged Hill models)";

    public static final String HILLBMD = "Modify BMD of flagged Hill as Best Models with a Fraction of Minimum BMD";

    public static final String FLAGBMD = "BMD asigned to flagged Hill as Best Models";

    public static final String FLAGBMDL = "BMDL asigned to flagged Hill as Best Models";

    public static final String RESELECTBEST = "Re-select Best Models";

    public static final String[] MODELPARAMS = {
            "Maximum Iterations",
            "Confidence Level",
            "BMR Factor",
            "Restrict Power"};

    public static final String[] POWERRESTRIC = {
            "No Restriction",
            ">= 1"};//Restrict Power

    public static final String[] BASICFIELDS = {
        "BMD",
        "BMDL",
        "Fit pValue",
        "Fit Log(likelihood)",
        "AIC"};

    public static final String[] EXPPARAMS = {}; // Exponential parameter names

    public static final String[] HILLPARAMS = {
        "intercept",
        "v-parameter",
        "n-parameter",
        "k-parameter"};

    public static final String[] POWERPARAMS = {"control", "slop", "power"};

    public static final String POLYPARAM = "beta";

    /**
     * @param degree is the degree of polynomial model such as 1, 2, 3...
     * @return parameter names for polynomial model give its degree, e.g.,
     *     if degree = 1 as a linear model, the names are beta_0 and beta_1
     */
    public static String[] polyParameterNames(int degree) {
        String[] params = new String[degree + 1];

        for (int i = 0; i <= degree; i++) {
            params[i] = POLYPARAM + "_" + i;
        }

        return params;
    }
}