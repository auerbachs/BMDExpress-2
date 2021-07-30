package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.service.PrefilterService;

public class CurveFitPrefilterRunner
{
	public CurveFitPrefilterResults runCurveFitPrefilter(IStatModelProcessable processableData,
			boolean useFoldFilter, double foldFilterValue, double pValueLoel, double foldChangeLoel,
			String outputName, int numThreads, boolean tTest, BMDProject project)
	{
		PrefilterService service = new PrefilterService();
		CurveFitPrefilterResults results = service.curveFitPrefilterAnalysis(processableData, useFoldFilter,
				foldFilterValue, pValueLoel, foldChangeLoel, numThreads, null, tTest);

		if (outputName != null)
			results.setName(outputName);
		else
			project.giveBMDAnalysisUniqueName(results, results.getName());
		return results;

	}
}
