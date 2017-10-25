package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.util.prefilter.WilliamsTrendAnalysis;

public class WilliamsTrendRunner {
	public WilliamsTrendResults runWilliamsTrendFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numPermutations, String outputName)
	{
		WilliamsTrendAnalysis analysis = new WilliamsTrendAnalysis();
		WilliamsTrendResults results = analysis.analyzeDoseResponseData(processableData, pCutOff,
				multipleTestingCorrection, filterOutControlGenes, useFoldFilter, foldFilterValue, numPermutations, null);

		if (outputName != null)
			results.setName(outputName);
		return results;

	}
}
