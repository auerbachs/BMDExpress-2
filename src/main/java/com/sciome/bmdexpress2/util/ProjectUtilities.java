package com.sciome.bmdexpress2.util;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;

public class ProjectUtilities
{

	/*
	 * add project to an existing project
	 */
	public static void addProjectToProject(BMDProject project, BMDProject newProject)
	{
		// add files to the current project
		for (DoseResponseExperiment data : newProject.getDoseResponseExperiments())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getDoseResponseExperiments().add(data);
		}
		for (WilliamsTrendResults data : newProject.getWilliamsTrendResults())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getWilliamsTrendResults().add(data);
		}
		for (CurveFitPrefilterResults data : newProject.getCurveFitPrefilterResults())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getCurveFitPrefilterResults().add(data);
		}
		for (OneWayANOVAResults data : newProject.getOneWayANOVAResults())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getOneWayANOVAResults().add(data);
		}
		for (OriogenResults data : newProject.getOriogenResults())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getOriogenResults().add(data);
		}

		for (BMDResult data : newProject.getbMDResult())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getbMDResult().add(data);
		}
		for (CategoryAnalysisResults data : newProject.getCategoryAnalysisResults())
		{
			project.giveBMDAnalysisUniqueName(data, data.getName());
			project.getCategoryAnalysisResults().add(data);
		}
	}

}
