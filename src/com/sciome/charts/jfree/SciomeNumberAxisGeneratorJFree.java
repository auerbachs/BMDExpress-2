package com.sciome.charts.jfree;

import java.text.NumberFormat;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.util.LogFormat;

public class SciomeNumberAxisGeneratorJFree
{

	public static ValueAxis generateAxis(boolean isLog, String label)
	{
		ValueAxis axis;
		if (isLog)
		{
			axis = new CustomJFreeLogarithmicAxis(label);
			((CustomJFreeLogarithmicAxis) axis).setStrictValuesFlag(false);
		}
		else
			axis = new NumberAxis(label);

		return axis;
	}
}
