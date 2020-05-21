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
import javafx.scene.transform.Rotate;
import javafx.scene.control.ScrollPane;

public class Venn5 extends VennDiagram{
	public Ellipse circleB, circleC, circleD, circleE;
	public Text headerB, headerC, headerD, headerE;

	public Venn5(BorderPane borderPane, VennCalc vennCalc) {
		super(borderPane, vennCalc);
	}

	@Override
	protected void setDefaultTabPane() {
		vennTP.addPropertiesTab();
		vennTP.addTabA();
		vennTP.addTabB();
		vennTP.addTabC();
		vennTP.addTabD();
		vennTP.addTabE();
		vennTP.fillColorPickerA.setValue(Color.rgb(255,255,0,0.4));
		vennTP.fillColorPickerB.setValue(Color.rgb(0,255,255,0.4));
		vennTP.fillColorPickerC.setValue(Color.rgb(255,0,255,0.4));
		vennTP.fillColorPickerD.setValue(Color.rgb(0,255,0,0.4));
		vennTP.fillColorPickerE.setValue(Color.rgb(0,0,255,0.4));
		vennTP.sliderFillOpacityA.setValue(0.4);
		vennTP.sliderFillOpacityB.setValue(0.4);
		vennTP.sliderFillOpacityC.setValue(0.4);
		vennTP.sliderFillOpacityD.setValue(0.4);
		vennTP.sliderFillOpacityE.setValue(0.4);
		vennTP.fontSize.setValue(28);
		vennTP.fontSize.valueProperty().addListener(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontFamily.valueProperty().addListener(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontWeight.setOnAction(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontPosture.setOnAction(e->{ setFont(); setTextValuesAlignmentToCenter(); });
		vennTP.fontUnderline.setOnAction(e->{ setFontUnderline(); });
		vennTP.fontShadow.setOnAction(e->{ setFontShadow(); });
		vennTP.valuesColorPicker.setOnAction(e->{ setValuesColor(); });
		vennTP.strokeStyleA.valueProperty().addListener(e->{ setOutlineStyleA(); });
		vennTP.strokeStyleB.valueProperty().addListener(e->{ setOutlineStyleB(); });
		vennTP.strokeStyleC.valueProperty().addListener(e->{ setOutlineStyleC(); });
		vennTP.strokeStyleD.valueProperty().addListener(e->{ setOutlineStyleD(); });
		vennTP.strokeStyleE.valueProperty().addListener(e->{ setOutlineStyleE(); });
		vennTP.colorPickerBG.setOnAction(e->{ setBGColor(); });
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

		vennTP.fillColorPickerD.setOnAction(e->{ 
				circleD.setFill(vennTP.fillColorPickerD.getValue());
				double o = vennTP.fillColorPickerD.getValue().getOpacity();
				vennTP.sliderFillOpacityD.setValue(o); 
			});
		vennTP.sliderFillOpacityD.valueProperty().addListener(e->{
				double r = vennTP.fillColorPickerD.getValue().getRed();
				double g = vennTP.fillColorPickerD.getValue().getGreen();
				double b = vennTP.fillColorPickerD.getValue().getBlue();
				double o = vennTP.sliderFillOpacityD.getValue();
				vennTP.fillColorPickerD.setValue(Color.color(r, g, b, o)); 
				circleD.setFill(Color.color(r, g, b, o));
			});
		vennTP.sliderStrokeWidthD.valueProperty().addListener(e->{ circleD.setStrokeWidth(vennTP.sliderStrokeWidthD.getValue()); });
		vennTP.strokeColorPickerD.setOnAction(e->{ circleD.setStroke(vennTP.strokeColorPickerD.getValue()); });
		vennTP.btnToFrontD.setOnAction(e->{ circleD.toFront(); });
		vennTP.btnToBackD.setOnAction(e->{ circleD.toBack(); });
		vennTP.datasetHeaderD.textProperty().addListener(e->{ headerD.setText(vennTP.datasetHeaderD.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeD.valueProperty().addListener(e->{ setFontD(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyD.valueProperty().addListener(e->{ setFontD(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightD.setOnAction(e->{ setFontD(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureD.setOnAction(e->{ setFontD(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineD.setOnAction(e->{ setFontUnderlineD(); });
		vennTP.fontShadowD.setOnAction(e->{ setFontShadowD(); });
		vennTP.titleColorPickerD.setOnAction(e->{ headerD.setFill(vennTP.titleColorPickerD.getValue()); });
		vennTP.sliderTitleAngleD.valueProperty().addListener(e->{ headerD.setRotate(vennTP.sliderTitleAngleD.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });

		vennTP.fillColorPickerE.setOnAction(e->{ 
				circleE.setFill(vennTP.fillColorPickerE.getValue());
				double o = vennTP.fillColorPickerE.getValue().getOpacity();
				vennTP.sliderFillOpacityE.setValue(o); 
			});
		vennTP.sliderFillOpacityE.valueProperty().addListener(e->{
				double r = vennTP.fillColorPickerE.getValue().getRed();
				double g = vennTP.fillColorPickerE.getValue().getGreen();
				double b = vennTP.fillColorPickerE.getValue().getBlue();
				double o = vennTP.sliderFillOpacityE.getValue();
				vennTP.fillColorPickerE.setValue(Color.color(r, g, b, o)); 
				circleE.setFill(Color.color(r, g, b, o));
			});
		vennTP.sliderStrokeWidthE.valueProperty().addListener(e->{ circleE.setStrokeWidth(vennTP.sliderStrokeWidthE.getValue()); });
		vennTP.strokeColorPickerE.setOnAction(e->{ circleE.setStroke(vennTP.strokeColorPickerE.getValue()); });
		vennTP.btnToFrontE.setOnAction(e->{ circleE.toFront(); });
		vennTP.btnToBackE.setOnAction(e->{ circleE.toBack(); });
		vennTP.datasetHeaderE.textProperty().addListener(e->{ headerE.setText(vennTP.datasetHeaderE.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeE.valueProperty().addListener(e->{ setFontE(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyE.valueProperty().addListener(e->{ setFontE(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightE.setOnAction(e->{ setFontE(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureE.setOnAction(e->{ setFontE(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineE.setOnAction(e->{ setFontUnderlineE(); });
		vennTP.fontShadowE.setOnAction(e->{ setFontShadowE(); });
		vennTP.titleColorPickerE.setOnAction(e->{ headerE.setFill(vennTP.titleColorPickerE.getValue()); });
		vennTP.sliderTitleAngleE.valueProperty().addListener(e->{ headerE.setRotate(vennTP.sliderTitleAngleE.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.totalCountA.setText(vCalc.getSizeA()+"");
		vennTP.totalCountB.setText(vCalc.getSizeB()+"");
		vennTP.totalCountC.setText(vCalc.getSizeC()+"");
		vennTP.totalCountD.setText(vCalc.getSizeD()+"");
		vennTP.totalCountE.setText(vCalc.getSizeE()+"");
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

	public void setOutlineStyleD() {
		if (vennTP.strokeStyleD.getValue()=="Dashes") {
			circleD.setStyle("-fx-stroke-dash-array: 10 20 10 20; -fx-stroke-line-cap: round;");
		} else if (vennTP.strokeStyleD.getValue()=="Dots") {
			circleD.setStyle("-fx-stroke-dash-array: 1 15 1 15; -fx-stroke-line-cap: round;");
		} else {
			circleD.setStyle("");
		}
	}

	public void setFontD() {
		if (vennTP.fontWeightD.isSelected() && vennTP.fontPostureD.isSelected()) {
			headerD.setFont(Font.font(vennTP.fontFamilyD.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeD.getValue()));
		} else if ((vennTP.fontWeightD.isSelected()) && !(vennTP.fontPostureD.isSelected())) {
			headerD.setFont(Font.font(vennTP.fontFamilyD.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeD.getValue()));
		} else if (!(vennTP.fontWeightD.isSelected()) && (vennTP.fontPostureD.isSelected())) {
			headerD.setFont(Font.font(vennTP.fontFamilyD.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeD.getValue()));
		} else {
			headerD.setFont(Font.font(vennTP.fontFamilyD.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeD.getValue()));
		}
	}

	public void setFontUnderlineD() {
		if (vennTP.fontUnderlineD.isSelected()) {
			headerD.setUnderline(true);
		} else {
			headerD.setUnderline(false);
		}
	}

	public void setFontShadowD() {
		if (vennTP.fontShadowD.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			headerD.setEffect(dropShadow);
		} else {
			headerD.setEffect(null);
		}
	}

	public void setOutlineStyleE() {
		if (vennTP.strokeStyleE.getValue()=="Dashes") {
			circleE.setStyle("-fx-stroke-dash-array: 10 20 10 20; -fx-stroke-line-cap: round;");
		} else if (vennTP.strokeStyleE.getValue()=="Dots") {
			circleE.setStyle("-fx-stroke-dash-array: 1 15 1 15; -fx-stroke-line-cap: round;");
		} else {
			circleE.setStyle("");
		}
	}

	public void setFontE() {
		if (vennTP.fontWeightE.isSelected() && vennTP.fontPostureE.isSelected()) {
			headerE.setFont(Font.font(vennTP.fontFamilyE.getValue(), FontWeight.BOLD, FontPosture.ITALIC, vennTP.fontSizeE.getValue()));
		} else if ((vennTP.fontWeightE.isSelected()) && !(vennTP.fontPostureE.isSelected())) {
			headerE.setFont(Font.font(vennTP.fontFamilyE.getValue(), FontWeight.BOLD, FontPosture.REGULAR, vennTP.fontSizeE.getValue()));
		} else if (!(vennTP.fontWeightE.isSelected()) && (vennTP.fontPostureE.isSelected())) {
			headerE.setFont(Font.font(vennTP.fontFamilyE.getValue(), FontWeight.NORMAL, FontPosture.ITALIC, vennTP.fontSizeE.getValue()));
		} else {
			headerE.setFont(Font.font(vennTP.fontFamilyE.getValue(), FontWeight.NORMAL, FontPosture.REGULAR, vennTP.fontSizeE.getValue()));
		}
	}

	public void setFontUnderlineE() {
		if (vennTP.fontUnderlineE.isSelected()) {
			headerE.setUnderline(true);
		} else {
			headerE.setUnderline(false);
		}
	}

	public void setFontShadowE() {
		if (vennTP.fontShadowE.isSelected()) {
			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(5.0);
			dropShadow.setOffsetX(3.0);
			dropShadow.setOffsetY(3.0);
			dropShadow.setColor(Color.color(0.3, 0.3, 0.3));
			headerE.setEffect(dropShadow);
		} else {
			headerE.setEffect(null);
		}
	}

	@Override
	protected void setVennType() {
		drawVenn5();
		setFont(); setFontA(); setFontB(); setFontC(); setFontD(); setFontE();
		if (!vCalc.getHeaderA().equals("")) { vennTP.datasetHeaderA.setText(vCalc.getHeaderA()); }
		if (!vCalc.getHeaderB().equals("")) { vennTP.datasetHeaderB.setText(vCalc.getHeaderB()); }
		if (!vCalc.getHeaderC().equals("")) { vennTP.datasetHeaderC.setText(vCalc.getHeaderC()); }
		if (!vCalc.getHeaderD().equals("")) { vennTP.datasetHeaderD.setText(vCalc.getHeaderD()); }
		if (!vCalc.getHeaderE().equals("")) { vennTP.datasetHeaderE.setText(vCalc.getHeaderE()); }
		int[] overlap = vCalc.getOverlap();
		vCount = 32;
		for (int i=1; i<32; i++) {
			vennValue[i].setText(String.valueOf(overlap[i]));
			vennValue[i].setId(""+i);
		}
		vennTP.sliderTitleAngleA.setValue(0);
		vennTP.sliderTitleAngleB.setValue(72);
		vennTP.sliderTitleAngleC.setValue(324);
		vennTP.sliderTitleAngleD.setValue(36);
		vennTP.sliderTitleAngleE.setValue(288);
		vennTP.sliderSizeCircleA.setDisable(true);
		vennTP.sliderSizeCircleB.setDisable(true);
		vennTP.sliderSizeCircleC.setDisable(true);
		vennTP.sliderSizeCircleD.setDisable(true);
		vennTP.sliderSizeCircleE.setDisable(true);
		setTextAlignmentToCenter();
		setTextValuesAlignmentToCenter();
	}

	private void repositionAnchor() {
		double angleA = headerA.getRotate();
		double angleB = headerB.getRotate();
		double angleC = headerC.getRotate();
		double angleD = headerD.getRotate();
		double angleE = headerE.getRotate();
		if (angleA==360.0) { angleA = 0; }
		if (angleB==360.0) { angleB = 0; }
		if (angleC==360.0) { angleC = 0; }
		if (angleD==360.0) { angleD = 0; }
		if (angleE==360.0) { angleE = 0; }
		double x = 0;
		double xA = headerA.getX() + headerA.getLayoutX() - (headerA.getLayoutBounds().getHeight()/4);
		double xB = headerB.getX() + headerB.getLayoutX() - (headerB.getLayoutBounds().getHeight()/4);
		double xC = headerC.getX() + headerC.getLayoutX() - (headerC.getLayoutBounds().getHeight()/4);
		double xD = headerD.getX() + headerD.getLayoutX() - (headerD.getLayoutBounds().getHeight()/4);
		double xE = headerE.getX() + headerE.getLayoutX() - (headerE.getLayoutBounds().getHeight()/4);
		if (xA<xB && xA<xC && xA<xD && xA<xE) { x = xA; }
		if (xB<xA && xB<xC && xB<xD && xB<xE) { x = xB; }
		if (xC<xA && xC<xB && xC<xD && xC<xE) { x = xC; }
		if (xD<xA && xD<xB && xD<xC && xD<xE) { x = xD; }
		if (xE<xA && xE<xB && xE<xC && xE<xD) { x = xE; }
		if (x>0) { x = 0; }
		anchorPoint.setX(x);
		double y = 0;
		double yA = headerA.getY() + headerA.getLayoutY()-headerA.getFont().getSize()-(headerA.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleA)));
		double yB = headerB.getY() + headerB.getLayoutY()-headerB.getFont().getSize()-(headerB.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleB)));
		double yC = headerC.getY() + headerC.getLayoutY()-headerC.getFont().getSize()-(headerC.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleC)));
		double yD = headerD.getY() + headerD.getLayoutY()-headerD.getFont().getSize()-(headerD.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleD)));
		double yE = headerE.getY() + headerE.getLayoutY()-headerE.getFont().getSize()-(headerE.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleE)));
		if (yA<yB && yA<yC && yA<yD && yA<yE) { y = yA; }
		if (yB<yA && yB<yC && yB<yD && yB<yE) { y = yB; }
		if (yC<yA && yC<yB && yC<yD && yC<yE) { y = yC; }
		if (yD<yA && yD<yB && yD<yC && yD<yE) { y = yD; }
		if (yE<yA && yE<yB && yE<yC && yE<yD) { y = yE; }
		if (y>0) { y = 0; }
		anchorPoint.setY(y);
	}

	private void setTextAlignmentToCenter() {
		headerA.setX(415-headerA.getLayoutBounds().getWidth()/2);
		headerB.setX(735-headerB.getLayoutBounds().getWidth()/2);
		headerC.setX(570-headerC.getLayoutBounds().getWidth()/2);
		headerD.setX(150-headerD.getLayoutBounds().getWidth()/2);
		headerE.setX(60-headerE.getLayoutBounds().getWidth()/2);
	}

	private void setTextValuesAlignmentToCenter() {
		vennValue[1].setX(420-vennValue[1].getLayoutBounds().getWidth()/2);
		vennValue[2].setX(660-vennValue[2].getLayoutBounds().getWidth()/2);
		vennValue[3].setX(542-vennValue[3].getLayoutBounds().getWidth()/2);
		vennValue[4].setX(510-vennValue[4].getLayoutBounds().getWidth()/2);
		vennValue[5].setX(425-vennValue[5].getLayoutBounds().getWidth()/2);
		vennValue[6].setX(552-vennValue[6].getLayoutBounds().getWidth()/2);
		vennValue[7].setX(465-vennValue[7].getLayoutBounds().getWidth()/2);
		vennValue[8].setX(210-vennValue[8].getLayoutBounds().getWidth()/2);
		vennValue[9].setX(460-vennValue[9].getLayoutBounds().getWidth()/2);
		vennValue[10].setX(212-vennValue[10].getLayoutBounds().getWidth()/2);
		vennValue[11].setX(515-vennValue[11].getLayoutBounds().getWidth()/2);
		vennValue[12].setX(333-vennValue[12].getLayoutBounds().getWidth()/2);
		vennValue[13].setX(360-vennValue[13].getLayoutBounds().getWidth()/2);
		vennValue[14].setX(280-vennValue[14].getLayoutBounds().getWidth()/2);
		vennValue[15].setX(370-vennValue[15].getLayoutBounds().getWidth()/2);
		vennValue[16].setX(130-vennValue[16].getLayoutBounds().getWidth()/2);
		vennValue[17].setX(318-vennValue[17].getLayoutBounds().getWidth()/2);
		vennValue[18].setX(585-vennValue[18].getLayoutBounds().getWidth()/2);
		vennValue[19].setX(535-vennValue[19].getLayoutBounds().getWidth()/2);
		vennValue[20].setX(235-vennValue[20].getLayoutBounds().getWidth()/2);
		vennValue[21].setX(303-vennValue[21].getLayoutBounds().getWidth()/2);
		vennValue[22].setX(553-vennValue[22].getLayoutBounds().getWidth()/2);
		vennValue[23].setX(510-vennValue[23].getLayoutBounds().getWidth()/2);
		vennValue[24].setX(190-vennValue[24].getLayoutBounds().getWidth()/2);
		vennValue[25].setX(410-vennValue[25].getLayoutBounds().getWidth()/2);
		vennValue[26].setX(203-vennValue[26].getLayoutBounds().getWidth()/2);
		vennValue[27].setX(490-vennValue[27].getLayoutBounds().getWidth()/2);
		vennValue[28].setX(250-vennValue[28].getLayoutBounds().getWidth()/2);
		vennValue[29].setX(330-vennValue[29].getLayoutBounds().getWidth()/2);
		vennValue[30].setX(250-vennValue[30].getLayoutBounds().getWidth()/2);
		vennValue[31].setX(390-vennValue[31].getLayoutBounds().getWidth()/2);
		vennValue[1].setY(130+vennValue[1].getLayoutBounds().getHeight()/4);
		vennValue[2].setY(350+vennValue[2].getLayoutBounds().getHeight()/4);
		vennValue[3].setY(278+vennValue[3].getLayoutBounds().getHeight()/4);
		vennValue[4].setY(640+vennValue[4].getLayoutBounds().getHeight()/4);
		vennValue[5].setY(595+vennValue[5].getLayoutBounds().getHeight()/4);
		vennValue[6].setY(510+vennValue[6].getLayoutBounds().getHeight()/4);
		vennValue[7].setY(532+vennValue[7].getLayoutBounds().getHeight()/4);
		vennValue[8].setY(600+vennValue[8].getLayoutBounds().getHeight()/4);
		vennValue[9].setY(215+vennValue[9].getLayoutBounds().getHeight()/4);
		vennValue[10].setY(500+vennValue[10].getLayoutBounds().getHeight()/4);
		vennValue[11].setY(270+vennValue[11].getLayoutBounds().getHeight()/4);
		vennValue[12].setY(593+vennValue[12].getLayoutBounds().getHeight()/4);
		vennValue[13].setY(585+vennValue[13].getLayoutBounds().getHeight()/4);
		vennValue[14].setY(510+vennValue[14].getLayoutBounds().getHeight()/4);
		vennValue[15].setY(530+vennValue[15].getLayoutBounds().getHeight()/4);
		vennValue[16].setY(290+vennValue[16].getLayoutBounds().getHeight()/4);
		vennValue[17].setY(215+vennValue[17].getLayoutBounds().getHeight()/4);
		vennValue[18].setY(420+vennValue[18].getLayoutBounds().getHeight()/4);
		vennValue[19].setY(378+vennValue[19].getLayoutBounds().getHeight()/4);
		vennValue[20].setY(272+vennValue[20].getLayoutBounds().getHeight()/4);
		vennValue[21].setY(235+vennValue[21].getLayoutBounds().getHeight()/4);
		vennValue[22].setY(485+vennValue[22].getLayoutBounds().getHeight()/4);
		vennValue[23].setY(460+vennValue[23].getLayoutBounds().getHeight()/4);
		vennValue[24].setY(405+vennValue[24].getLayoutBounds().getHeight()/4);
		vennValue[25].setY(250+vennValue[25].getLayoutBounds().getHeight()/4);
		vennValue[26].setY(430+vennValue[26].getLayoutBounds().getHeight()/4);
		vennValue[27].setY(305+vennValue[27].getLayoutBounds().getHeight()/4);
		vennValue[28].setY(335+vennValue[28].getLayoutBounds().getHeight()/4);
		vennValue[29].setY(280+vennValue[29].getLayoutBounds().getHeight()/4);
		vennValue[30].setY(410+vennValue[30].getLayoutBounds().getHeight()/4);
		vennValue[31].setY(400+vennValue[31].getLayoutBounds().getHeight()/4);
	}

	private void drawVenn5() {
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
		circleA = new Ellipse(420, 350, 140, 280);
		circleA.setFill(Color.rgb(255, 255, 0, 0.4));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		circleB = new Ellipse(447, 417, 140, 280);
		circleB.setFill(Color.rgb(0, 255, 255, 0.4));
		circleB.setStrokeWidth(2);
		circleB.setStroke(Color.BLACK);
		circleB.setRotate(72);
		circleB.setId("circleB");
		circleB.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(2);
        		});
		circleC = new Ellipse(390, 463, 140, 280);
		circleC.setFill(Color.rgb(255, 0, 255, 0.4));
		circleC.setStrokeWidth(2);
		circleC.setStroke(Color.BLACK);
		circleC.setRotate(144);
		circleC.setId("circleC");
		circleC.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(3);
        		});
		circleD = new Ellipse(330, 425, 140, 280);
		circleD.setFill(Color.rgb(0, 255, 0, 0.4));
		circleD.setStrokeWidth(2);
		circleD.setStroke(Color.BLACK);
		circleD.setRotate(216);
		circleD.setId("circleD");
		circleD.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(4);
        		});
		circleE = new Ellipse(348, 355, 140, 280);
		circleE.setFill(Color.rgb(0, 0, 255, 0.4));
		circleE.setStrokeWidth(2);
		circleE.setStroke(Color.BLACK);
		circleE.setRotate(288);
		circleE.setId("circleE");
		circleE.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(5);
        		});
		cBox.getChildren().addAll(circleA, circleB, circleC, circleD, circleE);
		headerA = new Text(315, 60, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont ,32));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		headerB = new Text(635, 335, "DatasetB");
		headerB.setFill(Color.rgb(0, 0, 0, 1.0));
		headerB.setFont(Font.font(defaultFont ,32));
		headerB.setRotate(72);
		headerB.setId("headerB");
		moveEvent.makeTextMovable(headerB);
		headerB.setOnMouseReleased(e->{ repositionAnchor(); });
		headerC = new Text(470, 710, "DatasetC");
		headerC.setFill(Color.rgb(0, 0, 0, 1.0));
		headerC.setFont(Font.font(defaultFont ,32));
		headerC.setRotate(324);
		headerC.setId("headerC");
		moveEvent.makeTextMovable(headerC);
		headerC.setOnMouseReleased(e->{ repositionAnchor(); });
		headerD = new Text(50, 675, "DatasetD");
		headerD.setFill(Color.rgb(0, 0, 0, 1.0));
		headerD.setFont(Font.font(defaultFont ,32));
		headerD.setRotate(36);
		headerD.setId("headerD");
		moveEvent.makeTextMovable(headerD);
		headerD.setOnMouseReleased(e->{ repositionAnchor(); });
		headerE = new Text(-40, 275, "DatasetE");
		headerE.setFill(Color.rgb(0, 0, 0, 1.0));
		headerE.setFont(Font.font(defaultFont ,32));
		headerE.setRotate(288);
		headerE.setId("headerE");
		moveEvent.makeTextMovable(headerE);
		headerE.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		for (int i=1; i<32; i++) {
			vennValue[i] = new Text();
		}
		vennValue[1].getStyleClass().add("vennValueText");
		vennValue[2].getStyleClass().add("vennValueText");
		vennValue[3].getStyleClass().add("vennValueText50"); vennValue[3].setScaleX(0.5); vennValue[3].setScaleY(0.5);
		vennValue[4].getStyleClass().add("vennValueText");
		vennValue[5].getStyleClass().add("vennValueText75"); vennValue[5].setScaleX(0.75); vennValue[5].setScaleY(0.75);
		vennValue[6].getStyleClass().add("vennValueText50"); vennValue[6].setScaleX(0.5); vennValue[6].setScaleY(0.5);
		vennValue[7].getStyleClass().add("vennValueText75"); vennValue[7].setScaleX(0.75); vennValue[7].setScaleY(0.75);
		vennValue[8].getStyleClass().add("vennValueText");
		vennValue[9].getStyleClass().add("vennValueText75"); vennValue[9].setScaleX(0.75); vennValue[9].setScaleY(0.75);
		vennValue[10].getStyleClass().add("vennValueText75"); vennValue[10].setScaleX(0.75); vennValue[10].setScaleY(0.75);
		vennValue[11].getStyleClass().add("vennValueText50"); vennValue[11].setScaleX(0.5); vennValue[11].setScaleY(0.5);
		vennValue[12].getStyleClass().add("vennValueText50"); vennValue[12].setScaleX(0.5); vennValue[12].setScaleY(0.5);
		vennValue[13].getStyleClass().add("vennValueText50"); vennValue[13].setScaleX(0.5); vennValue[13].setScaleY(0.5);
		vennValue[14].getStyleClass().add("vennValueText75"); vennValue[14].setScaleX(0.75); vennValue[14].setScaleY(0.75);
		vennValue[15].getStyleClass().add("vennValueText75"); vennValue[15].setScaleX(0.75); vennValue[15].setScaleY(0.75);
		vennValue[16].getStyleClass().add("vennValueText");
		vennValue[17].getStyleClass().add("vennValueText50"); vennValue[17].setScaleX(0.5); vennValue[17].setScaleY(0.5);
		vennValue[18].getStyleClass().add("vennValueText75"); vennValue[18].setScaleX(0.75); vennValue[18].setScaleY(0.75);
		vennValue[19].getStyleClass().add("vennValueText75"); vennValue[19].setScaleX(0.75); vennValue[19].setScaleY(0.75);
		vennValue[20].getStyleClass().add("vennValueText75"); vennValue[20].setScaleX(0.75); vennValue[20].setScaleY(0.75);
		vennValue[21].getStyleClass().add("vennValueText50"); vennValue[21].setScaleX(0.5); vennValue[21].setScaleY(0.5);
		vennValue[22].getStyleClass().add("vennValueText50"); vennValue[22].setScaleX(0.5); vennValue[22].setScaleY(0.5);
		vennValue[23].getStyleClass().add("vennValueText75"); vennValue[23].setScaleX(0.75); vennValue[23].setScaleY(0.75);
		vennValue[24].getStyleClass().add("vennValueText50"); vennValue[24].setScaleX(0.5); vennValue[24].setScaleY(0.5);
		vennValue[25].getStyleClass().add("vennValueText75"); vennValue[25].setScaleX(0.75); vennValue[25].setScaleY(0.75);
		vennValue[26].getStyleClass().add("vennValueText50"); vennValue[26].setScaleX(0.5); vennValue[26].setScaleY(0.5);
		vennValue[27].getStyleClass().add("vennValueText75"); vennValue[27].setScaleX(0.75); vennValue[27].setScaleY(0.75);
		vennValue[28].getStyleClass().add("vennValueText75"); vennValue[28].setScaleX(0.75); vennValue[28].setScaleY(0.75);
		vennValue[29].getStyleClass().add("vennValueText75"); vennValue[29].setScaleX(0.75); vennValue[29].setScaleY(0.75);
		vennValue[30].getStyleClass().add("vennValueText75"); vennValue[30].setScaleX(0.75); vennValue[30].setScaleY(0.75);
		vennValue[31].getStyleClass().add("vennValueText");
		for (int i=1; i<32; i++) {
			vennValue[i].setFill(Color.rgb(0, 0, 0, 1.0));
			vennValue[i].setFont(Font.font(defaultFont, 28));
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
		gBox.getChildren().addAll(headerA, headerB, headerC, headerD, headerE);
		vennPlot.setCenter(gBox);
	}
}


