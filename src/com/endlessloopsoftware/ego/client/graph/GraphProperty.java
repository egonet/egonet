package com.endlessloopsoftware.ego.client.graph;

import java.awt.*;


public class GraphProperty {

//	public static final int SHAPE = 1;
//	public static final int SIZE = 2;
//	public static final int COLOR = 3;
//	public static final int ALL = 4;
	
	Color color;
	int size;
	String label;
	boolean defaultSetting = true; 
	
	public GraphProperty()
	{
		color = Color.RED;
		size = 1;
		label = "";
	}
	
	public boolean isDefaultSetting() {
		return this.defaultSetting;
	}
	public void setDefaultSetting(boolean defaultSetting) {
		this.defaultSetting = defaultSetting;
	}
	public Color getColor() {
		return this.color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
