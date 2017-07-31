package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class BMDProjectSavedEvent extends BMDExpressEventBase<BMDProject>
{

	public BMDProjectSavedEvent(BMDProject payload)
	{
		super(payload);
	}
}
