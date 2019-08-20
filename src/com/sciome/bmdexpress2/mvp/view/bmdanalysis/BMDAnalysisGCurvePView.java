package com.sciome.bmdexpress2.mvp.view.bmdanalysis;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.math.NumberUtils;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.GCurvePInput;
import com.sciome.bmdexpress2.mvp.presenter.bmdanalysis.BMDAnalysisGCurvePPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis.IBMDAnalysisGCurvePView;
import com.sciome.bmdexpress2.service.BMDAnalysisService;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.bmds.shared.BMRFactor;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BMDAnalysisGCurvePView extends BMDExpressViewBase
		implements IBMDAnalysisGCurvePView, Initializable
{

	@FXML
	private TextField					bootStrapsTextField;

	// ComboBoxes
	@FXML
	private ComboBox					bMRFactorComboBox;
	@FXML
	private ComboBox					pValueConfidenceInterval;

	@FXML
	private ComboBox					adjustControlDoseComboBox;

	@FXML
	private CheckBox					adjustControlDoseCheckBox;

	@FXML
	private ProgressBar					progressBar;
	@FXML
	private Label						progressLabel;

	@FXML
	private Button						startButton;
	@FXML
	private Button						saveSettingsButton;
	@FXML
	private Button						cancelButton;

	@FXML
	private Label						expressionDataLabel;
	@FXML
	private Label						oneWayANOVADataLabel;
	@FXML
	private Label						oneWayANOVADataLabelLabel;

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
		this.progressBar.setVisible(true);
		GCurvePInputParameters inputParameters = new GCurvePInputParameters();
		inputParameters.setBMR(
				Float.valueOf(((BMRFactor) this.bMRFactorComboBox.getValue()).getValue()).floatValue());
		inputParameters.setBootStraps(Integer.valueOf(this.bootStrapsTextField.getText()).intValue());
		inputParameters.setpValueCutoff(
				Float.valueOf(this.pValueConfidenceInterval.getValue().toString()).floatValue());

		if (this.adjustControlDoseCheckBox.selectedProperty().getValue())
			inputParameters.setControlDoseAdjustment(
					Double.valueOf(this.adjustControlDoseComboBox.valueProperty().getValue().toString()));
		else
			inputParameters.setControlDoseAdjustment(null);

		presenter.performBMDAnalysisGCurveP(inputParameters);
	}

	public void handle_saveSettingsButtonPressed(ActionEvent event)
	{

		// Set check box values
		input.setBmrFactor((BMRFactor) this.bMRFactorComboBox.getValue());
		if (NumberUtils.isDigits(this.bootStrapsTextField.getText()))
			input.setBootStrapIterations(Integer.valueOf(this.bootStrapsTextField.getText()).intValue());

		if (NumberUtils.isCreatable(this.pValueConfidenceInterval.getValue().toString()))
			input.setpValueConfidenceInterval(
					Float.valueOf(this.pValueConfidenceInterval.getValue().toString()).floatValue());

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

		if (this.progressBar.isVisible())
		{
			presenter.cancel();
			this.progressBar.setVisible(false);

		}
		else
		{
			presenter.cancel();
			this.closeWindow();
		}

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void initData(List<IStatModelProcessable> processableData)
	{
		presenter.initData(processableData);
		this.progressBar.setVisible(false);

		this.processableData = processableData;

		if (processableData.size() > 1)
		{
			oneWayANOVADataLabelLabel.setVisible(false);
			expressionDataLabel.setText("Multiple Data Sets");
		}
		else if (processableData.get(0).getParentDataSetName() == null)
		{
			oneWayANOVADataLabelLabel.setVisible(false);
			expressionDataLabel.setText(processableData.toString());
		}
		else
		{
			oneWayANOVADataLabel.setText(processableData.get(0).toString());
			expressionDataLabel.setText(processableData.get(0).getParentDataSetName());
		}

		input = BMDExpressProperties.getInstance().getGCurvePnput();

		// pValue Cut OFF
		pValueConfidenceInterval.getItems().add("0.01");
		pValueConfidenceInterval.getItems().add("0.05");
		pValueConfidenceInterval.getItems().add("0.10");
		pValueConfidenceInterval.getItems().add("0.5");
		pValueConfidenceInterval.getItems().add("1");
		pValueConfidenceInterval.getSelectionModel().select(input.getpValueConfidenceInterval());

		// add data to the bmrFactor combobox
		bMRFactorComboBox.getItems().addAll(initBMRFactors());
		bMRFactorComboBox.getSelectionModel().select(input.getBmrFactor());

		this.bootStrapsTextField.setText(String.valueOf(input.getBootStrapIterations()));

		adjustControlDoseComboBox.getItems().addAll(initControlDoseAdjustment());
		this.adjustControlDoseComboBox.setDisable(true);
		adjustControlDoseCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (!newValue.booleanValue())
					BMDAnalysisGCurvePView.this.adjustControlDoseComboBox.setDisable(true);
				else
					BMDAnalysisGCurvePView.this.adjustControlDoseComboBox.setDisable(false);
			}
		});

		adjustControlDoseCheckBox.selectedProperty().setValue(false);

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
	public void initializeProgressBar(String label)
	{
		progressLabel.setText(label);
		progressBar.setProgress(0.0);

	}

	@Override
	public void updateProgressBar(String label, double value)
	{
		progressLabel.setText(label);
		progressBar.setProgress(value);
	}

	@Override
	public void clearProgressBar()
	{
		progressLabel.setText("");
		progressBar.setProgress(0.0);
		this.startButton.setDisable(false);
		this.progressBar.setVisible(false);

	}

	@Override
	public void finishedBMDAnalysis()
	{
		startButton.setDisable(false);

	}

	@Override
	public void startedBMDAnalysis()
	{
		startButton.setDisable(true);
		startButton.setDisable(true);

	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		this.close();
		stage.close();

	}

	private List<BMRFactor> initBMRFactors()
	{
		List<BMRFactor> factors = new ArrayList<>();
		factors.add(new BMRFactor("0.522 (1%)", "0.522"));
		factors.add(new BMRFactor("1 SD", "1.0"));
		factors.add(new BMRFactor("1.021 (5%)", "1.021"));
		factors.add(new BMRFactor("1.349 (10%)", "1.349"));
		factors.add(new BMRFactor("1.581 (15%)", "1.581"));
		factors.add(new BMRFactor("1.932484 (25%)", "1.932484"));
		factors.add(new BMRFactor("2 SD", "2.0"));
		factors.add(new BMRFactor("2.600898 (50%)", "2.600898"));
		factors.add(new BMRFactor("2.855148 (60%)", "2.855148"));
		factors.add(new BMRFactor("3 SD", "3.0"));
		return factors;
	}

	private List<Double> initControlDoseAdjustment()
	{
		List<Double> adjustments = new ArrayList<>();
		adjustments.add(0.5);
		adjustments.add(0.4);
		adjustments.add(0.3);
		adjustments.add(0.2);
		adjustments.add(0.1);
		adjustments.add(0.05);

		return adjustments;
	}

}
