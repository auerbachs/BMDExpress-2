package com.sciome.charts.venndis;

// Copyright (C) 2014 Vladimir Ignatchenko (vladimirsign@gmail.com)
// Dr. Thomas Kislinger laboratory (http://kislingerlab.uhnres.utoronto.ca/)
//
// This file is part of VennDIS software.
// VennDIS is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// VennDIS is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with VennDIS. If not, see <http://www.gnu.org/licenses/>.

import java.util.ArrayList;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ScrollPane;

public class Euler2 extends VennDiagram{
	public Ellipse		circleB;
	public Text 		headerB;

	public Euler2(BorderPane borderPane, VennCalc vennCalc) {
		super(borderPane, vennCalc);
	}

	@Override
	protected void setDefaultTabPane() {
		vennTP.addPropertiesTab();
		vennTP.addTabA();
		vennTP.addTabB();
		vennTP.fillColorPickerA.setValue(Color.rgb(255,0,0,0.7));
		vennTP.fillColorPickerB.setValue(Color.rgb(0,255,0,0.7));
		vennTP.sliderFillOpacityA.setValue(0.7);
		vennTP.sliderFillOpacityB.setValue(0.7);
		vennTP.fontSize.valueProperty().addListener(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontFamily.valueProperty().addListener(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontWeight.setOnAction(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontPosture.setOnAction(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontUnderline.setOnAction(e->{ setFontUnderline(); });
		vennTP.fontShadow.setOnAction(e->{ setFontShadow(); });
		vennTP.valuesColorPicker.setOnAction(e->{ setValuesColor(); });
		vennTP.colorPickerBG.setOnAction(e->{ setBGColor(); });
		vennTP.strokeStyleA.valueProperty().addListener(e->{ setOutlineStyleA(); });
		vennTP.strokeStyleB.valueProperty().addListener(e->{ setOutlineStyleB(); });
		vennTP.sliderOverlapCirclesAB.setDisable(true);
		vennTP.cbBG.setOnAction(e->{ 
				if (vennTP.cbBG.isSelected()) { 
					vennTP.colorPickerBG.setDisable(false); 
					setBGColor();
				} else { 
					vennTP.colorPickerBG.setDisable(true);
					vennPlot.setStyle("-fx-background-color: rgba(255,255,255,0);");
					bg.setFill(Color.rgb(255,255,255,0));
				} 
			});
		vennTP.sliderValuesAngle.valueProperty().addListener(e->{ 
				for (int i=1; i<vCount; i++) {
					vennValue[i].setRotate(vennTP.sliderValuesAngle.getValue());
				}
			});
		vennTP.fillColorPickerA.setOnAction(e->{ 
				circleA.setFill(vennTP.fillColorPickerA.getValue());
				double o = vennTP.fillColorPickerA.getValue().getOpacity();
				vennTP.sliderFillOpacityA.setValue(o); 
			});
		vennTP.sliderFillOpacityA.valueProperty().addListener(e->{
				double r = vennTP.fillColorPickerA.getValue().getRed();
				double g = vennTP.fillColorPickerA.getValue().getGreen();
				double b = vennTP.fillColorPickerA.getValue().getBlue();
				double o = vennTP.sliderFillOpacityA.getValue();
				vennTP.fillColorPickerA.setValue(Color.color(r, g, b, o)); 
				circleA.setFill(Color.color(r, g, b, o));
			});
		vennTP.sliderStrokeWidthA.valueProperty().addListener(e->{ circleA.setStrokeWidth(vennTP.sliderStrokeWidthA.getValue()); });
		vennTP.strokeColorPickerA.setOnAction(e->{ circleA.setStroke(vennTP.strokeColorPickerA.getValue()); });
		vennTP.btnToFrontA.setOnAction(e->{ circleA.toFront(); });
		vennTP.btnToBackA.setOnAction(e->{ circleA.toBack(); });
		vennTP.datasetHeaderA.textProperty().addListener(e->{ headerA.setText(vennTP.datasetHeaderA.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeA.valueProperty().addListener(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyA.valueProperty().addListener(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightA.setOnAction(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureA.setOnAction(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineA.setOnAction(e->{ setFontUnderlineA(); });
		vennTP.fontShadowA.setOnAction(e->{ setFontShadowA(); });
		vennTP.titleColorPickerA.setOnAction(e->{headerA.setFill( vennTP.titleColorPickerA.getValue()); });
		vennTP.sliderTitleAngleA.valueProperty().addListener(e->{ headerA.setRotate(vennTP.sliderTitleAngleA.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fillColorPickerB.setOnAction(e->{ 
				circleB.setFill(vennTP.fillColorPickerB.getValue());
				double o = vennTP.fillColorPickerB.getValue().getOpacity();
				vennTP.sliderFillOpacityB.setValue(o); 
			});
		vennTP.sliderFillOpacityB.valueProperty().addListener(e->{
				double r = vennTP.fillColorPickerB.getValue().getRed();
				double g = vennTP.fillColorPickerB.getValue().getGreen();
				double b = vennTP.fillColorPickerB.getValue().getBlue();
				double o = vennTP.sliderFillOpacityB.getValue();
				vennTP.fillColorPickerB.setValue(Color.color(r, g, b, o)); 
				circleB.setFill(Color.color(r, g, b, o));
			});
		vennTP.sliderStrokeWidthB.valueProperty().addListener(e->{ circleB.setStrokeWidth(vennTP.sliderStrokeWidthB.getValue()); });
		vennTP.strokeColorPickerB.setOnAction(e->{ circleB.setStroke(vennTP.strokeColorPickerB.getValue()); });
		vennTP.btnToFrontB.setOnAction(e->{ circleB.toFront(); });
		vennTP.btnToBackB.setOnAction(e->{ circleB.toBack(); });
		vennTP.datasetHeaderB.textProperty().addListener(e->{ headerB.setText(vennTP.datasetHeaderB.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeB.valueProperty().addListener(e->{ setFontB(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyB.valueProperty().addListener(e->{ setFontB(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightB.setOnAction(e->{ setFontB(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureB.setOnAction(e->{ setFontB(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineB.setOnAction(e->{ setFontUnderlineB(); });
		vennTP.fontShadowB.setOnAction(e->{ setFontShadowB(); });
		vennTP.titleColorPickerB.setOnAction(e->{ headerB.setFill(vennTP.titleColorPickerB.getValue()); });
		vennTP.sliderTitleAngleB.valueProperty().addListener(e->{ headerB.setRotate(vennTP.sliderTitleAngleB.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.totalCountA.setText(vCalc.getSizeA()+"");
		vennTP.totalCountB.setText(vCalc.getSizeB()+"");
	}

	public void setBGColor() {
		String rgbaColor = "rgba("+(vennTP.colorPickerBG.getValue().getRed()*255)+","+(vennTP.colorPickerBG.getValue().getGreen()*255)+","+(vennTP.colorPickerBG.getValue().getBlue()*255)+","+vennTP.colorPickerBG.getValue().getOpacity()+")";
		vennPlot.setStyle("-fx-background-color:"+rgbaColor+";");
		bg.setFill(vennTP.colorPickerBG.getValue());
	}

	public void setValuesColor() {
		for (int i=1; i<vCount; i++) {
			vennValue[i].setFill(vennTP.valuesColorPicker.getValue());
		}
	}

	public void setFont() {
		if (vennTP.fontWeight.isSelected() && vennTP.fontPosture.isSelected()) {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setFont(Font.font(vennTP.fontFamily.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSize.getValue()));
			}
		} else if ((vennTP.fontWeight.isSelected()) && !(vennTP.fontPosture.isSelected())) {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setFont(Font.font(vennTP.fontFamily.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSize.getValue()));
			}
		} else if (!(vennTP.fontWeight.isSelected()) && (vennTP.fontPosture.isSelected())) {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setFont(Font.font(vennTP.fontFamily.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSize.getValue()));
			}
		} else {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setFont(Font.font(vennTP.fontFamily.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSize.getValue()));
			}
		}
	}

	public void setFontUnderline() {
		if (vennTP.fontUnderline.isSelected()) {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setUnderline(true);
			}
		} else {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setUnderline(false);
			}
		}
	}

	public void setFontShadow() {
		if (vennTP.fontShadow.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			for (int i=1; i<vCount; i++) {
				vennValue[i].setEffect(dropShadow);
			}
		} else {
			for (int i=1; i<vCount; i++) {
				vennValue[i].setEffect(null);
			}
		}
	}

	public void setOutlineStyleA() {
		if (vennTP.strokeStyleA.getValue()=="Dashes") {
			circleA.setStyle("-fx-stroke-dash-array: 10 20 10 20; -fx-stroke-line-cap: round;");
		} else if (vennTP.strokeStyleA.getValue()=="Dots") {
			circleA.setStyle("-fx-stroke-dash-array: 1 15 1 15; -fx-stroke-line-cap: round;");
		} else {
			circleA.setStyle("");
		}
	}

	public void setFontA() {
//		if (vennTP.fontWeightA.isSelected() && vennTP.fontPostureA.isSelected()) {
//			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeA.getValue()));
//		} else if ((vennTP.fontWeightA.isSelected()) && !(vennTP.fontPostureA.isSelected())) {
//			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeA.getValue()));
//		} else if (!(vennTP.fontWeightA.isSelected()) && (vennTP.fontPostureA.isSelected())) {
//			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeA.getValue()));
//		} else {
//			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeA.getValue()));
//		}
	}

	public void setFontUnderlineA() {
		if (vennTP.fontUnderlineA.isSelected()) {
			headerA.setUnderline(true);
		} else {
			headerA.setUnderline(false);
		}
	}

	public void setFontShadowA() {
		if (vennTP.fontShadowA.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			headerA.setEffect(dropShadow);
		} else {
			headerA.setEffect(null);
		}
	}

	public void setOutlineStyleB() {
		if (vennTP.strokeStyleB.getValue()=="Dashes") {
			circleB.setStyle("-fx-stroke-dash-array: 10 20 10 20; -fx-stroke-line-cap: round;");
		} else if (vennTP.strokeStyleB.getValue()=="Dots") {
			circleB.setStyle("-fx-stroke-dash-array: 1 15 1 15; -fx-stroke-line-cap: round;");
		} else {
			circleB.setStyle("");
		}
	}

	public void setFontB() {
//		if (vennTP.fontWeightB.isSelected() && vennTP.fontPostureB.isSelected()) {
//			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeB.getValue()));
//		} else if ((vennTP.fontWeightB.isSelected()) && !(vennTP.fontPostureB.isSelected())) {
//			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeB.getValue()));
//		} else if (!(vennTP.fontWeightB.isSelected()) && (vennTP.fontPostureB.isSelected())) {
//			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeB.getValue()));
//		} else {
//			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeB.getValue()));
//		}
	}

	public void setFontUnderlineB() {
		if (vennTP.fontUnderlineB.isSelected()) {
			headerB.setUnderline(true);
		} else {
			headerB.setUnderline(false);
		}
	}

	public void setFontShadowB() {
		if (vennTP.fontShadowB.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			headerB.setEffect(dropShadow);
		} else {
			headerB.setEffect(null);
		}
	}

	public void setValues() {
		int[] overlap = vCalc.getOverlap();
		switch (vCalc.getVennType()) {
			case 3:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				break;
			case 5:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				break;
			case 6:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				break;
		}
	}

	@Override
	protected void setVennType() {
		int[] overlap = vCalc.getOverlap();
		double a = (double)(overlap[1]+overlap[3]);
		double b = (double)(overlap[2]+overlap[3]);
		double r = 0;
		switch (vCalc.getVennType()) {
			case 3:
				drawEuler003();
				vCount = 3;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennTP.sliderSizeCircleA.valueProperty().addListener(e->{ 
					double rA  =  1.8 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleA.getValue()/100));
					double rB  =  1.8 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleB.getValue()/100));
					circleA.setRadiusX(rA);
					circleA.setRadiusY(rA);
					circleB.setRadiusX(rB);
					circleB.setRadiusY(rB);
					headerA.setY(370 - rA); 
					headerB.setY(370 - rB); 
				});
				vennTP.sliderSizeCircleB.valueProperty().addListener(e->{ 
					double rA  =  1.8 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleA.getValue()/100));
					double rB  =  1.8 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleB.getValue()/100));
					circleA.setRadiusX(rA);
					circleA.setRadiusY(rA);
					circleB.setRadiusX(rB);
					circleB.setRadiusY(rB);
					headerA.setY(370 - rA); 
					headerB.setY(370 - rB); 
				});
				if (a==b) {
					vennTP.sliderSizeCircleA.setValue(100);
					vennTP.sliderSizeCircleB.setValue(100);	
				}
				if (a>b && b>0) {
					vennTP.sliderSizeCircleA.setValue(100);
					vennTP.sliderSizeCircleB.setValue(b/a*100);
				}
				if (a<b && a>0) {
					vennTP.sliderSizeCircleB.setValue(100);
					vennTP.sliderSizeCircleA.setValue(a/b*100);
				}
				break;
			case 5:
				drawEuler005();
				vCount = 3;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.valueProperty().addListener(e->{
					double rB  =  2.5 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleB.getValue()/100));
					circleB.setRadiusX(rB);
					circleB.setRadiusY(rB);
					circleB.setCenterY(650 - rB);
					setTextValuesAlignmentToCenter();
	        		});
				if (a==b) {
					vennTP.sliderSizeCircleB.setValue(100);	
				}
				if (a>b && b>0) {
					vennTP.sliderSizeCircleB.setValue(b/a*100);
				}
				break;
			case 6:
				drawEuler006();
				vCount = 3;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennTP.sliderSizeCircleA.valueProperty().addListener(e->{
					double rA  =  2.5 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleA.getValue()/100));
					circleA.setRadiusX(rA);
					circleA.setRadiusY(rA);
					circleA.setCenterY(150 + rA);
					setTextValuesAlignmentToCenter();
        			});
				vennTP.sliderSizeCircleB.setDisable(true);
				if (a==b) {
					vennTP.sliderSizeCircleA.setValue(100);	
				}
				if (a<b && a>0) {
					vennTP.sliderSizeCircleA.setValue(a/b*100);
				}
				break;
		}
		if (!vCalc.getHeaderA().equals("")) { vennTP.datasetHeaderA.setText(vCalc.getHeaderA()); }
		if (!vCalc.getHeaderB().equals("")) { vennTP.datasetHeaderB.setText(vCalc.getHeaderB()); }
		setFont(); setFontA(); setFontB();
		setTextAlignmentToCenter();
		setTextValuesAlignmentToCenter();
	}

	private void repositionAnchor() {
		double angleA = headerA.getRotate();
		double angleB = headerB.getRotate();
		if (angleA==360.0) { angleA = 0; }
		if (angleB==360.0) { angleB = 0; }
		double x = 0;
		double xA = headerA.getX() + headerA.getLayoutX() - (headerA.getLayoutBounds().getHeight()/4);
		double xB = headerB.getX() + headerB.getLayoutX() - (headerB.getLayoutBounds().getHeight()/4);
		if (xA<xB) { x = xA; }
		if (xB<xA) { x = xB; }
		if (x>0) { x = 0; }
		anchorPoint.setX(x);
		double y = 0;
		double yA = headerA.getY() + headerA.getLayoutY()-headerA.getFont().getSize()-(headerA.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleA)));
		double yB = headerB.getY() + headerB.getLayoutY()-headerB.getFont().getSize()-(headerB.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleB)));
		if (yA<yB) { y = yA; }
		if (yB<yA) { y = yB; }
		if (y>0) { y = 0; }
		anchorPoint.setY(y);
	}

	private void setTextAlignmentToCenter() {
		switch (vCalc.getVennType()) {
			case 3:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				break;
			case 5:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				break;
			case 6:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				break;
		}
	}

	private void setTextValuesAlignmentToCenter() {
		switch (vCalc.getVennType()) {
			case 3:
				vennValue[1].setX(200-vennValue[1].getLayoutBounds().getWidth()/2);
				vennValue[2].setX(600-vennValue[2].getLayoutBounds().getWidth()/2);
				vennValue[1].setY(400+vennValue[1].getLayoutBounds().getHeight()/4);
				vennValue[2].setY(400+vennValue[2].getLayoutBounds().getHeight()/4);
				break;
			case 5:
				vennValue[1].setX(400-vennValue[1].getLayoutBounds().getWidth()/2);
				vennValue[2].setX(400-vennValue[2].getLayoutBounds().getWidth()/2);
				vennValue[1].setY(((circleA.getCenterY()-circleA.getRadiusY())+(circleB.getCenterY()-circleB.getRadiusY()))/2 + vennValue[1].getLayoutBounds().getHeight()/4);
				vennValue[2].setY(circleB.getCenterY() + vennValue[2].getLayoutBounds().getHeight()/4);
				break;
			case 6:
				vennValue[1].setX(400-vennValue[1].getLayoutBounds().getWidth()/2);
				vennValue[2].setX(400-vennValue[2].getLayoutBounds().getWidth()/2);
				vennValue[1].setY(((circleA.getCenterY()+circleA.getRadiusY())+(circleB.getCenterY()+circleB.getRadiusY()))/2 + vennValue[2].getLayoutBounds().getHeight()/4);
				vennValue[2].setY(circleA.getCenterY() + vennValue[1].getLayoutBounds().getHeight()/4);
				break;
		}
	}

	private void drawEuler003() {
		vennPlot = new BorderPane();
		vennPlot.setMaxWidth(800);
		vennPlot.setMaxHeight(800);
		vennPlot.setStyle("-fx-background-color: rgba(255,255,255,1);");
		Group gBox = new Group();
		bg = new Rectangle(0, 0, 800, 800);
		bg.setFill(Color.rgb(255, 255, 255, 1));
		bg.setId("bg");
		bg.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
        		});
		anchorPoint = new Rectangle(0, 0, 1, 1);
		anchorPoint.setId("anchorPoint");
		anchorPoint.setFill(Color.rgb(0, 0, 0, 0));
		Group cBox = new Group();
		cBox.setId("cBox");
		circleA = new Ellipse(200, 400, 180, 180);
		circleA.setFill(Color.rgb(255, 0, 0, 0.7));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(600, 400, 180, 180);
		circleB.setFill(Color.rgb(0, 255, 0, 0.7));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		cBox.getChildren().addAll(circleA, circleB);
		headerA = new Text(200, 190, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 190, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		for (int i=1; i<3; i++) {
			vennValue[i] = new Text();
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setId(""+i);
			vennValue[i].getStyleClass().add("vennValueText");
			vennValue[i].setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
				ArrayList<ArrayList <String>> vennOverlapList = vCalc.getOverlapList(); 
				Text t = (Text)e.getSource();
				String s = "";
				for (int j=0; j<vennOverlapList.get(Integer.parseInt(t.getId())).size(); j++){
					s += vennOverlapList.get(Integer.parseInt(t.getId())).get(j)+"\n";
				}
				vennTP.overlapTextArea.setText(s);
        		});

			gBox.getChildren().add(vennValue[i]);
		}
		gBox.getChildren().addAll(headerA, headerB);
		vennPlot.setCenter(gBox);
	}

	private void  drawEuler005() {
		vennPlot = new BorderPane();
		vennPlot.setMaxWidth(800);
		vennPlot.setMaxHeight(800);
		vennPlot.setStyle("-fx-background-color: rgba(255,255,255,1);");
		Group gBox = new Group();
		bg = new Rectangle(0, 0, 800, 800);
		bg.setFill(Color.rgb(255, 255, 255, 1));
		bg.setId("bg");
		bg.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
        		});
		anchorPoint = new Rectangle(0, 0, 1, 1);
		anchorPoint.setId("anchorPoint");
		anchorPoint.setFill(Color.rgb(0, 0, 0, 0));
		Group cBox = new Group();
		cBox.setId("cBox");
		circleA = new Ellipse(400, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.7));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 500, 200, 200);
		circleB.setFill(Color.rgb(0, 255, 0, 0.7));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		cBox.getChildren().addAll(circleA, circleB);
		headerA = new Text(400, 100, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 730, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		for (int i=1; i<3; i++) {
			vennValue[i] = new Text();
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].getStyleClass().add("vennValueText");
			vennValue[i].setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
				ArrayList<ArrayList <String>> vennOverlapList = vCalc.getOverlapList(); 
				Text t = (Text)e.getSource();
				String s = "";
				for (int j=0; j<vennOverlapList.get(Integer.parseInt(t.getId())).size(); j++){
					s += vennOverlapList.get(Integer.parseInt(t.getId())).get(j)+"\n";
				}
				vennTP.overlapTextArea.setText(s);
        		});

			gBox.getChildren().add(vennValue[i]);
		}
		gBox.getChildren().addAll(headerA, headerB);
		vennPlot.setCenter(gBox);
	}

	private void drawEuler006() {
		vennPlot = new BorderPane();
		vennPlot.setMaxWidth(800);
		vennPlot.setMaxHeight(800);
		vennPlot.setStyle("-fx-background-color: rgba(255,255,255,1);");
		Group gBox = new Group();
		bg = new Rectangle(0, 0, 800, 800);
		bg.setFill(Color.rgb(255, 255, 255, 1));
		bg.setId("bg");
		bg.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
        		});
		anchorPoint = new Rectangle(0, 0, 1, 1);
		anchorPoint.setId("anchorPoint");
		anchorPoint.setFill(Color.rgb(0, 0, 0, 0));
		Group cBox = new Group();
		cBox.setId("cBox");
		circleB = new Ellipse(400, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.7));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleA = new Ellipse(400, 300, 200, 200);
		circleA.setFill(Color.rgb(255, 0, 0, 0.7));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		cBox.getChildren().addAll(circleB, circleA);
		headerA = new Text(400, 100, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 730, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		for (int i=1; i<3; i++) {
			vennValue[i] = new Text();
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].getStyleClass().add("vennValueText");
			vennValue[i].setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(0);
				ArrayList<ArrayList <String>> vennOverlapList = vCalc.getOverlapList(); 
				Text t = (Text)e.getSource();
				String s = "";
				for (int j=0; j<vennOverlapList.get(Integer.parseInt(t.getId())).size(); j++){
					s += vennOverlapList.get(Integer.parseInt(t.getId())).get(j)+"\n";
				}
				vennTP.overlapTextArea.setText(s);
        		});

			gBox.getChildren().add(vennValue[i]);
		}
		gBox.getChildren().addAll(headerA, headerB);
		vennPlot.setCenter(gBox);
	}
}


