package com.sciome.bmdexpress2.commandline;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;

public class QueryRunner
{
	BMDProject project = new BMDProject();

	public void analyze(String inputBM2, String analysisGroup)
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

		if (analysisGroup.equals(BMDExpressCommandLine.EXPRESSION))
		{
			for (DoseResponseExperiment experiment : project.getDoseResponseExperiments())
			{
				System.out.println(experiment.getName());
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.ONE_WAY_ANOVA))
		{
			for (OneWayANOVAResults experiment : project.getOneWayANOVAResults())
			{
				System.out.println(experiment.getName());
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.ORIOGEN))
		{
			for (OriogenResults experiment : project.getOriogenResults())
			{
				System.out.println(experiment.getName());
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.WILLIAMS))
		{
			for (WilliamsTrendResults experiment : project.getWilliamsTrendResults())
			{
				System.out.println(experiment.getName());
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.BMD_ANALYSIS))
		{
			for (BMDResult experiment : project.getbMDResult())
			{
				System.out.println(experiment.getName());
			}
		}
		else if (analysisGroup.equals(BMDExpressCommandLine.CATEGORICAL))
		{
			for (CategoryAnalysisResults experiment : project.getCategoryAnalysisResults())
			{
				System.out.println(experiment.getName());
			}
		}

	}
}
