package com.tactics.map;

public class PathNode extends Node {

	//combination of g and h costs
	Float fCost = null;
	//cost from the start
	Float gCost = null;
	//cost to the goal
	Float hCost = null;
	//previous node in path
	PathNode prevNode;
	//generic ID to use in comparing other pathnodes that are the same location but different objects
	String id;
	
	public PathNode(int x, int y) {
		super(x, y);
		setID(x,y);
	}
	
	public PathNode(Node node) {
		super(node.getX(), node.getY());
		this.collidable = node.getCollidable();
		this.occupied = node.getOccupied();
	}
	
	public Float getFCost() {
		return this.fCost;
	}
	
	public Float getGCost() {
		return this.gCost;
	}
	
	public Float getHCost() {
		return this.hCost;
	}
	
	public String getID() {
		return super.getID();
	}
	
	public PathNode getPrevNode() {
		return this.prevNode;
	}
	
	public void setFCost(Float fCost) {
		this.fCost = fCost;
	}
	
	public void setGCost(Float gCost) {
		this.gCost = gCost;
	}
	
	public void setHCost(Float hCost) {
		this.hCost = hCost;
	}
	
	public void setPrevNode(PathNode prevNode) {
		this.prevNode = prevNode;
	}
	
	public void setID(int x, int y) {
		this.id = "X:" + x + "Y:" + y;
	}

}
