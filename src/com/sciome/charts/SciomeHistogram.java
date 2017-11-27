package com.sciome.charts;

import java.util.List;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

/*
 * 
 */
public abstract class SciomeHistogram extends SciomeChartBase implements ChartDataExporter
{

	protected Double	bucketsize			= 20.0;
	protected final int	MAX_TOOL_TIP_SHOWS	= 20;

	public SciomeHistogram(String title, List<ChartDataPack> chartDataPacks, String key, Double bucketsize,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		this.bucketsize = bucketsize;
		chartableKeys = new String[] { key };
		showChart();

	}

	protected String joinAllObjects(List<Object> objects)
	{
		return joinObjects(objects, 2000000000);

	}

	protected String joinObjects(List<Object> objects, int max)
	{
		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("\n");
			if (i >= max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
	}

	@Override
	protected void redrawChart()
	{
		showChart();

	}

}
