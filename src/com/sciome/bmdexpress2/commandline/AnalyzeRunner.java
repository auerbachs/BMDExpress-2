package com.sciome.bmdexpress2.commandline;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.commandline.config.RunConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
import com.sciome.bmdexpress2.commandline.config.category.CategoryConfig;
import com.sciome.bmdexpress2.commandline.config.expression.ExpressionDataConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.PrefilterConfig;

public class AnalyzeRunner
{

	public void analyze(String configFile) throws Exception
	{
		System.out.println("analyze");
		System.out.println(configFile);
		RunConfig runConfig = getRunConfig(configFile);

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

		// 4: get all the category analysis configs
		List<CategoryConfig> catConfigs = runConfig.getCategoryAnalysisConfigs();
		if (catConfigs != null)
			for (CategoryConfig catConfig : catConfigs)
				doCatAnalysis(catConfig);
	}

	private void doCatAnalysis(CategoryConfig catConfig)
	{
		System.out.println("category analysis");

	}

	private void doBMDSAnalysis(BMDSConfig bmdsConfig)
	{
		System.out.println("bmds analysis");

	}

	private void doPrefilter(PrefilterConfig preFilterConfig)
	{
		System.out.println("prefilter analysis");

	}

	private void doExpressionConfig(ExpressionDataConfig expressionConfig)
	{
		System.out.println("expression data analysis");

	}

	private RunConfig getRunConfig(String configFile) throws Exception
	{
		return new ObjectMapper().readValue(new File(configFile), RunConfig.class);

	}
}
