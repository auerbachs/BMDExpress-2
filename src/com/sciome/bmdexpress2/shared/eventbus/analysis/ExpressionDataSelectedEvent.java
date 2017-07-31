package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ExpressionDataSelectedEvent extends BMDExpressEventBase<DoseResponseExperiment>
{

	public ExpressionDataSelectedEvent(DoseResponseExperiment payload)
	{
		super(payload);
	}
}
