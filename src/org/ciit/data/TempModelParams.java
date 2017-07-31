/**
 * TempModelParams.java
 *
 * Created 10/15/2008
 *
 * Read temporary model parameters written in the temp model-based file
 */

package org.ciit.data;


import org.ciit.util.NumberManager;

import java.util.Hashtable;

public class TempModelParams {
    private String sourceName;
    Hashtable<String, double[]> parameterHash;

    public TempModelParams() {
    }

    public TempModelParams(int max) {
        parameterHash = new Hashtable<String, double[]>(max);
    }

    public void setSourceName(String srcName){
        sourceName = srcName;
    }

    public void addParameters(String probe, double[] parameters) {
        parameterHash.put(probe, parameters);
    }

    public String getSourceName() {
        return sourceName;
    }

    public double[] getParametersOf(String probe) {
        return parameterHash.get(probe);
    }
}
