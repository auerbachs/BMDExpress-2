package com.sciome.filter;

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
	protected GenericFilterAnnotationExtractor	filterAnnotationExtractor;
	private Class<S>							filterableAnnotatedClass;
	// Value to compare object to
	protected T									value1;

	// The other value to compare object to. this will be used for between comparisons.
	protected T									value2;

	public DataFilter(DataFilterType dataFilterType, Class<S> filterableAnnotatedClass, String key, T value1)
	{
		this.key = key;
		this.dataFilterType = dataFilterType;
		this.value1 = value1;
		this.filterableAnnotatedClass = filterableAnnotatedClass;
		init();
	}

	public DataFilter(DataFilterType dataFilterType, Class<S> filterableAnnotatedClass, String key, T value1,
			T value2)
	{
		this.key = key;
		this.dataFilterType = dataFilterType;
		this.value1 = value1;
		this.value2 = value2;
		this.filterableAnnotatedClass = filterableAnnotatedClass;
		init();
	}

	private void init()
	{
		filterAnnotationExtractor = new GenericFilterAnnotationExtractor(filterableAnnotatedClass);
	}

	public abstract boolean passesFilter(S object);

	public T getValue1()
	{
		return value1;
	}

	public T getValue2()
	{
		return value2;
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
