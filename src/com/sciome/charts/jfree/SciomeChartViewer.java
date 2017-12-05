package com.sciome.charts.jfree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ZoomHandlerFX;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;

public class SciomeChartViewer extends ChartViewer {
	public static final double CHART_WIDTH = 500;
	public static final double CHART_HEIGHT = 500;
	
	public SciomeChartViewer(JFreeChart chart) {
		super(chart);
		ChartCanvas canvas = getCanvas();
		
		//Remove the preivous zoom handler and add one that activates only when you hold shift
		ZoomHandlerFX zoom = new ZoomHandlerFX("new", this, false, false, false, true);
		canvas.removeMouseHandler(canvas.getMouseHandler("zoom"));
		canvas.addMouseHandler(zoom);
		canvas.setDomainZoomable(false);
		canvas.setRangeZoomable(false);
		canvas.setHeight(CHART_HEIGHT);
		canvas.setWidth(CHART_WIDTH);
	}
	
	public void zoom(double x, double h, double y, double w) {
		Zoomable plot = (Zoomable)this.getChart().getPlot();
		
		Point2D startPoint = new Point2D.Double(x,h);
		Point2D endPoint = new Point2D.Double(y,w);
        Rectangle2D dataArea = getCanvas().findDataArea(startPoint);
		
		double pw0 = percentW(x, dataArea);
        double pw1 = percentW(x + w, dataArea);
        double ph0 = percentH(y, dataArea);
        double ph1 = percentH(y + h, dataArea);
        
        PlotRenderingInfo info = this.getRenderingInfo().getPlotInfo();
		 if (plot.getOrientation().isVertical()) {
             plot.zoomDomainAxes(pw0, pw1, info, endPoint);
             plot.zoomRangeAxes(1 - ph1, 1 - ph0, info, endPoint);
         } else {
             plot.zoomRangeAxes(pw0, pw1, info, endPoint);
             plot.zoomDomainAxes(1 - ph1, 1 - ph0, info, endPoint);
         }
	}
	
	private double percentW(double x, Rectangle2D r) {
        return (x - r.getMinX()) / r.getWidth();
    }
    
    private double percentH(double y, Rectangle2D r) {
        return (y - r.getMinY()) / r.getHeight();
    }
}
