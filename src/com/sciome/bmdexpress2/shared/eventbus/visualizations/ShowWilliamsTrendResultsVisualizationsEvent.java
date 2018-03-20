package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowWilliamsTrendResultsVisualizationsEvent extends BMDExpressEventBase<List<WilliamsTrendResults>>{
	public ShowWilliamsTrendResultsVisualizationsEvent(List<WilliamsTrendResults> payload)
	{
		super(payload);
	}
}
