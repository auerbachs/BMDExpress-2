package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ExpressionDataSelectedForProcessingEvent extends BMDExpressEventBase<DoseResponseExperiment>
{

	public ExpressionDataSelectedForProcessingEvent(DoseResponseExperiment payload)
	{
		super(payload);
	}
}
