package com.sciome.charts.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;

public class DraggableXYPointerAnnotation extends XYPointerAnnotation
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2197201087680962118L;
	private double				angle;
	private Stroke				arrowStroke;
	private Color				arrowPaint;
	private double				labelOffset;
	private double				arrowWidth;
	private double				arrowLength;
	private double				baseRadius;
	private double				tipRadius;

	public DraggableXYPointerAnnotation(String label, double x, double y, double angle)
	{
		super(label, x, y, angle);
		this.angle = angle;
		this.tipRadius = DEFAULT_TIP_RADIUS;
		this.baseRadius = DEFAULT_BASE_RADIUS;
		this.arrowLength = DEFAULT_ARROW_LENGTH;
		this.arrowWidth = DEFAULT_ARROW_WIDTH;
		this.labelOffset = DEFAULT_LABEL_OFFSET;
		this.arrowStroke = new BasicStroke(1.0f);
		this.arrowPaint = Color.BLACK;
	}

	private Shape hotspot = null;

	/**
	 * Draws the annotation.
	 *
	 * @param g2
	 *            the graphics device.
	 * @param plot
	 *            the plot.
	 * @param dataArea
	 *            the data area.
	 * @param domainAxis
	 *            the domain axis.
	 * @param rangeAxis
	 *            the range axis.
	 * @param rendererIndex
	 *            the renderer index.
	 * @param info
	 *            the plot rendering info.
	 */
	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis,
			ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info)
	{

		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
		RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
		double j2DX = domainAxis.valueToJava2D(getX(), dataArea, domainEdge);
		double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);
		if (orientation == PlotOrientation.HORIZONTAL)
		{
			double temp = j2DX;
			j2DX = j2DY;
			j2DY = temp;
		}
		double startX = j2DX + Math.cos(this.angle) * this.baseRadius;
		double startY = j2DY + Math.sin(this.angle) * this.baseRadius;

		double endX = j2DX + Math.cos(this.angle) * this.tipRadius;
		double endY = j2DY + Math.sin(this.angle) * this.tipRadius;

		double arrowBaseX = endX + Math.cos(this.angle) * this.arrowLength;
		double arrowBaseY = endY + Math.sin(this.angle) * this.arrowLength;

		double arrowLeftX = arrowBaseX + Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
		double arrowLeftY = arrowBaseY + Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

		double arrowRightX = arrowBaseX - Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
		double arrowRightY = arrowBaseY - Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

		GeneralPath arrow = new GeneralPath();
		arrow.moveTo((float) endX, (float) endY);
		arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
		arrow.lineTo((float) arrowRightX, (float) arrowRightY);
		arrow.closePath();

		g2.setStroke(this.arrowStroke);
		g2.setPaint(this.arrowPaint);
		Line2D line = new Line2D.Double(startX, startY, arrowBaseX, arrowBaseY);
		g2.draw(line);
		g2.fill(arrow);

		// draw the label
		double labelX = j2DX + Math.cos(this.angle) * (this.baseRadius + this.labelOffset);
		double labelY = j2DY + Math.sin(this.angle) * (this.baseRadius + this.labelOffset);
		g2.setFont(getFont());
		hotspot = TextUtils.calculateRotatedStringBounds(getText(), g2, (float) labelX, (float) labelY,
				getTextAnchor(), getRotationAngle(), getRotationAnchor());
		if (getBackgroundPaint() != null)
		{
			g2.setPaint(getBackgroundPaint());
			g2.fill(hotspot);
		}
		g2.setPaint(getPaint());
		TextUtils.drawRotatedString(getText(), g2, (float) labelX, (float) labelY, getTextAnchor(),
				getRotationAngle(), getRotationAnchor());
		if (isOutlineVisible())
		{
			g2.setStroke(getOutlineStroke());
			g2.setPaint(getOutlinePaint());
			g2.draw(hotspot);
		}

		String toolTip = getToolTipText();
		String url = getURL();
		if (toolTip != null || url != null)
		{
			addEntity(info, hotspot, rendererIndex, toolTip, url);
		}

	}

	public double getAngle()
	{
		return angle;
	}

	public void setAngle(double angle)
	{
		this.angle = angle;
	}

	public Stroke getArrowStroke()
	{
		return arrowStroke;
	}

	public void setArrowStroke(Stroke arrowStroke)
	{
		this.arrowStroke = arrowStroke;
	}

	public Color getArrowPaint()
	{
		return arrowPaint;
	}

	public void setArrowPaint(Color arrowPaint)
	{
		this.arrowPaint = arrowPaint;
	}

	public double getLabelOffset()
	{
		return labelOffset;
	}

	public void setLabelOffset(double labelOffset)
	{
		this.labelOffset = labelOffset;
	}

	public double getArrowWidth()
	{
		return arrowWidth;
	}

	public void setArrowWidth(double arrowWidth)
	{
		this.arrowWidth = arrowWidth;
	}

	public double getArrowLength()
	{
		return arrowLength;
	}

	public void setArrowLength(double arrowLength)
	{
		this.arrowLength = arrowLength;
	}

	public double getBaseRadius()
	{
		return baseRadius;
	}

	public void setBaseRadius(double baseRadius)
	{
		this.baseRadius = baseRadius;
	}

	public double getTipRadius()
	{
		return tipRadius;
	}

	public void setTipRadius(double tipRadius)
	{
		this.tipRadius = tipRadius;
	}

	public Shape getHotSpot()
	{
		return hotspot;
	}

}
