package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowOriogenResultsVisualizationsEvent extends BMDExpressEventBase<List<OriogenResults>>{
	public ShowOriogenResultsVisualizationsEvent(List<OriogenResults> payload)
	{
		super(payload);
	}
}
