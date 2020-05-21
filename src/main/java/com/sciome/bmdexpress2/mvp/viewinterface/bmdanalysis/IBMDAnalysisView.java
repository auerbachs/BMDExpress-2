package com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;

public interface IBMDAnalysisView
{

	public void clearProgressBar();

	public void updateProgressBar(String label, double value);

	public void initializeProgressBar(String label);

	public void finishedBMDAnalysis();

	public void startedBMDAnalysis();

	public void closeWindow();

	void initData(List<IStatModelProcessable> processableData, boolean selectModelsOnly);

}
