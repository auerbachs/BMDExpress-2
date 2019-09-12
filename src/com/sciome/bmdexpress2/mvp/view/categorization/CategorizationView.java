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
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters.DoseUnits;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFilesTool;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Units;
import com.sciome.commons.math.httk.model.Compound;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CategorizationView extends BMDExpressViewBase implements ICategorizationView, Initializable
{

	CategorizationPresenter					presenter;

	private CategoryAnalysisEnum			catAnalysisEnum;
	private DefinedCategoryFileParameters	probeFileParameters;
	private DefinedCategoryFileParameters	categoryFileParameters;

	// FXML injection
	@FXML
	private Tab								iviveTab;

	@FXML
	private HBox							autoPopulateHBox;

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
	private CheckBox						isInVitroCheckBox;
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

	// IVIVE
	@FXML
	private CheckBox						oneCompartmentCheckBox;
	@FXML
	private CheckBox						pbtkCheckBox;
	@FXML
	private CheckBox						threeCompartmentCheckBox;
	@FXML
	private CheckBox						threeCompartmentSSCheckBox;

	@FXML
	private TextField						nameTextField;
	@FXML
	private TextField						casrnTextField;
	@FXML
	private TextField						smilesTextField;
	@FXML
	private TextField						mwTextField;
	@FXML
	private TextField						logPTextField;
	@FXML
	private TextField						pKaDonorTextField;
	@FXML
	private TextField						pKaAcceptorTextField;
	@FXML
	private TextField						clintTextField;
	@FXML
	private TextField						fubTextField;
	@FXML
	private TextField						quantileTextField;
	@FXML
	private ComboBox						doseUnitsComboBox;
	@FXML
	private ComboBox						outputUnitsComboBox;
	@FXML
	private ComboBox						speciesComboBox;

	TextField								stringAutoCompleteSelector;

	CompoundTable							compoundTable	= null;

	private CategoryInput					input;
	private final String					MGPERKGPERDAY	= "mg/kg/day";
	private final String					UMOLPERKGPERDAY	= "uM/kg/day";

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

		Compound compound = null;

		compound = compoundTable.getCompoundByCAS(textValue);
		if (compound == null)
			compound = compoundTable.getCompoundByName(textValue);

		if (compound != null)
		{
			String species = (String) speciesComboBox.getSelectionModel().getSelectedItem();
			Double clint = compound.getInVitroParam(species, "Clint", false);
			Double fup = compound.getInVitroParam(species, "Funbound.plasma", false);
			if (clint == null && fup == null)
			{
				clint = compound.getInVitroParam("Human", "Clint", false);
				fup = compound.getInVitroParam("Human", "Funbound.plasma", false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Compound Data");
				alert.setHeaderText(null);
				alert.setContentText(
						"Compound data for species not found. Human values were used for CLint and Fup");
				alert.showAndWait();
			}
			else if (clint == null)
			{
				clint = compound.getInVitroParam("Human", "Clint", false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Compound Data");
				alert.setHeaderText(null);
				alert.setContentText("Some compound data was not found. Human values were used for Clint");
				alert.showAndWait();
			}
			else if (fup == null)
			{
				fup = compound.getInVitroParam("Human", "Funbound.plasma", false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Compound Data");
				alert.setHeaderText(null);
				alert.setContentText("Some compound data was not found. Human values were used for Fup");
				alert.showAndWait();
			}
			// If we got a compound fill in the fields
			nameTextField.setText(compound.getName());
			casrnTextField.setText(compound.getCAS());
			smilesTextField.setText(compound.getSMILES());
			mwTextField.setText("" + compound.getMW());
			logPTextField.setText("" + compound.getLogP());
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
			if (compound.getInVitroParam(species, "Clint.pValue") == null
					|| compound.getInVitroParam(species, "Clint.pValue") < .05)
				clintTextField.setText("" + clint);
			else
				clintTextField.setText("" + 0.0);

			fubTextField.setText("" + fup);
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
		toggleIVIVE(true);

		if (!this.isInVitroCheckBox.isSelected())
			iviveTab.setDisable(true);

		isInVitroCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

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
						List<String> list = new ArrayList<>();
						for (Compound c : compoundTable.getCompoundList())
						{
							list.add(c.getCAS());
							list.add(c.getName());
						}

						addIVIVESearchTextBox(list);

					}
				}
				else
					iviveTab.setDisable(true);
			}

		});

		oneCompartmentCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (newValue)
				{
					toggleIVIVE(false);
				}
				else if (!checkIVIVE())
				{
					toggleIVIVE(true);
				}
			}
		});
		pbtkCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (newValue)
				{
					toggleIVIVE(false);
				}
				else if (!checkIVIVE())
				{
					toggleIVIVE(true);
				}
			}
		});
		threeCompartmentCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (newValue)
				{
					toggleIVIVE(false);
				}
				else if (!checkIVIVE())
				{
					toggleIVIVE(true);
				}
			}
		});
		threeCompartmentSSCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue)
			{
				if (newValue)
				{
					toggleIVIVE(false);
				}
				else if (!checkIVIVE())
				{
					toggleIVIVE(true);
				}
			}
		});

		speciesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue)
			{
				if (newValue.equals("Human"))
				{
					oneCompartmentCheckBox.setDisable(false);
					threeCompartmentCheckBox.setDisable(false);
					pbtkCheckBox.setDisable(false);
				}
				else
				{
					oneCompartmentCheckBox.setSelected(false);
					threeCompartmentCheckBox.setSelected(false);
					pbtkCheckBox.setSelected(false);
					oneCompartmentCheckBox.setDisable(true);
					threeCompartmentCheckBox.setDisable(true);
					pbtkCheckBox.setDisable(true);
				}
			}
		});

		// Initialize quantile text field
		quantileTextField.setText("0.95");

		// Initialize Combo box fields
		doseUnitsComboBox.getItems().addAll(DoseUnits.values());
		doseUnitsComboBox.getSelectionModel().select(0);

		// kgs/mg/per day?
		outputUnitsComboBox.getItems().add(MGPERKGPERDAY);

		// is this milli-mols-per-kg-day?
		outputUnitsComboBox.getItems().add(UMOLPERKGPERDAY);
		outputUnitsComboBox.getSelectionModel().select(0);

		speciesComboBox.getItems().add("Human");
		speciesComboBox.getItems().add("Rat");
		speciesComboBox.getItems().add("Mouse");
		speciesComboBox.getItems().add("Dog");
		speciesComboBox.getItems().add("Rabbit");
		speciesComboBox.getSelectionModel().select(0);
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

		if (isInVitroCheckBox.isSelected() && checkIVIVE())
		{
			String name = nameTextField.getText();
			String casrn = casrnTextField.getText();
			String smiles = smilesTextField.getText();
			double mw = Double.valueOf(mwTextField.getText());
			double logP = Double.valueOf(logPTextField.getText());

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

			// Initialize InVitroData with clint and fub
			InVitroData data = new InVitroData();
			data.setParam("Clint", Double.valueOf(clintTextField.getText()));
			data.setParam("Funbound.plasma", Double.valueOf(fubTextField.getText()));
			HashMap<String, InVitroData> map = new HashMap<String, InVitroData>();
			map.put((String) speciesComboBox.getSelectionModel().getSelectedItem(), data);

			HashMap<String, Double> rBlood2Plasma = new HashMap<String, Double>();

			IVIVEParameters parameters = new IVIVEParameters();

			parameters.setCompound(new Compound(name, casrn, smiles, logP, mw, null, pkaAcceptors, pkaDonors,
					map, rBlood2Plasma));

			// Set params with
			List<Model> models = new ArrayList<Model>();
			if (oneCompartmentCheckBox.isSelected())
				models.add(Model.ONECOMP);
			if (threeCompartmentCheckBox.isSelected())
				models.add(Model.THREECOMP);
			if (pbtkCheckBox.isSelected())
				models.add(Model.PBTK);
			if (threeCompartmentSSCheckBox.isSelected())
				models.add(Model.THREECOMPSS);

			parameters.setModels(models);
			double quantile = Double.valueOf(quantileTextField.getText());
			if (quantile < 0 || quantile > 1)
				throw new IllegalArgumentException("Quantile must be between 0 and 1");
			else
				parameters.setQuantile(quantile);

			parameters.setDoseUnits((DoseUnits) doseUnitsComboBox.getSelectionModel().getSelectedItem());

			// user has friendly text for specifying output units.
			// we must translate to enum
			if (outputUnitsComboBox.getSelectionModel().getSelectedItem().equals(MGPERKGPERDAY))
				parameters.setOutputUnits(Units.MG);
			else
				parameters.setOutputUnits(Units.MOL);

			parameters.setSpecies((String) speciesComboBox.getSelectionModel().getSelectedItem());
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
		quantileTextField.setDisable(disable);
		doseUnitsComboBox.setDisable(disable);
		outputUnitsComboBox.setDisable(disable);
		speciesComboBox.setDisable(false);
	}

	private void addIVIVESearchTextBox(List<String> list)
	{
		TextField stringAutoCompleteSelector = new TextField();

		// create the data to show in the CheckComboBox
		final ObservableList<String> strings = FXCollections.observableArrayList(list);
		Set<String> possibleValues = new HashSet<>(list);

		Button clearButton = new Button("Clear");
		ComboBox<String> howtodostring;
		// Create the CheckComboBox with the data
		howtodostring = new ComboBox<String>(
				FXCollections.observableArrayList(Arrays.asList("begins with", "contains")));

		howtodostring.setValue("begins with");
		howtodostring.setMaxWidth(150);
		stringAutoCompleteSelector.setMinWidth(200);
		TextFields.bindAutoCompletion(stringAutoCompleteSelector,
				new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {

					@Override
					public Collection<String> call(ISuggestionRequest param)
					{
						List<String> returnList = new ArrayList<>();
						for (String p : strings)
							if (howtodostring.getValue().equals("contains")
									&& p.toLowerCase().contains(param.getUserText().toLowerCase()))
								returnList.add(p);
							else if (howtodostring.getValue().equals("begins with")
									&& p.toLowerCase().startsWith(param.getUserText().toLowerCase()))
								returnList.add(p);

						return returnList;
					}
				});

		stringAutoCompleteSelector.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == null)
				return;

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

	private boolean checkIVIVE()
	{
		return oneCompartmentCheckBox.isSelected() || pbtkCheckBox.isSelected()
				|| threeCompartmentCheckBox.isSelected() || threeCompartmentSSCheckBox.isSelected();
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
