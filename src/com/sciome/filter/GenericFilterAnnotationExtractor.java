package com.sciome.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.filter.annotation.Filterable;

/*
 * allows parsing annotations and does reflection to get the values
 * from a class that has been annotated for filtration.
 */
public class GenericFilterAnnotationExtractor
{
	Object				objectToFilter;

	Map<String, Method>	methodMap	= new HashMap<>();
	List<String>		keys		= new ArrayList<>();

	List<DataFilter>	dataFilters;

	public GenericFilterAnnotationExtractor(Class classToFilter)
	{
		this.objectToFilter = objectToFilter;
		getFilterableMembers(classToFilter);
	}

	private void getFilterableMembers(Class classToFilter)
	{
		if (classToFilter == null)
			return;
		for (Method method : classToFilter.getMethods())
		{
			if (method.isAnnotationPresent(Filterable.class))
			{

				Annotation annotation = method.getAnnotation(Filterable.class);
				Filterable test = (Filterable) annotation;

				methodMap.put(test.key(), method);
				keys.add(test.key());
			}

		}
	}

	public Object getFilterableValue(Object obj, String key)
	{
		if (methodMap.containsKey(key))
		{
			try
			{
				return methodMap.get(key).invoke(obj);
			}

			catch (Exception e)
			{}
		}

		return null;
	}

	public Class getReturnType(String key)
	{
		Method method = methodMap.get(key);
		if (methodMap.get(key) != null)
			return methodMap.get(key).getReturnType();
		return null;
	}

	public Method getMethod(String key)
	{
		return methodMap.get(key);
	}

	public List<String> getKeys()
	{
		return keys;
	}

}
