/* SampleStats.java
 * Created 1/5/2007 By Longlong Yang
 * Calculate sample's statistics
 */

package com.sciome.bmdexpress2.util.stat;


//import org.ciit.util.NumberManager;

public class SampleStats {
    private int n, df;
    private double X, XX, SS, W, mean, median, minimum, s2, sd, sdr, t, cl, cr;

    public SampleStats() {
    }

    public SampleStats(double t) {
        this.t = t;
    }

    /**
     *  @double t is the t values predetermined by samples size
     *  @param samples are ascending-sorted values
     */
    public SampleStats(double t, double[] samples) {
        this(t);
        sampleStatistics(samples, samples.length);
    }

    /**
     *  @double t is the t values predetermined by samples size
     *  @param samples are ascending-sorted values
     */
    public SampleStats(double t, double[] samples, int size) {
        this(t);
        sampleStatistics(samples, size);
    }

    /**
     *  @double t is the t values predetermined by samples size
     *  @param samples are ascending-sorted values
     *  @param weights are paired with each sample
     */
    public SampleStats(double t, double[] samples, double[]weights) {
        this(t);
        sampleStatistics(samples, weights);
    }

    /*
     * Compute sample statistics
     */
    private void sampleStatistics(double[] samples, int size) {
        X = XX = SS =  s2 = cl = cr = 0;
        mean = sd = sdr = Double.NaN;
        n = size;
        df = n - 1;

        for (int i = 0; i < n; i++) {
            X += samples[i];
            XX += samples[i] * samples[i];
        }

        mean = X / n;
        SS = XX - X * X / n;

        if (df > 0) {
            s2 = SS / df;
            sd = Math.sqrt(s2);
            sdr = Math.sqrt(s2 / n);
            cl = mean - t * sdr;
            cr = mean + t * sdr;
        }

        int mid = n / 2;
        int rem = n % 2;

        if (rem == 1) {
            median = samples[mid];
        } else {
            median = (samples[mid - 1] + samples[mid]) / 2;
        }

        minimum = samples[0];
    }

    /*
     * Compute weighted sample statistics
     */
    private void sampleStatistics(double[] samples, double[]weights) {
        X = XX = SS = s2 = cl = cr = 0;
        mean = sd = sdr = Double.NaN;
        n = samples.length;
        df = n - 1;

        for (int i = 0; i < n; i++) {
            X  += samples[i] * weights[i];
            XX += samples[i] * samples[i] * weights[i];
            W  += weights[i];
        }

        if (W != 0) {
            mean = X / W;
            SS = XX - X * X / W;

            if (SS < 0) {
                SS = pairedSumary(mean, samples, weights);
            }

            if (df > 0) {
                s2 = SS / (df * W / n);

                if (s2 >= 0) {
                    sd = Math.sqrt(s2);
                    sdr = Math.sqrt(s2/n);
                    cl = mean - t * sdr;
                    cr = mean + t * sdr;
                }
            }
        }

        int mid = n / 2;
        int rem = n % 2;

        if (rem == 1) { //odd
            median = samples[mid];
        } else { //even
            median = (samples[mid - 1] + samples[mid]) / 2;
        }

        minimum = samples[0];
    }

    private double pairedSumary(double mean, double[] samples, double[]weights) {
        double sum = 0;

        for (int i = 0; i < samples.length; i++) {
            sum += weights[i] * (samples[i] - mean) * (samples[i] - mean);
        }

        return sum;
    }

    public int df() {
        return df;
    }

    public double mean() {
        return mean;
    }

    public double median() {
        return median;
    }

    public double minimum() {
        return minimum;
    }

    public double standardDeviation() {
        return sd;
    }

    public double standardError() {
        return sdr;
    }

    public double confidenceLeft() {
        return cl;
    }

    public double confidenceRight() {
        return cr;
    }
}
