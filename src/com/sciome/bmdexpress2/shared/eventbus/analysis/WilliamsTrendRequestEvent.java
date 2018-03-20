package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class WilliamsTrendRequestEvent extends BMDExpressEventBase<String>
{
	public WilliamsTrendRequestEvent(String payload)
	{
		super(payload);
	}
}
