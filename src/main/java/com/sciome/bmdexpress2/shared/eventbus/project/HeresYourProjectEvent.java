package com.sciome.bmdexpress2.shared.eventbus.project;

import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class HeresYourProjectEvent extends BMDExpressEventBase<BMDProject>
{

	public HeresYourProjectEvent(BMDProject payload)
	{
		super(payload);
	}
}
