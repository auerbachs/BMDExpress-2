package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowOneWayANOVAResultsVisualizationsEvent extends BMDExpressEventBase<List<OneWayANOVAResults>>
{

	public ShowOneWayANOVAResultsVisualizationsEvent(List<OneWayANOVAResults> payload)
	{
		super(payload);
	}
}
