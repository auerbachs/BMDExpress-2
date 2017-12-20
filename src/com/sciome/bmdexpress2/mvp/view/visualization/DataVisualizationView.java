package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.presenter.visualization.DataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.SciomeChartListener;
import com.sciome.filter.DataFilterPack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/*
 * A place to show visualizations. 
 * it needs some better design, but this helps get started with summary visualizations.
 */
public abstract class DataVisualizationView extends BMDExpressViewBase
		implements IDataVisualizationView, SciomeChartListener
{

	protected final static String				DEFAULT_CHARTS			= "Default";
	protected AnchorPane						graphViewAnchorPane;

	List<Node>									chartsList;

	protected DataVisualizationPresenter		presenter;

	private final int							MAX_DATASETS_TO_VIEW	= 7;
	protected List<BMDExpressAnalysisDataSet>	results;
	protected DataFilterPack					defaultDPack;

	private VBox								vBox;
	protected ComboBox<String>					cBox;
	protected Map<String, SciomeChartBase>		chartCache				= new HashMap<>();

	// a list of ids (data point labels) that should only be displayed
	// the idea is that a user can selected a subset of the current data set to show.
	// this can be null, in that case it will be ignored and all will be displayed.
	protected List<String>						selectedIds;
	private List<BMDExpressAnalysisDataSet>		originatingResults;

	public DataVisualizationView()
	{
		this(BMDExpressEventBus.getInstance());

	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public DataVisualizationView(BMDExpressEventBus eventBus)
	{
		super();
		init();
	}

	private void init()
	{
		vBox = new VBox();
		HBox h = new HBox();
		graphViewAnchorPane = new AnchorPane();
		vBox.getChildren().add(h);
		vBox.getChildren().add(graphViewAnchorPane);

		HBox h1 = new HBox();
		HBox h2 = new HBox();
		h1.setAlignment(Pos.CENTER_LEFT);
		h2.setAlignment(Pos.CENTER_RIGHT);
		cBox = new ComboBox<>();
		cBox.getItems().setAll(getCannedCharts());
		cBox.getSelectionModel().select(0);

		cBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{
				redrawCharts(defaultDPack, selectedIds);

			}

		});

		h2.getChildren().addAll(new Label("Select Graph View"), cBox);
		h.getChildren().addAll(h1, h2);

		HBox.setHgrow(h2, Priority.ALWAYS);
		h.setAlignment(Pos.CENTER_LEFT);
		VBox.setVgrow(graphViewAnchorPane, Priority.ALWAYS);

	}

	public Node getNode()
	{
		return vBox;
	}

	@Override
	public void closeWindow()
	{
		handle_Done();
	}

	public void handle_Done()
	{

	}

	protected void showCharts()
	{
		VBox vBox = generateGridOfNodes(chartsList, 3);
		if (chartsList.size() == 1)
		{
			vBox = new VBox();
			HBox hbox = new HBox();
			hbox.getChildren().add(chartsList.get(0));
			HBox.setHgrow(chartsList.get(0), Priority.ALWAYS);
			hbox.setMinWidth(800);
			vBox.getChildren().add(hbox);
		}

		ScrollPane sp = new ScrollPane();
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		sp.setContent(vBox);
		graphViewAnchorPane.getChildren().add(sp);
		AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp, 0.0);
		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
	}

	private VBox generateGridOfNodes(List<Node> chartNodes, int width)
	{
		VBox vBox = new VBox();
		vBox.setMaxHeight(1400);
		HBox hBox = new HBox();
		hBox.setMaxWidth(1000);
		int i = 0;
		for (Node chart : chartNodes)
		{
			if (i % width == 0 && i > 0)
			{
				vBox.getChildren().add(hBox);
				hBox = new HBox();
				hBox.setMaxWidth(1000);
			}
			chart.setStyle("-fx-border-color: black;" + "-fx-padding: 0 0 30 0;");
			hBox.getChildren().add(chart);
			hBox.setSpacing(10.0);
			i++;
		}
		vBox.setSpacing(10.0);
		if (hBox.getChildren().size() > 0)
		{
			vBox.getChildren().add(hBox);
		}

		return vBox;
	}

	@SuppressWarnings("rawtypes")
	private Dialog createChartDialog(Node chart)
	{
		// Create the custom dialog.
		Dialog dialog = new Dialog();

		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(graphViewAnchorPane.getScene().getWindow());

		ButtonType loginButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		dialog.getDialogPane().getScene().getStylesheets()
				.add(getClass().getResource("/fxml/application.css").toExternalForm());
		// Create the username and password labels and fields.
		AnchorPane aPane = new AnchorPane();

		dialog.setResizable(true);
		dialog.getDialogPane().setMinHeight(500.0);
		dialog.getDialogPane().setMinWidth(500.0);
		aPane.getChildren().add(chart);

		AnchorPane.setLeftAnchor(chart, 0.0);
		AnchorPane.setRightAnchor(chart, 0.0);
		AnchorPane.setTopAnchor(chart, 0.0);
		AnchorPane.setBottomAnchor(chart, 0.0);
		dialog.getDialogPane().setContent(aPane);
		return dialog;

	}

	@Override
	public void close()
	{

		if (chartCache != null)
			chartCache.clear();
		if (chartsList != null)
			this.chartsList.clear();
		if (results != null)
			this.results.clear();
		if (originatingResults != null)
			this.originatingResults.clear();
		if (presenter != null)
			presenter.destroy();

	}

	public abstract void redrawCharts(DataFilterPack dataFilterPack, List<String> selectedIds);

	public abstract List<String> getCannedCharts();

	public void initData(DataFilterPack dPack)
	{
		defaultDPack = dPack;

	}

	@SuppressWarnings("unused")
	private <T> T choiceDialogForDataSet(List<T> dataSet, String title)
	{

		ChoiceDialog<T> dialog = new ChoiceDialog<>(null, dataSet);
		dialog.setTitle(title);
		dialog.setHeaderText("Select one");

		// Traditional way to get the response value.
		Optional<T> result = dialog.showAndWait();
		if (result.isPresent())
		{
			return result.get();

		}

		return null;

	}

	private <T> List<T> customChoiceDialogForDataSet(List<T> dataSet, String title)
	{
		Dialog<List<T>> dialog = new Dialog<>();
		dialog.setTitle(title);

		// Set the button types.
		ButtonType doneButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(doneButtonType, ButtonType.CANCEL);
		dialog.setResizable(true);
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		ListView<T> listView = new ListView<T>();

		ObservableList<T> list = FXCollections.observableArrayList();

		list.addAll(dataSet);
		GridPane.setHgrow(listView, Priority.ALWAYS);
		listView.setItems(list);
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		grid.add(listView, 0, 0);

		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().setMinWidth(500.0);
		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton ->
		{
			if (dialogButton == doneButtonType)
			{
				return new ArrayList<>(listView.getSelectionModel().getSelectedItems());
			}
			return null;
		});

		Optional<List<T>> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();

		return null;

	}

	@Override
	public void drawResults(List<BMDExpressAnalysisDataSet> results)
	{
		this.results = results;
		this.originatingResults = new ArrayList<>();
		this.originatingResults.addAll(results);
		redrawCharts(defaultDPack, selectedIds);

	}

	@Override
	public void expand(SciomeChartBase chart)
	{
		chart.setOnMouseClicked(null);
		Dialog dialog = createChartDialog(chart);
		dialog.getDialogPane().setPrefWidth(cBox.getScene().getWidth());
		dialog.getDialogPane().setPrefHeight(cBox.getScene().getHeight());
		dialog.showAndWait();
		chart.chartMinimized();
		showCharts();
	}

}
