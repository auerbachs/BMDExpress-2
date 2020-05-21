package com.sciome.charts.export;

import java.util.List;

/*
 * interface for class to implement method to get tab delimited lines of data from chart series.
 * The purpose of this is to allow user to get text representation of the chart so they can recreate the 
 * charts in other apps more easily.
 */
public interface ChartDataExporter
{

	public List<String> getLinesToExport();

}
