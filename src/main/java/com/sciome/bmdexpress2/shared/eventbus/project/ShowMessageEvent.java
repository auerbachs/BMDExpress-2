package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowMessageEvent extends BMDExpressEventBase<String>
{

	public ShowMessageEvent(String payload)
	{
		super(payload);
	}
}
