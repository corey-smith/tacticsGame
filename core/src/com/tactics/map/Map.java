package com.tactics.map;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * A map is a collection of tiles to render
 */
public class Map {
	
	//location attributes
	public float leftBound;
	public float rightBound;
	public float topBound;
	public float bottomBound;
	
	//tile width/height in pixels
	public float tileWidth;
	public float tileHeight;
	//map with/height in tiles
	public float mapWidth;
	public float mapHeight;
	
	//actual tiled map, ideally this class would just extend tiled map,
	//but I couldn't find a good way to do that
	public TiledMap tiledMap;
	
	public Map(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		//get properties from TiledMap file
		this.mapWidth = Float.parseFloat(this.getProperties().get("width").toString());
		this.mapHeight = Float.parseFloat(this.getProperties().get("height").toString());
		this.tileWidth = Float.parseFloat(this.getProperties().get("tilewidth").toString());
		this.tileHeight = Float.parseFloat(this.getProperties().get("tileheight").toString());
		//just set left and bottom to always be zero
		this.leftBound = 0;
		this.rightBound = this.mapWidth * this.tileWidth;
		this.bottomBound = 0;
		this.topBound = this.mapHeight * this.tileHeight;
	}
	
	//return the TiledMap layers for this Map object
	public MapLayers getLayers() {
		return this.tiledMap.getLayers();
	}
	
	//return the TiledMap properties for this Map object
	public MapProperties getProperties() {
		return this.tiledMap.getProperties();
	}
	
	//map location and size attributes
	public float getLeftBound() {
		return this.leftBound;
	}
	
	public float getRightBound() {
		return this.rightBound;
	}
	
	public float getTopBound() {
		return this.topBound;
	}
	
	public float getBottomBound() {
		return this.bottomBound;
	}

	//tile width in pixels
	public float getTileWidth() {
		return this.tileWidth;
	}
	
	//tile height in pixels
	public float getTileHeight() {
		return this.tileHeight;
	}
	
	//map width in tiles
	public float getMapWidth() {
		return this.mapWidth;
	}
	
	//map height in tiles
	public float getMapHeight() {
		return this.mapHeight;
	}
}
