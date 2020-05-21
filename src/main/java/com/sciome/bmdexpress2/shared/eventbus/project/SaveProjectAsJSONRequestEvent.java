package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class SaveProjectAsJSONRequestEvent extends BMDExpressEventBase<File>
{

	public SaveProjectAsJSONRequestEvent(File payload)
	{
		super(payload);
	}
}
