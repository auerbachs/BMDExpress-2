package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowErrorEvent extends BMDExpressEventBase<String>
{

	public ShowErrorEvent(String payload)
	{
		super(payload);
	}
}
