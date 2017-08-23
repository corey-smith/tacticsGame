package com.tactics.map;

import com.badlogic.gdx.maps.MapProperties;

/**
 *	An object that can't be walked through by the player
 */
public class CollidableObject extends MapObject{
	
	public CollidableObject(float x, float y, float width, float height, MapProperties properties) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.properties = properties;
	}
}
