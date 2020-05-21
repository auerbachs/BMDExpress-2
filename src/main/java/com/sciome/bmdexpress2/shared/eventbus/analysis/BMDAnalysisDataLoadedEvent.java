package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisDataLoadedEvent extends BMDExpressEventBase<BMDResult>
{

	public BMDAnalysisDataLoadedEvent(BMDResult payload)
	{
		super(payload);
	}
}
