package com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;

import javafx.event.ActionEvent;

public interface IBMDAnalysisView
{

	public void clearProgressBar();

	public void updateProgressBar(String label, double value);

	public void initializeProgressBar(String label);

	public void finishedBMDAnalysis();

	public void startedBMDAnalysis();

	public void closeWindow();

	void handle_PowerCheckBox(ActionEvent event);

	void handle_HillCheckBox(ActionEvent event);

	void handle_FlagHillCheckBox(ActionEvent event);

	void handle_cancel(ActionEvent event);

	void handle_start(ActionEvent event);

	void handle_close(ActionEvent event);
	
	void handle_saveSettingsButtonPressed(ActionEvent event);

	void initData(List<IStatModelProcessable> processableData, boolean selectModelsOnly);

}
