package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{

	public BMDAnalysisDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
