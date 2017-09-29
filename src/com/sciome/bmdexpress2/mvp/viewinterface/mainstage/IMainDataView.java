package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;

public interface IMainDataView
{
	/*
	 * Sets the list of projects
	 */
	void loadDoseResponseExperiment(DoseResponseExperiment doseResponseExperiement);

	void loadOneWayANOVAAnalysis(OneWayANOVAResults getPayload);
	
	void loadWilliamsTrendAnalysis(WilliamsTrendResults getPayload);

	void loadBMDResultAnalysis(BMDResult getPayload);

	void loadCategoryAnalysis(CategoryAnalysisResults getPayload);

	void showBMDExpressAnalysisInSeparateWindow(BMDExpressAnalysisDataSet getPayload);

	void showExpressDataInSeparateWindow(DoseResponseExperiment experiment);

	void clearTableView();

}
