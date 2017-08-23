package com.tactics.map;

import com.badlogic.gdx.maps.MapProperties;
import com.tactics.error.TileLoadingException;

/**
 *	An object that links the current map to another map through collisions
 */
public class LinkableObject extends MapObject {
	
	//name of the map that this links to
	String toMap;
	//name of current map
	String fromMap;
	//name of link this connects to on other map - should correspond with the 'fromName' property
	String toName;
	//the name of this link - should correspond with the 'toName' property
	String fromName;

	public LinkableObject(float x, float y, float width, float height, MapProperties properties) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.properties = properties;
		try {
			if(properties.get("toMap") == null || properties.get("fromMap") == null || properties.get("toName") == null || properties.get("fromName") == null) {
				throw new TileLoadingException("ERROR LOADING LINKABLE TILE");
			} else {
				this.toMap = properties.get("toMap").toString();
				this.fromMap = properties.get("fromMap").toString();
				this.toName = properties.get("toName").toString();
				this.fromName = properties.get("fromName").toString();
			}
		} catch(TileLoadingException e) {
			e.printStackTrace();
		}
	}
	
	//the string value of the map that this link points to
	public String getToMap() {
		return this.toMap;
	}
	
	//the string value of the map that this link is in
	public String getFromMap() {
		return this.fromMap;
	}
	
	//the string value of the name of the link that this link points to within the map that this link points to
	public String getToName() {
		return this.toName;
	}
	
	//the string value of this link's name - should correspond with the 'toName' property of another link
	public String getFromName() {
		return this.fromName;
	}
}