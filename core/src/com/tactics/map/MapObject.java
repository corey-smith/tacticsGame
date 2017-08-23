package com.tactics.map;

import com.badlogic.gdx.maps.MapProperties;

public class MapObject {

	//object location
	public float x;
	public float y;
	public float width;
	public float height;
	public MapProperties properties;

	public MapObject() {
		
	}
	
	//get object x position
	public float getXPos() {
		return this.x;
	}
	
	//get object y position
	public float getYPos() {
		return this.y;
	}
	
	//get object width
	public float getWidth() {
		return this.width;
	}
	
	//get object height
	public float getHeight() {
		return this.height;
	}
	
	//get object right bound
	public float getRightBound() {
		return this.x + this.width;
	}
	
	//get object right bound
	public float getLeftBound() {
		return this.x;
	}
	
	//get object right bound
	public float getTopBound() {
		return this.y + this.height;
	}
	
	//get object right bound
	public float getBottomBound() {
		return this.y;
	}
	
	//get object's properties
	public MapProperties getProperties() {
		return this.properties;
	}
	
}
