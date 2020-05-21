package com.sciome.bmdexpress2.serviceInterface;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;

public interface IDataCombinerService
{
	CombinedDataSet combineBMDExpressAnalysisDataSets(List<BMDExpressAnalysisDataSet> dataSets);
}
