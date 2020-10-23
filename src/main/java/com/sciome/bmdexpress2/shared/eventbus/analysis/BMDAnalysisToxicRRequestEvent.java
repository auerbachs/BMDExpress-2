package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisToxicRRequestEvent extends BMDExpressEventBase<String>
{

	public BMDAnalysisToxicRRequestEvent(String payload)
	{
		super(payload);
	}
}
