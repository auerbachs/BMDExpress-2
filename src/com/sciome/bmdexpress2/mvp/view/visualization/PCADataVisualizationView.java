package com.sciome.bmdexpress2.mvp.view.visualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.ChartKey;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.pca.PCAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.presenter.visualization.PCADataVisualizationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.visualization.IDataVisualizationView;
import com.sciome.bmdexpress2.service.VisualizationService;
import com.sciome.bmdexpress2.serviceInterface.IVisualizationService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.charts.SciomeChartBase;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.jfree.SciomePCAJFree;
import com.sciome.charts.jfree.SciomeScatterChartJFree;
import com.sciome.filter.DataFilterPack;

public class PCADataVisualizationView extends DataVisualizationView implements IDataVisualizationView {
	
	private static final String	DEFAULT			= "DEFAULT";
	// generate the charttable data. if this is null or empty, then
	// values for all data cells are generated.
	private Set<ChartKey>		useTheseKeysOnly;
	public PCADataVisualizationView() {
		super();
		IVisualizationService service = new VisualizationService();
		presenter = new PCADataVisualizationPresenter(this, service,
				BMDExpressEventBus.getInstance());

		useTheseKeysOnly = new HashSet<>();
		useTheseKeysOnly
				.addAll(Arrays.asList(new ChartKey(PCAResults.PC1, null),
						new ChartKey(PCAResults.PC2, null),
						new ChartKey(PCAResults.PC3, null),
						new ChartKey(PCAResults.PC4, null)));

		chartCache.put(DEFAULT + "-" + "PC1 V PC2",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC1, null),
						new ChartKey(PCAResults.PC2, null), this));

		chartCache.put(DEFAULT + "-" + "PC1 V PC3",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC1, null), 
						new ChartKey(PCAResults.PC3, null), this));
		
		chartCache.put(DEFAULT + "-" + "PC1 V PC4",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC1, null),
						new ChartKey(PCAResults.PC4, null), this));
		
		chartCache.put(DEFAULT + "-" + "PC2 V PC3",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC2, null), 
						new ChartKey(PCAResults.PC3, null), this));
		
		chartCache.put(DEFAULT + "-" + "PC2 V PC4",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC2, null), 
						new ChartKey(PCAResults.PC4, null), this));
		
		chartCache.put(DEFAULT + "-" + "PC3 V PC4",
				new SciomePCAJFree("", new ArrayList<>(),
						new ChartKey(PCAResults.PC3, null), 
						new ChartKey(PCAResults.PC4, null), this));
	}
	
	@Override
	public void redrawCharts(DataFilterPack dataFilterPack, List<String> selectedIds) {
		String chartKey = cBox.getSelectionModel().getSelectedItem();
		defaultDPack = dataFilterPack;
		this.selectedIds = selectedIds;
		if (results == null || results.size() == 0)
			return;
		
		List<BMDExpressAnalysisDataSet> pcaResults = new ArrayList<BMDExpressAnalysisDataSet>();
		pcaResults.add(((PCADataVisualizationPresenter)presenter).calculatePCA((DoseResponseExperiment)results.get(0)));
		
		List<ChartDataPack> chartDataPacks = presenter.getCategoryResultsChartPackData(pcaResults, dataFilterPack,
				selectedIds, useTheseKeysOnly, null, new ChartKey(PCAResults.DOSAGE, null));
		chartsList = new ArrayList<>();

		SciomeChartBase chart1 = chartCache
				.get(DEFAULT + "-" + "PC1 V PC2");
		chart1.redrawCharts(chartDataPacks);
		chartsList.add(chart1);
		SciomeChartBase chart2 = chartCache
				.get(DEFAULT + "-" + "PC1 V PC3");
		chart2.redrawCharts(chartDataPacks);
		chartsList.add(chart2);
		SciomeChartBase chart3 = chartCache
				.get(DEFAULT + "-" + "PC1 V PC4");
		chart3.redrawCharts(chartDataPacks);
		chartsList.add(chart3);
		SciomeChartBase chart4 = chartCache
				.get(DEFAULT + "-" + "PC2 V PC3");
		chart4.redrawCharts(chartDataPacks);
		chartsList.add(chart4);
		SciomeChartBase chart5 = chartCache
				.get(DEFAULT + "-" + "PC2 V PC4");
		chart5.redrawCharts(chartDataPacks);
		chartsList.add(chart5);
		SciomeChartBase chart6 = chartCache
				.get(DEFAULT + "-" + "PC3 V PC4");
		chart6.redrawCharts(chartDataPacks);
		chartsList.add(chart6);
		
		graphViewAnchorPane.getChildren().clear();
		showCharts();
	}

	@Override
	public List<String> getCannedCharts() {
		List<String> resultList = new ArrayList<>();
		resultList.add(DEFAULT_CHARTS);

		return resultList;
	}

}
