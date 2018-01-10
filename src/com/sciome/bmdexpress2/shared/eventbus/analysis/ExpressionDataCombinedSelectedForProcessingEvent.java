package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ExpressionDataCombinedSelectedForProcessingEvent extends BMDExpressEventBase<CombinedDataSet>
{

	public ExpressionDataCombinedSelectedForProcessingEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
