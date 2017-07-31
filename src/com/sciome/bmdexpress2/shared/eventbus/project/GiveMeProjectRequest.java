package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class GiveMeProjectRequest extends BMDExpressEventBase<String>
{

	public GiveMeProjectRequest(String payload)
	{
		super(payload);
	}
}
