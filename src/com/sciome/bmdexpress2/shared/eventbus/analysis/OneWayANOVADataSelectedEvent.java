package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OneWayANOVADataSelectedEvent extends BMDExpressEventBase<OneWayANOVAResults>
{

	public OneWayANOVADataSelectedEvent(OneWayANOVAResults payload)
	{
		super(payload);
	}
}
