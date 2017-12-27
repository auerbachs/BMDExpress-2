package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.net.URL;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.CurveFitPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.ICurveFitView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ActualCurveFitView extends BMDExpressViewBase implements ICurveFitView, Initializable
{
	@FXML
	private HBox				chartBox;
	@FXML
	private CheckBox			meanAndDeviationCheckBox;
	@FXML
	private CheckBox			allDataCheckBox;
	@FXML
	private CheckBox			logDosesCheckBox;
	@FXML
	private ComboBox			modelNameComboBox;
	@FXML
	private ComboBox			idComboBox;
	@FXML
	private TextField			modelTextField;
	@FXML
	private TextField			bmdlTextField;
	@FXML
	private TextField			bmdTextField;
	@FXML
	private TextField			bmduTextField;
	@FXML
	private TextField			fitPTextField;
	@FXML
	private TextField			aicTextField;
	@FXML
	private Button				printButton;
	@FXML
	private Button				clearButton;
	@FXML
	private Button				propertiesButton;
	@FXML
	private Button				closeButton;
	
	CurveFitPresenter			presenter;
	
	public ActualCurveFitView()
	{
		this(BMDExpressEventBus.getInstance());
	}
	
	public ActualCurveFitView(BMDExpressEventBus eventBus) {
		presenter = new CurveFitPresenter(this, eventBus);
	}
	
	public void initData(BMDResult bmdResult, ProbeStatResult probeStatResult) {
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	@Override
	public void closeWindow() {
		
	}

	@Override
	public void close() {
		
	}
	
	public void handle_printButtonPressed(ActionEvent event) {
		
	}
	
	public void handle_clearButtonPressed(ActionEvent event) {
		
	}

	public void handle_propertiesButtonPressed(ActionEvent event) {
		
	}

	public void handle_closeButtonPressed(ActionEvent event) {
		
	}
}
