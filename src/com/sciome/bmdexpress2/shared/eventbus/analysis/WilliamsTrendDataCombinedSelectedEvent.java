package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class WilliamsTrendDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{
	public WilliamsTrendDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
