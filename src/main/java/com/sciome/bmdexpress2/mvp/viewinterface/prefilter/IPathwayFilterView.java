package com.sciome.bmdexpress2.mvp.viewinterface.prefilter;

import java.io.File;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;

public interface IPathwayFilterView
{
	public void closeWindow();

	public void updateProgressBar(String label, double value);

	public File checkRPath(String rscriptPath);

	void initData(List<IStatModelProcessable> selectedItems, List<IStatModelProcessable> processableDatas);

}
