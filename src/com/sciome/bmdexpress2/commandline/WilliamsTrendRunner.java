package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.service.PrefilterService;

public class WilliamsTrendRunner
{
	public WilliamsTrendResults runWilliamsTrendFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numPermutations, String outputName, BMDProject project)
	{
		PrefilterService service = new PrefilterService();
		WilliamsTrendResults results = service.williamsTrendAnalysis(processableData, pCutOff,
				multipleTestingCorrection, filterOutControlGenes, useFoldFilter, foldFilterValue,
				numPermutations, null);

		if (outputName != null)
			results.setName(outputName);
		else
			project.giveBMDAnalysisUniqueName(results, results.getName());
		return results;

	}
}
