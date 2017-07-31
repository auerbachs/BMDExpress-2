package com.sciome.bmdexpress2.mvp.viewinterface.categorization;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;

import javafx.event.ActionEvent;

public interface ICategorizationView
{

	public void handle_browseProbe();

	public void handle_browseCategory();

	void handle_start(ActionEvent event);

	void handle_close(ActionEvent event);

	public void finishedCategorization();

	public void closeWindow();

	public void startedCategorization();

	public void updateProgressBar(String label, double value);

	public void enableButtons();

	void initData(List<BMDResult> bmdResults, CategoryAnalysisEnum catAnalysisEnum);

}
