package com.sciome.charts.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sciome.charts.annotation.ChartableDataPoint;

/*
 * given a class that has chartable data points, get the methods that are thusly annotated.
 * store those methods in a hash referenced by a key.
 * Allow invoking those methods via the key and passedin object through reflection.
 */
public class GenericChartDataExtractor
{

	Map<String, Method>	methodMap	= new HashMap<>();
	List<String>		keys		= new ArrayList<>();

	public GenericChartDataExtractor(Class classToChart)
	{
		getChartableMembers(classToChart);
	}

	private void getChartableMembers(Class classToChart)
	{
		if (classToChart == null)
			return;
		for (Method method : classToChart.getMethods())
		{
			if (method.isAnnotationPresent(ChartableDataPoint.class))
			{

				Annotation annotation = method.getAnnotation(ChartableDataPoint.class);
				ChartableDataPoint test = (ChartableDataPoint) annotation;

				methodMap.put(test.key(), method);
				keys.add(test.key());
			}

		}
	}

	/*
	 * chart data points should be numbers.
	 */
	public Double getDataPointValue(Object obj, String key)
	{
		if (methodMap.containsKey(key))
		{
			try
			{
				Object value = methodMap.get(key).invoke(obj);
				if (value instanceof Number)
					return ((Number) value).doubleValue();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public List<String> getKeys()
	{
		return keys;
	}

	@SuppressWarnings("unused")
	public static Method getAnnotatedMethod(Class classOfInterest, Class annotationOfInterest)
	{

		for (Method method : classOfInterest.getMethods())
		{
			if (method.isAnnotationPresent(annotationOfInterest))
			{
				return method;
			}

		}
		return null;
	}

}
