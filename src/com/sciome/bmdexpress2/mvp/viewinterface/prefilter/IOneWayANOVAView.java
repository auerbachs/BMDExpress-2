package com.sciome.bmdexpress2.mvp.viewinterface.prefilter;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;

public interface IOneWayANOVAView
{
	public void closeWindow();

	// void initData(IStatModelProcessable processableData, List<IStatModelProcessable> processabelDatas);

	void initData(List<IStatModelProcessable> processableData, List<IStatModelProcessable> processableDatas);

}
