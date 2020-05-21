package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class TryCloseProjectRequestEvent extends BMDExpressEventBase<String>
{

	public TryCloseProjectRequestEvent(String payload)
	{
		super(payload);
	}
}
