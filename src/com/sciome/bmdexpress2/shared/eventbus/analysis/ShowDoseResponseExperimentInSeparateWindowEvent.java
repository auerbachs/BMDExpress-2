package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowDoseResponseExperimentInSeparateWindowEvent
		extends BMDExpressEventBase<DoseResponseExperiment>
{

	public ShowDoseResponseExperimentInSeparateWindowEvent(DoseResponseExperiment payload)
	{
		super(payload);
	}
}
