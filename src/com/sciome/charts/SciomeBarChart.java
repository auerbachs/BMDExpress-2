package com.sciome.charts;

import java.util.ArrayList;
import java.util.List;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;

/*
 * 
 */
public abstract class SciomeBarChart extends ScrollableSciomeChart implements ChartDataExporter
{

	protected Tooltip	toolTip		= new Tooltip("");
	protected final int	MAXITEMS	= 50;

	public SciomeBarChart(String title, List<ChartDataPack> chartDataPacks, String key,
			SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);
		chartableKeys = new String[] { key };
		showLogAxes(false, true, false, true);

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});
		lockYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		initChart();
		intializeScrollableChart();
		setShowShowAll(false);
	}

	private void initChart()
	{
		seriesData.clear();
		showChart();
		setMaxGraphItems(MAXITEMS);
		intializeScrollableChart();
	}

	// never show all for this. because it's like a bar chart
	@Override
	public void setShowShowAll(boolean showshowall)
	{
		super.setShowShowAll(false);
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return true;
	}

	@Override
	protected void redrawChart()
	{
		initChart();

	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("series");
		sb.append("\t");
		sb.append("x");
		sb.append("\t");
		sb.append("y");
		sb.append("\t");
		sb.append("component");
		returnList.add(sb.toString());
		for (Object obj : this.seriesData)
		{
			SciomeSeries sData = (SciomeSeries) obj;
			for (Object d : sData.getData())
			{
				SciomeData xychartData = (SciomeData) d;
				ChartExtraValue extraValue = (ChartExtraValue) xychartData.getExtraValue();
				String X = (String) xychartData.getxValue();

				Double Y = (Double) xychartData.getyValue();

				if (extraValue.userData == null) // this means it's a faked value for showing multiple
													// datasets together. skip it
					continue;
				sb.setLength(0);

				sb.append(sData.getName());
				sb.append("\t");
				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(extraValue.userData.toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

}
