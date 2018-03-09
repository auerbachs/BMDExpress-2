package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class OriogenDataSelectedForProcessingEvent  extends BMDExpressEventBase<OriogenResults>{
	public OriogenDataSelectedForProcessingEvent(OriogenResults payload)
	{
		super(payload);
	}
}
