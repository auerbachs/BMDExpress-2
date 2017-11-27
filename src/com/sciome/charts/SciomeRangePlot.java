package com.sciome.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;

public abstract class SciomeRangePlot extends ScrollableSciomeChart implements ChartDataExporter
{

	// map that keeps track of enough information to instantiate a node.
	// so we don't have to store large amounts of nodes in memory
	protected Map<String, NodeInformation>	nodeInfoMap	= new HashMap<>();
	protected Tooltip						toolTip		= new Tooltip("");
	protected final int						MAXITEMS	= 20;

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

	/** Candle node used for drawing a candle */
	private class BoxAndWhisker extends Group
	{
		private Line	highLowLine		= new Line();
		private Line	topWhisker		= new Line();
		private Line	bottomWhisker	= new Line();
		private Region	bar				= new Region();
		private String	seriesStyleClass;
		private String	dataStyleClass;
		private boolean	openAboveClose	= true;
		private Tooltip	tooltip			= new Tooltip();
		private int		seriesIndex		= 0;

		private BoxAndWhisker(String seriesStyleClass, String dataStyleClass, int sI)
		{
			setAutoSizeChildren(false);

			topWhisker.resizeRelocate(0.0, 0.0, 0.0, 0.0);
			bottomWhisker.resizeRelocate(0.0, 0.0, 0.0, 0.0);
			getChildren().addAll(highLowLine, bar, topWhisker, bottomWhisker);
			this.seriesStyleClass = seriesStyleClass;
			this.dataStyleClass = dataStyleClass;
			this.seriesIndex = sI;
			updateStyleClasses();

		}

		public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass)
		{
			this.seriesStyleClass = seriesStyleClass;
			this.dataStyleClass = dataStyleClass;

			updateStyleClasses();
		}

		public void update(Double closeOffset, Double highOffset, Double lowOffset, Double candleWidth,
				Double seriesOffset)
		{
			// openAboveClose = closeOffset > 0;
			updateStyleClasses();
			Double barX = 0.0;
			if (closeOffset == null)
			{
				closeOffset = candleWidth;
				barX = candleWidth / 2;
			}

			if (lowOffset == null)
				lowOffset = closeOffset;
			highLowLine.setStartX(lowOffset);

			if (highOffset == null || highOffset == 0)
			{
				highOffset = closeOffset;
				highLowLine.setEndX(highOffset - candleWidth);
			}
			else
			{
				highLowLine.setEndX(highOffset);
			}

			highLowLine.resizeRelocate(lowOffset, seriesOffset, highOffset - lowOffset, 2.0);
			if (candleWidth == -1)
			{
				candleWidth = bar.prefWidth(-1);
			}

			bar.resizeRelocate(-candleWidth / 2,
					-candleWidth / 2 + seriesOffset + highLowLine.getStrokeWidth() / 2, closeOffset,
					candleWidth);

			// bar.resizeRelocate(closeOffset,
			// -candleWidth / 2 + seriesOffset + highLowLine.getStrokeWidth() / 2, closeOffset * -1,
			// candleWidth);

			if (highOffset != closeOffset && highOffset > 0)
			{
				topWhisker.setStartY(seriesOffset);
				topWhisker.setEndY(candleWidth / 2 + seriesOffset);
				topWhisker.resizeRelocate(highOffset, -candleWidth / 4 + seriesOffset, candleWidth / 2,
						candleWidth / 2);
			}
			else
			{
				topWhisker.setVisible(false);
			}

			if (lowOffset != closeOffset)
			{
				bottomWhisker.setStartY(seriesOffset);
				bottomWhisker.setEndY(candleWidth / 2 + seriesOffset);
				bottomWhisker.resizeRelocate(lowOffset, -candleWidth / 4 + seriesOffset, candleWidth / 2,
						candleWidth / 2);
			}
			else
				bottomWhisker.setVisible(false);

		}

		private void updateStyleClasses()
		{
			int colorIndex = seriesIndex % 7;
			getStyleClass().setAll("boxwhisker-bar", seriesStyleClass, dataStyleClass);
			highLowLine.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			bar.getStyleClass().setAll("boxwhisker-box", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			topWhisker.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			bottomWhisker.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);

		}
	}

	protected class NodeInformation
	{

		public Object	object;
		public boolean	invisible;

		public NodeInformation(Object o, boolean i)
		{
			object = o;
			invisible = i;
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
