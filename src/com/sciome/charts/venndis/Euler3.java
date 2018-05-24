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

public class Euler3 extends VennDiagram{
	public Ellipse circleB, circleC;
	public Text headerB, headerC;

	public Euler3(BorderPane borderPane, VennCalc vennCalc) {
		super(borderPane, vennCalc);
	}

	@Override
	protected void setDefaultTabPane() {
		vennTP.addPropertiesTab();
		vennTP.addTabA();
		vennTP.addTabB();
		vennTP.addTabC();
		vennTP.fillColorPickerA.setValue(Color.rgb(255,0,0,0.5));
		vennTP.fillColorPickerB.setValue(Color.rgb(0,255,0,0.5));
		vennTP.fillColorPickerC.setValue(Color.rgb(0,0,255,0.5));
		vennTP.sliderFillOpacityA.setValue(0.5);
		vennTP.sliderFillOpacityB.setValue(0.5);
		vennTP.sliderFillOpacityC.setValue(0.5);
		vennTP.fontSize.valueProperty().addListener(e->{ setFont(); });
		vennTP.fontFamily.valueProperty().addListener(e->{ setFont(); });
		vennTP.fontWeight.setOnAction(e->{ setFont(); });
		vennTP.fontPosture.setOnAction(e->{ setFont(); });
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
		vennTP.sliderTitleAngleC.valueProperty().addListener(e->{ headerC.setRotate(vennTP.sliderTitleAngleC.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });
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

	public void setVennValues() {
		int[] overlap = vCalc.getOverlap();
		switch (vCalc.getVennType()) {
			case 11:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				break;
			case 15:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				break;
			case 21:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				break;
			case 23:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				break;
			case 27:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				break;
			case 29:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				break;
			case 31:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[5]));
				vennValue[5].setId("5");
				break;
			case 38:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				break;
			case 39:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				break;
			case 43:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				break;
			case 46:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				break;
			case 47:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				break;
			case 56:
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				break;
			case 57:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				break;
			case 58:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				break;
			case 59:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				break;
			case 63:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[5]));
				vennValue[5].setId("5");
				vennValue[6].setText(String.valueOf(overlap[6]));
				vennValue[6].setId("6");
				break;
			case 69:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 70:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 71:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 75:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 77:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 78:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 79:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 81:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 83:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 85:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 87:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 88:
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 89:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 90:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 91:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 93:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 94:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 98:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[6]));
				vennValue[2].setId("6");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 99:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 102:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 103:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 104:
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[6]));
				vennValue[2].setId("6");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				break;
			case 105:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 106:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 107:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 109:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 110:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 115:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 119:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				break;
			case 120:
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				break;
			case 121:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 122:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				break;
			case 123:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				break;
			case 125:
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				break;
			case 126:
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				break;
		}		
	}

	@Override
	protected void setVennType() {
		int[] overlap = vCalc.getOverlap();
		switch (vCalc.getVennType()) {
			case 11:
				drawEuler011();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 15:
				drawEuler015();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 21:
				drawEuler021();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 23:
				drawEuler023();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 27:
				drawEuler027();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 29:
				drawEuler029();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 31:
				drawEuler031();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[5]));
				vennValue[5].setId("5");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 38:
				drawEuler038();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 39:
				drawEuler039();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 43:
				drawEuler043();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 46:
				drawEuler046();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 47:
				drawEuler047();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 56:
				drawEuler056();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 57:
				drawEuler057();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 58:
				drawEuler058();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 59:
				drawEuler059();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 63:
				drawEuler063();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[5]));
				vennValue[5].setId("5");
				vennValue[6].setText(String.valueOf(overlap[6]));
				vennValue[6].setId("6");
				vennTP.sliderTitleAngleA.setValue(300);
				vennTP.sliderTitleAngleB.setValue(60);
				vennTP.sliderTitleAngleC.setValue(0);
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 69:
				drawEuler069();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 70:
				drawEuler070();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 71:
				drawEuler071();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 75:
				drawEuler075();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 77:
				drawEuler077();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 78:
				drawEuler078();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 79:
				drawEuler079();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 81:
				drawEuler081();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 83:
				drawEuler083();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 85:
				drawEuler085();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 87:
				drawEuler087();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 88:
				drawEuler088();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 89:
				drawEuler089();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 90:
				drawEuler090();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 91:
				drawEuler091();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 93:
				drawEuler093();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 94:
				drawEuler094();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 95:
				drawEuler095();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[5]));
				vennValue[5].setId("5");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 98:
				drawEuler098();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[6]));
				vennValue[2].setId("6");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 99:
				drawEuler099();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 102:
				drawEuler102();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 103:
				drawEuler103();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 104:
				drawEuler104();
				vCount = 4;
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[6]));
				vennValue[2].setId("6");
				vennValue[3].setText(String.valueOf(overlap[7]));
				vennValue[3].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 105:
				drawEuler105();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 106:
				drawEuler106();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 107:
				drawEuler107();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 109:
				drawEuler109();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 110:
				drawEuler110();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 111:
				drawEuler111();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[4]));
				vennValue[4].setId("4");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 115:
				drawEuler115();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 119:
				drawEuler119();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[3]));
				vennValue[3].setId("3");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 120:
				drawEuler120();
				vCount = 5;
				vennValue[1].setText(String.valueOf(overlap[4]));
				vennValue[1].setId("4");
				vennValue[2].setText(String.valueOf(overlap[5]));
				vennValue[2].setId("5");
				vennValue[3].setText(String.valueOf(overlap[6]));
				vennValue[3].setId("6");
				vennValue[4].setText(String.valueOf(overlap[7]));
				vennValue[4].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 121:
				drawEuler121();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 122:
				drawEuler122();
				vCount = 6;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[4]));
				vennValue[2].setId("4");
				vennValue[3].setText(String.valueOf(overlap[5]));
				vennValue[3].setId("5");
				vennValue[4].setText(String.valueOf(overlap[6]));
				vennValue[4].setId("6");
				vennValue[5].setText(String.valueOf(overlap[7]));
				vennValue[5].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 123:
				drawEuler123();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[2]));
				vennValue[2].setId("2");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 125:
				drawEuler125();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
			case 126:
				drawEuler126();
				vCount = 7;
				vennValue[1].setText(String.valueOf(overlap[2]));
				vennValue[1].setId("2");
				vennValue[2].setText(String.valueOf(overlap[3]));
				vennValue[2].setId("3");
				vennValue[3].setText(String.valueOf(overlap[4]));
				vennValue[3].setId("4");
				vennValue[4].setText(String.valueOf(overlap[5]));
				vennValue[4].setId("5");
				vennValue[5].setText(String.valueOf(overlap[6]));
				vennValue[5].setId("6");
				vennValue[6].setText(String.valueOf(overlap[7]));
				vennValue[6].setId("7");
				vennTP.sliderSizeCircleA.setDisable(true);
				vennTP.sliderSizeCircleB.setDisable(true);
				vennTP.sliderSizeCircleC.setDisable(true);
				break;
		}
		if (!vCalc.getHeaderA().equals("")) { vennTP.datasetHeaderA.setText(vCalc.getHeaderA()); }
		if (!vCalc.getHeaderB().equals("")) { vennTP.datasetHeaderB.setText(vCalc.getHeaderB()); }
		if (!vCalc.getHeaderC().equals("")) { vennTP.datasetHeaderC.setText(vCalc.getHeaderC()); }
		setFont(); setFontA(); setFontB(); setFontC();
		setTextAlignmentToCenter();
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
		switch (vCalc.getVennType()) {
			case 11:
				headerA.setX(220-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(580-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 15:
				headerA.setX(225-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(575-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 21:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 23:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(225-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 27:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(200-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 29:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(225-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 31:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 38:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 39:
				headerA.setX(175-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(575-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 43:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 46:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(225-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 47:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 56:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 57:
				headerA.setX(175-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(575-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 58:
				headerA.setX(575-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(175-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 59:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 63:
				headerA.setX(165-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(640-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 69:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 70:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 71:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 75:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 77:
				headerA.setX(225-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(440-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 78:
				headerA.setX(440-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(225-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 79:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 81:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(200-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 83:
				headerA.setX(225-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(440-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 85:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 87:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(300-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 88:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 89:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 90:
				headerA.setX(430-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(225-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 91:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 93:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(300-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 94:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 95:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 98:
				headerA.setX(600-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(200-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 99:
				headerA.setX(225-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(430-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 102:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 103:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(550-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 104:
				headerA.setX(600-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 105:
				headerA.setX(225-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(430-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 106:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 107:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 109:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 110:
				headerA.setX(300-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 111:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 115:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 119:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;

			case 120:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 121:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(550-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 122:
				headerA.setX(550-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 123:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(600-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(400-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 125:
				headerA.setX(200-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(400-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
			case 126:
				headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
				headerB.setX(200-headerB.getLayoutBounds().getWidth()/2);
				headerC.setX(600-headerC.getLayoutBounds().getWidth()/2);
				break;
		}
	}

	private void drawEuler011() {
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
		circleA = new Ellipse(220, 245, 150, 150);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(580, 245, 150, 150);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 555, 150, 150);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(220, 85, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(580, 85, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(145, 245, "A");
		vennValue[2] = new Text(505, 245, "B");
		vennValue[3] = new Text(325, 555, "C");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler015() {
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
		circleA = new Ellipse(325, 225, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(475, 225, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 575, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(225, 40, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(575, 40, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 790, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(160, 225, "A");
		vennValue[2] = new Text(490, 225, "B");
		vennValue[3] = new Text(325, 225, "AB");
		vennValue[4] = new Text(325, 575, "C");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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
		gBox.getChildren().addAll(headerA, headerB, headerC);
		vennPlot.setCenter(gBox);
	}

	private void drawEuler021() {
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
		circleA = new Ellipse(400, 400, 300, 300);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(260, 425, 130, 130);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(540, 425, 130, 130);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 85, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 200, "A");
		vennValue[2] = new Text(180, 425, "AB");
		vennValue[3] = new Text(470, 425, "AC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler023() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(560, 400, 200, 200);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(230, 475, 125, 125);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 175, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(225, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(155, 275, "A");
		vennValue[2] = new Text(575, 400, "B");
		vennValue[3] = new Text(380, 400, "AB");
		vennValue[4] = new Text(155, 475, "AC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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


	private void drawEuler027() {
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
		circleA = new Ellipse(200, 325, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(600, 400, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(200, 475, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 135, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 200, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(200, 700, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(125, 225, "A");
		vennValue[2] = new Text(525, 400, "B");
		vennValue[3] = new Text(125, 575, "C");
		vennValue[4] = new Text(125, 400, "AC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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
		gBox.getChildren().addAll(headerA, headerB, headerC);
		vennPlot.setCenter(gBox);
	}

	private void drawEuler029() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(230, 475, 125, 125);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(560, 400, 200, 200);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(225, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 175, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(155, 275, "A");
		vennValue[2] = new Text(155, 475, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(380, 400, "AC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler031() {
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
		circleA = new Ellipse(400, 450, 200, 200);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(210, 350, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(590, 350, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 695, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 160, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 160, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 550, "A");
		vennValue[1].setId("1");
		vennValue[2] = new Text(70, 325, "B");
		vennValue[2].setId("2");
		vennValue[3] = new Text(220, 400, "AB");
		vennValue[3].setId("4");
		vennValue[4] = new Text(580, 325, "C");
		vennValue[4].setId("5");
		vennValue[5] = new Text(430, 400, "AC");
		vennValue[5].setId("6");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler038() {
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
		circleA = new Ellipse(260, 425, 130, 130);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 400, 300, 300);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(540, 425, 130, 130);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 85, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 200, "A");
		vennValue[2] = new Text(180, 425, "AB");
		vennValue[3] = new Text(470, 425, "AC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler039() {
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
		circleA = new Ellipse(270, 400, 200, 200);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(525, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(600, 475, 125, 125);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(175, 180, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(575, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "A");
		vennValue[2] = new Text(525, 275, "B");
		vennValue[3] = new Text(300, 400, "AB");
		vennValue[4] = new Text(525, 475, "BC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler043() {
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
		circleA = new Ellipse(200, 400, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(600, 325, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(600, 475, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 210, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 135, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(125, 400, "A");
		vennValue[2] = new Text(525, 225, "B");
		vennValue[3] = new Text(525, 575, "C");
		vennValue[4] = new Text(525, 400, "BC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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
		gBox.getChildren().addAll(headerA, headerB, headerC);
		vennPlot.setCenter(gBox);
	}

	private void drawEuler046() {
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
		circleA = new Ellipse(230, 325, 125, 125);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(300, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(560, 400, 200, 200);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(225, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 175, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(155, 525, "B");
		vennValue[2] = new Text(155, 325, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(380, 400, "BC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler047() {
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
		circleA = new Ellipse(210, 350, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 450, 200, 200);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(590, 350, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 160, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 695, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 160, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(70, 325, "A");
		vennValue[1].setId("1");
		vennValue[2] = new Text(325, 550, "B");
		vennValue[2].setId("2");
		vennValue[3] = new Text(220, 400, "AB");
		vennValue[3].setId("4");
		vennValue[4] = new Text(580, 325, "C");
		vennValue[4].setId("5");
		vennValue[5] = new Text(430, 400, "BC");
		vennValue[5].setId("6");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler056() {
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
		circleA = new Ellipse(260, 425, 130, 130);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(540, 425, 130, 130);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 400, 300, 300);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 85, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 200, "A");
		vennValue[2] = new Text(180, 425, "AB");
		vennValue[3] = new Text(470, 425, "AC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler057() {
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
		circleA = new Ellipse(270, 400, 200, 200);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(600, 325, 125, 125);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(175, 180, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(575, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "A");
		vennValue[2] = new Text(525, 525, "C");
		vennValue[3] = new Text(300, 400, "AC");
		vennValue[4] = new Text(525, 325, "BC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler058() {
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
		circleA = new Ellipse(600, 325, 125, 125);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(270, 400, 200, 200);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(575, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(175, 180, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "B");
		vennValue[2] = new Text(525, 525, "C");
		vennValue[3] = new Text(525, 325, "AC");
		vennValue[4] = new Text(300, 400, "BC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler059() {
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
		circleA = new Ellipse(210, 350, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(590, 350, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 450, 200, 200);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 160, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 160, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 695, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(70, 325, "A");
		vennValue[1].setId("1");
		vennValue[2] = new Text(580, 325, "B");
		vennValue[2].setId("2");
		vennValue[3] = new Text(325, 550, "C");
		vennValue[3].setId("4");
		vennValue[4] = new Text(220, 400, "AC");
		vennValue[4].setId("5");
		vennValue[5] = new Text(430, 400, "BC");
		vennValue[5].setId("6");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler063() {
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
		circleA = new Ellipse(300, 380, 100, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(30);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(500, 380, 100, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(-30);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 550, 100, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(90);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(165, 350, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setRotate(300);
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(640, 350, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setRotate(60);
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(225, 375, "A");
		vennValue[2] = new Text(425, 375, "B");
		vennValue[3] = new Text(325, 230, "AB");
		vennValue[4] = new Text(325, 550, "C");
		vennValue[5] = new Text(150, 525, "AC");
		vennValue[6] = new Text(500, 525, "BC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 28));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler069() {
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
		circleA = new Ellipse(400, 400, 300, 300);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 475, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 550, 150, 150);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 85, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "A");
		vennValue[2] = new Text(325, 325, "AB");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler070() {
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
		circleA = new Ellipse(400, 475, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 400, 300, 300);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 550, 150, 150);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 85, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "B");
		vennValue[2] = new Text(325, 325, "AB");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler071() {
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
		circleA = new Ellipse(310, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(490, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 460, 125, 125);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "A");
		vennValue[2] = new Text(575, 400, "B");
		vennValue[3] = new Text(325, 260, "AB");
		vennValue[4] = new Text(325, 460, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler075() {
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
		circleA = new Ellipse(330, 325, 165, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(300);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(470, 325, 165, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(60);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 430, 165, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 125, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 125, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 720, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(90, 250, "A");
		vennValue[2] = new Text(560, 250, "B");
		vennValue[3] = new Text(325, 575, "C");
		vennValue[4] = new Text(325, 350, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler077() {
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
		circleA = new Ellipse(275, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(343, 400, 180, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(550, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(225, 125, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(440, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 150, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(25, 400, "A");
		vennValue[2] = new Text(175, 400, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(350, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler078() {
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
		circleA = new Ellipse(343, 400, 180, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(275, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(550, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(440, 690, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(225, 125, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 150, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(25, 400, "B");
		vennValue[2] = new Text(175, 400, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(350, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler079() {
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
		circleA = new Ellipse(335, 270, 160, 240);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(300);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(465, 270, 160, 240);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(60);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 500, 150, 220);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
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
		headerC = new Text(400, 755, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(120, 200, "A");
		vennValue[2] = new Text(530, 200, "B");
		vennValue[3] = new Text(325, 220, "AB");
		vennValue[4] = new Text(325, 575, "C");
		vennValue[5] = new Text(325, 370, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler081() {
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
		circleA = new Ellipse(400, 400, 300, 300);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 550, 150, 150);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 475, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 85, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(200, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "A");
		vennValue[2] = new Text(325, 325, "AC");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler083() {
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
		circleA = new Ellipse(275, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(550, 400, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(343, 400, 180, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(225, 125, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 150, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(440, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(25, 400, "A");
		vennValue[2] = new Text(575, 400, "B");
		vennValue[3] = new Text(175, 400, "AC");
		vennValue[4] = new Text(350, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler085() {
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
		circleA = new Ellipse(400, 400, 300, 300);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(325, 450, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(475, 450, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 85, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 200, "A");
		vennValue[2] = new Text(150, 450, "AB");
		vennValue[3] = new Text(500, 450, "AC");
		vennValue[4] = new Text(325, 450, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler087() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(500, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(280, 475, 150, 150);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(300, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 290, "A");
		vennValue[2] = new Text(575, 400, "B");
		vennValue[3] = new Text(325, 275, "AB");
		vennValue[4] = new Text(135, 500, "AC");
		vennValue[5] = new Text(275, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler088() {
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
		circleA = new Ellipse(400, 475, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 550, 150, 150);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 400, 300, 300);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 85, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "C");
		vennValue[2] = new Text(325, 325, "AC");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler089() {
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
		circleA = new Ellipse(310, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 460, 125, 125);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(490, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 140, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "A");
		vennValue[2] = new Text(575, 400, "C");
		vennValue[3] = new Text(325, 260, "AC");
		vennValue[4] = new Text(325, 460, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler090() {
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
		circleA = new Ellipse(457, 400, 180, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(250, 400, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(430, 690, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(225, 150, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 125, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "B");
		vennValue[2] = new Text(625, 400, "C");
		vennValue[3] = new Text(475, 400, "AC");
		vennValue[4] = new Text(300, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler091() {
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
		circleA = new Ellipse(335, 270, 160, 240);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(300);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 500, 150, 220);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(465, 270, 160, 240);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(60);
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
		headerB = new Text(400, 755, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 80, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(120, 200, "A");
		vennValue[2] = new Text(325, 575, "B");
		vennValue[3] = new Text(530, 200, "C");
		vennValue[4] = new Text(325, 220, "AC");
		vennValue[5] = new Text(325, 370, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler093() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(280, 475, 150, 150);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(500, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(300, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 140, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 290, "A");
		vennValue[2] = new Text(135, 500, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(325, 275, "AC");
		vennValue[5] = new Text(275, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler094() {
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
		circleA = new Ellipse(400, 400, 220, 185);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(275, 400, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 680, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 680, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(50, 400, "B");
		vennValue[2] = new Text(175, 400, "AB");
		vennValue[3] = new Text(600, 400, "C");
		vennValue[4] = new Text(475, 400, "AC");
		vennValue[5] = new Text(325, 400, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler095() {
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
		circleA = new Ellipse(400, 430, 210, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(270, 325, 175, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(300);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(530, 325, 175, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(60);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 700, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 125, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 125, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 575, "A");
		vennValue[2] = new Text(90, 250, "B");
		vennValue[3] = new Text(190, 440, "AB");
		vennValue[4] = new Text(560, 250, "C");
		vennValue[5] = new Text(460, 440, "AC");
		vennValue[6] = new Text(325, 350, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler098() {
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
		circleA = new Ellipse(400, 550, 150, 150);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 400, 300, 300);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 475, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(600, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 85, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(200, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "B");
		vennValue[2] = new Text(325, 325, "BC");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler099() {
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
		circleA = new Ellipse(250, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(525, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(457, 400, 180, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(225, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 125, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(430, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "A");
		vennValue[2] = new Text(625, 400, "B");
		vennValue[3] = new Text(475, 400, "BC");
		vennValue[4] = new Text(300, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler102() {
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
		circleA = new Ellipse(325, 350, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 400, 300, 300);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(475, 350, 175, 175);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 90, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 90, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 600, "B");
		vennValue[2] = new Text(150, 350, "AB");
		vennValue[3] = new Text(500, 350, "BC");
		vennValue[4] = new Text(325, 350, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler103() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(500, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(520, 475, 150, 150);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(550, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "A");
		vennValue[2] = new Text(575, 290, "B");
		vennValue[3] = new Text(325, 275, "AB");
		vennValue[4] = new Text(515, 500, "AC");
		vennValue[5] = new Text(375, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler104() {
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
		circleA = new Ellipse(400, 550, 150, 150);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 475, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 400, 300, 300);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(600, 745, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 745, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 85, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 175, "C");
		vennValue[2] = new Text(325, 325, "BC");
		vennValue[3] = new Text(325, 555, "ABC");
		for (int i=1; i<4; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler105() {
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
		circleA = new Ellipse(250, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(457, 400, 180, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(225, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(430, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 125, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "A");
		vennValue[2] = new Text(625, 400, "C");
		vennValue[3] = new Text(475, 400, "BC");
		vennValue[4] = new Text(300, 400, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler106() {
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
		circleA = new Ellipse(400, 360, 125, 125);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(310, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(490, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 690, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(75, 400, "A");
		vennValue[2] = new Text(575, 400, "B");
		vennValue[3] = new Text(325, 550, "AB");
		vennValue[4] = new Text(325, 360, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler107() {
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
		circleA = new Ellipse(400, 500, 150, 220);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(335, 270, 160, 240);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(300);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(465, 270, 160, 240);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(60);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 755, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 80, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 80, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 575, "A");
		vennValue[2] = new Text(120, 200, "B");
		vennValue[3] = new Text(530, 200, "C");
		vennValue[4] = new Text(325, 220, "BC");
		vennValue[5] = new Text(325, 370, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler109() {
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
		circleA = new Ellipse(275, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 400, 220, 185);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 680, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 150, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(50, 400, "A");
		vennValue[2] = new Text(175, 400, "AB");
		vennValue[3] = new Text(600, 400, "C");
		vennValue[4] = new Text(475, 400, "BC");
		vennValue[5] = new Text(325, 400, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler110() {
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
		circleA = new Ellipse(280, 475, 150, 150);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(300, 400, 250, 250);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(500, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(300, 690, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 140, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 290, "B");
		vennValue[2] = new Text(135, 500, "AB");
		vennValue[3] = new Text(575, 400, "C");
		vennValue[4] = new Text(325, 275, "BC");
		vennValue[5] = new Text(275, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler111() {
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
		circleA = new Ellipse(270, 325, 175, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(300);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 430, 210, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(530, 325, 175, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(60);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 125, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 700, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 125, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(90, 250, "A");
		vennValue[2] = new Text(325, 575, "B");
		vennValue[3] = new Text(190, 440, "AB");
		vennValue[4] = new Text(560, 250, "C");
		vennValue[5] = new Text(460, 440, "BC");
		vennValue[6] = new Text(325, 350, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler115() {
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
		circleA = new Ellipse(275, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(525, 400, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 400, 220, 185);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 150, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 680, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(50, 400, "A");
		vennValue[2] = new Text(600, 400, "B");
		vennValue[3] = new Text(175, 400, "AC");
		vennValue[4] = new Text(475, 400, "BC");
		vennValue[5] = new Text(325, 400, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler119() {
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
		circleA = new Ellipse(275, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(525, 400, 225, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 460, 220, 125);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 150, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 680, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(110, 325, "A");
		vennValue[2] = new Text(540, 325, "B");
		vennValue[3] = new Text(325, 285, "AB");
		vennValue[4] = new Text(175, 460, "AC");
		vennValue[5] = new Text(475, 460, "BC");
		vennValue[6] = new Text(325, 450, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler120() {
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
		circleA = new Ellipse(325, 350, 175, 175);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(475, 350, 175, 175);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 400, 300, 300);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 90, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 90, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 745, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 600, "C");
		vennValue[2] = new Text(150, 350, "AC");
		vennValue[3] = new Text(500, 350, "BC");
		vennValue[4] = new Text(325, 350, "ABC");
		for (int i=1; i<5; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler121() {
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
		circleA = new Ellipse(300, 400, 250, 250);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(520, 475, 150, 150);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(500, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 140, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(550, 690, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 140, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "A");
		vennValue[2] = new Text(575, 290, "C");
		vennValue[3] = new Text(325, 275, "AC");
		vennValue[4] = new Text(515, 500, "BC");
		vennValue[5] = new Text(375, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler122() {
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
		circleA = new Ellipse(520, 475, 150, 150);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(300, 400, 250, 250); 
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(500, 400, 250, 250);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(550, 690, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 140, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 140, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(100, 400, "B");
		vennValue[2] = new Text(575, 290, "C");
		vennValue[3] = new Text(515, 500, "AC");
		vennValue[4] = new Text(325, 275, "BC");
		vennValue[5] = new Text(375, 460, "ABC");
		for (int i=1; i<6; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler123() {
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
		circleA = new Ellipse(270, 325, 175, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setRotate(300);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(530, 325, 175, 225);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(60);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(400, 430, 210, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 125, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(600, 125, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(400, 700, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(90, 250, "A");
		vennValue[2] = new Text(560, 250, "B");
		vennValue[3] = new Text(325, 575, "C");
		vennValue[4] = new Text(190, 440, "AC");
		vennValue[5] = new Text(460, 440, "BC");
		vennValue[6] = new Text(325, 350, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler125() {
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
		circleA = new Ellipse(275, 400, 225, 225);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(400, 460, 220, 125);
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(200, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(400, 680, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 150, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(110, 325, "A");
		vennValue[2] = new Text(175, 460, "AB");
		vennValue[3] = new Text(540, 325, "C");
		vennValue[4] = new Text(325, 285, "AC");
		vennValue[5] = new Text(475, 460, "BC");
		vennValue[6] = new Text(325, 450, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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

	private void drawEuler126() {
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
		circleA = new Ellipse(400, 340, 220, 125);
		circleA.setFill(Color.rgb(255, 0, 0, 0.5));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(275, 400, 225, 225); 
		circleB.setFill(Color.rgb(0, 255, 0, 0.5));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(525, 400, 225, 225);
		circleC.setFill(Color.rgb(0, 0, 255, 0.5));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC);
		headerA = new Text(400, 150, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(200, 680, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont, 28));
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(600, 680, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont, 28));
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(110, 485, "B");
		vennValue[2] = new Text(175, 340, "AB");
		vennValue[3] = new Text(540, 485, "C");
		vennValue[4] = new Text(475, 340, "AC");
		vennValue[5] = new Text(325, 510, "BC");
		vennValue[6] = new Text(325, 350, "ABC");
		for (int i=1; i<7; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 32));
			vennValue[i].setTextAlignment(TextAlignment.CENTER);
			vennValue[i].setTextOrigin(VPos.CENTER);
			vennValue[i].setWrappingWidth(150);
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


