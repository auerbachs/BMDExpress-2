package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class WilliamsTrendDataLoadedEvent extends BMDExpressEventBase<WilliamsTrendResults>{
	public WilliamsTrendDataLoadedEvent(WilliamsTrendResults payload)
	{
		super(payload);
	}
}
