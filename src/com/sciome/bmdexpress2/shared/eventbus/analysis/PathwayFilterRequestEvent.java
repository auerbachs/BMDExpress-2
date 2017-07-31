package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class PathwayFilterRequestEvent extends BMDExpressEventBase<String>
{

	public PathwayFilterRequestEvent(String payload)
	{
		super(payload);
	}
}
