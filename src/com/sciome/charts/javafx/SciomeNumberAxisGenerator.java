package com.sciome.charts.javafx;

import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;

public class SciomeNumberAxisGenerator
{
	private static Double[] decades = { .00000000001, .0000000001, .000000001, .00000001, .0000001, .000001,
			.00001, .0001, .001, .01, .1, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 10000000.0,
			100000000.0 };

	public static Axis generateAxis(boolean isLog, Double min, Double max, Double dataMin)
	{
		if (isLog && max != null && max > min && dataMin != null && dataMin > 0.0 && min.equals(0.0))
			min = SciomeNumberAxisGenerator.firstDecadeBelow(dataMin);

		if (isLog && min != null && max != null && max > min)
			return new LogarithmicAxis(min, max);
		else if (isLog)
			return new LogarithmicAxis();
		else if (!isLog && min != null && max != null && max > min)
			return new NumberAxis(min, max, (max - min) / 10);

		return new NumberAxis();

	}

	public static Double firstDecadeBelow(Double logZeroDose)
	{
		Double[] decades = { .00000000001, .0000000001, .000000001, .00000001, .0000001, .000001, .00001,
				.0001, .001, .01, .1, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 10000000.0,
				100000000.0 };
		for (int i = 1; i < decades.length; i++)
		{
			double decade = decades[i];
			double lessthan = decade * .0001;
			if (Math.abs(decade - logZeroDose) < lessthan)
				return decades[i];
			else if (logZeroDose < decade)
				return decades[i - 1];
		}
		return .000000000001;
	}

}
