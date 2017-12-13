package com.sciome.filter.component;

/*
 * provides an interface so that instances outside of the filter package
 * can know that the datafilter changed and update themselves accordingly
 */
public interface FilterComponentContainer
{
	public void close(FilterComponent fc);

}
