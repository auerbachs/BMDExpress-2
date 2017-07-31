package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class ImportBMDEvent extends BMDExpressEventBase<File>
{

	public ImportBMDEvent(File payload)
	{
		super(payload);
	}
}
