package com.sciome.bmdexpress2;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.TableInformation;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressInformation;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseApplicationRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BMDExpress2Main extends Application
{

	private Scene scene;

	@SuppressWarnings("restriction")
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			cleanTmpFiles();
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("/fxml/mainstage.fxml"));
			scene = new Scene(root, 800, 800);

			Platform.setImplicitExit(false);

			primaryStage.getIcons().add(new Image("icon.png"));
			primaryStage.setX(32);
			primaryStage.setY(32);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event)
				{
					event.consume();
					BMDExpressProperties.getInstance().saveTableInformation();
					BMDExpressEventBus.getInstance().post(new CloseApplicationRequestEvent("close it"));
				}
			});

			primaryStage.setResizable(true);
			scene.getStylesheets().add(getClass().getResource("/fxml/application.css").toExternalForm());
			primaryStage.setScene(scene);
			if (BMDExpressProperties.getInstance().getSizeY() > 50
					&& BMDExpressProperties.getInstance().getSizeX() > 50)
			{
				primaryStage.setHeight(BMDExpressProperties.getInstance().getSizeY());
				primaryStage.setWidth(BMDExpressProperties.getInstance().getSizeX());
			}

			System.out.println(Screen.getPrimary().getVisualBounds().getWidth());
			System.out.println(Screen.getPrimary().getVisualBounds().getHeight());
			if (BMDExpressProperties.getInstance().getLocX() > 0 && BMDExpressProperties.getInstance()
					.getLocX() < Screen.getPrimary().getVisualBounds().getWidth() - 200)
				primaryStage.setX(BMDExpressProperties.getInstance().getLocX());
			if (BMDExpressProperties.getInstance().getLocY() > 0 && BMDExpressProperties.getInstance()
					.getLocY() < Screen.getPrimary().getVisualBounds().getHeight() - 200)
				primaryStage.setY(BMDExpressProperties.getInstance().getLocY());

			// run the Rscript. I do this because windows likes to ask
			// permission the first time it is run. might as well get it
			// out of the way while the It Admin is hanging around.
			// runRScript();
			primaryStage.setTitle("BMDExpress 2");
			primaryStage.show();

			Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) ->
			{
				System.out.println("Handler caught exception: " + throwable.getMessage());
				throwable.printStackTrace();
				BMDExpressEventBus.getInstance().post(new ShowErrorEvent(throwable.toString()));
			});
			showInitDialog();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * when user first installs the app, just run rscript to get all the os level run permissions out of the
	 * way.
	 * 
	 */
	private void runRScript()
	{
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				try
				{
					ProcessBuilder builder = new ProcessBuilder();
					builder.command(BMDExpressProperties.getInstance().getRscript());
					Process process = builder.start();
				}
				catch (Exception e)
				{
					e.printStackTrace();

				}

				return 0;
			}
		};

		new Thread(task).start();

	}

	public static void main(String[] args)
	{
		Float myflot = Float.MIN_VALUE;
		Double mydoub = Double.MIN_VALUE;
		renameTableInformationJSONFields();
		launch(args);
	}

	private void cleanTmpFiles()
	{
		String tmpFolder = BMDExpressConstants.getInstance().TEMP_FOLDER;
		if (!tmpFolder.contains("tmp"))
			return;

		File folder = null;
		try
		{
			folder = new File(tmpFolder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		for (File file : folder.listFiles())
		{
			long diff = new Date().getTime() - file.lastModified();
			if (file.isDirectory())
				continue;

			if (diff > 1 * 24 * 60 * 60 * 1000)
			{
				try
				{
					file.delete();
				}
				catch (Exception e)
				{

				}
			}
		}

	}

	private void showInitDialog()
	{
		// first check to see if this is a new version. if it is, show the new features instead.
		String version = BMDExpressProperties.getInstance().getVersion();
		if (!version.equals(BMDExpressProperties.getInstance().getVersionFromFile()))
		{
			// show this version's changes

			BMDExpressProperties.getInstance().writeVersion(version);
			BMDExpressInformation.getInstance().showVersionDialog(scene, version);
		}

	}

	// This function is used to rename column names in the
	// tableinformation.json file. Occasionally we are requested to
	// change a column name, which used to be as simple as changing a
	// constant. But now we are storing column order and visibility.
	// So we need to make sure this is in sync with current naming scheme.
	private static void renameTableInformationJSONFields()
	{
		TableInformation tableInformation = BMDExpressProperties.getInstance().getTableInformation();

		// change the name of Category Analysis id and name fields. aug 14, 2018
		Map<String, Boolean> catAnalysisMap = tableInformation.getCategoryAnalysisMap();
		if (catAnalysisMap.get("GO/Pathway/Gene Set ID") != null)
			catAnalysisMap.put(CategoryAnalysisResults.CATEGORY_ID,
					catAnalysisMap.get("GO/Pathway/Gene Set ID"));

		if (catAnalysisMap.get("GO/Pathway/Gene Set Name") != null)
			catAnalysisMap.put(CategoryAnalysisResults.CATEGORY_DESCRIPTION,
					catAnalysisMap.get("GO/Pathway/Gene Set Name"));

		List<String> catAnalysisOrder = tableInformation.getCategoryAnalysisOrder();
		for (int i = 0; i < catAnalysisOrder.size(); i++)
			if (catAnalysisOrder.get(i).equals("GO/Pathway/Gene Set Name"))
				catAnalysisOrder.set(i, CategoryAnalysisResults.CATEGORY_DESCRIPTION);
			else if (catAnalysisOrder.get(i).equals("GO/Pathway/Gene Set ID"))
				catAnalysisOrder.set(i, CategoryAnalysisResults.CATEGORY_ID);

		BMDExpressProperties.getInstance().saveTableInformation();

	}

}
