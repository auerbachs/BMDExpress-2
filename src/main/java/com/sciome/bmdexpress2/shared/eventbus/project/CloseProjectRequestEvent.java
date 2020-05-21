package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CloseProjectRequestEvent extends BMDExpressEventBase<String>
{

	public CloseProjectRequestEvent(String payload)
	{
		super(payload);
	}
}
