package com.sciome.charts.javafx;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sciome.charts.data.ChartConfiguration;
import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;

/*
 * 
 */
public class SciomePieChart extends SciomeChartBase implements ChartDataExporter
{

	private Map<String, Double>			pieDataMap;
	private Map<String, List<Object>>	pieObjectMap;
	private final Label					caption	= new Label("");

	public SciomePieChart(Map<String, Double> pieDataMap, Map<String, List<Object>> pieObjectMap,
			List<ChartDataPack> packs, String title, SciomeChartListener listener)
	{
		super(title, packs, listener);
		this.pieDataMap = pieDataMap;
		this.pieObjectMap = pieObjectMap;
		chartableKeys = new String[] {};

		showChart(caption);

	}

	/*
	 * generate a pie chart and return
	 */
	@Override
	protected Chart generateChart(String[] keys, ChartConfiguration chartConfig)
	{

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		Double total = 0.0;
		for (Entry<String, Double> entry : pieDataMap.entrySet())
		{

			total += entry.getValue();

		}

		DecimalFormat formatter = new DecimalFormat("#.#");
		for (Entry<String, Double> entry : pieDataMap.entrySet())
		{

			Double avg = entry.getValue() * 100.0 / total;
			pieChartData.add(new PieChart.Data(entry.getKey() + "\n(" + formatter.format(avg) + "%)",
					entry.getValue()));

		}
		PieChart chart = new PieChart(pieChartData);

		chart.setTitle(title);

		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");

		for (final PieChart.Data data : chart.getData())
		{
			Node node = data.getNode();
			Tooltip toolTip = new Tooltip("");
			toolTip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");;
			Tooltip.install(node, toolTip);

			node.setOnMouseEntered(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					node.setEffect(new Glow());
					toolTip.setText(String.valueOf(data.getName() + "\n" + (int) data.getPieValue()));

				}
			});

			// OnMouseExited
			node.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent arg0)
				{
					node.setEffect(null);
					caption.setText("");
				}
			});

			// OnMouseReleased
			node.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
				@Override
				public void handle(javafx.scene.input.MouseEvent mouseEvent)
				{
				}
			});
		}

		return chart;
	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return false;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return false;
	}

	@Override
	protected void redrawChart()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * implement the getting of lines that need to be exported.
	 */
	@Override
	public List<String> getLinesToExport()
	{

		List<String> returnList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		sb.append("label");
		sb.append("\t");
		sb.append("value");
		returnList.add(sb.toString());

		for (final PieChart.Data data : ((PieChart) getChart()).getData())
		{
			sb.setLength(0);
			String label = data.getName().replaceAll("\n", " ");
			sb.append(label);
			sb.append("\t");
			sb.append(data.getPieValue());

			returnList.add(sb.toString());

		}

		return returnList;

	}

}
