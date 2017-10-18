package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.OriogenPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOriogenView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OriogenView extends BMDExpressViewBase implements IOriogenView, Initializable{

	@FXML
	private ComboBox					expressionDataComboBox;
	@FXML
	private ComboBox					analysisTypeComboBox;
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
	private Button						startButton;
	@FXML
	private Button						stopButton;

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

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
		presenter = new OriogenPresenter(this, eventBus);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> processableData,
			List<IStatModelProcessable> processableDatas)
	{

		this.processableData = processableData;
		this.processableDatas = processableDatas;

		analysisTypeComboBox.getItems().add("Time Course/Dose Response Pattern Analysis");
		analysisTypeComboBox.getItems().add("Multiple Pairwise Comparisons");
		analysisTypeComboBox.getSelectionModel().select(0);
		
		adjustedPValueCutoffComboBox.getItems().add("0.05");
		adjustedPValueCutoffComboBox.getItems().add("0.01");
		adjustedPValueCutoffComboBox.getItems().add("0.10");
		adjustedPValueCutoffComboBox.getItems().add("None");
		adjustedPValueCutoffComboBox.getSelectionModel().select(0);

		initialBootstrapComboBox.getItems().add("250");
		initialBootstrapComboBox.getItems().add("500");
		initialBootstrapComboBox.getItems().add("1000");
		initialBootstrapComboBox.getSelectionModel().select(1);
		
		maxBootstrapComboBox.getItems().add("500");
		maxBootstrapComboBox.getItems().add("1000");
		maxBootstrapComboBox.getItems().add("2500");
		maxBootstrapComboBox.getSelectionModel().select(1);
		
		s0AdjustmentComboBox.getItems().add("1.0");
		s0AdjustmentComboBox.getItems().add("5.0");
		s0AdjustmentComboBox.getItems().add("10.0");
		s0AdjustmentComboBox.getSelectionModel().select(1);
		
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
		if(!presenter.hasStartedTask()) {
			float pCutOff = 999999.0f; // initialize with very large value.
			String pCutOffSelectedItem = adjustedPValueCutoffComboBox.getEditor().getText();
	
			if (!pCutOffSelectedItem.equals("None"))
			{
				pCutOff = Float.valueOf(pCutOffSelectedItem);
			}
	
			if (processableData.size() > 1)
			{
				presenter.performOriogen(processableData, pCutOff, benAndHochCheckBox.isSelected(),
						filterControlGenesCheckBox.isSelected(), useFoldChangeCheckBox.isSelected(),
						foldChangeValueTextField.getText(), initialBootstrapComboBox.getEditor().getText());
			}
			else
			{
				presenter.performOriogen(
						(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem(),
						pCutOff, benAndHochCheckBox.isSelected(), filterControlGenesCheckBox.isSelected(),
						useFoldChangeCheckBox.isSelected(), foldChangeValueTextField.getText(),
						initialBootstrapComboBox.getEditor().getText());
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
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}
}
