package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowPathwayFilterResultVisualizationsEvent
		extends BMDExpressEventBase<List<PathwayFilterResults>>
{

	public ShowPathwayFilterResultVisualizationsEvent(List<PathwayFilterResults> payload)
	{
		super(payload);
	}
}
