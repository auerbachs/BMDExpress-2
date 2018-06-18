package com.sciome.charts.jfree.violin;

import java.util.List;
import java.util.Map;

public class ViolinItem  {
	private Number mean;
	private Number median;
	private Number q1;
	private Number q3;
	private Number minRegularValue;
	private Number maxRegularValue;
	private Number minOutlier;
	private Number maxOutlier;
	private List<Number> outliers;
	private Number onePercentile;
	private Number fivePercentile;
	private Number tenPercentile;
	private Number tenRank;
	private Number twentyFiveRank;
	private Map<Number, Number> distribution;

	public ViolinItem(Number mean, Number median, Number q1, Number q3, Number minRegularValue, Number maxRegularValue,
		Number minOutlier, Number maxOutlier, List<Number> outliers, Number onePercentile, Number fivePercentile,
		Number tenPercentile, Number tenRank, Number twentyFiveRank, Map<Number, Number> distribution)
	{
		this.mean = mean;
		this.median = median;
		this.q1 = q1;
		this.q3 = q3;
		this.minRegularValue = minRegularValue;
		this.maxRegularValue = maxRegularValue;
		this.minOutlier = minOutlier;
		this.maxOutlier = maxOutlier;
		this.outliers = outliers;
		this.onePercentile = onePercentile;
		this.fivePercentile = fivePercentile;
		this.tenPercentile = tenPercentile;
		this.tenRank = tenRank;
		this.twentyFiveRank = twentyFiveRank;
		this.distribution = distribution;
	}
	
	public Number getMean() {
		return mean;
	}

	public Number getMedian() {
		return median;
	}

	public Number getQ1() {
		return q1;
	}

	public Number getQ3() {
		return q3;
	}

	public Number getMinRegularValue() {
		return minRegularValue;
	}

	public Number getMaxRegularValue() {
		return maxRegularValue;
	}

	public Number getMinOutlier() {
		return minOutlier;
	}

	public Number getMaxOutlier() {
		return maxOutlier;
	}

	public List<Number> getOutliers() {
		return outliers;
	}

	public Number getOnePercentile() {
		return onePercentile;
	}
	
	public Number getFivePercentile() {
		return fivePercentile;
	}

	public Number getTenPercentile() {
		return tenPercentile;
	}

	public Number getTenRank() {
		return tenRank;
	}

	public Number getTwentyFiveRank() {
		return twentyFiveRank;
	}

	public Map<Number, Number> getDistribution() {
		return distribution;
	}
}
