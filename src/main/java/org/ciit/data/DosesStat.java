/*
 * DosesStat.java
 *
 * Compute doses statistics
 */

package org.ciit.data;

import java.util.*;

public class DosesStat {
    private int N, groups, minReplicates;
    private double maxDose, minDose;
    private int[] uniOrderedIndices;
    private double[] doses, sortedUniDoses;
    private int[][] indices;

    private Vector<Double>uniDoses;

    /*
     * Class Constructor
     */
    public DosesStat() {
    }

    /*public DosesStat(double[] doses) {
        this.doses = doses;
        setDoses(doses);
    }*/

    public void asscendingSort(double[] doses) {
        N = doses.length;
        indices = new int[N][N];
        uniDoses = new Vector<Double>();

        for (int i = 0; i < N; i++) {
            Double varX = new Double(doses[i]);
            int row = uniDoses.indexOf(varX);

            if (row < 0) {
                uniDoses.add(varX);
                row = uniDoses.indexOf(varX);
                indices[row][0] = 0;
            }

            indices[row][0] += 1;
            indices[row][indices[row][0]] = i;
        }

        orderUniDoses();
    }

    /**
     *  for testing purpose only
     */
    public void printDoses() {
        for (int i = 0; i < groups; i++) {
            System.out.println(uniDoses.get(uniOrderedIndices[i]) + ": " + indices[uniOrderedIndices[i]][0]);

            for (int j = 1; j <= indices[uniOrderedIndices[i]][0]; j++) {
                System.out.print("  " + doses[indices[uniOrderedIndices[i]][j]]);
            }

            System.out.println("\n\n");
        }
    }

    private void orderUniDoses() {
        groups = uniDoses.size();

        if (groups > 0) {
            uniOrderedIndices = new int[groups];
            sortedUniDoses = new double[groups];

            for (int i = 0; i < groups; i++) {
                double x = uniDoses.get(i).doubleValue();

                for (int j = i; j >= 0; j--) {
                    if (j == 0 || sortedUniDoses[j - 1] <= x) {
                        uniOrderedIndices[j] = i;
                        sortedUniDoses[j] = x;
                        break;
                    } else {
                        sortedUniDoses[j] = sortedUniDoses[j - 1];
                        uniOrderedIndices[j] = uniOrderedIndices[j - 1];
                    }
                }
            }
        }
    }

    /**
     * Use original expression data DOSE Object as input
    public void asscendingSort(Object[] doses, int start) {
        // first read unique doses
        uniDoses = new Vector<Double>();
        Double[] tmp = new Double[doses.length - start];

        for (int i = start; i < doses.length; i++) {
            if (doses[i] instanceof Double) {
                tmp[i - start] = (Double)doses[i];

                if (!uniDoses.contains(tmp[i - start])) {
                    for (int j = uniDoses.size(); j >= 0; j--) {
                        if (j == 0 || tmp[i - start].compareTo(uniDoses.get(j - 1)) > 0) {
                            uniDoses.add(j, tmp[i - start]);
                            break;
                        }
                    }
                }
            }
        }

        groups = uniDoses.size();
        N = tmp.length;
        indices = new int[groups][N];

        for (int i = 0; i < groups; i++) {
            indices[i][0] = 0; // for counts only
        }

        for (int i = 0; i < N; i++) {
            int row = uniDoses.indexOf(tmp[i]);

            indices[row][0] += 1;
            indices[row][indices[row][0]] = i;
        }
    }
     */

    private int numberDoses() {
        return N;
    }

    public int numberDosesGroups() {
        return groups;
    }

    public double minDose() {
        return sortedUniDoses[0];
    }

    public double maxDose() {
        return sortedUniDoses[groups - 1];
    }

    /**
     *  Find non-zero minimum dose
     */
    public double noZeroMinDose() {
        int k = 0;

        for (int i = 0; i < groups; i++) {
            if (sortedUniDoses[i] > 0) {
                k = i;
                break;
            }
        }

        return sortedUniDoses[k];
    }

    public double[] sortedUniDoses() {
        return sortedUniDoses;
    }

    public int[][] dosesIndices() {
        int[][] dosesIndices = new int[groups][];

        for (int i = 0; i < groups; i++) {
            int n = indices[uniOrderedIndices[i]][0];
            dosesIndices[i] = new int[n];

            for (int j = 0; j < n; j++) {
                dosesIndices[i][j] = indices[uniOrderedIndices[i]][j + 1];
            }
        }

        return dosesIndices;
    }

    public int[] doseIndicesAt(int i) {
        return indices[uniOrderedIndices[i]];
    }

    public int minmumReplicate() {
        minReplicates = indices[0][0];

        for (int i = 1; i < groups; i++) {
            if (indices[i][0] < minReplicates) {
                minReplicates = indices[i][0];
            }
        }

        return minReplicates;
    }

    public String[] uniDosesToString(String offset) {
        String[] doses = new String[groups];

        if (offset == null) {
            offset = "";
        }

        for (int i = 0; i < groups; i++) {
            doses[i] = offset + sortedUniDoses[i];//Double.toString()
        }

        return doses;
    }

    public static void main(String[] ARGV) {
        double[] xx = {6, 6, 0, 0, 0, 0, 0, 0, 0, 0,
                       0.7, 0.7, 0.7, 0.7,
                       2, 2, 2, 2,
                       6, 6,
                       15, 15, 15, 15};

        DosesStat dosesStat = new DosesStat();
        dosesStat.asscendingSort(xx);

        /*System.out.println("Total doses: " + dosesStat.numberDoses());
         System.out.println("Groups doses: " + dosesStat.numberDosesGroups());
        System.out.println("Min Replicates: " + dosesStat.minmumReplicate());
        System.out.println("Min Dose: " + dosesStat.minDose());
        System.out.println("Max Dose: " + dosesStat.maxDose());*/
    }
}
