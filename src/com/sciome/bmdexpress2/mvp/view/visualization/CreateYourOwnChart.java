package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.charts.SciomeChartBase;
import com.sciome.filter.DataFilterPack;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreateYourOwnChart extends Dialog<SciomeChartBase>
{

	private final String			SCATTER				= "Scatter Chart";
	private final String			BUBBLE				= "Bubble Chart";
	private final String			HISTOGRAM			= "Histogram";
	private final String			BAR					= "Bar";
	private final String			ACCUMULATION		= "Accumulation Chart";

	private ComboBox<String>		chartType			= new ComboBox<>();

	private List<ChartKeyLayout>	chartKeyComboBoxes	= new ArrayList<>();
	private DataFilterPack			defaultDPack;
	private VBox					contents			= new VBox(8);;

	public CreateYourOwnChart(DataFilterPack defaultDPack)
	{
		super();
		this.defaultDPack = defaultDPack;
		chartType.getItems().add(SCATTER);
		chartType.getItems().add(BUBBLE);
		chartType.getItems().add(HISTOGRAM);
		chartType.getItems().add(BAR);
		chartType.getItems().add(ACCUMULATION);

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
		return null;
	}

	private List<String> getKeys()
	{
		List<String> keys = new ArrayList<>();

		return keys;

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

			mathCombo.getItems().addAll(ChartKey.ABS, ChartKey.LOG, ChartKey.NEGLOG, ChartKey.SQRT);
			keyCombo.getItems().addAll(keys);
			getChildren().addAll(label, keyCombo, mathCombo);
		}

		public ChartKey getChartKey()
		{
			ChartKey chartKey = null;
			if (!keyCombo.getValue().equals(""))
			{
				String math = null;
				if (!mathCombo.getValue().equals(""))
					math = mathCombo.getValue();
				return new ChartKey(keyCombo.getValue(), math);
			}
			return null;
		}

	}

}
