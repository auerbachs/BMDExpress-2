/*
 * MultiInputPane.java
 *
 * Copyright (c) 2004, 2005 - Russell C. Bjork
 * Modified on Dec 9, 2005 by Longlong Yang
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * This is a utility class for displaying a dialog that asks for multiple values. Based on ideas in Wu's
 * javabook.MultiInputBox class and on ideas in javax.swing.JOptionPane
 */
public class MultiInputPane extends JOptionPane implements ActionListener, MouseListener
{
	private int				FLAG		= -1;
	private JTextField[]	fields;
	private JComboBox[]		comboBoxes;
	private JComponent[]	fieldsOrBoxes;
	private int[]			indices;
	private Vector<String>	selectedItems;
	private JList			aList;
	private boolean			ok;

	private final int		maxColumn	= 100;

	/**
	 * Constructor used by the above
	 *
	 * @param prompts
	 *            the prompts to display
	 * @param initialValues
	 *            the initial values to display for each item - this parameter can be null, in which case no
	 *            initial values are specified; or individual elements can be null, indicating that no initial
	 *            value is specified for a particular field
	 */
	public MultiInputPane(String message, String[] prompts, String[] initialValues)
	{
		super();
		removeAll();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		if (message != null && message.length() > 1)
		{
			JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
			msgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			add(msgLabel, BorderLayout.NORTH);
		}

		int length = prompts.length;
		fields = new JTextField[length];

		// JPanel labelPane = new JPanel(new GridLayout(0, 1, 0, 5));
		// JPanel inputPane = new JPanel(new GridLayout(0, 1, 0, 5));
		JPanel centerPane = new JPanel(new GridLayout(0, 2, 5, 5));

		for (int i = 0; i < prompts.length; i++)
		{
			// labelPane.add(new JLabel(prompts[i], SwingConstants.RIGHT));
			centerPane.add(new JLabel(prompts[i], SwingConstants.RIGHT));
			fields[i] = new JTextField(10);
			fields[i].setMargin(new Insets(0, 5, 0, 5));
			fields[i].addMouseListener(this);
			// inputPane.add(fields[i]);
			centerPane.add(fields[i]);

			if (initialValues != null && initialValues.length > i && initialValues[i] != null)
			{
				fields[i].setText(initialValues[i]);

				if (initialValues[i].length() > maxColumn)
				{
					fields[i].setColumns(maxColumn / 2);
				}
			}
		}

		// JPanel centerPane = new JPanel(new FlowLayout());
		centerPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JPanel borderPane = new JPanel();
		borderPane.add(centerPane);
		borderPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		// centerPane.add(labelPane);
		// centerPane.add(inputPane);

		add(borderPane, BorderLayout.CENTER);

	}

	/**
	 * initialValues are array of arrays with variable length
	 */
	public MultiInputPane(String message, String[] prompts, String[][] initialValues)
	{
		super();
		removeAll();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		if (message != null && message.length() > 1)
		{
			JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
			msgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			add(msgLabel, BorderLayout.NORTH);
		}

		int length = prompts.length;
		fieldsOrBoxes = new JComponent[length];

		JPanel labelPane = new JPanel(new GridLayout(0, 1, 10, 5));
		JPanel inputPane = new JPanel(new GridLayout(0, 1, 0, 5));

		for (int i = 0; i < prompts.length; i++)
		{
			labelPane.add(new JLabel(prompts[i], SwingConstants.RIGHT));

			if (initialValues[i] != null && initialValues[i].length > 1)
			{
				JComboBox box = new JComboBox(initialValues[i]);
				// box.setMinimumSize(new Dimension(80, 20));
				box.setEditable(true);
				fieldsOrBoxes[i] = box;
			}
			else
			{
				JTextField tField = new JTextField(10);
				tField.setMargin(new Insets(0, 5, 0, 5));
				tField.setEditable(true);
				tField.addMouseListener(this);

				if (initialValues[i] != null & initialValues[i].length > 0)
				{
					tField.setText(initialValues[i][0]);

					if (initialValues[i][0].length() > maxColumn)
					{
						fields[i].setColumns(maxColumn / 2);
					}
				}

				fieldsOrBoxes[i] = tField;
			}

			inputPane.add(fieldsOrBoxes[i]);
		}

		JPanel centerPane = new JPanel(new BorderLayout(10, 10));
		centerPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JPanel borderPane = new JPanel();
		borderPane.add(centerPane);
		borderPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		centerPane.add(labelPane, BorderLayout.CENTER);
		centerPane.add(inputPane, BorderLayout.EAST);

		add(borderPane, BorderLayout.CENTER);

	}

	/**
	 * Constructor used by matching initial values to prompts for example just focus on fewer colums of
	 * prompts than mutiple columns of initials
	 *
	 * @param prompts
	 *            the prompts to display
	 * @param initialValues
	 *            the initial values to display for each item - this parameter can be null, in which case no
	 *            initial values are specified; or individual elements can be null, indicating that no initial
	 *            value is specified for a particular field
	 * @type type is used to distinguish this function from the above
	 */
	public MultiInputPane(String message, String[] prompts, String[] initialValues, int type)
	{
		super();
		FLAG = type;
		removeAll();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		if (message != null && message.length() > 1)
		{
			JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
			msgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			add(msgLabel, BorderLayout.NORTH);
		}

		int length = prompts.length;
		comboBoxes = new JComboBox[length];
		indices = new int[length];
		selectedItems = new Vector<String>();
		JPanel centerPane = new JPanel(new GridLayout(0, 2, 5, 5));

		for (int i = 0; i < length; i++)
		{
			centerPane.add(new JLabel(prompts[i], SwingConstants.RIGHT));
			comboBoxes[i] = new JComboBox(initialValues);
			comboBoxes[i].setName(Integer.toString(i));
			comboBoxes[i].setSelectedItem(prompts[i]);
			indices[i] = comboBoxes[i].getSelectedIndex();
			comboBoxes[i].addActionListener(this);
			selectedItems.add(initialValues[i]);
			centerPane.add(comboBoxes[i]);
		}

		centerPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JPanel borderPane = new JPanel();
		borderPane.add(centerPane);
		borderPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		add(borderPane, BorderLayout.CENTER);

	}

	public MultiInputPane(String message, String[] initialValues)
	{
		super();
		removeAll();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		if (message != null && message.length() > 1)
		{
			add(new JLabel(message), BorderLayout.NORTH);
		}

		aList = new JList(initialValues);
		// aList.setMargin(new Insets(0, 5, 0, 5));
		aList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		aList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(aList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(250, 100));
		JPanel centerPane = new JPanel(new FlowLayout());
		centerPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		centerPane.add(scrollPane);

		add(centerPane, BorderLayout.CENTER);

	}

	public MultiInputPane(String message, Vector<String> symbols)
	{
		super();
		removeAll();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

		if (message != null && message.length() > 1)
		{
			add(new JLabel(message), BorderLayout.NORTH);
		}

		JPanel centerPane = new JPanel(new GridLayout(0, 3, 0, 5));
		centerPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		centerPane.add(new JLabel("Original", SwingConstants.CENTER));
		centerPane.add(new JLabel("Upstream", SwingConstants.CENTER));
		centerPane.add(new JLabel("Downstream", SwingConstants.CENTER));

		int length = symbols.size();
		fields = new JTextField[length * 2];

		for (int i = 0; i < length; i++)
		{
			String symbol = symbols.get(i);
			int j = i * 2;
			centerPane.add(new JLabel(symbol, SwingConstants.CENTER));
			fields[j] = new JTextField(symbol + "_U");
			fields[j].setMargin(new Insets(0, 5, 0, 5));
			fields[j].addMouseListener(this);
			centerPane.add(fields[j++]);

			fields[j] = new JTextField(symbol + "_D");
			fields[j].setMargin(new Insets(0, 5, 0, 5));
			fields[j].addMouseListener(this);
			centerPane.add(fields[j]);
		}

		add(centerPane, BorderLayout.CENTER);

	}

	public void procesSelection(JComboBox box)
	{
		int max = box.getItemCount(); // indices.length;
		int n = Integer.parseInt(box.getName());
		int idx = box.getSelectedIndex();
		String value = (String) box.getSelectedItem();

		if (n > 0)
		{
			for (int i = 0; i < n; i++)
			{
				if (indices[i] == idx)
				{
					String msg = "You couldn't select the value twice: " + (String) box.getSelectedItem();
					// JOptionPane.showMessageDialog(this, msg, "Repeat Match", JOptionPane.WARNING_MESSAGE);

					box.setSelectedIndex(indices[n]);
					return;
				}
			}
		}

		indices[n] = idx;
		selectedItems.setElementAt(value, n);

		/*
		 * Re-assign new item to a box if it's element has been selected by a previous box to automatically
		 * avoid repeats
		 */
		for (int i = n + 1; i < indices.length; i++)
		{
			if (selectedItems.get(i).equals(value))
			{
				for (int j = i + 1; j % max < max; j++)
				{
					j = j % max;
					String value2 = (String) box.getItemAt(j);

					if (!selectedItems.contains(value2))
					{
						selectedItems.setElementAt(value2, i);
						indices[i] = j;
						comboBoxes[i].setSelectedIndex(j);
						i = indices.length;
						break;
					}
				}
			}
		}

		validate();
	}

	public int[] getIndices()
	{
		return indices;
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (e.getSource() instanceof JComboBox)
		{
			procesSelection((JComboBox) e.getSource());
		}
		else
		{
			getTopLevelAncestor().setVisible(false);
		}
	}

	/**
	 * Implements MouseListener methods for description search
	 */
	public void mouseClicked(MouseEvent me)
	{
		JTextField aField = (JTextField) me.getComponent();
		int count = me.getClickCount();
		if (count == 2)
		{
			aField.selectAll();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

}
