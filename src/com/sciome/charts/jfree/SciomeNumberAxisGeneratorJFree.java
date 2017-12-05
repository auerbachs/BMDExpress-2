package com.sciome.charts.jfree;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

public class SciomeNumberAxisGeneratorJFree {

	public static ValueAxis generateAxis(boolean isLog, String label)
	{
		ValueAxis axis;
		if (isLog)
			axis = new LogarithmicAxis(label);
		else
			axis = new NumberAxis(label);
		
		return axis;
	}
}
