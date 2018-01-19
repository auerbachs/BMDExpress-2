package com.sciome.charts.jfree;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.AbstractMouseHandlerFX;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.ShapeUtils;

import javafx.embed.swing.SwingNode;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

public class SciomeChartViewer extends ChartViewer
{
	public static final double	CHART_WIDTH		= 500;
	public static final double	CHART_HEIGHT	= 500;

	public SciomeChartViewer(JFreeChart chart)
	{
		this(chart, CHART_WIDTH, CHART_HEIGHT);
	}

	public SciomeChartViewer(JFreeChart chart, double width, double height)
	{
		super(chart);
		ChartCanvas canvas = getCanvas();

		// Remove the zoom handler because zoom is now done with rangeslider
		// ZoomHandlerFX zoom = new ZoomHandlerFX("new", this, false, false, false, true);
		canvas.removeMouseHandler(canvas.getMouseHandler("zoom"));
		// canvas.addMouseHandler(zoom);
		addDragDropMouseHandler();
		canvas.setDomainZoomable(false);
		canvas.setRangeZoomable(false);

		// Set height and width of chart
		canvas.setHeight(width);
		canvas.setWidth(height);

		addProperties();
	}

	public ChartEntity getEntity(double x, double y)
	{
		return getCanvas().getRenderingInfo().getEntityCollection().getEntity(x, y);
	}

	private void addDragDropMouseHandler()
	{
		getCanvas().addMouseHandler(new AbstractMouseHandlerFX("drag", false, false, false, false) {
			ChartEntity						drag	= null;
			DraggableXYPointerAnnotation	dragAnn	= null;

			@Override
			public void handleMousePressed(ChartCanvas canvas, MouseEvent e)
			{
				if (getChart().getPlot() instanceof XYPlot)
				{
					ChartEntity entity = getEntity(e.getX(), e.getY());
					for (Object ann : getChart().getXYPlot().getAnnotations())
					{
						if (ann instanceof DraggableXYPointerAnnotation)
						{
							if (((DraggableXYPointerAnnotation) ann).getHotSpot().contains(e.getX(),
									e.getY()))
								dragAnn = (DraggableXYPointerAnnotation) ann;
						}

					}

					if (entity != null && entity instanceof XYAnnotationEntity)
						drag = entity;
				}
			}

			@Override
			public void handleMouseDragged(ChartCanvas canvas, MouseEvent e)
			{
				if (getChart().getPlot() instanceof XYPlot)
				{
					if (dragAnn != null)
					{
						Point2D pt = new Point2D.Double(e.getX(), e.getY());
						Rectangle2D dataArea = canvas.findDataArea(pt);

						PlotOrientation orientation = getChart().getXYPlot().getOrientation();
						RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
								getChart().getXYPlot().getDomainAxisLocation(), orientation);
						RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
								getChart().getXYPlot().getRangeAxisLocation(), orientation);
						double j2DX = getChart().getXYPlot().getDomainAxis().valueToJava2D(dragAnn.getX(),
								dataArea, domainEdge);
						double j2DY = getChart().getXYPlot().getRangeAxis().valueToJava2D(dragAnn.getY(),
								dataArea, rangeEdge);

						Point2D dropPoint = ShapeUtils.getPointInRectangle(e.getX(), e.getY(), dataArea);
						double distnace = Point.distance(j2DX, j2DY, dropPoint.getX(), dropPoint.getY());

						Point2D sourcePoint = new Point2D.Double(j2DX, j2DY);
						dragAnn.setAngle(getAngle(sourcePoint, dropPoint));
						dragAnn.setBaseRadius(distnace);

						dragAnn.getNotify();
						getChart().fireChartChanged();
					}
				}
			}

			@Override
			public void handleMouseReleased(ChartCanvas canvas, MouseEvent e)
			{
				if (getChart().getPlot() instanceof XYPlot)
				{
					if (dragAnn != null)
					{
						Point2D pt = new Point2D.Double(e.getX(), e.getY());
						Rectangle2D dataArea = canvas.findDataArea(pt);

						PlotOrientation orientation = getChart().getXYPlot().getOrientation();
						RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
								getChart().getXYPlot().getDomainAxisLocation(), orientation);
						RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
								getChart().getXYPlot().getRangeAxisLocation(), orientation);
						double j2DX = getChart().getXYPlot().getDomainAxis().valueToJava2D(dragAnn.getX(),
								dataArea, domainEdge);
						double j2DY = getChart().getXYPlot().getRangeAxis().valueToJava2D(dragAnn.getY(),
								dataArea, rangeEdge);

						Point2D dropPoint = ShapeUtils.getPointInRectangle(e.getX(), e.getY(), dataArea);
						double distnace = Point.distance(j2DX, j2DY, dropPoint.getX(), dropPoint.getY());
						Point2D sourcePoint = new Point2D.Double(j2DX, j2DY);
						dragAnn.setAngle(getAngle(sourcePoint, dropPoint));
						dragAnn.setBaseRadius(distnace);
						dragAnn.getNotify();
						// e.consume();

					}
					drag = null;
					dragAnn = null;
					canvas.clearLiveHandler();
				}
			}
		});
	}

	private double getAngle(Point2D source, Point2D destination)
	{
		double xDiff = source.getX() - destination.getX();
		double yDiff = source.getY() - destination.getY();
		return Math.atan2(yDiff, xDiff) + Math.PI;
	}

	private void addProperties()
	{
		MenuItem properties = new MenuItem("Properties");
		properties.setOnAction(e -> handleProperties());
		getContextMenu().getItems().add(properties);
	}

	private void handleProperties()
	{
		org.jfree.chart.editor.ChartEditor editor = org.jfree.chart.editor.ChartEditorManager
				.getChartEditor(this.getChart());
		JPanel panel = new JPanel();
		panel.add((JComponent) editor);
		SwingNode node = new SwingNode();
		node.setContent((JComponent) editor);
		node.setFocusTraversable(true);

		DialogPane dialogPane = new DialogPane();
		dialogPane.getButtonTypes().add(ButtonType.OK);
		dialogPane.setContent(node);
		dialogPane.setMinHeight(600);
		dialogPane.setMinWidth(600);

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Chart Editor");
		dialog.setDialogPane(dialogPane);
		dialog.setResizable(true);
		dialog.setResultConverter(buttonType ->
		{
			if (buttonType == ButtonType.OK)
			{
				editor.updateChart(getChart());
			}
			return "";
		});

		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.show();
	}
}
