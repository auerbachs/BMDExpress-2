package com.sciome.charts.jfree.editor;

import java.awt.Component;

import javax.swing.JDialog;

/**
 * A dialog that sits in front of the given component
 */
public class FrontDialog extends JDialog{
	public FrontDialog(Component c) {
		super();
		this.setLocationRelativeTo(c);
		this.setAlwaysOnTop(true);
	}
}
