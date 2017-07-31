package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class PathwayFilterDataLoadedEvent extends BMDExpressEventBase<PathwayFilterResults>
{

	public PathwayFilterDataLoadedEvent(PathwayFilterResults payload)
	{
		super(payload);
	}
}
