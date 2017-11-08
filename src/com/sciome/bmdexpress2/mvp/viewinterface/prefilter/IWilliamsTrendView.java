package com.sciome.bmdexpress2.mvp.viewinterface.prefilter;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;

public interface IWilliamsTrendView {
	public void updateProgress(double progress);
	
	public void updateMessage(String message);
	
	public void closeWindow();

	void initData(List<IStatModelProcessable> processableData, List<IStatModelProcessable> processableDatas);

}
