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

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.text.Text;
import javafx.scene.shape.Ellipse;

public class MoveObjectEvent {

	private class Delta { double x, y; }

	public void makeTextMovable(Text movableText) {
		final Delta dragDelta = new Delta();
		movableText.setOnMousePressed(mouseEvent->{
			dragDelta.x = movableText.getLayoutX() - mouseEvent.getSceneX();
			dragDelta.y = movableText.getLayoutY() - mouseEvent.getSceneY();
			movableText.setCursor(Cursor.MOVE);
        	});
		movableText.setOnMouseReleased(mouseEvent->{
			movableText.setCursor(Cursor.HAND);
        	});
		movableText.setOnMouseDragged(mouseEvent->{
			movableText.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
			movableText.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
        	});
		movableText.setOnMouseEntered(mouseEvent->{
			movableText.setCursor(Cursor.HAND);
        	});
	}

	public void makeEllipseMovable(Ellipse movableEllipse) {
		final Delta dragDelta = new Delta();
		movableEllipse.setOnMousePressed(mouseEvent->{
			dragDelta.x = movableEllipse.getLayoutX() - mouseEvent.getSceneX();
			dragDelta.y = movableEllipse.getLayoutY() - mouseEvent.getSceneY();
			movableEllipse.setCursor(Cursor.MOVE);
        	});
		movableEllipse.setOnMouseReleased(mouseEvent->{
			movableEllipse.setCursor(Cursor.HAND);
        	});
		movableEllipse.setOnMouseDragged(mouseEvent->{
			movableEllipse.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
			movableEllipse.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
        	});
		movableEllipse.setOnMouseEntered(mouseEvent->{
			movableEllipse.setCursor(Cursor.HAND);
        	});
	}
}
