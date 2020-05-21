package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class NoDataSelectedForProcessingEvent extends BMDExpressEventBase<String>
{

	public NoDataSelectedForProcessingEvent(String payload)
	{
		super(payload);
	}
}
