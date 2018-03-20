package com.sciome.filter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.filter.component.FilterDataExtractor;

/*
 * abstract class for making a data filter
 * S represents the class that should be annotated for filtratoin
 * T is the type of value that will be compared.
 * genericfilterannoationextractor has the keys and methods and map there of 
 * to get the data that needs comparing.
 * 
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = StringFilter.class, name = "string"),
		@Type(value = NumberFilter.class, name = "number"),
		@Type(value = IntegerFilter.class, name = "integer") })
public abstract class DataFilter<T>
{

	protected DataFilterType			dataFilterType;
	protected String					key;

	// these two fields are causing a memory leak
	// because we are storing these filter instances in a hash
	// that is cached.
	protected FilterDataExtractor		filterAnnotationExtractor;
	protected BMDExpressAnalysisDataSet	bmdanalysisDataSet;

	// Value to compare object to
	protected List<Object>				values;

	public DataFilter()
	{

	}

	public DataFilter(DataFilterType dataFilterType, BMDExpressAnalysisDataSet bmdanalysisDataSet, String key,
			List<Object> values)
	{
		this.key = key;
		this.dataFilterType = dataFilterType;
		this.values = values;
		this.bmdanalysisDataSet = bmdanalysisDataSet;
		init();
	}

	public void init()
	{
		filterAnnotationExtractor = new FilterDataExtractor(bmdanalysisDataSet);
	}

	public abstract boolean passesFilter(BMDExpressAnalysisRow object);

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

	@JsonIgnore
	public FilterDataExtractor getFilterAnnotationExtractor()
	{
		return filterAnnotationExtractor;
	}

	public void setFilterAnnotationExtractor(FilterDataExtractor filterAnnotationExtractor)
	{
		this.filterAnnotationExtractor = filterAnnotationExtractor;
	}

	@JsonIgnore
	public BMDExpressAnalysisDataSet getBmdanalysisDataSet()
	{
		return bmdanalysisDataSet;
	}

	public void setBmdanalysisDataSet(BMDExpressAnalysisDataSet bmdanalysisDataSet)
	{
		this.bmdanalysisDataSet = bmdanalysisDataSet;
	}

	public void setDataFilterType(DataFilterType dataFilterType)
	{
		this.dataFilterType = dataFilterType;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public void setValues(List<Object> values)
	{
		this.values = values;
	}

	public abstract DataFilter copy();

}
