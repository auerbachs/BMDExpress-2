package com.sciome.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.IGeneContainer;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.charts.data.ChartData;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeData;
import com.sciome.charts.model.SciomeSeries;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;

/*
 * 
 */
public abstract class SciomeAccumulationPlot extends SciomeChartBase<Number, Number>
		implements ChartDataExporter
{
	protected final static Integer					MAX_ACCUMULATION_BEFORE_MODULUS	= 300;
	protected final static Integer					MOD_AFTER_REACH_MAX				= 20;
	protected final static Integer					MAX_TO_POPUP					= 100;
	protected final static Integer					MAX_PREV_OBJECTS_TO_STORE		= 0;
	// protected CheckBox unBinCheckBox = new CheckBox(
	// "Show All Data");
	protected Button								genesButton						= new Button("Gene Sets");
	protected Tooltip								toolTip							= new Tooltip("");

	protected Set<String>							genesToHighLight				= new HashSet<>();
	private Map<String, Map<String, Set<String>>>	dbToPathwayToGeneSet;

	public SciomeAccumulationPlot(String title, List<ChartDataPack> chartDataPacks, ChartKey key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, new ChartKey[] { key }, chartListener);

		getLogXAxis().setSelected(true);
		getLogYAxis().setSelected(false);
		// this chart defines how the axes can be edited by the user in the chart configuration.
		showLogAxes(true, true, false, false, Arrays.asList(genesButton));
		showChart();

		getLogXAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		getLogYAxis().selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		// open a configuration diaglog and then redraw the chart.
		// currently this only allows you to set the x/y axis ranges.
		genesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				specifyGenesToHighlight();
			}

		});

	}

	// join objects together to show a list of lines that show values of the points being plotted
	// the objects and values need to be in alignment to ensure that the value they represent
	// is properly appended next to it in the popup.
	protected String joinObjects(List<Object> objects, Double accumulation, List<Double> values, String key,
			int max)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Accumulation = ");
		sb.append(accumulation);
		sb.append("\n");
		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("  ");
			sb.append(key);
			sb.append(" = ");
			sb.append(values.get(i));
			sb.append(System.getProperty("line.separator"));
			if (i > max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	protected String joinAllObjects(List<Object> objects, Double accumulation, List<Double> values,
			String key)
	{
		return joinObjects(objects, accumulation, values, key, 2000000000);

	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return true;
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
		List<SciomeSeries<Number, Number>> seriesData = new ArrayList<>();

		for (ChartDataPack chartDataPack : getChartDataPacks())
		{
			List<ChartData> doubleList = new ArrayList<>();
			for (ChartData chartData : chartDataPack.getChartData())
			{
				Double dataPoint = (Double) chartData.getDataPoints().get(key);
				if (dataPoint == null)
					continue;
				doubleList.add(chartData);
			}

			doubleList.sort(new Comparator<ChartData>() {
				@Override
				public int compare(ChartData o1, ChartData o2)
				{
					return ((Double) o1.getDataPoints().get(key))
							.compareTo((Double) o2.getDataPoints().get(key));
				}
			});

			int i = 0;
			SciomeSeries<Number, Number> series = new SciomeSeries<>();
			series.setName(chartDataPack.getName());
			Double accumulation = 0.0;

			int count = 0;
			Double currentValue = null;
			List<Object> charttableObjectsMasterList = new ArrayList<>();
			List<Object> charttableObjects = new ArrayList<>();
			/*
			 * start adding accumulation values
			 */

			// a list that will store the values associated with the object.
			List<Double> valuesList = new ArrayList<>();
			for (ChartData value : doubleList)
			{
				Double newValue = (Double) value.getDataPoints().get(key);
				if (!newValue.equals(currentValue) && currentValue != null)
				{

					if (charttableObjects.size() == 1) // we are in the area before the modulus kicks in.
					{
						int adds = 0;
						int j = i - 1;
						while (adds < MAX_PREV_OBJECTS_TO_STORE && j >= 0)
						{
							charttableObjects.add(charttableObjectsMasterList.get(j));
							j--;
							adds++;
						}
					}
					AccumulationData theData = new AccumulationData("", currentValue, accumulation,
							charttableObjects, valuesList);
					series.getData().add(theData);
					charttableObjects = new ArrayList<>();
					valuesList = new ArrayList<>();
				}

				count++;
				valuesList.add(newValue);
				charttableObjects.add(value.getCharttableObject());
				charttableObjectsMasterList.add(value.getCharttableObject());
				accumulation++;
				currentValue = newValue;
				i++;
			}
			// get the last one
			if (currentValue != null)
			{
				AccumulationData theData = new AccumulationData("", currentValue, accumulation,
						charttableObjects, valuesList);
				series.getData().add(theData);
			}
			seriesData.add(series);

		}

		setSeriesData(seriesData);

	}

	/*
	 * extend the SciomeData object so we can store a list of values associated with the Object (which is a
	 * list of categories)
	 */
	protected class AccumulationData extends SciomeData<Number, Number>
	{

		private List<Double> valuesList;

		public AccumulationData(String n, Number x, Number y, Object o, List<Double> v)
		{
			super(n, x, y, o);
			this.valuesList = v;
		}

		public List<Double> getValuesList()
		{
			return valuesList;
		}

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
		for (SciomeSeries<Number, Number> seriesData : getSeriesData())
		{
			for (SciomeData<Number, Number> xychartData : seriesData.getData())
			{
				sb.setLength(0);
				Double X = xychartData.getXValue().doubleValue();
				Double Y = xychartData.getYValue().doubleValue();
				List extraValue = (List) xychartData.getExtraValue();

				StringBuilder components = new StringBuilder();
				for (Object obj : extraValue)
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
				sb.append(components.toString());

				returnList.add(sb.toString());

			}
		}

		return returnList;

	}

	public void setdbToPathwayToGeneSet(Map<String, Map<String, Set<String>>> dbToPathwayToGeneSet)
	{
		this.dbToPathwayToGeneSet = dbToPathwayToGeneSet;
	}

	// show the configuration to the user.
	// this too coupled to the model
	// we will need to make the highlighting of genes or categories more generic
	// This will take a little more thought and reengineering.
	//
	private void specifyGenesToHighlight()
	{
		TextArea genesTextField = new TextArea();
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Select Gene Sets To Highlight");
		dialog.setResizable(true);
		dialog.initOwner(this.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);

		VBox vbox = new VBox();
		if (dbToPathwayToGeneSet != null)
		{
			ComboBox<String> howtodostring;
			// Create the CheckComboBox with the data
			howtodostring = new ComboBox<String>(
					FXCollections.observableArrayList(Arrays.asList("begins with", "contains")));

			howtodostring.setValue("begins with");
			List<String> pathways = new ArrayList<>();
			HBox hbox = new HBox();

			ComboBox<String> dbCombo = new ComboBox<>();
			TextField pathwayTextField = new TextField();
			Button clearButton = new Button("Clear");
			clearButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e)
				{
					genesTextField.clear();
				}

			});
			pathwayTextField.setMinWidth(300);
			TextFields.bindAutoCompletion(pathwayTextField,
					new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {

						@Override
						public Collection<String> call(ISuggestionRequest param)
						{
							List<String> returnList = new ArrayList<>();
							for (String p : pathways)
								if (howtodostring.getValue().equals("contains")
										&& p.toLowerCase().contains(param.getUserText().toLowerCase()))
									returnList.add(p);
								else if (howtodostring.getValue().equals("begins with")
										&& p.toLowerCase().startsWith(param.getUserText().toLowerCase()))
									returnList.add(p);

							return returnList;
						}
					});
			List<String> dbList = new ArrayList<>(dbToPathwayToGeneSet.keySet());
			Collections.sort(dbList);
			dbCombo.setItems(FXCollections.observableArrayList(dbList));

			dbCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public void changed(ObservableValue ov, String t, String t1)
				{
					pathways.clear();
					pathways.addAll(dbToPathwayToGeneSet.get(t1).keySet());
					Collections.sort(pathways);

				}
			});
			pathwayTextField.textProperty().addListener((observable, oldValue, newValue) ->
			{
				if (newValue == null)
					return;

				Set<String> genes = dbToPathwayToGeneSet.get(dbCombo.getValue()).get(newValue);
				if (genes == null)
					return;
				List<String> geneSymbols = new ArrayList<>(genes);
				Collections.sort(geneSymbols);

				// this is too coupled to the category analysis results need to redesign how this data is
				// gotten
				if (getChartDataPacks().size() > 0 && getChartDataPacks().get(0).getChartData().size() > 0
						&& getChartDataPacks().get(0).getChartData().get(0)
								.getCharttableObject() instanceof CategoryAnalysisResult)
				{
					genesTextField.setText(newValue + "\n" + genesTextField.getText());
				}
				else
					genesTextField.setText(String.join("\n", geneSymbols) + "\n" + genesTextField.getText());

			});

			hbox.getChildren().addAll(howtodostring, dbCombo, pathwayTextField, clearButton);
			vbox.getChildren().add(hbox);
			dbCombo.getSelectionModel().select(0);
		}

		genesTextField.setMinWidth(600.0);
		genesTextField.setMinHeight(400.0);

		String defaultText = String.join("\n", genesToHighLight);
		genesTextField.setText(defaultText);

		vbox.getChildren().add(genesTextField);

		dialog.getDialogPane().setContent(vbox);
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b)
			{
				if (b == buttonTypeOk)
					return genesTextField.getText();

				return null;
			}
		});

		dialog.getDialogPane().setPrefSize(600, 400);
		dialog.getDialogPane().autosize();
		Optional<String> value = dialog.showAndWait();

		if (value.isPresent())
		{
			genesToHighLight.clear();
			for (String line : genesTextField.getText().split("\\n"))
				genesToHighLight.add(line.toLowerCase());
			redrawChart();
		}

	}

	/*
	 * this is coupled to data model. need to decouple it one day
	 */
	protected int objectsNeedHighlighting(List<Object> objects)
	{

		for (Object object : objects)
		{
			if (object instanceof CategoryAnalysisResult)
			{
				if (genesToHighLight
						.contains(((CategoryAnalysisResult) object).getCategoryDescription().toLowerCase()))
				{
					return 1;
				}
			}
			else if (object instanceof IGeneContainer)
			{
				if (((IGeneContainer) object).containsGenes(genesToHighLight).size() > 0)
				{
					if (object instanceof ProbeStatResult
							&& ((ProbeStatResult) object).getBestFoldChange() != null
							&& ((ProbeStatResult) object).getBestFoldChange() > 0)
						return 1;
					else if (object instanceof ProbeStatResult
							&& ((ProbeStatResult) object).getBestFoldChange() != null
							&& ((ProbeStatResult) object).getBestFoldChange() < 0)
						return -1;
					else
						return 1;
				}
			}
		}
		return 0;
	}

	// need to decouple
	protected String getLabelIfNeedHighlighting(List<Object> objects)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		String returnValue = "";
		List<String> objectValues = new ArrayList<>();
		for (Object object : objects)
		{
			if (object instanceof CategoryAnalysisResult)
			{
				if (genesToHighLight
						.contains(((CategoryAnalysisResult) object).getCategoryDescription().toLowerCase()))
				{
					objectValues.add(((CategoryAnalysisResult) object).getCategoryDescription());
				}
			}
			else if (object instanceof IGeneContainer)
			{
				Set<String> genestohighlight = ((IGeneContainer) object).containsGenes(genesToHighLight);
				String appendage = "";
				if (object instanceof ProbeStatResult
						&& ((ProbeStatResult) object).getBestFoldChange() != null)
					appendage = ": Max FC=" + df.format(((ProbeStatResult) object).getBestFoldChange());
				if (genestohighlight.size() > 0)
					objectValues.add(String.join(",", genestohighlight) + appendage);
			}
		}
		return String.join(", ", objectValues);
	}

}
