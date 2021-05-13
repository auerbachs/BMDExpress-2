package com.sciome.bmdexpress2.shared;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;

public class BMDExpressInformation
{

	private static BMDExpressInformation instance = null;

	public static BMDExpressInformation getInstance()
	{
		if (instance == null)
		{
			instance = new BMDExpressInformation();
		}
		return instance;
	}

	public void showVersionDialog(Scene scene, String version)
	{
		Platform.runLater(()->{
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("About");
			dialog.setHeaderText("Features and Bugs " + version);
	
			dialog.getDialogPane().setMinHeight(600);
			dialog.getDialogPane().setMinWidth(800);
			ScrollPane sp = new ScrollPane();
	
			
			String content = BMDExpressProperties.getInstance().getVersionInfo();
		
			AnchorPane ap = new AnchorPane();
		
	
			dialog.getDialogPane().getChildren().add(ap);
	
			ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
			dialog.initOwner(scene.getWindow());
			dialog.initModality(Modality.WINDOW_MODAL);
	
			center(dialog, scene, 600, 800);
			WebView label = new WebView();	
			label.getEngine().loadContent(content);
			ap.getChildren().add(label);
			label.setMinHeight(500);
			label.setMaxHeight(500);
	
			dialog.showAndWait();
		});

	}

	public void showSplashDialog(Scene scene)
	{
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Welcome to BMDExpress 2.0");

		Image simage = new Image("splash.png");

		dialog.setHeight(simage.getHeight() + 100);
		dialog.setWidth(simage.getWidth());
		dialog.getDialogPane().setMinHeight(simage.getHeight() + 100);
		dialog.getDialogPane().setMinWidth(simage.getWidth());
		ImageView splash = new ImageView(new Image("splash.png"));
		dialog.getDialogPane().getChildren().add(splash);
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.initOwner(scene.getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		center(dialog, scene, simage.getWidth(), simage.getHeight() + 100);
		dialog.showAndWait();

	}

	private void center(Dialog dialog, Scene scene, double w2, double h2)
	{
		Window window = scene.getWindow();
		double x1 = window.getX();
		double y1 = window.getY();
		double w1 = window.getWidth();
		double h1 = window.getHeight();

		double x = x1 + w1 / 2 - w2 / 2;
		double y = y1 + h1 / 2 - h2 / 2;
		dialog.setX(x);
		dialog.setY(y);
	}

	public void showLicense(Scene scene)
	{
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("License Agreement");

		dialog.getDialogPane().setMinHeight(600);
		dialog.getDialogPane().setMinWidth(800);

		WebView label = new WebView();
		label.setMaxHeight(500);
		String content = BMDExpressProperties.getInstance().getLicense();
		label.getEngine().loadContent(content);
		AnchorPane ap = new AnchorPane();
		ap.getChildren().add(label);

		dialog.getDialogPane().getChildren().add(ap);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.initOwner(scene.getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);

		center(dialog, scene, 600, 800);

		dialog.showAndWait();

	}

	public void showTutorial(Scene scene)
	{
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Tutorial");

		dialog.getDialogPane().setMinHeight(600);
		dialog.getDialogPane().setMinWidth(800);
		ScrollPane sp = new ScrollPane();

		Label label = new Label("Placeholder for tutorial.");
		AnchorPane ap = new AnchorPane();
		ap.getChildren().add(label);

		dialog.getDialogPane().getChildren().add(ap);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.initOwner(scene.getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);

		center(dialog, scene, 600, 800);

		dialog.showAndWait();

	}

}
