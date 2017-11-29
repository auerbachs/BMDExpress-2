package com.sciome.charts.javafx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.SciomeRangePlot;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class SciomeRangePlotFX extends SciomeRangePlot implements ChartDataExporter
{

	public SciomeRangePlotFX(String title, List<ChartDataPack> chartDataPacks, String minKey, String maxKey,
			String lowKey, String highKey, String middleKey, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, minKey, maxKey, lowKey, highKey, middleKey, chartListener);

	}

	/**
	 *
	 */
	private class RangePlot extends XYChart<Number, String>
	{

		// -------------- CONSTRUCTORS ----------------------------------------------
		/**
		 */
		public RangePlot(CategoryAxis yAxis, Axis<Number> xAxis)
		{
			super(xAxis, yAxis);
			// super.setMinSize(900, 900);

			setAnimated(false);
			xAxis.setAnimated(false);
			yAxis.setAnimated(false);

			// initilaize with empty set of data.
			XYChart.Series<Number, String> nullSeries = new XYChart.Series<Number, String>();
			nullSeries.setName("nullname");
			setData(FXCollections.observableArrayList());
			// setData(FXCollections.observableArrayList(nullSeries));
			setLegend(createLegend());
		}

		/**
		 */
		@SuppressWarnings("unused")
		public RangePlot(CategoryAxis yAxis, Axis<Number> xAxis, ObservableList<Series<Number, String>> data)
		{
			this(yAxis, xAxis);
			setData(data);
		}

		private Node createLegend()
		{
			StackPane node = new StackPane();
			node.getStyleClass().setAll("chart-legend");
			VBox vBox = new VBox();
			int seriesIndex = 0;
			if (getData() == null)
				return node;
			for (Series<Number, String> series : getData())
			{
				int colorIndex = seriesIndex % 7;
				Region bar = new Region();
				bar.setMinWidth(10.0);
				bar.setMinHeight(10.0);
				bar.setMaxHeight(10.0);
				bar.setMaxWidth(10.0);
				bar.getStyleClass().setAll("boxwhisker-box", "series", "data", "default-color" + colorIndex);
				HBox hBox = new HBox();
				hBox.getChildren().addAll(bar, new Label(series.getName()));
				vBox.getChildren().add(hBox);

				hBox.setAlignment(Pos.CENTER_LEFT);
				hBox.setSpacing(5.0);
				seriesIndex++;
			}
			node.getChildren().add(vBox);

			return node;
		}

		// -------------- METHODS
		// ------------------------------------------------------------------------------------------
		/** Called to update and layout the content for the plot */
		@Override
		protected void layoutPlotChildren()
		{

			// we have nothing to layout if no data is present
			if (getData() == null)
			{
				return;
			}
			// update candle positions
			int seriesCount = getData().size();
			int barCount = 0;
			for (Series<Number, String> series : getData())
			{
				for (@SuppressWarnings("unused")
				Object data : series.getData())
					barCount++;
			}
			for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++)
			{
				Series<Number, String> series = getData().get(seriesIndex);
				Iterator<Data<Number, String>> iter = getDisplayedDataIterator(series);

				while (iter.hasNext())
				{
					Data<Number, String> item = iter.next();
					double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
					double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
					Node itemNode = item.getNode();
					RangePlotExtraValue extra = (RangePlotExtraValue) item.getExtraValue();
					if (itemNode instanceof RangeForPlot && extra != null)
					{
						RangeForPlot rangeForPlot = (RangeForPlot) itemNode;

						Double close = null;

						Double high = null;
						if (extra.getMax() != null)
							high = getXAxis().getDisplayPosition(extra.getMax()) - x;

						Double low = null;
						if (extra.getMin() != null)
							low = getXAxis().getDisplayPosition(extra.getMin()) - x;
						// calculate candle width
						double candleWidth = -1;
						double spacingOffset = 0.0;
						if (getYAxis() instanceof CategoryAxis)
						{
							CategoryAxis xa = (CategoryAxis) getYAxis();
							double scaler = 2 * (double) barCount / getMaxGraphItems();
							if (scaler > .9)
								scaler = .9;
							candleWidth = xa.getCategorySpacing() * scaler / (seriesCount);
							double actualCategorySpacing = candleWidth * (seriesCount);
							spacingOffset = -actualCategorySpacing / 2 + candleWidth / 2;
							// / 2;
							// between ticks
						}
						// update candle
						double theSpacingOffset = 0.0;
						if (seriesCount > 1)
							theSpacingOffset = seriesIndex * candleWidth + spacingOffset;
						rangeForPlot.update(close, high, low, candleWidth, theSpacingOffset);

						// position the candle
						rangeForPlot.setLayoutX(x);
						rangeForPlot.setLayoutY(y);
					}
				}
			}
		}

		@Override
		protected void dataItemChanged(Data<Number, String> item)
		{
		}

		@Override
		protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item)
		{
			Node candle = createRangePlotNode(getData().indexOf(series), item, itemIndex);
			if (shouldAnimate())
			{
				candle.setOpacity(0);
				getPlotChildren().add(candle);
				// fade in new candle
				FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
				ft.setToValue(1);
				ft.play();
			}
			else
			{
				getPlotChildren().add(candle);
			}
			// always draw average line on top
			if (series.getNode() != null)
			{
				series.getNode().toFront();
			}
		}

		@Override
		protected void dataItemRemoved(Data<Number, String> item, Series<Number, String> series)
		{
			final Node candle = item.getNode();
			if (shouldAnimate())
			{
				// fade out old candle
				FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
				ft.setToValue(0);
				ft.setOnFinished(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent actionEvent)
					{
						getPlotChildren().remove(candle);
					}
				});
				ft.play();
			}
			else
			{
				getPlotChildren().remove(candle);
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void seriesAdded(Series<Number, String> series, int seriesIndex)
		{
			// handle any data already in series
			for (int j = 0; j < series.getData().size(); j++)
			{
				Data item = series.getData().get(j);
				Node candle = createRangePlotNode(seriesIndex, item, j);
				if (shouldAnimate())
				{
					candle.setOpacity(0);
					getPlotChildren().add(candle);
					// fade in new candle
					FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
					ft.setToValue(1);
					ft.play();
				}
				else
				{
					getPlotChildren().add(candle);
				}
			}
			// create series path
			Path seriesPath = new Path();
			seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
			series.setNode(seriesPath);
			getPlotChildren().add(seriesPath);
			setLegend(createLegend());
		}

		@Override
		protected void seriesRemoved(Series<Number, String> series)
		{
			// remove all candle nodes
			for (XYChart.Data<Number, String> d : series.getData())
			{
				final Node candle = d.getNode();
				if (shouldAnimate())
				{
					// fade out old candle
					FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
					ft.setToValue(0);
					ft.setOnFinished(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent actionEvent)
						{
							getPlotChildren().remove(candle);
						}
					});
					ft.play();
				}
				else
				{
					getPlotChildren().remove(candle);
				}
			}
		}

		/**
		 */
		private Node createRangePlotNode(int seriesIndex, final Data item, int itemIndex)
		{
			Node rangePlotNode = item.getNode();
			// check if candle has already been created
			if (rangePlotNode instanceof RangeForPlot)
			{
				((RangeForPlot) rangePlotNode).setSeriesAndDataStyleClasses("series" + seriesIndex,
						"data" + itemIndex);
			}
			else
			{
				rangePlotNode = new RangeForPlot("series" + seriesIndex, "data" + itemIndex, seriesIndex);
				item.setNode(rangePlotNode);

			}
			return rangePlotNode;
		}

		/**
		 * This is called when the range has been invalidated and we need to update it. If the axis are auto
		 * ranging then we compile a list of all data that the given axis has to plot and call
		 * invalidateRange() on the axis passing it that data.
		 */
		@Override
		protected void updateAxisRange()
		{
			// For candle stick chart we need to override this method as we need to let the axis know that
			// they need to be able
			// to cover the whole area occupied by the high to low range not just its center data value
			final Axis<Number> xa = getXAxis();
			final Axis<String> ya = getYAxis();
			List<Number> xData = null;
			List<String> yData = null;
			if (xa.isAutoRanging())
			{
				xData = new ArrayList<Number>();
			}
			if (ya.isAutoRanging())
			{
				yData = new ArrayList<String>();
			}
			if (xData != null || yData != null)
			{
				for (Series<Number, String> series : getData())
				{
					for (Data<Number, String> data : series.getData())
					{
						if (yData != null)
						{
							yData.add(data.getYValue());
						}
						if (xData != null)
						{
							RangePlotExtraValue extras = (RangePlotExtraValue) data.getExtraValue();
							if (extras.getMax() != null)
							{
								xData.add(extras.getMax());

							}

							if (extras.getMin() != null)
							{
								xData.add(extras.getMin());
							}

							xData.add(data.getXValue());

						}
					}
				}
				if (xData != null)
				{
					xa.invalidateRange(xData);
				}
				if (yData != null)
				{
					ya.invalidateRange(yData);
				}
			}
		}
	}

	/** Candle node used for drawing a candle */
	private class RangeForPlot extends Group
	{
		private Line	highLowLine	= new Line();
		private Line	topLine		= new Line();
		private Line	bottomLine	= new Line();
		private Region	bar			= new Region();
		private String	seriesStyleClass;
		private String	dataStyleClass;
		private int		seriesIndex	= 0;

		private RangeForPlot(String seriesStyleClass, String dataStyleClass, int sI)
		{
			setAutoSizeChildren(false);

			topLine.resizeRelocate(0.0, 0.0, 0.0, 0.0);
			bottomLine.resizeRelocate(0.0, 0.0, 0.0, 0.0);
			getChildren().addAll(highLowLine, bar, topLine, bottomLine);
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
			if (closeOffset == null)
				closeOffset = candleWidth;

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
				topLine.setStartY(seriesOffset);
				topLine.setEndY(candleWidth / 2 + seriesOffset);
				topLine.resizeRelocate(highOffset, -candleWidth / 4 + seriesOffset, candleWidth / 2,
						candleWidth / 2);
			}
			else
			{
				topLine.setVisible(false);
			}

			if (lowOffset != closeOffset)
			{
				bottomLine.setStartY(seriesOffset);
				bottomLine.setEndY(candleWidth / 2 + seriesOffset);
				bottomLine.resizeRelocate(lowOffset, -candleWidth / 4 + seriesOffset, candleWidth / 2,
						candleWidth / 2);
			}
			else
				bottomLine.setVisible(false);

		}

		private void updateStyleClasses()
		{
			int colorIndex = seriesIndex % 7;
			getStyleClass().setAll("boxwhisker-bar", seriesStyleClass, dataStyleClass);
			highLowLine.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			bar.getStyleClass().setAll("boxwhisker-box", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			topLine.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);
			bottomLine.getStyleClass().setAll("boxwhisker-line", seriesStyleClass, dataStyleClass,
					"default-color" + colorIndex);

		}
	}

	/*
	 * generate box and whisker chart.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{

		String minKey = keys[0];
		String maxKey = keys[1];
		String key = keys[3];
		Double axisMin = getMinMin(minKey);
		Double dataMin = axisMin;
		Double axisMax = getMaxMax(maxKey);
		if (axisMax == 0.0)
			axisMax = getMaxMax(key);

		if (axisMax < axisMin)
		{
			axisMax = 1.0;
			axisMin = 0.1;
		}

		CategoryAxis xAxis = new CategoryAxis();

		if (!getLockXAxis().isSelected())
		{
			if (chartConfig != null && chartConfig.getMaxX() != null && chartConfig.getMinX() != null)
			{
				axisMax = chartConfig.getMaxX();
				axisMin = chartConfig.getMinX();
			}
		}
		final Axis yAxis;
		yAxis = SciomeNumberAxisGenerator.generateAxis(getLogXAxis().isSelected(), axisMin, axisMax, dataMin);

		xAxis.setLabel("Category");
		// yAxis.setLabel(minKey + "," + lowKey + "," + key + "," + maxKey);
		yAxis.setLabel(minKey + "," + key + "," + maxKey);
		RangePlot barChart = new RangePlot(xAxis, yAxis);

		barChart.setTitle("Range Plot");

		toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");

		return barChart;
	}

	private RangeForPlot userObjectPane(Object object, boolean invisible, int seriesIndex)
	{

		RangeForPlot rangeNode = new RangeForPlot("series" + seriesIndex, "data", seriesIndex);
		rangeNode.setUserData(object);

		if (invisible)
			rangeNode.setVisible(false);
		else
		{
			Tooltip.install(rangeNode, toolTip);

			rangeNode.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					rangeNode.setEffect(new Glow());
					Object object = rangeNode.getUserData();
					if (object != null)
						toolTip.setText(String.valueOf(rangeNode.getUserData().toString()));

				}
			});

			// OnMouseExited
			rangeNode.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					rangeNode.setEffect(null);
				}
			});

			// OnMouseReleased
			rangeNode.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent mouseEvent)
				{
				}
			});
		}
		return rangeNode;
	}

	@Override
	protected Node getNode(String seriesName, String dataPointLabel, int seriesIndex)
	{
		NodeInformation nI = getNodeInformation(seriesName + dataPointLabel);

		return userObjectPane(nI.object, nI.invisible, seriesIndex);
	}

}
