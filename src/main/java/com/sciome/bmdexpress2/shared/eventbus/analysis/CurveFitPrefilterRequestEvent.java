package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CurveFitPrefilterRequestEvent extends BMDExpressEventBase<String>
{
	public CurveFitPrefilterRequestEvent(String payload)
	{
		super(payload);
	}
}
