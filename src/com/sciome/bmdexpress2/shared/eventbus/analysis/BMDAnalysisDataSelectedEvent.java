package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisDataSelectedEvent extends BMDExpressEventBase<BMDResult>
{

	public BMDAnalysisDataSelectedEvent(BMDResult payload)
	{
		super(payload);
	}
}
