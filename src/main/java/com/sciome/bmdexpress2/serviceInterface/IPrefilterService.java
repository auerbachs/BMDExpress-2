package com.sciome.bmdexpress2.serviceInterface;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

public interface IPrefilterService
{
	public WilliamsTrendResults williamsTrendAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			double foldFilterValue, int numberOfPermutations, double loelPValue, double loelFoldChange,
			int numThreads, SimpleProgressUpdater updater, boolean tTest);

	public OriogenResults oriogenAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, int maxBootstraps, double s0Adjustment,
			boolean filterOutControlGenes, boolean useFoldFilter, double foldFilterValue, double loelPValue,
			double loelFoldChange, int numThreads, SimpleProgressUpdater updater, boolean tTest);

	public OneWayANOVAResults oneWayANOVAAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			double foldFilterValue, double loelPValue, double loelFoldChange, int numThreads,
			SimpleProgressUpdater updater, boolean tTest);

	public CurveFitPrefilterResults curveFitPrefilterAnalysis(IStatModelProcessable processableData,
			boolean useFoldFilter, double foldFilterValue, double loelPValue, double loelFoldChange,
			int numThreads, IBMDSToolProgress updater, boolean tTest);

	public void cancel();

	public void start();
}
