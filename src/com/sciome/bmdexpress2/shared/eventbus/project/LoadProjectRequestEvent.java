package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class LoadProjectRequestEvent extends BMDExpressEventBase<File>
{

	public LoadProjectRequestEvent(File payload)
	{
		super(payload);
	}
}
