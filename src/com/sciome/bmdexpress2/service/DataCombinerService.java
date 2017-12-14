package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.model.CombinedRow;
import com.sciome.bmdexpress2.serviceInterface.IDataCombinerService;

public class DataCombinerService implements IDataCombinerService
{

	@Override
	public CombinedDataSet combineBMDExpressAnalysisDataSets(List<BMDExpressAnalysisDataSet> dataSets)
	{
		// step one aggregate the Header
		List<String> theHeader = new ArrayList<>();
		Set<String> headersAdded = new HashSet<>();
		Map<String, Integer> headerToIndex = new HashMap<>();

		int i = 1;
		for (BMDExpressAnalysisDataSet dataset : dataSets)
		{
			for (String head : dataset.getColumnHeader())
			{
				if (headersAdded.contains(head))
					continue;
				theHeader.add(head);
				headerToIndex.put(head, i++);
				headersAdded.add(head);
			}
		}
		theHeader.add(0, "Analysis");

		// now we have a header. maybe not ordered perfect, but the data is there
		// now we have the length of rows and can create a Combined data set and start adding rows.

		CombinedDataSet combinedDataSet = new CombinedDataSet(theHeader, "Combined Analyses");
		// intialize the rows.
		List<BMDExpressAnalysisRow> rows = new ArrayList<>();
		for (BMDExpressAnalysisDataSet dataset : dataSets)
			for (BMDExpressAnalysisRow r : dataset.getAnalysisRows())
			{
				CombinedRow cr = new CombinedRow(r.getObject());
				for (String str : theHeader)
					cr.getRow().add(null);

				// this is the "series" name
				cr.getRow().add(0, dataset.getName());
				rows.add(cr);

			}
		combinedDataSet.getAnalysisRows().addAll(rows);

		// now the rows are there, let's fill them in
		i = 0;
		for (BMDExpressAnalysisDataSet dataset : dataSets)
		{
			for (BMDExpressAnalysisRow r : dataset.getAnalysisRows())
			{
				int j = 0;
				for (String str : dataset.getColumnHeader())
				{
					// there really has to be a value here. we did the same loop
					// above and put indexes in here.
					int index = headerToIndex.get(str).intValue();
					combinedDataSet.getAnalysisRows().get(i).getRow().set(index, r.getRow().get(j));
					j++;
				}
				i++;
			}
		}

		return combinedDataSet;
	}

}
