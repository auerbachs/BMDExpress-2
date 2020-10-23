package com.toxicR;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class ToxicRUtils
{

	public static double[] convert2ColumnMajorOrder(double[] arr, int rowSize, int colSize)
	{
		double[] retarr = new double[arr.length];

		int index = 0;
		for (int i = 0; i < colSize; i++)
			for (int j = 0; j < rowSize; j++)
			{
				retarr[index++] = arr[i + colSize * j];
			}

		return retarr;

	}

	/*
	 * calculate direction of data using simple regression.
	 * assume doses and Y are in order and same length.
	 * 
	 * calculate mean of each dose group
	 */
	public static int calculateDirection(double[] doses, double[] Y)
	{

		List<Double> means = new ArrayList<>();
		List<Double> currGroup = new ArrayList<>();
		List<Double> individualDoses = new ArrayList<>();
		double currdose = -9999;
		for (int i = 0; i <= doses.length; i++)
		{

			if (i == doses.length || (doses[i] != currdose && currdose != -9999))
			{
				// calculate mean
				DescriptiveStatistics stats = new DescriptiveStatistics();

				// Add the data from the array
				for (Double value : currGroup)
					stats.addValue(value);
				means.add(stats.getMean());
				individualDoses.add(currdose);
				currGroup.clear();
			}

			if (i < doses.length)
			{
				currdose = doses[i];
				currGroup.add(Y[i]);
			}
		}

		SimpleRegression regression = new SimpleRegression();
		for (int i = 0; i < individualDoses.size(); i++)
			regression.addData(individualDoses.get(i), means.get(i));

		int direction = -1;
		if (regression.getSlope() > 0)
			direction = 1;

		return direction;
	}

}
