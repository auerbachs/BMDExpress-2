package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.commandline.config.RunConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSModelConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.ExponentialConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.HillConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.PolyConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.PowerConfig;
import com.sciome.bmdexpress2.commandline.config.category.CategoryConfig;
import com.sciome.bmdexpress2.commandline.config.category.DefinedConfig;
import com.sciome.bmdexpress2.commandline.config.category.GOConfig;
import com.sciome.bmdexpress2.commandline.config.category.GeneLevelConfig;
import com.sciome.bmdexpress2.commandline.config.category.IVIVEConfig;
import com.sciome.bmdexpress2.commandline.config.category.PathwayConfig;
import com.sciome.bmdexpress2.commandline.config.expression.ExpressionDataConfig;
import com.sciome.bmdexpress2.commandline.config.nonparametric.NonParametricConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.ANOVAConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.OriogenConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.PrefilterConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.WilliamsConfig;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.FileIO;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionBMDLandBMDU;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionWithFlaggedHillModelEnum;
import com.sciome.bmdexpress2.util.bmds.shared.BestPolyModelTestEnum;
import com.sciome.bmdexpress2.util.bmds.shared.ExponentialModel;
import com.sciome.bmdexpress2.util.bmds.shared.FlagHillModelDoseEnum;
import com.sciome.bmdexpress2.util.bmds.shared.HillModel;
import com.sciome.bmdexpress2.util.bmds.shared.PolyModel;
import com.sciome.bmdexpress2.util.bmds.shared.PowerModel;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.model.Compound;
import com.sciome.commons.math.httk.model.CompoundTable;
import com.sciome.commons.math.httk.model.InVitroData;

/*
 * When command line is in "analyze" mode, use this class to run the different analyses
 * specified in the configuration file that is passed in.
 */
public class AnalyzeRunner
{

	BMDProject project = new BMDProject();

	public void analyze(String configFile) throws Exception
	{

		// deserialize the config file that was passed on commandline
		RunConfig runConfig = getRunConfig(configFile);

		// load the project if the file exists.
		// if overwrite is set to true then don't open it, but rather start fresh
		// This little bit of code will set the base directory path.
		// sometimes when running command line, you want to copy your base path
		// to another node from home dir and use that so that many simultaneous
		// running instances are not hitting the home dir at the same time.
		if (runConfig.getBasePath() == null || runConfig.getBasePath().equals(""))
			BMDExpressConstants.getInstance();
		else
			BMDExpressConstants.getInstance(runConfig.getBasePath());

		BMDExpressProperties.getInstance().setIsConsole(true);

		if (new File(runConfig.getBm2FileName()).exists() && !runConfig.getOverwrite())
		{
			try
			{
				FileInputStream fileIn = new FileInputStream(new File(runConfig.getBm2FileName()));
				BufferedInputStream bIn = new BufferedInputStream(fileIn, 1024 * 2000);

				ObjectInputStream in = new ObjectInputStream(bIn);
				project = (BMDProject) in.readObject();
				in.close();
				fileIn.close();
			}
			catch (IOException i)
			{
				i.printStackTrace();
			}
			catch (ClassNotFoundException c)
			{
				c.printStackTrace();
			}
		}

		// 1: get all the expression data configs
		List<ExpressionDataConfig> expressionConfigs = runConfig.getExpressionDataConfigs();
		if (expressionConfigs != null)
			for (ExpressionDataConfig expressionConfig : expressionConfigs)
				doExpressionConfig(expressionConfig);

		// 2: get all the anova configs
		List<PrefilterConfig> preFilterConfigs = runConfig.getPreFilterConfigs();

		if (preFilterConfigs != null)
			for (PrefilterConfig preFilterConfig : preFilterConfigs)
				doPrefilter(preFilterConfig);

		// 3: get all the analysis configs
		List<BMDSConfig> bmdsConfigs = runConfig.getBmdsConfigs();
		if (bmdsConfigs != null)
			for (BMDSConfig bmdsConfig : bmdsConfigs)
				doBMDSAnalysis(bmdsConfig);

		// 4: get all the analysis configs
		List<NonParametricConfig> nonParametricConfigs = runConfig.getNonParametricConfigs();
		if (nonParametricConfigs != null)
			for (NonParametricConfig nonPConfig : nonParametricConfigs)
				doNonParametricAnalysis(nonPConfig);

		// 5: get all the category analysis configs
		List<CategoryConfig> catConfigs = runConfig.getCategoryAnalysisConfigs();
		if (catConfigs != null)
			for (CategoryConfig catConfig : catConfigs)
				doCatAnalysis(catConfig);

		// 6. see if this needs exporting to json
		if (runConfig.getJsonExportFileName() != null)
			doJsonExport(runConfig.getJsonExportFileName());

		try
		{
			File selectedFile = new File(runConfig.getBm2FileName());
			FileOutputStream fileOut = new FileOutputStream(selectedFile);

			int bufferSize = 2000 * 1024; // make it a 2mb buffer
			BufferedOutputStream bout = new BufferedOutputStream(fileOut, bufferSize);
			ObjectOutputStream out = new ObjectOutputStream(bout);
			project.setName(selectedFile.getName());
			out.writeObject(project);
			out.close();
			fileOut.close();
		}
		catch (IOException i)
		{
			i.printStackTrace();
		}
	}

	// invoke the export to json functionality.
	private void doJsonExport(String jsonExportFileName) throws Exception
	{
		System.out.println("export to json");
		new ExportRunner().exportToJson(project, jsonExportFileName);
	}

	/*
	 * perform category analysis based on the category configuration.
	 */
	private void doCatAnalysis(CategoryConfig catConfig)
	{

		CategoryAnalysisEnum catAn = null;
		String analysisSpecificMessage = "";
		if (catConfig instanceof GOConfig)
		{
			catAn = CategoryAnalysisEnum.GO;
			analysisSpecificMessage = "GO category = " + ((GOConfig) catConfig).getGoCategory();
		}
		else if (catConfig instanceof DefinedConfig)
		{
			catAn = CategoryAnalysisEnum.DEFINED;

			analysisSpecificMessage = "probe file=" + ((DefinedConfig) catConfig).getProbeFilePath() + "\n"
					+ "category file=" + ((DefinedConfig) catConfig).getCategoryFilePath();
		}
		else if (catConfig instanceof PathwayConfig)
		{
			catAn = CategoryAnalysisEnum.PATHWAY;
			analysisSpecificMessage = "Pathway = " + ((PathwayConfig) catConfig).getSignalingPathway();
		}
		else if (catConfig instanceof GeneLevelConfig)
		{
			catAn = CategoryAnalysisEnum.GENE_LEVEL;
			analysisSpecificMessage = "Gene Level Category Analysis";
		}

		if (catConfig.getInputName() != null)
			System.out.println(catAn.toString() + " analysis on " + catConfig.getInputName());
		else
			System.out.println(catAn.toString() + " analysis");

		System.out.println(analysisSpecificMessage);

		List<BMDResult> bmdResultsToRun = new ArrayList<>();
		for (BMDResult result : project.getbMDResult())
			if (catConfig.getInputName() == null)
				bmdResultsToRun.add(result);
			else if (result.getName().equalsIgnoreCase(catConfig.getInputName()))
				bmdResultsToRun.add(result);

		CategoryAnalysisParameters params = new CategoryAnalysisParameters();

		if (catConfig.getBmdBMDLRatioMin() == null)
			params.setRemoveBMDBMDLRatio(false);
		else
		{
			params.setBmdBmdlRatio(catConfig.getBmdBMDLRatioMin());
			params.setRemoveBMDBMDLRatio(true);
		}

		if (catConfig.getBmduBMDLRatioMin() == null)
			params.setRemoveBMDUBMDLRatio(false);
		else
		{
			params.setBmduBmdlRatio(catConfig.getBmduBMDLRatioMin());
			params.setRemoveBMDUBMDLRatio(true);
		}

		if (catConfig.getBmduBMDRatioMin() == null)
			params.setRemoveBMDUBMDRatio(false);
		else
		{
			params.setBmduBmdRatio(catConfig.getBmduBMDRatioMin());
			params.setRemoveBMDUBMDRatio(true);
		}
		if (catConfig.getBmdPValueCutoff() == null)
			params.setRemoveBMDPValueLessCuttoff(false);
		else
		{
			params.setpValueCutoff(catConfig.getBmdPValueCutoff());
			params.setRemoveBMDPValueLessCuttoff(true);
		}

		if (catConfig.getMaxFoldChange() == null)
			params.setUserFoldChangeFilter(false);
		else
		{
			params.setMaxFoldChange(catConfig.getMaxFoldChange());
			params.setUserFoldChangeFilter(true);
		}

		if (catConfig.getPrefilterPValueMin() == null)
			params.setUserPValueFilter(false);
		else
		{
			params.setPValue(catConfig.getPrefilterPValueMin());
			params.setUserPValueFilter(true);
		}

		if (catConfig.getPrefilterAdjustedPValueMin() == null)
			params.setUserAdjustedPValueFilter(false);
		else
		{
			params.setAdjustedPValue(catConfig.getPrefilterAdjustedPValueMin());
			params.setUserAdjustedPValueFilter(true);
		}

		if (catConfig.getCorrelationCutoffForConflictingProbeSets() != null)
			params.setCorrelationCutoffConflictingProbeSets(
					catConfig.getCorrelationCutoffForConflictingProbeSets());

		if (catConfig.getIdentifyConflictingProbeSets() == null)
			params.setIdentifyConflictingProbeSets(false);
		else
			params.setIdentifyConflictingProbeSets(catConfig.getIdentifyConflictingProbeSets());

		if (catConfig.getnFoldBelowLowestDose() == null)
			params.setRemoveNFoldBelowLowestDose(false);
		else
		{
			params.setnFoldbelowLowestDoseValue(catConfig.getnFoldBelowLowestDose());
			params.setRemoveNFoldBelowLowestDose(true);
		}

		if (catConfig.getRemoveBMDGreaterHighDose() == null)
			params.setRemoveBMDGreaterHighDose(false);
		else
			params.setRemoveBMDGreaterHighDose(catConfig.getRemoveBMDGreaterHighDose());

		if (catConfig.getRemovePromiscuousProbes() == null)
			params.setRemovePromiscuousProbes(false);
		else
			params.setRemovePromiscuousProbes(catConfig.getRemovePromiscuousProbes());

		if (catConfig.getDeduplicateGeneSets() == null)
			params.setDeduplicateGeneSets(false);
		else
			params.setDeduplicateGeneSets(catConfig.getDeduplicateGeneSets());

		// Set IVIVE parameters
		if (catConfig.getComputeIVIVE())
		{
			IVIVEConfig config = catConfig.getIviveConfig();

			IVIVEParameters iviveParameters = new IVIVEParameters();
			// Set compound
			Compound compound = null;
			if (config.getUseAutoPopulate())
			{
				// Initialize InVitroData with clint and fub
				InVitroData data = new InVitroData();
				data.setParam("Clint", config.getCLint());
				data.setParam("Funbound.plasma", config.getFractionUnboundPlasma());
				HashMap<String, InVitroData> map = new HashMap<String, InVitroData>();
				map.put(config.getSpecies(), data);

				HashMap<String, Double> rBlood2Plasma = new HashMap<String, Double>();

				compound = new Compound(config.getCompoundName(), config.getCompoundCASRN(),
						config.getCompoundSMILES(), config.getLogP(), config.getMw(), 0.0,
						config.getPkaAcceptor(), config.getPkaDonor(), map, rBlood2Plasma);
			}
			else
			{
				CompoundTable table = CompoundTable.getInstance();
				table.loadDefault();
				if (config.getCompoundName() != null)
				{
					compound = table.getCompoundByName(config.getCompoundName());
				}
				else if (config.getCompoundCASRN() != null)
				{
					compound = table.getCompoundByCAS(config.getCompoundCASRN());
				}
				else if (config.getCompoundSMILES() != null)
				{
					compound = table.getCompoundBySMILES(config.getCompoundSMILES());
				}
			}
			iviveParameters.setCompound(compound);

			// Set models
			List<Model> models = new ArrayList<Model>();
			if (config.getOneCompartment())
				models.add(Model.ONECOMP);
			if (config.getPbtk())
				models.add(Model.PBTK);
			if (config.getThreeCompartment())
				models.add(Model.THREECOMP);
			if (config.getThreeCompartmentSS())
				models.add(Model.THREECOMPSS);
			iviveParameters.setModels(models);

			iviveParameters.setDoseUnits(config.getDoseUnits());
			iviveParameters.setOutputUnits(config.getOutputUnits());
			iviveParameters.setQuantile(config.getQuantile());
			iviveParameters.setSpecies(config.getSpecies());

			params.setIviveParameters(iviveParameters);
		}

		if (catConfig instanceof DefinedConfig)
		{
			DefinedCategoryFileParameters probeFileParameters = new DefinedCategoryFileParameters();

			probeFileParameters.setUsedColumns(new int[] { 0, 1 });
			probeFileParameters.setFileName(((DefinedConfig) catConfig).getProbeFilePath());
			MatrixData idsMatrix = FileIO.readFileMatrix(null,
					new File(((DefinedConfig) catConfig).getProbeFilePath()));
			idsMatrix.setAllString(true);
			probeFileParameters.setMatrixData(idsMatrix);

			DefinedCategoryFileParameters catFileParameters = new DefinedCategoryFileParameters();

			catFileParameters.setUsedColumns(new int[] { 0, 1, 2 });
			catFileParameters.setFileName(((DefinedConfig) catConfig).getProbeFilePath());
			MatrixData idsMatrix1 = FileIO.readFileMatrix(null,
					new File(((DefinedConfig) catConfig).getCategoryFilePath()));
			idsMatrix1.setAllString(true);
			catFileParameters.setMatrixData(idsMatrix1);
			params.setProbeFileParameters(probeFileParameters);
			params.setCategoryFileParameters(catFileParameters);
		}

		if (catConfig instanceof PathwayConfig)
			params.setPathwayDB(((PathwayConfig) catConfig).getSignalingPathway());
		if (catConfig instanceof GOConfig)
		{
			params.setGoCat(((GOConfig) catConfig).getGoCategory());

			if (((GOConfig) catConfig).getGoCategory().equalsIgnoreCase("universal"))
				params.setGoTermIdx(0);
			else if (((GOConfig) catConfig).getGoCategory().equalsIgnoreCase("biological_process"))
				params.setGoTermIdx(1);
			else if (((GOConfig) catConfig).getGoCategory().equalsIgnoreCase("molecular_function"))
				params.setGoTermIdx(2);
			else if (((GOConfig) catConfig).getGoCategory().equalsIgnoreCase("cellular_component"))
				params.setGoTermIdx(3);

		}

		for (BMDResult bmdResult : bmdResultsToRun)
		{
			CategoryAnalysisResults catResults = new CategoryAnalysisRunner().runCategoryAnalysis(bmdResult,
					catAn, params);

			if (catConfig.getOutputName() != null)
				catResults.setName(catConfig.getOutputName());
			else
				project.giveBMDAnalysisUniqueName(catResults, catResults.getName());
			project.getCategoryAnalysisResults().add(catResults);
		}
	}

	/*
	 * perform bmd analysis on the data.
	 */
	private void doBMDSAnalysis(BMDSConfig bmdsConfig)
	{
		if (bmdsConfig.getInputName() != null)
			System.out.println("bmd analysis on " + bmdsConfig.getInputName() + " from group "
					+ bmdsConfig.getInputCategory());
		else
			System.out.println("bmd analysis on group " + bmdsConfig.getInputCategory());
		// first set up the model input parameters basedo n
		// bmdsConfig setup
		ModelInputParameters inputParameters = new ModelInputParameters();

		inputParameters.setIterations(bmdsConfig.getBmdsInputConfig().getMaxIterations());
		inputParameters.setConfidence(bmdsConfig.getBmdsInputConfig().getConfidenceLevel());
		inputParameters.setBmrLevel(bmdsConfig.getBmdsInputConfig().getBmrFactor());
		inputParameters.setNumThreads(bmdsConfig.getNumberOfThreads());
		if (bmdsConfig.getKillTime() != null)
			inputParameters.setKillTime(bmdsConfig.getKillTime().intValue() * 1000);
		else
			inputParameters.setKillTime(600000); // default to 10 minute timeout

		inputParameters.setBmdlCalculation(1);
		inputParameters.setBmdCalculation(1);
		inputParameters.setConstantVariance((bmdsConfig.getBmdsInputConfig().getConstantVariance()) ? 1 : 0);
		// for simulation only?
		inputParameters.setRestirctPower((bmdsConfig.getBmdsInputConfig().getRestrictPower()) ? 1 : 0);

		// in practice bmrtype can only be set to relative deviation for non-log normalized data.
		inputParameters.setBmrType(1);
		if (bmdsConfig.getBmdsInputConfig().getBmrType() != null)
			inputParameters.setBmrType(bmdsConfig.getBmdsInputConfig().getBmrType().intValue());

		if (inputParameters.getConstantVariance() == 0)
			inputParameters.setRho(inputParameters.getNegative());

		// now set up the model selection parameters.
		ModelSelectionParameters modelSelectionParameters = new ModelSelectionParameters();

		// set up how to use the bmdl and bmdu
		modelSelectionParameters
				.setBestModelSelectionBMDLandBMDU(BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE);
		if (bmdsConfig.getBmdsBestModelSelection().getBmdlBMDUUse().equals(2))
			modelSelectionParameters
					.setBestModelSelectionBMDLandBMDU(BestModelSelectionBMDLandBMDU.COMPUTE_BUT_IGNORE);
		else if (bmdsConfig.getBmdsBestModelSelection().getBmdlBMDUUse().equals(3))
			modelSelectionParameters
					.setBestModelSelectionBMDLandBMDU(BestModelSelectionBMDLandBMDU.DO_NOT_COMPUTE);
		BestPolyModelTestEnum polyTest = null;
		if (bmdsConfig.getBmdsBestModelSelection().getBestPolyTest().equals(2))
			polyTest = BestPolyModelTestEnum.LOWEST_AIC;
		else if (bmdsConfig.getBmdsBestModelSelection().getBestPolyTest().equals(1))
			polyTest = BestPolyModelTestEnum.NESTED_CHI_SQUARED;
		modelSelectionParameters.setBestPolyModelTest(polyTest);

		// set up the pValue
		modelSelectionParameters.setpValue(bmdsConfig.getBmdsBestModelSelection().getpValueCutoff());

		// set up Flag HIll
		modelSelectionParameters
				.setFlagHillModel(bmdsConfig.getBmdsBestModelSelection().getFlagHillWithKParameter());

		FlagHillModelDoseEnum flagHillDose = null;
		if (bmdsConfig.getBmdsBestModelSelection().getkParameterValue().equals(1))
			flagHillDose = FlagHillModelDoseEnum.LOWEST_DOSE;
		else if (bmdsConfig.getBmdsBestModelSelection().getkParameterValue().equals(2))
			flagHillDose = FlagHillModelDoseEnum.ONE_HALF_OF_LOWEST_DOSE;
		else if (bmdsConfig.getBmdsBestModelSelection().getkParameterValue().equals(3))
			flagHillDose = FlagHillModelDoseEnum.ONE_THIRD_OF_LOWEST_DOSE;

		modelSelectionParameters.setFlagHillModelDose(flagHillDose);

		// best model selection with flagged hill model
		BestModelSelectionWithFlaggedHillModelEnum bestModeSel = null;
		if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(1))
			bestModeSel = BestModelSelectionWithFlaggedHillModelEnum.INCLUDE_FLAGGED_HILL;
		else if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(2))
			bestModeSel = BestModelSelectionWithFlaggedHillModelEnum.EXCLUDE_FLAGGED_HILL_FROM_BEST;
		else if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(3))
			bestModeSel = BestModelSelectionWithFlaggedHillModelEnum.EXCLUDE_ALL_HILL_FROM_BEST;
		else if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(4))
			bestModeSel = BestModelSelectionWithFlaggedHillModelEnum.MODIFY_BMD_IF_FLAGGED_HILL_BEST;
		else if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(5))
			bestModeSel = BestModelSelectionWithFlaggedHillModelEnum.SELECT_NEXT_BEST_PVALUE_GREATER_OO5;

		modelSelectionParameters.setBestModelSelectionWithFlaggedHill(bestModeSel);

		if (bmdsConfig.getBmdsBestModelSelection().getBestModelSelectionWithFlaggedHill().equals(4))
			modelSelectionParameters.setModFlaggedHillBMDFractionMinBMD(
					bmdsConfig.getBmdsBestModelSelection().getModifyFlaggedHillWithFractionMinBMD());
		else
			modelSelectionParameters.setModFlaggedHillBMDFractionMinBMD(0.5);

		// figure out which models are going to be run
		List<StatModel> modelsToRun = new ArrayList<>();
		for (BMDSModelConfig modelConfig : bmdsConfig.getModelConfigs())
		{
			if (modelConfig instanceof HillConfig)
			{
				HillModel hillModel = new HillModel();
				hillModel.setVersion(BMDExpressProperties.getInstance().getHillVersion());
				modelsToRun.add(hillModel);
			}
			if (modelConfig instanceof PowerConfig)
			{
				PowerModel powerModel = new PowerModel();
				powerModel.setVersion(BMDExpressProperties.getInstance().getPowerVersion());
				modelsToRun.add(powerModel);
			}
			if (modelConfig instanceof PolyConfig)
			{
				PolyModel polymodel = new PolyModel();
				polymodel.setVersion(BMDExpressProperties.getInstance().getPolyVersion());
				polymodel.setDegree(((PolyConfig) modelConfig).getDegree());
				modelsToRun.add(polymodel);
			}

			if (modelConfig instanceof ExponentialConfig)
			{
				ExponentialModel exponentialModel = new ExponentialModel();
				exponentialModel.setVersion(BMDExpressProperties.getInstance().getExponentialVersion());
				exponentialModel.setOption(((ExponentialConfig) modelConfig).getExpModel());
				modelsToRun.add(exponentialModel);
			}

		}

		// if inputname is specified then get the analysis that matches name.
		// otherwise get all the analysis based on the given input category.
		// input category can be "anova" or "expression" which means
		// one way anova results or dose response expersssion data.
		List<IStatModelProcessable> processables = new ArrayList<>();
		// get the dataset to run

		for (OneWayANOVAResults ways : project.getOneWayANOVAResults())
			if (bmdsConfig.getInputCategory().equalsIgnoreCase("anova"))
				if (bmdsConfig.getInputName() == null)
					processables.add(ways);
				else if (ways.getName().equalsIgnoreCase(bmdsConfig.getInputName()))
					processables.add(ways);

		for (WilliamsTrendResults will : project.getWilliamsTrendResults())
			if (bmdsConfig.getInputCategory().equalsIgnoreCase("williams"))
				if (bmdsConfig.getInputName() == null)
					processables.add(will);
				else if (will.getName().equalsIgnoreCase(bmdsConfig.getInputName()))
					processables.add(will);

		for (OriogenResults ori : project.getOriogenResults())
			if (bmdsConfig.getInputCategory().equalsIgnoreCase("oriogen"))
				if (bmdsConfig.getInputName() == null)
					processables.add(ori);
				else if (ori.getName().equalsIgnoreCase(bmdsConfig.getInputName()))
					processables.add(ori);

		for (DoseResponseExperiment exps : project.getDoseResponseExperiments())
			if (bmdsConfig.getInputCategory().equalsIgnoreCase("expression"))
				if (bmdsConfig.getInputName() == null)
					processables.add(exps);
				else if (exps.getName().equalsIgnoreCase(bmdsConfig.getInputName()))
					processables.add(exps);

		// for each processable analysis, run the models and select best models.
		for (IStatModelProcessable processableData : processables)
		{
			BMDResult result = new BMDAnalysisRunner().runBMDAnalysis(processableData,
					modelSelectionParameters, modelsToRun, inputParameters, bmdsConfig.getTmpFolder());
			if (bmdsConfig.getOutputName() != null)
				result.setName(bmdsConfig.getOutputName());
			else
				project.giveBMDAnalysisUniqueName(result, result.getName());
			project.getbMDResult().add(result);
		}

	}

	/*
	 * perform bmd analysis on the data.
	 */
	private void doNonParametricAnalysis(NonParametricConfig config)
	{
		if (config.getInputName() != null)
			System.out.println(
					"bmd analysis on " + config.getInputName() + " from group " + config.getInputCategory());
		else
			System.out.println("non parametric bmd analysis on group " + config.getInputCategory());
		// first set up the model input parameters basedo n
		// bmdsConfig setup
		GCurvePInputParameters inputParameters = new GCurvePInputParameters();
		inputParameters.setBootStraps(config.getBootStraps());
		inputParameters.setBMR(config.getBmrFactor().floatValue());
		inputParameters.setpValueCutoff(config.getpValueConfidence().floatValue());

		// if inputname is specified then get the analysis that matches name.
		// otherwise get all the analysis based on the given input category.
		// input category can be "anova" or "expression" which means
		// one way anova results or dose response expersssion data.
		List<IStatModelProcessable> processables = new ArrayList<>();
		// get the dataset to run

		for (OneWayANOVAResults ways : project.getOneWayANOVAResults())
			if (config.getInputCategory().equalsIgnoreCase("anova"))
				if (config.getInputName() == null)
					processables.add(ways);
				else if (ways.getName().equalsIgnoreCase(config.getInputName()))
					processables.add(ways);

		for (WilliamsTrendResults will : project.getWilliamsTrendResults())
			if (config.getInputCategory().equalsIgnoreCase("williams"))
				if (config.getInputName() == null)
					processables.add(will);
				else if (will.getName().equalsIgnoreCase(config.getInputName()))
					processables.add(will);

		for (OriogenResults ori : project.getOriogenResults())
			if (config.getInputCategory().equalsIgnoreCase("oriogen"))
				if (config.getInputName() == null)
					processables.add(ori);
				else if (ori.getName().equalsIgnoreCase(config.getInputName()))
					processables.add(ori);

		for (DoseResponseExperiment exps : project.getDoseResponseExperiments())
			if (config.getInputCategory().equalsIgnoreCase("expression"))
				if (config.getInputName() == null)
					processables.add(exps);
				else if (exps.getName().equalsIgnoreCase(config.getInputName()))
					processables.add(exps);

		// for each processable analysis, run the models and select best models.
		for (IStatModelProcessable processableData : processables)
		{
			BMDResult result = new NonParametricAnalysisRunner().runBMDAnalysis(processableData,
					inputParameters);

			if (config.getOutputName() != null)
				result.setName(config.getOutputName());
			else
				project.giveBMDAnalysisUniqueName(result, result.getName());
			project.getbMDResult().add(result);
		}

	}

	/*
	 * do prefilter
	 */
	private void doPrefilter(PrefilterConfig preFilterConfig)
	{
		// if the user specifies a dose experiment name, then find it and add it.
		// if the inputname is null, then add all dose response experiments
		// to receive the pre filter.
		List<IStatModelProcessable> processables = new ArrayList<>();
		for (DoseResponseExperiment exp : project.getDoseResponseExperiments())
			if (preFilterConfig.getInputName() == null)
				processables.add(exp);
			else if (exp.getName().equalsIgnoreCase(preFilterConfig.getInputName()))
				processables.add(exp);

		String stdoutInfo = "";
		if (preFilterConfig instanceof ANOVAConfig)
		{
			ANOVARunner anovaRunner = new ANOVARunner();
			if (preFilterConfig.getInputName() != null)
				stdoutInfo = "One-way ANOVA on " + preFilterConfig.getInputName();
			else
				stdoutInfo = "One-way ANOVA";

			System.out.println("Starting " + stdoutInfo);
			for (IStatModelProcessable processable : processables)
			{
				project.getOneWayANOVAResults()
						.add(anovaRunner.runANOVAFilter(processable, preFilterConfig.getpValueCutoff(),
								preFilterConfig.getUseMultipleTestingCorrection(),
								preFilterConfig.getFilterOutControlGenes(),
								preFilterConfig.getUseFoldChange(), preFilterConfig.getFoldChange(),
								preFilterConfig.getpValueLotel(), preFilterConfig.getFoldChangeLoel(),
								preFilterConfig.getOutputName(), preFilterConfig.getNumberOfThreads(),
								preFilterConfig.getlotelTest().equals(2), project));
			}
		}
		else if (preFilterConfig instanceof WilliamsConfig)
		{
			WilliamsTrendRunner williamsRunner = new WilliamsTrendRunner();

			if (preFilterConfig.getInputName() != null)
				stdoutInfo = "Williams Trend Test on " + preFilterConfig.getInputName();
			else
				stdoutInfo = "Williams Trend Test";

			System.out.println("Starting " + stdoutInfo);
			for (IStatModelProcessable processable : processables)
			{
				project.getWilliamsTrendResults().add(williamsRunner.runWilliamsTrendFilter(processable,
						preFilterConfig.getpValueCutoff(), preFilterConfig.getUseMultipleTestingCorrection(),
						preFilterConfig.getFilterOutControlGenes(), preFilterConfig.getUseFoldChange(),
						preFilterConfig.getFoldChange(),
						((WilliamsConfig) preFilterConfig).getNumberOfPermutations(),
						preFilterConfig.getpValueLotel(), preFilterConfig.getFoldChangeLoel(),
						preFilterConfig.getOutputName(), preFilterConfig.getNumberOfThreads(),
						preFilterConfig.getlotelTest().equals(2), project));
			}
		}
		else if (preFilterConfig instanceof OriogenConfig)
		{
			OriogenRunner oriogenRunner = new OriogenRunner();

			if (preFilterConfig.getInputName() != null)
				stdoutInfo = "Oriogen on " + preFilterConfig.getInputName();
			else
				stdoutInfo = "Oriogen";

			System.out.println("Starting " + stdoutInfo);
			for (IStatModelProcessable processable : processables)
			{
				project.getOriogenResults()
						.add(oriogenRunner.runOriogenFilter(processable, preFilterConfig.getpValueCutoff(),
								preFilterConfig.getUseMultipleTestingCorrection(),
								((OriogenConfig) preFilterConfig).getMpc(),
								((OriogenConfig) preFilterConfig).getInitialBootstraps(),
								((OriogenConfig) preFilterConfig).getMaxBootstraps(),
								((OriogenConfig) preFilterConfig).getS0Adjustment(),
								preFilterConfig.getFilterOutControlGenes(),
								preFilterConfig.getUseFoldChange(), preFilterConfig.getFoldChange(),
								preFilterConfig.getpValueLotel(), preFilterConfig.getFoldChangeLoel(),
								preFilterConfig.getOutputName(), preFilterConfig.getNumberOfThreads(),
								preFilterConfig.getlotelTest().equals(2), project));
			}
		}
		System.out.println("Finished " + stdoutInfo);
	}

	private void doExpressionConfig(ExpressionDataConfig expressionConfig)
	{

		System.out.println("import expression: " + expressionConfig.getInputFileName());

		// if the inputfilename is a directory, then loop through each file
		// in the directory and import it as a doseresponse experiment.
		if (new File(expressionConfig.getInputFileName()).isDirectory())
		{
			for (final File fileEntry : new File(expressionConfig.getInputFileName()).listFiles())
			{
				if (fileEntry.isDirectory())
					continue;
				// the name stored in project bm2 file should be name of file without the extension
				String outname = FilenameUtils.removeExtension(fileEntry.getName());
				project.getDoseResponseExperiments()
						.add((new ExpressionImportRunner()).runExpressionImport(fileEntry,
								expressionConfig.getPlatform(), outname,
								expressionConfig.getLogTransformation()));
			}

		}
		else
		{
			File inputFile = new File(expressionConfig.getInputFileName());
			String outname = FilenameUtils.removeExtension(inputFile.getName());

			// if config file outputname is set, then override the default
			if (expressionConfig.getOutputName() != null)
				outname = expressionConfig.getOutputName();
			project.getDoseResponseExperiments()
					.add(new ExpressionImportRunner().runExpressionImport(inputFile,
							expressionConfig.getPlatform(), outname,
							expressionConfig.getLogTransformation()));
		}

	}

	// deserialize the json file and return the RunConfig object
	private RunConfig getRunConfig(String configFile) throws Exception
	{
		return new ObjectMapper().readValue(new File(configFile), RunConfig.class);

	}
}
