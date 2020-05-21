package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisRequestEvent extends BMDExpressEventBase<String>
{

	public BMDAnalysisRequestEvent(String payload)
	{
		super(payload);
	}
}
