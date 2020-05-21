package com.sciome.charts.venndis;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public abstract class VennDiagram {
	public VennTabPane vennTP;
	public BorderPane vennPlot;
	public VennCalc vCalc;
	public Rectangle bg, anchorPoint;
	public Ellipse circleA;
	public Text headerA;
	public Text[] vennValue = new Text[32];
	protected int vCount;
	protected MoveObjectEvent moveEvent = new MoveObjectEvent();
	protected final String defaultFont = "System";
	
	public VennDiagram(BorderPane borderPane, VennCalc vennCalc) {
		vennTP = new VennTabPane(borderPane);
		vCalc = vennCalc;
		setDefaultTabPane();
		setVennType();
		ScrollPane vennPlotSP = new ScrollPane();
		BorderPane vennPlotInnerBP = new BorderPane();
		vennPlotInnerBP.setCenter(vennPlot);
		vennPlotInnerBP.setId("mainBorderPane");
		vennPlotInnerBP.prefHeightProperty().bind(vennPlotSP.heightProperty());
		vennPlotInnerBP.prefWidthProperty().bind(vennPlotSP.widthProperty());
		vennPlotSP.setContent(vennPlotInnerBP);
		borderPane.setCenter(vennPlotSP);
	}
	
	protected abstract void setDefaultTabPane();
	protected abstract void setVennType();
}
