package com.sciome.filter.component;

import java.util.List;

import javafx.scene.control.Control;

/*
 * provides an interface so that instances outside of the filter package
 * can know that the datafilter changed and update themselves accordingly
 */
public interface FilterComponentContainer
{
	public void close(FilterComponent fc);

	public void filterChanged(List<Control> controls);

}
