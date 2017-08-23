package com.tactics.map;

import com.tactics.unit.Unit;

/**
 * I'm not really sure what a node is supposed to be, but this is just going to
 * hold x/y coordinates to use in pathfinding
 */
public class Node {

	int x;
	int y;
	public boolean collidable = false;
	public boolean occupied = false;
	Unit occupier = null;
	//generic ID to use in comparing other pathnodes that are the same location but different objects
	String id;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		setID();
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public boolean getOccupied() {
		return this.occupied;
	}
	
	public Unit getOccupier() {
		return this.occupier;
	}
	
	public boolean getCollidable() {
		return (this.collidable || this.occupied);
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public void setOccupier(Unit occupier) {
		this.occupier = occupier;
	}
	
	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}
	
	public void setID() {
		this.id = "X: " + this.x + "& Y: " + this.y;
	}
	
	public String toString() {
		return this.id;
	}
}
