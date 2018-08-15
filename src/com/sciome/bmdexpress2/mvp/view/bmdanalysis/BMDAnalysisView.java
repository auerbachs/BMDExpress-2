package com.sciome.bmdexpress2.mvp.view.bmdanalysis;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDInput;
import com.sciome.bmdexpress2.mvp.presenter.bmdanalysis.BMDAnalysisPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis.IBMDAnalysisView;
import com.sciome.bmdexpress2.service.BMDAnalysisService;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.BMRFactor;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionBMDLandBMDU;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionWithFlaggedHillModelEnum;
import com.sciome.bmdexpress2.util.bmds.shared.BestPolyModelTestEnum;
import com.sciome.bmdexpress2.util.bmds.shared.ExponentialModel;
import com.sciome.bmdexpress2.util.bmds.shared.FlagHillModelDoseEnum;
import com.sciome.bmdexpress2.util.bmds.shared.HillModel;
import com.sciome.bmdexpress2.util.bmds.shared.PolyModel;
import com.sciome.bmdexpress2.util.bmds.shared.PowerModel;
import com.sciome.bmdexpress2.util.bmds.shared.RestrictPowerEnum;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BMDAnalysisView extends BMDExpressViewBase implements IBMDAnalysisView, Initializable
{

	BMDAnalysisPresenter				presenter;

	// FXML injection

	// checkboxes
	@FXML
	private CheckBox					exponential2CheckBox;
	@FXML
	private CheckBox					exponential3CheckBox;
	@FXML
	private CheckBox					exponential4CheckBox;
	@FXML
	private CheckBox					exponential5CheckBox;
	@FXML
	private CheckBox					hillCheckBox;
	@FXML
	private CheckBox					powerCheckBox;
	@FXML
	private CheckBox					linearCheckBox;
	@FXML
	private CheckBox					poly2CheckBox;
	@FXML
	private CheckBox					poly3CheckBox;
	@FXML
	private CheckBox					poly4CheckBox;

	@FXML
	private CheckBox					constantVarianceCheckBox;

	@FXML
	private CheckBox					flagHillkParamCheckBox;
	@FXML
	private CheckBox					setThreadCheckBox;

	// textfields
	@FXML
	private TextField					maximumIterationsTextField;
	@FXML
	private TextField					modifyFlaggedHillBMDTextField;

	// ComboBoxes
	@FXML
	private ComboBox					bMRFactorComboBox;
	@FXML
	private ComboBox					confidenceLevelComboBox;
	@FXML
	private ComboBox					restrictPowerComboBox;

	@FXML
	private ComboBox					bestPolyTestComboBox;
	@FXML
	private ComboBox					pValueCutoffComboBox;

	@FXML
	private ComboBox					flagHillkParamComboBox;
	@FXML
	private ComboBox					bestModelSeletionWithFlaggedHillComboBox;

	@FXML
	private ComboBox					bmdlBmduComboBox;

	@FXML
	private ComboBox					numberOfThreadsComboBox;
	@FXML
	private ComboBox					killTimeComboBox;

	// labels
	@FXML
	private Label						expressionDataLabel;
	@FXML
	private Label						oneWayANOVADataLabel;
	@FXML
	private Label						oneWayANOVADataLabelLabel;
	@FXML
	private Label						modifyFlaggedHillBMDLabel;
	@FXML
	private Label						bestModelSeletionWithFlaggedHillLabel;
	@FXML
	private Label						restrictPowerLabel;

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
	private VBox						mainVBox;
	// anchor panes
	@FXML
	private AnchorPane					startCancelPane;
	@FXML
	private AnchorPane					threadPane;
	@FXML
	private AnchorPane					modelSelectionPane;
	@FXML
	private AnchorPane					parametersPane;
	@FXML
	private AnchorPane					modelsPane;
	@FXML
	private AnchorPane					dataOptionsPane;

	private List<IStatModelProcessable>	processableData;

	private boolean						selectModelsOnly	= false;

	private BMDInput					input;

	public BMDAnalysisView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public BMDAnalysisView(BMDExpressEventBus eventBus)
	{
		super();
		IBMDAnalysisService service = new BMDAnalysisService();
		presenter = new BMDAnalysisPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getBmdInput();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		this.progressBar.setVisible(false);
		this.exponential2CheckBox.setDisable(false);

	}

	/*
	 * use clicked close button
	 */
	@Override
	public void handle_close(ActionEvent event)
	{

	}

	/*
	 * use clicked start button
	 */
	@Override
	public void handle_start(ActionEvent event)
	{
		// create InputParameters object based on things that are selected.

		this.progressBar.setVisible(true);
		ModelInputParameters inputParameters = assignParameters();
		ModelSelectionParameters modelSectionParameters = assignModelSelectionParameters();
		List<StatModel> modelsToRun = new ArrayList<>();
		if (hillCheckBox.isSelected())
		{
			HillModel hillModel = new HillModel();
			hillModel.setVersion(BMDExpressProperties.getInstance().getHillVersion());
			modelsToRun.add(hillModel);
		}
		if (powerCheckBox.isSelected())
		{
			PowerModel powerModel = new PowerModel();
			powerModel.setVersion(BMDExpressProperties.getInstance().getPowerVersion());
			modelsToRun.add(powerModel);
		}
		if (linearCheckBox.isSelected())
		{
			PolyModel linearModel = new PolyModel();
			linearModel.setVersion(BMDExpressProperties.getInstance().getPolyVersion());
			linearModel.setDegree(1);
			modelsToRun.add(linearModel);
		}
		if (poly2CheckBox.isSelected())

		{
			PolyModel poly2Model = new PolyModel();
			poly2Model.setDegree(2);
			poly2Model.setVersion(BMDExpressProperties.getInstance().getPolyVersion());
			modelsToRun.add(poly2Model);
		}
		if (poly3CheckBox.isSelected())
		{
			PolyModel poly3Model = new PolyModel();
			poly3Model.setDegree(3);
			poly3Model.setVersion(BMDExpressProperties.getInstance().getPolyVersion());
			modelsToRun.add(poly3Model);
		}
		if (poly4CheckBox.isSelected())
		{
			PolyModel poly4Model = new PolyModel();
			poly4Model.setDegree(4);
			poly4Model.setVersion(BMDExpressProperties.getInstance().getPolyVersion());
			modelsToRun.add(poly4Model);
		}

		if (exponential2CheckBox.isSelected())
		{
			ExponentialModel exponentialModel = new ExponentialModel();
			exponentialModel.setVersion(BMDExpressProperties.getInstance().getExponentialVersion());
			exponentialModel.setOption(2);
			modelsToRun.add(exponentialModel);
		}
		if (exponential3CheckBox.isSelected())
		{
			ExponentialModel exponentialModel = new ExponentialModel();
			exponentialModel.setVersion(BMDExpressProperties.getInstance().getExponentialVersion());
			modelsToRun.add(exponentialModel);
			exponentialModel.setOption(3);
		}
		if (exponential4CheckBox.isSelected())
		{
			ExponentialModel exponentialModel = new ExponentialModel();
			exponentialModel.setVersion(BMDExpressProperties.getInstance().getExponentialVersion());
			exponentialModel.setOption(4);
			modelsToRun.add(exponentialModel);
		}
		if (exponential5CheckBox.isSelected())
		{
			ExponentialModel exponentialModel = new ExponentialModel();
			exponentialModel.setVersion(BMDExpressProperties.getInstance().getExponentialVersion());
			exponentialModel.setOption(5);
			modelsToRun.add(exponentialModel);
		}

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (inputParameters.getNumThreads() > availableProcessors * 4)
		{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Number Of Threads Exceeds Number of Available.");
			alert.setContentText(
					"The number of threads that you chose is more than 4 times the number of available processors you have on your machine.  The number avaiable processors detected by this application is: "
							+ availableProcessors);

			alert.showAndWait();
		}
		// start the BMD Analysis
		if (selectModelsOnly)
		{
			presenter.performReselectParameters(inputParameters, modelSectionParameters);
			this.closeWindow();
		}
		else
			presenter.performBMDAnalysis(inputParameters, modelSectionParameters, modelsToRun);
	}

	@Override
	public void handle_saveSettingsButtonPressed(ActionEvent event)
	{
		// Set check box values
		input.setExp2(this.exponential2CheckBox.isSelected());
		input.setExp3(this.exponential3CheckBox.isSelected());
		input.setExp4(this.exponential4CheckBox.isSelected());
		input.setExp5(this.exponential5CheckBox.isSelected());
		input.setLinear(this.linearCheckBox.isSelected());
		input.setPoly2(this.poly2CheckBox.isSelected());
		input.setPoly3(this.poly3CheckBox.isSelected());
		input.setPoly4(this.poly4CheckBox.isSelected());
		input.setHill(this.hillCheckBox.isSelected());
		input.setPower(this.powerCheckBox.isSelected());
		input.setConstantVariance(this.constantVarianceCheckBox.isSelected());
		input.setFlagHillModel(this.flagHillkParamCheckBox.isSelected());

		// Set numerical values
		input.setMaxIterations(Integer.parseInt(this.maximumIterationsTextField.getText()));
		input.setNumThreads(Integer.parseInt(this.numberOfThreadsComboBox.getEditor().getText()));
		input.setKillTime(Integer.parseInt(
				this.killTimeComboBox.getEditor().getText().replaceAll("\\(default\\)", "").trim()));
		input.setConfidenceLevel(Double.parseDouble(this.confidenceLevelComboBox.getEditor().getText()));
		input.setpValueCutoff(Double.parseDouble(this.pValueCutoffComboBox.getEditor().getText()));
		input.setModifyBMDFlaggedHill(Double.parseDouble(this.modifyFlaggedHillBMDTextField.getText()));

		// Set String values
		input.setBMRFactor((BMRFactor) this.bMRFactorComboBox.getValue());
		input.setRestrictPower((RestrictPowerEnum) this.restrictPowerComboBox.getValue());
		input.setBestPolyModelTest((BestPolyModelTestEnum) this.bestPolyTestComboBox.getValue());
		input.setkParameterLessThan((FlagHillModelDoseEnum) this.flagHillkParamComboBox.getValue());
		input.setBestModelWithFlaggedHill(
				(BestModelSelectionWithFlaggedHillModelEnum) this.bestModelSeletionWithFlaggedHillComboBox
						.getValue());

		BMDExpressProperties.getInstance().saveBMDInput(input);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved Settings");
		alert.setHeaderText(null);
		alert.setContentText("Your settings have been saved");

		alert.showAndWait();
	}

	/*
	 * use clicked done button
	 */
	@Override
	public void handle_cancel(ActionEvent event)
	{
		// if presenter cancels the process it will return true
		// otherwise interpret to cancel and close the window
		if (!this.progressBar.isVisible() || this.selectModelsOnly)
		{
			this.closeWindow();
		}
		else
		{
			presenter.cancel();
		}

	}

	private void setModifyBMDOfFlaggedHillEnabledness()
	{
		if (bestModelSeletionWithFlaggedHillComboBox.getSelectionModel().getSelectedIndex() == 3
				&& !flagHillkParamCheckBox.isDisable())
		{
			modifyFlaggedHillBMDLabel.setDisable(false);
			modifyFlaggedHillBMDTextField.setDisable(false);
		}
		else
		{
			modifyFlaggedHillBMDLabel.setDisable(true);
			modifyFlaggedHillBMDTextField.setDisable(true);
		}
	}

	@Override
	public void handle_FlagHillCheckBox(ActionEvent event)
	{

		if (hillCheckBox.isSelected())
		{
			flagHillkParamComboBox.setDisable(!flagHillkParamCheckBox.isSelected());
			bestModelSeletionWithFlaggedHillLabel.setDisable(!flagHillkParamCheckBox.isSelected());
			bestModelSeletionWithFlaggedHillComboBox.setDisable(!flagHillkParamCheckBox.isSelected());
			modifyFlaggedHillBMDLabel.setDisable(!flagHillkParamCheckBox.isSelected());
			modifyFlaggedHillBMDTextField.setDisable(!flagHillkParamCheckBox.isSelected());

			if (flagHillkParamCheckBox.isSelected())
			{
				setModifyBMDOfFlaggedHillEnabledness();
			}

		}

	}

	@Override
	public void handle_HillCheckBox(ActionEvent event)
	{

		flagHillkParamCheckBox.setDisable(!hillCheckBox.isSelected());
		flagHillkParamComboBox.setDisable(!hillCheckBox.isSelected());
		bestModelSeletionWithFlaggedHillLabel.setDisable(!hillCheckBox.isSelected());
		bestModelSeletionWithFlaggedHillComboBox.setDisable(!hillCheckBox.isSelected());
		modifyFlaggedHillBMDLabel.setDisable(!hillCheckBox.isSelected());
		modifyFlaggedHillBMDTextField.setDisable(!hillCheckBox.isSelected());
		handle_FlagHillCheckBox(event);
	}

	@Override
	public void handle_PowerCheckBox(ActionEvent event)
	{
		restrictPowerComboBox.setDisable(!powerCheckBox.isSelected());
		restrictPowerLabel.setDisable(!powerCheckBox.isSelected());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> processableData, boolean selectModelsOnly)
	{
		presenter.initData(processableData);

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

		// init checkboxes
		exponential2CheckBox.setSelected(input.isExp2());
		exponential3CheckBox.setSelected(input.isExp3());
		exponential4CheckBox.setSelected(input.isExp4());
		exponential5CheckBox.setSelected(input.isExp5());
		linearCheckBox.setSelected(input.isLinear());
		poly2CheckBox.setSelected(input.isPoly2());
		poly3CheckBox.setSelected(input.isPoly3());
		poly4CheckBox.setSelected(input.isPoly4());
		hillCheckBox.setSelected(input.isHill());
		powerCheckBox.setSelected(input.isPower());
		constantVarianceCheckBox.setSelected(input.isConstantVariance());
		flagHillkParamCheckBox.setSelected(input.isFlagHillModel());

		// init confidence level
		confidenceLevelComboBox.getItems().add("0.95");
		confidenceLevelComboBox.getItems().add("0.99");
		confidenceLevelComboBox.getSelectionModel().select(input.getConfidenceLevel());

		// init restrict power
		restrictPowerComboBox.getItems().addAll(RestrictPowerEnum.values());
		restrictPowerComboBox.getSelectionModel().select(input.getRestrictPower());

		// init best poly model test
		bestPolyTestComboBox.getItems().setAll(BestPolyModelTestEnum.values());
		bestPolyTestComboBox.getSelectionModel().select(input.getBestPolyModelTest());

		// pValue Cut OFF
		pValueCutoffComboBox.getItems().add("0.01");
		pValueCutoffComboBox.getItems().add("0.05");
		pValueCutoffComboBox.getItems().add("0.10");
		pValueCutoffComboBox.getItems().add("0.5");
		pValueCutoffComboBox.getItems().add("1");
		pValueCutoffComboBox.getSelectionModel().select(input.getpValueCutoff());

		flagHillkParamComboBox.getItems().setAll(FlagHillModelDoseEnum.values());
		flagHillkParamComboBox.getSelectionModel().select(input.getkParameterLessThan());

		bmdlBmduComboBox.getItems().setAll(BestModelSelectionBMDLandBMDU.values());
		bmdlBmduComboBox.getSelectionModel().select(BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE);

		bestModelSeletionWithFlaggedHillComboBox.getItems()
				.setAll(BestModelSelectionWithFlaggedHillModelEnum.values());

		bestModelSeletionWithFlaggedHillComboBox.getSelectionModel()
				.select(input.getBestModelWithFlaggedHill());

		bestModelSeletionWithFlaggedHillComboBox.valueProperty()
				.addListener(new ChangeListener<BestModelSelectionWithFlaggedHillModelEnum>() {

					@Override
					public void changed(
							ObservableValue<? extends BestModelSelectionWithFlaggedHillModelEnum> observable,
							BestModelSelectionWithFlaggedHillModelEnum oldValue,
							BestModelSelectionWithFlaggedHillModelEnum newValue)
					{
						setModifyBMDOfFlaggedHillEnabledness();
					}

				});

		// let's add 100 threads to drop down
		for (int i = 1; i <= 100; i++)
		{
			numberOfThreadsComboBox.getItems().add(String.valueOf(i));
		}
		numberOfThreadsComboBox.setValue(input.getNumThreads());

		// Add values to kill time combo box
		killTimeComboBox.getItems().add("30");
		killTimeComboBox.getItems().add("60");
		killTimeComboBox.getItems().add("90");
		killTimeComboBox.getItems().add("120");
		killTimeComboBox.getItems().add("150");
		killTimeComboBox.getItems().add("180");
		killTimeComboBox.getItems().add("210");
		killTimeComboBox.getItems().add("240");
		killTimeComboBox.getItems().add("270");
		killTimeComboBox.getItems().add("300");
		killTimeComboBox.getItems().add("330");
		killTimeComboBox.getItems().add("360");
		killTimeComboBox.getItems().add("390");
		killTimeComboBox.getItems().add("600 (default)");
		killTimeComboBox.getItems().add("none");
		killTimeComboBox.setValue("600 (default)");
		if (input.getKillTime() == 600)
			killTimeComboBox.setValue(String.valueOf(input.getKillTime()) + " (default)");
		else
			killTimeComboBox.setValue(String.valueOf(input.getKillTime()));
		// remove most of the panes.
		if (selectModelsOnly)
		{
			mainVBox.getChildren().remove(modelsPane);
			mainVBox.getChildren().remove(parametersPane);
			mainVBox.getChildren().remove(threadPane);
			mainVBox.getChildren().remove(dataOptionsPane);

			this.progressBar.setVisible(false);
			this.progressLabel.setVisible(false);
		}
		this.selectModelsOnly = selectModelsOnly;

		// add data to the bmrFactor combobox
		bMRFactorComboBox.getItems().addAll(initBMRFactors());
		bMRFactorComboBox.getSelectionModel().select(input.getBMRFactor());

		ActionEvent event = new ActionEvent();
		handle_HillCheckBox(event);
		handle_PowerCheckBox(event);
	}

	private ModelInputParameters assignParameters()
	{
		ModelInputParameters inputParameters = new ModelInputParameters();
		if (!selectModelsOnly)
		{
			inputParameters.setIterations(Integer.valueOf(maximumIterationsTextField.getText()));
			inputParameters.setConfidence(Double.valueOf(confidenceLevelComboBox.getEditor().getText()));
			inputParameters.setBmrLevel(Double.valueOf(
					((BMRFactor) bMRFactorComboBox.getSelectionModel().getSelectedItem()).getValue()));
			inputParameters.setNumThreads(Integer.valueOf(numberOfThreadsComboBox.getEditor().getText()));
			// Multiply by 1000 to convert seconds to milliseconds
			if (killTimeComboBox.getEditor().getText().equals("none"))
				inputParameters.setKillTime(-1);
			else
				inputParameters.setKillTime(Integer.valueOf(
						killTimeComboBox.getEditor().getText().replaceAll("\\(default\\)", "").trim())
						* 1000);
			inputParameters.setBmdlCalculation(1);
			inputParameters.setBmdCalculation(1);
			inputParameters.setConstantVariance((constantVarianceCheckBox.isSelected()) ? 1 : 0);
			// for simulation only?
			inputParameters.setRestirctPower(restrictPowerComboBox.getSelectionModel().getSelectedIndex());
			// inputParameters.setBMRLevel(1);
			// inputParameters.setObservations(
			// processableData.getProcessableDoseResponseExperiment().getTreatments().size());

			if (inputParameters.getConstantVariance() == 0)
			{
				inputParameters.setRho(inputParameters.getNegative());
			}
		}

		return inputParameters;
	}

	private ModelSelectionParameters assignModelSelectionParameters()
	{
		ModelSelectionParameters modelSelectionParameters = new ModelSelectionParameters();

		modelSelectionParameters.setBestPolyModelTest(
				(BestPolyModelTestEnum) this.bestPolyTestComboBox.getSelectionModel().getSelectedItem());

		// set up the pValue
		modelSelectionParameters.setpValue(Double.valueOf(pValueCutoffComboBox.getEditor().getText()));

		// set up Flag HIll
		modelSelectionParameters.setFlagHillModel(flagHillkParamCheckBox.isSelected());

		modelSelectionParameters.setFlagHillModelDose(
				(FlagHillModelDoseEnum) flagHillkParamComboBox.getSelectionModel().getSelectedItem());

		// best model selection with flagged hill model

		modelSelectionParameters.setBestModelSelectionWithFlaggedHill(
				(BestModelSelectionWithFlaggedHillModelEnum) bestModelSeletionWithFlaggedHillComboBox
						.getSelectionModel().getSelectedItem());

		if (!modifyFlaggedHillBMDTextField.isDisabled())
		{
			modelSelectionParameters.setModFlaggedHillBMDFractionMinBMD(
					Double.valueOf(modifyFlaggedHillBMDTextField.getText()));
		}
		else
		{
			modelSelectionParameters.setModFlaggedHillBMDFractionMinBMD(0.5);
		}

		modelSelectionParameters.setBestModelSelectionBMDLandBMDU(
				(BestModelSelectionBMDLandBMDU) bmdlBmduComboBox.getSelectionModel().getSelectedItem());

		return modelSelectionParameters;

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

	@Override
	public void close()
	{
		if (presenter != null)
		{
			presenter.close();
		}

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
}
