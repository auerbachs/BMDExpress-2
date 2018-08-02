package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.presenter.mainstage.MenuBarPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMenuBarView;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.BMDExpressInformation;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.ViewUtilities;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MenuBarView extends BMDExpressViewBase implements IMenuBarView, Initializable
{

	// Menu Bar
	@FXML
	private MenuBar			menuBar;

	// Menu check items
	@FXML
	private CheckMenuItem	useWebServiceCheckMenu;
	@FXML
	private CheckMenuItem	usePrecisionCheckMenu;
	@FXML
	private CheckMenuItem	autoUpdateCheckMenu;

	// Menu items
	@FXML
	private MenuItem		oneWayANOVAMenuItem;
	@FXML
	private MenuItem		williamsTrendMenuItem;
	@FXML
	private MenuItem		oriogenMenuItem;
	@FXML
	private MenuItem		bMDAnalysesMenuItem;
	@FXML
	private MenuItem		GOAnalysesMenuItem;
	@FXML
	private MenuItem		pathwayAnalysesMenuItem;
	@FXML
	private MenuItem		definedCategoryAnalysesMenuItem;
	@FXML
	private MenuItem		geneLevelBMDMenuItem;

	MenuBarPresenter		presenter;

	public MenuBarView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public MenuBarView(BMDExpressEventBus eventBus)
	{
		super();
		// addComponent(new Label("Hello from the architecture demo!!"));
		presenter = new MenuBarPresenter(this, eventBus);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		// put the menu bar where it is supposed to be
		try
		{
			final String os = System.getProperty("os.name");
			if (os != null && os.toLowerCase().contains("mac"))
				menuBar.useSystemMenuBarProperty().set(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * MenuBAR handlers.
	 */

	/*
	 * open a project
	 */
	public void handle_openProject(ActionEvent event)
	{
		presenter.loadProject(null);

	}

	/*
	 * add a project
	 */
	public void handle_addProject(ActionEvent event)
	{
		presenter.addProject(null);
	}

	/*
	 * import raw expression data
	 */
	public void handle_importExpressionData(ActionEvent event)
	{

		// prompt the user to select a file and then tell the presenter to fire off loading the experiment
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import Expression Data");
		File initialDirectory = new File(BMDExpressProperties.getInstance().getExpressionPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Text Files", "*.txt", "*.csv", "*.dat"));

		List<File> selectedFile = fileChooser.showOpenMultipleDialog(menuBar.getScene().getWindow());
		if (selectedFile != null && selectedFile.size() > 0)
		{

			BMDExpressProperties.getInstance().setExpressionPath(selectedFile.get(0).getParent());
			// inform subscribers.
			presenter.loadExperiment(selectedFile);

		}

	}

	/*
	 * update annotation files from web
	 */
	public void handle_annotationsFromWeb(ActionEvent event)
	{
		// need to run this on the main ui thread. this is being called from event bus thread..hence the
		// runlater.
		Platform.runLater(new Runnable() {

			@Override
			public void run()
			{

				try
				{

					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/annotationupdate.fxml"));

					Stage stage = BMDExpressFXUtils.getInstance().generateStage("");
					stage.setScene(new Scene((AnchorPane) loader.load(), 800, 800));

					stage.show();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	/*
	 * update annotation files from local file system
	 */
	public void handle_annotationsFromFile(ActionEvent event)
	{

	}

	/*
	 * save the current project as another name (or what ever name is selected)
	 */
	public void handle_saveAs(ActionEvent event)
	{
		File selectedFile = ViewUtilities.getInstance().getSaveAsFile(menuBar.getScene().getWindow());
		if (selectedFile != null)
		{
			// inform subscribers.
			presenter.saveProjectAs(selectedFile);
		}

	}

	public void handle_exportAsJSON(ActionEvent event)
	{
		File selectedFile = ViewUtilities.getInstance().getSaveAsJSONFile(menuBar.getScene().getWindow());
		if (selectedFile != null)
		{
			presenter.saveProjectAsJSON(selectedFile);
		}
	}

	/*
	 * save the project to disk. invoke serialization
	 */
	public void handle_saveProject(ActionEvent event)
	{

		// prompt the user whether or not to overwrite current project
		presenter.saveProject();
	}

	/*
	 * close the project
	 */
	public void handle_closeProject(ActionEvent event)
	{
		presenter.closeProject();
	}

	/*
	 * exit application
	 */
	public void handle_exit(ActionEvent event)
	{
		presenter.sendCloseEvent();
	}

	/*
	 * use web service to stuff
	 */
	public void handle_useWebServicePreference(ActionEvent event)
	{

	}

	/*
	 * let user define precision
	 */
	public void handle_userPrecisionPreference(ActionEvent event)
	{

	}

	/*
	 * do auto updates or not
	 */
	public void handle_autoUpdatePreference(ActionEvent event)
	{

	}

	/*
	 * decimal rounding
	 */
	public void handle_roundDecimals(ActionEvent event)
	{

	}

	/*
	 * spread sheet
	 */
	public void handle_spreadSheetMenuView(ActionEvent event)
	{

	}

	/*
	 * one way anova analysis
	 */
	public void handle_oneWayANOVA(ActionEvent event)
	{

		presenter.performOneWayANOVA();

	}

	/*
	 * williams trend analysis
	 */
	public void handle_williamsTrend(ActionEvent event)
	{

		presenter.performWilliamsTrend();

	}

	/*
	 * oriogen analysis
	 */
	public void handle_oriogen(ActionEvent event)
	{

		presenter.performOriogen();

	}

	/*
	 * bmd analysis
	 */
	public void handle_BMDAnalyses(ActionEvent event)
	{
		presenter.performBMDAnalsyis();

	}

	/*
	 * GO analysis
	 */
	public void handle_GOAnalyses(ActionEvent event)
	{
		presenter.performCategoryAnalysis(CategoryAnalysisEnum.GO);

	}

	/*
	 * pathway analysis
	 */
	public void handle_pathwayAnalyses(ActionEvent event)
	{
		presenter.performCategoryAnalysis(CategoryAnalysisEnum.PATHWAY);

	}

	/*
	 * defined category analysis
	 */
	public void handle_definedCategoryAnalyses(ActionEvent event)
	{
		presenter.performCategoryAnalysis(CategoryAnalysisEnum.DEFINED);

	}
	
	public void handle_geneLevelBMD(ActionEvent event)
	{
		presenter.performCategoryAnalysis(CategoryAnalysisEnum.GENE_LEVEL);
	}

	/*
	 * show the tutorial
	 */
	public void handle_tutorial(ActionEvent event)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(BMDExpressConstants.getInstance().TUTORIAL_URL));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * display the credits
	 */
	public void handle_credits(ActionEvent event)
	{
		BMDExpressInformation.getInstance().showSplashDialog(this.menuBar.getScene());

	}

	/*
	 * display about
	 */
	public void handle_about(ActionEvent event)
	{
		BMDExpressInformation.getInstance().showVersionDialog(this.menuBar.getScene(),
				BMDExpressProperties.getInstance().getVersion());
	}

	/*
	 * display license
	 */
	public void handle_license(ActionEvent event)
	{
		BMDExpressInformation.getInstance().showLicense(this.menuBar.getScene());
	}

	public void handle_importBMDFile(ActionEvent event)
	{
		presenter.importBMDFile();
	}

	public void handle_importJSON(ActionEvent event)
	{
		presenter.importJSONFile();
	}

	public void handle_dataVisualization(ActionEvent event)
	{
		presenter.performDataVisualization();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sciome.bmdexpress2.viewinterface.mainstage.IMenuBarView#expressionDataSelected()
	 */
	@Override
	public void expressionDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(false);
		togglePrefilterMenuItems(false);
		toggleCategoryMenuItems(true);

	}

	@Override
	public void oneWayANOVADataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(false);
		togglePrefilterMenuItems(true);
		toggleCategoryMenuItems(true);

	}

	@Override
	public void williamsTrendDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(false);
		togglePrefilterMenuItems(true);
		toggleCategoryMenuItems(true);
	}

	@Override
	public void oriogenDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(false);
		togglePrefilterMenuItems(true);
		toggleCategoryMenuItems(true);
	}

	@Override
	public void bMDAnalysisDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(true);
		togglePrefilterMenuItems(true);
		toggleCategoryMenuItems(false);

	}

	@Override
	public void functionalCategoryDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(true);
		togglePrefilterMenuItems(true);
		toggleCategoryMenuItems(true);
	}

	@Override
	public Window getWindow()
	{
		return menuBar.getScene().getWindow();
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

	@Override
	public void saveAs()
	{
		File selectedFile = ViewUtilities.getInstance().getSaveAsFile(menuBar.getScene().getWindow());
		if (selectedFile != null)
		{
			// inform subscribers.
			presenter.saveProjectAs(selectedFile);
		}

	}

	/**
	 * True to disable all prefilter items and false otherwise
	 * 
	 * @param disabled
	 */
	private void togglePrefilterMenuItems(boolean disabled)
	{
		this.oneWayANOVAMenuItem.setDisable(disabled);
		this.williamsTrendMenuItem.setDisable(disabled);
		this.oriogenMenuItem.setDisable(disabled);
	}
	
	private void toggleCategoryMenuItems(boolean disabled)
	{
		this.GOAnalysesMenuItem.setDisable(disabled);
		this.pathwayAnalysesMenuItem.setDisable(disabled);
		this.definedCategoryAnalysesMenuItem.setDisable(disabled);
		this.geneLevelBMDMenuItem.setDisable(disabled);
	}

	@Override
	public void combinedSelected()
	{

	}

	@Override
	public void noDataSelected()
	{
		this.bMDAnalysesMenuItem.setDisable(true);
		togglePrefilterMenuItems(true);
		this.GOAnalysesMenuItem.setDisable(true);
		this.pathwayAnalysesMenuItem.setDisable(true);
		this.definedCategoryAnalysesMenuItem.setDisable(true);

	}

}
