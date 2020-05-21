package com.sciome.charts.jfree.violin;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jfree.chart.util.Args;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.Statistics;

public class ViolinCalculator {
	protected static final int NUM_MAX_VALUES = 100;
	
	//Private constructor to ensure class isn't instantiated
	private ViolinCalculator()
	{
		
	}
	
	/**
     * Calculates the statistics required for a {@link BoxAndWhiskerItem}
     * from a list of {@code Number} objects.  Any items in the list
     * that are {@code null}, not an instance of {@code Number}, or
     * equivalent to {@code Double.NaN}, will be ignored.
     *
     * @param values  a list of numbers (a {@code null} list is not
     *                permitted).
     *
     * @return A box-and-whisker item.
     */
    public static ViolinItem calculateViolinStatistics (
                                        List values, Double bandwidth) {
        return calculateViolinStatistics(values, bandwidth, true);
    }

    /**
     * Calculates the statistics required for a {@link BoxAndWhiskerItem}
     * from a list of {@code Number} objects.  Any items in the list
     * that are {@code null}, not an instance of {@code Number}, or
     * equivalent to {@code Double.NaN}, will be ignored.
     *
     * @param values  a list of numbers (a {@code null} list is not
     *                permitted).
     * @param stripNullAndNaNItems  a flag that controls the handling of null
     *     and NaN items.
     *
     * @return A box-and-whisker item.
     *
     * @since 1.0.3
     */
    public static ViolinItem calculateViolinStatistics(
            List values, Double bandwidth, boolean stripNullAndNaNItems) {

        Args.nullNotPermitted(values, "values");

        List vlist;
        if (stripNullAndNaNItems) {
            vlist = new ArrayList(values.size());
            Iterator iterator = values.listIterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Number) {
                    Number n = (Number) obj;
                    double v = n.doubleValue();
                    if (!Double.isNaN(v)) {
                        vlist.add(n);
                    }
                }
            }
        }
        else {
            vlist = values;
        }
        
        Collections.sort(vlist);
		
        double mean = Statistics.calculateMean(vlist, false);
        double median = Statistics.calculateMedian(vlist, false);
        double q1 = calculateQ1(vlist);
        double q3 = calculateQ3(vlist);

        double interQuartileRange = q3 - q1;

        double upperOutlierThreshold = q3 + (interQuartileRange * 1.5);
        double lowerOutlierThreshold = q1 - (interQuartileRange * 1.5);

        double minRegularValue = Double.POSITIVE_INFINITY;
        double maxRegularValue = Double.NEGATIVE_INFINITY;
        double minOutlier = Double.POSITIVE_INFINITY;
        double maxOutlier = Double.NEGATIVE_INFINITY;
        List<Number> outliers = new ArrayList<Number>();

        Iterator iterator = vlist.iterator();
        while (iterator.hasNext()) {
            Number number = (Number) iterator.next();
            double value = number.doubleValue();
            if (value > upperOutlierThreshold) {
                outliers.add(number);
                if (value > maxOutlier) {
                    maxOutlier = value;
                }
            }
            else if (value < lowerOutlierThreshold) {
                outliers.add(number);
                if (value < minOutlier) {
                    minOutlier = value;
                }
            }
            else {
                minRegularValue = Math.min(minRegularValue, value);
                maxRegularValue = Math.max(maxRegularValue, value);
            }
            minOutlier = Math.min(minOutlier, minRegularValue);
            maxOutlier = Math.max(maxOutlier, maxRegularValue);
        }
        
        int size = vlist.size();
        Number onePercentile = (Number)vlist.get((int)(Math.ceil(.01 * size)));
        Number fivePercentile = (Number)vlist.get((int)(Math.ceil(.05 * size)));
        Number tenPercentile = (Number)vlist.get((int)(Math.ceil(.10 * size)));
        Number tenRank = null;
        Number twentyFiveRank = null;
        if(size > 10)
        	tenRank = (Number)vlist.get(9);
        if(size > 25)
        	twentyFiveRank = (Number)vlist.get(24);
        
        //Calculate kernel density estimation
        HashMap<Number, Point2D.Double> dist = new HashMap<Number, Point2D.Double>();
        
        StandardDeviation std = new StandardDeviation();
        double[] data = new double[vlist.size()];
        double max = Double.MIN_VALUE;
        for(int i = 0; i < vlist.size(); i++) {
        	data[i] = (double)vlist.get(i);
        	if(data[i] > max)
        		max = data[i];
        }
        if(bandwidth == null) {
	        bandwidth = 1.06 * std.evaluate(data) * Math.pow(data.length, (-1/5));
			bandwidth /= 10;
        }
		
		for(int i = 0; i <= NUM_MAX_VALUES; i++) {
			double x = i * (max/(double)NUM_MAX_VALUES);
			double sum = 0;
			for(int j = 0; j < data.length; j++) {
				double gaussVal = gaussian((x - data[j]) / bandwidth);
				sum += gaussVal;
			}
			double y = (1/(data.length * bandwidth)) * sum;
			dist.put(i, new Point2D.Double(x,y));
		}
        
        return new ViolinItem(new Double(mean), new Double(median),
                new Double(q1), new Double(q3), new Double(minRegularValue),
                new Double(maxRegularValue), new Double(minOutlier),
                new Double(maxOutlier), outliers,
                onePercentile,
                fivePercentile,
                tenPercentile,
                tenRank,
                twentyFiveRank,
                dist);

    }

    /**
     * Calculates the first quartile for a list of numbers in ascending order.
     * If the items in the list are not in ascending order, the result is
     * unspecified.  If the list contains items that are {@code null}, not
     * an instance of {@code Number}, or equivalent to
     * {@code Double.NaN}, the result is unspecified.
     *
     * @param values  the numbers in ascending order ({@code null} not
     *     permitted).
     *
     * @return The first quartile.
     */
    public static double calculateQ1(List values) {
        Args.nullNotPermitted(values, "values");

        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, 0, count / 2);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, 0, count / 2 - 1);
            }

        }
        return result;
    }

    /**
     * Calculates the third quartile for a list of numbers in ascending order.
     * If the items in the list are not in ascending order, the result is
     * unspecified.  If the list contains items that are {@code null}, not
     * an instance of {@code Number}, or equivalent to
     * {@code Double.NaN}, the result is unspecified.
     *
     * @param values  the list of values ({@code null} not permitted).
     *
     * @return The third quartile.
     */
    public static double calculateQ3(List values) {
        Args.nullNotPermitted(values, "values");
        double result = Double.NaN;
        int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, count / 2,
                            count - 1);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, count / 2,
                        count - 1);
            }
        }
        return result;
    }
    
	private static double gaussian(double u) {
		return (Math.exp(((-u * u) / 2.0))) / (Math.sqrt(2 * Math.PI));
	}
}
