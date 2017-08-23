package com.tactics.unit.action;

/**
 * Generic class to hold attacks and other stuff
 */
public class Action {

	String name;
	
	int cost;
	int baseDamage;
	int baseAccuracy;
	
	public Action(String name, int cost, int baseDamage, int baseAccuracy) {
		this.name = name;
		this.cost = cost;
		this.baseDamage = baseDamage;
		this.baseAccuracy = baseAccuracy;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public void setBaseDamage(int baseDamage) {
		this.baseDamage = baseDamage;
	}
	
	public void setBaseAccuracy(int baseAccuracy) {
		this.baseAccuracy = baseAccuracy;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public int getBaseDamage() {
		return this.baseDamage;
	}
	
	public int getBaseAccuracy() {
		return this.baseAccuracy;
	}
	
	public String getName() {
		return this.name;
	}
}
