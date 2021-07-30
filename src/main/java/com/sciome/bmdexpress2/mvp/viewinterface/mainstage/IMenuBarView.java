package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import javafx.stage.Window;

public interface IMenuBarView
{

	public void expressionDataSelected();

	public void oneWayANOVADataSelected();

	public void williamsTrendDataSelected();

	public void curveFitPrefilterDataSelected();

	public void oriogenDataSelected();

	public void bMDAnalysisDataSelected();

	public void functionalCategoryDataSelected();

	public void saveAs();

	public Window getWindow();

	public void combinedSelected();

	public void noDataSelected();

}
