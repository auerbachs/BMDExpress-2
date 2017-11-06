package com.sciome.bmdexpress2.serviceInterface;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

public interface IPrefilterService {
	public WilliamsTrendResults williamsTrendAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, SimpleProgressUpdater updater);
	
	public OriogenResults oriogenAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, int maxBootstraps, 
			float s0Adjustment, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, SimpleProgressUpdater updater);
	
	public OneWayANOVAResults oneWayANOVAAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue);
	
	public void cancel();
}
