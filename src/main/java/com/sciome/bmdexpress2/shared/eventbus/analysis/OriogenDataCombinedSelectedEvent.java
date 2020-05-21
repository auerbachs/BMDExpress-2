package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OriogenDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{
	public OriogenDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
