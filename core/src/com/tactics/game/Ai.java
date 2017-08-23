package com.tactics.game;

import java.util.ArrayList;
import java.util.LinkedList;

import com.tactics.map.AStar;
import com.tactics.map.Node;
import com.tactics.map.PathNode;
import com.tactics.unit.Unit;

/**
 * Class to hold all AI logic
 */
public class Ai {

	SkirmishView game;
	AStar aStar;
	
	public Ai(SkirmishView game) {
		this.game = game;
		this.aStar = game.aStar;
	}
	

	
	/**
	 * Handle all AI stuff here for now
	 * @param cpuUnit
	 */
	protected void processComputerTurn(Unit cpuUnit) {
		//see if there's anybody that can be shot at
		ArrayList<Unit> enemyUnitsToAttack = getAttackableUnits(cpuUnit);
		//find the best unit to attack of available units
		if(enemyUnitsToAttack.size() > 0) {
			Unit defender = enemyUnitsToAttack.get(0);
			game.attackUnit(cpuUnit, defender);
		//if nobody was found to attack, then find somewhere to move to
		} else {
			//see what the shortest travel distance is of all adjacent nodes according to closest enemy unit
			ArrayList<Unit> enemyUnits = getEnemyUnits(cpuUnit);
			Float finalHCost = null;
			Unit finalUnit = null;
			for(Unit curEnemy : enemyUnits) {
				Float curHcost = aStar.calculateHCost(cpuUnit.getNode(), curEnemy.getNode());
				if(finalHCost == null || (finalHCost != null && curHcost <= finalHCost)) {
					finalHCost = curHcost;
					finalUnit = curEnemy;
				}
			}
			//given the closest unit, find the best path to that unit
			LinkedList<PathNode> enemyAdjNodes = aStar.getAdjacentNodes(finalUnit);
			Integer pathSize = null;
			ArrayList<PathNode> finalPath = null;
			for (Node curNode : enemyAdjNodes) {
				ArrayList<PathNode> curPath = aStar.findPath(cpuUnit.getNode(), curNode);
				if(pathSize == null || (pathSize != null && curPath.size() <= pathSize)) {
					pathSize = curPath.size();
					//move to the furthest point within move distance on this path
					finalPath = curPath;
				}
			}
			//get subset of the resulting path based on the unit's move distance
			if(finalPath.size() > cpuUnit.getActionPoints()) {
				finalPath = new ArrayList<PathNode>(finalPath.subList(0, cpuUnit.getActionPoints()));
			}
			game.setUnitPath(cpuUnit, finalPath);
		}
	}
	
	/**
	 * Method to get a list of all enemy units given a single unit
	 * @param curUnit
	 * @return list of enemy units
	 */
	public ArrayList<Unit> getEnemyUnits(Unit curUnit) {
		ArrayList<Unit> enemyUnits = new ArrayList<Unit>();
		for(Unit unit : game.units) {
			if(unit.getSide() != curUnit.getSide()) enemyUnits.add(unit);
		}
		return enemyUnits;
	}
	
	/**
	 * Method to get a list of all available units that can be attacked
	 * @param attacker
	 */
	private ArrayList<Unit> getAttackableUnits(Unit attacker) {
		ArrayList<Unit> enemyUnits = getEnemyUnits(attacker);
		ArrayList<Unit> enemyUnitsToAttack = new ArrayList<Unit>();
		for(Unit enemyUnit : enemyUnits) {
			if(enemyUnit.getSide() != attacker.getSide()) {
				Node enemyNode = aStar.nodes[game.getXNode(enemyUnit.getCenterXOffset())][game.getYNode(enemyUnit.getCenterYOffset())];
				ArrayList<Node> LOSNodes = aStar.getLineOfSightNodes((int) attacker.getCenterXOffset(), (int) attacker.getCenterYOffset(), (int) enemyUnit.getCenterXOffset(), (int) enemyUnit.getCenterYOffset());  
				game.setLOSNodes(LOSNodes);
				//attack enemy unit if you can see one
				if(LOSNodes.size() > 0 && LOSNodes.get(LOSNodes.size()-1).equals(enemyNode)) {
					enemyUnitsToAttack.add(enemyUnit);
				}
			}
		}
		return enemyUnitsToAttack;
	}
	
}
