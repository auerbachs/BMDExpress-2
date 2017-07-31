package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CloseApplicationRequestEvent extends BMDExpressEventBase<String>
{

	public CloseApplicationRequestEvent(String payload)
	{
		super(payload);
	}
}
