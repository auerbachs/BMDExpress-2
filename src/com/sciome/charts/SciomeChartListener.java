package com.sciome.charts;

import java.util.List;

public interface SciomeChartListener
{
	// a chart may want to tell it's parent that it is wants to make itself bigger
	public void expand(SciomeChartBase chart);

	public void close(SciomeChartBase sciomeChartBase);

	// charts can call this function that will relay to other charts which objects were
	// engaged via user interation
	public void chatWithOtherCharts(Object theChatter, List<Object> objects);
}
