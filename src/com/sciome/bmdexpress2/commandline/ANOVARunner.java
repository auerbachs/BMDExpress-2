package com.sciome.bmdexpress2.commandline;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.service.PrefilterService;

/*
 * use the presenter to run the one way anova as it would be run from the view.
 */
public class ANOVARunner
{
	public OneWayANOVAResults runANOVAFilter(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String pValueLoel, String foldChangeLoel, String outputName,
			boolean tTest, BMDProject project)
	{
		PrefilterService service = new PrefilterService();
		OneWayANOVAResults results = service.oneWayANOVAAnalysis(processableData, pCutOff,
				multipleTestingCorrection, filterOutControlGenes, useFoldFilter, foldFilterValue,
				pValueLoel, foldChangeLoel, tTest);

		if (outputName != null)
			results.setName(outputName);
		else
			project.giveBMDAnalysisUniqueName(results, results.getName());
		return results;

	}
}
