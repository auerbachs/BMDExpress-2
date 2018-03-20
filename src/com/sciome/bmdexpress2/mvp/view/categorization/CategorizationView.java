package com.sciome.bmdexpress2.mvp.view.categorization;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.sciome.bmdexpress2.mvp.model.category.CategoryInput;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.categorization.CategorizationPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.categorization.ICategorizationView;
import com.sciome.bmdexpress2.service.CategoryAnalysisService;
import com.sciome.bmdexpress2.serviceInterface.ICategoryAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFilesTool;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CategorizationView extends BMDExpressViewBase implements ICategorizationView, Initializable
{

	CategorizationPresenter					presenter;

	private CategoryAnalysisEnum			catAnalysisEnum;
	private DefinedCategoryFileParameters	probeFileParameters;
	private DefinedCategoryFileParameters	categoryFileParameters;

	// FXML injection

	// checkboxes
	@FXML
	private CheckBox						BMDUBMDCheckBox;
	@FXML
	private CheckBox						BMDUBMDLCheckBox;

	@FXML
	private CheckBox						bmdFilter4CheckBox;
	@FXML
	private CheckBox						bmdFilter3CheckBox;
	@FXML
	private CheckBox						bmdFilter2CheckBox;
	@FXML
	private CheckBox						bmdFilter1CheckBox;
	@FXML
	private CheckBox						conflictingProbeSetsCheckBox;
	@FXML
	private CheckBox						removePromiscuousProbesCheckBox;

	@FXML
	private CheckBox						deduplicateGeneSetsCheckBox;

	// textfields
	@FXML
	private TextField						correlationCutoffProbeSetsValue;
	@FXML
	private TextField						bmdFilter2Value;
	@FXML
	private TextField						bmdFilter3Value;
	@FXML
	private TextField						bmdFilter4Value;

	@FXML
	private TextField						BMDUBMDTextbox;
	@FXML
	private TextField						BMDUBMDLTextbox;

	// ComboBoxes
	@FXML
	private ComboBox						categoryComboBox;
	@FXML
	private Label							selectionLabel;

	@FXML
	private HBox							probeFileHBox;
	@FXML
	private HBox							categoryFileHBox;
	@FXML
	private HBox							selectionHBox;

	@FXML
	private VBox							mainVBox;

	@FXML
	private Label							probeFileLabel;
	@FXML
	private TextField						probeFileTextField;
	@FXML
	private Button							browseProbeFile;

	@FXML
	private Label							categoryFileLabel;
	@FXML
	private TextField						categoryFileTextField;
	@FXML
	private Button							browseCategoryFile;

	// labels
	@FXML
	private Label							bMDAnalysisName;

	@FXML
	private ProgressBar						progressBar;
	@FXML
	private Label							progressLabel;
	@FXML
	private HBox							progressHBox;

	@FXML
	private Button							startButton;
	@FXML
	private Button							closeButton;
	@FXML
	private Button							saveSettingsButton;

	// ComboBoxes
	@FXML
	private CheckBox						bmdFilterMaxFoldChangeCheckBox;
	@FXML
	private TextField						bmdFilterMaxFoldChangeValue;
	@FXML
	private CheckBox						bmdFilterMaxPValueCheckBox;
	@FXML
	private TextField						bmdFilterMaxPValueChangeValue;
	@FXML
	private CheckBox						bmdFilterMaxAdjustedPValueCheckBox;
	@FXML
	private TextField						bmdFilterMaxAdjustedPValueChangeValue;

	private CategoryInput					input;
	
	public CategorizationView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	/*
	 * Event bus is passed as an argument so the unit tests can pass their own custom eventbus
	 */
	public CategorizationView(BMDExpressEventBus eventBus)
	{
		super();
		ICategoryAnalysisService service = new CategoryAnalysisService();
		presenter = new CategorizationPresenter(this, service, eventBus);
		input = BMDExpressProperties.getInstance().getCategoryInput();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
	}

	/*
	 * user clicked close button
	 */
	@Override
	public void handle_close(ActionEvent event)
	{
		closeWindow();
	}

	/*
	 * use clicked start button
	 */
	@Override
	public void handle_start(ActionEvent event)
	{
		startButton.setDisable(true);
		closeButton.setDisable(true);
		CategoryAnalysisParameters params = this.gatherParameters();
		if (params != null)
			presenter.startAnalyses(params);

	}
	
	@Override
	public void handle_saveSettingsButtonPressed(ActionEvent event) {
		input.setRemovePromiscuousProbes(this.removePromiscuousProbesCheckBox.isSelected());
		input.setRemoveBMDGreaterThanHighestDose(this.bmdFilter1CheckBox.isSelected());
		input.setRemoveBMDLessThanPValue(this.bmdFilter2CheckBox.isSelected());
		input.setRemoveGenesWithBMD_BMDL(this.bmdFilter3CheckBox.isSelected());
		input.setRemoveGenesWithBMDU_BMD(this.BMDUBMDCheckBox.isSelected());
		input.setRemoveGenesWithBMDU_BMDL(this.BMDUBMDLCheckBox.isSelected());
		input.setRemoveGenesWithBMDValuesGreaterThanNFold(this.bmdFilter4CheckBox.isSelected());
		input.setRemoveGenesWithMaxFoldChangeLessThan(this.bmdFilterMaxFoldChangeCheckBox.isSelected());
		input.setRemoveGenesWithPrefilterPValue(this.bmdFilterMaxPValueCheckBox.isSelected());
		input.setRemoveGenesWithPrefilterAdjustedPValue(this.bmdFilterMaxAdjustedPValueCheckBox.isSelected());
		input.setEliminateGeneSetRedundancy(this.deduplicateGeneSetsCheckBox.isSelected());
		input.setIdentifyConflictingProbeSets(this.conflictingProbeSetsCheckBox.isSelected());
		
		input.setRemoveBMDLessThanPValueNumber(Double.parseDouble(this.bmdFilter2Value.getText()));
		input.setRemoveGenesWithBMD_BMDLNumber(Double.parseDouble(this.bmdFilter3Value.getText()));
		input.setRemoveGenesWithBMDU_BMDNumber(Double.parseDouble(this.BMDUBMDTextbox.getText()));
		input.setRemoveGenesWithBMDU_BMDLNumber(Double.parseDouble(this.BMDUBMDLTextbox.getText()));
		input.setRemoveGenesWithBMDValuesGreaterThanNFoldNumber(Double.parseDouble(this.bmdFilter4Value.getText()));
		input.setRemoveGenesWithMaxFoldChangeLessThanNumber(Double.parseDouble(this.bmdFilterMaxFoldChangeValue.getText()));
		input.setRemoveGenesWithPrefilterPValueNumber(Double.parseDouble(this.bmdFilterMaxPValueChangeValue.getText()));
		input.setRemoveGenesWithPrefilterAdjustedPValueNumber(Double.parseDouble(this.bmdFilterMaxAdjustedPValueChangeValue.getText()));
		input.setCorrelationCutoffForConflictingProbeSets(Double.parseDouble(this.correlationCutoffProbeSetsValue.getText()));
		
		BMDExpressProperties.getInstance().saveCategoryInput(input);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved Settings");
		alert.setHeaderText(null);
		alert.setContentText("Your settings have been saved");

		alert.showAndWait();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initData(List<BMDResult> bmdResults, CategoryAnalysisEnum catAnalysisEnum)
	{
		this.catAnalysisEnum = catAnalysisEnum;

		// Render the view based on the Category Analysis style.
		if (catAnalysisEnum == CategoryAnalysisEnum.PATHWAY)
		{
			selectionLabel.setText("Select Pathway Database:");
			categoryComboBox.getItems().addAll("REACTOME");
			categoryComboBox.getSelectionModel().select(0);

			mainVBox.getChildren().remove(probeFileHBox);
			mainVBox.getChildren().remove(categoryFileHBox);
		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.GO)
		{
			selectionLabel.setText("GO Categories");
			categoryComboBox.getItems().addAll(BMDExpressConstants.getInstance().GO_CATEGORIES);
			categoryComboBox.getSelectionModel().select(0);
			mainVBox.getChildren().remove(probeFileHBox);
			mainVBox.getChildren().remove(categoryFileHBox);
		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.DEFINED)
		{
			mainVBox.getChildren().remove(selectionHBox);
		}
		
		// Initalize fields using saved settings
		this.removePromiscuousProbesCheckBox.setSelected(input.isRemovePromiscuousProbes());
		this.bmdFilter1CheckBox.setSelected(input.isRemoveBMDGreaterThanHighestDose());
		this.bmdFilter2CheckBox.setSelected(input.isRemoveBMDLessThanPValue());
		this.bmdFilter3CheckBox.setSelected(input.isRemoveGenesWithBMD_BMDL());
		this.bmdFilter4CheckBox.setSelected(input.isRemoveGenesWithBMDValuesGreaterThanNFold());
		this.bmdFilterMaxAdjustedPValueCheckBox.setSelected(input.isRemoveGenesWithPrefilterAdjustedPValue());
		this.bmdFilterMaxFoldChangeCheckBox.setSelected(input.isRemoveGenesWithMaxFoldChangeLessThan());
		this.bmdFilterMaxPValueCheckBox.setSelected(input.isRemoveGenesWithPrefilterPValue());
		this.BMDUBMDCheckBox.setSelected(input.isRemoveGenesWithBMDU_BMD());
		this.BMDUBMDLCheckBox.setSelected(input.isRemoveGenesWithBMDU_BMDL());
		this.conflictingProbeSetsCheckBox.setSelected(input.isIdentifyConflictingProbeSets());
		this.deduplicateGeneSetsCheckBox.setSelected(input.isEliminateGeneSetRedundancy());
		
		this.bmdFilter2Value.setText("" + input.getRemoveBMDLessThanPValueNumber());
		this.bmdFilter3Value.setText("" + input.getRemoveGenesWithBMD_BMDLNumber());
		this.BMDUBMDTextbox.setText("" + input.getRemoveGenesWithBMDU_BMDNumber());
		this.BMDUBMDLTextbox.setText("" + input.getRemoveGenesWithBMDU_BMDLNumber());
		this.bmdFilter4Value.setText("" + input.getRemoveGenesWithBMDValuesGreaterThanNFoldNumber());
		this.bmdFilterMaxFoldChangeValue.setText("" + input.getRemoveGenesWithMaxFoldChangeLessThanNumber());
		this.bmdFilterMaxPValueChangeValue.setText("" + input.getRemoveGenesWithPrefilterPValueNumber());
		this.bmdFilterMaxAdjustedPValueChangeValue.setText("" + input.getRemoveGenesWithPrefilterAdjustedPValueNumber());
		this.correlationCutoffProbeSetsValue.setText("" + input.getCorrelationCutoffForConflictingProbeSets());
		presenter.initData(bmdResults, catAnalysisEnum);

	}

	@Override
	public void handle_browseProbe()
	{

		DefinedCategoryFilesTool definedFilesTool = new DefinedCategoryFilesTool();
		DefinedCategoryFileParameters fileParameters = definedFilesTool
				.probeGetter(this.startButton.getScene().getWindow());
		if (fileParameters != null)
			this.probeFileTextField.setText(fileParameters.getFileName());

		this.probeFileParameters = fileParameters;
	}

	@Override
	public void handle_browseCategory()
	{
		DefinedCategoryFilesTool definedFilesTool = new DefinedCategoryFilesTool();
		DefinedCategoryFileParameters fileParameters = definedFilesTool
				.categoryGetter(this.startButton.getScene().getWindow());
		if (fileParameters != null)
			this.categoryFileTextField.setText(fileParameters.getFileName());
		this.categoryFileParameters = fileParameters;
	}

	/*
	 * set up the parameters object to send to the presenter.
	 */
	private CategoryAnalysisParameters gatherParameters()
	{
		CategoryAnalysisParameters params = new CategoryAnalysisParameters();

		// analysis style specific parameters
		if (catAnalysisEnum == CategoryAnalysisEnum.PATHWAY)
		{
			params.setPathwayDB((String) this.categoryComboBox.getSelectionModel().getSelectedItem());
		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.GO)
		{
			params.setGoCat((String) this.categoryComboBox.getSelectionModel().getSelectedItem());
			params.setGoTermIdx(this.categoryComboBox.getSelectionModel().getSelectedIndex());
		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.DEFINED)
		{
			params.setCategoryFileParameters(this.categoryFileParameters);
			params.setProbeFileParameters(this.probeFileParameters);
		}

		// common parameters
		params.setIdentifyConflictingProbeSets(this.conflictingProbeSetsCheckBox.isSelected());
		params.setCorrelationCutoffConflictingProbeSets(
				Double.valueOf(this.correlationCutoffProbeSetsValue.getText()));

		params.setRemoveBMDGreaterHighDose(this.bmdFilter1CheckBox.isSelected());
		params.setRemoveBMDPValueLessCuttoff(this.bmdFilter2CheckBox.isSelected());
		params.setRemovePromiscuousProbes(removePromiscuousProbesCheckBox.isSelected());
		params.setpValueCutoff(Double.valueOf(this.bmdFilter2Value.getText()));

		params.setRemoveBMDBMDLRatio(this.bmdFilter3CheckBox.isSelected());
		params.setBmdBmdlRatio(Double.valueOf(this.bmdFilter3Value.getText()));

		params.setRemoveNFoldBelowLowestDose(this.bmdFilter4CheckBox.isSelected());
		params.setnFoldbelowLowestDoseValue(Double.valueOf(this.bmdFilter4Value.getText()));

		params.setRemoveBMDUBMDLRatio(this.BMDUBMDLCheckBox.isSelected());
		params.setBmduBmdlRatio(Double.valueOf(this.BMDUBMDLTextbox.getText()));

		params.setRemoveBMDUBMDRatio(this.BMDUBMDCheckBox.isSelected());
		params.setBmduBmdRatio(Double.valueOf(this.BMDUBMDTextbox.getText()));

		params.setUserFoldChangeFilter(this.bmdFilterMaxFoldChangeCheckBox.isSelected());
		params.setMaxFoldChange(Double.valueOf(this.bmdFilterMaxFoldChangeValue.getText()));

		params.setUserPValueFilter(this.bmdFilterMaxPValueCheckBox.isSelected());
		params.setPValue(Double.valueOf(this.bmdFilterMaxPValueChangeValue.getText()));

		params.setUserAdjustedPValueFilter(this.bmdFilterMaxAdjustedPValueCheckBox.isSelected());
		params.setAdjustedPValue(Double.valueOf(this.bmdFilterMaxAdjustedPValueChangeValue.getText()));

		params.setDeduplicateGeneSets(deduplicateGeneSetsCheckBox.isSelected());

		return params;

	}

	@Override
	public void finishedCategorization()
	{
		progressLabel.setText("Finished Categorization");
		progressBar.setProgress(0.0);

	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) this.bmdFilter2CheckBox.getScene().getWindow();
		this.close();
		stage.close();

	}

	@Override
	public void startedCategorization()
	{
		progressHBox.setVisible(true);
		progressLabel.setText("Beginning Categorization");
		progressBar.setProgress(0.0);
	}

	@Override
	public void updateProgressBar(String label, double value)
	{
		progressLabel.setText(label);
		progressBar.setProgress(value);

	}

	@Override
	public void enableButtons()
	{
		startButton.setDisable(false);
		closeButton.setDisable(false);

	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.close();
	}

}
