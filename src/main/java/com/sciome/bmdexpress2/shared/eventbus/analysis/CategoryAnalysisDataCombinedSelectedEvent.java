package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CategoryAnalysisDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{

	public CategoryAnalysisDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
