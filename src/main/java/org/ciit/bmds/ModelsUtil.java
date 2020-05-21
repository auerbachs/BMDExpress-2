/**
 * ModelsUtil.java
 *
 * 3/23/2010, Longlong Yang
 *
 * Define constants objects used for Affymetrix Otholog project
 */

package org.ciit.bmds;

import java.util.Vector;
import java.util.regex.*;


public class ModelsUtil {

    public ModelsUtil() {
    }

    /**
     * @param degree is the degree of polynomial model such as 1, 2, 3...
     * @return parameter names for polynomial model give its degree, e.g.,
     *     if degree = 1 as a linear model, the names are beta_0 and beta_1
     */
    public static String[] polyParameterNames(int degree) {
        String[] params = new String[degree + 1];

        for (int i = 0; i <= degree; i++) {
            params[i] = ModelsConstants.POLYPARAM + "_" + i;
        }

        return params;
    }

    /**
     * Create colomn names for parameter file given a model
     *
     * @param model the name of a model
     */
    public static String[] createModelColumns(String model) {
        Vector<String> colNames = new Vector<String>();
        colNames.add("ID");
        int len = ModelsConstants.BASICFIELDS.length;

        for (int i = 0; i < len; i++) {
            colNames.add(ModelsConstants.BASICFIELDS[i]);
        }

        //if (model.equals(singleModels[0])) { // Exponential
        if (model.startsWith("Exponential")) { //
            ; // not implemented
        } else if (model.startsWith("Hill")) {//equals(singleModels[1])) {
            for (int i = 0; i < ModelsConstants.HILLPARAMS.length; i++) {
                colNames.add(ModelsConstants.HILLPARAMS[i]);
            }
        } else if (model.startsWith("Power")) {//(singleModels[2])) {
            for (int i = 0; i < ModelsConstants.POWERPARAMS.length; i++) {
                colNames.add(ModelsConstants.POWERPARAMS[i]);
            }
        } else { // Linear and Polynomial
            String degree = polynomialDegree(model);

            if (degree != null) {
                int d = Integer.parseInt(degree);

                for (int i = 0; i <= d; i++) {
                    colNames.add(ModelsConstants.POLYPARAM + "_" + i);
                }
            }
        }

        //colNames.add(outColNames[len]);// directory
        String[] names = new String[colNames.size()];

        return colNames.toArray(names);
    }

    public static  String polynomialDegree(String model) {
        String degree = null;
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(model);

        if (matcher.find()) {
            degree = matcher.group(1);
        }

        return degree;
    }
}