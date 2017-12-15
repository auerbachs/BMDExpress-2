package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.CheckListView;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.ProjectNavigationPresenter;
import com.sciome.bmdexpress2.mvp.view.bmdanalysis.BMDAnalysisView;
import com.sciome.bmdexpress2.mvp.view.categorization.CategorizationView;
import com.sciome.bmdexpress2.mvp.view.prefilter.OneWayANOVAView;
import com.sciome.bmdexpress2.mvp.view.prefilter.OriogenView;
import com.sciome.bmdexpress2.mvp.view.prefilter.WilliamsTrendView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IProjectNavigationView;
import com.sciome.bmdexpress2.service.ProjectNavigationService;
import com.sciome.bmdexpress2.serviceInterface.IProjectNavigationService;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.ViewUtilities;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

//treeViewContainer
public class ProjectNavigationView extends VBox implements IProjectNavigationView
{

	private final String									EXPRESSION_DATA		= "Expression Data";
	private final String									ONEWAY_DATA			= "One-way ANOVA";
	private final String									WILLIAMS_DATA		= "William's Trend";
	private final String									ORIOGEN_DATA		= "Oriogen";
	private final String									BENCHMARK_DATA		= "Benchmark Dose Analyses";
	private final String									CATEGORY_DATA		= "Functional Classifications";

	private Map<String, List<BMDExpressAnalysisDataSet>>	dataSetMap			= new HashMap<>();
	private ComboBox<String>								dataGroupCombo		= new ComboBox<>();
	private CheckListView<BMDExpressAnalysisDataSet>		analysisCheckList	= new CheckListView<>();

	ProjectNavigationPresenter								presenter;
	private boolean											clearingChecks		= false;

	public ProjectNavigationView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public ProjectNavigationView(BMDExpressEventBus eventBus)
	{
		super();
		IProjectNavigationService service = new ProjectNavigationService();
		presenter = new ProjectNavigationPresenter(this, service, eventBus);
		initialize();
	}

	public void initialize()
	{
		initializeDataSetMap();
		initializeDataGroupCombo();
		initializeAnalysisList();
		getChildren().add(dataGroupCombo);
		getChildren().add(analysisCheckList);
		VBox.setVgrow(analysisCheckList, Priority.ALWAYS);

		analysisCheckList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{

				if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.SECONDARY)
				{
					System.out.println(mouseEvent.getSource());

					List<BMDExpressAnalysisDataSet> selecteDataSets = getCheckedItems();

					if (selecteDataSets.size() == 1)
						dealWithRightClickOnTree(selecteDataSets.get(0), mouseEvent);
					else if (selecteDataSets.size() > 1)
						dealWithRightClickOnTreeMultiSelected(selecteDataSets, mouseEvent);

				}

			}
		});

	}

	/*
	 * method to handle right clicks on tree nodes.
	 */
	private void dealWithRightClickOnTree(BMDExpressAnalysisDataSet selectedItem, MouseEvent mouseEvent)
	{
		if (selectedItem instanceof DoseResponseExperiment)
		{
			showDoseExperimentContextMenu((DoseResponseExperiment) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
		else if (selectedItem instanceof OneWayANOVAResults)
		{
			showOneWayAnovaContextMenu((OneWayANOVAResults) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());

		}
		else if (selectedItem instanceof WilliamsTrendResults)
		{
			showWilliamsTrendContextMenu((WilliamsTrendResults) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());

		}
		else if (selectedItem instanceof OriogenResults)
		{
			showOriogenContextMenu((OriogenResults) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());

		}
		else if (selectedItem instanceof CategoryAnalysisResults)
		{
			showCategorizationContextMenu((CategoryAnalysisResults) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
		else if (selectedItem instanceof BMDResult)
		{
			showBMDAnalysisContextMenu((BMDResult) selectedItem).show(
					this.analysisCheckList.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
	}

	/*
	 * method to handle right clicks on tree nodes.
	 */
	private void dealWithRightClickOnTreeMultiSelected(
			@SuppressWarnings("rawtypes") List<BMDExpressAnalysisDataSet> selectedItems,
			MouseEvent mouseEvent)
	{
		showMultiSelectedContextMenu(selectedItems).show(this.analysisCheckList.getScene().getWindow(),
				mouseEvent.getScreenX(), mouseEvent.getScreenY());;
	}

	@Override
	public void clearNavigationTree()
	{
		presenter.clearMainDataView();
		initializeDataSetMap();
		analysisCheckList.getItems().clear();

	}

	/*
	 * handle navigation tree view selection change
	 */
	@SuppressWarnings("rawtypes")
	private void handle_navigationTreeViewSelection()
	{

		List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();
		BMDExpressAnalysisDataSet selectedItem = null;

		if (datasets.size() > 0)
			presenter.BMDExpressAnalysisDataSetSelected(datasets.get(0));

		if (datasets.size() == 1 && datasets.get(0) instanceof DoseResponseExperiment)
			presenter.doseResponseExperimentSelected((DoseResponseExperiment) datasets.get(0));
		else if (datasets.size() == 1)
			presenter.BMDExpressAnalysisDataSetSelected(datasets.get(0));
		else if (datasets.size() > 1 && datasets.get(0) instanceof DoseResponseExperiment)
			presenter.clearMainDataView(); // not combining dose response data
		else if (datasets.size() > 1)
			presenter.multipleDataSetsSelected(datasets);
		else
			presenter.clearMainDataView();

	}

	/*
	 * put the dose response data into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addDoseResponseExperiement(DoseResponseExperiment doseResponseExperiment, boolean selectIt)
	{
		addDataSetToList(EXPRESSION_DATA, doseResponseExperiment);

	}

	/*
	 * put the oneway result into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addOneWayANOVAAnalysis(OneWayANOVAResults oneWayANOVAResults, boolean selectIt)
	{
		addDataSetToList(ONEWAY_DATA, oneWayANOVAResults);
	}

	/*
	 * put the williams trend result into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addWilliamsTrendAnalysis(WilliamsTrendResults williamsTrendResults, boolean selectIt)
	{
		addDataSetToList(WILLIAMS_DATA, williamsTrendResults);

	}

	/*
	 * put the oriogen result into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addOriogenAnalysis(OriogenResults oriogenResults, boolean selectIt)
	{
		addDataSetToList(ORIOGEN_DATA, oriogenResults);

	}

	/*
	 * put the bmd result into the tree
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addBMDAnalysis(BMDResult bMDResult, boolean selectIt)
	{

		addDataSetToList(BENCHMARK_DATA, bMDResult);

	}

	/*
	 * put the category analysis into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addCategoryAnalysis(CategoryAnalysisResults categoryAnalysisResults, boolean selectIt)
	{

		addDataSetToList(CATEGORY_DATA, categoryAnalysisResults);

	}

	/*
	 * load the anova analysis view, pass it the dose response experiement data and start let user do stuff
	 * from there.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void performOneWayANOVA()
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();

				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (BMDExpressAnalysisDataSet selectedItem : datasets)
				{
					if (selectedItem instanceof IStatModelProcessable)
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}

				if (datasets.size() > 0)
				{
					// now create a list of doseResponseExperement objects so the oneway anova view can offer
					// a selection list.
					List<IStatModelProcessable> processableDatas = new ArrayList<>();
					for (BMDExpressAnalysisDataSet item : analysisCheckList.getItems())
						processableDatas.add((IStatModelProcessable) item);
					try
					{

						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/onewayanova.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("One Way ANOVA");
						stage.setScene(new Scene((BorderPane) loader.load()));
						OneWayANOVAView controller = loader.<OneWayANOVAView> getController();
						controller.initData(selectedItems, processableDatas);

						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event)
							{
								controller.close();
							}
						});
						stage.sizeToScene();
						stage.show();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public void performWilliamsTrend()
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();

				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (BMDExpressAnalysisDataSet selectedItem : datasets)
				{
					if (selectedItem instanceof IStatModelProcessable)
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}

				if (datasets.size() > 0)
				{
					// now create a list of doseResponseExperement objects so the oneway anova view can offer
					// a selection list.
					List<IStatModelProcessable> processableDatas = new ArrayList<>();
					for (BMDExpressAnalysisDataSet item : analysisCheckList.getItems())
						processableDatas.add((IStatModelProcessable) item);
					try
					{

						FXMLLoader loader = new FXMLLoader(
								getClass().getResource("/fxml/williamstrend.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("William's Trend");
						stage.setScene(new Scene((BorderPane) loader.load()));
						WilliamsTrendView controller = loader.<WilliamsTrendView> getController();
						controller.initData(selectedItems, processableDatas);

						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event)
							{
								controller.close();
							}
						});
						stage.sizeToScene();
						stage.show();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void performOriogen()
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();

				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (BMDExpressAnalysisDataSet selectedItem : datasets)
				{
					if (selectedItem instanceof IStatModelProcessable)
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}

				if (datasets.size() > 0)
				{
					// now create a list of doseResponseExperement objects so the oneway anova view can offer
					// a selection list.
					List<IStatModelProcessable> processableDatas = new ArrayList<>();
					for (BMDExpressAnalysisDataSet item : analysisCheckList.getItems())
						processableDatas.add((IStatModelProcessable) item);
					try
					{

						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/oriogen.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("Oriogen");
						stage.setScene(new Scene((BorderPane) loader.load()));
						OriogenView controller = loader.<OriogenView> getController();
						controller.initData(selectedItems, processableDatas);

						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event)
							{
								controller.close();
							}
						});
						stage.sizeToScene();
						stage.show();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	/*
	 * get the selected data set and start up a new view to perform bmd analysis
	 */
	@Override
	public void performBMDAnalysis()
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void run()
			{

				List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();

				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (BMDExpressAnalysisDataSet selectedItem : datasets)
				{
					if ((selectedItem instanceof IStatModelProcessable
							&& ((IStatModelProcessable) selectedItem).getProcessableProbeResponses() != null))
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}

				if (selectedItems.size() > 0)
				{
					try
					{
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bmdanalysis.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("BMD Analysis");
						stage.setScene(new Scene((BorderPane) loader.load()));
						BMDAnalysisView viewCode = loader.<BMDAnalysisView> getController();
						// The false means that we are not running "select models only mode"
						viewCode.initData(selectedItems, false);
						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event)
							{
								viewCode.close();
							}
						});
						stage.sizeToScene();
						stage.show();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

	}

	/*
	 * get the selected data set and start up a new view to perform category analysis
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void performCategoryAnalysis(CategoryAnalysisEnum catAnalysisType)
	{
		List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();
		List<BMDResult> selectedItems = new ArrayList<>();
		for (BMDExpressAnalysisDataSet selectedItem : datasets)
		{
			if (selectedItem instanceof BMDResult)
			{
				BMDResult processableData = (BMDResult) selectedItem;
				selectedItems.add(processableData);
			}
		}

		if (selectedItems.size() == 0)
		{
			return;
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run()
			{

				// currently the category analsyis request can be one of 3 views.
				String view = "category.fxml";

				try
				{
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + view));

					Stage stage = BMDExpressFXUtils.getInstance().generateStage("");
					if (catAnalysisType == CategoryAnalysisEnum.DEFINED)
					{
						stage.setTitle("Defined Category Analysis");
					}
					else if (catAnalysisType == CategoryAnalysisEnum.GO)
					{
						stage.setTitle("Gene Ontology Category Analysis");
					}
					else if (catAnalysisType == CategoryAnalysisEnum.PATHWAY)
					{
						stage.setTitle("Signaling Pathway Analysis");
					}
					else
					{
						stage.setTitle("Category Analysis");
					}

					stage.setScene(new Scene((BorderPane) loader.load()));
					CategorizationView viewCode = loader.<CategorizationView> getController();
					viewCode.initData(selectedItems, catAnalysisType);
					stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent event)
						{
							viewCode.close();
						}
					});
					stage.sizeToScene();
					stage.show();

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		});
	}

	/*
	 * Given list of choices, present the user with them and let them choose. then let the presenter assign
	 * the chip choice to the doseresponsexperiment.
	 */
	@Override
	public void getAChip(List<ChipInfo> choices, List<DoseResponseExperiment> doseResponseExperiment,
			FileAnnotation fileAnnotation)
	{

		Collections.sort(choices, new Comparator<ChipInfo>() {

			@Override
			public int compare(ChipInfo o1, ChipInfo o2)
			{
				return (o1.getProvider() + o1.getGeoName().toLowerCase())
						.compareTo(o2.getProvider() + o2.getGeoName().toLowerCase());
			}
		});
		ChipInfo genericChip = new ChipInfo("Generic");
		genericChip.setId("generic");
		genericChip.setProvider("generic");
		genericChip.setSpecies("generic");
		choices.add(genericChip);
		ChoiceDialog<ChipInfo> annationDialog = new ChoiceDialog<>(choices.get(0), choices);

		annationDialog.setTitle("Choose");
		annationDialog.setGraphic(null);
		annationDialog.setHeaderText("Platform chooser");
		annationDialog.setContentText("Choose a platform");
		annationDialog.initOwner(analysisCheckList.getScene().getWindow());
		annationDialog.initModality(Modality.WINDOW_MODAL);
		Optional<ChipInfo> myvalue = annationDialog.showAndWait();

		try
		{
			ChoiceDialog<LogTransformationEnum> logTransFormationDialog = new ChoiceDialog<>(
					LogTransformationEnum.BASE2, LogTransformationEnum.values());

			logTransFormationDialog.setTitle("How is your data transformed?");
			logTransFormationDialog.setGraphic(null);
			logTransFormationDialog.setHeaderText("Log Transformation chooser");
			logTransFormationDialog.setContentText("Choose a Log Transformation");
			logTransFormationDialog.initOwner(analysisCheckList.getScene().getWindow());
			logTransFormationDialog.initModality(Modality.WINDOW_MODAL);
			Optional<LogTransformationEnum> logtransform = logTransFormationDialog.showAndWait();

			LogTransformationEnum lt = LogTransformationEnum.BASE2;;
			if (logtransform.isPresent())
				lt = logtransform.get();

			for (DoseResponseExperiment de : doseResponseExperiment)
				de.setLogTransformation(lt);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (myvalue.isPresent())
			presenter.assignArrayAnnotations(myvalue.get(), doseResponseExperiment, fileAnnotation);
		else
			presenter.assignArrayAnnotations(null, doseResponseExperiment, fileAnnotation);

	}

	/*
	 * set up contextmenus per tree node type.
	 */
	private ContextMenu showOneWayAnovaContextMenu(OneWayANOVAResults oneWayResult)
	{
		ContextMenu ctxMenu = new ContextMenu();
		ctxMenu.getItems().addAll(getCommonMenuItems());

		setContextMenuCommonHandlers("One Way ANOVA", ctxMenu, oneWayResult);
		return ctxMenu;
	}

	private ContextMenu showWilliamsTrendContextMenu(WilliamsTrendResults williamsTrendResult)
	{
		ContextMenu ctxMenu = new ContextMenu();
		ctxMenu.getItems().addAll(getCommonMenuItems());

		setContextMenuCommonHandlers("William's Trend", ctxMenu, williamsTrendResult);
		return ctxMenu;
	}

	private ContextMenu showOriogenContextMenu(OriogenResults oriogenResult)
	{
		ContextMenu ctxMenu = new ContextMenu();
		ctxMenu.getItems().addAll(getCommonMenuItems());

		setContextMenuCommonHandlers("Oriogen", ctxMenu, oriogenResult);
		return ctxMenu;
	}

	private ContextMenu showCategorizationContextMenu(CategoryAnalysisResults categoryAnalysisResult)
	{
		ContextMenu ctxMenu = new ContextMenu();
		ctxMenu.getItems().addAll(getCommonMenuItems());

		setContextMenuCommonHandlers("Category Analysis", ctxMenu, categoryAnalysisResult);
		return ctxMenu;

	}

	private ContextMenu showBMDAnalysisContextMenu(BMDResult bmdResults)
	{
		ContextMenu ctxMenu = new ContextMenu();

		boolean hasHill = false;
		if (bmdResults.getProbeStatResults() != null && bmdResults.getProbeStatResults().size() > 0)
			for (StatResult statResult : bmdResults.getProbeStatResults().get(0).getStatResults())
				if (statResult instanceof HillResult)
					hasHill = true;

		if (hasHill)
		{
			MenuItem reselectBestModelMenuItem = new MenuItem("Re-select Best models");
			reselectBestModelMenuItem.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event)
				{
					handle_BMDResultReselectBestModels(bmdResults);
				}
			});
			ctxMenu.getItems().add(reselectBestModelMenuItem);
		}

		ctxMenu.getItems().addAll(getCommonMenuItems());
		setContextMenuCommonHandlers("BMD Analysis", ctxMenu, bmdResults);

		MenuItem exportBestModelsMenuItem = new MenuItem("Export Best Models");
		exportBestModelsMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				handle_BMDResultExportBestModels(bmdResults);
			}
		});

		ctxMenu.getItems().add(exportBestModelsMenuItem);
		return ctxMenu;

	}

	/*
	 * deal with handling common menu items for BMDAnalysisDataSet objects
	 */
	private void setContextMenuCommonHandlers(String theThing, ContextMenu ctxMenu,
			BMDExpressAnalysisDataSet analysisDataSet)
	{
		ctxMenu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				switch (((MenuItem) event.getTarget()).getText())
				{
					case "Rename":
						handle_BMDExpressAnalysisDataSetRename(analysisDataSet, "Rename " + theThing);
						break;
					case "Remove":
						handle_BMDExpressAnalysisDataSetRemove(analysisDataSet);
						break;
					case "Export":
						handle_BMDExpressAnalysisDataSetExport(analysisDataSet, "Export " + theThing);
						break;
					case "Spreadsheet View":
						handle_DataAnalysisResultsSpreadSheetView(analysisDataSet);
						break;
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private ContextMenu showMultiSelectedContextMenu(List<BMDExpressAnalysisDataSet> selectedItems)
	{
		ContextMenu ctxMenu = new ContextMenu();

		ctxMenu.getItems().addAll(getCommonMultiSelectMenuItems());

		ctxMenu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				switch (((MenuItem) event.getTarget()).getText())
				{

					case "Remove All Checked Items":
						handle_MultiSelectRemove(selectedItems);
						break;
					case "Export":
						handle_MultiSelectExport(selectedItems);
						break;
				}

			}
		});

		return ctxMenu;
	}

	private ContextMenu showDoseExperimentContextMenu(DoseResponseExperiment doseResponseExperiment)
	{
		ContextMenu ctxMenu = new ContextMenu();

		Menu showAnnotationMenu = new Menu("Show Annotations");
		MenuItem probeToGenesMenuItem = new MenuItem("Probe to Genes");
		MenuItem genesToProbeMenuItem = new MenuItem("Genes to Probe");
		showAnnotationMenu.getItems().add(probeToGenesMenuItem);
		showAnnotationMenu.getItems().add(genesToProbeMenuItem);
		ctxMenu.getItems().add(showAnnotationMenu);
		ctxMenu.getItems().addAll(getCommonMenuItems());

		ctxMenu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				switch (((MenuItem) event.getTarget()).getText())
				{
					case "Rename":
						handle_DoseResponseExperimentRename(doseResponseExperiment);
						break;
					case "Remove":
						handle_DoseResponseExperimentRemove(doseResponseExperiment);
						break;
					case "Export":
						handle_DoseResponseExperimentExport(doseResponseExperiment);
						break;
					case "Spreadsheet View":
						handle_DoseResponseExperimentSpreadSheetView(doseResponseExperiment);
						break;
				}

			}
		});

		probeToGenesMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				handle_DoseResponseViewProbeToGenes(doseResponseExperiment);

			}
		});

		genesToProbeMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				handle_DoseResponseViewGenesToProbe(doseResponseExperiment);

			}
		});
		return ctxMenu;
	}

	private List<MenuItem> getCommonMenuItems()
	{
		List<MenuItem> menuItems = new ArrayList<>();
		menuItems.add(new MenuItem("Rename"));
		menuItems.add(new MenuItem("Remove"));
		menuItems.add(new MenuItem("Export"));
		menuItems.add(new MenuItem("Spreadsheet View"));

		return menuItems;
	}

	private List<MenuItem> getCommonMultiSelectMenuItems()
	{
		List<MenuItem> menuItems = new ArrayList<>();
		menuItems.add(new MenuItem("Remove All Checked Items"));
		menuItems.add(new MenuItem("Export"));

		return menuItems;
	}

	@SuppressWarnings("rawtypes")
	private void handle_BMDExpressAnalysisDataSetRemove(BMDExpressAnalysisDataSet analysisDataSet)
	{
		if (!showAlert("Remove Result", "Remove", "Are you sure you want to remove this result?"))
			return;

		presenter.removeBMDExpressAnalysisDataSetFromProject(analysisDataSet);

		removeItem(analysisDataSet);
		refreshAnalysisList(dataGroupCombo.getValue());

	}

	private void handle_BMDExpressAnalysisDataSetRename(BMDExpressAnalysisDataSet catAnalysisResults,
			String title)
	{
		String newName = textInputDialog(catAnalysisResults.toString(), title, "Rename",
				"Enter the new name.");

		if (newName == null)
			return;

		catAnalysisResults.setName(newName);
		analysisCheckList.refresh();

	}

	/*
	 * handle doseresponseexperiment menu items.
	 */
	private void handle_DoseResponseExperimentExport(DoseResponseExperiment doseResponseExperiment)
	{
		File selectedFile = getFileToSave(
				"Export Dose Response Experiment " + doseResponseExperiment.toString(),
				doseResponseExperiment.toString() + ".txt");
		if (selectedFile == null)
			return;

		presenter.exportDoseResponseExperiment(doseResponseExperiment, selectedFile);
	}

	private void handle_DoseResponseExperimentRemove(DoseResponseExperiment doseResponseExperiment)
	{
		if (!showAlert("Remove Dose Response Experiment Confirmation", "Remove",
				"Are you sure you want to remove this result?"))
			return;

		presenter.removeDoseResponseExperimentFromProject(doseResponseExperiment);
		removeItem(doseResponseExperiment);
		refreshAnalysisList(dataGroupCombo.getValue());
	}

	private void handle_DoseResponseExperimentRename(DoseResponseExperiment doseResponseExperiment)
	{
		String newName = textInputDialog(doseResponseExperiment.toString(), "Rename BMD Result", "Rename",
				"Enter the new name.");

		if (newName == null)
			return;

		doseResponseExperiment.setName(newName);
		analysisCheckList.refresh();
	}

	private void handle_DoseResponseViewGenesToProbe(DoseResponseExperiment doseResponseExperiment)
	{
		presenter.showGenesToProbeMatrix(doseResponseExperiment);
	}

	private void handle_DoseResponseViewProbeToGenes(DoseResponseExperiment doseResponseExperiment)
	{
		presenter.showProbeToGeneMatrix(doseResponseExperiment);
	}

	private void handle_DoseResponseExperimentSpreadSheetView(DoseResponseExperiment results)
	{
		presenter.handle_DoseResponseExperimentSpreadSheetView(results);

	}

	private void handle_DataAnalysisResultsSpreadSheetView(BMDExpressAnalysisDataSet results)
	{
		presenter.handle_DataAnalysisResultsSpreadSheetView(results);

	}

	/*
	 * handle exporting analysis data
	 */
	private void handle_BMDExpressAnalysisDataSetExport(BMDExpressAnalysisDataSet bmdResults,
			String saveAsTitle)
	{
		File selectedFile = getFileToSave(saveAsTitle + " " + bmdResults.toString(),
				bmdResults.toString() + ".txt");
		if (selectedFile == null)
			return;

		presenter.exportBMDExpressAnalysisDataSet(bmdResults, selectedFile);
	}

	private void handle_BMDResultReselectBestModels(BMDResult bmdResults)
	{

		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void run()
			{
				List<BMDExpressAnalysisDataSet> datasets = getCheckedItems();

				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (BMDExpressAnalysisDataSet selectedItem : datasets)
				{
					if ((selectedItem instanceof IStatModelProcessable
							&& ((IStatModelProcessable) selectedItem).getProcessableProbeResponses() != null))
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}

				if (selectedItems.size() > 0)
				{
					try
					{
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bmdanalysis.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("");
						stage.setScene(new Scene((BorderPane) loader.load()));

						BMDAnalysisView viewCode = loader.<BMDAnalysisView> getController();
						// The true means that we are not running "select models only mode"
						viewCode.initData(selectedItems, true);
						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event)
							{
								viewCode.close();
							}
						});
						stage.sizeToScene();
						stage.show();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

	}

	private void handle_BMDResultExportBestModels(BMDResult bmdResults)
	{
		File selectedFile = getFileToSave("Export Best Models " + bmdResults.toString(),
				"BestModels_" + bmdResults.toString() + ".txt");
		if (selectedFile == null)
			return;

		presenter.exportBMDResultBestModel(bmdResults, selectedFile);
	}

	@SuppressWarnings("rawtypes")
	private void handle_MultiSelectRemove(List<BMDExpressAnalysisDataSet> selectedItems)
	{

		if (!showAlert("Remove Multiple Items Confirmation", "Remove",
				"Are you sure you want to remove these results?"))
			return;

		refreshAnalysisList(dataGroupCombo.getValue());
		for (BMDExpressAnalysisDataSet selectedItem : selectedItems)
			removeItem(selectedItem);

		presenter.clearMainDataView();
	}

	private void removeItem(BMDExpressAnalysisDataSet selectedItem)
	{

		removeFromDataSetMap(selectedItem);
		if (selectedItem instanceof DoseResponseExperiment)
		{
			presenter.removeDoseResponseExperimentFromProject((DoseResponseExperiment) selectedItem);
			analysisCheckList.getItems().remove(selectedItem);
		}
		else
		{
			presenter.removeBMDExpressAnalysisDataSetFromProject(selectedItem);
			analysisCheckList.getItems().remove(selectedItem);
		}

	}

	private void clearChecks(BMDExpressAnalysisDataSet selectedItem)
	{
		// so the handler doesn't fire off events while clearing.
		clearingChecks = true;
		if (selectedItem == null)
			analysisCheckList.getCheckModel().clearChecks();
		else
			analysisCheckList.getCheckModel().clearCheck(selectedItem);
		clearingChecks = false;
	}

	@SuppressWarnings("rawtypes")
	private void handle_MultiSelectExport(List<BMDExpressAnalysisDataSet> selectedItems)
	{
		File selectedFile = getFileToSave("Export Multi Selected Results", "multiselect_results.txt");
		if (selectedFile == null)
			return;

		presenter.exportMultipleResults(selectedItems, selectedFile);
	}

	/*
	 * generate a FileChooser for exporting data.
	 */
	private File getFileToSave(String title, String initName)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		File initialDirectory = new File(BMDExpressProperties.getInstance().getExportPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setInitialFileName(initName);
		File selectedFile = fileChooser.showSaveDialog(analysisCheckList.getScene().getWindow());

		if (selectedFile != null)
		{
			BMDExpressProperties.getInstance().setExportPath(selectedFile.getParent());
		}
		return selectedFile;
	}

	/*
	 * generate an alert and return true or false.
	 */
	private boolean showAlert(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.initOwner(analysisCheckList.getScene().getWindow());
		alert.initModality(Modality.WINDOW_MODAL);
		Optional<ButtonType> result = alert.showAndWait();
		((Stage) analysisCheckList.getScene().getWindow()).toFront();
		if (result.get() == ButtonType.OK)
		{
			return true;
		}

		return false;
	}

	/*
	 * generate text input dialog
	 */
	private String textInputDialog(String initText, String title, String header, String content)
	{
		TextInputDialog dialog = new TextInputDialog(initText);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		dialog.initOwner(analysisCheckList.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.getDialogPane().setMinWidth(500);
		dialog.setResizable(true);
		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		((Stage) analysisCheckList.getScene().getWindow()).toFront();
		if (result.isPresent())
		{
			return result.get();
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void expandTree()
	{

	}

	@Override
	public int askToSaveBeforeClose()
	{

		Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to save the current project first?",
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.initOwner(this.getWindow());
		alert.initModality(Modality.WINDOW_MODAL);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES)
		{
			return 1;
		}
		if (result.isPresent() && result.get() == ButtonType.CANCEL)
		{
			return -1;
		}

		return 0;

	}

	@Override
	public File askForAProjectFile()
	{
		return ViewUtilities.getInstance().getSaveAsFile(analysisCheckList.getScene().getWindow());
	}

	@Override
	public File askForAProjectFileToOpen()
	{
		return ViewUtilities.getInstance().getOpenProjectFile(analysisCheckList.getScene().getWindow());
	}

	@Override
	public File askForABMDFileToImport()
	{
		return ViewUtilities.getInstance().getBMDImportFileToImport(analysisCheckList.getScene().getWindow());
	}

	@Override
	public File askForAJSONFileToImport()
	{
		return ViewUtilities.getInstance()
				.getJSONImportFileToImport(analysisCheckList.getScene().getWindow());
	}

	@Override
	public void showMatrixPreview(String header, MatrixData matrixData)
	{
		ViewUtilities.getInstance().matrixPreviewStage("Spreadsheet View", header, matrixData);

	}

	@Override
	public void setWindowSizeProperties()
	{
		Stage stage = (Stage) analysisCheckList.getScene().getWindow();
		BMDExpressProperties.getInstance().setSizeY((int) stage.getHeight());
		BMDExpressProperties.getInstance().setSizeX((int) stage.getWidth());
		BMDExpressProperties.getInstance().setLocX((int) stage.getX());
		BMDExpressProperties.getInstance().setLocY((int) stage.getY());
		BMDExpressProperties.getInstance().save();

	}

	public Window getWindow()
	{
		return analysisCheckList.getScene().getWindow();
	}

	private void initializeDataSetMap()
	{
		clearChecks(null);
		analysisCheckList.getItems().clear();
		dataSetMap.put(EXPRESSION_DATA, new ArrayList<>());
		dataSetMap.put(ONEWAY_DATA, new ArrayList<>());
		dataSetMap.put(WILLIAMS_DATA, new ArrayList<>());
		dataSetMap.put(ORIOGEN_DATA, new ArrayList<>());
		dataSetMap.put(BENCHMARK_DATA, new ArrayList<>());
		dataSetMap.put(CATEGORY_DATA, new ArrayList<>());
	}

	private void refreshAnalysisList(String forDataGroup)
	{
		clearChecks(null);
		analysisCheckList.getItems().clear();
		analysisCheckList.getItems().addAll(new ArrayList<>(dataSetMap.get(forDataGroup)));
		analysisCheckList.refresh();
		presenter.clearMainDataView();
	}

	private void initializeDataGroupCombo()
	{
		dataGroupCombo.getItems().addAll(Arrays.asList(EXPRESSION_DATA, ONEWAY_DATA, WILLIAMS_DATA,
				ORIOGEN_DATA, BENCHMARK_DATA, CATEGORY_DATA));

		dataGroupCombo.setValue(EXPRESSION_DATA);

		dataGroupCombo.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{
				refreshAnalysisList(oldValue);
				presenter.clearMainDataView();
				refreshAnalysisList(newValue);

			}
		});
	}

	private void initializeAnalysisList()
	{

		analysisCheckList.getCheckModel().getCheckedItems()
				.addListener(new ListChangeListener<BMDExpressAnalysisDataSet>() {
					public void onChanged(ListChangeListener.Change<? extends BMDExpressAnalysisDataSet> c)
					{
						if (!clearingChecks)
							handle_navigationTreeViewSelection();
					}
				});
	}

	private void addDataSetToList(String group, BMDExpressAnalysisDataSet dataset)
	{
		dataSetMap.get(group).add(dataset);

		if (dataGroupCombo.getValue().equals(group))
			refreshAnalysisList(group);
	}

	private void removeFromDataSetMap(BMDExpressAnalysisDataSet selectedItem)
	{
		if (selectedItem instanceof DoseResponseExperiment)
			dataSetMap.get(EXPRESSION_DATA).remove(selectedItem);
		else if (selectedItem instanceof OneWayANOVAResults)
			dataSetMap.get(ONEWAY_DATA).remove(selectedItem);
		else if (selectedItem instanceof WilliamsTrendResults)
			dataSetMap.get(WILLIAMS_DATA).remove(selectedItem);
		else if (selectedItem instanceof OriogenResults)
			dataSetMap.get(ORIOGEN_DATA).remove(selectedItem);
		else if (selectedItem instanceof BMDResult)
			dataSetMap.get(BENCHMARK_DATA).remove(selectedItem);
		else if (selectedItem instanceof CategoryAnalysisResults)
			dataSetMap.get(CATEGORY_DATA).remove(selectedItem);

	}

	private List<BMDExpressAnalysisDataSet> getCheckedItems()
	{
		List<BMDExpressAnalysisDataSet> datasets = new ArrayList<>();

		for (BMDExpressAnalysisDataSet ds : analysisCheckList.getCheckModel().getCheckedItems())
			if (ds != null)
				datasets.add(ds);

		return datasets;
	}

}
