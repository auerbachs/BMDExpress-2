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

public class Euler1 extends VennDiagram{

	public Euler1(BorderPane borderPane, VennCalc vennCalc) {
		super(borderPane, vennCalc);
	}

	@Override
	protected void setDefaultTabPane() {
		vennTP.addPropertiesTab();
		vennTP.addTabA();
		vennTP.fillColorPickerA.setValue(Color.rgb(255,0,0,0.7));
		vennTP.sliderFillOpacityA.setValue(0.7);
		vennTP.btnToFrontA.setDisable(true);
		vennTP.btnToBackA.setDisable(true);
		vennTP.fontSize.valueProperty().addListener(e->{ setFont(); });
		vennTP.fontFamily.valueProperty().addListener(e->{ setFont(); });
		vennTP.fontWeight.setOnAction(e->{ setFont(); });
		vennTP.fontPosture.setOnAction(e->{ setFont(); });
		vennTP.fontUnderline.setOnAction(e->{ setFontUnderline(); });
		vennTP.fontShadow.setOnAction(e->{ setFontShadow(); });
		vennTP.valuesColorPicker.setOnAction(e->{ setValuesColor(); });
		vennTP.colorPickerBG.setOnAction(e->{ setBGColor(); });
		vennTP.strokeStyleA.valueProperty().addListener(e->{ setOutlineStyleA(); });
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
		vennTP.datasetHeaderA.textProperty().addListener(e->{ headerA.setText(vennTP.datasetHeaderA.getText()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontSizeA.valueProperty().addListener(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontFamilyA.valueProperty().addListener(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontWeightA.setOnAction(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontPostureA.setOnAction(e->{ setFontA(); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.fontUnderlineA.setOnAction(e->{ setFontUnderlineA(); });
		vennTP.fontShadowA.setOnAction(e->{ setFontShadowA(); });
		vennTP.titleColorPickerA.setOnAction(e->{headerA.setFill( vennTP.titleColorPickerA.getValue()); });
		vennTP.sliderTitleAngleA.valueProperty().addListener(e->{ headerA.setRotate(vennTP.sliderTitleAngleA.getValue()); repositionAnchor(); setTextAlignmentToCenter(); });
		vennTP.totalCountA.setText(vCalc.getSizeA()+"");

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

	@Override
	public void setVennType() {
		int[] overlap = vCalc.getOverlap();
		switch (vCalc.getVennType()) {
			case 1:
				drawEuler001();
				vCount = 2;
				vennValue[1].setText(String.valueOf(overlap[1]));
				vennValue[1].setId("1");
				vennTP.sliderSizeCircleA.setDisable(true);
				break;
		}
		setFont(); setFontA();
		if (!vCalc.getHeaderA().equals("")) { vennTP.datasetHeaderA.setText(vCalc.getHeaderA()); }

	}

	private void repositionAnchor() {
		double angleA = headerA.getRotate();
		if (angleA==360.0) { angleA = 0; }
		double x = headerA.getX() + headerA.getLayoutX() - (headerA.getLayoutBounds().getHeight()/4);
		if (x>0) { x = 0; }
		anchorPoint.setX(x);
		double y = headerA.getY() + headerA.getLayoutY()-headerA.getFont().getSize()-(headerA.getLayoutBounds().getWidth()/2) * Math.abs(Math.sin(Math.toRadians(angleA)));
		if (y>0) { y = 0; }
		anchorPoint.setY(y);
	}

	private void setTextAlignmentToCenter() {
		headerA.setX(400-headerA.getLayoutBounds().getWidth()/2);
	}

	private void drawEuler001() {
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
		circleA = new Ellipse(400, 400, 200, 200);
		circleA.setFill(Color.rgb(255, 0, 0, 0.7));
		circleA.setStrokeWidth(2);
		circleA.setStroke(Color.BLACK);
		circleA.setId("circleA");
		circleA.setOnMouseClicked(e->{
				vennTP.tabPane.getSelectionModel().select(1);
        		});
		cBox.getChildren().add(circleA);
		headerA = new Text(400, 180, "DatasetA");
		headerA.setFill(Color.rgb(0, 0, 0, 1.0));
		headerA.setFont(Font.font(defaultFont, FontWeight.NORMAL, FontPosture.REGULAR, 28));
		headerA.setId("headerA");
		moveEvent.makeTextMovable(headerA);
		headerA.setOnMouseReleased(e->{ repositionAnchor(); });
		gBox.getChildren().addAll(anchorPoint, bg, cBox);
		vennValue[1] = new Text(325, 400, "A");
		for (int i=1; i<2; i++) {
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
		gBox.getChildren().add(headerA);
		setTextAlignmentToCenter();
		vennPlot.setCenter(gBox);
	}
}


