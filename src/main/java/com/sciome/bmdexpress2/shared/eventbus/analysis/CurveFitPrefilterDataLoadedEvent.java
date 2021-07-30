package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CurveFitPrefilterDataLoadedEvent extends BMDExpressEventBase<CurveFitPrefilterResults>
{
	public CurveFitPrefilterDataLoadedEvent(CurveFitPrefilterResults payload)
	{
		super(payload);
	}
}
