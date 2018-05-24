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

import javafx.scene.layout.BorderPane;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class VennTabPane {
	public TabPane tabPane;
	public Tab startTab, propertiesTab, tabA, tabB, tabC, tabD, tabE;
	public ColorPicker colorPickerBG, valuesColorPicker;
	public ColorPicker fillColorPickerA, fillColorPickerB, fillColorPickerC, fillColorPickerD, fillColorPickerE;
	public ColorPicker strokeColorPickerA, strokeColorPickerB, strokeColorPickerC, strokeColorPickerD, strokeColorPickerE;
	public Slider sliderFillOpacityA, sliderFillOpacityB, sliderFillOpacityC, sliderFillOpacityD, sliderFillOpacityE;
	public Slider sliderStrokeWidthA, sliderStrokeWidthB, sliderStrokeWidthC, sliderStrokeWidthD, sliderStrokeWidthE;
	public Slider sliderSizeCircleA, sliderSizeCircleB, sliderSizeCircleC, sliderSizeCircleD, sliderSizeCircleE;
	public Slider sliderTitleAngleA, sliderTitleAngleB, sliderTitleAngleC, sliderTitleAngleD, sliderTitleAngleE;
	public Slider sliderValuesAngle;
	public Slider sliderOverlapCirclesAB; // sliderOverlapCirclesAC, sliderOverlapCirclesBC;
	public TextField datasetHeaderA, datasetHeaderB, datasetHeaderC, datasetHeaderD, datasetHeaderE;
	public ComboBox<Integer>  fontSize, fontSizeA, fontSizeB, fontSizeC, fontSizeD, fontSizeE;
	public ComboBox<String> fontFamily, fontFamilyA, fontFamilyB, fontFamilyC, fontFamilyD, fontFamilyE;
	public ComboBox<String> strokeStyleA, strokeStyleB, strokeStyleC, strokeStyleD, strokeStyleE;
	public TextArea datasetTextAreaA, datasetTextAreaB, datasetTextAreaC, datasetTextAreaD, datasetTextAreaE;
	public ToggleButton fontWeight, fontWeightA, fontWeightB, fontWeightC, fontWeightD, fontWeightE;
	public ToggleButton fontPosture, fontPostureA, fontPostureB, fontPostureC, fontPostureD, fontPostureE;
	public ToggleButton fontUnderline, fontUnderlineA, fontUnderlineB, fontUnderlineC, fontUnderlineD, fontUnderlineE;
	public ToggleButton fontShadow, fontShadowA, fontShadowB, fontShadowC, fontShadowD, fontShadowE;
	public Button btnToFrontA, btnToFrontB, btnToFrontC, btnToFrontD, btnToFrontE;
	public Button btnToBackA, btnToBackB, btnToBackC, btnToBackD, btnToBackE;
	public ColorPicker titleColorPickerA, titleColorPickerB, titleColorPickerC, titleColorPickerD, titleColorPickerE;
	public TextArea overlapTextArea;
	public CheckBox cbBG;
	public Text totalCountA, totalCountB, totalCountC, totalCountD, totalCountE;
	private final Image imB, imI, imU, imS;
	private final Integer[] fontSizeList = new Integer[]{8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56, 58, 60, 62, 64};
	private final String[] strokeStyleList = new String[]{"Line", "Dashes", "Dots"};
	private String defaultFont = "System";

	public VennTabPane(BorderPane borderPane) {
		imB = new Image("https://i.imgur.com/E4DxZpw.png", 30, 30, false, false);
		imI = new Image("https://i.imgur.com/E4DxZpw.png", 30, 30, false, false);
		imU = new Image("https://i.imgur.com/E4DxZpw.png", 30, 30, false, false);
		imS = new Image("https://i.imgur.com/E4DxZpw.png", 30, 30, false, false);
		makeTabPane();
		borderPane.setLeft(tabPane);
	}

	private void makeTabPane() {
		tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.setPrefWidth(400);
		tabPane.setId("mainTabPane");
	}

	public void addPropertiesTab() {
		propertiesTab = new Tab();
		propertiesTab.setClosable(false);
		propertiesTab.setText("  B & O  ");
		GridPane gridPaneBG = new GridPane();
			gridPaneBG.setHgap(10);
			gridPaneBG.setVgap(10);
			Text colorTextBG = new Text("Background: ");
			colorTextBG.getStyleClass().add("tabPaneText");
			gridPaneBG.add(colorTextBG, 1, 1);
			HBox bgHB = new HBox();
 			cbBG = new CheckBox();
			cbBG.setSelected(true);
			cbBG.setPrefWidth(26);
			cbBG.setPrefHeight(26);
			colorPickerBG = new ColorPicker(Color.WHITE);
			colorPickerBG.setId("colorPickerBG");
			bgHB.getChildren().addAll(cbBG, colorPickerBG);
			gridPaneBG.add(bgHB, 2, 1);

			Separator tabSeparator1 = new Separator();
			gridPaneBG.add(tabSeparator1, 1, 2, 2, 1);

 			// Ellipses overlap slider
			Text overlapCirclesTextAB = new Text("Overlap %:");
			overlapCirclesTextAB.getStyleClass().add("tabPaneText");
			gridPaneBG.add(overlapCirclesTextAB, 1, 4);
			sliderOverlapCirclesAB = new Slider();
			sliderOverlapCirclesAB.setMin(0);
			sliderOverlapCirclesAB.setMax(100);
			sliderOverlapCirclesAB.setValue(50);
			sliderOverlapCirclesAB.setShowTickLabels(true);
			sliderOverlapCirclesAB.setShowTickMarks(true);
			sliderOverlapCirclesAB.setMajorTickUnit(20);
			sliderOverlapCirclesAB.setMinorTickCount(3);
			sliderOverlapCirclesAB.setBlockIncrement(1);
			gridPaneBG.add(sliderOverlapCirclesAB, 2, 4);

			Separator tabSeparator2 = new Separator();
			gridPaneBG.add(tabSeparator2, 1, 6, 2, 1);

			// Dataset header Font
			Text fontHeaderText = new Text("Font: ");
			fontHeaderText.getStyleClass().add("tabPaneText");
			gridPaneBG.add(fontHeaderText, 1, 7);
			HBox fontHB = new HBox();
			fontSize = new ComboBox<Integer>();
			fontSize.getItems().addAll(fontSizeList);
			fontSize.setValue(32);
			fontSize.setPrefWidth(80);
			fontFamily = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderText.getFont().getFamilies().size(); i++) {
				fontFamily.getItems().add(fontHeaderText.getFont().getFamilies().get(i));
			}
			fontFamily.setValue(defaultFont);
			fontFamily.setPrefWidth(150);
			fontFamily.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSize, fontFamily);
			gridPaneBG.add(fontHB, 2, 7);
			// Font Family Style
			Text fontStyleValueText = new Text("Font style: ");
			fontStyleValueText.getStyleClass().add("tabPaneText");
			gridPaneBG.add(fontStyleValueText, 1, 8);
			HBox fontStyleHB = new HBox();
			fontWeight = new ToggleButton();
			fontWeight.setId("fontWeight");
			fontWeight.setTooltip(new Tooltip("Bold"));
			fontWeight.getStyleClass().add("fontStyleTB");
			fontWeight.setGraphic(new ImageView(imB));
			fontPosture = new ToggleButton();
			fontPosture.setId("fontPosture");
			fontPosture.setTooltip(new Tooltip("Italic"));
			fontPosture.getStyleClass().add("fontStyleTB");
			fontPosture.setGraphic(new ImageView(imI));
			fontUnderline = new ToggleButton();
			fontUnderline.setId("fontUnderline");
			fontUnderline.setTooltip(new Tooltip("Underline"));
			fontUnderline.getStyleClass().add("fontStyleTB");
			fontUnderline.setGraphic(new ImageView(imU));
			fontShadow = new ToggleButton();
			fontShadow.setId("fontShadow");
			fontShadow.setTooltip(new Tooltip("Shadow"));
			fontShadow.getStyleClass().add("fontStyleTB");
			fontShadow.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeight, fontPosture, fontUnderline, fontShadow);
			gridPaneBG.add(fontStyleHB, 2, 8);
			// Title color picker
			Text valuesText = new Text("Values color:");
			valuesText.getStyleClass().add("tabPaneText");
			gridPaneBG.add(valuesText, 1, 9);
			valuesColorPicker = new ColorPicker(Color.rgb(0,0,0,1));
			valuesColorPicker.setId("valuesColorPicker");
			gridPaneBG.add(valuesColorPicker, 2, 9);
 			// Values angle slider
			Text titleValuesText = new Text("Values angle: ");
			titleValuesText.getStyleClass().add("tabPaneText");
			gridPaneBG.add(titleValuesText, 1, 10);
			sliderValuesAngle = new Slider();
			sliderValuesAngle.setMin(0);
			sliderValuesAngle.setMax(360);
			sliderValuesAngle.setValue(0);
			sliderValuesAngle.setShowTickLabels(true);
			sliderValuesAngle.setShowTickMarks(true);
			sliderValuesAngle.setMajorTickUnit(90);
			sliderValuesAngle.setMinorTickCount(1);
			sliderValuesAngle.setBlockIncrement(1);
			gridPaneBG.add(sliderValuesAngle, 2, 10);
			// Overlap Text Area
			overlapTextArea = new TextArea();
			overlapTextArea.setWrapText(false);
			overlapTextArea.setPrefWidth(340);
			overlapTextArea.setPrefHeight(400);
			overlapTextArea.setEditable(false);
			gridPaneBG.add(overlapTextArea, 1, 11, 2, 1);
		propertiesTab.setContent(gridPaneBG);
		tabPane.getTabs().add(propertiesTab);
	}

	public void addTabA() {
		tabA = new Tab();
		tabA.setClosable(false);
		tabA.setText("EllipseA");
		GridPane gridPaneA = new GridPane();
			gridPaneA.setHgap(10);
			gridPaneA.setVgap(10);
			// Ellipse fill color picker
			Text fillColorTextA = new Text("Fill color: ");
			fillColorTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(fillColorTextA, 1, 1);
			fillColorPickerA = new ColorPicker(Color.rgb(255,0,0,0.7));
			fillColorPickerA.setId("fillColorPicker");
			gridPaneA.add(fillColorPickerA, 2, 1);
 			// Ellipse fill opacity
			Text fillOpacityTextA = new Text("Fill opacity: ");
			fillOpacityTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(fillOpacityTextA, 1, 2);
			sliderFillOpacityA = new Slider();
			sliderFillOpacityA.setMin(0);
			sliderFillOpacityA.setMax(1);
			sliderFillOpacityA.setValue(0.5);
			sliderFillOpacityA.setShowTickLabels(true);
			sliderFillOpacityA.setShowTickMarks(true);
			sliderFillOpacityA.setMajorTickUnit(0.2);
			sliderFillOpacityA.setMinorTickCount(1);
			sliderFillOpacityA.setBlockIncrement(0.1);
			gridPaneA.add(sliderFillOpacityA, 2, 2);
			// Ellipse stroke style
			Text strokeStyleTextA = new Text("Stroke style: ");
			strokeStyleTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(strokeStyleTextA, 1, 3);
			strokeStyleA = new ComboBox<String>();
			strokeStyleA.getItems().addAll(strokeStyleList);
			strokeStyleA.setPrefWidth(150);
			strokeStyleA.setMaxWidth(150);
			strokeStyleA.setId("strokeStyle");
			strokeStyleA.setValue("Line");
			gridPaneA.add(strokeStyleA, 2, 3);
 			// Ellipse stroke width slider
			Text strokeWidthTextA = new Text("Stroke width: ");
			strokeWidthTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(strokeWidthTextA, 1, 4);
			sliderStrokeWidthA = new Slider();
			sliderStrokeWidthA.setMin(0);
			sliderStrokeWidthA.setMax(10);
			sliderStrokeWidthA.setValue(2);
			sliderStrokeWidthA.setShowTickLabels(true);
			sliderStrokeWidthA.setShowTickMarks(true);
			sliderStrokeWidthA.setMajorTickUnit(2);
			sliderStrokeWidthA.setMinorTickCount(1);
			sliderStrokeWidthA.setBlockIncrement(1);
			gridPaneA.add(sliderStrokeWidthA, 2, 4);
			// Ellipse stroke color picker
			Text strokeColorTextA = new Text("Stroke color: ");
			strokeColorTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(strokeColorTextA, 1, 5);
			strokeColorPickerA = new ColorPicker(Color.rgb(0,0,0,1.0));
			strokeColorPickerA.setId("strokeColorPicker");
			gridPaneA.add(strokeColorPickerA, 2, 5);
			// Ellipse size slider
			Text sizeCircleTextA = new Text("Size %: ");
			sizeCircleTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(sizeCircleTextA, 1, 6);
			sliderSizeCircleA = new Slider();
			sliderSizeCircleA.setMin(0);
			sliderSizeCircleA.setMax(100);
			sliderSizeCircleA.setValue(100);
			sliderSizeCircleA.setShowTickLabels(true);
			sliderSizeCircleA.setShowTickMarks(true);
			sliderSizeCircleA.setMajorTickUnit(20);
			sliderSizeCircleA.setMinorTickCount(3);
			sliderSizeCircleA.setBlockIncrement(1);
			gridPaneA.add(sliderSizeCircleA, 2, 6);
			// Ellipse z-order
			Text zOrderTextA = new Text("Z-order: ");
			zOrderTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(zOrderTextA, 1, 7);
			HBox zOrderHB = new HBox();
			btnToFrontA = new Button("Bring to Front");
			btnToBackA = new Button("Send to Back");
			zOrderHB.getChildren().addAll(btnToFrontA, btnToBackA);
			gridPaneA.add(zOrderHB, 2, 7);
			Separator tabSeparator = new Separator();
			gridPaneA.add(tabSeparator, 1, 8, 2, 1);
			// Dataset header
			datasetHeaderA = new TextField();
			datasetHeaderA.setPromptText("DatasetA");
			gridPaneA.add(datasetHeaderA, 1, 9, 2, 1);
			// Dataset header Font
			Text fontHeaderTextA = new Text("Font: ");
			fontHeaderTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(fontHeaderTextA, 1, 10);
			HBox fontHB = new HBox();
			fontSizeA = new ComboBox<Integer>();
			fontSizeA.getItems().addAll(fontSizeList);
			fontSizeA.setValue(28);
			fontSizeA.setPrefWidth(80);
			fontFamilyA = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderTextA.getFont().getFamilies().size(); i++) {
				fontFamilyA.getItems().add(fontHeaderTextA.getFont().getFamilies().get(i));
			}
			fontFamilyA.setValue(defaultFont);
			fontFamilyA.setPrefWidth(150);
			fontFamilyA.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSizeA, fontFamilyA);
			gridPaneA.add(fontHB, 2, 10);
			// Font Family Style
			Text fontStyleHeaderTextA = new Text("Font style: ");
			fontStyleHeaderTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(fontStyleHeaderTextA, 1, 11);
			HBox fontStyleHB = new HBox();
			fontWeightA = new ToggleButton();
			fontWeightA.setId("fontWeight");
			fontWeightA.setTooltip(new Tooltip("Bold"));
			fontWeightA.getStyleClass().add("fontStyleTB");
			fontWeightA.setGraphic(new ImageView(imB));
			fontPostureA = new ToggleButton();
			fontPostureA.setId("fontPosture");
			fontPostureA.setTooltip(new Tooltip("Italic"));
			fontPostureA.getStyleClass().add("fontStyleTB");
			fontPostureA.setGraphic(new ImageView(imI));
			fontUnderlineA = new ToggleButton();
			fontUnderlineA.setId("fontUnderline");
			fontUnderlineA.setTooltip(new Tooltip("Underline"));
			fontUnderlineA.getStyleClass().add("fontStyleTB");
			fontUnderlineA.setGraphic(new ImageView(imU));
			fontShadowA = new ToggleButton();
			fontShadowA.setId("fontShadow");
			fontShadowA.setTooltip(new Tooltip("Shadow"));
			fontShadowA.getStyleClass().add("fontStyleTB");
			fontShadowA.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeightA, fontPostureA, fontUnderlineA, fontShadowA);
			gridPaneA.add(fontStyleHB, 2, 11);
			// Title color picker
			Text titleTextA = new Text("Title color:");
			titleTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(titleTextA, 1, 12);
			titleColorPickerA = new ColorPicker(Color.rgb(0,0,0,1));
			titleColorPickerA.setId("titleColorPicker");
			gridPaneA.add(titleColorPickerA, 2, 12);
 			// Title angle slider
			Text titleAngleTextA = new Text("Title angle: ");
			titleAngleTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(titleAngleTextA, 1, 13);
			sliderTitleAngleA = new Slider();
			sliderTitleAngleA.setMin(0);
			sliderTitleAngleA.setMax(360);
			sliderTitleAngleA.setValue(0);
			sliderTitleAngleA.setShowTickLabels(true);
			sliderTitleAngleA.setShowTickMarks(true);
			sliderTitleAngleA.setMajorTickUnit(90);
			sliderTitleAngleA.setMinorTickCount(1);
			sliderTitleAngleA.setBlockIncrement(1);
			gridPaneA.add(sliderTitleAngleA, 2, 13);
			Separator tabSeparator2 = new Separator();
			gridPaneA.add(tabSeparator2, 1, 14, 2, 1);
			Text totalTextA = new Text("Total counts:");
			totalTextA.getStyleClass().add("tabPaneText");
			gridPaneA.add(totalTextA, 1, 15);
			totalCountA = new Text("0");
			totalCountA.getStyleClass().add("tabPaneText");
			gridPaneA.add(totalCountA, 2, 15);
		tabA.setContent(gridPaneA);
		tabPane.getTabs().add(tabA);
	}

	public void addTabB() {
		tabB = new Tab();
		tabB.setClosable(false);
		tabB.setText("EllipseB");
		GridPane gridPaneB = new GridPane();
			gridPaneB.setHgap(10);
			gridPaneB.setVgap(10);
			// Circle fill color picker
			Text fillColorTextB = new Text("Fill color: ");
			fillColorTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(fillColorTextB, 1, 1);
			fillColorPickerB = new ColorPicker(Color.rgb(0,255,0,0.7));
			fillColorPickerB.setId("fillColorPicker");
			gridPaneB.add(fillColorPickerB, 2, 1);
 			// Ellipse fill opacity
			Text fillOpacityTextB = new Text("Fill opacity: ");
			fillOpacityTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(fillOpacityTextB, 1, 2);
			sliderFillOpacityB = new Slider();
			sliderFillOpacityB.setMin(0);
			sliderFillOpacityB.setMax(1);
			sliderFillOpacityB.setValue(0.5);
			sliderFillOpacityB.setShowTickLabels(true);
			sliderFillOpacityB.setShowTickMarks(true);
			sliderFillOpacityB.setMajorTickUnit(0.2);
			sliderFillOpacityB.setMinorTickCount(1);
			sliderFillOpacityB.setBlockIncrement(0.1);
			gridPaneB.add(sliderFillOpacityB, 2, 2);
			// Ellipse stroke style
			Text strokeStyleTextB = new Text("Stroke style: ");
			strokeStyleTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(strokeStyleTextB, 1, 3);
			strokeStyleB = new ComboBox<String>();
			strokeStyleB.getItems().addAll(strokeStyleList);
			strokeStyleB.setPrefWidth(150);
			strokeStyleB.setMaxWidth(150);
			strokeStyleB.setId("strokeStyle");
			strokeStyleB.setValue("Line");
			gridPaneB.add(strokeStyleB, 2, 3);
 			// Circle stroke width slider
			Text strokeWidthTextB = new Text("Stroke width: ");
			strokeWidthTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(strokeWidthTextB, 1, 4);
			sliderStrokeWidthB = new Slider();
			sliderStrokeWidthB.setMin(0);
			sliderStrokeWidthB.setMax(10);
			sliderStrokeWidthB.setValue(2);
			sliderStrokeWidthB.setShowTickLabels(true);
			sliderStrokeWidthB.setShowTickMarks(true);
			sliderStrokeWidthB.setMajorTickUnit(2);
			sliderStrokeWidthB.setMinorTickCount(1);
			sliderStrokeWidthB.setBlockIncrement(1);
			gridPaneB.add(sliderStrokeWidthB, 2, 4); 
			// Circle stroke color picker
			Text strokeColorTextB = new Text("Stroke color: ");
			strokeColorTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(strokeColorTextB, 1, 5);
			strokeColorPickerB = new ColorPicker(Color.rgb(0,0,0,1.0));
			strokeColorPickerB.setId("strokeColorPicker");
			gridPaneB.add(strokeColorPickerB, 2, 5);
 			// Circle size slider
			Text sizeCircleTextB = new Text("Size %: ");
			sizeCircleTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(sizeCircleTextB, 1, 6);
			sliderSizeCircleB = new Slider();
			sliderSizeCircleB.setMin(0);
			sliderSizeCircleB.setMax(100);
			sliderSizeCircleB.setValue(100);
			sliderSizeCircleB.setShowTickLabels(true);
			sliderSizeCircleB.setShowTickMarks(true);
			sliderSizeCircleB.setMajorTickUnit(20);
			sliderSizeCircleB.setMinorTickCount(3);
			sliderSizeCircleB.setBlockIncrement(1);
			gridPaneB.add(sliderSizeCircleB, 2, 6);

			// Ellipse z-order
			Text zOrderTextB = new Text("Z-order: ");
			zOrderTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(zOrderTextB, 1, 7);
			HBox zOrderHB = new HBox();
			btnToFrontB = new Button("Bring to Front");
			btnToBackB = new Button("Send to Back");
			zOrderHB.getChildren().addAll(btnToFrontB, btnToBackB);
			gridPaneB.add(zOrderHB, 2, 7);
			Separator tabSeparator = new Separator();
			gridPaneB.add(tabSeparator, 1, 8, 2, 1);

			// Dataset header
			datasetHeaderB = new TextField();
			datasetHeaderB.setPromptText("DatasetB");
			gridPaneB.add(datasetHeaderB, 1, 9, 2, 1);
			// Dataset header Font
			Text fontHeaderTextB = new Text("Font: ");
			fontHeaderTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(fontHeaderTextB, 1, 10);
			HBox fontHB = new HBox();
			fontSizeB = new ComboBox<Integer>();
			fontSizeB.getItems().addAll(fontSizeList);
			fontSizeB.setValue(28);
			fontSizeB.setPrefWidth(80);
			fontFamilyB = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderTextB.getFont().getFamilies().size(); i++) {
				fontFamilyB.getItems().add(fontHeaderTextB.getFont().getFamilies().get(i));
			}
			fontFamilyB.setValue(defaultFont);
			fontFamilyB.setPrefWidth(150);
			fontFamilyB.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSizeB, fontFamilyB);
			gridPaneB.add(fontHB, 2, 10);
			// Font Family Style
			Text fontStyleHeaderTextB = new Text("Font style: ");
			fontStyleHeaderTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(fontStyleHeaderTextB, 1, 11);
			HBox fontStyleHB = new HBox();
			fontWeightB = new ToggleButton();
			fontWeightB.setId("fontWeight");
			fontWeightB.setTooltip(new Tooltip("Bold"));
			fontWeightB.getStyleClass().add("fontStyleTB");
			fontWeightB.setGraphic(new ImageView(imB));
			fontPostureB = new ToggleButton();
			fontPostureB.setId("fontPosture");
			fontPostureB.setTooltip(new Tooltip("Italic"));
			fontPostureB.getStyleClass().add("fontStyleTB");
			fontPostureB.setGraphic(new ImageView(imI));
			fontUnderlineB = new ToggleButton();
			fontUnderlineB.setId("fontUnderline");
			fontUnderlineB.setTooltip(new Tooltip("Underline"));
			fontUnderlineB.getStyleClass().add("fontStyleTB");
			fontUnderlineB.setGraphic(new ImageView(imU));
			fontShadowB = new ToggleButton();
			fontShadowB.setId("fontShadow");
			fontShadowB.setTooltip(new Tooltip("Shadow"));
			fontShadowB.getStyleClass().add("fontStyleTB");
			fontShadowB.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeightB, fontPostureB, fontUnderlineB, fontShadowB);
			gridPaneB.add(fontStyleHB, 2, 11);
			// Title color picker
			Text titleTextB = new Text("Title color:");
			titleTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(titleTextB, 1, 12);
			titleColorPickerB = new ColorPicker(Color.rgb(0,0,0,1));
			titleColorPickerB.setId("titleColorPicker");
			gridPaneB.add(titleColorPickerB, 2, 12);
 			// Title angle slider
			Text titleAngleTextB = new Text("Title angle: ");
			titleAngleTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(titleAngleTextB, 1, 13);
			sliderTitleAngleB = new Slider();
			sliderTitleAngleB.setMin(0);
			sliderTitleAngleB.setMax(360);
			sliderTitleAngleB.setValue(0);
			sliderTitleAngleB.setShowTickLabels(true);
			sliderTitleAngleB.setShowTickMarks(true);
			sliderTitleAngleB.setMajorTickUnit(90);
			sliderTitleAngleB.setMinorTickCount(1);
			sliderTitleAngleB.setBlockIncrement(1);
			gridPaneB.add(sliderTitleAngleB, 2, 13);
			Separator tabSeparator2 = new Separator();
			gridPaneB.add(tabSeparator2, 1, 14, 2, 1);
			Text totalTextB = new Text("Total counts:");
			totalTextB.getStyleClass().add("tabPaneText");
			gridPaneB.add(totalTextB, 1, 15);
			totalCountB = new Text("0");
			totalCountB.getStyleClass().add("tabPaneText");
			gridPaneB.add(totalCountB, 2, 15);
		tabB.setContent(gridPaneB);
		tabPane.getTabs().add(tabB);
	}

	public void addTabC() {
		tabC = new Tab();
		tabC.setClosable(false);
		tabC.setText("EllipseC");
		GridPane gridPaneC = new GridPane();
			gridPaneC.setHgap(10);
			gridPaneC.setVgap(10);
			// Circle fill color picker
			Text fillColorTextC = new Text("Fill color: ");
			fillColorTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(fillColorTextC, 1, 1);
			fillColorPickerC = new ColorPicker(Color.rgb(255,0,255,0.4));
			fillColorPickerC.setId("fillColorPicker");
			gridPaneC.add(fillColorPickerC, 2, 1);
 			// Ellipse fill opacity
			Text fillOpacityTextC = new Text("Fill opacity: ");
			fillOpacityTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(fillOpacityTextC, 1, 2);
			sliderFillOpacityC = new Slider();
			sliderFillOpacityC.setMin(0);
			sliderFillOpacityC.setMax(1);
			sliderFillOpacityC.setValue(0.5);
			sliderFillOpacityC.setShowTickLabels(true);
			sliderFillOpacityC.setShowTickMarks(true);
			sliderFillOpacityC.setMajorTickUnit(0.2);
			sliderFillOpacityC.setMinorTickCount(1);
			sliderFillOpacityC.setBlockIncrement(0.1);
			gridPaneC.add(sliderFillOpacityC, 2, 2);
			// Ellipse stroke style
			Text strokeStyleTextC = new Text("Stroke style: ");
			strokeStyleTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(strokeStyleTextC, 1, 3);
			strokeStyleC = new ComboBox<String>();
			strokeStyleC.getItems().addAll(strokeStyleList);
			strokeStyleC.setPrefWidth(150);
			strokeStyleC.setMaxWidth(150);
			strokeStyleC.setId("strokeStyle");
			strokeStyleC.setValue("Line");
			gridPaneC.add(strokeStyleC, 2, 3);
 			// Circle stroke width slider
			Text strokeWidthTextC = new Text("Stroke width: ");
			strokeWidthTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(strokeWidthTextC, 1, 4);
			sliderStrokeWidthC = new Slider();
			sliderStrokeWidthC.setMin(0);
			sliderStrokeWidthC.setMax(10);
			sliderStrokeWidthC.setValue(2);
			sliderStrokeWidthC.setShowTickLabels(true);
			sliderStrokeWidthC.setShowTickMarks(true);
			sliderStrokeWidthC.setMajorTickUnit(2);
			sliderStrokeWidthC.setMinorTickCount(1);
			sliderStrokeWidthC.setBlockIncrement(1);
			gridPaneC.add(sliderStrokeWidthC, 2, 4); 
			// Circle stroke color picker
			Text strokeColorTextC = new Text("Stroke color: ");
			strokeColorTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(strokeColorTextC, 1, 5);
			strokeColorPickerC = new ColorPicker(Color.rgb(0,0,0,1.0));
			strokeColorPickerC.setId("strokeColorPicker");
			gridPaneC.add(strokeColorPickerC, 2, 5);
 			// Circle size slider
			Text sizeCircleTextC = new Text("Size %: ");
			sizeCircleTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(sizeCircleTextC, 1, 6);
			sliderSizeCircleC = new Slider();
			sliderSizeCircleC.setMin(0);
			sliderSizeCircleC.setMax(100);
			sliderSizeCircleC.setValue(100);
			sliderSizeCircleC.setShowTickLabels(true);
			sliderSizeCircleC.setShowTickMarks(true);
			sliderSizeCircleC.setMajorTickUnit(20);
			sliderSizeCircleC.setMinorTickCount(3);
			sliderSizeCircleC.setBlockIncrement(1);
			gridPaneC.add(sliderSizeCircleC, 2, 6);
			// Ellipse z-order
			Text zOrderTextC = new Text("Z-order: ");
			zOrderTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(zOrderTextC, 1, 7);
			HBox zOrderHB = new HBox();
			btnToFrontC = new Button("Bring to Front");
			btnToBackC = new Button("Send to Back");
			zOrderHB.getChildren().addAll(btnToFrontC, btnToBackC);
			gridPaneC.add(zOrderHB, 2, 7);
			Separator tabSeparator = new Separator();
			gridPaneC.add(tabSeparator, 1, 8, 2, 1);
			// Dataset header
			datasetHeaderC = new TextField();
			datasetHeaderC.setPromptText("DatasetC");
			gridPaneC.add(datasetHeaderC, 1, 9, 2, 1);
			// Dataset header Font
			Text fontHeaderTextC = new Text("Font: ");
			fontHeaderTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(fontHeaderTextC, 1, 10);
			HBox fontHB = new HBox();

			fontSizeC = new ComboBox<Integer>();
			fontSizeC.getItems().addAll(fontSizeList);
			fontSizeC.setValue(28);
			fontSizeC.setPrefWidth(80);
			fontFamilyC = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderTextC.getFont().getFamilies().size(); i++) {
				fontFamilyC.getItems().add(fontHeaderTextC.getFont().getFamilies().get(i));
			}
			fontFamilyC.setValue(defaultFont);
			fontFamilyC.setPrefWidth(150);
			fontFamilyC.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSizeC, fontFamilyC);
			gridPaneC.add(fontHB, 2, 10);
			// Font Family Style
			Text fontStyleHeaderTextC = new Text("Font style: ");
			fontStyleHeaderTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(fontStyleHeaderTextC, 1, 11);
			HBox fontStyleHB = new HBox();
			fontWeightC = new ToggleButton();
			fontWeightC.setId("fontWeight");
			fontWeightC.setTooltip(new Tooltip("Bold"));
			fontWeightC.getStyleClass().add("fontStyleTB");
			fontWeightC.setGraphic(new ImageView(imB));
			fontPostureC = new ToggleButton();
			fontPostureC.setId("fontPosture");
			fontPostureC.setTooltip(new Tooltip("Italic"));
			fontPostureC.getStyleClass().add("fontStyleTB");
			fontPostureC.setGraphic(new ImageView(imI));
			fontUnderlineC = new ToggleButton();
			fontUnderlineC.setId("fontUnderline");
			fontUnderlineC.setTooltip(new Tooltip("Underline"));
			fontUnderlineC.getStyleClass().add("fontStyleTB");
			fontUnderlineC.setGraphic(new ImageView(imU));
			fontShadowC = new ToggleButton();
			fontShadowC.setId("fontShadow");
			fontShadowC.setTooltip(new Tooltip("Shadow"));
			fontShadowC.getStyleClass().add("fontStyleTB");
			fontShadowC.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeightC, fontPostureC, fontUnderlineC, fontShadowC);
			gridPaneC.add(fontStyleHB, 2, 11);
			// Title color picker
			Text titleTextC = new Text("Title color:");
			titleTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(titleTextC, 1, 12);
			titleColorPickerC = new ColorPicker(Color.rgb(0,0,0,1));
			titleColorPickerC.setId("titleColorPicker");
			gridPaneC.add(titleColorPickerC, 2, 12);
 			// Title angle slider
			Text titleAngleTextC = new Text("Title angle: ");
			titleAngleTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(titleAngleTextC, 1, 13);
			sliderTitleAngleC = new Slider();
			sliderTitleAngleC.setMin(0);
			sliderTitleAngleC.setMax(360);
			sliderTitleAngleC.setValue(0);
			sliderTitleAngleC.setShowTickLabels(true);
			sliderTitleAngleC.setShowTickMarks(true);
			sliderTitleAngleC.setMajorTickUnit(90);
			sliderTitleAngleC.setMinorTickCount(1);
			sliderTitleAngleC.setBlockIncrement(1);
			gridPaneC.add(sliderTitleAngleC, 2, 13);
			Separator tabSeparator2 = new Separator();
			gridPaneC.add(tabSeparator2, 1, 14, 2, 1);
			Text totalTextC = new Text("Total counts:");
			totalTextC.getStyleClass().add("tabPaneText");
			gridPaneC.add(totalTextC, 1, 15);
			totalCountC = new Text("0");
			totalCountC.getStyleClass().add("tabPaneText");
			gridPaneC.add(totalCountC, 2, 15);
		tabC.setContent(gridPaneC);
		tabPane.getTabs().add(tabC);
	}

	public void addTabD() {
		tabD = new Tab();
		tabD.setClosable(false);
		tabD.setText("EllipseD");
		GridPane gridPaneD = new GridPane();
			gridPaneD.setHgap(10);
			gridPaneD.setVgap(10);
			// Circle fill color picker
			Text fillColorTextD = new Text("Fill color: ");
			fillColorTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(fillColorTextD, 1, 1);
			fillColorPickerD = new ColorPicker(Color.rgb(0,255,0,0.4));
			fillColorPickerD.setId("fillColorPicker");
			gridPaneD.add(fillColorPickerD, 2, 1);
 			// Ellipse fill opacity
			Text fillOpacityTextD = new Text("Fill opacity: ");
			fillOpacityTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(fillOpacityTextD, 1, 2);
			sliderFillOpacityD = new Slider();
			sliderFillOpacityD.setMin(0);
			sliderFillOpacityD.setMax(1);
			sliderFillOpacityD.setValue(0.5);
			sliderFillOpacityD.setShowTickLabels(true);
			sliderFillOpacityD.setShowTickMarks(true);
			sliderFillOpacityD.setMajorTickUnit(0.2);
			sliderFillOpacityD.setMinorTickCount(1);
			sliderFillOpacityD.setBlockIncrement(0.1);
			gridPaneD.add(sliderFillOpacityD, 2, 2);
			// Ellipse stroke style
			Text strokeStyleTextD = new Text("Stroke style: ");
			strokeStyleTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(strokeStyleTextD, 1, 3);
			strokeStyleD = new ComboBox<String>();
			strokeStyleD.getItems().addAll(strokeStyleList);
			strokeStyleD.setPrefWidth(150);
			strokeStyleD.setMaxWidth(150);
			strokeStyleD.setId("strokeStyle");
			strokeStyleD.setValue("Line");
			gridPaneD.add(strokeStyleD, 2, 3);
 			// Circle stroke width slider
			Text strokeWidthTextD = new Text("Stroke width: ");
			strokeWidthTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(strokeWidthTextD, 1, 4);
			sliderStrokeWidthD = new Slider();
			sliderStrokeWidthD.setMin(0);
			sliderStrokeWidthD.setMax(10);
			sliderStrokeWidthD.setValue(2);
			sliderStrokeWidthD.setShowTickLabels(true);
			sliderStrokeWidthD.setShowTickMarks(true);
			sliderStrokeWidthD.setMajorTickUnit(2);
			sliderStrokeWidthD.setMinorTickCount(1);
			sliderStrokeWidthD.setBlockIncrement(1);
			gridPaneD.add(sliderStrokeWidthD, 2, 4); 
			// Circle stroke color picker
			Text strokeColorTextD = new Text("Stroke color: ");
			strokeColorTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(strokeColorTextD, 1, 5);
			strokeColorPickerD = new ColorPicker(Color.rgb(0,0,0,1.0));
			strokeColorPickerD.setId("strokeColorPicker");
			gridPaneD.add(strokeColorPickerD, 2, 5);
 			// Circle size slider
			Text sizeCircleTextD = new Text("Size %: ");
			sizeCircleTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(sizeCircleTextD, 1, 6);
			sliderSizeCircleD = new Slider();
			sliderSizeCircleD.setMin(0);
			sliderSizeCircleD.setMax(100);
			sliderSizeCircleD.setValue(0);
			sliderSizeCircleD.setShowTickLabels(true);
			sliderSizeCircleD.setShowTickMarks(true);
			sliderSizeCircleD.setMajorTickUnit(20);
			sliderSizeCircleD.setMinorTickCount(3);
			sliderSizeCircleD.setBlockIncrement(1);
			gridPaneD.add(sliderSizeCircleD, 2, 6);
			// Ellipse z-order
			Text zOrderTextD = new Text("Z-order: ");
			zOrderTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(zOrderTextD, 1, 7);
			HBox zOrderHB = new HBox();
			btnToFrontD = new Button("Bring to Front");
			btnToBackD = new Button("Send to Back");
			zOrderHB.getChildren().addAll(btnToFrontD, btnToBackD);
			gridPaneD.add(zOrderHB, 2, 7);
			Separator tabSeparator = new Separator();
			gridPaneD.add(tabSeparator, 1, 8, 2, 1);
			// Dataset header
			datasetHeaderD = new TextField();
			datasetHeaderD.setPromptText("DatasetD");
			gridPaneD.add(datasetHeaderD, 1, 9, 2, 1);
			// Dataset header Font
			Text fontHeaderTextD = new Text("Font: ");
			fontHeaderTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(fontHeaderTextD, 1, 10);
			HBox fontHB = new HBox();
			fontSizeD = new ComboBox<Integer>();
			fontSizeD.getItems().addAll(fontSizeList);
			fontSizeD.setValue(28);
			fontSizeD.setPrefWidth(80);
			fontFamilyD = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderTextD.getFont().getFamilies().size(); i++) {
				fontFamilyD.getItems().add(fontHeaderTextD.getFont().getFamilies().get(i));
			}
			fontFamilyD.setValue(defaultFont);
			fontFamilyD.setPrefWidth(150);
			fontFamilyD.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSizeD, fontFamilyD);
			gridPaneD.add(fontHB, 2, 10);
			// Font Family Style
			Text fontStyleHeaderTextD = new Text("Font style: ");
			fontStyleHeaderTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(fontStyleHeaderTextD, 1, 11);
			HBox fontStyleHB = new HBox();
			fontWeightD = new ToggleButton();
			fontWeightD.setId("fontWeight");
			fontWeightD.setTooltip(new Tooltip("Bold"));
			fontWeightD.getStyleClass().add("fontStyleTB");
			fontWeightD.setGraphic(new ImageView(imB));
			fontPostureD = new ToggleButton();
			fontPostureD.setId("fontPosture");
			fontPostureD.setTooltip(new Tooltip("Italic"));
			fontPostureD.getStyleClass().add("fontStyleTB");
			fontPostureD.setGraphic(new ImageView(imI));
			fontUnderlineD = new ToggleButton();
			fontUnderlineD.setId("fontUnderline");
			fontUnderlineD.setTooltip(new Tooltip("Underline"));
			fontUnderlineD.getStyleClass().add("fontStyleTB");
			fontUnderlineD.setGraphic(new ImageView(imU));
			fontShadowD = new ToggleButton();
			fontShadowD.setId("fontShadow");
			fontShadowD.setTooltip(new Tooltip("Shadow"));
			fontShadowD.getStyleClass().add("fontStyleTB");
			fontShadowD.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeightD, fontPostureD, fontUnderlineD, fontShadowD);
			gridPaneD.add(fontStyleHB, 2, 11);
			// Title color picker
			Text titleTextD = new Text("Title color:");
			titleTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(titleTextD, 1, 12);
			titleColorPickerD = new ColorPicker(Color.rgb(0,0,0,1));
			titleColorPickerD.setId("titleColorPicker");
			gridPaneD.add(titleColorPickerD, 2, 12);
 			// Title angle slider
			Text titleAngleTextD = new Text("Title angle: ");
			titleAngleTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(titleAngleTextD, 1, 13);
			sliderTitleAngleD = new Slider();
			sliderTitleAngleD.setMin(0);
			sliderTitleAngleD.setMax(360);
			sliderTitleAngleD.setValue(0);
			sliderTitleAngleD.setShowTickLabels(true);
			sliderTitleAngleD.setShowTickMarks(true);
			sliderTitleAngleD.setMajorTickUnit(90);
			sliderTitleAngleD.setMinorTickCount(1);
			sliderTitleAngleD.setBlockIncrement(1);
			gridPaneD.add(sliderTitleAngleD, 2, 13);
			Separator tabSeparator2 = new Separator();
			gridPaneD.add(tabSeparator2, 1, 14, 2, 1);
			Text totalTextD = new Text("Total counts:");
			totalTextD.getStyleClass().add("tabPaneText");
			gridPaneD.add(totalTextD, 1, 15);
			totalCountD = new Text("0");
			totalCountD.getStyleClass().add("tabPaneText");
			gridPaneD.add(totalCountD, 2, 15);
		tabD.setContent(gridPaneD);
		tabPane.getTabs().add(tabD);
	}

	public void addTabE() {
		tabE = new Tab();
		tabE.setClosable(false);
		tabE.setText("EllipseE");
		GridPane gridPaneE = new GridPane();
			gridPaneE.setHgap(10);
			gridPaneE.setVgap(10);
			// Circle fill color picker
			Text fillColorTextE = new Text("Fill color: ");
			fillColorTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(fillColorTextE, 1, 1);
			fillColorPickerE = new ColorPicker(Color.rgb(0,0,255,0.4));
			fillColorPickerE.setId("fillColorPicker");
			gridPaneE.add(fillColorPickerE, 2, 1);
 			// Ellipse fill opacity
			Text fillOpacityTextE = new Text("Fill opacity: ");
			fillOpacityTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(fillOpacityTextE, 1, 2);
			sliderFillOpacityE = new Slider();
			sliderFillOpacityE.setMin(0);
			sliderFillOpacityE.setMax(1);
			sliderFillOpacityE.setValue(0.5);
			sliderFillOpacityE.setShowTickLabels(true);
			sliderFillOpacityE.setShowTickMarks(true);
			sliderFillOpacityE.setMajorTickUnit(0.2);
			sliderFillOpacityE.setMinorTickCount(1);
			sliderFillOpacityE.setBlockIncrement(0.1);
			gridPaneE.add(sliderFillOpacityE, 2, 2);
			// Ellipse stroke style
			Text strokeStyleTextE = new Text("Stroke style: ");
			strokeStyleTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(strokeStyleTextE, 1, 3);
			strokeStyleE = new ComboBox<String>();
			strokeStyleE.getItems().addAll(strokeStyleList);
			strokeStyleE.setPrefWidth(150);
			strokeStyleE.setMaxWidth(150);
			strokeStyleE.setId("strokeStyle");
			strokeStyleE.setValue("Line");
			gridPaneE.add(strokeStyleE, 2, 3);
 			// Circle stroke width slider
			Text strokeWidthTextE = new Text("Stroke width: ");
			strokeWidthTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(strokeWidthTextE, 1, 4);
			sliderStrokeWidthE = new Slider();
			sliderStrokeWidthE.setMin(0);
			sliderStrokeWidthE.setMax(10);
			sliderStrokeWidthE.setValue(2);
			sliderStrokeWidthE.setShowTickLabels(true);
			sliderStrokeWidthE.setShowTickMarks(true);
			sliderStrokeWidthE.setMajorTickUnit(2);
			sliderStrokeWidthE.setMinorTickCount(1);
			sliderStrokeWidthE.setBlockIncrement(1);
			gridPaneE.add(sliderStrokeWidthE, 2, 4); 
			// Circle stroke color picker
			Text strokeColorTextE = new Text("Stroke color: ");
			strokeColorTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(strokeColorTextE, 1, 5);
			strokeColorPickerE = new ColorPicker(Color.rgb(0,0,0,1.0));
			strokeColorPickerE.setId("strokeColorPicker");
			gridPaneE.add(strokeColorPickerE, 2, 5);
 			// Circle size slider
			Text sizeCircleTextE = new Text("Size %: ");
			sizeCircleTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(sizeCircleTextE, 1, 6);
			sliderSizeCircleE = new Slider();
			sliderSizeCircleE.setMin(0);
			sliderSizeCircleE.setMax(100);
			sliderSizeCircleE.setValue(0);
			sliderSizeCircleE.setShowTickLabels(true);
			sliderSizeCircleE.setShowTickMarks(true);
			sliderSizeCircleE.setMajorTickUnit(20);
			sliderSizeCircleE.setMinorTickCount(3);
			sliderSizeCircleE.setBlockIncrement(1);
			sliderSizeCircleE.setDisable(true);
			gridPaneE.add(sliderSizeCircleE, 2, 6);
			// Ellipse z-order
			Text zOrderTextE = new Text("Z-order: ");
			zOrderTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(zOrderTextE, 1, 7);
			HBox zOrderHB = new HBox();
			btnToFrontE = new Button("Bring to Front");
			btnToBackE = new Button("Send to Back");
			zOrderHB.getChildren().addAll(btnToFrontE, btnToBackE);
			gridPaneE.add(zOrderHB, 2, 7);
			Separator tabSeparator = new Separator();
			gridPaneE.add(tabSeparator, 1, 8, 2, 1);
			// Dataset header
			datasetHeaderE = new TextField();
			datasetHeaderE.setPromptText("DatasetE");
			gridPaneE.add(datasetHeaderE, 1, 9, 2, 1);
			// Dataset header Font
			Text fontHeaderTextE = new Text("Font: ");
			fontHeaderTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(fontHeaderTextE, 1, 10);
			HBox fontHB = new HBox();
			fontSizeE = new ComboBox<Integer>();
			fontSizeE.getItems().addAll(fontSizeList);
			fontSizeE.setValue(28);
			fontSizeE.setPrefWidth(80);
			fontFamilyE = new ComboBox<String>();
			for (int i=0; i < (Integer)fontHeaderTextE.getFont().getFamilies().size(); i++) {
				fontFamilyE.getItems().add(fontHeaderTextE.getFont().getFamilies().get(i));
			}
			fontFamilyE.setValue(defaultFont);
			fontFamilyE.setPrefWidth(150);
			fontFamilyE.setMaxWidth(150);
			fontHB.getChildren().addAll(fontSizeE, fontFamilyE);
			gridPaneE.add(fontHB, 2, 10);
			// Font Family Style
			Text fontStyleHeaderTextE = new Text("Font style: ");
			fontStyleHeaderTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(fontStyleHeaderTextE, 1, 11);
			HBox fontStyleHB = new HBox();
			fontWeightE = new ToggleButton();
			fontWeightE.setId("fontWeight");
			fontWeightE.setTooltip(new Tooltip("Bold"));
			fontWeightE.getStyleClass().add("fontStyleTB");
			fontWeightE.setGraphic(new ImageView(imB));
			fontPostureE = new ToggleButton();
			fontPostureE.setId("fontPosture");
			fontPostureE.setTooltip(new Tooltip("Italic"));
			fontPostureE.getStyleClass().add("fontStyleTB");
			fontPostureE.setGraphic(new ImageView(imI));
			fontUnderlineE = new ToggleButton();
			fontUnderlineE.setId("fontUnderline");
			fontUnderlineE.setTooltip(new Tooltip("Underline"));
			fontUnderlineE.getStyleClass().add("fontStyleTB");
			fontUnderlineE.setGraphic(new ImageView(imU));
			fontShadowE = new ToggleButton();
			fontShadowE.setId("fontShadow");
			fontShadowE.setTooltip(new Tooltip("Shadow"));
			fontShadowE.getStyleClass().add("fontStyleTB");
			fontShadowE.setGraphic(new ImageView(imS));
			fontStyleHB.getChildren().addAll(fontWeightE, fontPostureE, fontUnderlineE, fontShadowE);
			gridPaneE.add(fontStyleHB, 2, 11);
			// Title color picker
			Text titleTextE = new Text("Title color:");
			titleTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(titleTextE, 1, 12);
			titleColorPickerE = new ColorPicker(Color.rgb(0,0,0,1));
			titleColorPickerE.setId("titleColorPicker");
			gridPaneE.add(titleColorPickerE, 2, 12);
 			// Title angle slider
			Text titleAngleTextE = new Text("Title angle: ");
			titleAngleTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(titleAngleTextE, 1, 13);
			sliderTitleAngleE = new Slider();
			sliderTitleAngleE.setMin(0);
			sliderTitleAngleE.setMax(360);
			sliderTitleAngleE.setValue(0);
			sliderTitleAngleE.setShowTickLabels(true);
			sliderTitleAngleE.setShowTickMarks(true);
			sliderTitleAngleE.setMajorTickUnit(90);
			sliderTitleAngleE.setMinorTickCount(1);
			sliderTitleAngleE.setBlockIncrement(1);
			gridPaneE.add(sliderTitleAngleE, 2, 13);
			Separator tabSeparator2 = new Separator();
			gridPaneE.add(tabSeparator2, 1, 14, 2, 1);
			Text totalTextE = new Text("Total counts:");
			totalTextE.getStyleClass().add("tabPaneText");
			gridPaneE.add(totalTextE, 1, 15);
			totalCountE = new Text("0");
			totalCountE.getStyleClass().add("tabPaneText");
			gridPaneE.add(totalCountE, 2, 15);
		tabE.setContent(gridPaneE);
		tabPane.getTabs().add(tabE);
	}
}





