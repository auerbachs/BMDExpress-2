package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OneWayANOVADataSelectedForProcessingEvent extends BMDExpressEventBase<OneWayANOVAResults>
{

	public OneWayANOVADataSelectedForProcessingEvent(OneWayANOVAResults payload)
	{
		super(payload);
	}
}
