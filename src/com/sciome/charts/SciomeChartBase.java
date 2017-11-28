package com.sciome.charts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;
import com.sciome.charts.model.SciomeSeries;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Callback;

/*
 * 
 */
public abstract class SciomeChartBase<X, Y> extends StackPane
{
	protected String					title;
	protected SciomeChartListener		chartListener;
	private List<ChartDataPack>			chartDataPacks;
	private int							maxGraphItems				= 2000000;
	protected boolean					cancel						= false;
	private Node						chart;
	protected CheckBox					logXAxis					= new CheckBox("Log X Axis");
	protected CheckBox					logYAxis					= new CheckBox("Log Y Axis");
	protected CheckBox					lockXAxis					= new CheckBox("Lock X Axis");
	protected CheckBox					lockYAxis					= new CheckBox("Lock Y Axis");
	protected Label						warningTooManyNodesLabel	= new Label(
			"There are too many data points to show all.  Please use the slider to scroll through your data.");
	private VBox						vBox;

	private Button						exportToTextButton;
	private Button						maxMinButton;
	private Button						configurationButton;
	private HBox						checkBoxes;
	private String[]					chartableKeys;
	private ChartConfiguration			chartConfiguration;

	private List<SciomeSeries<X, Y>>	seriesData					= new ArrayList<>();

	public SciomeChartBase(String title, List<ChartDataPack> chartDataPacks, String[] keys,
			SciomeChartListener chartListener)
	{
		this.title = title;
		this.chartableKeys = keys;
		this.chartListener = chartListener;
		this.chartDataPacks = chartDataPacks;

		this.convertChartDataPacksToSciomeSeries(chartableKeys, chartDataPacks);

		vBox = new VBox();
		maxMinButton = GlyphsDude.createIconButton(FontAwesomeIcon.EXPAND);
		maxMinButton.setTooltip(new Tooltip("View this chart in a large separate window."));
		configurationButton = GlyphsDude.createIconButton(FontAwesomeIcon.GEAR);
		configurationButton.setTooltip(new Tooltip("Configure the X and Y axis of this chart."));
		exportToTextButton = GlyphsDude.createIconButton(FontAwesomeIcon.DOWNLOAD);
		exportToTextButton.setTooltip(new Tooltip("Click to download textual representation of this chart."));

		exportToTextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				performDownload();

			}

		});

		HBox overlayButtons = new HBox();

		maxMinButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				maxMinButton.setVisible(false);
				chartListener.expand(SciomeChartBase.this);

			}
		});

		configurationButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				showConfiguration();
			}

		});

		// only show the export to text button if this instance is
		// implementing the ChartDataExporter
		if (this instanceof ChartDataExporter)
			overlayButtons.getChildren().addAll(exportToTextButton);

		overlayButtons.getChildren().addAll(configurationButton, maxMinButton);
		overlayButtons.setAlignment(Pos.TOP_RIGHT);
		this.getChildren().addAll(overlayButtons, vBox);
		StackPane.setAlignment(overlayButtons, Pos.TOP_RIGHT);
		this.setPickOnBounds(false);
		vBox.setSpacing(5.0);
		StackPane.setMargin(vBox, new Insets(25.0, 5.0, 5.0, 5.0));
		warningTooManyNodesLabel.setWrapText(true);
	}

	protected void showTooManyNodes(boolean b)
	{
		if (b && !vBox.getChildren().contains(warningTooManyNodesLabel))
			vBox.getChildren().add(0, warningTooManyNodesLabel);
		else if (!b)
			vBox.getChildren().remove(warningTooManyNodesLabel);

	}

	protected void showLogAxes(boolean allowXLogAxis, boolean allowYLogAxis, boolean allowLockXAxis,
			boolean allowLockYAxis)
	{
		showLogAxes(allowXLogAxis, allowYLogAxis, allowLockXAxis, allowLockYAxis, new ArrayList<>());
	}

	@SuppressWarnings("restriction")
	protected void showLogAxes(boolean allowXLogAxis, boolean allowYLogAxis, boolean allowLockXAxis,
			boolean allowLockYAxis, List<CheckBox> otherComponents)
	{

		vBox.getChildren().remove(checkBoxes);
		checkBoxes = new HBox();
		checkBoxes.setSpacing(10.0);
		int insertIndex = 0;
		if (vBox.getChildren().contains(warningTooManyNodesLabel))
			insertIndex = 1;
		if (allowXLogAxis)
			checkBoxes.getChildren().addAll(logXAxis);

		if (allowYLogAxis)
			checkBoxes.getChildren().addAll(logYAxis);
		if (allowLockXAxis)
			checkBoxes.getChildren().addAll(lockXAxis);

		if (allowLockYAxis)
			checkBoxes.getChildren().addAll(lockYAxis);

		checkBoxes.getChildren().addAll(otherComponents);

		vBox.getChildren().add(insertIndex, checkBoxes);

	}

	@SuppressWarnings("restriction")
	protected void showChart(Label caption2)
	{
		vBox.getChildren().remove(chart);
		chart = generateChart(this.chartableKeys, chartConfiguration);
		int insertIndex = 0;
		if (vBox.getChildren().contains(warningTooManyNodesLabel))
			insertIndex++;

		if (vBox.getChildren().contains(checkBoxes))
			insertIndex++;
		if (vBox.getChildren().size() > 0 && vBox.getChildren().get(0) instanceof CheckBox)
			insertIndex++;

		vBox.getChildren().add(insertIndex, chart);
		if (caption2 != null)
			vBox.getChildren().add(caption2);

		VBox.setVgrow(chart, Priority.ALWAYS);

	}

	protected void showChart()
	{

		showChart(null);
	}

	protected boolean componentIsVisible(Node showAllCheckBox2)
	{
		if (vBox.getChildren().contains(showAllCheckBox2))
			return true;
		return false;
	}

	protected void addComponentToTop(Node node)
	{
		if (!vBox.getChildren().contains(node))
			vBox.getChildren().add(0, node);

	}

	protected void removeComponent(Node node)
	{
		vBox.getChildren().remove(node);

	}

	protected void addComponentToEnd(Node node)
	{
		if (!vBox.getChildren().contains(node))
			vBox.getChildren().add(node);

	}

	/*
	 * get the minimum of all the mins amongst all data packs associated with the supplied key
	 */
	protected Double getMinMin(String key)
	{
		Double min = 9999999999.0;

		for (ChartDataPack pack : chartDataPacks)
		{
			Double currMin = pack.getChartStatMap().get(key).getMin();
			if (currMin == null)
				continue;

			if (currMin < min)
			{
				min = currMin;
			}
		}
		return min;
	}

	/*
	 * get max of maximums amongst data packs associated with key
	 */
	protected Double getMaxMax(String key)
	{

		Double max = -9999999999.0;

		for (ChartDataPack pack : chartDataPacks)
		{
			Double currMax = pack.getChartStatMap().get(key).getMax();
			if (currMax == null)
				continue;

			if (currMax > max)
			{
				max = currMax;
			}
		}
		return max;
	}

	public int getMaxGraphItems()
	{
		// max items will be a function of the number of series being displayed.
		int seriescount = chartDataPacks.size();
		if (seriescount == 0)
			seriescount = 1;
		return maxGraphItems / seriescount;
	}

	public void setMaxGraphItems(int maxGraphItems)
	{
		this.maxGraphItems = maxGraphItems;
	}

	public void cancel()
	{
		cancel = true;
	}

	public void redrawCharts(List<ChartDataPack> chartDataPacks)
	{

		this.chartDataPacks = chartDataPacks;
		// recreate the sciome series
		if (this instanceof SciomeAccumulationPlot)
			System.out.println();
		this.convertChartDataPacksToSciomeSeries(chartableKeys, chartDataPacks);
		showChart();

	}

	protected void showObjectText(String value)
	{
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Contents of Node");
		dialog.setHeaderText("Node Contents");
		dialog.setResizable(true);
		dialog.initOwner(this.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);
		TextArea tf = new TextArea(value);
		tf.setEditable(false);
		tf.setMinHeight(400);

		dialog.getDialogPane().setContent(tf);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b)
			{

				if (b == buttonTypeOk)
				{

					return "";
				}

				return null;
			}
		});

		dialog.getDialogPane().setPrefSize(800, 600);
		dialog.getDialogPane().autosize();
		dialog.showAndWait();

	}

	public void chartMinimized()
	{
		this.maxMinButton.setVisible(true);
	}

	protected Node getChart()
	{
		return chart;
	}

	protected List<SciomeSeries<X, Y>> getSeriesData()
	{
		return seriesData;
	}

	protected void setSeriesData(List<SciomeSeries<X, Y>> sd)
	{
		seriesData = sd;
	}

	protected List<ChartDataPack> getChartDataPacks()
	{
		return chartDataPacks;
	}

	protected abstract Node generateChart(String[] keys, ChartConfiguration chartConfiguration);

	protected abstract boolean isXAxisDefineable();

	protected abstract boolean isYAxisDefineable();

	// The implementing chart will implement this method to redraw itself
	protected abstract void redrawChart();

	// the subclass needs to conver the charted data packs to a sciome series.
	// then the actual chart implementation can use the SciomeSeries and SciomeData
	// to construct the chart
	protected abstract void convertChartDataPacksToSciomeSeries(String[] keys,
			List<ChartDataPack> chartPacks);

	// show the configuration to the user.
	private void showConfiguration()
	{
		Dialog<ChartConfiguration> dialog = new Dialog<>();
		dialog.setTitle("Chart Configuration");
		dialog.setResizable(true);
		dialog.initOwner(this.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);
		TextField minXTF = new TextField();
		minXTF.setMaxWidth(100.0);
		TextField maxXTF = new TextField();
		maxXTF.setMaxWidth(100.0);
		TextField minYTF = new TextField();
		minYTF.setMaxWidth(100.0);
		TextField maxYTF = new TextField();
		maxYTF.setMaxWidth(100.0);

		if (chartConfiguration != null && chartConfiguration.getMinX() != null)
			minXTF.setText(chartConfiguration.getMinX().toString());
		if (chartConfiguration != null && chartConfiguration.getMinY() != null)
			minYTF.setText(chartConfiguration.getMinY().toString());
		if (chartConfiguration != null && chartConfiguration.getMaxX() != null)
			maxXTF.setText(chartConfiguration.getMaxX().toString());
		if (chartConfiguration != null && chartConfiguration.getMaxY() != null)
			maxYTF.setText(chartConfiguration.getMaxY().toString());
		VBox vb = new VBox();
		vb.setSpacing(20.0);
		HBox hb1 = new HBox();
		hb1.setAlignment(Pos.CENTER_LEFT);
		hb1.setSpacing(10.0);

		HBox hb2 = new HBox();
		hb2.setAlignment(Pos.CENTER_LEFT);
		hb2.setSpacing(10.0);
		hb1.getChildren().addAll(new Label("X Axis Range"), minXTF, maxXTF);
		hb2.getChildren().addAll(new Label("Y Axis Range"), minYTF, maxYTF);
		if (isXAxisDefineable())
			vb.getChildren().addAll(hb1);
		if (isYAxisDefineable())
			vb.getChildren().addAll(hb2);

		if (vb.getChildren().size() == 0)
			vb.getChildren().add(new Label("No configurable options available for this chart."));

		dialog.getDialogPane().setContent(vb);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

		dialog.setResultConverter(new Callback<ButtonType, ChartConfiguration>() {
			@Override
			public ChartConfiguration call(ButtonType b)
			{

				if (b == buttonTypeOk)
				{
					ChartConfiguration config = new ChartConfiguration();

					try
					{
						config.setMaxX(Double.valueOf(maxXTF.getText()));
					}
					catch (Exception e)
					{}
					try
					{
						config.setMinX(Double.valueOf(minXTF.getText()));
					}
					catch (Exception e)
					{}
					try
					{
						config.setMaxY(Double.valueOf(maxYTF.getText()));
					}
					catch (Exception e)
					{}
					try
					{
						config.setMinY(Double.valueOf(minYTF.getText()));
					}
					catch (Exception e)
					{}
					return config;
				}

				return null;
			}
		});

		dialog.getDialogPane().setPrefSize(400, 400);
		dialog.getDialogPane().autosize();
		Optional<ChartConfiguration> value = dialog.showAndWait();

		if (value.isPresent())
		{
			this.chartConfiguration = value.get();
			redrawChart();
		}

	}

	/*
	 * perform data export of the chart first we need to make sure that this implements the ChartDataExporter
	 * interface. if so, get the lines and send to a file.
	 */
	private void performDownload()
	{
		if (!(this instanceof ChartDataExporter))
			return;

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		File initialDirectory = new File(BMDExpressProperties.getInstance().getExportPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setInitialFileName("chartdataExport.txt");
		File selectedFile = fileChooser.showSaveDialog(this.getScene().getWindow());

		if (selectedFile == null)
			return;

		List<String> linesToExport = ((ChartDataExporter) this).getLinesToExport();

		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", linesToExport));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * store information about a data point that is "extra" this information has the purpose of being used to
	 * sort the data.
	 */
	protected class ChartExtraValue
	{
		public String	label;
		public Integer	count;
		public Object	userData;

		public ChartExtraValue(String l, Integer c, Object u)
		{
			label = l;
			count = c;
			userData = u;
		}

		@Override
		public String toString()
		{
			return label;
		}

	}

}
