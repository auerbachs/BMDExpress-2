
/**
 * ArrayMatrixData.java
 *
 * Created 9/18/2008
 * Last Modified 9/18/2008
 * Author: Longlong Yang
 *
 * Add array/chip name as defined when import expression data
 */

package org.ciit.data;

import org.ciit.util.MatrixData;
import org.ciit.annot.ArrayAnnotation;

import java.util.HashSet;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.SwingWorker;

public class ArrayMatrixData extends WSMatrixData {
    private ArrayAnnotation arrayAnnotation;
    private int maxDecimal = -1; //default means no matching
    private int[] sortedIndices;
    private int[][] groupDoseIndices = null;  // arrange index per dose in 2D array
    private double minDose, maxDose, minResponse, maxResponse;
    private double[] doses, uniDoses; // in ascending order
    private double[][] responses;
    private String chip = null;// chipId = null, species = null, provider = null;
    private String[] probes, samples;
    private HashSet<String> ctrlProbes; // for control genes
    public Hashtable<String, Integer> probesHash;
    private boolean reOrdered = false;

    private final String CHIP = "Chip";
    private final String CHIPID = "Chip ID";
    private final String SPECIES = "Species";
    private final String PROVIDER = "Provider";

    public ArrayMatrixData() {
        this("New", null, null);
    }

    public ArrayMatrixData(String name) {
        this(name, null, null);
    }

    public ArrayMatrixData(String name, String[] colNames, Object[][] dt) {
        super(name, colNames, dt);
    }

    public void setChip(String st){
        chip = st;
        addKeyValue(CHIP, chip);
    }

    public void setMaxDecimal(int n) {
        maxDecimal = n;
    }


    /**
     *  Convert Object[][] to dosese and responses of double.
     *  Sort columns if original data is not ordered based on doses
     *  Used by all tools to get dosese and responses data
     *
     *  Run the process in separate thread
     */
    public boolean checkDosesResponses() {
        sortDoses(data[0]);
        //readResponses();
        readProbes();

        return reOrdered;
    }

    /**
     *  Read doses and sort in ascending order and track original indices
     *
     *  The first column contains probe IDs. The first row are doses.
     */
    private void sortDoses(Object[] dosesRow) {
        minDose = maxDose = -1;
        doses = new double[cols - 1];
        sortedIndices = new int[cols - 1];
        samples = new String[cols - 1];
        Vector<Double> vectDoses = new Vector<Double>();
        int cur = 0;

        for (int i = 1; i < cols; i++) {
            Double value = new Double(dosesRow[i].toString());
            double dose = value.doubleValue();

            // insertion sorting loop
            for (int j = cur; j >= 0; j--) {
                if (j == 0 || dose >= doses[j - 1]) {
                    doses[j] = dose;
                    sortedIndices[j] = i;
                    samples[j] = columnNames[i];
                    break;
                } else {
                    doses[j] = doses[j - 1];
                    sortedIndices[j] = sortedIndices[j - 1];
                    samples[j] = samples[j - 1];
                    reOrdered = true;
                }
            }

            if (!vectDoses.contains(value)) {
                for (int v = vectDoses.size(); v >= 0; v--) {
                    if (v == 0 || value.compareTo(vectDoses.get(v - 1)) > 0) {
                        vectDoses.insertElementAt(value, v);
                        break;
                    }
                }
            }

            cur++;
        }

        minDose = doses[0];
        maxDose = doses[doses.length - 1];
        uniDoses = new double[vectDoses.size()];

        for (int i = 0; i < vectDoses.size(); i++) {
            uniDoses[i] = vectDoses.get(i).doubleValue();
            //System.out.println(i + " unidose = " + uniDoses[i]);
        }

        /*for testing
        for (int i = 0; i < doses.length; i++) { // for testing
            System.out.println(doses[i] + ": " + sortedIndices[i] + ", " + samples[i]);
        }*/
    }

    /**
     *  Read probes or identifiers as the first column
     *  If the order of doses changed re-order colum names and Object data
     */
    public void readProbes() {
        // excluding doses row and probes columns
        //long t = System.currentTimeMillis();
        probes = new String[rows - 1];
        ctrlProbes = new HashSet<String>();
        minResponse = maxResponse = -1;
        probesHash = new Hashtable<String, Integer>(rows + 1, 1);
        //System.out.println("Re-order doses: " + reOrdered);

        for (int i = 1; i < rows; i++) {
            int r = i - 1;
            probes[r] = (String)data[i][0];
            probesHash.put(probes[r], Integer.valueOf(r));
            Double[] rowDt = new Double[sortedIndices.length];

            if (isControlGene(probes[r])) {
                ctrlProbes.add(probes[r]);
            }

            for (int j = 0; j < sortedIndices.length; j++) {
                rowDt[j] = new Double(data[i][sortedIndices[j]].toString());
                double value = rowDt[j].doubleValue();


                if (r == 0 && j == 0) {
                    minResponse = value;
                    maxResponse = value;
                } else {
                    if (minResponse > value) {
                        minResponse = value;
                    }

                    if (maxResponse < value) {
                        maxResponse = value;
                    }
                }
            }

            if (reOrdered) {
                for (int j = 0; j < rowDt.length; j++) {
                    data[i][j + 1] = rowDt[j];
                }
            }
        }

        if (reOrdered) { // doses are reorder so re-arrange columns
            for (int i = 0; i < samples.length; i++) {
                columnNames[i + 1] = samples[i];
                data[0][i + 1] = Double.valueOf(doses[i]);
            }
        }

        //t = System.currentTimeMillis() - t;
        //System.out.println("Times spend on readResponses():" + t);
    }

    /**
     *  Read responses according to the order of doses
     *  If order changed re-order colum names and Object data
    private void readResponses() {
        long t = System.currentTimeMillis();
        // excluding doses row and probes columns
        responses = new double[rows - 1][ cols - 1];
        probes = new String[rows - 1];
        ctrlProbes = new HashSet<String>();
        minResponse = maxResponse = -1;
        //System.out.println("Re-order doses: " + reOrdered);

        for (int i = 1; i < rows; i++) {
            int r = i - 1;
            probes[r] = (String)data[i][0];
            Double[] rowDt = new Double[sortedIndices.length];

            if (isControlGene(probes[r])) {
                ctrlProbes.add(probes[r]);
            }

            for (int j = 0; j < sortedIndices.length; j++) {
                rowDt[j] = new Double(data[i][sortedIndices[j]].toString());
                responses[r][j] = rowDt[j].doubleValue();

                if (r == 0 && j == 0) {
                    minResponse = responses[r][j];
                    maxResponse = responses[r][j];
                } else {
                    if (minResponse > responses[r][j]) {
                        minResponse = responses[r][j];
                    }

                    if (maxResponse < responses[r][j]) {
                        maxResponse = responses[r][j];
                    }
                }
            }

            if (reOrdered) {
                for (int j = 0; j < rowDt.length; j++) {
                    data[i][j + 1] = rowDt[j];
                }
            }
        }

        if (reOrdered) { // doses are reorder so re-arrange columns
            for (int i = 0; i < samples.length; i++) {
                columnNames[i + 1] = samples[i];
                data[0][i + 1] = Double.valueOf(doses[i]);
            }
        }

        t = System.currentTimeMillis() - t;
        System.out.println("Times spend on readResponses():" + t);
    }
     */

    public int getMaxDecimal() {
        return maxDecimal;
    }

    /**
     *  @reuturn chip name
     */
    public String getChip() {
        if (chip == null) {
            chip = readKeyValue(CHIP);
        }

        return chip;
    }


    /**
     * Depends on sorted doses and sorted unique doses in ascending order
     *
     * @return groupDoseIndices as an int[][], created to record index per dose
     *     grouped by unique doses sorted in ascending order.
     */
    public int[][] getGroupDoseIndices() {
        if (groupDoseIndices == null) {
            int group = uniDoses.length;
            int total = doses.length;
            groupDoseIndices = new int[group][];

            for (int g = 0; g < group; g++) {
                int[] temp = new int[total];
                int n = 0;

                for (int t = 0; t < total; t++) {
                    if (uniDoses[g] == doses[t]) {
                        temp[n++] = t;
                    }
                }

                groupDoseIndices[g] = new int[n];

                for (int i = 0; i < n; i++) {
                    groupDoseIndices[g][i] = temp[i];
                }
            }

            /* for testing only
            for (int i = 0; i < groupDoseIndices.length; i++) {
                System.out.print("\n" + uniDoses[i] + ":");

                for (int j = 0; j < groupDoseIndices[i].length; j++) {
                    System.out.print("\t" + groupDoseIndices[i][j]);
                }

                System.out.print("\n");
            }*/
        }

        return groupDoseIndices;
    }

    /**
     *  @reuturn unique doses group in order
     */
    public double[] getUniDoses() {
        return uniDoses;
    }

    /**
     *  @reuturn doses in ascending order
     */
    public double[] getDoses() {
        return doses;
    }

    /**
     *  @return responses of doses given a probe
     */
    public double[] getResponses(String probe) {
        //int row = arrayAnnotation.indexOfProbe(probe);
        int row = probesHash.get(probe).intValue();
        int columns = data[row + 1].length; // first row is for Doses
        double[] ret = new double[columns - 1]; // first column is for probes

        for(int i = 1; i < columns; i++){
            ret[i - 1] = Double.parseDouble(data[row + 1][i].toString());
        }

        return ret;
    }

    /**
     *  @return responses of doses as 2D array of doubles
    public double[][] getResponses() {
        return responses;
    }
     */

    /**
     *  @reuturn the minimum dose
     */
    public double minimumDose() {
        return minDose;
    }

    /**
     *  @reuturn the maximum dose
     */
    public double maximumDose() {
        return maxDose;
    }
    /**
     *  @reuturn the minimum response
    public double minimumResponse() {
        return minResponse;
    }
     */

    /**
     *  @reuturn the maximum response
    public double maximumResponse() {
        return maxResponse;
    }
     */

    /**
     *  @reuturn sample names as an array of String
     */
    public String[] getSamples() {
        return samples;
    }

    /**
     *  @reuturn probes as an array of String
     */
    public String[] allProbes() {
        return probes;
    }

    public HashSet<String> getControlProbes() {
        return ctrlProbes;
    }

    public void setArrayAnnotation(ArrayAnnotation annotation) {
        arrayAnnotation = annotation;
    }

    public ArrayAnnotation getArrayAnnotation() {
        return arrayAnnotation;
    }

    public boolean hasControlGenes() {
        return ctrlProbes.size() > 0;
    }

    /**
     *  Only works for Affymetrix and Agilent arrays so far
     *
     *  Affmatrix control genes starts with "AFFX", all probes end with "_at"
     *  Agelent control gene not startsWith "A_"
     */
    public boolean isControlGene(String probe) {
        boolean isControl = false;

        //if ((provider.equals("Affymetrix") && probe.startsWith("AFFX")) ||
        //       (provider.equals("Agilent") && !probe.startsWith("A_"))) {
        if ((probe.endsWith("_at") && probe.startsWith("AFFX"))
               || (!probe.endsWith("_at") && !probe.startsWith("A_"))) {
            isControl = true;
        }

        return isControl;
    }
}
