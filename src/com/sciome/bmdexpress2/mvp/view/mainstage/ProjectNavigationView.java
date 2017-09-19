package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.BMDExpress2Main;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.ProjectNavigationPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.view.bmdanalysis.BMDAnalysisView;
import com.sciome.bmdexpress2.mvp.view.categorization.CategorizationView;
import com.sciome.bmdexpress2.mvp.view.prefilter.OneWayANOVAView;
import com.sciome.bmdexpress2.mvp.view.prefilter.PathwayFilterView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IProjectNavigationView;
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class ProjectNavigationView extends BMDExpressViewBase implements IProjectNavigationView, Initializable
{

	// Navigation View
	@FXML
	private TreeView							navigationTreeView;

	// base tree items.
	private TreeItem<DoseResponseExperiment>	expressionDataTreeItem				= null;
	private TreeItem<OneWayANOVAResults>		oneWayANOVATreeItem					= null;
	private TreeItem<PathwayFilterResults>		pathwayFilterTreeItem				= null;
	private TreeItem<BMDResult>					bMDDoseAnalysesTreeItem				= null;
	private TreeItem<CategoryAnalysisResults>	functionalClassificationsTreeItem	= null;

	ProjectNavigationPresenter					presenter;

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
		presenter = new ProjectNavigationPresenter(this, eventBus);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		// let's initialize the navigation Tree
		Node homeImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/home.png")));
		navigationTreeView.setRoot(new TreeItem("Data Tree", homeImage));
		navigationTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// instantiate first level treeitems
		// A tree item requires its own instance of a node for the image...cannot use same image across
		// multiple treeitems.
		Node node1 = new ImageView(new Image(BMDExpress2Main.class.getResourceAsStream("/icons/folder.png")));
		Node node2 = new ImageView(new Image(BMDExpress2Main.class.getResourceAsStream("/icons/folder.png")));
		Node node3 = new ImageView(new Image(BMDExpress2Main.class.getResourceAsStream("/icons/folder.png")));
		Node node4 = new ImageView(new Image(BMDExpress2Main.class.getResourceAsStream("/icons/folder.png")));
		Node node5 = new ImageView(new Image(BMDExpress2Main.class.getResourceAsStream("/icons/folder.png")));
		expressionDataTreeItem = new TreeItem("Expression Data", node1);

		oneWayANOVATreeItem = new TreeItem("One-way ANOVA", node2);
		bMDDoseAnalysesTreeItem = new TreeItem("Benchmark Dose Analyses", node3);
		functionalClassificationsTreeItem = new TreeItem("Functional Classifications", node4);
		pathwayFilterTreeItem = new TreeItem("Pathway Filter", node5);
		// add tree items to navigation tree
		navigationTreeView.getRoot().getChildren().add(expressionDataTreeItem);
		navigationTreeView.getRoot().getChildren().add(oneWayANOVATreeItem);
		// remove pathwayFilterTreeItem
		// navigationTreeView.getRoot().getChildren().add(pathwayFilterTreeItem);
		navigationTreeView.getRoot().getChildren().add(bMDDoseAnalysesTreeItem);
		navigationTreeView.getRoot().getChildren().add(functionalClassificationsTreeItem);

		// add a selection change listener to the tree
		navigationTreeView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Object>() {
					@Override
					public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
					{
						TreeItem selectedItem = (TreeItem) newValue;
						if (selectedItem != null)
							handle_navigationTreeViewSelection(selectedItem);
					}

				});

		navigationTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{

				if (mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.SECONDARY)
				{
					System.out.println(mouseEvent.getSource());
					Object selectedItem = ((TreeItem) navigationTreeView.getSelectionModel()
							.getSelectedItem()).getValue();

					ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel()
							.getSelectedItems();
					List<TreeItem> selectedItems = new ArrayList<>();
					for (TreeItem tItem : treeItems)
					{
						selectedItems.add(tItem);
					}

					if (selectedItems.size() == 1)
						dealWithRightClickOnTree(selectedItem, mouseEvent);
					else if (selectedItems.size() > 1)
						dealWithRightClickOnTreeMultiSelected(selectedItems, mouseEvent);

				}

			}
		});

		navigationTreeView.getRoot().setExpanded(true);

		navigationTreeView.setCellFactory(tv ->
		{
			final Tooltip tooltip = new Tooltip();
			TreeCell<Object> cell = new TreeCell<Object>() {
				@Override
				public void updateItem(Object item, boolean empty)
				{
					super.updateItem(item, empty);
					if (empty)
					{
						setText(null);
						setTooltip(null);
						setGraphic(null);
					}
					else
					{
						setText(item.toString());
						tooltip.setText(item.toString());
						setTooltip(tooltip);
						setGraphic(getTreeItem().getGraphic());
					}
				}
			};
			cell.setOnMouseClicked(e ->
			{
				if (e.getClickCount() == 2 && !cell.isEmpty())
				{
					// Path file = cell.getItem();
					// do whatever you need with path...
				}
			});
			return cell;
		});

	}

	/*
	 * method to handle right clicks on tree nodes.
	 */
	private void dealWithRightClickOnTree(Object selectedItem, MouseEvent mouseEvent)
	{
		if (selectedItem instanceof DoseResponseExperiment)
		{
			showDoseExperimentContextMenu((DoseResponseExperiment) selectedItem).show(
					this.navigationTreeView.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
		else if (selectedItem instanceof OneWayANOVAResults)
		{
			showOneWayAnovaContextMenu((OneWayANOVAResults) selectedItem).show(
					this.navigationTreeView.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());

		}
		else if (selectedItem instanceof PathwayFilterResults)
		{
			showPathwayFilterContextMenu((PathwayFilterResults) selectedItem).show(
					this.navigationTreeView.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());

		}
		else if (selectedItem instanceof CategoryAnalysisResults)
		{
			showCategorizationContextMenu((CategoryAnalysisResults) selectedItem).show(
					this.navigationTreeView.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
		else if (selectedItem instanceof BMDResult)
		{
			showBMDAnalysisContextMenu((BMDResult) selectedItem).show(
					this.navigationTreeView.getScene().getWindow(), mouseEvent.getScreenX(),
					mouseEvent.getScreenY());
		}
	}

	/*
	 * method to handle right clicks on tree nodes.
	 */
	private void dealWithRightClickOnTreeMultiSelected(
			@SuppressWarnings("rawtypes") List<TreeItem> selectedItems, MouseEvent mouseEvent)
	{
		showMultiSelectedContextMenu(selectedItems).show(this.navigationTreeView.getScene().getWindow(),
				mouseEvent.getScreenX(), mouseEvent.getScreenY());;
	}

	@Override
	public void clearNavigationTree()
	{
		expressionDataTreeItem.getChildren().clear();
		oneWayANOVATreeItem.getChildren().clear();
		pathwayFilterTreeItem.getChildren().clear();
		bMDDoseAnalysesTreeItem.getChildren().clear();
		functionalClassificationsTreeItem.getChildren().clear();

	}

	/*
	 * handle navigation tree view selection change
	 */
	@SuppressWarnings("rawtypes")
	private void handle_navigationTreeViewSelection(TreeItem selectedItem)
	{
		if (selectedItem.getValue() instanceof DoseResponseExperiment)
		{
			DoseResponseExperiment dRE = (DoseResponseExperiment) selectedItem.getValue();
			presenter.doseResponseExperimentSelected(dRE);
		}
		else if (selectedItem.getValue() instanceof BMDExpressAnalysisDataSet)
		{
			presenter.BMDExpressAnalysisDataSetSelected((BMDExpressAnalysisDataSet) selectedItem.getValue());
		}
		else
		{
			presenter.clearMainDataView();
		}

	}

	/*
	 * put the dose response data into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addDoseResponseExperiement(DoseResponseExperiment doseResponseExperiment, boolean selectIt)
	{
		Node docImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/document.png")));
		TreeItem<DoseResponseExperiment> newTreeItem = new TreeItem<>(doseResponseExperiment, docImage);
		expressionDataTreeItem.getChildren().add(newTreeItem);
		if (selectIt)
		{
			navigationTreeView.getSelectionModel().clearSelection();
			navigationTreeView.getSelectionModel().select(newTreeItem);
		}

	}

	/*
	 * put the oneway result into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addOneWayANOVAAnalysis(OneWayANOVAResults oneWayANOVAResults, boolean selectIt)
	{
		Node docImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/document.png")));
		TreeItem<OneWayANOVAResults> newTreeItem = new TreeItem<>(oneWayANOVAResults, docImage);
		oneWayANOVATreeItem.getChildren().add(newTreeItem);
		if (selectIt)
		{
			navigationTreeView.getSelectionModel().clearSelection();
			navigationTreeView.getSelectionModel().select(newTreeItem);
		}
	}

	/*
	 * put the oneway result into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPathwayFilterResults(PathwayFilterResults pathwayFilterResults, boolean selectIt)
	{
		Node docImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/document.png")));
		TreeItem<PathwayFilterResults> newTreeItem = new TreeItem<>(pathwayFilterResults, docImage);
		pathwayFilterTreeItem.getChildren().add(newTreeItem);
		if (selectIt)
		{
			navigationTreeView.getSelectionModel().clearSelection();
			navigationTreeView.getSelectionModel().select(newTreeItem);
		}
	}

	/*
	 * put the bmd result into the tree
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addBMDAnalysis(BMDResult bMDResult, boolean selectIt)
	{
		Node docImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/document.png")));
		TreeItem<BMDResult> newTreeItem = new TreeItem<>(bMDResult, docImage);
		bMDDoseAnalysesTreeItem.getChildren().add(newTreeItem);
		if (selectIt)
		{
			navigationTreeView.getSelectionModel().clearSelection();
			navigationTreeView.getSelectionModel().select(newTreeItem);
		}
	}

	/*
	 * put the category analysis into the tree.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addCategoryAnalysis(CategoryAnalysisResults categoryAnalysisResults, boolean selectIt)
	{
		Node docImage = new ImageView(
				new Image(BMDExpress2Main.class.getResourceAsStream("/icons/document.png")));
		TreeItem<CategoryAnalysisResults> newTreeItem = new TreeItem<>(categoryAnalysisResults, docImage);
		functionalClassificationsTreeItem.getChildren().add(newTreeItem);
		if (selectIt)
		{
			navigationTreeView.getSelectionModel().clearSelection();
			navigationTreeView.getSelectionModel().select(newTreeItem);
		}
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
				ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel()
						.getSelectedItems();
				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (TreeItem tItem : treeItems)
				{
					Object selectedItem = tItem.getValue();
					if (selectedItem instanceof IStatModelProcessable)
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}
				TreeItem treeItem = (TreeItem) navigationTreeView.getSelectionModel().getSelectedItem();

				if (selectedItems.size() > 0)
				{
					// now create a list of doseResponseExperement objects so the oneway anova view can offer
					// a selection list.

					List<IStatModelProcessable> processabeDatas = new ArrayList<>();

					for (int i = 0; i < treeItem.getParent().getChildren().size(); i++)
					{
						TreeItem item = (TreeItem) treeItem.getParent().getChildren().get(i);
						processabeDatas.add((IStatModelProcessable) item.getValue());
					}
					try
					{

						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/onewayanova.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("One Way ANOVA");
						stage.setScene(new Scene((BorderPane) loader.load()));
						OneWayANOVAView controller = loader.<OneWayANOVAView> getController();
						controller.initData(selectedItems, processabeDatas);

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
	 * pathway filter view. pass it the dose response experiement data and start let user do stuff from there.
	 *
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void performPathwayFilter()
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel()
						.getSelectedItems();
				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (TreeItem tItem : treeItems)
				{
					Object selectedItem = tItem.getValue();
					if (selectedItem instanceof IStatModelProcessable)
					{
						IStatModelProcessable processableData = (IStatModelProcessable) selectedItem;
						selectedItems.add(processableData);
					}
				}
				TreeItem treeItem = (TreeItem) navigationTreeView.getSelectionModel().getSelectedItem();
				if (selectedItems.size() > 0)
				{
					// now create a list of doseResponseExperement objects so the oneway anova view can offer
					// a selection list.

					List<IStatModelProcessable> processabeDatas = new ArrayList<>();

					for (int i = 0; i < treeItem.getParent().getChildren().size(); i++)
					{
						TreeItem item = (TreeItem) treeItem.getParent().getChildren().get(i);
						processabeDatas.add((IStatModelProcessable) item.getValue());
					}
					try
					{

						FXMLLoader loader = new FXMLLoader(
								getClass().getResource("/fxml/pathwayfilter.fxml"));

						Stage stage = BMDExpressFXUtils.getInstance().generateStage("Pathway Filter");
						stage.setScene(new Scene((BorderPane) loader.load()));
						PathwayFilterView controller = loader.<PathwayFilterView> getController();

						controller.initData(selectedItems, processabeDatas);

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

				ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel()
						.getSelectedItems();
				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (TreeItem tItem : treeItems)
				{
					Object selectedItem = tItem.getValue();
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
		ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel().getSelectedItems();
		List<BMDResult> selectedItems = new ArrayList<>();
		for (TreeItem tItem : treeItems)
		{
			Object selectedItem = tItem.getValue();
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
		annationDialog.initOwner(navigationTreeView.getScene().getWindow());
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
			logTransFormationDialog.initOwner(navigationTreeView.getScene().getWindow());
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

	private ContextMenu showPathwayFilterContextMenu(PathwayFilterResults pathWayResult)
	{
		ContextMenu ctxMenu = new ContextMenu();
		ctxMenu.getItems().addAll(getCommonMenuItems());
		setContextMenuCommonHandlers("Pathway Filter", ctxMenu, pathWayResult);
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
	private ContextMenu showMultiSelectedContextMenu(List<TreeItem> selectedItems)
	{
		ContextMenu ctxMenu = new ContextMenu();

		ctxMenu.getItems().addAll(getCommonMultiSelectMenuItems());

		ctxMenu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event)
			{
				switch (((MenuItem) event.getTarget()).getText())
				{

					case "Remove":
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
		menuItems.add(new MenuItem("Remove"));
		menuItems.add(new MenuItem("Export"));

		return menuItems;
	}

	@SuppressWarnings("rawtypes")
	private void handle_BMDExpressAnalysisDataSetRemove(BMDExpressAnalysisDataSet analysisDataSet)
	{
		if (!showAlert("Remove Result", "Remove", "Are you sure you want to remove this result?"))
			return;

		presenter.removeBMDExpressAnalysisDataSetFromProject(analysisDataSet);

		for (Object ti : navigationTreeView.getRoot().getChildren())
		{
			((TreeItem) ti).getChildren().remove(navigationTreeView.getSelectionModel().getSelectedItem());
		}
		navigationTreeView.refresh();
	}

	private void handle_BMDExpressAnalysisDataSetRename(BMDExpressAnalysisDataSet catAnalysisResults,
			String title)
	{
		String newName = textInputDialog(catAnalysisResults.toString(), title, "Rename",
				"Enter the new name.");

		if (newName == null)
			return;

		catAnalysisResults.setName(newName);
		navigationTreeView.refresh();

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
		this.expressionDataTreeItem.getChildren()
				.remove(navigationTreeView.getSelectionModel().getSelectedItem());
		// this.expressionDataTreeItem.
		navigationTreeView.refresh();
	}

	private void handle_DoseResponseExperimentRename(DoseResponseExperiment doseResponseExperiment)
	{
		String newName = textInputDialog(doseResponseExperiment.toString(), "Rename BMD Result", "Rename",
				"Enter the new name.");

		if (newName == null)
			return;

		doseResponseExperiment.setName(newName);
		navigationTreeView.refresh();
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

				ObservableList<TreeItem> treeItems = navigationTreeView.getSelectionModel()
						.getSelectedItems();
				List<IStatModelProcessable> selectedItems = new ArrayList<>();
				for (TreeItem tItem : treeItems)
				{
					Object selectedItem = tItem.getValue();
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
	private void handle_MultiSelectRemove(List<TreeItem> selectedItems)
	{

		if (!showAlert("Remove Multiple Items Confirmation", "Remove",
				"Are you sure you want to remove these results?"))
			return;

		navigationTreeView.getSelectionModel().clearSelection();
		for (TreeItem selectedItem : selectedItems)
		{
			if (selectedItem.getValue() instanceof BMDExpressAnalysisDataSet)
			{
				presenter.removeBMDExpressAnalysisDataSetFromProject(
						(BMDExpressAnalysisDataSet) selectedItem.getValue());
				for (Object ti : navigationTreeView.getRoot().getChildren())
				{
					((TreeItem) ti).getChildren().remove(selectedItem);
				}
				navigationTreeView.refresh();
			}
			else if (selectedItem.getValue() instanceof DoseResponseExperiment)
			{
				presenter.removeDoseResponseExperimentFromProject(
						(DoseResponseExperiment) selectedItem.getValue());
				this.expressionDataTreeItem.getChildren().remove(selectedItem);
				navigationTreeView.refresh();
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void handle_MultiSelectExport(List<TreeItem> selectedItems)
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
		File selectedFile = fileChooser.showSaveDialog(navigationTreeView.getScene().getWindow());

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
		alert.initOwner(navigationTreeView.getScene().getWindow());
		alert.initModality(Modality.WINDOW_MODAL);
		Optional<ButtonType> result = alert.showAndWait();
		((Stage) navigationTreeView.getScene().getWindow()).toFront();
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
		dialog.initOwner(navigationTreeView.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.getDialogPane().setMinWidth(500);
		dialog.setResizable(true);
		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		((Stage) navigationTreeView.getScene().getWindow()).toFront();
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
		navigationTreeView.getRoot().setExpanded(true);
		for (Object treeItem : navigationTreeView.getRoot().getChildren())
		{
			((TreeItem) treeItem).setExpanded(true);
		}

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
		return ViewUtilities.getInstance().getSaveAsFile(navigationTreeView.getScene().getWindow());
	}

	@Override
	public File askForAProjectFileToOpen()
	{
		return ViewUtilities.getInstance().getOpenProjectFile(navigationTreeView.getScene().getWindow());
	}

	@Override
	public File askForABMDFileToImport()
	{
		return ViewUtilities.getInstance()
				.getBMDImportFileToImport(navigationTreeView.getScene().getWindow());
	}

	@Override
	public File askForAJSONFileToImport()
	{
		return ViewUtilities.getInstance()
				.getJSONImportFileToImport(navigationTreeView.getScene().getWindow());
	}

	@Override
	public void showMatrixPreview(String header, MatrixData matrixData)
	{
		ViewUtilities.getInstance().matrixPreviewStage("Spreadsheet View", header, matrixData);

	}

	@Override
	public void setWindowSizeProperties()
	{
		Stage stage = (Stage) navigationTreeView.getScene().getWindow();
		BMDExpressProperties.getInstance().setSizeY((int) stage.getHeight());
		BMDExpressProperties.getInstance().setSizeX((int) stage.getWidth());
		BMDExpressProperties.getInstance().setLocX((int) stage.getX());
		BMDExpressProperties.getInstance().setLocY((int) stage.getY());
		BMDExpressProperties.getInstance().save();

	}

	@Override
	public Window getWindow()
	{
		return navigationTreeView.getScene().getWindow();
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
