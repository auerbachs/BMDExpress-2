package com.sciome.charts;

import java.util.List;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;

public abstract class SciomeRangePlot extends ScrollableSciomeChart implements ChartDataExporter
{

	protected Tooltip	toolTip		= new Tooltip("");
	protected final int	MAXITEMS	= 20;

	@SuppressWarnings("unchecked")
	public SciomeRangePlot(String title, List<ChartDataPack> chartDataPacks, String minKey, String maxKey,
			String lowKey, String highKey, String middleKey, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);

		this.chartDataPacks = chartDataPacks;
		this.addDataAtTop = true;

		chartableKeys = new String[] { minKey, maxKey, lowKey, highKey, middleKey };

		logXAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});
		lockXAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				initChart();
			}
		});

		showLogAxes(true, false, true, false);
		initChart();
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

	/** Data extra values for storing close, high and low. */
	@SuppressWarnings("rawtypes")
	protected class BoxAndWhiskerExtraValues extends ChartExtraValue
	{
		private Double	min;
		private Double	max;
		private Double	high;
		private String	description;

		public BoxAndWhiskerExtraValues(String label, Integer count, Double min, Double max, Double high,
				String description, Object userData)
		{
			super(label, count, userData);
			this.min = min;
			this.max = max;
			this.high = high;
			this.description = description;
		}

		public Double getMin()
		{
			return min;
		}

		public Double getMax()
		{
			return max;
		}

		public Double getHigh()
		{
			return high;
		}

	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
	}

	@Override
	protected void redrawChart()
	{
		initChart();

	}

}
