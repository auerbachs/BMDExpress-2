package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;


import com.sciome.bmdexpress2.mvp.presenter.mainstage.MatrixSwingNodePresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMatrixSwingNodeView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.MatrixDataPreviewer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MatrixSwingNodeView extends BMDExpressViewBase implements IMatrixSwingNodeView, Initializable
{

	@FXML
	VBox swingNode;

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
		swingNode.getChildren().clear();
		MatrixDataPreviewer pane = new MatrixDataPreviewer(matrixData);
		swingNode.getChildren().add(pane);
		
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}
}
