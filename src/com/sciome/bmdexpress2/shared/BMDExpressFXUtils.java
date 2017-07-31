package com.sciome.bmdexpress2.shared;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BMDExpressFXUtils
{

	private static BMDExpressFXUtils instance = null;

	protected BMDExpressFXUtils()
	{

	}

	public static BMDExpressFXUtils getInstance()
	{
		if (instance == null)
		{
			instance = new BMDExpressFXUtils();
		}
		return instance;
	}

	public Stage generateStage(String title)
	{

		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setTitle(title);
		stage.getIcons().add(new Image("icon.png"));
		return stage;
	}

}
