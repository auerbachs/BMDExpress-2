package com.sciome.bmdexpress2.util;

import java.io.File;


import com.sciome.bmdexpress2.mvp.view.mainstage.MatrixSwingNodeView;
import com.sciome.bmdexpress2.shared.BMDExpressFXUtils;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.MatrixDataPreviewer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

/*
 * A singleton to return Dialog or other types of views for quickly viewing data.
 */
public class ViewUtilities
{

	private static ViewUtilities instance = null;

	public static ViewUtilities getInstance()
	{
		if (instance == null)
		{
			instance = new ViewUtilities();
		}
		return instance;
	}

	/*
	 * Custom JavaFX Dialog to view matrix data using a Swing Node.
	 */
	public Dialog<String> matrixPreviewDialog(String titleTxt, String headerText, MatrixData matrixData,
			Window owner)
	{

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(titleTxt);
		dialog.setHeaderText(headerText);
		dialog.setResizable(true);
		dialog.initOwner(owner);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setResizable(false);
		MatrixDataPreviewer pane = new MatrixDataPreviewer(matrixData);
		dialog.getDialogPane().setContent(pane);
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
		return dialog;

	}

	/*
	 * Custom JavaFX Dialog to view matrix data using a Swing Node.
	 */
	public void matrixPreviewStage(String titleTxt, String headerText, MatrixData matrixData)
	{

		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/matrixswingnode.fxml"));

			Stage stage = BMDExpressFXUtils.getInstance().generateStage("");
			stage.setScene(new Scene((AnchorPane) loader.load()));
			stage.setTitle(titleTxt);

			MatrixSwingNodeView controller = loader.<MatrixSwingNodeView> getController();
			controller.initData(headerText, matrixData);

			stage.sizeToScene();
			stage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}

	}

	public File getSaveAsFile(Window window)
	{
		// prompt the user to select a file and then tell the presenter to fire off loading the experiment

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save BMDExpress2 Project As");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMDExpress2(*.bm2)", "*.bm2"));
		File initialDirectory = new File(BMDExpressProperties.getInstance().getProjectPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		File selectedFile = fileChooser.showSaveDialog(window);
		if (selectedFile != null)
		{
			BMDExpressProperties.getInstance().setProjectPath(selectedFile.getParent());
			if (!selectedFile.getName().matches("^.*\\.bm2$"))
			{
				selectedFile = new File(selectedFile.getAbsolutePath() + ".bm2");
			}

		}
		return selectedFile;

	}

	public File getSaveAsJSONFile(Window window)
	{
		// prompt the user to select a file and then tell the presenter to fire off loading the experiment

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export BMDExpress2 Project As JSON");
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("BMDExpress2(*.json)", "*.json"));
		File initialDirectory = new File(BMDExpressProperties.getInstance().getProjectPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		File selectedFile = fileChooser.showSaveDialog(window);
		if (selectedFile != null)
		{
			BMDExpressProperties.getInstance().setProjectPath(selectedFile.getParent());
			if (!selectedFile.getName().matches("^.*\\.json$"))
			{
				selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
			}

		}
		return selectedFile;

	}

	public File getOpenProjectFile(Window window)
	{
		// prompt the user to select a file and then tell the presenter to fire off loading the experiment
		FileChooser fileChooser = new FileChooser();
		File initialDirectory = new File(BMDExpressProperties.getInstance().getProjectPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setTitle("Open BMDExpress2 Project");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMDExpress2(*.bm2)", "*.bm2"));
		File selectedFile = fileChooser.showOpenDialog(window);
		if (selectedFile != null)
		{

			BMDExpressProperties.getInstance().setProjectPath(selectedFile.getParent());
			if (!selectedFile.getName().matches("^.*\\.bm2$"))
			{
				selectedFile = new File(selectedFile.getAbsolutePath() + ".bm2");
			}

		}
		return selectedFile;

	}

	public File getBMDImportFileToImport(Window window)
	{
		// prompt the user to select a file and then tell the presenter to fire off loading the experiment
		FileChooser fileChooser = new FileChooser();
		File initialDirectory = new File(BMDExpressProperties.getInstance().getProjectPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setTitle("Import BMDExpress 1 Project");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMDExpress(*.bmd)", "*.bmd"));
		File selectedFile = fileChooser.showOpenDialog(window);
		if (selectedFile != null)
		{

			BMDExpressProperties.getInstance().setProjectPath(selectedFile.getParent());
			if (!selectedFile.getName().matches("^.*\\.bmd$"))
			{
				selectedFile = new File(selectedFile.getAbsolutePath() + ".bmd");
			}

		}
		return selectedFile;

	}

	public File getJSONImportFileToImport(Window window)
	{
		// prompt the user to select a file and then tell the presenter to fire off loading the experiment
		FileChooser fileChooser = new FileChooser();
		File initialDirectory = new File(BMDExpressProperties.getInstance().getProjectPath());
		if (initialDirectory.exists())
			fileChooser.setInitialDirectory(initialDirectory);
		fileChooser.setTitle("Import BMDExpress 2 Project in JSON Format");
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("BMDExpress(*.json)", "*.json"));
		File selectedFile = fileChooser.showOpenDialog(window);
		if (selectedFile != null)
		{

			BMDExpressProperties.getInstance().setProjectPath(selectedFile.getParent());
			if (!selectedFile.getName().matches("^.*\\.json$"))
				selectedFile = new File(selectedFile.getAbsolutePath() + ".json");

		}
		return selectedFile;
	}

}
