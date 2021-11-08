package com.sciome.bmdexpress2.commandline;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.service.PrefilterService;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

public class CurveFitPrefilterRunner implements IBMDSToolProgress
{
	public CurveFitPrefilterResults runCurveFitPrefilter(IStatModelProcessable processableData,
			boolean useFoldFilter, double foldFilterValue, double pValueLoel, double foldChangeLoel,
			String outputName, int numThreads, boolean tTest, List<StatModel> modelsToRun, Double bmrFactor,
			int constantVariance, BMDProject project)
	{
		PrefilterService service = new PrefilterService();
		CurveFitPrefilterResults results = service.curveFitPrefilterAnalysis(processableData, useFoldFilter,
				foldFilterValue, pValueLoel, foldChangeLoel, numThreads, this, tTest, modelsToRun, bmrFactor,
				constantVariance);

		if (outputName != null)
			results.setName(outputName);
		else
			project.giveBMDAnalysisUniqueName(results, results.getName());
		return results;

	}

	@Override
	public void updateProgress(String label, double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clearProgress()
	{
		// TODO Auto-generated method stub

	}
}
