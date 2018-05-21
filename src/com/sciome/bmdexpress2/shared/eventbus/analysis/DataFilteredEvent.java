package com.sciome.bmdexpress2.shared.eventbus.analysis;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBase;

import javafx.collections.transformation.FilteredList;

public class DataFilteredEvent extends BMDExpressEventBase<FilteredList<BMDExpressAnalysisRow>> {
	public DataFilteredEvent(FilteredList<BMDExpressAnalysisRow> payload)
	{
		super(payload);
	}
}
