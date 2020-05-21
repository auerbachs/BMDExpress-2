package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;

public interface IInfoView
{

	/*
	 * Display the warning message to the user
	 */
	void setWarningMessage(String value);

	void showAnalysisInfo(AnalysisInfo analysisInfo);

	void clearList();
}
