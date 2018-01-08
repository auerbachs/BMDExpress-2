package com.sciome.bmdexpress2.mvp.model;

import java.awt.Color;
import java.util.Set;

public interface IMarkable
{

	public Set<String> getMarkableKeys();

	public String getMarkableLabel();

	public Color getMarkableColor();

}
