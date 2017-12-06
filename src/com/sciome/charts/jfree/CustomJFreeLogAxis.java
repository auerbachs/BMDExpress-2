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
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;

public class CustomJFreeLogAxis extends LogAxis
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1279891269174124131L;

	public CustomJFreeLogAxis()
	{
		super();
		init();
	}

	public CustomJFreeLogAxis(String label)
	{
		super(label);
		init();
	}

	private void init()
	{
		this.setMinorTickMarksVisible(false);
		this.setBase(10);
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

		Range range = getRange();
		List<NumberTick> ticks = new ArrayList<NumberTick>();
		double start = Math.floor(calculateLog(getLowerBound()));
		double end = Math.ceil(calculateLog(getUpperBound()));
		for (int i = (int) start; i < end; i++)
		{
			double v = Math.pow(this.getBase(), i);
			for (double j = 1; j <= this.getBase(); j++)
			{
				AttributedString s = createTickLabel(j * v);
				StringBuilder sb = new StringBuilder();
				AttributedCharacterIterator iterator = s.getIterator();
				char c = iterator.first();
				while (c != CharacterIterator.DONE)
				{
					sb.append(c);
					c = iterator.next();
				}
				String l = sb.toString();
				if (j != this.getBase())
					l = "";
				if (range.contains(j * v))
				{
					ticks.add(new NumberTick(new Double(j * v), l, TextAnchor.TOP_CENTER, TextAnchor.CENTER,
							0.0));
				}
			}
		}
		return ticks;
	}

}
