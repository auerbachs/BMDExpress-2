package com.sciome.bmdexpress2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import org.ciit.io.ProjectReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

/*
 * A singleton to return Dialog or other types of views for quickly viewing data.
 */
public class DialogWithThreadProcess
{

	Dialog<String>	dialog	= new Dialog<>();
	Window			owner;

	public DialogWithThreadProcess(Window owner)
	{
		this.owner = owner;
	}

	public void saveJSONProject(BMDProject bmdProject, File selectedFile)
	{
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception
			{
				try
				{

					saveAsJSON(bmdProject, selectedFile);

				}
				catch (IOException i)
				{
					Platform.runLater(new Runnable() {

						@Override
						public void run()
						{
							BMDExpressEventBus.getInstance()
									.post(new ShowErrorEvent("Error saving file. " + i.toString()));

						}
					});
					i.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void cancelled()
			{
				super.cancelled();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void failed()
			{
				super.failed();
				dialog.setResult("finished");
				dialog.close();
			}
		};
		new Thread(task).start();

		showWaitDialog("Save Project",
				"Saving Project : " + bmdProject.getName() + " to " + selectedFile.getAbsolutePath());
	}

	public void saveProject(BMDProject bmdProject, File selectedFile)
	{

		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception
			{
				try
				{
					FileOutputStream fileOut = new FileOutputStream(selectedFile);

					int bufferSize = 2000 * 1024; // make it a 2mb buffer
					BufferedOutputStream bout = new BufferedOutputStream(fileOut, bufferSize);
					ObjectOutputStream out = new ObjectOutputStream(bout);
					bmdProject.setName(selectedFile.getName());
					out.writeObject(bmdProject);
					out.close();
					fileOut.close();

				}
				catch (IOException i)
				{
					Platform.runLater(new Runnable() {

						@Override
						public void run()
						{
							BMDExpressEventBus.getInstance()
									.post(new ShowErrorEvent("Error saving file. " + i.toString()));

						}
					});
					i.printStackTrace();
				}
				return null;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void cancelled()
			{
				super.cancelled();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void failed()
			{
				super.failed();
				dialog.setResult("finished");
				dialog.close();
			}
		};
		new Thread(task).start();

		showWaitDialog("Save Project",
				"Saving Project : " + bmdProject.getName() + " to " + selectedFile.getAbsolutePath());
	}

	public BMDProject loadProject(File selectedFile)
	{
		final BMDProject returnProject = null;

		Task task = new Task<BMDProject>() {

			BMDProject loadedProject = null;

			@Override
			protected BMDProject call() throws Exception
			{
				try
				{
					FileInputStream fileIn = new FileInputStream(selectedFile);
					BufferedInputStream bIn = new BufferedInputStream(fileIn, 1024 * 2000);

					ObjectInputStream in = new ObjectInputStream(bIn);
					loadedProject = (BMDProject) in.readObject();
					in.close();
					fileIn.close();
				}
				catch (IOException i)
				{
					Platform.runLater(new Runnable() {

						@Override
						public void run()
						{
							BMDExpressEventBus.getInstance()
									.post(new ShowErrorEvent("Project file corrupted. " + i.toString()));

						}
					});
					i.printStackTrace();
				}
				catch (ClassNotFoundException c)
				{

					Platform.runLater(new Runnable() {

						@Override
						public void run()
						{
							BMDExpressEventBus.getInstance()
									.post(new ShowErrorEvent("Project file incorrect. " + c.toString()));

						}
					});

					c.printStackTrace();
				}

				return loadedProject;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void cancelled()
			{
				super.cancelled();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void failed()
			{
				super.failed();
				dialog.setResult("finished");
				dialog.close();
			}
		};
		new Thread(task).start();

		showWaitDialog("Load Project", "Loading Project from " + selectedFile.getAbsolutePath());
		return (BMDProject) task.getValue();
	}

	/*
	 * show a dialog with indeterminate Progress bar
	 */
	private void showWaitDialog(String titleTxt, String headerText)
	{

		dialog.setTitle(titleTxt);
		dialog.setHeaderText(headerText);
		dialog.setResizable(false);
		dialog.initOwner(owner);
		dialog.initModality(Modality.WINDOW_MODAL);
		ProgressBar progressBar = new ProgressBar();
		progressBar.setMaxHeight(20);
		progressBar.setMinWidth(275);

		VBox vBox = new VBox();
		vBox.getChildren().add(new Label("Progress"));
		vBox.getChildren().add(progressBar);
		dialog.getDialogPane().setContent(vBox);
		dialog.getDialogPane().setPrefSize(300, 200);
		dialog.getDialogPane().autosize();
		dialog.showAndWait();

	}

	public BMDProject importBMDFile(File selectedFile)
	{

		final BMDProject returnProject = null;

		Task task = new Task<BMDProject>() {

			BMDProject loadedProject = null;

			@Override
			protected BMDProject call() throws Exception
			{
				try
				{
					ProjectReader bmdProjectReader = new ProjectReader(selectedFile);
					bmdProjectReader.read();
					ConversionUtil conversion = new ConversionUtil();
					loadedProject = conversion.convertOldToNew(bmdProjectReader);
				}
				catch (Exception i)
				{
					i.printStackTrace();
				}

				return loadedProject;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void cancelled()
			{
				super.cancelled();
				dialog.setResult("finished");
				dialog.close();
			}

			@Override
			protected void failed()
			{
				super.failed();
				dialog.setResult("finished");
				dialog.close();
			}
		};
		new Thread(task).start();

		showWaitDialog("Load Project", "Loading Project from " + selectedFile.getAbsolutePath());
		return (BMDProject) task.getValue();

	}

	private void saveAsJSON(BMDProject project, File theFile) throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();

		/**
		 * To make the JSON String pretty use the below code
		 */
		mapper.writerWithDefaultPrettyPrinter().writeValue(theFile, project);

	}

	public BMDProject importJSONFile(File selectedFile) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		BMDProject projecttest = mapper.readValue(selectedFile, BMDProject.class);

		return projecttest;
	}

}
