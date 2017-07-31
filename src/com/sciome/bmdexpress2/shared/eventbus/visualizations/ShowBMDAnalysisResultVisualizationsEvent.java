package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowBMDAnalysisResultVisualizationsEvent
		extends BMDExpressEventBase<List<BMDExpressAnalysisDataSet>>
{

	public ShowBMDAnalysisResultVisualizationsEvent(List<BMDExpressAnalysisDataSet> payload)
	{
		super(payload);
	}
}
