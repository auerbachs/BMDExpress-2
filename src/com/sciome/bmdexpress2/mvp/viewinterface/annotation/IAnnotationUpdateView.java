package com.sciome.bmdexpress2.mvp.viewinterface.annotation;

import java.util.List;

public interface IAnnotationUpdateView
{

	public void handle_updateButton();

	public void handle_doneButton();

	public void updateTableView(List<Object[]> tableDats);

	public void beginUpdateProgress();

	public void updateProgress(String labelText, double value);

	public void finishUpdate();

	public void closeWindow();

}
