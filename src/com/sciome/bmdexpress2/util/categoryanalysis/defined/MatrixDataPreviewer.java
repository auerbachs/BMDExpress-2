/*
 * MatrixDataPreviewer.java      0.5    10/10/2006
 *
 * Copyright (c) 2006 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for go term
 * It is used to view matrix data in table format.
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import com.sciome.bmdexpress2.util.MatrixData;

/**
 * The class for MatrixDataPreviewer
 *
 * @version 0.5, 06/24/2005
 * @author Longlong Yang
 */
public class MatrixDataPreviewer extends JOptionPane implements ActionListener
{

	private JTextArea			noteArea;
	private String				name;
	private String[]			columnNames;
	private Object[]			longValues;
	private Object[][]			data;
	private JFrame				parent;
	private JPanel				tableContainer;
	private JScrollPane			tablePane;
	private DefaultTableModel	tModel;
	private JCheckBox			yesBox;
	private JButton				setButton;

	private MatrixData			matrixData;

	private boolean				ok;

	/**
	 * Class constructor
	 * 
	 * @param sg
	 *            is a ScreenGui object
	 * @param adb
	 *            is a AccessDbase object
	 */
	public MatrixDataPreviewer(MatrixData matrix)
	{
		super();
		matrixData = matrix;
		name = matrix.getName();
		columnNames = matrix.getColumnNames();
		data = matrix.getData();
		init();
	}

	private void assignLongValues()
	{
		longValues = new Object[columnNames.length];

		for (int i = 0; i < columnNames.length; i++)
		{
			longValues[i] = columnNames[i];
		}
	}

	private void init()
	{
		if (data.length > 0)
		{
			assignLongValues();
			JPanel pan = createPane();
			removeAll();
			add(pan);
		}
	}

	/**
	 * Create components for login interface
	 */
	private JPanel createPane()
	{
		tModel = new NumeralTableModel(data, columnNames);
		JTable aTable = new JTable(tModel);
		aTable.setAutoCreateRowSorter(true);
		aTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		aTable.setDefaultRenderer(Double.class, new StringTableCellRenderer());

		// tModel = new DefaultTableModel(data, columnNames);
		// TableSorter sorter = new TableSorter(tModel);
		// JTable aTable = new JTable(sorter);
		// sorter.addMouseListenerToHeaderInTable(aTable);

		if (columnNames.length > 5)
		{
			aTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		tablePane = new JScrollPane(aTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableContainer = new JPanel(new GridLayout(1, 1));
		tableContainer.add(tablePane);
		tableContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel topPane = new JPanel(new BorderLayout());
		topPane.add(tableContainer, BorderLayout.CENTER);

		if (matrixData != null && matrixData.hasHeaders())
		{
			yesBox = new JCheckBox("Yes");
			yesBox.addActionListener(this);
			setButton = new JButton("Use");
			setButton.setEnabled(false);
			setButton.addActionListener(this);
			JPanel setPane = new JPanel(new FlowLayout());
			setPane.setPreferredSize(new Dimension(540, 36));
			setPane.add(new JLabel("Does the data contain column headers?"));
			setPane.add(yesBox);
			setPane.add(setButton);
			topPane.add(setPane, BorderLayout.SOUTH);
		}

		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.setPreferredSize(new Dimension(560, 300));
		pan.add(topPane, BorderLayout.CENTER);

		return pan;
	}

	private void removeHeaders()
	{
		matrixData.removeHeaders();
		tModel.removeRow(0);
		tModel.setColumnIdentifiers(matrixData.getColumnNames());
		setButton.setEnabled(false);
		yesBox.setSelected(false);
	}

	public MatrixData getMatrix()
	{
		return matrixData;
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if ("OK".equals(cmd))
		{
			ok = true;
			getTopLevelAncestor().setVisible(false);
		}
		else if ("Cancel".equals(cmd))
		{
			matrixData = null;
			ok = true;
			getTopLevelAncestor().setVisible(false);
		}
		else if (cmd.equals("Yes"))
		{
			if (yesBox.isSelected())
			{
				setButton.setEnabled(true);
			}
			else
			{
				setButton.setEnabled(false);
			}
		}
		else if (cmd.equals("Use"))
		{
			removeHeaders();
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Ask your program provider for help.");
		}
	}

}
