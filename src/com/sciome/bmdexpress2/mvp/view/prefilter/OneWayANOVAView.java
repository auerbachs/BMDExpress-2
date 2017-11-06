package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OneWayANOVAPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.service.PrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

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
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> processableData,
			List<IStatModelProcessable> processableDatas)
	{

		this.processableData = processableData;
		this.processableDatas = processableDatas;

		adjustedPValueCutoffComboBox.getItems().add("0.05");
		adjustedPValueCutoffComboBox.getItems().add("0.01");
		adjustedPValueCutoffComboBox.getItems().add("0.10");
		adjustedPValueCutoffComboBox.getItems().add("None");
		adjustedPValueCutoffComboBox.getSelectionModel().select(0);

		for (IStatModelProcessable experiment : processableDatas)
		{
			expressionDataComboBox.getItems().add(experiment);

		}

		expressionDataComboBox.getSelectionModel().select(processableData.get(0));

		if (processableData.size() > 1)
		{
			expressionDataComboBox.setDisable(true);
		}
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
					foldChangeValueTextField.getText());
		}
		else
		{
			presenter.performOneWayANOVA(
					(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem(),
					pCutOff, benAndHochCheckBox.isSelected(), filterControlGenesCheckBox.isSelected(),
					useFoldChangeCheckBox.isSelected(), foldChangeValueTextField.getText());
		}

		closeWindow();
	}

	public void handle_cancelButtonPressed(ActionEvent event)
	{
		this.closeWindow();

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
