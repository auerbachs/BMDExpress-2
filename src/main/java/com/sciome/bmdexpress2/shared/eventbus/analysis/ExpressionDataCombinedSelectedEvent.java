package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ExpressionDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{

	public ExpressionDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
