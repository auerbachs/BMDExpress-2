package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class RequestFileNameForProjectSaveEvent extends BMDExpressEventBase<File>
{

	public RequestFileNameForProjectSaveEvent(File payload)
	{
		super(payload);
	}
}
