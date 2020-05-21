/**
 * BMDMatrixData.java
 *
 * Created 7/21/2006
 * Last Modified 11/21/2006
 * Author: Longlong Yang
 */

package org.ciit.data;

import org.ciit.data.ModelParameters;
import java.util.regex.*;

public class BMDMatrixData extends WSMatrixData {
    private ModelParameters modelParams;

    public static final String MAXIMUMDOSE = "Maximum Dose";

    public BMDMatrixData() {
        this("New", null, null);
    }

    public BMDMatrixData(String name) {
        this(name, null, null);
    }

    public BMDMatrixData(String name, String[] colNames, Object[][] dt) {
        super(name, colNames, dt);
    }

    public void setModelParameters(ModelParameters m){
        modelParams = m;
    }

    public ModelParameters getMP(){
        return modelParams;
    }

    public double readMaxDose() {
        double maxDose = -1;
        String value = readKeyValue(this.MAXIMUMDOSE);

        if (value == null) {
            value = readKeyValue("Highest Dose");
        }

        if (value != null) {
            try {
                maxDose = Double.parseDouble(value);
            } catch (Exception e) {
                //ExceptionDialog.showError(parent, "Read Maximum Dose", e);
            }
        }

        return maxDose;
    }

    public static String polynomialDegree(String model) {
        String degree = null;
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(model);

        if (matcher.find()) {
            degree = matcher.group(1);
        }

        return degree;
    }
}
