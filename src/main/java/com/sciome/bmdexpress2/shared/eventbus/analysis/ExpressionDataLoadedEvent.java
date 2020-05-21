package com.sciome.bmdexpress2.shared.eventbus.analysis;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ExpressionDataLoadedEvent extends BMDExpressEventBase<List<DoseResponseExperiment>>
{

	public ExpressionDataLoadedEvent(List<DoseResponseExperiment> payload)
	{
		super(payload);
	}
}
