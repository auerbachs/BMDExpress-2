package com.sciome.bmdexpress2.mvp.view.categorization;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

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
import com.sciome.bmdexpress2.shared.CompoundTableLoader;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters.ConcentrationUnits;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFilesTool;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Units;
import com.sciome.commons.math.httk.model.Compound;
import com.sciome.commons.math.httk.model.Compound.Source;
import com.sciome.commons.math.httk.model.CompoundTable;
import com.sciome.commons.math.httk.model.InVitroData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CategorizationView extends BMDExpressViewBase implements ICategorizationView, Initializable
{

	CategorizationPresenter presenter;

	private CategoryAnalysisEnum catAnalysisEnum;
	private DefinedCategoryFileParameters probeFileParameters;
	private DefinedCategoryFileParameters categoryFileParameters;

	// FXML injection
	@FXML
	private Tab iviveTab;

	@FXML
	private HBox autoPopulateHBox;

	// checkboxes
	@FXML
	private CheckBox BMDUBMDCheckBox;
	@FXML
	private CheckBox BMDUBMDLCheckBox;

	@FXML
	private CheckBox bmdFilter4CheckBox;
	@FXML
	private CheckBox bmdFilter3CheckBox;
	@FXML
	private CheckBox bmdFilter2CheckBox;
	@FXML
	private CheckBox bmdFilter1CheckBox;
	@FXML
	private CheckBox conflictingProbeSetsCheckBox;
	@FXML
	private CheckBox doIVIVECheckBox;
	@FXML
	private CheckBox removePromiscuousProbesCheckBox;

	@FXML
	private CheckBox deduplicateGeneSetsCheckBox;

	// textfields
	@FXML
	private TextField correlationCutoffProbeSetsValue;
	@FXML
	private TextField bmdFilter2Value;
	@FXML
	private TextField bmdFilter3Value;
	@FXML
	private TextField bmdFilter4Value;

	@FXML
	private TextField BMDUBMDTextbox;
	@FXML
	private TextField BMDUBMDLTextbox;

	// ComboBoxes
	@FXML
	private ComboBox categoryComboBox;
	@FXML
	private Label selectionLabel;

	@FXML
	private HBox probeFileHBox;
	@FXML
	private HBox categoryFileHBox;
	@FXML
	private HBox selectionHBox;

	@FXML
	private VBox mainVBox;

	@FXML
	private Label probeFileLabel;
	@FXML
	private TextField probeFileTextField;
	@FXML
	private Button browseProbeFile;

	@FXML
	private Label categoryFileLabel;
	@FXML
	private TextField categoryFileTextField;
	@FXML
	private Button browseCategoryFile;

	// labels
	@FXML
	private Label bMDAnalysisName;

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	@FXML
	private HBox progressHBox;

	@FXML
	private Button startButton;
	@FXML
	private Button closeButton;
	@FXML
	private Button saveSettingsButton;

	// ComboBoxes
	@FXML
	private CheckBox bmdFilterMaxFoldChangeCheckBox;
	@FXML
	private TextField bmdFilterMaxFoldChangeValue;
	@FXML
	private CheckBox bmdFilterMaxPValueCheckBox;
	@FXML
	private TextField bmdFilterMaxPValueChangeValue;
	@FXML
	private CheckBox bmdFilterMaxAdjustedPValueCheckBox;
	@FXML
	private TextField bmdFilterMaxAdjustedPValueChangeValue;

	// IVIVE
	@FXML
	private Label quantile_doseSpacingLabel;
	@FXML
	private Label finalTimeLabel;

	@FXML
	private Label doseCountLabel;

	@FXML
	private TextField doseCountTextField;

	ToggleGroup inVivoGroup;
	@FXML
	private RadioButton inVitroRadioButton;
	@FXML
	private RadioButton inVivoRadioButton;
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField casrnTextField;
	@FXML
	private TextField smilesTextField;
	@FXML
	private TextField mwTextField;
	@FXML
	private TextField logPTextField;
	@FXML
	private TextField pKaDonorTextField;
	@FXML
	private TextField pKaAcceptorTextField;
	@FXML
	private TextField clintTextField;
	@FXML
	private TextField fubTextField;

	@FXML
	private TextField fGutAbs;
	@FXML
	private TextField quantile_doseSpacingTextField;
	@FXML
	private TextField finalTimeTextField;
	@FXML
	private ComboBox inputUnitsComboBox;
	@FXML
	private ComboBox outputUnitsComboBox;
	@FXML
	private ComboBox speciesComboBox;

	@FXML
	private Label inputUnitsLabel;
	@FXML
	private Label outputUnitsLabel;

	TextField stringAutoCompleteSelector;

	CompoundTable compoundTable = null;
	Compound compound = null;

	private CategoryInput input;
	private final String MGPERKGPERDAY = "mg/kg/day";
	private final String UMOLPERKGPERDAY = "mmol/kg/day";

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
		inVivoGroup = new ToggleGroup();
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
		CategoryAnalysisParameters params = null;

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Invalid Input");
		alert.setHeaderText(null);
		try
		{
			params = this.gatherParameters();
		}
		catch (NumberFormatException e)
		{
			// Otherwise give user a message
			alert.setContentText("Invalid input fields");
			alert.showAndWait();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			if (e.getMessage() != null)
				alert.setContentText(e.getMessage());
			else
				alert.setContentText("Invalid input fields");
			alert.showAndWait();
		}

		if (params != null)
		{
			startButton.setDisable(true);
			closeButton.setDisable(true);
			presenter.startAnalyses(params);
		}
	}

	@Override
	public void handle_saveSettingsButtonPressed(ActionEvent event)
	{
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
		input.setRemoveGenesWithBMDValuesGreaterThanNFoldNumber(
				Double.parseDouble(this.bmdFilter4Value.getText()));
		input.setRemoveGenesWithMaxFoldChangeLessThanNumber(
				Double.parseDouble(this.bmdFilterMaxFoldChangeValue.getText()));
		input.setRemoveGenesWithPrefilterPValueNumber(
				Double.parseDouble(this.bmdFilterMaxPValueChangeValue.getText()));
		input.setRemoveGenesWithPrefilterAdjustedPValueNumber(
				Double.parseDouble(this.bmdFilterMaxAdjustedPValueChangeValue.getText()));
		input.setCorrelationCutoffForConflictingProbeSets(
				Double.parseDouble(this.correlationCutoffProbeSetsValue.getText()));

		BMDExpressProperties.getInstance().saveCategoryInput(input);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Saved Settings");
		alert.setHeaderText(null);
		alert.setContentText("Your settings have been saved");

		alert.showAndWait();
	}

	public void handle_auto_populate(String textValue)
	{
		compound = null;

		compound = compoundTable.getCompoundByCAS(textValue);
		if (compound == null)
			compound = compoundTable.getCompoundByName(textValue);

		if (compound != null)
		{
			String species = (String) speciesComboBox.getSelectionModel().getSelectedItem();
			Double clint = compound.getInVitroParam(species, "Clint", false);
			Double fup = compound.getInVitroParam(species, "Funbound.plasma", false);
			Double fgutabs = compound.getInVitroParam(species, "Fgutabs", false);

			if (clint == null || fup == null)
			{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Compound Data");
				alert.setHeaderText(null);
				StringBuilder builder = new StringBuilder();
				builder.append("Some compound data was not found.\n");
				if (clint == null)
				{
					clint = compound.getInVitroParam("Human", "Clint", false);
					if (clint == null)
						builder.append("No Clint values were found - Defaulted to 0\n");
					else
						builder.append("Human values were used for Clint.\n");
				}
				if (fup == null)
				{
					fup = compound.getInVitroParam("Human", "Funbound.plasma", false);
					if (fup == null)
						builder.append("No Fup values were found.\n");
					else
						builder.append("Human values were used for Fup.\n");
				}

				alert.setContentText(builder.toString());
				alert.showAndWait();
			}

			if (fgutabs == null)
			{
				fgutabs = compound.getInVitroParam("Human", "Fgutabs", false);
				if (fgutabs == null)
					fgutabs = 1.0;
			}

			// If we got a compound fill in the fields
			nameTextField.setText(compound.getName());
			casrnTextField.setText(compound.getCAS());
			smilesTextField.setText(compound.getSMILES());
			if (compound.getMW() != null)
				mwTextField.setText("" + compound.getMW());
			else
				mwTextField.setText("");

			if (compound.getLogP() != null)
				logPTextField.setText("" + compound.getLogP());
			else
				logPTextField.setText("");

			String pkaDonorString = "";
			String pkaAcceptorString = "";
			if (!compound.getpKaDonors().toString().equals("[]"))
			{
				pkaDonorString = compound.getpKaDonors().toString().substring(1,
						compound.getpKaDonors().toString().length() - 1);
			}
			if (!compound.getpKaAcceptors().toString().equals("[]"))
			{
				pkaAcceptorString = compound.getpKaAcceptors().toString().substring(1,
						compound.getpKaAcceptors().toString().length() - 1);
			}
			pKaDonorTextField.setText(pkaDonorString);
			pKaAcceptorTextField.setText(pkaAcceptorString);
			if ((compound.getInVitroParam(species, "Clint.pValue") == null
					|| compound.getInVitroParam(species, "Clint.pValue") < .05) && clint != null)
				clintTextField.setText("" + clint);
			else
				clintTextField.setText("" + 0.0);

			if (fup != null)
				fubTextField.setText("" + fup);
			else
				fubTextField.setText("");

			fGutAbs.setText("" + fgutabs);

			// Initialize source hover
			if (compound.getMWSource() != null)
				mwTextField.setTooltip(new Tooltip("MW Source: " + compound.getMWSource()));
			else
				mwTextField.setTooltip(new Tooltip("No source available"));

			if (compound.getLogPSource() != null)
				logPTextField.setTooltip(new Tooltip("LogP Source: " + compound.getLogPSource()));
			else
				logPTextField.setTooltip(new Tooltip("No source available"));

			if (compound.getpKaAcceptorsSource() != null)
				pKaAcceptorTextField
						.setTooltip(new Tooltip("pKa Acceptor Source: " + compound.getpKaAcceptorsSource()));
			else
				pKaAcceptorTextField.setTooltip(new Tooltip("No source available"));

			if (compound.getpKaDonorsSource() != null)
				pKaDonorTextField
						.setTooltip(new Tooltip("pKa Donor Source: " + compound.getpKaDonorsSource()));
			else
				pKaDonorTextField.setTooltip(new Tooltip("No source available"));
			Source dataSource = null;
			try
			{
				dataSource = compound.getIVdataSourceForSpecies(species, "Clint");
			}
			catch (Exception e)
			{}
			if (dataSource != null)
				clintTextField.setTooltip(new Tooltip("Clint Source: " + dataSource));
			else
				clintTextField.setTooltip(new Tooltip("No source available"));

			dataSource = null;
			try
			{
				dataSource = compound.getIVdataSourceForSpecies(species, "Funbound.plasma");
			}
			catch (Exception e)
			{}

			if (dataSource != null)
				fubTextField.setTooltip(new Tooltip("Fup Source: " + dataSource));
			else
				fubTextField.setTooltip(new Tooltip("No source available"));

			dataSource = null;
			try
			{
				dataSource = compound.getIVdataSourceForSpecies(species, "Fgutabs");
			}
			catch (Exception e)
			{}

			if (dataSource != null)
				fGutAbs.setTooltip(new Tooltip("Fraction Absorbed: " + dataSource));
			else
				fGutAbs.setTooltip(new Tooltip("No source available"));

		}
		else
		{
			// Otherwise give user a message
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Compound Search");
			alert.setHeaderText(null);
			alert.setContentText("Could not find compound");
			// alert.showAndWait();
		}
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
			categoryComboBox.getItems().addAll("REACTOME", "BioPlanet");
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
		else if (catAnalysisEnum == CategoryAnalysisEnum.GENE_LEVEL)
		{
			mainVBox.getChildren().remove(probeFileHBox);
			mainVBox.getChildren().remove(categoryFileHBox);
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
		this.bmdFilterMaxAdjustedPValueChangeValue
				.setText("" + input.getRemoveGenesWithPrefilterAdjustedPValueNumber());
		this.correlationCutoffProbeSetsValue
				.setText("" + input.getCorrelationCutoffForConflictingProbeSets());
		presenter.initData(bmdResults, catAnalysisEnum);

		// Initialize IVIVE check box listeners
		toggleIVIVE(false);

		if (!this.doIVIVECheckBox.isSelected())
			iviveTab.setDisable(true);

		doIVIVECheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (newValue)
				{
					iviveTab.setDisable(false);

					// load compoundTable one time if iviveTab is checked.
					// it's a big one so no need to load it multiple times.
					if (compoundTable == null)
					{
						compoundTable = CompoundTableLoader.getInstance().getCompoundTable();
						List<AutoCompleteChemical> list = new ArrayList<>();
						for (Compound c : compoundTable.getCompoundList())
						{
							AutoCompleteChemical ac = new AutoCompleteChemical();
							ac.casrn = c.getCAS();
							ac.name = c.getName();
							list.add(ac);
						}

						addIVIVESearchTextBox(list);

					}
				}
				else
					iviveTab.setDisable(true);
			}

		});

		speciesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{

			}
		});

		// Initialize quantile text field
		quantile_doseSpacingTextField.setText("0.95");

		speciesComboBox.getItems().add("Human");
		speciesComboBox.getItems().add("Rat");
		speciesComboBox.getItems().add("Mouse");
		speciesComboBox.getItems().add("Dog");
		speciesComboBox.getItems().add("Rabbit");
		speciesComboBox.getSelectionModel().select(0);

		inVitroRadioButton.setToggleGroup(inVivoGroup);
		inVivoRadioButton.setToggleGroup(inVivoGroup);
		inVitroRadioButton.setSelected(true);

		inVivoGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
					Toggle newValue)
			{
				if (((RadioButton) inVivoGroup.getSelectedToggle()).getText().contains("Forward"))
				{
					toggleInvivo(true);
				}
				else
				{
					toggleInvivo(false);
				}
			}
		});

		toggleInvivo(false);
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

		if (doIVIVECheckBox.isSelected())
		{
			compound.setName(nameTextField.getText());
			compound.setCAS(casrnTextField.getText());
			compound.setSMILES(smilesTextField.getText());
			compound.setMW(Double.valueOf(mwTextField.getText()));
			compound.setLogP(Double.valueOf(logPTextField.getText()));

			// Read in pka donors and acceptors
			ArrayList<Double> pkaDonors = new ArrayList<Double>();
			ArrayList<Double> pkaAcceptors = new ArrayList<Double>();
			Scanner scanner = new Scanner(pKaDonorTextField.getText());
			scanner.useDelimiter(", |,| ");
			while (scanner.hasNextDouble())
			{
				pkaDonors.add(scanner.nextDouble());
			}
			scanner.close();
			scanner = new Scanner(pKaAcceptorTextField.getText());
			scanner.useDelimiter(", |,| ");
			while (scanner.hasNextDouble())
			{
				pkaAcceptors.add(scanner.nextDouble());
			}
			scanner.close();
			compound.setpKaAcceptors(pkaAcceptors);
			compound.setpKaDonors(pkaDonors);

			// Initialize InVitroData with clint and fub
			InVitroData data = new InVitroData();
			data.setParam("Clint", Double.valueOf(clintTextField.getText()));
			data.setParam("Funbound.plasma", Double.valueOf(fubTextField.getText()));
			data.setParam("Fgutabs", Double.valueOf(fGutAbs.getText()));

			HashMap<String, InVitroData> map = new HashMap<String, InVitroData>();
			map.put((String) speciesComboBox.getSelectionModel().getSelectedItem(), data);
			compound.setIVdata(map);

			HashMap<String, Double> rBlood2Plasma = new HashMap<String, Double>();
			compound.setrBlood2Plasma(rBlood2Plasma);

			IVIVEParameters parameters = new IVIVEParameters();

			parameters.setCompound(compound);

			// Set params with
			List<Model> models = new ArrayList<Model>();

			models.add(Model.THREECOMPSS);

			parameters.setSpecies((String) speciesComboBox.getSelectionModel().getSelectedItem());

			parameters.setModels(models);

			if (((RadioButton) inVivoGroup.getSelectedToggle()).getText().contains("IVIVE"))
			{
				double quantile = Double.valueOf(quantile_doseSpacingTextField.getText());
				if (quantile < 0 || quantile > 1)
					throw new IllegalArgumentException("Quantile must be between 0 and 1");
				else
					parameters.setQuantile(quantile);
				parameters.setInvivo(false);

				parameters.setConcentrationUnits(
						(ConcentrationUnits) inputUnitsComboBox.getSelectionModel().getSelectedItem());
				// user has friendly text for specifying output units.
				// we must translate to enum
				if (outputUnitsComboBox.getSelectionModel().getSelectedItem().equals(MGPERKGPERDAY))
					parameters.setDoseUnits(Units.MG);
				else
					parameters.setDoseUnits(Units.MOL);
			}
			else
			{
				try
				{
					double doseSpacing = Double.valueOf(quantile_doseSpacingTextField.getText());

					// the finalTime is the sum of dosespacing between doses plus the time after last dose
					double finalTime = Double.valueOf(finalTimeTextField.getText())
							+ (doseSpacing * (Integer.valueOf(this.doseCountTextField.getText()) - 1));

					int numDoses = Integer.valueOf(this.doseCountTextField.getText());

					parameters.setFinalTime(finalTime);
					parameters.setDoseSpacing(doseSpacing);
					parameters.setInvivo(true);
					parameters.setNumberOfDoses(numDoses);

					parameters.setConcentrationUnits(ConcentrationUnits.uM);
					parameters.setDoseUnits(Units.MG);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new IllegalArgumentException(e.getMessage());
				}
			}
			params.setIviveParameters(parameters);
		}

		return params;
	}

	private void toggleIVIVE(boolean disable)
	{
		nameTextField.setDisable(disable);
		casrnTextField.setDisable(disable);
		smilesTextField.setDisable(disable);
		mwTextField.setDisable(disable);
		logPTextField.setDisable(disable);
		pKaDonorTextField.setDisable(disable);
		pKaAcceptorTextField.setDisable(disable);
		clintTextField.setDisable(disable);
		fubTextField.setDisable(disable);
		fGutAbs.setDisable(disable);
		quantile_doseSpacingTextField.setDisable(disable);
		finalTimeTextField.setDisable(disable);
		inputUnitsComboBox.setDisable(disable);
		outputUnitsComboBox.setDisable(disable);
		speciesComboBox.setDisable(false);
	}

	private void toggleInvivo(boolean invivo)
	{
		if (invivo)
		{
			quantile_doseSpacingLabel.setText("Dose Spacing  ");
			quantile_doseSpacingTextField.setText("24");
			finalTimeLabel.setVisible(true);
			finalTimeTextField.setVisible(true);
			finalTimeTextField.setText("24");
			this.doseCountLabel.setVisible(true);
			this.doseCountTextField.setVisible(true);
			doseCountTextField.setText("5");

			inputUnitsComboBox.getItems().removeAll(inputUnitsComboBox.getItems());
			inputUnitsComboBox.getItems().add(MGPERKGPERDAY);
			inputUnitsComboBox.getSelectionModel().select(0);
			outputUnitsComboBox.getItems().removeAll(outputUnitsComboBox.getItems());
			outputUnitsComboBox.getItems().add(ConcentrationUnits.uM);
			outputUnitsComboBox.getSelectionModel().select(0);
			inputUnitsLabel.setText("External Dose Units");
			outputUnitsLabel.setText("Internal dose/In Vitro Dose Units");

		}
		else
		{
			quantile_doseSpacingLabel.setText("Quantile  ");
			quantile_doseSpacingTextField.setText("0.95");
			finalTimeLabel.setVisible(false);
			finalTimeTextField.setVisible(false);

			this.doseCountLabel.setVisible(false);
			this.doseCountTextField.setVisible(false);

			inputUnitsComboBox.getItems().removeAll(inputUnitsComboBox.getItems());
			inputUnitsComboBox.getItems().addAll(ConcentrationUnits.values());
			inputUnitsComboBox.getSelectionModel().select(0);
			outputUnitsComboBox.getItems().removeAll(outputUnitsComboBox.getItems());
			outputUnitsComboBox.getItems().add(MGPERKGPERDAY);
			outputUnitsComboBox.getItems().add(UMOLPERKGPERDAY);
			outputUnitsComboBox.getSelectionModel().select(0);
			inputUnitsLabel.setText("Internal dose/In Vitro Dose Units");
			outputUnitsLabel.setText("External Dose Units");

		}
	}

	private void addIVIVESearchTextBox(List<AutoCompleteChemical> list)
	{
		handle_auto_populate("Ametryn");
		TextField stringAutoCompleteSelector = new TextField();

		// create the data to show in the CheckComboBox
		final ObservableList<AutoCompleteChemical> strings = FXCollections.observableArrayList(list);
		Set<AutoCompleteChemical> possibleValues = new HashSet<>(list);

		ComboBox<String> howtodostring;
		// Create the CheckComboBox with the data
		howtodostring = new ComboBox<String>(
				FXCollections.observableArrayList(Arrays.asList("begins with", "contains")));

		howtodostring.setValue("begins with");
		howtodostring.setMaxWidth(150);
		stringAutoCompleteSelector.setMinWidth(200);
		TextFields.bindAutoCompletion(stringAutoCompleteSelector,
				new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<AutoCompleteChemical>>() {

					@Override
					public Collection<AutoCompleteChemical> call(ISuggestionRequest param)
					{
						List<AutoCompleteChemical> returnList = new ArrayList<>();
						for (AutoCompleteChemical p : strings)
							if (howtodostring.getValue().equals("contains")
									&& p.contains(param.getUserText()))
								returnList.add(p);
							else if (howtodostring.getValue().equals("begins with")
									&& p.beginsWith(param.getUserText()))
								returnList.add(p);

						return returnList;
					}
				});

		stringAutoCompleteSelector.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == null)
				return;

			if (newValue.contains(":"))
				newValue = newValue.split(":")[0];
			handle_auto_populate(newValue);
			if (newValue.trim().equals(""))
				return;
			if (!possibleValues.contains(newValue))
				return;

		});

		HBox.setHgrow(stringAutoCompleteSelector, Priority.ALWAYS);
		autoPopulateHBox.getChildren().add(howtodostring);
		autoPopulateHBox.getChildren().add(stringAutoCompleteSelector);
	}

	private class AutoCompleteChemical
	{
		public String casrn;
		public String name;

		public boolean contains(String s)
		{
			return casrn.toLowerCase().contains(s.toLowerCase())
					|| name.toLowerCase().contains(s.toLowerCase());
		}

		public boolean beginsWith(String s)
		{
			return casrn.toLowerCase().startsWith(s.toLowerCase())
					|| name.toLowerCase().startsWith(s.toLowerCase());
		}

		@Override
		public String toString()
		{
			return casrn + ": " + name;
		}
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
