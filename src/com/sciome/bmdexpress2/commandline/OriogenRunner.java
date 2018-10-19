package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.service.PrefilterService;

public class OriogenRunner
{

	public OriogenResults runOriogenFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean mpc, int initialBootstraps, int maxBootstraps,
			float s0Adjustment, boolean filterOutControlGenes, boolean useFoldFilter, String foldFilterValue,
			String pValueLoel, String foldChangeLoel, String outputName, boolean tTest, BMDProject project)
	{
		PrefilterService service = new PrefilterService();
		OriogenResults results = service.oriogenAnalysis(processableData, pCutOff, multipleTestingCorrection,
				initialBootstraps, maxBootstraps, s0Adjustment, filterOutControlGenes, useFoldFilter,
				foldFilterValue, pValueLoel, foldChangeLoel, null, tTest);

		if (outputName != null)
			results.setName(outputName);
		else
			project.giveBMDAnalysisUniqueName(results, results.getName());
		return results;

	}
}
