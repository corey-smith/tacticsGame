package com.tactics.unit;

import java.awt.Point;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tactics.map.Node;
import com.tactics.map.PathNode;
import com.tactics.unit.action.Action;

public class Unit {
	
	float offsetX;
	float offsetY;
	
	Node node;
	
	//distance a player can move in a turn in tiles
	int maxActionPoints = 5;
	int actionPoints;
	double maxHealth = 100;
	double curHealth;
	
	Action curAction;
	
	double damage = 20;
	int attackAPCost = 5;
	
	ArrayList<Action> actions;
	
	int dx;
	int dy;
	int speed = 2;
	
	//animation speed - hardcoded
	Float animSpeed = 5/15f;
	
	//textures
	TextureAtlas standing_txtr;
	//animations
	Animation standing_anim;
	//current animation of the player
	Animation curAnim;
	
	//width and height of one of the images for this unit
	int width;
	int height;
	
	//enum types for the unit
	Side side;
	Type type;
	
	//current destination for movement
	Point destination;
	//final destination
	Point finalDestination;
	ArrayList<PathNode> path = new ArrayList<PathNode>();
	
	boolean selected = false;

	public Unit(float x, float y, Side side, Type type, ArrayList<Action> actions) {
		this.offsetX = x;
		this.offsetY = y;
		this.side = side;
		this.actions = actions;
		initializeUnit();
	}
	
	public void initializeUnit() {
		//load texture atlases
		standing_txtr = new TextureAtlas(Gdx.files.internal("images/sprites/english_stand_down_pack"));
		//load animations
		standing_anim = new Animation(animSpeed, standing_txtr.getRegions());
		setCurAnim(standing_anim);
		this.width = curAnim.getKeyFrames()[0].getRegionWidth();
		this.height = curAnim.getKeyFrames()[0].getRegionHeight();
		this.curHealth = this.maxHealth;
	}
	
	//basic game loop stuff
	public void update() {
		this.offsetX += dx;
		this.offsetY += dy;
	}
	
	public void setCurAnim(Animation curAnim) {
		this.curAnim = curAnim;
	}
	
	//return current animation to draw in Core.java
	public Animation getCurAnim() {
		return curAnim;
	}
	
	//get unit x position within map
	public float getXOffset() {
		return this.offsetX;
	}
	
	//get center of unit
	public float getCenterXOffset() {
		return this.offsetX + this.width/2;
	}
	
	//get center of unit
	public float getCenterYOffset() {
		return this.offsetY + this.height/2;
	}
	
	//get unit x position within map
	public float getYOffset() {
		return this.offsetY;
	}
	
	//get image width in pixels
	public int getWidth() {
		return this.width;
	}
	
	//get image height in pixels
	public int getHeight() {
		return this.height;
	}
	
	public Point getDestination() {
		return this.destination;
	}
	
	public Point getFinalDestination() {
		return this.finalDestination;
	}
	
	public double getHealth() {
		return this.curHealth;
	}
	
	public double getHealthMax() {
		return this.maxHealth;
	}
	
	public double getDamage() {
		return this.damage;
	}
	
	public ArrayList<PathNode> getPath() {
		return this.path;
	}
	
	public int getDx() {
		return this.dx;
	}
	
	public int getDy() {
		return this.dy;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public int getActionPoints() {
		return this.actionPoints;
	}
	
	public int getMaxActionPoints() {
		return this.maxActionPoints;
	}
	
	public Side getSide() {
		return this.side;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public Action getAction() {
		return this.curAction;
	}
	
	public void setDx(int dx) {
		this.dx = dx;
	}
	
	public void setDy(int dy) {
		this.dy = dy;
	}
	
	public void setDestination(Point destination) {
		this.destination = destination;
	}
	
	public void setDestination(int x, int y) {
		this.destination = new Point(x, y);
	}
	
	public void setFinalDestination(Point finalDestination) {
		this.finalDestination = finalDestination;
	}
	
	public void setPath(ArrayList<PathNode> path) {
		this.path = path;
	}
	
	public void setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public void setHealth(int curHealth) {
		this.curHealth = curHealth;
	}
	
	public void takeDamage(double damage) {
		this.curHealth = Math.max(this.curHealth - damage, 0);
	}
	
	public int getAttackAPCost() {
		return this.attackAPCost;
	}
	
	public ArrayList<Action> getActions() {
		return this.actions;
	}
	
	public void setAction(Action curAction) {
		this.curAction = curAction;
	}
	
	public String toString() {
		return "X: " + offsetX + (this.width/2) + "& Y: " + this.offsetY + (this.height/2);
	}
}
