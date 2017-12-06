package com.sciome.charts.jfree;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;

public class CustomJFreeLogarithmicAxis extends LogarithmicAxis
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1279891269174124131L;

	// use this to define the ticks for log axis. try to keep it clean
		private final double[]			DECADES			= { 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001,
				0.001, 0.01, 0.1, 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };

		// log of zero is no good. So let this very small number represent log of zero
		private final double			LOGZEROVALUE		= 0.00000001;

	public CustomJFreeLogarithmicAxis(String label)
	{
		super(label);
		init();
	}

	private void init()
	{
		this.setMinorTickMarksVisible(false);
		final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		newSymbols.setExponentSeparator("E");
		final DecimalFormat decForm = new DecimalFormat("0.##E0#");
		decForm.setDecimalFormatSymbols(newSymbols);

		this.setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
			{
				// deal with the zero dose on in the log axis.
				// if (Math.abs(logZeroDose - number) < .00000000000001 && doses[0] == 0.0)
				// return new StringBuffer("0");
				return new StringBuffer(decForm.format(number));
			}

			@Override
			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos)
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Number parse(String source, ParsePosition parsePosition)
			{
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge)
	{
		 List ticks = new java.util.ArrayList();
	        Range range = getRange();

	        //get lower bound value:
	        double lowerBoundVal = range.getLowerBound();
	              //if small log values and lower bound value too small
	              // then set to a small value (don't allow <= 0):
	        if (this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
	            lowerBoundVal = SMALL_LOG_VALUE;
	        }

	        //get upper bound value
	        double upperBoundVal = range.getUpperBound();

	        //get log10 version of lower bound and round to integer:
	        int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
	        //get log10 version of upper bound and round to integer:
	        int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));

	        if (iBegCount == iEndCount && iBegCount > 0
	                && Math.pow(10, iBegCount) > lowerBoundVal) {
	              //only 1 power of 10 value, it's > 0 and its resulting
	              // tick value will be larger than lower bound of data
	            --iBegCount;       //decrement to generate more ticks
	        }

	        double currentTickValue;
			double upperdecade = getUpperdecade(upperBoundVal);
			int lowerIndex = getLowerdecadeIndex(lowerBoundVal);
			
			int i=lowerIndex;	
			while(DECADES[i] < upperdecade)
	        {
	            currentTickValue = DECADES[i];
	            if (currentTickValue >= lowerBoundVal - SMALL_LOG_VALUE) {
                    //tick value not below lowest data value
                    TextAnchor anchor;
                    TextAnchor rotationAnchor;
                    double angle = 0.0;
                    if (isVerticalTickLabels()) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                        if (edge == RectangleEdge.TOP) {
                            angle = Math.PI / 2.0;
                        }
                        else {
                            angle = -Math.PI / 2.0;
                        }
                    }
                    else {
                        if (edge == RectangleEdge.TOP) {
                            anchor = TextAnchor.BOTTOM_CENTER;
                            rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        }
                        else {
                            anchor = TextAnchor.TOP_CENTER;
                            rotationAnchor = TextAnchor.TOP_CENTER;
                        }
                    }

                    Tick tick = new NumberTick(new Double(currentTickValue),
                    		Double.valueOf(currentTickValue).toString() , anchor, rotationAnchor, angle);
                    ticks.add(tick);
                    
                   
	            }
	            i++;
	        }
	        return ticks;
	}
	
	@Override
	protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge)
	{
		
		List ticks = new java.util.ArrayList();

        //get lower bound value:
        double lowerBoundVal = getRange().getLowerBound();
        //if small log values and lower bound value too small
        // then set to a small value (don't allow <= 0):
        if (this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
            lowerBoundVal = SMALL_LOG_VALUE;
        }
        //get upper bound value
        double upperBoundVal = getRange().getUpperBound();

        //get log10 version of lower bound and round to integer:
        int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
        //get log10 version of upper bound and round to integer:
        int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));

        if (iBegCount == iEndCount && iBegCount > 0
                && Math.pow(10, iBegCount) > lowerBoundVal) {
              //only 1 power of 10 value, it's > 0 and its resulting
              // tick value will be larger than lower bound of data
            --iBegCount;       //decrement to generate more ticks
        }

        double tickVal;
        double upperdecade = getUpperdecade(upperBoundVal);
		int lowerIndex = getLowerdecadeIndex(lowerBoundVal);
		
		int i=lowerIndex;	
		while(DECADES[i] < upperdecade)
        {
           
                if (DECADES[i] > upperdecade) {
                    return ticks;  //if past highest data value then exit method
                }

            tickVal = DECADES[i];
            if (tickVal >= lowerBoundVal - SMALL_LOG_VALUE) {
                //tick value not below lowest data value
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = -Math.PI / 2.0;
                    }
                    else {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }
                //create tick object and add to list:
                ticks.add(new NumberTick(new Double(tickVal), Double.valueOf(tickVal).toString(),
                        anchor, rotationAnchor, angle));
            }
            i++;
        }
        return ticks;
		
	}
	

	
	private double getUpperdecade(double upperBound)
	{
		if (upperBound == 0)
			return 10;

		for (double decade : DECADES)
		{
			if (decade > upperBound)
				return decade;
		}

		return 0;
	}

	private double getLowerdecade(double lowerBound)
	{
		if (lowerBound == 0)
			return 1;
		for (int i = DECADES.length - 1; i > 0; i--)
			if (DECADES[i] < lowerBound)
				return DECADES[i];

		return LOGZEROVALUE;
	}
	
	private int getLowerdecadeIndex(double lowerBound)
	{
		if (lowerBound == 0)
			return 1;
		for (int i = DECADES.length - 1; i > 0; i--)
			if (DECADES[i] < lowerBound)
				return i;

		return 0;
	}
	
}
