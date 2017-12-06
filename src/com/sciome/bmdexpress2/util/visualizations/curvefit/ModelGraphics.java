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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.prefilter.OnewayAnova;
import com.sciome.charts.jfree.CustomJFreeLogAxis;

/**
 * The ModelGraphics class
 */
public class ModelGraphics extends JPanel
{

	// Variables declaration

	// Model Graphics Variables
	private BMDResult						bmdResults;					// the matrix of data from the parent
	private OnewayAnova						oneway;						//
	private BMDoseModel						bmdModel;					//
	private StatResult						bestModel;

	// srcName = getName() from called MatrixData to avoind create it again
	// if called from selecting probe from parent displayview
	private String							srcName;

	private double[]						doses;						// read in doses
	private double[]						responses;					// holds responses
	private double[]						parameters;					// read in parameters BMD, BMDL, BMDU,
																		// pValue...

	private XYSeries						dataSeries;					// holds the raw data
	private XYSeries						modelSeries;				// holds the model
	private XYSeries						bmdSeries;					// holds the BMD drawing setup
	private XYSeries						bmdlSeries;					// holds the BMDL drawing setup
	private XYSeries						bmduSeries;					// holds the BMDU drawing setup
	private XYSeriesCollection				seriesSet;					// holds the set of series currently
																		// displayed

	private Color[]							chartColors;				// holds the colors for various chart
																		// components

	private JFreeChart						chart;						// the displayed chart
	private ChartPanel						cP;							// the panel for the chart

	private int								NUM_SERIES;					// holds the number of data series

	private int								CHART_WIDTH;				// for the chart's width
	private int								CHART_HEIGHT;				// for the chart's height

	private double							HIGH;						// holds the high y value
	private double							LOW;						// holds the low y value

	// constants to set parameters
	final int								DATA;
	final int								POWER;
	final int								LINEAR;
	final int								TWODEG;
	final int								THREEDEG;
	final double							LBUFFER;					// used to make model sample differ
																		// from data
																		// range
	final double							RBUFFER;					// used to make model sample differ
																		// from data
																		// range

	private javax.swing.JButton				printButton;
	private javax.swing.JButton				closeButton;
	private javax.swing.JButton				clearButton;
	private javax.swing.JButton				colorButton;
	private javax.swing.JComboBox<String>	modelBox;
	private javax.swing.JComboBox<Probe>	probeBox;
	private javax.swing.JLabel				modelLabel;
	private javax.swing.JLabel				addModelLabel;
	private javax.swing.JLabel				bmdlLabel;
	private javax.swing.JLabel				bmduLabel;
	private javax.swing.JLabel				bmdLabel;
	private javax.swing.JLabel				aicLabel;
	private javax.swing.JLabel				idLabel;
	private javax.swing.JLabel				pvLabel;
	private javax.swing.JPanel				jPanel1;
	private javax.swing.JPanel				jPanel2;
	private javax.swing.JPanel				jPanel3;
	private javax.swing.JTextField			modelField;
	private javax.swing.JTextField			bmdField;
	private javax.swing.JTextField			bmdLField;
	private javax.swing.JTextField			bmdUField;
	private javax.swing.JTextField			aicField;
	private javax.swing.JTextField			pvalField;
	private javax.swing.JCheckBox			allDataBox;
	private javax.swing.JCheckBox			meanStdBox;
	private javax.swing.JCheckBox			logDoseAxis;
	private javax.swing.JSeparator			jSep1;

	private Map<Probe, double[]>			probeResponseMap;
	private Map<Probe, ProbeStatResult>		probeStatResultMap;
	private ModelGraphicsEvent				modelGraphicsEventListener;
	private double							logZeroDose;

	// End of variables declaration

	/**
	 * The default constructor. Not used as no graphics can be created without a set of parameters.
	 */
	public ModelGraphics(ModelGraphicsEvent modelGraphicsEventListener)
	{
		CHART_HEIGHT = 325;
		CHART_WIDTH = 550;
		DATA = 0;
		POWER = 1;
		LINEAR = 2;
		TWODEG = 3;
		THREEDEG = 4;
		LBUFFER = 0.5;
		RBUFFER = 0.5;
		this.modelGraphicsEventListener = modelGraphicsEventListener;
	}

	/**
	 * Class constructor
	 *
	 * @param p
	 *            - the parent BenchmarkDose
	 * @param m
	 *            - the parameters object passed in with required data
	 */
	public ModelGraphics(BMDResult bmdResults, StatResult bestModel,
			ModelGraphicsEvent modelGraphicsEventListener)
	{
		this.bmdResults = bmdResults;
		this.bestModel = bestModel;
		this.modelGraphicsEventListener = modelGraphicsEventListener;
		// put the probes' data accessible via map(responses and statresults)
		mapProbesToData();

		// set the doses.
		doses = new double[bmdResults.getDoseResponseExperiment().getTreatments().size()];
		for (int i = 0; i < bmdResults.getDoseResponseExperiment().getTreatments().size(); i++)
		{
			doses[i] = bmdResults.getDoseResponseExperiment().getTreatments().get(i).getDose();
		}
		oneway = new OnewayAnova();
		oneway.setVariablesXX(0, doses);

		// init const holders
		CHART_HEIGHT = 400;
		CHART_WIDTH = 900;
		NUM_SERIES = 0;
		DATA = 0;
		POWER = 1;
		LINEAR = 2;
		TWODEG = 3;
		THREEDEG = 4;
		LBUFFER = 0.5;
		RBUFFER = 0.5;

		// init holder series
		dataSeries = new XYSeries("Data");
		modelSeries = new XYSeries("Model");
		bmdSeries = new XYSeries("BMD");
		bmdlSeries = new XYSeries("BMDL");
		bmduSeries = new XYSeries("BMDU");

		// set up the holder for all the series
		seriesSet = new XYSeriesCollection(dataSeries);

		// init color array
		// 0: data series color
		// 1: model series color
		// 2: bmd and bmdl series color
		chartColors = new Color[4];
		chartColors[0] = Color.RED;
		chartColors[1] = Color.BLUE;
		chartColors[2] = Color.BLACK;
		chartColors[3] = Color.GREEN;

		// init the chart
		chart = ChartFactory.createXYLineChart("Title", // chart title
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
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.01);
		plot.setRenderer(renderer);

		cP = new ChartPanel(chart);
		cP.setMaximumSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));

		// setAlwaysOnTop(true);

		// init frame components
		initComponents();
		getDataSeries();
		createModels();
		createDataset();
		placeChart();

		// this.closeButton.setVisible(false);
		// getDisplayedPValue();
		// pack();
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
				// double lessthan = decade * .00000001;
				// if (Math.abs(decade - logZeroDose) < lessthan)
				// decade *= .1;
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
		Double[] decades = { .00000000001, .0000000001, .000000001, .00000001, .0000001, .000001, .00001,
				.0001, .001, .01, .1, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 10000000.0,
				100000000.0 };
		for (int i = 1; i < decades.length; i++)
		{
			double decade = decades[i];
			double lessthan = decade * .0001;
			if (Math.abs(decade - logZeroDose) < lessthan)
			{
				return decades[i];
			}
			else if (logZeroDose < decade)
				return decades[i - 1];
		}
		return .000000000001;
	}

	private double maskDose(double dose)
	{
		if (logDoseAxis.isSelected() && dose == 0)
			return logZeroDose;

		return dose;

	}

	/*
	 * This method is called from within the constructor to initialize the form.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Init Code ">
	private void initComponents()
	{
		jPanel1 = new javax.swing.JPanel();
		modelLabel = new javax.swing.JLabel();
		modelField = new javax.swing.JTextField();
		addModelLabel = new javax.swing.JLabel();
		printButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();
		modelBox = new javax.swing.JComboBox();
		probeBox = new javax.swing.JComboBox();
		clearButton = new javax.swing.JButton();
		colorButton = new javax.swing.JButton();
		bmdField = new javax.swing.JTextField();
		bmdLField = new javax.swing.JTextField();
		bmdUField = new javax.swing.JTextField();
		aicField = new javax.swing.JTextField();
		pvalField = new javax.swing.JTextField();
		bmdlLabel = new javax.swing.JLabel();
		bmduLabel = new javax.swing.JLabel();
		bmdLabel = new javax.swing.JLabel();
		aicLabel = new javax.swing.JLabel();
		idLabel = new javax.swing.JLabel();
		pvLabel = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		meanStdBox = new javax.swing.JCheckBox();
		allDataBox = new javax.swing.JCheckBox();
		logDoseAxis = new javax.swing.JCheckBox();
		jSep1 = new javax.swing.JSeparator();

		addModelLabel.setPreferredSize(new Dimension(1, 1));
		idLabel.setPreferredSize(new Dimension(2, 2));
		pvLabel.setPreferredSize(new Dimension(3, 3));
		bmdlLabel.setPreferredSize(new Dimension(4, 4));
		bmduLabel.setPreferredSize(new Dimension(4, 4));
		bmdLabel.setPreferredSize(new Dimension(5, 5));
		aicLabel.setPreferredSize(new Dimension(5, 5));
		modelLabel.setPreferredSize(new Dimension(6, 6));
		modelBox.setPreferredSize(new Dimension(7, 7));
		probeBox.setPreferredSize(new Dimension(8, 8));
		// setTitle("Model Graph");
		// setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setMinimumSize(new java.awt.Dimension(650, 650));
		// setPreferredSize(new java.awt.Dimension(600, 650));

		jPanel1.setBorder(
				javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

		// Set various text labels
		modelLabel.setText("Model");
		addModelLabel.setText("Model Name");
		printButton.setText("Print");
		closeButton.setText("Close");
		clearButton.setText("Clear");
		colorButton.setText("Properties");
		colorButton.setVisible(true);
		bmdlLabel.setText("BMDL");
		bmduLabel.setText("BMDU");
		bmdLabel.setText("BMD");
		aicLabel.setText("AIC");
		idLabel.setText("  ID");
		pvLabel.setText("Fit P");
		allDataBox.setText("All Data");
		meanStdBox.setText("Mean & Standard Deviation");
		logDoseAxis.setText("Logaritmic Dose Axis");
		logDoseAxis.setSelected(false);
		allDataBox.setSelected(true);

		// retrieve models and probe IDs to populate the comboboxes
		modelBox.setModel(new javax.swing.DefaultComboBoxModel<String>(getModelNames()));
		probeBox.setModel(new javax.swing.DefaultComboBoxModel<Probe>(getProbes()));
		modelBox.setSelectedIndex(0);
		probeBox.setSelectedIndex(0);

		// add listeners to buttons and comboboxes
		buttonReader();
		comboBoxReader();

		// Set up the panel to display charts
		jPanel2.setSize(CHART_WIDTH, CHART_HEIGHT);
		// jPanel2.add(cP);
		jSep1.setOrientation(javax.swing.SwingConstants.VERTICAL);

		// Setup for the frame layouts
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(bmdlLabel).addComponent(modelLabel))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel1Layout.createSequentialGroup()
														.addComponent(bmdLField,
																javax.swing.GroupLayout.PREFERRED_SIZE, 140,
																140)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(bmdLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(bmdField,
																javax.swing.GroupLayout.DEFAULT_SIZE, 140,
																140)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(bmduLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(bmdUField,
																javax.swing.GroupLayout.DEFAULT_SIZE, 140,
																140)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(pvLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(pvalField,
																javax.swing.GroupLayout.PREFERRED_SIZE, 140,
																140)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(aicLabel)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(aicField,
																javax.swing.GroupLayout.PREFERRED_SIZE, 140,
																140)
														.addGap(42, 42, 42))
												.addComponent(modelField,
														javax.swing.GroupLayout.DEFAULT_SIZE, 294,
														Short.MAX_VALUE))
										.addGap(13, 13, 13))
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(printButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(clearButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(colorButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(closeButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
						/// <>///
						/*
						 * .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.
						 * LEADING, false) .addComponent(idLabel)
						 * .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						 * jPanel1Layout.createSequentialGroup() .addComponent(addModelLabel,
						 * javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
						 * .addGap(83, 83, 83)) .addGroup(jPanel1Layout.createSequentialGroup() .addGap(10,
						 * 10, 10) .addComponent(probeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
						 * Short.MAX_VALUE)
						 * .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
						 * .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						 * jPanel1Layout.createSequentialGroup() .addGap(10, 10, 10) .addComponent(modelBox,
						 * 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						 * .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
						 */
						/// <>///
						.addGap(10, 10, 10)));

		jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { printButton, closeButton, clearButton, colorButton });
		jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
				new java.awt.Component[] { bmdLField, bmdField, bmdUField });

		jPanel1Layout.setVerticalGroup(jPanel1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
								.addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(modelLabel)
										.addComponent(modelField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(bmdLField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(bmdlLabel))
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(bmdLabel).addComponent(bmdField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(bmdUField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(bmduLabel))
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(pvalField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(pvLabel))
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(aicField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(aicLabel)))))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(printButton).addComponent(clearButton))
										.addGroup(jPanel1Layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(colorButton).addComponent(closeButton))))
						.addContainerGap()));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup().addComponent(cP)));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup().addComponent(cP)));

		jPanel3.setBorder(
				javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		meanStdBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		meanStdBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		allDataBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		allDataBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

		logDoseAxis.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		logDoseAxis.setMargin(new java.awt.Insets(0, 0, 0, 0));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(meanStdBox)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(allDataBox)
						/// <>///
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jSep1, javax.swing.GroupLayout.PREFERRED_SIZE, 12,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(logDoseAxis)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jSep1, javax.swing.GroupLayout.PREFERRED_SIZE, 12,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(addModelLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(modelBox)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(idLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(probeBox)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		/// <>///
		);
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(jPanel3Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(meanStdBox)
										// .addComponent(allDataBox))
										/// <>///
										.addComponent(allDataBox).addComponent(jSep1)
										.addComponent(addModelLabel).addComponent(logDoseAxis)
										.addComponent(modelBox).addComponent(idLabel).addComponent(probeBox))
								/// <>///
								.addContainerGap()));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		// pack();
	}// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc=" Add Listener Code ">
	/**
	 * Adds listeners to the buttons and checkboxes
	 *
	 */
	private void buttonReader()
	{
		// Buttons
		ButtonListener buttonListener = new ButtonListener();
		printButton.addActionListener(buttonListener);
		closeButton.addActionListener(buttonListener);
		clearButton.addActionListener(buttonListener);
		colorButton.addActionListener(buttonListener);
		// checkboxes
		CheckListener checkListener = new CheckListener();
		allDataBox.addActionListener(checkListener);
		meanStdBox.addActionListener(checkListener);
		logDoseAxis.addActionListener(checkListener);
	}

	/**
	 * Adds listeners to the two comboboxes
	 *
	 */
	private void comboBoxReader()
	{
		// comboListener mListener = new comboListener();
		modelBox.addActionListener(new comboListener());
		probeBox.addActionListener(new comboListener());
	}

	/**
	 * Listener class for the checkboxes
	 *
	 */
	public class CheckListener implements ActionListener
	{
		/**
		 * creates a new chart to account for the checkbox changes
		 *
		 * @params evt - the event
		 */
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			// decide which box was checked.
			if ("All Data".equals(evt.getActionCommand()))
			{
				// all data was clicked
				if (allDataBox.isSelected())
				{
					// is now checked, wasn't before
					// set the other box to unchecked
					meanStdBox.setSelected(false);
				}
				else
				{
					// is now unchecked, was before
					// set the other box to checked
					meanStdBox.setSelected(true);
				}
			}
			else if ("Mean & Standard Deviation".equals(evt.getActionCommand()))
			{
				// mean and std dev was checked
				if (meanStdBox.isSelected())
				{
					// is now checked, wasn't before
					// set the other box to unchecked
					allDataBox.setSelected(false);
				}
				else
				{
					// is now unchecked, was before
					// set the other box to checked
					allDataBox.setSelected(true);
				}
			}
			else if (logDoseAxis.getText().equals(evt.getActionCommand()))
			{
				XYPlot plot = (XYPlot) chart.getPlot();
				if (logDoseAxis.isSelected())
				{

					LogAxis logAxis = new CustomJFreeLogAxis();
					double lowRange = firstNonZeroDose(doses);
					logAxis.setRange(new Range(lowRange, doses[doses.length - 1] * 1.1));
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
				else
				{
					NumberAxis axis = new NumberAxis("Dose");
					axis.setRange(doses[0], doses[doses.length - 1]);
					plot.setDomainAxis(axis);
				}
			}

			setColors();
			getDataSeries();
			if (parameters.length > 1)
			{
				createModels();
				createDataset();
			}

		}
	}

	/**
	 * An alternative function above
	 */
	public void setSelectedProbe(Probe probe)
	{
		probeBox.setSelectedItem(probe);
	}

	/**
	 * changes the combobox to the passed in item
	 */
	public void setSelectedModel(String model)
	{
		modelBox.setSelectedItem(model);
	}

	public void setBestModel()
	{
		Probe selectedProbe = (Probe) probeBox.getSelectedItem();

		if (selectedProbe != null)
		{
			ProbeStatResult probeStatResult = this.probeStatResultMap.get(selectedProbe);
			bestModel = probeStatResult.getBestStatResult();
			if (bestModel != null)
				setSelectedModel(probeStatResult.getBestStatResult().toString());
			else
			{
				setSelectedModel(probeStatResult.getStatResults().get(0).toString());
			}
		}
	}

	/**
	 * Listener clas for the comboboxes
	 *
	 */
	class comboListener implements ActionListener
	{
		/**
		 * detects actions performed, and creates a new chart to account for the combobox changes
		 *
		 * @params evt - the event
		 */
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() == probeBox)
			{
				// updateGraph = false; // avoid redraw graph when best model is set
				setBestModel();
				return;
			}

			setColors();
			getDataSeries();
			if (parameters.length > 1)
			{
				createModels();
				createDataset();
			}
		}
	}

	/**
	 * Listener class for the buttons
	 *
	 */
	class ButtonListener implements ActionListener
	{
		/**
		 * detects actions performed
		 *
		 * @params evt - the event
		 */
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			if ("Print".equals(evt.getActionCommand()))
			{

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						cP.createChartPrintJob();

					}
				});

			}
			else if ("Close".equals(evt.getActionCommand()))
			{
				modelGraphicsEventListener.closeModelGraphics();
			}
			else if ("Clear".equals(evt.getActionCommand()))
			{
				// reset the chart's colors
				XYPlot plot = (XYPlot) chart.getPlot();

				// reset colors to defaults
				chartColors[0] = Color.RED;
				chartColors[1] = Color.BLUE;
				chartColors[2] = Color.BLACK;
				// paint the gridlines and the chart background
				plot.setDomainGridlinePaint(new Color(192, 192, 192));
				plot.setRangeGridlinePaint(new Color(192, 192, 192));
				plot.setOutlinePaint(new Color(128, 128, 128));

				plot.setBackgroundPaint(Color.WHITE);
				plot.setOrientation(PlotOrientation.VERTICAL);
				plot.getDomainAxis().setLabelPaint(Color.BLACK);
				plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
				plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
				plot.getDomainAxis().setTickLabelsVisible(true);
				plot.getDomainAxis().setTickMarksVisible(true);
				plot.getDomainAxis().setLabel("Dose");
				plot.getRangeAxis().setLabelPaint(Color.BLACK);
				plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
				plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
				plot.getRangeAxis().setTickLabelsVisible(true);
				plot.getRangeAxis().setTickMarksVisible(true);
				plot.getRangeAxis().setLabel("Log(Expression)");

				chart.setBackgroundPaint(new Color(238, 238, 238));
				// now reset the titles, etc
				String modelName = (String) modelBox.getSelectedItem();
				chart.setTitle(modelName);
				chart.getTitle().setPaint(Color.BLACK);
				chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

				// getDataSeries();
				// createModels();
				// createDataset();
				createChart(null);
			}
			else if ("Properties".equals(evt.getActionCommand()))
			{

				ChartEditor editor = ChartEditorManager.getChartEditor(cP.getChart());

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						JFrame frmOpt = new JFrame();
						frmOpt.setVisible(false);
						frmOpt.setLocation(cP.getX(), cP.getY());
						frmOpt.setAlwaysOnTop(true);

						int result = JOptionPane.showConfirmDialog(frmOpt, editor, "Chart Properties",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (result == JOptionPane.OK_OPTION)
						{
							editor.updateChart(cP.getChart());
						}
						frmOpt.dispose();

					}
				});

			}
		}

	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc=" ModelParameters Manipulation ">
	// public void changeMP(BMDMatrixData d)
	// {
	// // set the new modelParams var
	// bmdResults = d;
	// modelParams = d.getMP();
	// // set the comboboxes
	// modelBox.setModel(new javax.swing.DefaultComboBoxModel(modelParams.modelNames()));
	// probeBox.setModel(new javax.swing.DefaultComboBoxModel(modelParams.idNames()));
	// // now change the other set vars
	// oneway = new OnewayAnova();
	// oneway.setVariablesXX(0, doses);
	//
	// // do these last to trigger the listeners
	// modelBox.setSelectedIndex(0);
	// probeBox.setSelectedIndex(0);
	// }

	/**
	 * Sets the data series for the chart based on the model and probe that are selected in the comboboxes
	 *
	 */
	private void getDataSeries()
	{
		// Variables to hold the chart plot, combobox items,
		// model parameters and responses
		double lastDose, mean, stdD;
		int holder, counter, counter2;
		Vector sv = new Vector();
		XYSeries sh;// = new XYSeries("Data");
		XYPlot plot = (XYPlot) chart.getPlot();
		Probe probe = (Probe) probeBox.getSelectedItem();
		String name = (String) modelBox.getSelectedItem();
		double[] responses = this.probeResponseMap.get(probe);
		parameters = getParameters(probe, name);
		parameters = truncateDecimal(parameters);
		dataSeries = new XYSeries("Data");
		NUM_SERIES = 0;

		// perform onewayANOVA on the responses provided by the modelParams var
		oneway.onewayANOVA(responses);
		double[][] estimates = oneway.estimates();
		// set up the bmdModel for use
		bmdModel = new BMDoseModel(name, probe);
		bmdModel.setParameters(parameters);
		bmdModel.setEstimates(estimates);

		if (allDataBox.isSelected())
		{
			// if true, show all the data
			// doses[X] corresponds to responses[X]
			HIGH = responses[0];
			LOW = responses[0];
			for (counter = 0; counter < doses.length; counter++)
			{
				dataSeries.add(maskDose(doses[counter]), responses[counter]);
				if (responses[counter] > HIGH)
				{
					HIGH = responses[counter];
				}
				else if (responses[counter] < LOW)
				{
					LOW = responses[counter];
				}
			}
			NUM_SERIES = 1;
			seriesSet = new XYSeriesCollection(dataSeries);
			plot.setRenderer(new XYLineAndShapeRenderer(true, false) {

				@Override
				public Shape getItemShape(int row, int col)
				{
					Shape rectangle = new Rectangle(-3, -3, 6, 6);
					return rectangle;
				}

				@Override
				public Shape getLegendShape(int series)
				{

					Shape rectangle = new Rectangle(-3, -3, 6, 6);
					return rectangle;
				}
			});
		}
		else
		{
			// show the mean and std to console
			// org.jfree.data.statistics.Statistics.
			HIGH = responses[0];
			LOW = responses[0];
			lastDose = doses[0];
			holder = 0;
			for (counter = 0; counter < doses.length; counter++)
			{
				if (lastDose != doses[counter])
				{
					lastDose = doses[counter];
					Double[] dr = new Double[(counter - holder)];
					Double[] dr2 = new Double[(counter - holder)];
					for (counter2 = holder; counter2 < counter; counter2++)
					{
						// get responses
						dr[(counter2 - holder)] = responses[counter2];
					}
					// get mean and std dev
					mean = org.jfree.data.statistics.Statistics.calculateMean(dr);
					stdD = org.jfree.data.statistics.Statistics.getStdDev(dr);

					// make series
					sh = new XYSeries("Data" + String.valueOf(counter));
					sh.add(maskDose(doses[counter - 1]), mean); // add mean
					sh.add(maskDose(doses[counter - 1]), (mean + stdD));// add Standard Deviation
					sh.add(maskDose(doses[counter - 1]), (mean - stdD));
					// add the series to the vector
					sv.add(sh);
					// set LOW and HIGH
					if ((mean - stdD) < LOW)
					{
						LOW = mean - stdD;
					}
					if ((mean + stdD) > HIGH)
					{
						HIGH = mean + stdD;
					}
					// set holder
					holder = counter;
				}
			}
			// Run in the last data set
			Double[] dr = new Double[(counter - holder)];
			Double[] dr2 = new Double[(counter - holder)];
			for (counter2 = holder; counter2 < counter; counter2++)
			{
				// get responses
				dr[(counter2 - holder)] = responses[counter2];
			}
			// get mean and std dev
			mean = org.jfree.data.statistics.Statistics.calculateMean(dr);
			stdD = org.jfree.data.statistics.Statistics.getStdDev(dr);

			// make series
			sh = new XYSeries("MoreData");
			sh.add(maskDose(doses[counter - 1]), mean); // add mean
			sh.add(maskDose(doses[counter - 1]), (mean + stdD));// add Standard Deviation
			sh.add(maskDose(doses[counter - 1]), (mean - stdD));

			// add the series to the vector
			sv.add(sh);
			// set LOW and HIGH
			if ((mean - stdD) < LOW)
			{
				LOW = mean - stdD;
			}
			if ((mean + stdD) > HIGH)
			{
				HIGH = mean + stdD;
			}

			XYItemRenderer renderer = plot.getRenderer();

			// set the base data sets in the seriesSet collection
			seriesSet = new XYSeriesCollection();
			NUM_SERIES = sv.size();
			for (Enumeration e = sv.elements(); e.hasMoreElements();)
			{
				// add all the series into the dataset
				seriesSet.addSeries((XYSeries) e.nextElement());
			}

			plot.setRenderer(new XYLineAndShapeRenderer(true, false) {

				@Override
				public Shape getItemShape(int row, int col)
				{
					if (col == 1)
					{
						Shape rectangle = new Rectangle(-4, 0, 8, 1);
						return rectangle;
					}
					else if (col == 2)
					{
						Shape rectangle = new Rectangle(-4, 0, 8, 1);
						return rectangle;
					}
					else
					{
						Shape rectangle = new Rectangle(-4, -4, 8, 8);
						return rectangle;
					}
				}

				@Override
				public Shape getLegendShape(int series)
				{

					Shape rectangle = new Rectangle(-4, -4, 8, 8);
					return rectangle;
				}

			});
		}
	}

	/**
	 * make a copy of the data applying decision without changing original
	 */
	private double[] truncateDecimal(double[] inputs)
	{
		double[] outputs = new double[inputs.length];

		for (int i = 0; i < inputs.length; i++)
		{
			outputs[i] = NumberManager.numberFormat(6, inputs[i]);;
		}

		return outputs;
	}

	/**
	 * stores sample datapoints for the chosen model to use for display Also sets up the BMD for display
	 *
	 */
	private void createModels()
	{
		// low and high doses, to set model sample boundries
		double minDose = bmdModel.minimumDose();
		double maxDose = bmdModel.maximumDose();

		// create new series
		modelSeries = new XYSeries("Model");
		bmdSeries = new XYSeries("BMD");
		bmdlSeries = new XYSeries("BMDL");
		bmduSeries = new XYSeries("BMDU");
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
			double increment = (dose - prevDose) / 1000.0;
			if (increment > .05 && prevDose < 10.0)
				increment = .05;
			for (double counter = prevDose; counter < dose; counter += increment)
				modelSeries.add(counter, bmdModel.response(counter));
			prevDose = dose;
		}

		// Set up BMD and BMDL and BMDU
		if (parameters[0] >= minDose && parameters[0] <= maxDose)
		{
			bmdSeries.add(parameters[0], bmdModel.response(parameters[0]));
			bmdSeries.add(parameters[0], LOW - .01);
		}

		double smallestDose = 0.0;
		if (logDoseAxis.isSelected())
			smallestDose = logZeroDose;
		else
			smallestDose = minDose;

		bmdSeries.add(smallestDose, bmdModel.response(parameters[0]));

		if (parameters[1] >= minDose && parameters[1] <= maxDose)
		{
			bmdlSeries.add(parameters[1], bmdModel.response(parameters[0]));
			bmdlSeries.add(parameters[1], LOW - .01);
		}
		if (parameters[2] >= minDose && parameters[2] <= maxDose && parameters[2] > 0.0)
		{
			bmduSeries.add(parameters[2], bmdModel.response(parameters[0]));
			bmduSeries.add(parameters[2], LOW - .01);
		}
		bmduSeries.add(parameters[0], bmdModel.response(parameters[0]));
	}

	/**
	 * sets the colors used to color the chart series
	 *
	 */
	private void setColors()
	{
		XYPlot plot = (XYPlot) chart.getPlot();
		int count = plot.getSeriesCount();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		// data series color
		chartColors[0] = (Color) renderer.getSeriesPaint(0);
		// model series color
		chartColors[1] = (Color) renderer.getSeriesPaint(count - 3);
		// bmd series color
		chartColors[2] = (Color) renderer.getSeriesPaint(count - 2);
	}

	/**
	 * sets the various textfields to display the used model, bmd, and bmdl
	 *
	 * @ params model - specifies the name of model used
	 */
	private void setDisplays(String model)
	{
		// sets the textFields based on the selected model
		// DecimalFormat mF = new DecimalFormat("#####0.000000");
		// DecimalFormat bmdF = new DecimalFormat("#####0.000000");
		chart.setTitle(model);
		StringBuilder formula = new StringBuilder("RESPONSE = " + parameters[4]);

		// if(model == POWER){
		if (model.equals("Power"))
		{
			// displaying a power model
			// chart.setTitle("Power");

			if (parameters[5] >= 0)
			{
				// the parameter is positive, so set display with a +
				formula.append(" + " + parameters[5] + " * DOSE^");
			}
			else
			{
				// the parameter is negative, set display with a -
				formula.append(" " + parameters[5] + " * DOSE^");
			}

			if (parameters[6] >= 0)
			{
				formula.append(parameters[6]);
			}
			else
			{
				formula.append("(" + parameters[6] + ")");
			}
		}
		else if (model.equals("Hill"))
		{
			// Y[dose] = intercept + v*dose^n/(k^n + dose^n)
			if (parameters[5] >= 0)
			{
				formula.append(" + " + parameters[5] + " * DOSE^");
			}
			else
			{
				formula.append(" " + parameters[5] + " * DOSE^");
			}

			String param5 = Double.toString(parameters[6]);

			if (parameters[6] < 0)
			{
				param5 = "(" + parameters[6] + ")";
			}

			formula.append(param5 + "/(");

			if (parameters[7] >= 0)
			{
				formula.append(parameters[7] + "^" + param5 + " + DOSE^" + param5 + ")");
			}
			else
			{
				formula.append("(" + parameters[7] + ")^" + param5 + " + DOSE^" + param5 + ")");
			}
		}
		else if (model.equals("Exp 2"))
		{
			formula = new StringBuilder("RESPONSE = ");
			double a = parameters[6];
			double b = parameters[7];
			formula.append(a + " * EXP(" + b + " * DOSE)");
		}
		else if (model.equals("Exp 3"))
		{
			formula = new StringBuilder("RESPONSE = ");
			double a = parameters[6];
			double b = parameters[7];
			double d = parameters[8];
			formula.append(a + " * EXP((" + b + " * DOSE)^" + d + ")");
		}
		else if (model.equals("Exp 4"))
		{
			formula = new StringBuilder("RESPONSE = ");
			double a = parameters[6];
			double b = parameters[7];
			double c = parameters[8];
			formula.append(a + " * (" + c + " - (" + c + " - 1) * EXP(-" + b + " * DOSE)");
		}
		else if (model.equals("Exp 5"))
		{
			formula = new StringBuilder("RESPONSE = ");
			double a = parameters[6];
			double b = parameters[7];
			double c = parameters[8];
			double d = parameters[9];
			formula.append(a + " * (" + c + " - (" + c + " - 1) * EXP((-" + b + " * DOSE)^" + d + ")");
		}
		else
		{ // Linear (1�) and Polinominal d�
			Pattern pattern = Pattern.compile("(\\d+)");
			Matcher matcher = pattern.matcher(model);

			if (matcher.find())
			{
				String st = matcher.group(1);
				int degree = Integer.parseInt(st); // degree = 1, 2, 3...
				int index = 5;

				for (int i = 1; i <= degree; i++)
				{
					if (parameters[index] >= 0)
					{ // positive
						formula.append(" + " + parameters[index] + " * DOSE");
					}
					else
					{ // negative
						formula.append(" " + parameters[index] + " * DOSE");
					}

					if (i > 1)
					{
						formula.append("^" + i);
					}

					index += 1;
				}
			}
		}

		modelField.setText(bmdModel.getModelEquation(model));// formula.toString());
		bmdField.setText(Double.toString(parameters[0]));
		bmdLField.setText(Double.toString(parameters[1]));
		bmdUField.setText(Double.toString(parameters[2]));
		pvalField.setText(Double.toString(parameters[3]));// getDisplayedPValue()
		aicField.setText(Double.toString(parameters[4]));

	}

	/**
	 * used by BenchmarkDose to change the displayed probe based on user selection
	 *
	 * @params probe - the selected probe to graph
	 */
	public void setSelectedProbe(String probe)
	{
		// changes the combobox to the selected item
		probeBox.setSelectedItem(probe);
		getDataSeries();
	}

	private void placeChart()
	{
		// Point location;
		// Dimension winSize = getSize();
		//
		// location = parent.getLocation();
		// Dimension parentSize = parent.getSize();
		//
		// if ((winSize.height > parentSize.height + 50) && (winSize.width > parentSize.width + 50))
		// {
		//
		// // dialog would cover parent, so offset rather than center.
		// location.x += 25;
		// location.y += 25;
		// }
		// else
		// {
		// // Center dialog over the parent.
		// location.x += (parentSize.width - winSize.width) / 2;
		// location.y += (parentSize.height - winSize.height) / 2;
		// }
		//
		// setLocation(location);
	}

	/**
	 * modifies the chart plot, displays, and renderer based on selections
	 *
	 * @params model - indicates the model so displays can be set correctly
	 */
	private void createChart(String model)
	{
		// chart =
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		//////////// CHART CUSTOMIZATION///////////
		// set titles and displays
		if (model != null)
		{
			setDisplays(model);
		}

		// set dataset
		plot.setDataset(seriesSet);
		// Painting
		if (allDataBox.isSelected())
		{
			// just showing datapoints
			// a single dataseries is present
			renderer.setSeriesPaint(0, chartColors[0]);
			renderer.setSeriesLinesVisible(0, false);
			renderer.setSeriesShapesVisible(0, true);
			// options for the model set
			renderer.setSeriesPaint(1, chartColors[1]);
			renderer.setSeriesShapesVisible(1, false);
			renderer.setSeriesVisibleInLegend(1, true);
			// options for the BMD set
			renderer.setSeriesPaint(2, chartColors[2]);
			renderer.setSeriesShapesVisible(2, false);
			renderer.setSeriesVisibleInLegend(2, false);
			// options for the BMDL set
			renderer.setSeriesPaint(3, chartColors[2]);
			renderer.setSeriesShapesVisible(3, false);
			renderer.setSeriesVisibleInLegend(3, false);

			// options for the BMDU set
			renderer.setSeriesPaint(4, chartColors[3]);
			renderer.setSeriesShapesVisible(4, false);
			renderer.setSeriesVisibleInLegend(4, false);
		}
		else
		{
			// displaying mean and std sets
			// one dataset exists for each dose value
			// paint all the same, but display a single one in the legend
			renderer.setSeriesPaint(0, chartColors[0]);
			renderer.setSeriesShapesVisible(0, true);
			renderer.setSeriesLinesVisible(0, true);
			for (int counter = 1; counter < NUM_SERIES; counter++)
			{
				// set the dataseries paint
				renderer.setSeriesPaint(counter, chartColors[0]);
				renderer.setSeriesShape(counter, renderer.getSeriesShape(0));
				renderer.setSeriesShapesVisible(counter, true);
				renderer.setSeriesLinesVisible(counter, true);
				// set renderer options
				renderer.setSeriesVisibleInLegend(counter, false);
			}
			// the last three series are the Model, BMD, and BMDL, in that order
			renderer.setSeriesPaint(NUM_SERIES, chartColors[1]);
			renderer.setSeriesShapesVisible(NUM_SERIES, false);

			// BMD
			renderer.setSeriesPaint(NUM_SERIES + 1, chartColors[2]);
			renderer.setSeriesVisibleInLegend(NUM_SERIES + 1, false);
			renderer.setSeriesShapesVisible(NUM_SERIES + 1, false);
			// BMDL
			renderer.setSeriesPaint(NUM_SERIES + 2, chartColors[2]);
			renderer.setSeriesVisibleInLegend(NUM_SERIES + 2, false);
			renderer.setSeriesShapesVisible(NUM_SERIES + 2, false);

			// BMDU
			renderer.setSeriesPaint(NUM_SERIES + 3, chartColors[3]);
			renderer.setSeriesVisibleInLegend(NUM_SERIES + 3, false);
			renderer.setSeriesShapesVisible(NUM_SERIES + 3, false);
		}
	}

	/**
	 * creates a new series collection and adds model and bmd series to the collection for display. Calls
	 * createChart to set colors and display once the series collection has been updated.
	 *
	 */
	private void createDataset()
	{
		// seriesSet = new XYSeriesCollection(dataSeries);
		// hold the selected combobox item index
		String modelName = (String) modelBox.getSelectedItem();

		// data series have been added, adds the models
		seriesSet.addSeries(modelSeries);
		seriesSet.addSeries(bmdSeries);
		seriesSet.addSeries(bmdlSeries);
		seriesSet.addSeries(bmduSeries);
		createChart(modelName);
	}

	/**
	 * Assigned by MatrixData.getName()
	 */
	public void setSrcName(String name)
	{
		srcName = name;
	}

	public String getSrcName()
	{
		return srcName;
	}

	/*
	 * get a list of model names that are used in the bmdResults anlaysis
	 */
	private String[] getModelNames()
	{
		String[] modelNames = new String[bmdResults.getProbeStatResults().get(0).getStatResults().size()];
		int i = 0;
		for (StatResult statResult : bmdResults.getProbeStatResults().get(0).getStatResults())
		{
			modelNames[i] = statResult.toString();
			i++;
		}
		return modelNames;
	}

	/*
	 * get a list of probes that are used in the bmdAnalysis
	 */
	private Probe[] getProbes()
	{
		Probe[] probes = new Probe[bmdResults.getProbeStatResults().size()];
		int i = 0;
		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			probes[i] = probeStatResult.getProbeResponse().getProbe();
			i++;
		}
		return probes;
	}

	/*
	 * popuplate maps from probes to probe responses and from probes to stat results
	 */
	private void mapProbesToData()
	{
		this.probeStatResultMap = new HashMap<>();
		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			probeStatResultMap.put(probeStatResult.getProbeResponse().getProbe(), probeStatResult);
		}

		this.probeResponseMap = new HashMap<>();
		for (ProbeResponse probeResponse : bmdResults.getDoseResponseExperiment().getProbeResponses())
		{
			if (!probeStatResultMap.containsKey(probeResponse.getProbe()))
				continue;

			double[] responseDoubles = new double[probeResponse.getResponseArray().length];
			for (int i = 0; i < probeResponse.getResponseArray().length; i++)
			{
				responseDoubles[i] = probeResponse.getResponseArray()[i];
			}
			probeResponseMap.put(probeResponse.getProbe(), responseDoubles);

		}
	}

	/*
	 * get the parameters of a given probe and the model name
	 */
	private double[] getParameters(Probe probe, String name)
	{
		ProbeStatResult probeStatResult = this.probeStatResultMap.get(probe);
		StatResult theStatResult = null;

		for (StatResult statResult : probeStatResult.getStatResults())
		{
			if (statResult.toString().equals(name))
			{
				theStatResult = statResult;
				break;
			}
		}

		if (theStatResult.getCurveParameters() != null)
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

}
