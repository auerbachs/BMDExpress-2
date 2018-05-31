package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenInput;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OriogenPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOriogenView;
import com.sciome.bmdexpress2.service.PrefilterService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class OriogenView extends BMDExpressViewBase implements IOriogenView, Initializable{

	@FXML
	private ComboBox					expressionDataComboBox;
	@FXML
	private ComboBox					adjustedPValueCutoffComboBox;
	@FXML
	private ComboBox					initialBootstrapComboBox;
	@FXML
	private ComboBox					maxBootstrapComboBox;
	@FXML
	private ComboBox					s0AdjustmentComboBox;
	@FXML
	private CheckBox					benAndHochCheckBox;
	@FXML
	private CheckBox					filterControlGenesCheckBox;
	@FXML
	private CheckBox					useFoldChangeCheckBox;
	@FXML
	private TextField					foldChangeValueTextField;
	@FXML
	private ProgressBar					oriogenProgressBar;
	@FXML
	private Label						oriogenProgressMessage;
	@FXML
	private Button						startButton;
	@FXML
	private Button						saveSettingsButton;
	@FXML
	private Button						stopButton;
	@FXML
	private TextField					pValueLoelTextField;
	@FXML
	private TextField					foldChangeLoelTextField;

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

	private	OriogenInput				input;
	
	OriogenPresenter					presenter;

	public OriogenView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public OriogenView(BMDExpressEventBus eventBus)
	{
		super();
		PrefilterService service = new PrefilterService();
		presenter = new OriogenPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getOriogenInput();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> processableData,
			List<IStatModelProcessable> processableDatas)
	{

		this.processableData = processableData;
		this.processableDatas = processableDatas;

		for (IStatModelProcessable experiment : processableDatas)
		{
			expressionDataComboBox.getItems().add(experiment);
		}

		expressionDataComboBox.getSelectionModel().select(processableData.get(0));

		if (processableData.size() > 1)
		{
			expressionDataComboBox.setDisable(true);
		}
		
		adjustedPValueCutoffComboBox.getItems().add("0.05");
		adjustedPValueCutoffComboBox.getItems().add("0.01");
		adjustedPValueCutoffComboBox.getItems().add("0.10");
		adjustedPValueCutoffComboBox.getItems().add("None");
		adjustedPValueCutoffComboBox.setValue(input.getpValueCutOff());

		initialBootstrapComboBox.getItems().add("250");
		initialBootstrapComboBox.getItems().add("500");
		initialBootstrapComboBox.getItems().add("1000");
		initialBootstrapComboBox.setValue(input.getNumInitialBootstraps());
		
		maxBootstrapComboBox.getItems().add("500");
		maxBootstrapComboBox.getItems().add("1000");
		maxBootstrapComboBox.getItems().add("2500");
		maxBootstrapComboBox.setValue(input.getNumMaximumBootstraps());
		
		s0AdjustmentComboBox.getItems().add("1.0");
		s0AdjustmentComboBox.getItems().add("5.0");
		s0AdjustmentComboBox.getItems().add("10.0");
		s0AdjustmentComboBox.setValue(input.getShrinkagePercentile());

		benAndHochCheckBox.setSelected(input.isUseBenAndHoch());
		filterControlGenesCheckBox.setSelected(input.isFilterControlGenes());
		useFoldChangeCheckBox.setSelected(input.isUseFoldChange());
		foldChangeValueTextField.setText("" + input.getFoldChangeValue());
		
	}

	public void handle_startButtonPressed(ActionEvent event)
	{
		if(!presenter.hasStartedTask()) {
			float pCutOff = 999999.0f; // initialize with very large value.
			String pCutOffSelectedItem = adjustedPValueCutoffComboBox.getEditor().getText();
			if (!pCutOffSelectedItem.equals("None"))
			{
				pCutOff = Float.valueOf(pCutOffSelectedItem);
			}
			
			int initialBootstraps = Integer.parseInt(initialBootstrapComboBox.getEditor().getText());
			int maxBootstraps = Integer.parseInt(maxBootstrapComboBox.getEditor().getText());
			float s0Adjustment = Float.parseFloat(s0AdjustmentComboBox.getEditor().getText());
			
			if (processableData.size() > 1)
			{
				presenter.performOriogen(processableData, pCutOff, benAndHochCheckBox.isSelected(),
						initialBootstraps, maxBootstraps, s0Adjustment,
						filterControlGenesCheckBox.isSelected(), useFoldChangeCheckBox.isSelected(),
						foldChangeValueTextField.getText(), pValueLoelTextField.getText(),
						foldChangeLoelTextField.getText());
			}
			else
			{
				presenter.performOriogen(
						(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem(),
						pCutOff, benAndHochCheckBox.isSelected(), initialBootstraps, maxBootstraps, s0Adjustment,
						filterControlGenesCheckBox.isSelected(), useFoldChangeCheckBox.isSelected(), 
						foldChangeValueTextField.getText(), pValueLoelTextField.getText(),
						foldChangeLoelTextField.getText());
			}
			startButton.setDisable(true);
		}
	}

	public void handle_cancelButtonPressed(ActionEvent event)
	{
		if(!presenter.hasStartedTask()) {
			this.closeWindow();
		} else {
			presenter.cancel();
			startButton.setDisable(false);
		}
	}
	
	public void handle_saveSettingsButtonPressed(ActionEvent event) {
		input.setFilterControlGenes(this.filterControlGenesCheckBox.isSelected());
		input.setUseBenAndHoch(this.benAndHochCheckBox.isSelected());
		input.setUseFoldChange(this.useFoldChangeCheckBox.isSelected());
		input.setpValueCutOff(Double.parseDouble(this.adjustedPValueCutoffComboBox.getEditor().getText()));
		input.setFoldChangeValue(Double.parseDouble(this.foldChangeValueTextField.getText()));
		
		input.setNumInitialBootstraps(Integer.parseInt(this.initialBootstrapComboBox.getEditor().getText()));
		input.setNumMaximumBootstraps(Integer.parseInt(this.maxBootstrapComboBox.getEditor().getText()));
		input.setShrinkagePercentile(Double.parseDouble(this.s0AdjustmentComboBox.getEditor().getText()));
		BMDExpressProperties.getInstance().saveOriogenInput(input);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved Settings");
		alert.setHeaderText(null);
		alert.setContentText("Your settings have been saved");

		alert.showAndWait();
	}

	public void handle_UseFoldChangeFilter()
	{
		if (this.useFoldChangeCheckBox.isSelected())
		{
			this.foldChangeValueTextField.setDisable(false);

		}
		else
		{
			this.foldChangeValueTextField.setDisable(true);

		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) benAndHochCheckBox.getScene().getWindow();
		this.close();
		stage.close();
	}
	
	@Override
	public void updateProgress(double progress) {
		oriogenProgressBar.setProgress(progress);
	}
	
	@Override
	public void updateMessage(String message) {
		oriogenProgressMessage.setText(message);
	}
	
	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}
}
