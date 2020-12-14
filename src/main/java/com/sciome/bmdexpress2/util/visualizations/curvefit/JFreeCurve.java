/*
 * ModelGraphics.java
 *
 * Created on 10/17/2007
 *
 * Uses the JFreeChart libraries to create graphs based on
 * Benchmark Dose Analyses done in BMDExpress and provides the user with
 * the ability to edit various aspects of the resulting graph.
 *
 * Modified by Longlong Yang, 11/13/2008
 * parameters below with pValue added to index = 2
 * So the first three values are BMD, BMDL, pValue
 */

/*
 * @author  ehealy
 */
package com.sciome.bmdexpress2.util.visualizations.curvefit;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.ColorBlock;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.prefilter.OnewayAnova;
import com.sciome.charts.jfree.CustomJFreeLogAxis;
import com.sciome.charts.jfree.SciomeChartViewer;

/**
 */
public class JFreeCurve
{

	// Variables declaration

	private XYSeriesCollection seriesSet;
	private Color[] chartColors;

	private JFreeChart chart;

	private double logZeroDose;
	private boolean logDoseAxis;
	private final Double[] decades = { .00000000001, .0000000001, .000000001, .00000001, .0000001, .000001,
			.00001, .0001, .001, .01, .1, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 10000000.0,
			100000000.0 };

	private String pathway = "";
	// store a mapping of the BMDResults and the individual stat results that will be plotted
	private Map<BMDResult, Set<ProbeStatResult>> bmdResultToProbeStatResultMap;

	private SciomeChartViewer chartView;
	private List<AbstractXYAnnotation> annotations = new ArrayList<>();

	// End of variables declaration

	/**
	 */
	public JFreeCurve(ModelGraphicsEvent modelGraphicsEventListener, String p,
			Map<BMDResult, Set<ProbeStatResult>> bmdResultToProbeStatResultMap)
	{
		this.bmdResultToProbeStatResultMap = bmdResultToProbeStatResultMap;
		pathway = p;

		chartColors = new Color[4];
		chartColors[0] = Color.RED;
		chartColors[1] = Color.BLUE;
		chartColors[2] = Color.BLACK;
		chartColors[3] = Color.GREEN;
		seriesSet = new XYSeriesCollection();
		// init the chart
		chart = ChartFactory.createXYLineChart(pathway, // chart title
				"Dose", // domain axis label
				"Log(Expression)", // range axis label
				seriesSet, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
		);
		// configure default renderer options

		XYPlot plot = (XYPlot) chart.getPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(2, Color.BLACK);
		renderer.setSeriesPaint(3, Color.BLACK);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setLowerMargin(0.0);

		setAxisForChart();

		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.01);
		plot.setRenderer(renderer);

		initComponents();

		for (BMDResult key : bmdResultToProbeStatResultMap.keySet())
			for (ProbeStatResult psr : bmdResultToProbeStatResultMap.get(key))
				seriesSet.addSeries(createModels(psr, key));

		plot.setBackgroundPaint(Color.white);

		for (AbstractXYAnnotation ann : annotations)
			plot.addAnnotation(ann);
		// Create Panel
		chartView = new SciomeChartViewer(chart);

	}

	public ChartViewer getChartViewer()
	{
		return chartView;
	}

	private double firstNonZeroDose(double[] doses2)
	{
		boolean hasZeroLowDose = false;
		for (int i = 0; i < doses2.length; i++)
		{
			if (doses2[i] == 0.0)
				hasZeroLowDose = true;

			if (hasZeroLowDose && doses2[i] > 0)
			{
				if (doses2[i] > 1) // default to something below 1. but not to far below one
				{
					logZeroDose = 0.1;
					return 0.09;
				}
				logZeroDose = doses2[i];
				double decade = 1.0;
				double decadeBelowLow = firstdDecadeBelow(logZeroDose);
				for (decade = 1.0; decade >= decadeBelowLow; decade *= .1)
				{

				}
				logZeroDose = decade;
				return decade * .9;
			}
		}
		// no zero dose. just return the minimum dose
		logZeroDose = doses2[0];
		return doses2[0] * .9;
	}

	private double firstdDecadeBelow(double logZeroDose2)
	{

		for (int i = 1; i < decades.length; i++)
		{
			double decade = decades[i];
			double lessthan = decade * .0001;
			if (Math.abs(decade - logZeroDose) < lessthan)
				return decades[i];
			else if (logZeroDose < decade)
				return decades[i - 1];
		}
		return .000000000001;
	}

	private double maskDose(double dose)
	{
		if (logDoseAxis && dose == 0)
			return logZeroDose;

		return dose;

	}

	/*
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents()
	{

	}

	/**
	 * make a copy of the data applying decision without changing original
	 */
	private double[] truncateDecimal(double[] inputs)
	{
		double[] outputs = new double[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			outputs[i] = NumberManager.numberFormat(6, inputs[i]);

		return outputs;
	}

	private double[] getDoses(List<BMDResult> bmdResults)
	{
		List<Double> doses = new ArrayList<>();
		for (BMDResult bmdresult : bmdResults)
		{
			double[] dos = getDoses(bmdresult);
			for (double d : dos)
				doses.add(d);
		}

		Collections.sort(doses);
		double[] doseArray = new double[doses.size()];
		for (int i = 0; i < doses.size(); i++)
			doseArray[i] = doses.get(i);
		return doseArray;
	}

	private double[] getDoses(BMDResult bmdResult)
	{
		double[] doses = new double[bmdResult.getDoseResponseExperiment().getTreatments().size()];
		for (int i = 0; i < bmdResult.getDoseResponseExperiment().getTreatments().size(); i++)
			doses[i] = bmdResult.getDoseResponseExperiment().getTreatments().get(i).getDose();

		return doses;
	}

	/**
	 *
	 */
	private XYSeries createModels(ProbeStatResult probeStatResult, BMDResult bmdResult)
	{
		OnewayAnova oneway = new OnewayAnova();
		double[] doses = getDoses(bmdResult);
		oneway.setVariablesXX(0, doses);
		BMDoseModel bmdModel = new BMDoseModel(probeStatResult.getBestStatResult(),
				probeStatResult.getProbeResponse().getProbe());
		bmdModel.setEstimates(oneway.estimates());
		double[] parameters = getParameters(probeStatResult);
		bmdModel.setParameters(parameters);

		// low and high doses, to set model sample boundries
		double minDose = bmdModel.minimumDose();
		double maxDose = bmdModel.maximumDose();

		// create new series
		XYSeries modelSeries = new XYSeries(bmdResult.getName() + ": " + probeStatResult.toString());
		Set<Double> uniqueDosesSet = new HashSet<>();
		for (int i = 0; i < doses.length; i++)
			uniqueDosesSet.add(doses[i]);
		List<Double> uniqueDoses = new ArrayList<>(uniqueDosesSet);
		Collections.sort(uniqueDoses);
		Double prevDose = null;
		for (Double dose : uniqueDoses)
		{
			if (prevDose == null)
			{
				prevDose = dose;
				continue;
			}
			// Set up modelSeries from LBUFFER below the minDose to RBUFFER above maxDose
			double increment = (dose - prevDose) / 190.0;
			if (increment > .05 && prevDose < 10.0)
				increment = .05;
			for (double counter = prevDose; counter < dose; counter += increment)
				modelSeries.add(counter, bmdModel.response(counter));
			prevDose = dose;
		}

		XYDrawableAnnotation ann = new XYDrawableAnnotation(probeStatResult.getBestBMD().doubleValue(),
				bmdModel.response(probeStatResult.getBestBMD().doubleValue()), 15, 15,
				new ColorBlock(Color.GREEN, 15, 15));
		XYDrawableAnnotation ann1 = new XYDrawableAnnotation(probeStatResult.getBestBMDL().doubleValue(),
				bmdModel.response(probeStatResult.getBestBMDL().doubleValue()), 15, 15,
				new ColorBlock(Color.RED, 15, 15));
		XYDrawableAnnotation ann2 = new XYDrawableAnnotation(probeStatResult.getBestBMDU().doubleValue(),
				bmdModel.response(probeStatResult.getBestBMDU().doubleValue()), 15, 15,
				new ColorBlock(Color.BLUE, 15, 15));

		annotations.add(ann);
		annotations.add(ann1);
		annotations.add(ann2);

		double smallestDose = 0.0;
		if (logDoseAxis)
			smallestDose = logZeroDose;
		else
			smallestDose = minDose;

		return modelSeries;
	}

	/*
	 * get the parameters of a given probe and the model name
	 */
	private double[] getParameters(ProbeStatResult probeStatResult)
	{
		StatResult theStatResult = probeStatResult.getBestStatResult();

		if (theStatResult != null && theStatResult.getCurveParameters() != null)
		{
			double parameters[] = new double[theStatResult.getCurveParameters().length + 6];
			parameters[0] = theStatResult.getBMD();
			parameters[1] = theStatResult.getBMDL();
			parameters[2] = theStatResult.getBMDU();
			parameters[3] = theStatResult.getFitPValue();
			parameters[4] = theStatResult.getAIC();
			parameters[5] = theStatResult.getFitLogLikelihood();
			for (int i = 0; i < theStatResult.getCurveParameters().length; i++)
				parameters[i + 6] = theStatResult.getCurveParameters()[i];
			return parameters;
		}
		else
		{
			double parameters[] = new double[1];
			return parameters;
		}

	}

	private void setAxisForChart()
	{
		LogAxis logAxis = new CustomJFreeLogAxis();
		double[] doses = getDoses(new ArrayList<>(bmdResultToProbeStatResultMap.keySet()));
		double lowRange = firstNonZeroDose(doses);
		logAxis.setRange(new Range(lowRange, doses[doses.length - 1] * 1.1));
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainAxis(logAxis);

		// logAxis.setMinorTickCount(10);
		logAxis.setMinorTickMarksVisible(false);
		logAxis.setBase(10);
		final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		newSymbols.setExponentSeparator("E");
		final DecimalFormat decForm = new DecimalFormat("0.##E0#");
		decForm.setDecimalFormatSymbols(newSymbols);

		logAxis.setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
			{
				// deal with the zero dose on in the log axis.
				if (Math.abs(logZeroDose - number) < .00000000000001 && doses[0] == 0.0)
					return new StringBuffer("0");
				return new StringBuffer(decForm.format(number));
			}

			@Override
			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos)
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Number parse(String source, ParsePosition parsePosition)
			{
				// TODO Auto-generated method stub
				return null;
			}

		});

	}

}
