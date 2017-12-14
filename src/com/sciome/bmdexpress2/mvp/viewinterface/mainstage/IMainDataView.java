package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;

public interface IMainDataView
{
	/*
	 * Sets the list of projects
	 */
	void loadDoseResponseExperiment(BMDExpressAnalysisDataSet doseResponseExperiement);

	void loadOneWayANOVAAnalysis(BMDExpressAnalysisDataSet getPayload);

	void loadWilliamsTrendAnalysis(BMDExpressAnalysisDataSet getPayload);

	void loadOriogenAnalysis(BMDExpressAnalysisDataSet getPayload);

	void loadBMDResultAnalysis(BMDExpressAnalysisDataSet getPayload);

	void loadCategoryAnalysis(BMDExpressAnalysisDataSet getPayload);

	void showBMDExpressAnalysisInSeparateWindow(BMDExpressAnalysisDataSet getPayload);

	void showExpressDataInSeparateWindow(DoseResponseExperiment experiment);

	void clearTableView();

}
