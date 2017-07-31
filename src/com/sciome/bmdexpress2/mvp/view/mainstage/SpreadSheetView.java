package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.presenter.mainstage.SpreadSheetPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.view.mainstage.dataview.BMDExpressDataView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.ISpreadSheetView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SpreadSheetView extends BMDExpressViewBase implements ISpreadSheetView, Initializable
{

	@FXML
	Button						doneButton;

	@FXML
	VBox						vBox;

	private BMDExpressDataView	dataTableView;
	SpreadSheetPresenter		presenter;

	public SpreadSheetView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public SpreadSheetView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new SpreadSheetPresenter(this, eventBus);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void closeWindow()
	{

		Stage stage = (Stage) doneButton.getScene().getWindow();
		this.close();
		stage.close();
	}

	public void handle_Done()
	{
		closeWindow();
	}

	public void initData(BMDExpressDataView dataTableView)
	{
		vBox.getChildren().add(0, dataTableView);
		VBox.setVgrow(dataTableView, Priority.ALWAYS);
		VBox.setMargin(dataTableView, new Insets(10, 10, 10, 10));
		this.dataTableView = dataTableView;
	}

	@Override
	public void close()
	{
		if (dataTableView != null)
		{
			dataTableView.close();
		}
		if (presenter != null)
			presenter.destroy();

	}

}
