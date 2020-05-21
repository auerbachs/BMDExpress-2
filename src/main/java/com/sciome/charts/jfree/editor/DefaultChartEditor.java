/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------------
 * DefaultChartEditor.java
 * -----------------------
 * (C) Copyright 2000-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *                   Daniel Gredler;
 *
 * Changes
 * -------
 * 24-Nov-2005 : New class, based on ChartPropertyEditPanel.java (DG);
 * 18-Dec-2008 : Use ResourceBundleWrapper - see patch 1607918 by
 *               Jess Thrysoee (DG);
 *
 */

package com.sciome.charts.jfree.editor;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;

/**
 * A panel for editing chart properties (includes subpanels for the title, legend and plot).
 */
class DefaultChartEditor extends JPanel implements ActionListener, ChartEditor
{
	private static final String		SERIES_COLOR			= "Series Color";

	/** A panel for displaying/editing the properties of the title. */
	private DefaultTitleEditor		titleEditor;

	/** A panel for displaying/editing the properties of the plot. */
	private DefaultPlotEditor		plotEditor;

	/**
	 * A checkbox indicating whether or not the chart is drawn with anti-aliasing.
	 */
	private JCheckBox				antialias;

	/** The chart background color. */
	private PaintSample				background;

	/** The current series color */
	private ArrayList<Paint>		seriesColor;

	/** The color panel used to display the current series colors */
	private JPanel					colorPanel;

	/** The resourceBundle for the localization. */
	protected static ResourceBundle	localizationResources	= ResourceBundleWrapper
			.getBundle("org.jfree.chart.editor.LocalizationBundle");

	/**
	 * Standard constructor - the property panel is made up of a number of sub-panels that are displayed in
	 * the tabbed pane.
	 *
	 * @param chart
	 *            the chart, whichs properties should be changed.
	 */
	public DefaultChartEditor(JFreeChart chart)
	{
		setLayout(new BorderLayout());

		JPanel other = new JPanel(new BorderLayout());
		other.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel general = new JPanel(new BorderLayout());
		general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				localizationResources.getString("General")));

		JPanel interior = new JPanel(new LCBLayout(6));
		interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		this.antialias = new JCheckBox(localizationResources.getString("Draw_anti-aliased"));
		this.antialias.setSelected(chart.getAntiAlias());
		interior.add(this.antialias);
		interior.add(new JLabel(""));
		interior.add(new JLabel(""));
		interior.add(new JLabel(localizationResources.getString("Background_paint")));
		this.background = new PaintSample(chart.getBackgroundPaint());
		interior.add(this.background);
		JButton button = new JButton(localizationResources.getString("Select..."));
		button.setActionCommand("BackgroundPaint");
		button.addActionListener(this);
		interior.add(button);

		interior.add(new JLabel(localizationResources.getString("Series_Paint")));
		button = new JButton(localizationResources.getString("Edit..."));

		JTextField info = new JTextField(localizationResources.getString("No_editor_implemented"));
		Plot plot = chart.getPlot();
		if (plot instanceof XYPlot)
		{
			seriesColor = new ArrayList<Paint>();
			colorPanel = new JPanel();
			for (int i = 0; i < ((XYPlot) plot).getSeriesCount(); i++)
			{
				seriesColor.add(((XYPlot) plot).getRenderer().getSeriesPaint(i));
				colorPanel.add(new PaintSample(seriesColor.get(i)));
			}
			interior.add(colorPanel);
			button.setActionCommand(SERIES_COLOR);
			button.addActionListener(this);
		}
		else if (plot instanceof CategoryPlot)
		{
			seriesColor = new ArrayList<Paint>();
			colorPanel = new JPanel();
			for (int i = 0; i < ((CategoryPlot) plot).getDataset().getRowCount(); i++)
			{
				seriesColor.add(((CategoryPlot) plot).getRenderer().getSeriesPaint(i));
				colorPanel.add(new PaintSample(seriesColor.get(i)));
			}
			interior.add(colorPanel);
			button.setActionCommand(SERIES_COLOR);
			button.addActionListener(this);
		}
		else
		{
			info.setEnabled(false);
			button.setEnabled(false);
			interior.add(info);
		}
		interior.add(button);

		interior.add(new JLabel(localizationResources.getString("Series_Stroke")));
		info = new JTextField(localizationResources.getString("No_editor_implemented"));
		info.setEnabled(false);
		interior.add(info);
		button = new JButton(localizationResources.getString("Edit..."));
		button.setEnabled(false);
		interior.add(button);

		interior.add(new JLabel(localizationResources.getString("Series_Outline_Paint")));
		info = new JTextField(localizationResources.getString("No_editor_implemented"));
		info.setEnabled(false);
		interior.add(info);
		button = new JButton(localizationResources.getString("Edit..."));
		button.setEnabled(false);
		interior.add(button);

		interior.add(new JLabel(localizationResources.getString("Series_Outline_Stroke")));
		info = new JTextField(localizationResources.getString("No_editor_implemented"));
		info.setEnabled(false);
		interior.add(info);
		button = new JButton(localizationResources.getString("Edit..."));
		button.setEnabled(false);
		interior.add(button);

		general.add(interior, BorderLayout.NORTH);
		other.add(general, BorderLayout.NORTH);

		JPanel parts = new JPanel(new BorderLayout());

		Title title = chart.getTitle();

		JTabbedPane tabs = new JTabbedPane();

		this.titleEditor = new DefaultTitleEditor(title);
		this.titleEditor.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		tabs.addTab(localizationResources.getString("Title"), this.titleEditor);

		if (plot instanceof PolarPlot)
		{
			this.plotEditor = new DefaultPolarPlotEditor((PolarPlot) plot);
		}
		else
		{
			this.plotEditor = new DefaultPlotEditor(plot);
		}
		this.plotEditor.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		tabs.addTab(localizationResources.getString("Plot"), this.plotEditor);

		tabs.add(localizationResources.getString("Other"), other);
		parts.add(tabs, BorderLayout.NORTH);
		add(parts);
	}

	/**
	 * Returns a reference to the title editor.
	 *
	 * @return A panel for editing the title.
	 */
	public DefaultTitleEditor getTitleEditor()
	{
		return this.titleEditor;
	}

	/**
	 * Returns a reference to the plot property sub-panel.
	 *
	 * @return A panel for editing the plot properties.
	 */
	public DefaultPlotEditor getPlotEditor()
	{
		return this.plotEditor;
	}

	/**
	 * Returns the current setting of the anti-alias flag.
	 *
	 * @return <code>true</code> if anti-aliasing is enabled.
	 */
	public boolean getAntiAlias()
	{
		return this.antialias.isSelected();
	}

	/**
	 * Returns the current background paint.
	 *
	 * @return The current background paint.
	 */
	public Paint getBackgroundPaint()
	{
		return this.background.getPaint();
	}

	/**
	 * Handles user interactions with the panel.
	 *
	 * @param event
	 *            a BackgroundPaint action.
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.equals("BackgroundPaint"))
		{
			attemptModifyBackgroundPaint();
		}
		else if (command.equals(SERIES_COLOR))
		{
			attemptModifySeriesPaint();
		}
	}

	/**
	 * Allows the user the opportunity to select a new background paint. Uses JColorChooser, so we are only
	 * allowing a subset of all Paint objects to be selected (fix later).
	 */
	private void attemptModifyBackgroundPaint()
	{
		Color c;
		c = JColorChooser.showDialog(new FrontDialog(this),
				localizationResources.getString("Background_Color"), Color.blue);
		if (c != null)
		{
			this.background.setPaint(c);
		}
	}

	/**
	 * Allows the user the opportunity to select a new set of series paint. Uses JColorChooser, so we are only
	 * allowing a subset of all Paint objects to be selected (fix later).
	 */
	private void attemptModifySeriesPaint()
	{
		JDialog dialog = new FrontDialog(this);
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(seriesColor.size() + 1, 2);
		panel.setLayout(layout);

		ArrayList<Paint> tempSeriesColor = new ArrayList<Paint>();
		for (int i = 0; i < seriesColor.size(); i++)
		{
			tempSeriesColor.add(seriesColor.get(i));
		}

		final Component comp = this;
		// Create action listeners for all the buttons
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("Ok"))
				{
					seriesColor = tempSeriesColor;
					setColorPanel();
					dialog.setVisible(false);
				}
				else if (e.getActionCommand().equals("Cancel"))
				{
					dialog.setVisible(false);
				}
				else
				{
					// Open up the color chooser
					Color c;
					Integer series = Integer.parseInt(e.getActionCommand());
					c = JColorChooser.showDialog(new FrontDialog(comp), "Series " + series + " Color",
							Color.blue);
					if (c != null)
					{
						((Button) e.getSource()).setBackground(c);
						tempSeriesColor.set(series, c);
					}
				}
			}
		};

		for (int i = 0; i < seriesColor.size(); i++)
		{
			panel.add(new Label("Series " + (i + 1)));
			Button btn = new Button();
			btn.setBackground((Color) seriesColor.get(i));
			btn.setForeground((Color) seriesColor.get(i));
			btn.setActionCommand("" + i);
			btn.addActionListener(listener);
			panel.add(btn);
		}
		Button ok = new Button("Ok");
		ok.setActionCommand("Ok");
		ok.addActionListener(listener);
		Button cancel = new Button("Cancel");
		cancel.setActionCommand("Cancel");
		cancel.addActionListener(listener);
		panel.add(ok);
		panel.add(cancel);

		JScrollPane scroll = new JScrollPane(panel);
		dialog.add(scroll);
		dialog.setSize(200, 300);
		dialog.setVisible(true);
	}

	/**
	 * Updates the properties of a chart to match the properties defined on the panel.
	 *
	 * @param chart
	 *            the chart.
	 */
	@Override
	public void updateChart(JFreeChart chart)
	{
		this.titleEditor.setTitleProperties(chart);
		this.plotEditor.updatePlotProperties(chart.getPlot());
		chart.setAntiAlias(getAntiAlias());
		chart.setBackgroundPaint(getBackgroundPaint());
		Plot plot = chart.getPlot();
		if (plot instanceof XYPlot)
		{
			for (int i = 0; i < seriesColor.size(); i++)
				((XYPlot) plot).getRenderer().setSeriesPaint(i, seriesColor.get(i));
		}
		else if (plot instanceof CategoryPlot)
		{
			for (int i = 0; i < seriesColor.size(); i++)
				((CategoryPlot) plot).getRenderer().setSeriesPaint(i, seriesColor.get(i));
		}
	}

	/**
	 * Reset the color panel with the new series colors
	 */
	private void setColorPanel()
	{
		colorPanel.removeAll();
		for (int i = 0; i < seriesColor.size(); i++)
			colorPanel.add(new PaintSample(seriesColor.get(i)));
		colorPanel.revalidate();
	}
}
