package com.sciome.bmdexpress2.shared.eventbus.visualizations;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ShowDataVisualizationEvent extends BMDExpressEventBase<BMDProject>
{

	public ShowDataVisualizationEvent(BMDProject payload)
	{
		super(payload);
	}
}
