package com.sciome.charts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * annotation that tells us whether a field in a POJO is Chartable Data Point
 * a Data point is a value that can be plotted.  
 * The key represents the name of the value which is meant to be used in the view
 * The key is meant to reference the annotated method so reflection can happen and values can be gotten.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChartableDataPoint
{
	public String key() default "";
}
