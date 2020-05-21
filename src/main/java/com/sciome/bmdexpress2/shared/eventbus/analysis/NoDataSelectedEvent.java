package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class NoDataSelectedEvent extends BMDExpressEventBase<String>
{

	public NoDataSelectedEvent(String payload)
	{
		super(payload);
	}
}
