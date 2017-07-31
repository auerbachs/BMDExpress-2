package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import com.sciome.bmdexpress2.mvp.presenter.mainstage.MatrixSwingNodePresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMatrixSwingNodeView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.MatrixDataPreviewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MatrixSwingNodeView extends BMDExpressViewBase implements IMatrixSwingNodeView, Initializable
{

	@FXML
	SwingNode swingNode;

	@FXML
	Button doneButton;
	@FXML
	Label headerLabel;
	@FXML
	VBox vBox;

	MatrixSwingNodePresenter presenter;

	public MatrixSwingNodeView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public MatrixSwingNodeView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new MatrixSwingNodePresenter(this, eventBus);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void closeWindow()
	{
		handle_Done();
	}

	public void handle_Done()
	{
		Stage stage = (Stage) doneButton.getScene().getWindow();
		stage.close();
	}

	public void initData(String headerText, MatrixData matrixData)
	{
		headerLabel.setText(headerText + ", " + matrixData.rows() + " rows.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				MatrixDataPreviewer pane = new MatrixDataPreviewer(matrixData);
				swingNode.setContent(pane);
			}
		});

		this.swingNode.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth)
			{

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						swingNode.getContent().repaint();
					}
				});

			}
		});
		this.swingNode.getScene().heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2)
			{

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						swingNode.getContent().repaint();
					}
				});

			}
		});

	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}
}
