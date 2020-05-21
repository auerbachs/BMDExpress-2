/*
 * CategoryTool.java     1.0    7/25/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for pathway analyses.
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import java.io.File;
import java.util.Optional;

import com.sciome.bmdexpress2.util.FileIO;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.ViewUtilities;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * The class for CategoryTool
 */
public class DefinedCategoryFilesTool
{
	private MatrixData idsMatrix, mapsMatrix;

	public DefinedCategoryFilesTool()
	{

	}

	public DefinedCategoryFileParameters probeGetter(Window owner)
	{
		DefinedCategoryFileParameters fileParameters = new DefinedCategoryFileParameters();
		// Choose a File containing probe data.
		File inFile = FileIO.chooseInputFile(null, "Choose probe file.", "data");
		if (inFile == null)
			return null;
		fileParameters.setFileName(inFile.getAbsolutePath());
		idsMatrix = FileIO.readFileMatrix(null, inFile);
		idsMatrix.setAllString(true);

		// View the data and figure out if there column headers.
		Dialog<String> dialog = ViewUtilities.getInstance().matrixPreviewDialog("", "Probe Data", idsMatrix,
				owner);
		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
		{
			return null;
		}
		fileParameters.setMatrixData(idsMatrix);
		// Now the probe/categoryid column must needs be identified.

		// idsMatrix = MatrixDataPreviewer.showInputMatrix(jFrame, idsMatrix);

		String[] colNames = idsMatrix.getColumnNames();
		String[] prompts = { "Array Probe", "Category Component" };
		String message = "Match appropriate columns to probes and components.";
		String title = "Columns Match";

		Dialog<int[]> mdialog = this.selectProbeColumns("", message, prompts, colNames, owner);
		Optional<int[]> mresult = mdialog.showAndWait();
		if (!mresult.isPresent())
			return null;

		fileParameters.setUsedColumns(mresult.get());

		return fileParameters;
	}

	public DefinedCategoryFileParameters categoryGetter(Window owner)
	{
		DefinedCategoryFileParameters fileParameters = new DefinedCategoryFileParameters();

		File inFile = FileIO.chooseInputFile(null, "Choose category file.", "data");
		if (inFile == null)
			return null;
		fileParameters.setFileName(inFile.getAbsolutePath());

		mapsMatrix = FileIO.readFileMatrix(null, inFile);
		mapsMatrix.setAllString(true);

		// View the data and figure out if there column headers.
		Dialog<String> dialog = ViewUtilities.getInstance().matrixPreviewDialog("", "Category Data",
				mapsMatrix, owner);
		dialog.setResizable(false);
		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
			return null;

		fileParameters.setMatrixData(mapsMatrix);

		String[] colNames = mapsMatrix.getColumnNames();
		String[] prompts = { "Category ID", "Category Name", "Category Component" };
		String message = "Match appropriate columns to category and components.";
		String title = "Columns Match";
		Dialog<int[]> mdialog = this.selectProbeColumns("", message, prompts, colNames, owner);
		Optional<int[]> mresult = mdialog.showAndWait();
		if (!mresult.isPresent())
		{
			return null;
		}
		fileParameters.setUsedColumns(mresult.get());

		return fileParameters;
	}

	/*
	 * Custom JavaFX Dialog to define columns to probe/categoryid
	 */
	public Dialog<int[]> selectProbeColumns(String titleTxt, String message, String[] prompts,
			String[] colNames, Window owner)
	{

		Dialog<int[]> dialog = new Dialog<>();
		dialog.setResizable(false);
		dialog.setTitle(titleTxt);
		dialog.setHeaderText("Here is your matrix data.");
		dialog.initOwner(owner);
		dialog.initModality(Modality.WINDOW_MODAL);

		VBox swingNode = new VBox();
		dialog.getDialogPane().setContent(swingNode);

		MultiInputPane mpane = new MultiInputPane(message, prompts, colNames, 1);

		swingNode.getChildren().add(mpane);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

		dialog.setResultConverter(new Callback<ButtonType, int[]>() {
			@Override
			public int[] call(ButtonType b)
			{

				if (b == buttonTypeOk)
				{
					return mpane.getIndices();
				}

				return null;
			}
		});

		dialog.getDialogPane().setPrefSize(800, 600);
		dialog.getDialogPane().autosize();

		return dialog;

	}

}