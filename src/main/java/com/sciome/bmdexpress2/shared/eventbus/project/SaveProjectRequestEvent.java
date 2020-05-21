package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class SaveProjectRequestEvent extends BMDExpressEventBase<File>
{

	public SaveProjectRequestEvent(File payload)
	{
		super(payload);
	}
}
