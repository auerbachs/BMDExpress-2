package com.sciome.charts.javafx;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;

/**
 * A logarithmic axis implementation for JavaFX 2 charts<br>
 * <br>
 * 
 * @author Kevin Senechal
 * 
 */
public class LogarithmicAxis extends ValueAxis<Number>
{

	/**
	 * The time of animation in ms
	 */
	private static final double		ANIMATION_TIME		= 2000;
	private final Timeline			lowerRangeTimeline	= new Timeline();
	private final Timeline			upperRangeTimeline	= new Timeline();

	private final DoubleProperty	logUpperBound		= new SimpleDoubleProperty();
	private final DoubleProperty	logLowerBound		= new SimpleDoubleProperty();

	// use this to define the ticks for log axis. try to keep it clean
	private final double[]			CENTURIES			= { 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001,
			0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };

	// log of zero is no good. So let this very small number represent log of zero
	private final double			LOGZEROVALUE		= 0.00000001;

	public LogarithmicAxis()
	{
		// super(1, 10000);
		super();
		bindLogBoundsToDefaultBounds();
	}

	public LogarithmicAxis(double lowerBound, double upperBound)
	{
		super(lowerBound, upperBound);
		try
		{
			validateBounds(lowerBound, upperBound);
			bindLogBoundsToDefaultBounds();
		}
		catch (IllegalLogarithmicRangeException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Bind our logarithmic bounds with the super class bounds, consider the base 10 logarithmic scale.
	 */
	private void bindLogBoundsToDefaultBounds()
	{
		logLowerBound.bind(new DoubleBinding() {

			{
				super.bind(lowerBoundProperty());
			}

			@Override
			protected double computeValue()
			{
				double lowb = lowerBoundProperty().get();
				double lowBLog10 = Math.log10(lowb);
				return lowBLog10;

			}
		});
		logUpperBound.bind(new DoubleBinding() {

			{
				super.bind(upperBoundProperty());
			}

			@Override
			protected double computeValue()
			{
				double upperb = upperBoundProperty().get();
				double upperbLog10 = Math.log10(upperb);
				return upperbLog10;

			}
		});
	}

	/**
	 * Validate the bounds by throwing an exception if the values are not conform to the mathematics log
	 * interval: ]0,Double.MAX_VALUE]
	 * 
	 * @param lowerBound
	 * @param upperBound
	 * @throws IllegalLogarithmicRangeException
	 */
	private void validateBounds(double lowerBound, double upperBound) throws IllegalLogarithmicRangeException
	{
		if (lowerBound < 0 || upperBound < 0 || lowerBound > upperBound)
		{
			throw new IllegalLogarithmicRangeException(
					"The logarithmic range should be include to ]0,Double.MAX_VALUE] and the lowerBound should be less than the upperBound");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Number> calculateMinorTickMarks()
	{
		Number[] range = getRange();
		List<Number> minorTickMarksPositions = new ArrayList<Number>();
		try
		{
			if (range != null)
			{

				Number upperBound = range[1];
				Number lowerBound = range[0];
				double logLowerBound = Math.log10(lowerBound.doubleValue());
				double logUpperBound = Math.log10(upperBound.doubleValue());
				int minorTickMarkCount = getMinorTickCount();

				if (logLowerBound < 0)
					logLowerBound = lowerBound.doubleValue();
				if (logUpperBound < 0)
					logUpperBound = upperBound.doubleValue();
				if (logLowerBound == 0.0)
					logLowerBound = LOGZEROVALUE;

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return minorTickMarksPositions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Number> calculateTickValues(double length, Object range)
	{
		List<Number> tickPositions = new ArrayList<Number>();
		try
		{
			if (range != null)
			{
				Number lowerBound = ((Number[]) range)[0];
				Number upperBound = ((Number[]) range)[1];

				// for (double i = 0; i <= logUpperBound; i += 1)

				double lowcentury = getLowerCentury(lowerBound.doubleValue());
				double uppercentury = getUpperCentury(upperBound.doubleValue());
				for (double i = lowcentury; i < uppercentury; i *= 10)
					tickPositions.add(i);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return tickPositions;
	}

	private double getUpperCentury(double upperBound)
	{
		if (upperBound == 0)
			return 10;

		for (double century : CENTURIES)
		{
			if (century > upperBound)
				return century;
		}

		return 0;
	}

	private double getLowerCentury(double lowerBound)
	{
		if (lowerBound == 0)
			return 1;
		for (int i = CENTURIES.length - 1; i > 0; i--)
			if (CENTURIES[i] < lowerBound)
				return CENTURIES[i];

		return LOGZEROVALUE;
	}

	@Override
	protected Number[] getRange()
	{
		return new Number[] { lowerBoundProperty().get(), upperBoundProperty().get() };
	}

	@Override
	protected String getTickMarkLabel(Number value)
	{
		NumberFormat formatter = DecimalFormat.getInstance();
		if (value.doubleValue() < 0.001)
		{
			formatter = new DecimalFormat("0E0");
			return formatter.format(value);
		}
		formatter.setMaximumIntegerDigits(10);
		formatter.setMinimumIntegerDigits(1);
		return formatter.format(value);
	}

	@Override
	protected java.lang.Object autoRange(double minValue, double maxValue, double length, double labelSize)
	{
		if (minValue == 0.0) // log of 0.0 is Infinity. so, better bump it up a notch
			minValue = LOGZEROVALUE;

		//
		double centuryMin = minValue;
		for (int i = 10; i > -20; i--)
		{
			if (minValue > Math.pow(10.0, i))
			{
				centuryMin = Math.pow(10.0, i);
				break;
			}
		}

		double centuryMax = maxValue;
		for (int i = -10; i < 20; i++)
		{
			if (maxValue < Math.pow(10.0, i))
			{
				centuryMax = Math.pow(10.0, i);
				break;
			}
		}
		Number[] range = new Number[] { Double.valueOf(centuryMin), Double.valueOf(centuryMax) };
		return range;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setRange(Object range, boolean animate)
	{
		if (range != null)
		{
			Number lowerBound = ((Number[]) range)[0];
			Number upperBound = ((Number[]) range)[1];
			try
			{
				validateBounds(lowerBound.doubleValue(), upperBound.doubleValue());
			}
			catch (IllegalLogarithmicRangeException e)
			{
				e.printStackTrace();
			}
			if (animate)
			{
				try
				{
					lowerRangeTimeline.getKeyFrames().clear();
					upperRangeTimeline.getKeyFrames().clear();

					lowerRangeTimeline.getKeyFrames()
							.addAll(new KeyFrame(Duration.ZERO,
									new KeyValue(lowerBoundProperty(), lowerBoundProperty().get())),
									new KeyFrame(new Duration(ANIMATION_TIME),
											new KeyValue(lowerBoundProperty(), lowerBound.doubleValue())));

					upperRangeTimeline.getKeyFrames()
							.addAll(new KeyFrame(Duration.ZERO,
									new KeyValue(upperBoundProperty(), upperBoundProperty().get())),
									new KeyFrame(new Duration(ANIMATION_TIME),
											new KeyValue(upperBoundProperty(), upperBound.doubleValue())));
					lowerRangeTimeline.play();
					upperRangeTimeline.play();
				}
				catch (Exception e)
				{
					lowerBoundProperty().set(lowerBound.doubleValue());
					upperBoundProperty().set(upperBound.doubleValue());
				}
			}
			lowerBoundProperty().set(lowerBound.doubleValue());
			upperBoundProperty().set(upperBound.doubleValue());
		}
	}

	@Override
	public Number getValueForDisplay(double displayPosition)
	{
		double delta = logUpperBound.get() - logLowerBound.get();
		if (getSide().isVertical())
		{
			return Math.pow(10,
					(((displayPosition - getHeight()) / -getHeight()) * delta) + logLowerBound.get());
		}
		else
		{
			return Math.pow(10, (((displayPosition / getWidth()) * delta) + logLowerBound.get()));
		}
	}

	@Override
	public double getDisplayPosition(Number value)
	{

		double delta = logUpperBound.get() - logLowerBound.get();
		double deltaV = Math.log10(value.doubleValue()) - logLowerBound.get();
		if (getSide().isVertical())
		{
			return (1. - ((deltaV) / delta)) * getHeight();
		}
		else
		{
			return ((deltaV) / delta) * getWidth();
		}

	}

	/**
	 * Exception to be thrown when a bound value isn't supported by the logarithmic axis<br>
	 * <br>
	 * 
	 * @author Kevin Senechal mailto: kevin.senechal@dooapp.com
	 * 
	 */
	public class IllegalLogarithmicRangeException extends Exception
	{

		/**
		 * @param string
		 */
		public IllegalLogarithmicRangeException(String message)
		{
			super(message);
		}

	}
}
