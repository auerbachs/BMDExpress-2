package com.sciome.bmdexpress2.shared.eventbus.project;

import java.io.File;

import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

public class AddProjectRequestEvent extends BMDExpressEventBase<File>{

	public AddProjectRequestEvent(File payload) {
		super(payload);
	}

}
