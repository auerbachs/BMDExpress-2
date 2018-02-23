package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.service.DataCombinerService;
import com.sciome.bmdexpress2.service.ProjectNavigationService;

public class ExportRunner
{

	BMDProject project = new BMDProject();

	public void analyze(String inputBM2, String outputFile, String analysisGroup, String analysisName)
	{

		if (new File(inputBM2).exists())
		{
			try
			{
				FileInputStream fileIn = new FileInputStream(new File(inputBM2));
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

		System.out.println("export");
		System.out.println(inputBM2 + " " + outputFile + " " + analysisGroup + " " + analysisName);

		ProjectNavigationService service = new ProjectNavigationService();
		DataCombinerService combinerService = new DataCombinerService();

		if (analysisGroup.equals(BMDExpressCommandLine.EXPRESSION))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (DoseResponseExperiment experiment : project.getDoseResponseExperiments())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
			{
				for (DoseResponseExperiment experiment : project.getDoseResponseExperiments())
				{
					if (analysisName.equals(experiment.getName()))
					{
						service.exportDoseResponseExperiment(experiment, new File(outputFile));
					}
				}
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.ONE_WAY_ANOVA))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (OneWayANOVAResults experiment : project.getOneWayANOVAResults())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
			{
				for (OneWayANOVAResults experiment : project.getOneWayANOVAResults())
				{
					service.exportBMDExpressAnalysisDataSet(experiment, new File(outputFile));
				}
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.ORIOGEN))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (OriogenResults experiment : project.getOriogenResults())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
			{
				for (OriogenResults experiment : project.getOriogenResults())
				{
					service.exportBMDExpressAnalysisDataSet(experiment, new File(outputFile));
				}
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.WILLIAMS))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (WilliamsTrendResults experiment : project.getWilliamsTrendResults())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
			{
				for (WilliamsTrendResults experiment : project.getWilliamsTrendResults())
				{
					service.exportBMDExpressAnalysisDataSet(experiment, new File(outputFile));
				}
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.BMD_ANALYSIS))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (BMDResult experiment : project.getbMDResult())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
			{
				for (BMDResult experiment : project.getbMDResult())
				{
					service.exportBMDExpressAnalysisDataSet(experiment, new File(outputFile));
				}
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.CATEGORICAL))
		{
			if (analysisName == null || analysisName.trim().equals(""))
			{
				List<BMDExpressAnalysisDataSet> dataset = new ArrayList<BMDExpressAnalysisDataSet>();
				for (CategoryAnalysisResults experiment : project.getCategoryAnalysisResults())
				{
					dataset.add(experiment);
				}
				CombinedDataSet combinedDataSet = combinerService.combineBMDExpressAnalysisDataSets(dataset);
				service.exportBMDExpressAnalysisDataSet(combinedDataSet, new File(outputFile));
			}
			else
				for (CategoryAnalysisResults experiment : project.getCategoryAnalysisResults())
				{
					service.exportBMDExpressAnalysisDataSet(experiment, new File(outputFile));
				}
		}

	}

	public void exportToJson(BMDProject project, String jsonExportFileName) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();

		/**
		 * To make the JSON String pretty use the below code
		 */
		File testFile = new File(jsonExportFileName);
		mapper.writerWithDefaultPrettyPrinter().writeValue(testFile, project);

	}
}
