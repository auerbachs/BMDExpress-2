package com.sciome.bmdexpress2.mvp.viewinterface.visualization;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;

public interface IDataVisualizationView
{

	public void closeWindow();

	public void drawResults(List<BMDExpressAnalysisDataSet> getPayload);

}
