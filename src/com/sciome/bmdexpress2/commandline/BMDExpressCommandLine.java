package com.sciome.bmdexpress2.commandline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.commandline.config.RunConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSBestModelSelectionConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSInputConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSModelConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.ExponentialConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.HillConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.PolyConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.PowerConfig;
import com.sciome.bmdexpress2.commandline.config.category.CategoryConfig;
import com.sciome.bmdexpress2.commandline.config.category.DefinedConfig;
import com.sciome.bmdexpress2.commandline.config.category.GOConfig;
import com.sciome.bmdexpress2.commandline.config.category.PathwayConfig;
import com.sciome.bmdexpress2.commandline.config.expression.ExpressionDataConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.ANOVAConfig;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;

public class BMDExpressCommandLine
{

	public final static String	CONFIG_FILE			= "config-file";
	public final static String	INPUT_BM2			= "input-bm2";
	public final static String	OUTPUT_FORMAT		= "output-format";
	public final static String	ANALYSIS_GROUP		= "analysis-group";
	public final static String	ANALYSIS_NAME		= "analysis-name";
	public final static String	OUTPUT_FILE_NAME	= "output-file-name";

	public final static String	ANALYZE				= "analyze";
	public final static String	QUERY				= "query";
	public final static String	EXPORT				= "export";
	public final static String	DELETE				= "delete";

	public static void main(String[] args)
	{
		BMDExpressProperties.getInstance().setIsConsole(true);
		CommandLineParser parser = new DefaultParser();

		Options analyzeOptions = new Options();

		Options exportOptions = new Options();

		Options deleteOptions = new Options();

		Options queryOptions = new Options();

		final OptionGroup analyzeGroup = new OptionGroup();
		analyzeOptions
				.addOption(Option.builder().longOpt(CONFIG_FILE).hasArg().argName("JSON").required().build());

		exportOptions.addOption(Option.builder().longOpt(OUTPUT_FORMAT).hasArg().argName("FORMAT").build());
		exportOptions.addOption(
				Option.builder().longOpt(INPUT_BM2).hasArg().required().argName("BM2FILE").build());
		exportOptions.addOption(Option.builder().longOpt(ANALYSIS_GROUP).hasArg().argName("GROUP").build());
		exportOptions.addOption(Option.builder().longOpt(ANALYSIS_NAME).hasArg().argName("NAME").build());
		exportOptions
				.addOption(Option.builder().longOpt(OUTPUT_FILE_NAME).hasArg().argName("OUTPUT").build());

		deleteOptions.addOption(
				Option.builder().longOpt(INPUT_BM2).hasArg().required().argName("BM2FILE").build());
		deleteOptions.addOption(
				Option.builder().longOpt(ANALYSIS_GROUP).hasArg().required().argName("GROUP").build());
		deleteOptions.addOption(
				Option.builder().longOpt(ANALYSIS_NAME).hasArg().required().argName("NAME").build());

		queryOptions.addOption(
				Option.builder().longOpt(INPUT_BM2).hasArg().required().argName("BM2FILE").build());
		queryOptions.addOption(Option.builder().longOpt(ANALYSIS_GROUP).hasArg().argName("GROUP").build());
		queryOptions.addOption(Option.builder().longOpt(ANALYSIS_NAME).hasArg().argName("NAME").build());
		HelpFormatter formatter = new HelpFormatter();

		formatter.setWidth(160);
		formatter.printHelp("bmdexpress2 " + ANALYZE, "", analyzeOptions, "", true);

		formatter.printHelp("bmdexpress2 " + EXPORT, "", exportOptions, "", true);

		formatter.printHelp("bmdexpress2 " + DELETE, "", deleteOptions, "", true);
		formatter.printHelp("bmdexpress2 " + QUERY, "", queryOptions, "", true);

		try
		{

			String[] theArgs = Arrays.copyOfRange(args, 1, args.length);
			if (args[0].equals(ANALYZE))
			{
				CommandLine cmd = parser.parse(analyzeOptions, theArgs);
				AnalyzeRunner aRounder = new AnalyzeRunner();
				aRounder.analyze(cmd.getOptionValue(CONFIG_FILE));
			}
			else if (args[0].equals(EXPORT))
			{
				CommandLine cmd = parser.parse(exportOptions, theArgs);
				ExportRunner eRunner = new ExportRunner();
				eRunner.analyze(cmd.getOptionValue(INPUT_BM2), cmd.getOptionValue(OUTPUT_FILE_NAME),
						cmd.getOptionValue(OUTPUT_FORMAT), cmd.getOptionValue(ANALYSIS_GROUP),
						cmd.getOptionValue(ANALYSIS_NAME));
			}
			else if (args[0].equals(DELETE))
			{
				CommandLine cmd = parser.parse(deleteOptions, theArgs);
				DeleteRunner dRunner = new DeleteRunner();
				dRunner.analyze(cmd.getOptionValue(INPUT_BM2), cmd.getOptionValue(ANALYSIS_GROUP),
						cmd.getOptionValue(ANALYSIS_NAME));
			}
			else if (args[0].equals(QUERY))
			{
				CommandLine cmd = parser.parse(queryOptions, theArgs);
				QueryRunner qRunner = new QueryRunner();
				qRunner.analyze(cmd.getOptionValue(INPUT_BM2), cmd.getOptionValue(ANALYSIS_GROUP),
						cmd.getOptionValue(ANALYSIS_NAME));
			}

		}
		catch (Exception exp)
		{
			System.out.println("Unexpected exception:" + exp.getMessage());
			exp.printStackTrace();
		}
		new BMDExpressCommandLine().createStrawMan();

	}

	public void createStrawMan()
	{
		RunConfig runConfig = new RunConfig();

		runConfig.setAppendToNames(".sept2017");
		runConfig.setPreprendToNames("testrun_");
		runConfig.setBm2FileName("/home/japhill/analysis/myfile.bm2");
		runConfig.setJsonExportFileName("/home/japhill/analysis/myfile.json");

		// load doseresponse data
		ExpressionDataConfig expression1 = new ExpressionDataConfig();
		expression1.setHasHeaders(false);
		expression1.setInputFileName("/home/japhill/analysis/expressiondata/expression1.txt");
		expression1.setOutputName("expression1");
		expression1.setPlatform("GPL1255");

		ExpressionDataConfig expression2 = new ExpressionDataConfig();
		expression2.setHasHeaders(false);
		expression2.setInputFileName("/home/japhill/analysis/expressiondata/expression2.txt");
		expression2.setOutputName("expression2");
		expression2.setPlatform("GPL1255");

		runConfig.setExpressionDataConfigs(Arrays.asList(expression1, expression2));

		// load prefilters
		ANOVAConfig anovaConfig = new ANOVAConfig();

		anovaConfig.setFilterOutControlGenes(true);
		anovaConfig.setFoldChange(2.0);
		anovaConfig.setInputName("expression1");
		anovaConfig.setOutputName("expression1_anova");
		anovaConfig.setLogTransformationOfData(1);
		anovaConfig.setpValueCutoff(0.05);
		anovaConfig.setUseFoldChange(true);

		ANOVAConfig anovaConfig1 = new ANOVAConfig();

		anovaConfig1.setFilterOutControlGenes(true);
		anovaConfig1.setFoldChange(2.0);
		anovaConfig1.setInputName("expression2");
		anovaConfig1.setOutputName("expression2_anova");
		anovaConfig1.setLogTransformationOfData(1);
		anovaConfig1.setpValueCutoff(0.05);
		anovaConfig1.setUseFoldChange(true);

		runConfig.setPreFilterConfigs(Arrays.asList(anovaConfig, anovaConfig1));

		// load bmd config

		BMDSConfig bmdsConfig = new BMDSConfig();
		bmdsConfig.setInputCategory("anova");
		bmdsConfig.setInputName("expression1_anova");
		bmdsConfig.setOutputName("expression1_anova_bmds");
		bmdsConfig.setNumberOfThreads(100);
		BMDSBestModelSelectionConfig bestModConfig = new BMDSBestModelSelectionConfig();
		bestModConfig.setBestModelSelectionWithFlaggedHill(1);
		bestModConfig.setBestPolyTest(1);
		bestModConfig.setFlagHillWithKParameter(true);

		bestModConfig.setkParameterValue(1);
		bestModConfig.setModifyFlaggedHillWithFractionMinBMD(0.05);

		bestModConfig.setpValueCutoff(0.05);
		bmdsConfig.setBmdsInputConfig(getBMDSInputConfig());
		bmdsConfig.setBmdsBestModelSelection(bestModConfig);

		bmdsConfig.setModelConfigs(getModelConfigs());

		BMDSConfig bmdsConfig1 = new BMDSConfig();
		bmdsConfig1.setInputCategory("anova");
		bmdsConfig.setNumberOfThreads(100);
		bmdsConfig1.setInputName("expression1_anova");
		bmdsConfig1.setOutputName("expression1_anova_bmds");
		BMDSBestModelSelectionConfig bestModConfig1 = new BMDSBestModelSelectionConfig();
		bestModConfig1.setBestModelSelectionWithFlaggedHill(1);
		bestModConfig1.setBestPolyTest(1);
		bestModConfig1.setFlagHillWithKParameter(true);

		bestModConfig1.setkParameterValue(1);
		bestModConfig1.setModifyFlaggedHillWithFractionMinBMD(0.05);

		bestModConfig1.setpValueCutoff(0.05);
		bmdsConfig1.setBmdsInputConfig(getBMDSInputConfig());
		bmdsConfig1.setModelConfigs(getModelConfigs());
		bmdsConfig1.setBmdsBestModelSelection(bestModConfig1);
		runConfig.setBmdsConfigs(Arrays.asList(bmdsConfig, bmdsConfig1));

		List<CategoryConfig> configs = new ArrayList<>();
		configs.addAll(getCategoryConfigs("express1_anova_bmds"));
		configs.addAll(getCategoryConfigs("express2_anova_bmds"));
		runConfig.setCategoryAnalysisConfigs(configs);

		ObjectMapper mapper = new ObjectMapper();

		/**
		 * To make the JSON String pretty use the below code
		 */
		File testFile = new File("/tmp/runConfig.json");
		try
		{
			mapper.writerWithDefaultPrettyPrinter().writeValue(testFile, runConfig);
		}
		catch (JsonGenerationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private List<CategoryConfig> getCategoryConfigs(String input)
	{
		GOConfig go = new GOConfig();

		go.setRemovePromiscuousProbes(true);
		go.setRemoveBMDGreaterHighDose(true);
		go.setnFoldBelowLowestDose(10.0);
		go.setBmdPValueCutoff(0.1);
		go.setBmduBMDLRatioMin(20.0);
		go.setInputName(input);
		go.setOutputName(input + "_GOuniversal");
		go.setGoCategory("universal");

		PathwayConfig path = new PathwayConfig();

		path.setRemovePromiscuousProbes(true);
		path.setRemoveBMDGreaterHighDose(true);
		path.setnFoldBelowLowestDose(10.0);
		path.setBmdPValueCutoff(0.1);
		path.setBmduBMDLRatioMin(20.0);
		path.setSignalingPathway("REACTOME");
		path.setInputName(input);
		path.setOutputName(input + "_REACTOME");

		DefinedConfig defined = new DefinedConfig();

		defined.setRemovePromiscuousProbes(true);
		defined.setRemoveBMDGreaterHighDose(true);
		defined.setnFoldBelowLowestDose(10.0);
		defined.setBmdPValueCutoff(0.1);
		defined.setBmduBMDLRatioMin(20.0);
		defined.setProbeFilePath("/home/japhill/analysis/defined/probes.txt");
		defined.setCategoryFilePath("/home/japhill/analysis/defined/categories.txt");
		defined.setInputName(input);
		defined.setOutputName(input + "_DEFINED");

		return Arrays.asList(go, path, defined);
	}

	private BMDSInputConfig getBMDSInputConfig()
	{
		BMDSInputConfig conf = new BMDSInputConfig();

		conf.setBmrFactor(1.349);
		conf.setConfidenceLevel(0.95);
		conf.setConstantVariance(true);
		conf.setMaxIterations(250);
		conf.setRestrictPower(true);
		return conf;
	}

	private List<BMDSModelConfig> getModelConfigs()
	{
		HillConfig hill = new HillConfig();

		PowerConfig power = new PowerConfig();

		PolyConfig poly1 = new PolyConfig();
		poly1.setDegree(1);

		PolyConfig poly2 = new PolyConfig();
		poly2.setDegree(2);

		PolyConfig poly3 = new PolyConfig();
		poly3.setDegree(3);

		ExponentialConfig exp2 = new ExponentialConfig();
		exp2.setExpModel(2);

		ExponentialConfig exp3 = new ExponentialConfig();
		exp3.setExpModel(3);

		ExponentialConfig exp4 = new ExponentialConfig();
		exp4.setExpModel(4);

		ExponentialConfig exp5 = new ExponentialConfig();
		exp5.setExpModel(5);

		return Arrays.asList(hill, power, poly1, poly2, poly3, exp2, exp3, exp4, exp5);

	}

}
