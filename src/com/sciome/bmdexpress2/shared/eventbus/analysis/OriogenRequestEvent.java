package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OriogenRequestEvent extends BMDExpressEventBase<String>{
	public OriogenRequestEvent(String payload)
	{
		super(payload);
	}
}
