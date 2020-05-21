package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.InfoPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IInfoView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class InfoView extends BMDExpressViewBase implements IInfoView, Initializable
{

	InfoPresenter				presenter;

	@FXML
	private ListView<String>	infoListView;

	public InfoView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public InfoView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new InfoPresenter(this, eventBus);
	}

	@Override
	public void setWarningMessage(String value)
	{

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void showAnalysisInfo(AnalysisInfo analysisInfo)
	{
		infoListView.getItems().clear();

		infoListView.getItems().addAll(analysisInfo.getNotes());

	}

	@Override
	public void clearList()
	{
		infoListView.getItems().clear();

	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
