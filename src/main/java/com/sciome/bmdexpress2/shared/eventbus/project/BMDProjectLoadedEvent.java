package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDProjectLoadedEvent extends BMDExpressEventBase<BMDProject>
{

	public BMDProjectLoadedEvent(BMDProject payload)
	{
		super(payload);
	}
}
