package com.sciome.filter.component;

import java.util.List;
import java.util.Set;

/*
 * provides an interface so that instances outside of the filter package
 * can know that the datafilter changed and update themselves accordingly
 */
public interface DataFilterComponentListener
{
	public void dataFilterChanged();

	public Set<String> getItemsForKey(String key);

	public List<Object> getRangeForKey(String method);

}
