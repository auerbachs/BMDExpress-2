package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import javafx.event.ActionEvent;
import javafx.stage.Window;

public interface IMenuBarView
{

	public void expressionDataSelected();

	public void oneWayANOVADataSelected();

	public void williamsTrendDataSelected();
	
	public void bMDAnalysisDataSelected();

	public void functionalCategoryDataSelected();

	public void saveAs();

	public Window getWindow();

}
