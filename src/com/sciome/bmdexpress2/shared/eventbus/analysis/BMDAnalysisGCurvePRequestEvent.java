package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDAnalysisGCurvePRequestEvent extends BMDExpressEventBase<String>
{

	public BMDAnalysisGCurvePRequestEvent(String payload)
	{
		super(payload);
	}
}
