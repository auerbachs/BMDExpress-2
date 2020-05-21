package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class WilliamsTrendDataSelectedEvent extends BMDExpressEventBase<WilliamsTrendResults>
{
	public WilliamsTrendDataSelectedEvent(WilliamsTrendResults payload)
	{
		super(payload);
	}
}
