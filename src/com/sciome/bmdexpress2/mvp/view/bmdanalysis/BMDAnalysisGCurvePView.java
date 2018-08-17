package com.sciome.bmdexpress2.mvp.view.bmdanalysis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.GCurvePInput;
import com.sciome.bmdexpress2.mvp.presenter.bmdanalysis.BMDAnalysisGCurvePPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis.IBMDAnalysisGCurvePView;
import com.sciome.bmdexpress2.service.BMDAnalysisService;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BMDAnalysisGCurvePView extends BMDExpressViewBase
		implements IBMDAnalysisGCurvePView, Initializable
{

	BMDAnalysisGCurvePPresenter			presenter;

	private List<IStatModelProcessable>	processableData;

	private GCurvePInput				input;

	public BMDAnalysisGCurvePView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public BMDAnalysisGCurvePView(BMDExpressEventBus eventBus)
	{
		super();
		IBMDAnalysisService service = new BMDAnalysisService();
		presenter = new BMDAnalysisGCurvePPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getGCurvePnput();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	/*
	 * use clicked close button
	 */
	public void handle_close(ActionEvent event)
	{

	}

	/*
	 * use clicked start button
	 */

	public void handle_start(ActionEvent event)
	{
		GCurvePInputParameters inputParameters = new GCurvePInputParameters();

		presenter.performBMDAnalysisGCurveP(inputParameters);
	}

	@SuppressWarnings("restriction")
	public void handle_saveSettingsButtonPressed(ActionEvent event)
	{

		// Set check box values

		BMDExpressProperties.getInstance().saveGCurvePInput(input);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved Settings");
		alert.setHeaderText(null);
		alert.setContentText("Your settings have been saved");

		alert.showAndWait();

	}

	/*
	 * use clicked done button
	 */
	public void handle_cancel(ActionEvent event)
	{

		presenter.cancel();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> processableData)
	{
		presenter.initData(processableData);

		this.processableData = processableData;

	}

	@Override
	public void close()
	{
		if (presenter != null)
		{
			presenter.close();
		}

	}

	@Override
	public void clearProgressBar()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProgressBar(String label, double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeProgressBar(String label)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void finishedBMDAnalysis()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void startedBMDAnalysis()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void closeWindow()
	{
		// TODO Auto-generated method stub

	}

}
