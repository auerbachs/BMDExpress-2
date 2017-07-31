package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowBMDAnalysisDataSetVisualizationsEvent
		extends BMDExpressEventBase<List<BMDExpressAnalysisDataSet>>
{

	public ShowBMDAnalysisDataSetVisualizationsEvent(List<BMDExpressAnalysisDataSet> payload)
	{
		super(payload);
	}
}
