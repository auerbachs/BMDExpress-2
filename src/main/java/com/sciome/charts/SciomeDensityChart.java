package com.sciome.charts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;

public abstract class SciomeDensityChart extends SciomeChartBase<Number, Number> implements ChartDataExporter {
	
	private final static int NUM_X_VALUES = 500;
	
	private Double bandwidth = null;
	private ChartKey key;
	
	public SciomeDensityChart(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			SciomeChartListener chartListener) {
		super(title, chartDataPacks, new ChartKey[] {key} , true, false, chartListener);
		this.key = key;
		this.configurationButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				showConfiguration();
			}
		});
	}

	@Override
	protected boolean isXAxisDefineable() {
		return false;
	}

	@Override
	protected boolean isYAxisDefineable() {
		return false;
	}

	@Override
	protected void redrawChart() {
		showChart();
	}

	@Override
	protected void convertChartDataPacksToSciomeSeries(ChartKey[] keys, List<ChartDataPack> chartPacks) {
		ChartKey key = keys[0];
		Double max = getMaxMax(key);
		
		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();
		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());

			double[] data = new double[chartDataPack.getChartData().size()];
			
			int count = 0;
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPointValue = (Double) chartData.getDataPoints().get(key);

				if (dataPointValue != null) 
					data[count] = dataPointValue;
				
				count++;
			}

			StandardDeviation std = new StandardDeviation();
			if(bandwidth == null) {
				bandwidth = 1.06 * std.evaluate(data) * Math.pow(data.length, (-1/5));
				bandwidth /= 10;
			}
			
			for(int i = 1; i < NUM_X_VALUES; i++) {
				double x = i * (max/NUM_X_VALUES);
				double sum = 0;
				for(int j = 0; j < data.length; j++) {
					double gaussVal = gaussian((x - data[j]) / bandwidth);
					sum += gaussVal;
				}
				double y = (1/(data.length * bandwidth)) * sum;
				
				SciomeData<Number, Number> point = new SciomeData<>("", x,
						y, chartDataPack.getName());
				
				series.getData().add(point);
			}
			
			seriesData.add(series);

		}
		setSeriesData(seriesData);
	}

	private void showConfiguration()
	{
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Chart Configuration");
		dialog.setResizable(true);
		dialog.initOwner(this.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);
		TextField bandwidthTF = new TextField();
		bandwidthTF.setMaxWidth(100.0);
		bandwidthTF.setText(bandwidth.toString());

		VBox vb = new VBox();
		vb.setSpacing(20.0);
		HBox hb1 = new HBox();
		hb1.setAlignment(Pos.CENTER_LEFT);
		hb1.setSpacing(10.0);

		HBox hb2 = new HBox();
		hb2.setAlignment(Pos.CENTER_LEFT);
		hb2.setSpacing(10.0);
		hb1.getChildren().addAll(new Label("Bandwidth"), bandwidthTF);
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
					bandwidth = Double.valueOf(bandwidthTF.getText());
					convertChartDataPacksToSciomeSeries(new ChartKey[] {key}, getChartDataPacks());
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
	
	private double gaussian(double u) {
		return (Math.exp(((-u * u)/2.0)))/(Math.sqrt(2 * Math.PI));
	}
}
