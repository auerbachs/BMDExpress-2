package com.sciome.bmdexpress2.mvp.view.mainstage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.GCurvePResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.CurveFitPresenter;
import com.sciome.bmdexpress2.mvp.view.BMDExpressViewBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.ICurveFitView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.prefilter.OnewayAnova;
import com.sciome.bmdexpress2.util.visualizations.curvefit.BMDoseModel;
import com.sciome.charts.jfree.CustomJFreeLogAxis;
import com.sciome.charts.jfree.SciomeChartViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CurveFitView extends BMDExpressViewBase implements ICurveFitView, Initializable
{
	@FXML
	private HBox						chartBox;
	@FXML
	private CheckBox					meanAndDeviationCheckBox;
	@FXML
	private CheckBox					logDosesCheckBox;
	@FXML
	private ComboBox					modelNameComboBox;
	@FXML
	private ComboBox					idComboBox;
	@FXML
	private TextField					modelTextField;
	@FXML
	private TextField					bmdlTextField;
	@FXML
	private TextField					bmdTextField;
	@FXML
	private TextField					bmduTextField;
	@FXML
	private TextField					fitPTextField;
	@FXML
	private TextField					aicTextField;
	@FXML
	private Button						printButton;
	@FXML
	private Button						clearButton;
	@FXML
	private Button						propertiesButton;
	@FXML
	private Button						closeButton;

	private BMDResult					bmdResults;					// the matrix of data from the parent
																	// analyis
	private OnewayAnova					oneway;						//
	private BMDoseModel					bmdModel;					//
	private StatResult					bestModel;

	private String						srcName;

	private double[]					doses;						// read in doses
	private double[]					responses;					// holds responses
	private double[]					parameters;					// read in parameters BMD, BMDL, BMDU,
																	// pValue...

	private XYSeries					dataSeries;					// holds the raw data
	private XYSeries					modelSeries;				// holds the model
	private XYSeries					bmdSeries;					// holds the BMD drawing setup
	private XYSeries					bmdlSeries;					// holds the BMDL drawing setup
	private XYSeries					bmduSeries;					// holds the BMDU drawing setup
	private XYSeries					noelSeries;
	private XYSeries					loelSeries;
	private XYSeriesCollection			seriesSet;					// holds the set of series currently
																	// displayed

	private Color[]						chartColors;				// holds the colors for various chart
																	// components

	private JFreeChart					chart;						// the displayed chart
	private SciomeChartViewer			cP;							// the panel for the chart

	private int							NUM_SERIES;					// holds the number of data series

	private int							CHART_WIDTH		= 9000;		// fill up as much space as it can
	private int							CHART_HEIGHT	= 9000;		// fill up as much space as it can

	private double						HIGH;						// holds the high y value
	private double						LOW;						// holds the low y value

	private Map<Probe, double[]>		probeResponseMap;
	private Map<Probe, ProbeStatResult>	probeStatResultMap;
	private double						logZeroDose;
	CurveFitPresenter					presenter;
	private StatResult					theStatResult;

	public CurveFitView()
	{
		this(BMDExpressEventBus.getInstance());
	}

	public CurveFitView(BMDExpressEventBus eventBus)
	{
		presenter = new CurveFitPresenter(this, eventBus);
	}

	public void initData(BMDResult bmdResult, ProbeStatResult probeStatResult)
	{
		this.bmdResults = bmdResult;
		logDosesCheckBox.setSelected(true);
		meanAndDeviationCheckBox.setSelected(true);
		setSelectedProbe(probeStatResult.getProbeResponse().getProbe());
		if (probeStatResult.getBestStatResult() != null)
			setSelectedModel(probeStatResult.getBestStatResult().toString());
		else
			setSelectedModel(probeStatResult.getStatResults().get(0).toString());

		mapProbesToData();

		// set the doses.
		doses = new double[bmdResults.getDoseResponseExperiment().getTreatments().size()];
		for (int i = 0; i < bmdResults.getDoseResponseExperiment().getTreatments().size(); i++)
		{
			doses[i] = bmdResults.getDoseResponseExperiment().getTreatments().get(i).getDose();
		}
		oneway = new OnewayAnova();
		oneway.setVariablesXX(0, doses);

		// init holder series
		dataSeries = new XYSeries("Data");
		modelSeries = new XYSeries("Model");
		bmdSeries = new XYSeries("BMD");
		bmdlSeries = new XYSeries("BMDL");
		bmduSeries = new XYSeries("BMDU");
		noelSeries = new XYSeries("NOTEL");
		loelSeries = new XYSeries("LOTEL");

		// set up the holder for all the series
		seriesSet = new XYSeriesCollection(dataSeries);

		// init color array
		// 0: data series color
		// 1: model series color
		// 2: bmd and bmdl series color
		chartColors = new Color[5];
		chartColors[0] = Color.RED;
		chartColors[1] = Color.BLUE;
		chartColors[2] = Color.BLACK;
		chartColors[3] = Color.GREEN;
		chartColors[4] = Color.orange;

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
		plot.setRenderer(0, renderer);

		cP = new SciomeChartViewer(chart, CHART_WIDTH, CHART_HEIGHT);

		initComponents();
		getDataSeries();
		updateGraphs();

		chartBox.getChildren().addAll(cP);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
	}

	@Override
	public void closeWindow()
	{
		Stage stage = (Stage) meanAndDeviationCheckBox.getScene().getWindow();
		this.close();
		stage.close();
	}

	@Override
	public void close()
	{
		if (presenter != null)
			presenter.destroy();
	}

	public void handle_printButtonPressed(ActionEvent event)
	{
		// NOT IMPLEMENTED YET
	}

	public void handle_clearButtonPressed(ActionEvent event)
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
		String modelName = (String) modelNameComboBox.getSelectionModel().getSelectedItem();
		chart.setTitle(modelName);
		chart.getTitle().setPaint(Color.BLACK);
		chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));
		createChart(null);
	}

	public void handle_propertiesButtonPressed(ActionEvent event)
	{
		// NOT IMPLEMENTED
	}

	public void handle_closeButtonPressed(ActionEvent event)
	{
		this.closeWindow();
	}

	public void handle_logAxesChecked(ActionEvent event)
	{
		updateGraphs();

	}

	public void handle_meanAndDeviationChecked(ActionEvent event)
	{
		updateGraphs();
	}

	public void setBestModel()
	{
		Probe selectedProbe = (Probe) idComboBox.getSelectionModel().getSelectedItem();

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

	/*
	 * populate maps from probes to probe responses and from probes to stat results
	 */
	private void mapProbesToData()
	{
		this.probeStatResultMap = new HashMap<>();
		if (bmdResults == null)
			return;
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

	private void initComponents()
	{
		// retrieve models and probe IDs to populate the comboboxes
		modelNameComboBox.getItems().addAll(getModelNames());
		idComboBox.getItems().addAll(getProbes());

		logDosesCheckBox.setSelected(true);

		idComboBox.valueProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object t1, Object t2)
			{
				setBestModel();
				updateGraphs();
			}
		});

		modelNameComboBox.valueProperty().addListener(new ChangeListener() {

			@Override
			public void changed(ObservableValue ov, Object t1, Object t2)
			{
				updateGraphs();
			}

		});
	}

	private void updateGraphs()
	{
		XYPlot plot = (XYPlot) chart.getPlot();
		if (logDosesCheckBox.isSelected())
		{
			LogAxis logAxis = new CustomJFreeLogAxis("Dose");
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

		setColors();
		getDataSeries();
		if (parameters.length > 1)
		{
			createModels();
			createDataset();
		}
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

				double decadeBelowLow = firstdDecadeBelow(logZeroDose);
				logZeroDose = decadeBelowLow;
				return decadeBelowLow * .9;
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
				return decades[i - 1];
			else if (logZeroDose < decade)
				return decades[i - 1];
		}
		return .000000000001;
	}

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
		Probe probe = (Probe) idComboBox.getSelectionModel().getSelectedItem();
		String name = (String) modelNameComboBox.getSelectionModel().getSelectedItem();
		double[] responses = this.probeResponseMap.get(probe);
		setParameters(probe, name);
		dataSeries = new XYSeries("Data");
		NUM_SERIES = 0;

		// perform onewayANOVA on the responses provided by the modelParams var
		oneway.onewayANOVA(responses);
		double[][] estimates = oneway.estimates();
		// set up the bmdModel for use
		bmdModel = new BMDoseModel(name, probe);
		bmdModel.setParameters(parameters);
		bmdModel.setEstimates(estimates);

		if (!meanAndDeviationCheckBox.isSelected())
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

			XYItemRenderer renderer = plot.getRenderer(0);

			// set the base data sets in the seriesSet collection
			seriesSet = new XYSeriesCollection();
			NUM_SERIES = sv.size();
			for (Enumeration e = sv.elements(); e.hasMoreElements();)
			{
				// add all the series into the dataset
				seriesSet.addSeries((XYSeries) e.nextElement());
			}

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
	 */
	private void createModels()
	{
		// low and high doses, to set model sample boundries
		double minDose = bmdModel.minimumDose();
		double maxDose = bmdModel.maximumDose();

		if (bmdModel.getName().equalsIgnoreCase("gcurvep"))
		{
			showNonParametricCurve();
			return;
		}

		// create new series
		modelSeries = new XYSeries("Model");
		bmdSeries = new XYSeries("BMD");
		bmdlSeries = new XYSeries("BMDL");
		bmduSeries = new XYSeries("BMDU");
		noelSeries = new XYSeries("NOTEL");
		loelSeries = new XYSeries("LOTEL");

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
		if (logDosesCheckBox.isSelected())
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

		Probe probe = (Probe) idComboBox.getSelectionModel().getSelectedItem();
		String name = (String) modelNameComboBox.getSelectionModel().getSelectedItem();
		ProbeStatResult probeStatResult = this.probeStatResultMap.get(probe);
		if (probeStatResult != null && probeStatResult.getPrefilterNoel() != null)
			noelSeries.add(probeStatResult.getPrefilterNoel().doubleValue(),
					bmdModel.response(probeStatResult.getPrefilterNoel().doubleValue()));
		if (probeStatResult != null && probeStatResult.getPrefilterLoel() != null)
			loelSeries.add(probeStatResult.getPrefilterLoel().doubleValue(),
					bmdModel.response(probeStatResult.getPrefilterLoel().doubleValue()));

	}

	/**
	 * sets the colors used to color the chart series
	 *
	 */
	private void setColors()
	{
		XYPlot plot = (XYPlot) chart.getPlot();
		int count = plot.getSeriesCount();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);

		// data series color
		// chartColors[0] = (Color) renderer.getSeriesPaint(0);
		// model series color
		// chartColors[1] = (Color) renderer.getSeriesPaint(1);
		// bmd series color
		// chartColors[2] = (Color) renderer.getSeriesPaint(2);
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

		modelTextField.setText(bmdModel.getModelEquation(model));// formula.toString());
		bmdTextField.setText(Double.toString(parameters[0]));
		bmdlTextField.setText(Double.toString(parameters[1]));
		bmduTextField.setText(Double.toString(parameters[2]));
		fitPTextField.setText(Double.toString(parameters[3]));// getDisplayedPValue()
		aicTextField.setText(Double.toString(parameters[4]));
	}

	private double maskDose(double dose)
	{
		if (logDosesCheckBox.isSelected() && dose == 0)
			return logZeroDose;

		return dose;

	}

	/*
	 * get the parameters of a given probe and the model name
	 */
	private void setParameters(Probe probe, String name)
	{
		ProbeStatResult probeStatResult = this.probeStatResultMap.get(probe);
		theStatResult = null;

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

			this.parameters = truncateDecimal(parameters);
		}
		else
		{
			double parameters[] = new double[6];
			parameters[0] = theStatResult.getBMD();
			parameters[1] = theStatResult.getBMDL();
			parameters[2] = theStatResult.getBMDU();
			parameters[3] = theStatResult.getFitPValue();
			parameters[4] = theStatResult.getAIC();
			parameters[5] = theStatResult.getFitLogLikelihood();
			this.parameters = truncateDecimal(parameters);
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
		String modelName = (String) modelNameComboBox.getSelectionModel().getSelectedItem();

		// data series have been added, adds the models
		seriesSet.addSeries(modelSeries);
		seriesSet.addSeries(bmdSeries);
		seriesSet.addSeries(bmdlSeries);
		seriesSet.addSeries(bmduSeries);
		seriesSet.addSeries(noelSeries);
		seriesSet.addSeries(loelSeries);
		createChart(modelName);
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
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);

		//////////// CHART CUSTOMIZATION///////////
		// set titles and displays
		if (model != null)
		{
			setDisplays(model);
		}

		// set dataset
		plot.setDataset(0, seriesSet);
		// Painting
		if (!meanAndDeviationCheckBox.isSelected())
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

			// options for the NOEL/LOEL set
			renderer.setSeriesPaint(5, chartColors[4]);
			renderer.setSeriesShapesVisible(5, true);
			renderer.setSeriesVisibleInLegend(5, true);
			renderer.setSeriesShape(5, ShapeUtils.createDownTriangle(8.0f));

			renderer.setSeriesPaint(6, chartColors[4]);
			renderer.setSeriesShapesVisible(6, true);
			renderer.setSeriesVisibleInLegend(6, true);
			renderer.setSeriesShape(6, ShapeUtils.createDiamond(8.0f));

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
			// options for the NOEL/LOEL set
			renderer.setSeriesPaint(NUM_SERIES + 4, chartColors[4]);
			renderer.setSeriesShapesVisible(NUM_SERIES + 4, true);
			renderer.setSeriesVisibleInLegend(NUM_SERIES + 4, true);
			renderer.setSeriesLinesVisible(NUM_SERIES + 4, false);
			renderer.setSeriesShape(NUM_SERIES + 4, ShapeUtils.createDownTriangle(8.0f));

			renderer.setSeriesPaint(NUM_SERIES + 5, chartColors[4]);
			renderer.setSeriesShapesVisible(NUM_SERIES + 5, true);
			renderer.setSeriesVisibleInLegend(NUM_SERIES + 5, true);
			renderer.setSeriesLinesVisible(NUM_SERIES + 5, false);
			renderer.setSeriesShape(NUM_SERIES + 5, ShapeUtils.createDiamond(8.0f));
		}
	}

	public void setSelectedProbe(Probe probe)
	{
		idComboBox.getSelectionModel().select(probe);
	}

	/**
	 * changes the combobox to the passed in item
	 */
	public void setSelectedModel(String model)
	{
		modelNameComboBox.getSelectionModel().select(model);
	}

	/**
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

	/**
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
	 * for non parametric fit, let's add a different way to view the curve
	 */
	private void showNonParametricCurve()
	{

		// Variables to hold the chart plot, combobox items,
		// model parameters and responses

		XYSeriesCollection meanSeriesSet = new XYSeriesCollection();
		XYSeriesCollection medianSeriesSet = new XYSeriesCollection();
		double lastDose, mean, stdD, median;
		int holder, counter, counter2;
		XYSeries meanPlusSD = new XYSeries("Mean + SD Curve");
		XYSeries meanMinusSD = new XYSeries("Mean - SD Curve ");
		XYSeries meanSeries = new XYSeries("Mean Curve");
		XYSeries medianSeries = new XYSeries("Median Curve");
		XYPlot plot = (XYPlot) chart.getPlot();
		Probe probe = (Probe) idComboBox.getSelectionModel().getSelectedItem();
		String name = (String) modelNameComboBox.getSelectionModel().getSelectedItem();
		List<Float> correctedValues = ((GCurvePResult) theStatResult).getCorrectedDoseResponseValues();
		double[] responses = new double[correctedValues.size()];
		for (int i = 0; i < correctedValues.size(); i++)
			responses[i] = correctedValues.get(i).doubleValue();

		lastDose = doses[0];
		holder = 0;
		for (counter = 0; counter < doses.length; counter++)
		{
			if (lastDose != doses[counter])
			{

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
				median = org.jfree.data.statistics.Statistics.calculateMedian(Arrays.asList(dr));

				meanPlusSD.add(maskDose(lastDose), mean + stdD);
				meanMinusSD.add(maskDose(lastDose), mean - stdD);
				meanSeries.add(maskDose(lastDose), mean);
				medianSeries.add(maskDose(lastDose), median);
				lastDose = doses[counter];
				holder = counter;
			}
		}
		// Run in the last data set
		Double[] dr = new Double[(counter - holder)];
		Double[] dr2 = new Double[(counter - holder)];
		for (counter2 = holder; counter2 < counter; counter2++)
			dr[(counter2 - holder)] = responses[counter2];
		// get mean and std dev
		mean = org.jfree.data.statistics.Statistics.calculateMean(dr);
		stdD = org.jfree.data.statistics.Statistics.getStdDev(dr);
		median = org.jfree.data.statistics.Statistics.calculateMedian(Arrays.asList(dr));
		meanPlusSD.add(maskDose(lastDose), mean + stdD);
		meanMinusSD.add(maskDose(lastDose), mean - stdD);
		meanSeries.add(maskDose(lastDose), mean);
		medianSeries.add(maskDose(lastDose), median);
		medianSeriesSet.addSeries(medianSeries);
		meanSeriesSet.addSeries(meanSeries);
		meanSeriesSet.addSeries(meanPlusSD);
		meanSeriesSet.addSeries(meanMinusSD);

		XYSplineRenderer renderer1 = new XYSplineRenderer();
		renderer1.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] { 2.0f, 6.0f }, 0.0f));
		renderer1.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] { 2.0f, 6.0f }, 0.0f));
		renderer1.setSeriesStroke(2, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
				1.0f, new float[] { 2.0f, 6.0f }, 0.0f));
		renderer1.setSeriesPaint(0, Color.black);
		renderer1.setSeriesPaint(1, Color.blue);
		renderer1.setSeriesPaint(2, Color.blue);

		XYSplineRenderer renderer2 = new XYSplineRenderer();

		renderer2.setSeriesPaint(0, Color.black);

		plot.setDataset(1, meanSeriesSet);
		plot.setDataset(2, medianSeriesSet);

		plot.setRenderer(1, renderer1);
		plot.setRenderer(2, renderer2);

		// now put the lotel/notel/bmd/bmdl/bmdu on that bad boy

		double minDose = bmdModel.minimumDose();
		double maxDose = bmdModel.maximumDose();

		bmdSeries = new XYSeries("BMD");
		bmdlSeries = new XYSeries("BMDL");
		bmduSeries = new XYSeries("BMDU");
		noelSeries = new XYSeries("NOTEL");
		loelSeries = new XYSeries("LOTEL");

		List<Float> cSorted = new ArrayList<>();
		cSorted.addAll(correctedValues);
		Collections.sort(cSorted);
		double maxResponse = cSorted.get(cSorted.size() - 1).doubleValue();

		// Set up BMD and BMDL and BMDU
		if (parameters[0] >= minDose && parameters[0] <= maxDose)
		{
			bmdSeries.add(parameters[0], maxResponse);
			bmdSeries.add(parameters[0], LOW - .01);
		}

		double smallestDose = 0.0;
		if (logDosesCheckBox.isSelected())
			smallestDose = logZeroDose;
		else
			smallestDose = minDose;

		bmdSeries.add(smallestDose, maxResponse);

		if (parameters[1] >= minDose && parameters[1] <= maxDose)
		{
			bmdlSeries.add(parameters[1], maxResponse);
			bmdlSeries.add(parameters[1], LOW - .01);
		}
		if (parameters[2] >= minDose && parameters[2] <= maxDose && parameters[2] > 0.0)
		{
			bmduSeries.add(parameters[2], maxResponse);
			bmduSeries.add(parameters[2], LOW - .01);
		}
		bmduSeries.add(parameters[0], maxResponse);

		probe = (Probe) idComboBox.getSelectionModel().getSelectedItem();
		name = (String) modelNameComboBox.getSelectionModel().getSelectedItem();
		ProbeStatResult probeStatResult = this.probeStatResultMap.get(probe);
		if (probeStatResult != null && probeStatResult.getPrefilterNoel() != null)
			noelSeries.add(probeStatResult.getPrefilterNoel().doubleValue(), maxResponse);
		if (probeStatResult != null && probeStatResult.getPrefilterLoel() != null)
			loelSeries.add(probeStatResult.getPrefilterLoel().doubleValue(), maxResponse);

	}
}
