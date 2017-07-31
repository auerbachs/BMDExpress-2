package com.sciome.bmdexpress2.mvp.view.prefilter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.presenter.prefilter.PathwayFilterPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPathwayFilterView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.PathWayFilterPValueEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PathwayFilterView extends BMDExpressViewBase implements IPathwayFilterView, Initializable
{

	@FXML
	private ComboBox					expressionDataComboBox;
	@FXML
	private ComboBox					pValueCutoffComboBox;
	@FXML
	private ComboBox					threadsComboBox;

	@FXML
	private TextField					alphaTextField;
	@FXML
	private TextField					iterationsTextField;

	@FXML
	private ProgressBar					progressBar;

	@FXML
	private Label						progressLabel;
	@FXML
	private CheckBox					filterControlGenesCheckBox;
	@FXML
	private CheckBox					ignoreSingletonDoseReponse;
	@FXML
	private TextField					minGenePerPathwayTextField;

	@FXML
	private Button						startButton;
	@FXML
	private Button						cancelButton;

	@FXML
	private RadioButton					useFWER;
	@FXML
	private RadioButton					useUnadjusted;
	@FXML
	private RadioButton					useFDR;

	private List<IStatModelProcessable>	processableData		= null;
	private List<IStatModelProcessable>	processableDatas	= null;

	PathwayFilterPresenter				presenter;

	public PathwayFilterView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public PathwayFilterView(BMDExpressEventBus eventBus)
	{
		super();
		presenter = new PathwayFilterPresenter(this, eventBus);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<IStatModelProcessable> selectedItems,
			List<IStatModelProcessable> processableDatas)
	{

		this.processableData = selectedItems;
		this.processableDatas = processableDatas;
		this.progressBar.setVisible(false);

		pValueCutoffComboBox.getItems().add("0.05");
		pValueCutoffComboBox.getItems().add("0.01");
		pValueCutoffComboBox.getItems().add("0.10");
		pValueCutoffComboBox.getItems().add("None");
		pValueCutoffComboBox.getSelectionModel().select(0);

		for (int i = 1; i <= 40; i++)
		{
			threadsComboBox.getItems().add(String.valueOf(i));

		}
		threadsComboBox.getSelectionModel().select(0);

		for (IStatModelProcessable experiment : processableDatas)
		{
			expressionDataComboBox.getItems().add(experiment);

		}

		expressionDataComboBox.getSelectionModel().select(selectedItems.get(0));

		if (selectedItems.size() > 1)
		{
			expressionDataComboBox.setDisable(true);
		}
	}

	public void handle_startButtonPressed(ActionEvent event)
	{

		Double pCutOff = 999999.0; // initialize with very large value.
		String pCutOffSelectedItem = pValueCutoffComboBox.getEditor().getText();

		if (!pCutOffSelectedItem.equals("None"))
		{
			pCutOff = Double.valueOf(pCutOffSelectedItem);
		}

		this.startButton.setDisable(true);
		this.cancelButton.setDisable(true);
		this.progressBar.setVisible(true);

		PathWayFilterPValueEnum pathwayPValue = PathWayFilterPValueEnum.UNADJUSTED;

		if (this.useFDR.isSelected())
		{
			pathwayPValue = PathWayFilterPValueEnum.FDR;
		}
		else if (this.useFWER.isSelected())
		{
			pathwayPValue = PathWayFilterPValueEnum.FWER;
		}

		if (processableData.size() > 1)
		{
			presenter.performPathwayFilter(processableData, pCutOff,
					Integer.valueOf(iterationsTextField.getText()), Double.valueOf(alphaTextField.getText()),
					Integer.valueOf(threadsComboBox.getEditor().getText()),
					filterControlGenesCheckBox.isSelected(), ignoreSingletonDoseReponse.isSelected(),
					Integer.valueOf(minGenePerPathwayTextField.getText()), pathwayPValue);
		}
		else
		{
			// gather the parameters and let the presenter do the rest.
			List<IStatModelProcessable> singletonList = new ArrayList<>();
			singletonList.add(
					(IStatModelProcessable) expressionDataComboBox.getSelectionModel().getSelectedItem());
			presenter.performPathwayFilter(singletonList, pCutOff,
					Integer.valueOf(iterationsTextField.getText()), Double.valueOf(alphaTextField.getText()),
					Integer.valueOf(threadsComboBox.getEditor().getText()),
					filterControlGenesCheckBox.isSelected(), ignoreSingletonDoseReponse.isSelected(),
					Integer.valueOf(minGenePerPathwayTextField.getText()), pathwayPValue);
		}
	}

	public void handle_cancelButtonPressed(ActionEvent event)
	{
		this.closeWindow();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{

	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) pValueCutoffComboBox.getScene().getWindow();
		this.close();
		stage.close();
	}

	@Override
	public void updateProgressBar(String label, double value)
	{
		// currently on windows, the stdout of RScript is not working
		// so will set pregress bar to indeterminite until this issue is resolved.
		if (BMDExpressProperties.getInstance().isWindows())
		{
			this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		}
		else
		{
			this.progressBar.setProgress(value);
			this.progressLabel.setText(label);
		}

	}

	@Override
	public File checkRPath(String rscriptPath)
	{
		return getRsciptFile("Select Rscript executable");

	}

	/*
	 * generate a FileChooser for exporting data.
	 */
	private File getRsciptFile(String title)
	{
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Rscript chooser");
		dialog.setHeaderText("Please select the Rscript executable in order to perfrom Pathway Filter");
		dialog.initOwner(this.alphaTextField.getScene().getWindow());
		dialog.initModality(Modality.WINDOW_MODAL);
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.showAndWait();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		File selectedFile = fileChooser.showOpenDialog(progressBar.getScene().getWindow());

		return selectedFile;
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();

	}

}
