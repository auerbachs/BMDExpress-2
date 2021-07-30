package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class CurveFitPrefilterDataCombinedSelectedEvent extends BMDExpressEventBase<CombinedDataSet>
{
	public CurveFitPrefilterDataCombinedSelectedEvent(CombinedDataSet payload)
	{
		super(payload);
	}
}
