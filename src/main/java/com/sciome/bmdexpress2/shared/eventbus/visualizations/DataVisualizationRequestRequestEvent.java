package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class DataVisualizationRequestRequestEvent extends BMDExpressEventBase<String>
{

	public DataVisualizationRequestRequestEvent(String payload)
	{
		super(payload);
	}
}
