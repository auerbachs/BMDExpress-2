package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.javafx.SciomeBarChartFX;
import com.sciome.charts.jfree.SciomeAccumulationPlotJFree;
import com.sciome.charts.jfree.SciomeBubbleChartJFree;
import com.sciome.charts.jfree.SciomeHistogramJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreateYourOwnChart extends Dialog<SciomeChartBase>
{

	private final String				SCATTER			= "Scatter Chart";
	private final String				BUBBLE			= "Bubble Chart";
	private final String				HISTOGRAM		= "Histogram";
	private final String				BAR				= "Bar";
	private final String				ACCUMULATION	= "Accumulation Chart";

	private ComboBox<String>			chartType		= new ComboBox<>();

	private List<ChartKeyLayout>		chartKeyLayouts	= new ArrayList<>();
	private VBox						contents		= new VBox(8);;
	private BMDExpressAnalysisDataSet	bmdAnalysisDataSet;
	private SciomeChartListener			chartChangeListener;
	private List<ChartDataPack>			chartDataPacks	= new ArrayList<>();

	public CreateYourOwnChart(BMDExpressAnalysisDataSet bmdAnalysisDataSet,
			SciomeChartListener chartChangeListener)
	{
		super();
		this.chartChangeListener = chartChangeListener;
		this.bmdAnalysisDataSet = bmdAnalysisDataSet;

		chartType.getItems().add(SCATTER);
		chartType.getItems().add(BUBBLE);
		chartType.getItems().add(HISTOGRAM);
		chartType.getItems().add(BAR);
		chartType.getItems().add(ACCUMULATION);

		chartType.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1)
			{
				setUpChartKeyLayout();
			}

		});

		ButtonType loginButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		getDialogPane().getScene().getStylesheets()
				.add(getClass().getResource("/fxml/application.css").toExternalForm());

		contents.getChildren().add(new Label("Define Your Chart"));
		contents.getChildren().add(chartType);

		setResizable(true);
		getDialogPane().setMinHeight(500.0);
		getDialogPane().setMinWidth(500.0);

		getDialogPane().setContent(contents);

		setResultConverter(buttonType ->
		{
			SciomeChartBase chart = getChart();
			return chart;
		});
	}

	private SciomeChartBase getChart()
	{
		List<ChartKey> chartKeys = getChartKeys();
		if (chartType.getValue() == null)
			return null;

		if (chartKeys == null)
			return null;
		if (chartType.getValue().equals(SCATTER))
		{
			if (chartKeys.size() != 2)
				return null;
			return new SciomeScatterChartJFree(
					chartKeys.get(0).toString() + " Vs. " + chartKeys.get(1).toString(), chartDataPacks,
					chartKeys.get(0), chartKeys.get(1), chartChangeListener);

		}
		else if (chartType.getValue().equals(BUBBLE))
		{
			if (chartKeys.size() != 3)
				return null;
			return new SciomeBubbleChartJFree(
					chartKeys.get(0).toString() + " Vs. " + chartKeys.get(1).toString() + " Vs. "
							+ chartKeys.get(2).toString(),
					chartDataPacks, chartKeys.get(0), chartKeys.get(1), chartKeys.get(2),
					chartChangeListener);
		}
		else if (chartType.getValue().equals(BAR))
		{
			if (chartKeys.size() != 1)
				return null;

			return new SciomeBarChartFX("Bar: " + chartKeys.get(0).toString(), chartDataPacks,
					chartKeys.get(0), chartChangeListener);
		}

		else if (chartType.getValue().equals(HISTOGRAM))
		{
			if (chartKeys.size() != 1)
				return null;
			return new SciomeHistogramJFree("Histogram: " + chartKeys.get(0).toString(), chartDataPacks,
					chartKeys.get(0), 20.0, chartChangeListener);
		}
		else if (chartType.getValue().equals(ACCUMULATION))
		{
			if (chartKeys.size() != 1)
				return null;
			return new SciomeAccumulationPlotJFree("Accumulation: " + chartKeys.get(0).toString(),
					chartDataPacks, chartKeys.get(0), 20.0, chartChangeListener);
		}
		return null;
	}

	private List<ChartKey> getChartKeys()
	{
		List<ChartKey> ckeys = new ArrayList<>();
		for (ChartKeyLayout ckl : chartKeyLayouts)
			ckeys.add(ckl.getChartKey());

		// do sanity check
		for (ChartKey ck : ckeys)
			if (ck == null || ck.getKey() == null || ck.getKey().equals(""))
				return null;

		return ckeys;
	}

	private List<String> getKeys()
	{
		List<String> keys = new ArrayList<>();

		for (String key : bmdAnalysisDataSet.getColumnHeader())
		{
			Class clazz = bmdAnalysisDataSet.getHeaderClass(key);
			if (clazz == null)
				continue;
			if (bmdAnalysisDataSet.getHeaderClass(key).equals(Number.class)
					|| bmdAnalysisDataSet.getHeaderClass(key).equals(Double.class)
					|| bmdAnalysisDataSet.getHeaderClass(key).equals(Integer.class)
					|| bmdAnalysisDataSet.getHeaderClass(key).equals(Short.class)
					|| bmdAnalysisDataSet.getHeaderClass(key).equals(Float.class))
				keys.add(key);
		}

		return keys;

	}

	private void setUpChartKeyLayout()
	{
		contents.getChildren().removeAll(chartKeyLayouts);
		chartKeyLayouts.clear();

		if (chartType.getValue().equals(SCATTER))
		{
			chartKeyLayouts.add(new ChartKeyLayout("X Axis", getKeys()));
			chartKeyLayouts.add(new ChartKeyLayout("Y Axis", getKeys()));

		}
		else if (chartType.getValue().equals(BUBBLE))
		{
			chartKeyLayouts.add(new ChartKeyLayout("X Axis", getKeys()));
			chartKeyLayouts.add(new ChartKeyLayout("Y Axis", getKeys()));
			chartKeyLayouts.add(new ChartKeyLayout("Bubble Size", getKeys()));
		}
		else if (chartType.getValue().equals(BAR))
		{
			chartKeyLayouts.add(new ChartKeyLayout("Value", getKeys()));
		}
		else if (chartType.getValue().equals(HISTOGRAM))
		{
			chartKeyLayouts.add(new ChartKeyLayout("Value", getKeys()));
		}
		else if (chartType.getValue().equals(ACCUMULATION))
		{
			chartKeyLayouts.add(new ChartKeyLayout("Value", getKeys()));
		}
		contents.getChildren().addAll(chartKeyLayouts);
	}

	private class ChartKeyLayout extends HBox
	{
		private Label				label		= new Label();
		private ComboBox<String>	keyCombo	= new ComboBox<>();
		private ComboBox<String>	mathCombo	= new ComboBox<>();

		public ChartKeyLayout(String labelText, List<String> keys)
		{
			super(8);
			this.label.setText(labelText);
			label.setMinWidth(100);
			label.setMaxWidth(100);
			mathCombo.setMaxWidth(100);
			keyCombo.setMinWidth(200);
			keyCombo.setMaxWidth(200);

			mathCombo.getItems().addAll(ChartKey.ABS, ChartKey.LOG, ChartKey.NEGLOG, ChartKey.SQRT);
			keyCombo.getItems().addAll(keys);
			getChildren().addAll(label, keyCombo, mathCombo);
		}

		public ChartKey getChartKey()
		{
			ChartKey chartKey = null;
			if (keyCombo.getValue() == null)
				return null;
			if (!keyCombo.getValue().equals(""))
			{
				String math = null;
				if (mathCombo.getValue() != null && !mathCombo.getValue().equals(""))
					math = mathCombo.getValue();
				return new ChartKey(keyCombo.getValue(), math);
			}
			return null;
		}

	}

}
