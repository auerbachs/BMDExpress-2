package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAInput;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OneWayANOVAPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.service.PrefilterService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class OneWayANOVAView extends BMDExpressViewBase implements IOneWayANOVAView, Initializable
{

	@FXML
	private ComboBox					expressionDataComboBox;
	@FXML
	private ComboBox					adjustedPValueCutoffComboBox;
	@FXML
	private CheckBox					benAndHochCheckBox;
	@FXML
	private CheckBox					filterControlGenesCheckBox;
	@FXML
	private CheckBox					useFoldChangeCheckBox;
	@FXML
	private TextField					foldChangeValueTextField;
	@FXML
	private TextField					pValueLoelTextField;
	@FXML
	private TextField					foldChangeLoelTextField;
	@FXML
	private RadioButton					dunnettsRadioButton;
	@FXML
	private RadioButton					tRadioButton;
	

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

	private OneWayANOVAInput			input;
	
	OneWayANOVAPresenter				presenter;

	public OneWayANOVAView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public OneWayANOVAView(BMDExpressEventBus eventBus)
	{
		super();
		PrefilterService service = new PrefilterService();
		presenter = new OneWayANOVAPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getOneWayInput();
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
		
		adjustedPValueCutoffComboBox.getItems().add("0.05");
		adjustedPValueCutoffComboBox.getItems().add("0.01");
		adjustedPValueCutoffComboBox.getItems().add("0.10");
		adjustedPValueCutoffComboBox.getItems().add("None");
		adjustedPValueCutoffComboBox.setValue(input.getpValueCutOff());

		benAndHochCheckBox.setSelected(input.isUseBenAndHoch());
		filterControlGenesCheckBox.setSelected(input.isFilterControlGenes());
		useFoldChangeCheckBox.setSelected(input.isUseFoldChange());
		tRadioButton.setSelected(input.istTest());
		foldChangeValueTextField.setText("" + input.getFoldChangeValue());
		pValueLoelTextField.setText("" + input.getLoelPValue());
		foldChangeLoelTextField.setText("" + input.getLoelFoldChangeValue());
	}

	public void handle_startButtonPressed(ActionEvent event)
	{

		float pCutOff = 999999.0f; // initialize with very large value.
		String pCutOffSelectedItem = adjustedPValueCutoffComboBox.getEditor().getText();

		if (!pCutOffSelectedItem.equals("None"))
		{
			pCutOff = Float.valueOf(pCutOffSelectedItem);
		}

		if (processableData.size() > 1)
		{
			presenter.performOneWayANOVA(processableData, pCutOff, benAndHochCheckBox.isSelected(),
					filterControlGenesCheckBox.isSelected(), useFoldChangeCheckBox.isSelected(),
					foldChangeValueTextField.getText(), pValueLoelTextField.getText(),
					foldChangeLoelTextField.getText(), tRadioButton.isSelected());
		}
		else
		{
			presenter.performOneWayANOVA(
					(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem(),
					pCutOff, benAndHochCheckBox.isSelected(), filterControlGenesCheckBox.isSelected(),
					useFoldChangeCheckBox.isSelected(), foldChangeValueTextField.getText(),
					pValueLoelTextField.getText(), foldChangeLoelTextField.getText(), tRadioButton.isSelected());
		}

		closeWindow();
	}

	public void handle_cancelButtonPressed(ActionEvent event)
	{
		this.closeWindow();

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

		pValueLoelTextField.setText("" + input.getLoelPValue());
		foldChangeLoelTextField.setText("" + input.getLoelFoldChangeValue());
		BMDExpressProperties.getInstance().saveOneWayANOVAInput(input);
		
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
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
