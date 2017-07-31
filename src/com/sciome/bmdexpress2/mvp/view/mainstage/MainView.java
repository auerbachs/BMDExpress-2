package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.sciome.bmdexpress2.mvp.presenter.mainstage.MainPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainView;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

public class MainView extends BMDExpressViewBase implements IMainView, Initializable
{

	MainPresenter presenter;

	@FXML
	private Label projectNameLabel;

	@FXML
	private Label currentSelectionLabel;

	@FXML
	private Label actionLabel;

	@FXML
	private ImageView sciomeImageView;
	@FXML
	private ImageView ntpImageView;
	@FXML
	private ImageView healthCanadaImageView;
	@FXML
	private ImageView epaImageView;

	@FXML
	private SwingNode swingNode;

	public MainView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public MainView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new MainPresenter(this, eventBus);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				swingNode.setContent(new JLabel(""));
			}
		});
	}

	@Override
	public void updateProjectLabel(String label)
	{
		projectNameLabel.setText(label);

	}

	@Override
	public void updateSelectionLabel(String label)
	{
		currentSelectionLabel.setText(label);
	}

	@Override
	public void updateActionStatusLabel(String label)
	{
		actionLabel.setText(label);

	}

	@FXML
	public void exitApplication(ActionEvent event)
	{
		System.out.println("exiting....");
	}

	@Override
	public void showErrorAlert(String errorString)
	{
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle("Error");

		alert.setHeaderText("Error Alert.");
		alert.initOwner(this.projectNameLabel.getScene().getWindow());
		alert.initModality(Modality.WINDOW_MODAL);

		alert.setContentText(errorString);

		alert.showAndWait();

	}

	public void handle_SciomeClick(MouseEvent event)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(BMDExpressConstants.getInstance().SCIOM_WWW));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	public void handle_SciomeEnter(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.HAND);
		sciomeImageView.setOpacity(0.5);
	}

	public void handle_SciomeExit(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.DEFAULT);
		sciomeImageView.setOpacity(1.0);
	}

	public void handle_NTPClick(MouseEvent event)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(BMDExpressConstants.getInstance().NTP_WWW));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

	}

	public void handle_NTPEnter(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.HAND);
		ntpImageView.setOpacity(0.5);
	}

	public void handle_NTPExit(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.DEFAULT);
		ntpImageView.setOpacity(1.0);
	}

	public void handle_EPAClick(MouseEvent event)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(BMDExpressConstants.getInstance().EPA_WWW));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

	}

	public void handle_EPAEnter(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.HAND);
		epaImageView.setOpacity(0.5);
	}

	public void handle_EPAExit(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.DEFAULT);
		epaImageView.setOpacity(1.0);
	}

	public void handle_HCClick(MouseEvent event)
	{
		try
		{
			java.awt.Desktop.getDesktop().browse(new URI(BMDExpressConstants.getInstance().HC_WWW));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

	}

	public void handle_HCEnter(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.HAND);
		healthCanadaImageView.setOpacity(0.5);
	}

	public void handle_HCExit(MouseEvent event)
	{
		projectNameLabel.getScene().setCursor(Cursor.DEFAULT);
		healthCanadaImageView.setOpacity(1.0);
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

	@Override
	public void showMessageDialog(String message)
	{
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle("Message");

		alert.setHeaderText("Message.");
		alert.initOwner(this.projectNameLabel.getScene().getWindow());
		alert.initModality(Modality.WINDOW_MODAL);

		alert.setContentText(message);

		alert.showAndWait();

	}

	@Override
	public void showWarningDialog(String warning)
	{
		Alert alert = new Alert(AlertType.WARNING);

		alert.setTitle("Warning");

		alert.setHeaderText("Warning.");
		alert.initOwner(this.projectNameLabel.getScene().getWindow());
		alert.initModality(Modality.WINDOW_MODAL);

		alert.setContentText(warning);

		alert.showAndWait();

	}

}
