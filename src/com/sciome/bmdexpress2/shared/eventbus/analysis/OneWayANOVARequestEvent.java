package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OneWayANOVARequestEvent extends BMDExpressEventBase<String>
{

	public OneWayANOVARequestEvent(String payload)
	{
		super(payload);
	}
}
