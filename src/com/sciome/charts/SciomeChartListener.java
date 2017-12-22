package com.sciome.charts;

public interface SciomeChartListener
{
	// a chart may want to tell it's parent that it is wants to make itself bigger
	public void expand(SciomeChartBase chart);

	public void close(SciomeChartBase sciomeChartBase);
}
