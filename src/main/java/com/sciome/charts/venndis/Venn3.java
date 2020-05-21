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

public class Venn3 extends VennDiagram{
	public Ellipse circleB, circleC;
	public Text headerB, headerC;

	public Venn3(BorderPane borderPane, VennCalc vennCalc) {
		super(borderPane, vennCalc);
	}

	@Override
	protected void setDefaultTabPane() {
		vennTP.addPropertiesTab();
		vennTP.addTabA();
		vennTP.addTabB();
		vennTP.addTabC();
		vennTP.fillColorPickerA.setValue(Color.rgb(255,255,0,0.5));
		vennTP.fillColorPickerB.setValue(Color.rgb(0,255,255,0.5));
		vennTP.fillColorPickerC.setValue(Color.rgb(255,0,255,0.5));
		vennTP.sliderFillOpacityA.setValue(0.5);
		vennTP.sliderFillOpacityB.setValue(0.5);
		vennTP.sliderFillOpacityC.setValue(0.5);
		vennTP.fontSize.valueProperty().addListener(e->{ setFont(); setEllipseAndValuesPosition(); });
		vennTP.fontFamily.valueProperty().addListener(e->{ setFont(); setEllipseAndValuesPosition(); });
		vennTP.fontWeight.setOnAction(e->{ setFont(); setEllipseAndValuesPosition(); });
		vennTP.fontPosture.setOnAction(e->{ setFont(); setEllipseAndValuesPosition(); });
		vennTP.fontUnderline.setOnAction(e->{ setFontUnderline(); });
		vennTP.fontShadow.setOnAction(e->{ setFontShadow(); });
		vennTP.valuesColorPicker.setOnAction(e->{ setValuesColor(); });
		vennTP.colorPickerBG.setOnAction(e->{ setBGColor(); });
		vennTP.strokeStyleA.valueProperty().addListener(e->{ setOutlineStyleA(); });
		vennTP.strokeStyleB.valueProperty().addListener(e->{ setOutlineStyleB(); });
		vennTP.strokeStyleC.valueProperty().addListener(e->{ setOutlineStyleC(); });
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
		vennTP.sliderTitleAngleA.valueProperty().addListener(e->{ headerA.setRotate(vennTP.sliderTitleAngleA.getValue()); repositionAnchor(); });
		vennTP.sliderSizeCircleA.valueProperty().addListener(e->{ setEllipseSize(); });
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
		vennTP.sliderTitleAngleB.valueProperty().addListener(e->{ headerB.setRotate(vennTP.sliderTitleAngleB.getValue()); repositionAnchor(); });
		vennTP.sliderSizeCircleB.valueProperty().addListener(e->{ setEllipseSize(); });
		vennTP.fillColorPickerC.setOnAction(e->{ 
				circleC.setFill(vennTP.fillColorPickerC.getValue());
				double o = vennTP.fillColorPickerC.getValue().getOpacity();
				vennTP.sliderFillOpacityC.setValue(o); 
			});
		vennTP.sliderFillOpacityC.valueProperty().addListener(e->{
				double r = vennTP.fillColorPickerC.getValue().getRed();
				double g = vennTP.fillColorPickerC.getValue().getGreen();
				double b = vennTP.fillColorPickerC.getValue().getBlue();
				double o = vennTP.sliderFillOpacityC.getValue();
				vennTP.fillColorPickerC.setValue(Color.color(r, g, b, o)); 
				circleC.setFill(Color.color(r, g, b, o));
			});
		vennTP.sliderStrokeWidthC.valueProperty().addListener(e->{ circleC.setStrokeWidth(vennTP.sliderStrokeWidthC.getValue()); });
		vennTP.strokeColorPickerC.setOnAction(e->{ circleC.setStroke(vennTP.strokeColorPickerC.getValue()); });
		vennTP.btnToFrontC.setOnAction(e->{ circleC.toFront(); });
		vennTP.btnToBackC.setOnAction(e->{ circleC.toBack(); });
		vennTP.datasetHeaderC.textProperty().addListener(e->{ headerC.setText(vennTP.datasetHeaderC.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeC.valueProperty().addListener(e->{ setFontC(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyC.valueProperty().addListener(e->{ setFontC(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightC.setOnAction(e->{ setFontC(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureC.setOnAction(e->{ setFontC(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineC.setOnAction(e->{ setFontUnderlineC(); });
		vennTP.fontShadowC.setOnAction(e->{ setFontShadowC(); });
		vennTP.titleColorPickerC.setOnAction(e->{ headerC.setFill(vennTP.titleColorPickerC.getValue()); });
		vennTP.sliderTitleAngleC.valueProperty().addListener(e->{ headerC.setRotate(vennTP.sliderTitleAngleC.getValue()); repositionAnchor(); });
		vennTP.sliderSizeCircleC.valueProperty().addListener(e->{ setEllipseSize(); });
		vennTP.totalCountA.setText(vCalc.getSizeA()+"");
		vennTP.totalCountB.setText(vCalc.getSizeB()+"");
		vennTP.totalCountC.setText(vCalc.getSizeC()+"");
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
		if (vennTP.fontWeightA.isSelected() && vennTP.fontPostureA.isSelected()) {
			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeA.getValue()));
		} else if ((vennTP.fontWeightA.isSelected()) && !(vennTP.fontPostureA.isSelected())) {
			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeA.getValue()));
		} else if (!(vennTP.fontWeightA.isSelected()) && (vennTP.fontPostureA.isSelected())) {
			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeA.getValue()));
		} else {
			headerA.setFont(Font.font(vennTP.fontFamilyA.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeA.getValue()));
		}
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
		if (vennTP.fontWeightB.isSelected() && vennTP.fontPostureB.isSelected()) {
			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeB.getValue()));
		} else if ((vennTP.fontWeightB.isSelected()) && !(vennTP.fontPostureA.isSelected())) {
			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeB.getValue()));
		} else if (!(vennTP.fontWeightB.isSelected()) && (vennTP.fontPostureA.isSelected())) {
			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeB.getValue()));
		} else {
			headerB.setFont(Font.font(vennTP.fontFamilyB.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeB.getValue()));
		}
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

	public void setOutlineStyleC() {
		if (vennTP.strokeStyleC.getValue()=="Dashes") {
			circleC.setStyle("-fx-stroke-dash-array: 10 20 10 20; -fx-stroke-line-cap: round;");
		} else if (vennTP.strokeStyleC.getValue()=="Dots") {
			circleC.setStyle("-fx-stroke-dash-array: 1 15 1 15; -fx-stroke-line-cap: round;");
		} else {
			circleC.setStyle("");
		}
	}

	public void setFontC() {
		if (vennTP.fontWeightC.isSelected() && vennTP.fontPostureC.isSelected()) {
			headerC.setFont(Font.font(vennTP.fontFamilyC.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeC.getValue()));
		} else if ((vennTP.fontWeightC.isSelected()) && !(vennTP.fontPostureC.isSelected())) {
			headerC.setFont(Font.font(vennTP.fontFamilyC.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeC.getValue()));
		} else if (!(vennTP.fontWeightC.isSelected()) && (vennTP.fontPostureC.isSelected())) {
			headerC.setFont(Font.font(vennTP.fontFamilyC.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeC.getValue()));
		} else {
			headerC.setFont(Font.font(vennTP.fontFamilyC.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeC.getValue()));
		}
	}

	public void setFontUnderlineC() {
		if (vennTP.fontUnderlineC.isSelected()) {
			headerC.setUnderline(true);
		} else {
			headerC.setUnderline(false);
		}
	}

	public void setFontShadowC() {
		if (vennTP.fontShadowC.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			headerC.setEffect(dropShadow);
		} else {
			headerC.setEffect(null);
		}
	}

	public void setEllipseAndValuesPosition() {
		double R1 = circleA.getRadiusX();
		double R2 = circleB.getRadiusX();
		double R3 = circleC.getRadiusX();
		double oAB = 0.5;
		double oAC = 0.5;
		double oBC = 0.5;
		double dRAB = (R1 + R2) - (Math.max(R1,R2) - Math.min(R1,R2));
		double dRAC = (R1 + R3) - (Math.max(R1,R3) - Math.min(R1,R3));
		double dRBC = (R2 + R3) - (Math.max(R2,R3) - Math.min(R2,R3));
		double distanceAB = ((Math.max(R1,R2)-Math.min(R1,R2)) + dRAB * oAB)/2;
		double distanceAC = ((Math.max(R1,R3)-Math.min(R1,R3)) + dRAC * oAC)/2;
		double distanceBC = ((Math.max(R2,R3)-Math.min(R2,R3)) + dRBC * oBC)/2;
		if ((distanceAC+distanceBC) < distanceAB) { distanceAB = (distanceAC+distanceBC); }
		double h = Area.getTriangleHight(distanceAC, distanceBC, distanceAB);
		double xA = 400 - distanceAB;
		double yA = 400 - h;
		double xB = 400 + distanceAB;
		double yB = 400 - h;
		double xC = 400 - distanceAB + 2*Math.sqrt(distanceAC*distanceAC - h*h);
		double yC = 400 + h;
		circleA.setCenterX(xA);
		circleA.setCenterY(yA);
		circleB.setCenterX(xB);
		circleB.setCenterY(yB);
		circleC.setCenterX(xC);
		circleC.setCenterY(yC);

		double ABTopH = Area.getTriangleHight(R2, R1, (xB-xA));
		double ABTopS = Math.sqrt(R1*R1-ABTopH*ABTopH);
		double ABTopX = xA+ABTopS;
		double ABTopY = yA-ABTopH;
		double ABBottomX = xA+ABTopS;
		double ABBottomY = yA+ABTopH;

		double ACLeftD = Math.sqrt((xC-xA)*(xC-xA)+(yC-yA)*(yC-yA));
		double ACLeftH = Area.getTriangleHight(R1, R3, ACLeftD);
		double ACLeftS = Math.sqrt(R1*R1-ACLeftH*ACLeftH);
		double ACLeftA = Area.getTriangleAlphaAngle((xC-xA),ACLeftD);
		double ACLeftXsh = ACLeftH*Math.cos(Math.toRadians(ACLeftA));
		double ACLeftYsh = ACLeftH*Math.sin(Math.toRadians(ACLeftA));
		double ACLeftX = xA + (xC-xA)*ACLeftS/ACLeftD - ACLeftXsh;
		double ACLeftY = yA + (yC-yA)*ACLeftS/ACLeftD + ACLeftYsh;

		double ACRightD = Math.sqrt((xC-xA)*(xC-xA)+(yC-yA)*(yC-yA));
		double ACRightH = Area.getTriangleHight(R1, R3, ACRightD);
		double ACRightS = Math.sqrt(R1*R1-ACRightH*ACRightH);
		double ACRightA = Area.getTriangleAlphaAngle((xC-xA),ACRightD);
		double ACRightXsh = ACRightH*Math.cos(Math.toRadians(ACRightA));
		double ACRightYsh = ACRightH*Math.sin(Math.toRadians(ACRightA));
		double ACRightX = xA + (xC-xA)*ACRightS/ACRightD + ACRightXsh;
		double ACRightY = yB + (yC-yA)*ACRightS/ACRightD - ACRightYsh;

		double BCLeftD = Math.sqrt((xB-xC)*(xB-xC)+(yC-yB)*(yC-yB));
		double BCLeftH = Area.getTriangleHight(R2, R3, BCLeftD);
		double BCLeftS = Math.sqrt(R2*R2-BCLeftH*BCLeftH);
		double BCLeftA = Area.getTriangleAlphaAngle((xB-xC),BCLeftD);
		double BCLeftXsh = BCLeftH*Math.cos(Math.toRadians(BCLeftA));
		double BCLeftYsh = BCLeftH*Math.sin(Math.toRadians(BCLeftA));
		double BCLeftX = xB - (xB-xC)*BCLeftS/BCLeftD - BCLeftXsh;
		double BCLeftY = yB + (yC-yB)*BCLeftS/BCLeftD - BCLeftYsh;

		double BCRightD = Math.sqrt((xB-xC)*(xB-xC)+(yC-yB)*(yC-yB));
		double BCRightH = Area.getTriangleHight(R2, R3, BCRightD);
		double BCRightS = Math.sqrt(R2*R2-BCRightH*BCRightH);
		double BCRightA = Math.abs(Area.getTriangleAlphaAngle((xC-xB),BCRightD));
		double BCRightXsh = BCRightH*Math.cos(Math.toRadians(BCRightA));
		double BCRightYsh = BCRightH*Math.sin(Math.toRadians(BCRightA));
		double BCRightX = xB - (xB-xC)*BCRightS/BCRightD + BCRightXsh;
		double BCRightY = yB + (yC-yB)*BCRightS/BCRightD + BCRightYsh;

		vennValue[1].setX(((xA-R1)+(xB-R2))/2-vennValue[1].getLayoutBounds().getWidth()/2);
		vennValue[1].setY(yA-R1/4 + vennValue[1].getLayoutBounds().getHeight()/4);

		vennValue[2].setX(((xA+R1)+(xB+R2))/2-vennValue[2].getLayoutBounds().getWidth()/2);
		vennValue[2].setY(yB-R2/4 + vennValue[2].getLayoutBounds().getHeight()/4);

		double x3 = (ABTopX+ACRightX+BCLeftX)/3;
		double y3 = (ABTopY+ACRightY+BCLeftY)/3;
		vennValue[3].setX(x3 - vennValue[3].getLayoutBounds().getWidth()/2);
		vennValue[3].setY(y3 + vennValue[3].getLayoutBounds().getHeight()/4);

		vennValue[4].setX(xC + (R1-R2)/(4+200/R3) - vennValue[4].getLayoutBounds().getWidth()/2);
		vennValue[4].setY((yC+R3/2) + vennValue[4].getLayoutBounds().getHeight()/4);

		double x5 = (ABBottomX+ACLeftX+BCLeftX)/3;
		double y5 = (ABBottomY+ACLeftY+BCLeftY)/3;
		vennValue[5].setX( x5 - 5 - vennValue[5].getLayoutBounds().getWidth()/2);
		vennValue[5].setY( y5 + vennValue[5].getLayoutBounds().getHeight()/4);

		double x6 = (ABBottomX+BCRightX+ACRightX)/3;
		double y6 = (ABBottomY+BCRightY+ACRightY)/3;
		vennValue[6].setX(x6 + 5 - vennValue[6].getLayoutBounds().getWidth()/2);
		vennValue[6].setY(y6 + vennValue[6].getLayoutBounds().getHeight()/4);

		double x7 = (ABBottomX+BCLeftX+ACRightX)/3;
		double y7 = (ABBottomY+BCLeftY+ACRightY)/3;
		vennValue[7].setX(x7 - vennValue[7].getLayoutBounds().getWidth()/2);
		vennValue[7].setY(y7 + vennValue[7].getLayoutBounds().getHeight()/4);
	}

	public void setEllipseSize() {
		double rA  =  2 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleA.getValue()/100));
		double rB  =  2 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleB.getValue()/100));
		double rC  =  2 * Area.getCircleRadius(Area.getCircleArea(100)*(vennTP.sliderSizeCircleC.getValue()/100));
		circleA.setRadiusX(rA);
		circleA.setRadiusY(rA);
		circleB.setRadiusX(rB);
		circleB.setRadiusY(rB);
		circleC.setRadiusX(rC);
		circleC.setRadiusY(rC);
		setEllipseAndValuesPosition();
	}

	@Override
	protected void setVennType() {
		drawVenn3();
		setFont(); setFontA(); setFontB(); setFontC();
		if (!vCalc.getHeaderA().equals("")) { vennTP.datasetHeaderA.setText(vCalc.getHeaderA()); }
		if (!vCalc.getHeaderB().equals("")) { vennTP.datasetHeaderB.setText(vCalc.getHeaderB()); }
		if (!vCalc.getHeaderC().equals("")) { vennTP.datasetHeaderC.setText(vCalc.getHeaderC()); }
		int[] overlap = vCalc.getOverlap();
		vCount = 8;
		for (int i=1; i<8; i++) {
			vennValue[i].setText(String.valueOf(overlap[i]));
			vennValue[i].setId(""+i);
		}
		int a = overlap[1]+overlap[3]+overlap[5]+overlap[7];
		int b = overlap[2]+overlap[3]+overlap[6]+overlap[7];
		int c = overlap[4]+overlap[5]+overlap[6]+overlap[7];
		double rA = 0;
		double rB = 0;
		double rC = 0;
		if (a>0 && b>0 && c>0) {
			if (a==b && a==c) {
				vennTP.sliderSizeCircleA.setValue(100);
				vennTP.sliderSizeCircleB.setValue(100);
				vennTP.sliderSizeCircleC.setValue(100);
			} else if (a>=b && a>=c) {
				rA = 100;
				rB = Area.getCircleRadius(Area.getCircleArea(100)*b/a);
				rC = Area.getCircleRadius(Area.getCircleArea(100)*c/a);
				vennTP.sliderSizeCircleA.setValue(rA);
				vennTP.sliderSizeCircleB.setValue(rB);
				vennTP.sliderSizeCircleC.setValue(rC);
			} else if (b>=a && b>=c) {
				rA = Area.getCircleRadius(Area.getCircleArea(100)*a/b);
				rB = 100;
				rC = Area.getCircleRadius(Area.getCircleArea(100)*c/b);
				vennTP.sliderSizeCircleA.setValue(rA);
				vennTP.sliderSizeCircleB.setValue(rB);
				vennTP.sliderSizeCircleC.setValue(rC);
			} else if (c>=a && c>=b) {
				rA = Area.getCircleRadius(Area.getCircleArea(100)*a/c);
				rB = Area.getCircleRadius(Area.getCircleArea(100)*b/c);
				rC = 100;
				vennTP.sliderSizeCircleA.setValue(rA);
				vennTP.sliderSizeCircleB.setValue(rB);
				vennTP.sliderSizeCircleC.setValue(rC);
			}
		}
		setTextAlignmentToCenter();
		setEllipseAndValuesPosition();
	}

	private void repositionAnchor() {
		double angleA = headerA.getRotate();
		double angleB = headerB.getRotate();
		double angleC = headerC.getRotate();
		if (angleA==360.0) { angleA = 0; }
		if (angleB==360.0) { angleB = 0; }
		if (angleC==360.0) { angleC = 0; }
		double x = 0;
		double xA = headerA.getX() + headerA.getLayoutX() - (headerA.getLayoutBounds().getHeight()/4);
		double xB = headerB.getX() + headerB.getLayoutX() - (headerB.getLayoutBounds().getHeight()/4);
		double xC = headerC.getX() + headerC.getLayoutX() - (headerC.getLayoutBounds().getHeight()/4);
		if (xA<xB && xA<xC) { x = xA; }
		if (xB<xA && xB<xC) { x = xB; }
		if (xC<xA && xC<xB) { x = xC; }
		if (x>0) { x = 0; }
		anchorPoint.setX(x);
		double y = 0;
		double yA = headerA.getY() + headerA.getLayoutY()-headerA.getFont().getSize()-(headerA.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleA)));
		double yB = headerB.getY() + headerB.getLayoutY()-headerB.getFont().getSize()-(headerB.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleB)));
		double yC = headerC.getY() + headerC.getLayoutY()-headerC.getFont().getSize()-(headerC.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleC)));
		if (yA<yB && yA<yC) { y = yA; }
		if (yB<yA && yB<yC) { y = yB; }
		if (yC<yA && yC<yB) { y = yC; }
		if (y>0) { y = 0; }
		anchorPoint.setY(y);
	}

	private void setTextAlignmentToCenter() {
		headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
		headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
		headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
	}

	private void drawVenn3() {
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
		circleA = new Ellipse(300, 300, 200, 200);
		circleA.setFill(Color.rgb(255, 255, 0, 0.4));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(500, 300, 200, 200);
		circleB.setFill(Color.rgb(0, 255, 255, 0.4));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 500, 200, 200);
		circleC.setFill(Color.rgb(255, 0, 255, 0.4));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 80, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 80, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 750, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		for (int i=1; i<8; i++) {
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
		gBox.getChildren().addAll(headerA, headerB, headerC);
		vennPlot.setCenter(gBox);
	}

}


