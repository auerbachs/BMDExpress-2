package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ImportJSONEvent extends BMDExpressEventBase<File>
{

	public ImportJSONEvent(File payload)
	{
		super(payload);
	}
}
