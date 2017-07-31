package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowWarningEvent extends BMDExpressEventBase<String>
{

	public ShowWarningEvent(String payload)
	{
		super(payload);
	}
}
