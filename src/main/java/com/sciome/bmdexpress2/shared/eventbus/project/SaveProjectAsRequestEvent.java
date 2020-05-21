package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class SaveProjectAsRequestEvent extends BMDExpressEventBase<File>
{

	public SaveProjectAsRequestEvent(File payload)
	{
		super(payload);
	}
}
