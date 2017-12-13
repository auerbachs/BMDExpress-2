package com.sciome.filter;

import java.util.List;

import com.sciome.filter.component.FilterDataExtractor;

/*
 * abstract class for making a data filter
 * S represents the class that should be annotated for filtratoin
 * T is the type of value that will be compared.
 * genericfilterannoationextractor has the keys and methods and map there of 
 * to get the data that needs comparing.
 * 
 */
public abstract class DataFilter<T, S>
{

	protected DataFilterType					dataFilterType;
	protected String							key;
	protected FilterDataExtractor	filterAnnotationExtractor;
	private Class<S>							filterableAnnotatedClass;
	// Value to compare object to
	protected List<Object>						values;

	public DataFilter(DataFilterType dataFilterType, Class<S> filterableAnnotatedClass, String key,
			List<Object> values)
	{
		this.key = key;
		this.dataFilterType = dataFilterType;
		this.values = values;
		this.filterableAnnotatedClass = filterableAnnotatedClass;
		init();
	}

	private void init()
	{
		filterAnnotationExtractor = new FilterDataExtractor(filterableAnnotatedClass);
	}

	public abstract boolean passesFilter(S object);

	public List<Object> getValues()
	{
		return values;
	}

	public DataFilterType getDataFilterType()
	{
		return dataFilterType;
	}

	public String getKey()
	{
		return key;
	}

}
