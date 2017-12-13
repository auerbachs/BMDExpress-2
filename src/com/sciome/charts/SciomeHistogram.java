package com.sciome.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;

/*
 * 
 */
public abstract class SciomeHistogram extends SciomeChartBase<String, Number> implements ChartDataExporter
{

	protected Double	bucketsize			= 20.0;
	protected final int	MAX_TOOL_TIP_SHOWS	= 20;

	public SciomeHistogram(String title, List<ChartDataPack> chartDataPacks, ChartKey key, Double bucketsize,
			SciomeChartListener chartListener)
	{

		super(title, chartDataPacks, new ChartKey[] { key }, chartListener);
		this.configurationButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				showConfiguration();
			}
		});
		this.bucketsize = bucketsize;
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

	protected Double getBucketSize()
	{
		return this.bucketsize;
	}

	protected void setBucketSize(Double bucketsize)
	{
		this.bucketsize = bucketsize;
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

	/*
	 * fill up the sciome series data structure so implementing classes can use it to create charts
	 */
	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks)
	{

		ChartKey key = keys[0];
		Double max = getMaxMax(key);
		Double min = getMinMin(key);

		if (bucketsize == null)
			bucketsize = 20.0;
		Double bucketSize = (max - min) / bucketsize;
		List<Double> dataPointCounts = new ArrayList<Double>(bucketsize.intValue());

		List<List<Object>> bucketObjects = new ArrayList<>();
		for (int i = 0; i <= bucketsize.intValue(); i++)
			bucketObjects.add(new ArrayList<>());

		List<Double> xDataPoints = new ArrayList<Double>(bucketsize.intValue());
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			xDataPoints.add(min + bucketSize * i);
			dataPointCounts.add(0.0);
		}

		// Now put the data in a bucket
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;

				// which bin?
				for (int i = 0; i <= bucketsize.intValue(); i++)
				{
					if (dataPoint <= min + bucketSize * i)
					{
						dataPointCounts.set(i, dataPointCounts.get(i) + 1.0);
						bucketObjects.get(i).add(chartData.getCharttableObject());
						break;
					}
				}
			}
		}
		List<SciomeSeries<String, Number>> seriesData = new ArrayList<>();
		SciomeSeries<String, Number> series1 = new SciomeSeries<>();
		series1.setName(key.toString());

		DecimalFormat df = new DecimalFormat("#.###");
		for (int i = 0; i <= bucketsize.intValue(); i++)
		{
			SciomeData<String, Number> data = new SciomeData<>("", df.format(xDataPoints.get(i)),
					dataPointCounts.get(i), bucketObjects.get(i));
			series1.getData().add(data);
		}
		seriesData.add(series1);
		setSeriesData(seriesData);

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
		sb.append("components delimited by ///");
		returnList.add(sb.toString());

		for (SciomeSeries<String, Number> seriesData : getSeriesData())
		{
			for (SciomeData<String, Number> xychartData : seriesData.getData())
			{
				sb.setLength(0);
				String X = (String) xychartData.getXValue();

				Double Y = (Double) xychartData.getYValue();

				List<Object> objects = (List) xychartData.getExtraValue();

				StringBuilder components = new StringBuilder();
				for (Object obj : objects)
				{
					if (components.length() > 0)
						components.append("///");
					components.append(obj.toString());
				}

				sb.append(seriesData.getName());
				sb.append("\t");
				sb.append(X);
				sb.append("\t");
				sb.append(Y);
				sb.append("\t");

				sb.append(components);

				returnList.add(sb.toString());

			}
		}

		return returnList;
	}

	private void showConfiguration()
	{
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Chart Configuration");
		dialog.setResizable(true);
		dialog.initOwner(this.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);
		TextField bucketSizeTF = new TextField();
		bucketSizeTF.setMaxWidth(100.0);

		VBox vb = new VBox();
		vb.setSpacing(20.0);
		HBox hb1 = new HBox();
		hb1.setAlignment(Pos.CENTER_LEFT);
		hb1.setSpacing(10.0);

		HBox hb2 = new HBox();
		hb2.setAlignment(Pos.CENTER_LEFT);
		hb2.setSpacing(10.0);
		hb1.getChildren().addAll(new Label("Bucket Size"), bucketSizeTF);
		vb.getChildren().addAll(hb1);

		dialog.getDialogPane().setContent(vb);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

		dialog.setResultConverter(new Callback<ButtonType, Boolean>() {
			@Override
			public Boolean call(ButtonType b)
			{

				if (b == buttonTypeOk)
				{
					try
					{
						setBucketSize(Double.valueOf(bucketSizeTF.getText()));
					}
					catch (Exception e)
					{

					}
					return true;
				}

				return false;
			}
		});

		dialog.getDialogPane().setPrefSize(400, 400);
		dialog.getDialogPane().autosize();
		Optional<Boolean> value = dialog.showAndWait();

		if (value.isPresent())
		{
			redrawCharts(getChartDataPacks());
		}
	}
}
