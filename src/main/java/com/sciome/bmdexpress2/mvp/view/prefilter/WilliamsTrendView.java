package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendInput;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.WilliamsTrendPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPrefilterView;
import com.sciome.bmdexpress2.service.PrefilterService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class WilliamsTrendView extends BMDExpressViewBase implements IPrefilterView, Initializable
{
	@FXML
	private ComboBox					expressionDataComboBox;
	@FXML
	private ComboBox					adjustedPValueCutoffComboBox;
	@FXML
	private ComboBox					numberOfPermutationsComboBox;
	@FXML
	private CheckBox					benAndHochCheckBox;
	@FXML
	private CheckBox					filterControlGenesCheckBox;
	@FXML
	private CheckBox					useFoldChangeCheckBox;
	@FXML
	private TextField					foldChangeValueTextField;
	@FXML
	private ProgressBar					williamsTrendProgress;
	@FXML
	private Label						williamsProgressMessage;
	@FXML
	private Label						datasetsCompletedLabel;
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
	@FXML
	private TextField					numberOfThreadsTextField;
	@FXML
	private RadioButton					dunnettsRadioButton;
	@FXML
	private RadioButton					tRadioButton;

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

	private WilliamsTrendInput			input;
	
	WilliamsTrendPresenter				presenter;

	public WilliamsTrendView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public WilliamsTrendView(BMDExpressEventBus eventBus)
	{
		super();
		PrefilterService service = new PrefilterService();
		presenter = new WilliamsTrendPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getWilliamsInput();
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
		
		ToggleGroup radioGroup = new ToggleGroup();
		tRadioButton.setToggleGroup(radioGroup);
		dunnettsRadioButton.setToggleGroup(radioGroup);
		
		if(input.istTest())
			tRadioButton.setSelected(true);
		else
			dunnettsRadioButton.setSelected(true);
		
		adjustedPValueCutoffComboBox.getItems().add("0.01");
		adjustedPValueCutoffComboBox.getItems().add("0.05");
		adjustedPValueCutoffComboBox.getItems().add("0.10");
		adjustedPValueCutoffComboBox.getItems().add("None");
		adjustedPValueCutoffComboBox.setValue(input.getpValueCutOff());

		numberOfPermutationsComboBox.getItems().add("50");
		numberOfPermutationsComboBox.getItems().add("100");
		numberOfPermutationsComboBox.getItems().add("250");
		numberOfPermutationsComboBox.getItems().add("500");
		numberOfPermutationsComboBox.setValue((int)input.getNumPermutations());
		
		benAndHochCheckBox.setSelected(input.isUseBenAndHoch());
		filterControlGenesCheckBox.setSelected(input.isFilterControlGenes());
		useFoldChangeCheckBox.setSelected(input.isUseFoldChange());
		tRadioButton.setSelected(input.istTest());
		foldChangeValueTextField.setText("" + input.getFoldChangeValue());
		pValueLoelTextField.setText("" + input.getLoelPValue());
		foldChangeLoelTextField.setText("" + input.getLoelFoldChangeValue());
		numberOfThreadsTextField.setText("" + input.getNumThreads());
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
	
			if (processableData.size() > 1)
			{
				presenter.performWilliamsTrend(processableData, pCutOff, benAndHochCheckBox.isSelected(),
						filterControlGenesCheckBox.isSelected(), useFoldChangeCheckBox.isSelected(),
						foldChangeValueTextField.getText(), numberOfPermutationsComboBox.getEditor().getText(),
						pValueLoelTextField.getText(), foldChangeLoelTextField.getText(),
						numberOfThreadsTextField.getText(), tRadioButton.isSelected());
			}
			else
			{
				presenter.performWilliamsTrend(
						(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem(),
						pCutOff, benAndHochCheckBox.isSelected(), filterControlGenesCheckBox.isSelected(),
						useFoldChangeCheckBox.isSelected(), foldChangeValueTextField.getText(),
						numberOfPermutationsComboBox.getEditor().getText(), pValueLoelTextField.getText(),
						foldChangeLoelTextField.getText(), numberOfThreadsTextField.getText(),
						tRadioButton.isSelected());
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
		input.settTest(this.tRadioButton.isSelected());
		input.setpValueCutOff(Double.parseDouble(this.adjustedPValueCutoffComboBox.getEditor().getText()));
		input.setFoldChangeValue(Double.parseDouble(this.foldChangeValueTextField.getText()));
		input.setLoelFoldChangeValue(Double.parseDouble(this.foldChangeLoelTextField.getText()));
		input.setLoelPValue(Double.parseDouble(this.pValueLoelTextField.getText()));
		input.setNumThreads(Integer.parseInt(this.numberOfThreadsTextField.getText()));
		
		input.setNumPermutations(Double.parseDouble(this.numberOfPermutationsComboBox.getEditor().getText()));
		
		BMDExpressProperties.getInstance().saveWilliamsInput(input);
		
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
		williamsTrendProgress.setProgress(progress);
	}
	
	@Override
	public void updateMessage(String message) {
		williamsProgressMessage.setText(message);
	}
	
	@Override
	public void updateDatasetLabel(String message) {
		datasetsCompletedLabel.setText(message);
	}
	
	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
