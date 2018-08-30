package com.sciome.charts.jfree;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;


public class CustomJFreeLogarithmicAxis extends LogarithmicAxis {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1279891269174124131L;

	// use this to define the ticks for log axis. try to keep it clean
	private final double[] DECADES = { 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001, 0.001, 0.01, 0.1, 1, 10, 100,
			1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };

	// log of zero is no good. So let this very small number represent log of zero
	private final double LOGZEROVALUE = 0.00000001;

	public CustomJFreeLogarithmicAxis(String label) {
		super(label);
		this.setVerticalTickLabels(true);
		init();
	}

	private void init() {
		this.setMinorTickMarksVisible(false);
		final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		newSymbols.setExponentSeparator("E");
		final DecimalFormat decForm = new DecimalFormat("0.##E0#");
		decForm.setDecimalFormatSymbols(newSymbols);

		this.setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
				// deal with the zero dose on in the log axis.
				// if (Math.abs(logZeroDose - number) < .00000000000001 && doses[0] == 0.0)
				// return new StringBuffer("0");
				return new StringBuffer(decForm.format(number));
			}

			@Override
			public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Number parse(String source, ParsePosition parsePosition) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
		List ticks = new java.util.ArrayList();
		Range range = getRange();
		// get lower bound value:
		double lowerBoundVal = range.getLowerBound();
		// if small log values and lower bound value too small
		// then set to a small value (don't allow <= 0):
		if (this.smallLogFlag && lowerBoundVal < LOGZEROVALUE) {
	        lowerBoundVal = LOGZEROVALUE;
	    }
		// get upper bound value
		double upperBoundVal = range.getUpperBound();
		// get log10 version of lower bound and round to integer:
		int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
		// get log10 version of upper bound and round to integer:
		int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));
		if (iBegCount == iEndCount && iBegCount > 0 && Math.pow(10, iBegCount) > lowerBoundVal) {
			// only 1 power of 10 value, it's > 0 and its resulting
			// tick value will be larger than lower bound of data
			--iBegCount; // decrement to generate more ticks
		}
		double currentTickValue;
		String tickLabel;
		boolean zeroTickFlag = false;
		for (int i = iBegCount; i <= iEndCount; i++) {
			// for each power of 10 value; create ten ticks
			for (int j = 0; j < 10; ++j) {
				// for each tick to be displayed
				if (this.smallLogFlag) {
					// small log values in use; create numeric value for tick
					currentTickValue = Math.pow(10, i) + (Math.pow(10, i) * j);
					if (this.expTickLabelsFlag || (i < 0 && currentTickValue > 0.0 && currentTickValue < 1.0)) {
						// showing "1e#"-style ticks or negative exponent
						// generating tick value between 0 & 1; show fewer
						if (j == 0) {
							// first tick of series, or not too small a value and
							// one of first 3 ticks, or last tick to be displayed
							// set exact number of fractional digits to be shown
							// (no effect if showing "1e#"-style ticks):
							this.numberFormatterObj.setMaximumFractionDigits(-i);
							// create tick label (force use of fmt obj):
							tickLabel = makeTickLabel(currentTickValue, true);
						} else { // no tick label to be shown
							tickLabel = "";
						}
					} else { // tick value not between 0 & 1
								// show tick label if it's the first or last in
								// the set
						tickLabel = (j < 1)
								? makeTickLabel(currentTickValue)
								: "";
					}
				} else { // not small log values in use; allow for values <= 0
					if (zeroTickFlag) { // if did zero tick last iter then
						--j; // decrement to do 1.0 tick now
					} // calculate power-of-ten value for tick:
					currentTickValue = (i >= 0) ? Math.pow(10, i) + (Math.pow(10, i) * j)
							: -(Math.pow(10, -i) - (Math.pow(10, -i - 1) * j));
					if (!zeroTickFlag) { // did not do zero tick last iteration
						if (Math.abs(currentTickValue - 1.0) < 0.0001 && lowerBoundVal <= 0.0 && upperBoundVal >= 0.0) {
							// tick value is 1.0 and 0.0 is within data range
							currentTickValue = 0.0; // set tick value to zero
							zeroTickFlag = true; // indicate zero tick
						}
					} else { // did zero tick last iteration
						zeroTickFlag = false; // clear flag
					} // create tick label string:
					// show tick label if "1e#"-style and it's one
					// of the first two, if it's the first or last
					// in the set, or if it's 1-5; beyond that
					// show fewer as the values get larger:
					tickLabel = (j < 1) ? makeTickLabel(currentTickValue) : "";
				}
				if (currentTickValue > upperBoundVal) {
					return ticks; // if past highest data value then exit
									// method
				}
				if (currentTickValue >= lowerBoundVal - SMALL_LOG_VALUE) {
					// tick value not below lowest data value
					TextAnchor anchor;
					TextAnchor rotationAnchor;
					double angle = 0.0;
					if (isVerticalTickLabels()) {
						anchor = TextAnchor.CENTER_RIGHT;
						rotationAnchor = TextAnchor.CENTER_RIGHT;
						if (edge == RectangleEdge.TOP) {
							angle = Math.PI / 2.0;
						} else {
							angle = -Math.PI / 2.0;
						}
					} else {
						if (edge == RectangleEdge.TOP) {
							anchor = TextAnchor.BOTTOM_CENTER;
							rotationAnchor = TextAnchor.BOTTOM_CENTER;
						} else {
							anchor = TextAnchor.TOP_CENTER;
							rotationAnchor = TextAnchor.TOP_CENTER;
						}
					}
					Tick tick = new NumberTick(new Double(currentTickValue), tickLabel, anchor, rotationAnchor, angle);
					ticks.add(tick);
				}
			}
		}
		return ticks;

	}

	@Override
	protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {

		List ticks = new java.util.ArrayList();

		// get lower bound value:
		double lowerBoundVal = getRange().getLowerBound();
		// if small log values and lower bound value too small
		// then set to a small value (don't allow <= 0):
		if (this.smallLogFlag && lowerBoundVal < LOGZEROVALUE) {
	        lowerBoundVal = LOGZEROVALUE;
	    }		// get upper bound value
		double upperBoundVal = getRange().getUpperBound();

		// get log10 version of lower bound and round to integer:
		int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
		// get log10 version of upper bound and round to integer:
		int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));

		if (iBegCount == iEndCount && iBegCount > 0 && Math.pow(10, iBegCount) > lowerBoundVal) {
			// only 1 power of 10 value, it's > 0 and its resulting
			// tick value will be larger than lower bound of data
			--iBegCount; // decrement to generate more ticks
		}

		double tickVal;
		String tickLabel;
		boolean zeroTickFlag = false;
		for (int i = iBegCount; i <= iEndCount; i++) {
			// for each tick with a label to be displayed
			int jEndCount = 10;
			if (i == iEndCount) {
				jEndCount = 1;
			}

			for (int j = 0; j < jEndCount; j++) {
				// for each tick to be displayed
				if (this.smallLogFlag) {
					// small log values in use
					tickVal = Math.pow(10, i) + (Math.pow(10, i) * j);
					if (j == 0) {
						// first tick of group; create label text
						if (this.log10TickLabelsFlag) {
							// if flag then
							tickLabel = "10^" + i; // create "log10"-type label
						} else { // not "log10"-type label
							if (this.expTickLabelsFlag) {
								// if flag then
								tickLabel = "1e" + i; // create "1e#"-type label
							} else { // not "1e#"-type label
								NumberFormat format = getNumberFormatOverride();
								if (format != null) {
									tickLabel = format.format(tickVal);
								} else {
									tickLabel = Long.toString((long) Math.rint(tickVal));
								}
							}
						}
					} else { // not first tick to be displayed
						tickLabel = ""; // no tick label
					}
				} else { // not small log values in use; allow for values <= 0
					if (zeroTickFlag) { // if did zero tick last iter then
						--j;
					} // decrement to do 1.0 tick now
					tickVal = (i >= 0) ? Math.pow(10, i) + (Math.pow(10, i) * j)
							: -(Math.pow(10, -i) - (Math.pow(10, -i - 1) * j));
					if (j == 0) { // first tick of group
						if (!zeroTickFlag) { // did not do zero tick last
												// iteration
							if (i > iBegCount && i < iEndCount && Math.abs(tickVal - 1.0) < 0.0001) {
								// not first or last tick on graph and value
								// is 1.0
								tickVal = 0.0; // change value to 0.0
								zeroTickFlag = true; // indicate zero tick
								tickLabel = "0"; // create label for tick
							} else {
								// first or last tick on graph or value is 1.0
								// create label for tick:
								if (this.log10TickLabelsFlag) {
									// create "log10"-type label
									tickLabel = (((i < 0) ? "-" : "") + "10^" + Math.abs(i));
								} else {
									if (this.expTickLabelsFlag) {
										// create "1e#"-type label
										tickLabel = (((i < 0) ? "-" : "") + "1e" + Math.abs(i));
									} else {
										NumberFormat format = getNumberFormatOverride();
										if (format != null) {
											tickLabel = format.format(tickVal);
										} else {
											tickLabel = Long.toString((long) Math.rint(tickVal));
										}
									}
								}
							}
						} else { // did zero tick last iteration
							tickLabel = ""; // no label
							zeroTickFlag = false; // clear flag
						}
					} else { // not first tick of group
						tickLabel = ""; // no label
						zeroTickFlag = false; // make sure flag cleared
					}
				}

				if (tickVal > upperBoundVal) {
					return ticks; // if past highest data value then exit method
				}

				if (tickVal >= lowerBoundVal - SMALL_LOG_VALUE) {
					// tick value not below lowest data value
					TextAnchor anchor;
					TextAnchor rotationAnchor;
					double angle = 0.0;
					
					if (edge == RectangleEdge.LEFT) {
						anchor = TextAnchor.CENTER_RIGHT;
						rotationAnchor = TextAnchor.CENTER_RIGHT;
					} else {
						anchor = TextAnchor.CENTER_LEFT;
						rotationAnchor = TextAnchor.CENTER_LEFT;
					}
					
					// create tick object and add to list:
					ticks.add(new NumberTick(new Double(tickVal), tickLabel, anchor, rotationAnchor, angle));
				}
			}
		}
		return ticks;

	}
	
	@Override
	public void setRange(Range range) {

		Range adjustedRange = range;
		if (range.getLowerBound() == 0.0)
			adjustedRange = new Range(getLowerdecade(range.getLowerBound()), range.getUpperBound());
		setRange(adjustedRange, true, true);
	}

	private double getUpperdecade(double upperBound) {
		if (upperBound == 0)
			return 10;

		for (double decade : DECADES) {
			if (decade > upperBound)
				return decade;
		}

		return 0;
	}

	private double getLowerdecade(double lowerBound) {
		if (lowerBound <= 0)
			return LOGZEROVALUE;
		for (int i = DECADES.length - 1; i > 0; i--)
			if (DECADES[i] < lowerBound)
				return DECADES[i];

		return LOGZEROVALUE;
	}

	private int getLowerdecadeIndex(double lowerBound) {
		if (lowerBound <= 0)
			return 1;
		for (int i = DECADES.length - 1; i > 0; i--)
			if (DECADES[i] < lowerBound)
				return i;

		return 0;
	}

}
