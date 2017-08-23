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
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
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
		expression1.setHasHeaders(0);
		expression1.setInputFileName("/home/japhill/analysis/expressiondata/expression1.txt");
		expression1.setOutputName("expression1");
		expression1.setPlatform("GPL1255");

		ExpressionDataConfig expression2 = new ExpressionDataConfig();
		expression2.setHasHeaders(0);
		expression2.setInputFileName("/home/japhill/analysis/expressiondata/expression2.txt");
		expression2.setOutputName("expression2");
		expression2.setPlatform("GPL1255");

		runConfig.setExpressionDataConfigs(Arrays.asList(expression1, expression2));

		// load prefilters
		ANOVAConfig anovaConfig = new ANOVAConfig();

		anovaConfig.setFilterOutControlGenes(1);
		anovaConfig.setFoldChange(2.0);
		anovaConfig.setInputName("expression1");
		anovaConfig.setOutputName("expression1_anova");
		anovaConfig.setLogTransformationOfData(1);
		anovaConfig.setpValueCutoff(0.05);
		anovaConfig.setUseFoldChange(1);

		ANOVAConfig anovaConfig1 = new ANOVAConfig();

		anovaConfig1.setFilterOutControlGenes(1);
		anovaConfig1.setFoldChange(2.0);
		anovaConfig1.setInputName("expression2");
		anovaConfig1.setOutputName("expression2_anova");
		anovaConfig1.setLogTransformationOfData(1);
		anovaConfig1.setpValueCutoff(0.05);
		anovaConfig1.setUseFoldChange(1);

		runConfig.setPreFilterConfigs(Arrays.asList(anovaConfig, anovaConfig1));

		// load bmd config

		BMDSConfig bmdsConfig = new BMDSConfig();
		bmdsConfig.setBestModelSelectionWithFlaggedHill(1);
		bmdsConfig.setBestPolyTest(1);
		bmdsConfig.setFlagHillWithKParameter(1);
		bmdsConfig.setInputCategory("anova");
		bmdsConfig.setInputName("expression1_anova");
		bmdsConfig.setkParameterValue(1);
		bmdsConfig.setModifyFlaggedHillWithFractionMinBMD(0.05);
		bmdsConfig.setNumberOfThreads(100);
		bmdsConfig.setOutputName("expression1_anova_bmds");
		bmdsConfig.setpValueCutoff(0.05);

		bmdsConfig.setModelConfigs(getModelConfigs());

		BMDSConfig bmdsConfig1 = new BMDSConfig();
		bmdsConfig1.setBestModelSelectionWithFlaggedHill(1);
		bmdsConfig1.setBestPolyTest(1);
		bmdsConfig1.setFlagHillWithKParameter(1);
		bmdsConfig1.setInputCategory("anova");
		bmdsConfig1.setInputName("expression2_anova");
		bmdsConfig1.setkParameterValue(1);
		bmdsConfig1.setModifyFlaggedHillWithFractionMinBMD(0.05);
		bmdsConfig1.setNumberOfThreads(100);
		bmdsConfig1.setOutputName("expression2_anova_bmds");
		bmdsConfig1.setpValueCutoff(0.05);

		bmdsConfig1.setModelConfigs(getModelConfigs());

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

		go.setRemovePromiscuousProbes(1);
		go.setRemoveBMDGreaterHighDose(1);
		go.setnFoldBelowLowestDose(10.0);
		go.setBmdPValueCutoff(0.1);
		go.setBmduBMDLRatioMin(20.0);
		go.setInputName(input);
		go.setOutputName(input + "_GOuniversal");
		go.setGoCategory("universal");

		PathwayConfig path = new PathwayConfig();

		path.setRemovePromiscuousProbes(1);
		path.setRemoveBMDGreaterHighDose(1);
		path.setnFoldBelowLowestDose(10.0);
		path.setBmdPValueCutoff(0.1);
		path.setBmduBMDLRatioMin(20.0);
		path.setSignalingPathway("REACTOME");
		path.setInputName(input);
		path.setOutputName(input + "_REACTOME");

		DefinedConfig defined = new DefinedConfig();

		defined.setRemovePromiscuousProbes(1);
		defined.setRemoveBMDGreaterHighDose(1);
		defined.setnFoldBelowLowestDose(10.0);
		defined.setBmdPValueCutoff(0.1);
		defined.setBmduBMDLRatioMin(20.0);
		defined.setProbeFilePath("/home/japhill/analysis/defined/probes.txt");
		defined.setCategoryFilePath("/home/japhill/analysis/defined/categories.txt");
		defined.setInputName(input);
		defined.setOutputName(input + "_DEFINED");

		return Arrays.asList(go, path, defined);
	}

	private List<BMDSModelConfig> getModelConfigs()
	{
		HillConfig hill = new HillConfig();
		hill.setBmrFactor(1.349);
		hill.setConfidenceLevel(0.95);
		hill.setConstantVariance(1);
		hill.setMaxIterations(250);

		PowerConfig power = new PowerConfig();
		power.setBmrFactor(1.349);
		power.setConfidenceLevel(0.95);
		power.setConstantVariance(1);
		power.setMaxIterations(250);

		PolyConfig poly1 = new PolyConfig();
		poly1.setDegree(1);
		poly1.setBmrFactor(1.349);
		poly1.setConfidenceLevel(0.95);
		poly1.setConstantVariance(1);
		poly1.setMaxIterations(250);

		PolyConfig poly2 = new PolyConfig();
		poly2.setDegree(2);
		poly2.setBmrFactor(1.349);
		poly2.setConfidenceLevel(0.95);
		poly2.setConstantVariance(1);
		poly2.setMaxIterations(250);

		PolyConfig poly3 = new PolyConfig();
		poly3.setDegree(3);
		poly3.setBmrFactor(1.349);
		poly3.setConfidenceLevel(0.95);
		poly3.setConstantVariance(1);
		poly3.setMaxIterations(250);

		ExponentialConfig exp2 = new ExponentialConfig();
		exp2.setExpModel(2);
		exp2.setBmrFactor(1.349);
		exp2.setConfidenceLevel(0.95);
		exp2.setConstantVariance(1);
		exp2.setMaxIterations(250);

		ExponentialConfig exp3 = new ExponentialConfig();
		exp3.setExpModel(3);
		exp3.setBmrFactor(1.349);
		exp3.setConfidenceLevel(0.95);
		exp3.setConstantVariance(1);
		exp3.setMaxIterations(250);
		ExponentialConfig exp4 = new ExponentialConfig();
		exp4.setExpModel(4);
		exp4.setBmrFactor(1.349);
		exp4.setConfidenceLevel(0.95);
		exp4.setConstantVariance(1);
		exp4.setMaxIterations(250);
		ExponentialConfig exp5 = new ExponentialConfig();
		exp5.setExpModel(5);
		exp5.setBmrFactor(1.349);
		exp5.setConfidenceLevel(0.95);
		exp5.setConstantVariance(1);
		exp5.setMaxIterations(250);

		return Arrays.asList(hill, power, poly1, poly2, poly3, exp2, exp3, exp4, exp5);

	}

}
