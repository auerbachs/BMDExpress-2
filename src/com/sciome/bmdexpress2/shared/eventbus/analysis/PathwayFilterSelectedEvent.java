package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class PathwayFilterSelectedEvent extends BMDExpressEventBase<PathwayFilterResults>
{

	public PathwayFilterSelectedEvent(PathwayFilterResults payload)
	{
		super(payload);
	}
}
