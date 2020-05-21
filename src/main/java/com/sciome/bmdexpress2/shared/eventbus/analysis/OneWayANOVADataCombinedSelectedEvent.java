package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OneWayANOVADataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{

	public OneWayANOVADataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
