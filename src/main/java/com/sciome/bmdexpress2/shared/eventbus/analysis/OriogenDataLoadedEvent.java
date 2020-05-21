package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OriogenDataLoadedEvent extends BMDExpressEventBase<OriogenResults>{
	public OriogenDataLoadedEvent(OriogenResults payload)
	{
		super(payload);
	}
}
