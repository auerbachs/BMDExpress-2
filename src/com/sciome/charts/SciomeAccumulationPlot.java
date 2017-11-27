package com.sciome.charts;

import java.util.Arrays;
import java.util.List;

import com.sciome.charts.data.ChartDataPack;
import com.sciome.charts.export.ChartDataExporter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;

/*
 * 
 */
public abstract class SciomeAccumulationPlot extends SciomeChartBase implements ChartDataExporter
{
	protected final static Integer	MAX_ACCUMULATION_BEFORE_MODULUS	= 300;
	protected final static Integer	MOD_AFTER_REACH_MAX				= 20;
	protected final static Integer	MAX_TO_POPUP					= 100;
	protected final static Integer	MAX_PREV_OBJECTS_TO_STORE		= 0;
	protected CheckBox				unBinCheckBox					= new CheckBox("Show All Data");
	protected Tooltip				toolTip							= new Tooltip("");

	public SciomeAccumulationPlot(String title, List<ChartDataPack> chartDataPacks, String key,
			Double bucketsize, SciomeChartListener chartListener)
	{
		super(title, chartDataPacks, chartListener);

		logXAxis.setSelected(true);
		logYAxis.setSelected(false);
		this.chartableKeys = new String[] { key };
		showLogAxes(true, true, false, false, Arrays.asList(unBinCheckBox));
		showChart();

		logXAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		logYAxis.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

		unBinCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
			{
				showChart();
			}
		});

	}

	// join objects together to show a list of lines that show values of the points being plotted
	// the objects and values need to be in alignment to ensure that the value they represent
	// is properly appended next to it in the popup.
	protected String joinObjects(List<Object> objects, Double accumulation, List<Double> values, String key,
			int max)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Accumulation = ");
		sb.append(accumulation);
		sb.append("\n");
		int i = 0;
		for (Object obj : objects)
		{
			sb.append(obj);
			sb.append("  ");
			sb.append(key);
			sb.append(" = ");
			sb.append(values.get(i));
			sb.append(System.getProperty("line.separator"));
			if (i > max)
			{
				sb.append("....");
				break;
			}
			i++;
		}
		return sb.toString();
	}

	protected String joinAllObjects(List<Object> objects, Double accumulation, List<Double> values,
			String key)
	{
		return joinObjects(objects, accumulation, values, key, 2000000000);

	}

	@Override
	protected boolean isXAxisDefineable()
	{
		return true;
	}

	@Override
	protected boolean isYAxisDefineable()
	{
		return true;
	}

	@Override
	protected void redrawChart()
	{
		showChart();

	}

}
