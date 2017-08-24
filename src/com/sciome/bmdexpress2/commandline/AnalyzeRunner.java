package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.commandline.config.RunConfig;
import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
import com.sciome.bmdexpress2.commandline.config.category.CategoryConfig;
import com.sciome.bmdexpress2.commandline.config.expression.ExpressionDataConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.ANOVAConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.PrefilterConfig;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;

public class AnalyzeRunner
{

	BMDProject project = new BMDProject();

	public void analyze(String configFile) throws Exception
	{
		System.out.println("analyze");
		System.out.println(configFile);
		RunConfig runConfig = getRunConfig(configFile);

		// load the project if the file exists.
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
				project.getDoseResponseExperiments().add(doExpressionConfig(expressionConfig));

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
		if (preFilterConfig instanceof ANOVAConfig)
		{
			ANOVARunner anovaRunner = new ANOVARunner();
			Double baseValue = 0.0;
			if (preFilterConfig.getLogTransformationOfData().equals(1))
				baseValue = 2.0;
			else if (preFilterConfig.getLogTransformationOfData().equals(2))
				baseValue = 10.0;
			else if (preFilterConfig.getLogTransformationOfData().equals(3))
				baseValue = Math.E;

			IStatModelProcessable processable = null;
			for (DoseResponseExperiment exp : project.getDoseResponseExperiments())
				if (exp.getName().equalsIgnoreCase(preFilterConfig.getInputName()))
					processable = exp;

			for (OneWayANOVAResults oneway : project.getOneWayANOVAResults())
				if (oneway.getName().equalsIgnoreCase(preFilterConfig.getInputName()))
					processable = oneway;

			project.getOneWayANOVAResults()
					.add(anovaRunner.runBMDAnalysis(processable, preFilterConfig.getpValueCutoff(),
							preFilterConfig.getUseMultipleTestingCorrection(),
							preFilterConfig.getFilterOutControlGenes(), preFilterConfig.getUseFoldChange(),
							String.valueOf(preFilterConfig.getFoldChange()),
							!new Integer(0).equals(preFilterConfig.getLogTransformationOfData()), baseValue,
							preFilterConfig.getOutputName()));
		}
		System.out.println("prefilter analysis");

	}

	private DoseResponseExperiment doExpressionConfig(ExpressionDataConfig expressionConfig)
	{
		return (new ExpressionImportRunner()).runExpressionImport(
				new File(expressionConfig.getInputFileName()), expressionConfig.getPlatform(),
				expressionConfig.getOutputName());

	}

	private RunConfig getRunConfig(String configFile) throws Exception
	{
		return new ObjectMapper().readValue(new File(configFile), RunConfig.class);

	}
}
