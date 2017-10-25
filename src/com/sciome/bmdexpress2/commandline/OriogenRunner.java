package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.util.prefilter.OriogenAnalysis;

public class OriogenRunner {
	
	public OriogenResults runOriogenFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean mpc, int initialBootstraps, 
			int maxBootstraps, float s0Adjustment, boolean filterOutControlGenes, 
			boolean useFoldFilter, String foldFilterValue, String outputName)
	{
		OriogenAnalysis analysis = new OriogenAnalysis();
		OriogenResults results = analysis.analyzeDoseResponseData(processableData, pCutOff, multipleTestingCorrection,
				initialBootstraps, maxBootstraps, s0Adjustment, filterOutControlGenes, useFoldFilter, foldFilterValue, null);

		if (outputName != null)
			results.setName(outputName);
		return results;

	}
}
