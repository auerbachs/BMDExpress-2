package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowBMDExpressDataAnalysisInSeparateWindow extends BMDExpressEventBase<BMDExpressAnalysisDataSet>
{

	public ShowBMDExpressDataAnalysisInSeparateWindow(BMDExpressAnalysisDataSet payload)
	{
		super(payload);
	}
}
