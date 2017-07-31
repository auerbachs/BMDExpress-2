package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import javafx.event.ActionEvent;
import javafx.stage.Window;

public interface IMenuBarView
{

	public void expressionDataSelected();

	public void oneWayANOVADataSelected();

	public void bMDAnalysisDataSelected();

	public void functionalCategoryDataSelected();

	void handle_pathwayFilter(ActionEvent event);

	public void pathwayFilterSelected();

	public void saveAs();

	public Window getWindow();

}
