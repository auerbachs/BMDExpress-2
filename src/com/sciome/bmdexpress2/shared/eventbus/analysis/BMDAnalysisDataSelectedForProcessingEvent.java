package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisDataSelectedForProcessingEvent extends BMDExpressEventBase<BMDResult>
{

	public BMDAnalysisDataSelectedForProcessingEvent(BMDResult payload)
	{
		super(payload);
	}
}
