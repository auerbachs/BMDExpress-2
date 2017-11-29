package com.sciome.charts.jfree;

import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.NumberAxis;

public class SciomeNumberAxisGeneratorJFree {

	public static ValueAxis generateAxis(boolean isLog)
	{
		if (isLog)
			return new LogAxis();
		else
			return new NumberAxis();
	}
}
