package com.tactics.unit;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.tactics.unit.action.Action;

/**
 * Class to build out units and their actions
 */
public class UnitBuilder {

    int tileWidth;
    int tileHeight;

	public UnitBuilder(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Initializes units
	 */
	public ArrayList<Unit> initializeUnits() {
		ArrayList<Unit> units = new ArrayList<Unit>();
		Unit unit = initializeUnit(5, 4, Side.CPU, Type.INFANTRY);
		units.add(unit);
		Unit unit1 = initializeUnit(3, 2, Side.PLAYER, Type.INFANTRY);
		units.add(unit1);
		return units;
	}

	/**
	 * Initialize individual unit
	 */
	private Unit initializeUnit(int xNode, int yNode, Side side, Type type) {
		ArrayList<Action> actions = getActions(type);
		Unit unit = new Unit(getOffsetPosXFromNode(xNode),getOffsetPosYFromNode(yNode), side, type, actions);
		return unit;
	}
	
	/**
	 * Get actions from XML based on unit type, build out actions arraylist
	 * @param type - unit type
	 * @return - ArrayList of Actions
	 */
	public ArrayList<Action> getActions(Type type) {
		ArrayList<Action> actions = new ArrayList<Action>();
		FileHandle xmlInputFile = Gdx.files.internal("files/actions/" + type + ".xml");
		XmlReader reader = new XmlReader();
		Element root = null;
		try {
            root = reader.parse(xmlInputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//loop through actions, build out object based on attributes in XML
		Array<Element> xmlActions = root.getChildrenByName("action");
		for (Element curAction : xmlActions) {
			String name = curAction.get("name");
			int cost = Integer.parseInt(curAction.get("cost").replaceAll("[^-0-9]",""));
			int baseDamage = Integer.parseInt(curAction.get("damage").replaceAll("[^-0-9]",""));
			int baseAccuracy = Integer.parseInt(curAction.get("accuracy").replaceAll("[^-0-9]",""));
			Action action = new Action(name, cost, baseDamage, baseAccuracy);
			actions.add(action);
		}
		return actions;
	}
	
	/**
	 * Get map position from node
	 * @param nodeX - x position of node
	 * @return returnVal - offset position X
	 */
	private int getOffsetPosXFromNode(int nodeX) {
		int returnVal;
		returnVal = Math.round(nodeX * this.tileWidth);
		return returnVal;
	}
	
	/**
	 * Get map position from node
	 * @param nodeY - y position of node
	 * @return returnVal - offset position Y
	 */
	private int getOffsetPosYFromNode(int nodeY) {
		int returnVal;
		returnVal = Math.round(nodeY * this.tileHeight);
		return returnVal;
	}
}
