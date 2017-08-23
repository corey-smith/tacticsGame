package com.tactics.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import com.tactics.unit.Unit;

/**
 * 
 * A* path finder
 */
public class AStar {

	//2D array or x/y position of tiles - should be height of map
	public Node nodes[][];
	ArrayList<CollidableObject> collidableObjects;
	int tileWidth;
	int tileHeight;
	
	/**
	 * Should only need to declare this once in the main class to initialize these params
	 * @param tiles
	 * @param tileWidth
	 * @param tileHeight
	 */
	public AStar(ArrayList<CollidableObject> collidableObjects, int[][] nodes, int tileWidth, int tileHeight) {
		this.collidableObjects = collidableObjects;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.nodes = new Node[nodes.length][nodes[0].length];
		initializeNodes();
	}
	
	/**
	 * build out node array and clear any previous values
	 */
	public void initializeNodes() {
		//build out nodes array
		for(int x = 0; x < nodes.length; x++) {
			for(int y = 0; y < nodes[0].length; y++) {
				this.nodes[x][y] = new Node(x, y);
			}
		}
		setCollidableNodes();
	}
	
	/**
	 * loop through all of the collidable objects and find their corresponding node and set it to collidable
	 */
	public void setCollidableNodes() {
		for(CollidableObject collObj : collidableObjects) {
			float objLeft = collObj.x;
			float objBottom = collObj.y;
			float objRight = objLeft + collObj.width;
			float objTop = objBottom + collObj.height;
			int firstX = (int) Math.floor(objLeft / tileWidth);
			int lastX = (int) Math.ceil(objRight / tileWidth);
			int firstY = (int) Math.floor(objBottom / tileHeight);
			int lastY = (int) Math.ceil(objTop / tileHeight);
			for(int i = firstX; i < lastX; i++) {
				for(int j = firstY; j < lastY; j++) {
					nodes[i][j].setCollidable(true);
				}
			}
		}
	}
	
	public ArrayList<PathNode> findPath(Node startingNode, Node endingNode) {
		return this.findPath(startingNode.x,  startingNode.y, endingNode.x, endingNode.y);
	}
	
	/**
	 * Actual logic to find the path
	 * @param startingX - map in relation to map
	 * @param startingY - map in relation to map
	 * @param endingX   - map in relation to map
	 * @param endingY   - map in relation to map
	 * @return
	 */
	public ArrayList<PathNode> findPath(int startingX, int startingY, int endingX, int endingY) {
		LinkedList<PathNode> openList = new LinkedList<PathNode>();
		LinkedList<PathNode> closedList = new LinkedList<PathNode>();
		PathNode[][] pathNodes = getPathNodes();
		PathNode startingNode =  pathNodes[startingX][startingY];
		Node endingNode = nodes[endingX][endingY];
		openList.add(startingNode);
		startingNode.setGCost(0f);
		boolean done = false;
		PathNode curNode = startingNode;
		while(!done) {
			curNode = lowestFCostOpenNode(openList);
			closedList.add(curNode);
			openList.remove(curNode);
			
			//check to see if the current node is the ending node
			if ((curNode.getX() == endingNode.getX()) && (curNode.getY() == endingNode.getY())) {
                return getPath(startingNode, curNode);
            }
			
			//find all adjacent nodes and evaluate
			LinkedList<PathNode> adjacentNodes = getAdjacentNodes(curNode, pathNodes);
			for(PathNode curAdj : adjacentNodes) {
				if (!openList.contains(curAdj) && !closedList.contains(curAdj)) {
					curAdj.setPrevNode(curNode);
					//set h costs of this node (estimated costs to goal)
					curAdj.setHCost(calculateHCost(curAdj, endingNode));
					//set g costs of this node (costs from start to this node), just add one to the cost of the current node
					curAdj.setGCost(curNode.getGCost() + 1);
					curAdj.setFCost(curAdj.getHCost() + curAdj.getGCost());
                    openList.add(curAdj);
                } else {
                	//costs from current node are cheaper than previous costs
                    if (curAdj.getGCost() > curNode.getGCost() + 1) {
                    	closedList.remove(curAdj);
                    	//set current node as previous for this node
                    	curAdj.setPrevNode(curNode);
                    	//set g costs of this node (costs from start to this node)
    					curAdj.setHCost(calculateHCost(curAdj, endingNode));
                    	curAdj.setGCost(curNode.getGCost() + 1); 
    					curAdj.setFCost(curAdj.getHCost() + curAdj.getGCost());
                    	if(!openList.contains(curAdj)) openList.add(curAdj);
                    }
                }
			}
			/*
			if(curNode.getPrevNode() != null && curNode.getPrevNode().getPrevNode() != null && curNode.getPrevNode().getPrevNode().equals(curNode)) {
				System.out.println("curNodeX: " + curNode.getX() + ", curNodeY: " + curNode.getY());
				System.out.println("prevNodeX: " + curNode.getPrevNode().getX() + ", prevNodeY: " + curNode.getPrevNode().getY());
				System.out.println("");
			}
			*/
			//no path exists, return empty list
			if(openList.isEmpty()) {
				return new ArrayList<PathNode>();
			}
		}
		//this isn't actually reachable
		return null;
	}
	
	/**
	 * Get a local copy of all nodes to use in determining path
	 * @return - basically a copy of nodes[][]
	 */
	public PathNode[][] getPathNodes() {
		PathNode[][] returnArray = new PathNode[nodes.length][nodes[0].length];
		for(int x = 0; x < nodes.length; x++) {
			for(int y = 0; y < nodes[0].length; y++) {
				returnArray[x][y] = new PathNode(nodes[x][y]);
			}
		}
		return returnArray;
	}
	
	public PathNode lowestFCostOpenNode(LinkedList<PathNode> openList) {
		PathNode returnNode = null;
		for(PathNode curNode : openList) {
			if(returnNode == null || (curNode.getFCost() != null && returnNode.getFCost() != null && curNode.getFCost() < returnNode.getFCost())) {
				returnNode = curNode;
			}
		}
		return returnNode;
	}
	
	/**
	 * Get all of the possible adjacent (not collidable) nodes given a unit
	 * @return - list of adjacent nodes
	 */
	public LinkedList<PathNode> getAdjacentNodes(Unit curUnit) {
		return this.getAdjacentNodes(new PathNode(curUnit.getNode()));
	}
	
	/**
	 * Get all of the possible adjacent (not collidable) nodes given a node
	 * @return - list of adjacent nodes
	 */
	public LinkedList<PathNode> getAdjacentNodes(PathNode currentNode) {
		return this.getAdjacentNodes(currentNode, this.getPathNodes());
	}
	
	/**
	 * Get all of the possible adjacent (not collidable) nodes given a node and an original or copy of the nodes in the map
	 * @return - list of adjacent nodes
	 */
	public LinkedList<PathNode> getAdjacentNodes(Node currentNode, PathNode[][] pathNodes) {
		LinkedList<PathNode> returnList = new LinkedList<PathNode>();
		//define nodes, validate they're there
		int curX = currentNode.x;
		int curY = currentNode.y;
		PathNode northNode = (PathNode) (nodeExists(curX, curY + 1) ? pathNodes[currentNode.x][currentNode.y+1] : null);
		PathNode southNode = (PathNode) (nodeExists(curX, curY - 1) ? pathNodes[currentNode.x][currentNode.y-1] : null);
		PathNode eastNode = (PathNode) (nodeExists(curX + 1, curY) ? pathNodes[currentNode.x+1][currentNode.y] : null);
		PathNode westNode = (PathNode) (nodeExists(curX - 1, curY) ? pathNodes[currentNode.x-1][currentNode.y] : null);
		//add non-collidable to list
		//north
		if(northNode != null && !northNode.getCollidable()) {
			returnList.add(northNode);
		}
		//south
		if(southNode != null && !southNode.getCollidable()) {
			returnList.add(southNode);
		}
		//east
		if(eastNode != null && !eastNode.getCollidable()) {
			returnList.add(eastNode);
		}
		//west
		if(westNode != null && !westNode.getCollidable()) {
			returnList.add(westNode);
		}
		//not allowing diagonal movement right now
		//northeast
		/*
		PathNode northEastNode = (northNode != null && eastNode != null) ? pathNodes[currentNode.x+1][currentNode.y+1] : null;
		PathNode northWestNode = (northNode != null && westNode != null) ? pathNodes[currentNode.x-1][currentNode.y+1] : null;
		PathNode southEastNode = (southNode != null && eastNode != null) ? pathNodes[currentNode.x + 1][currentNode.y-1] : null;
		PathNode southWestNode = (southNode != null && westNode != null) ? pathNodes[currentNode.x-1][currentNode.y-1] : null;
		if(northEastNode != null && !northEastNode.getCollidable() && !northNode.getCollidable() && !eastNode.getCollidable()) {
			returnList.add(northEastNode);
		}
		//nortwest
		if(northWestNode != null && !northWestNode.getCollidable() && !northNode.getCollidable() && !westNode.getCollidable()) {
			returnList.add(northWestNode);
		}
		//southeast
		if(southEastNode != null && !southEastNode.getCollidable() && !southNode.getCollidable() && !eastNode.getCollidable()) {
			returnList.add(southEastNode);
		}
		//southwest
		if(southWestNode != null && !southWestNode.getCollidable() && !southNode.getCollidable() && !westNode.getCollidable()) {
			returnList.add(southWestNode);
		}
		*/
		return returnList;
	}
	
	public boolean nodeExists(int x, int y) {
		if(x >= 0 && y >= 0 && x < this.nodes.length && y < this.nodes[0].length) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Calcualate H cost from a given node to the ending node
	 * H cost is basically the cost of from here to the goal
	 * @param curNode
	 * @param endingNode
	 * @return
	 */
	public float calculateHCost(Node curNode, Node endingNode) {
		/*
		int dx = endingNode.getX() - curNode.getX();
	    int dy = endingNode.getY() - curNode.getY();
	    return (float) Math.sqrt((dx*dx)+(dy*dy));
	    */
	    //less costly and simpler method
	    int dx = Math.abs(endingNode.getX() - curNode.getX());
	    int dy = Math.abs(endingNode.getY() - curNode.getY());
	    return (float) dx+dy;
	     
	}
	
	/**
	 * Build out arraylist with the resulting path
	 * @return - ordered list of nodes in the path
	 */
	public ArrayList<PathNode> getPath(PathNode startingNode, PathNode endingNode) {
		ArrayList<PathNode> returnPath = new ArrayList<PathNode>();
		PathNode curNode = endingNode;
		//work back through previous nodes and add them to the path
		while(curNode.getPrevNode() != null) {
			returnPath.add(curNode);
			curNode = curNode.getPrevNode();
		}
		//reverse order of path
		Collections.reverse(returnPath);
		return returnPath;
	}
	
	/**
	 * Get a list of all of the nodes a specific unit can move to in one turn
	 * @param curUnit - the unit who is going to move somewhere
	 * @return - arraylist of nodes that the unit can move to
	 */
	public ArrayList<PathNode> getMovableNodes(int xNode, int yNode, int distance) {
		ArrayList<PathNode> movableNodes = new ArrayList<PathNode>();
		ArrayList<String> movableIDs = new ArrayList<String>();
		//loop through x direction, y direction
		for(int x = xNode - distance; x <= xNode + distance; x++) {
			for(int y = yNode - distance; y <= yNode + distance; y++) {
				if(x >= 0 && x < nodes.length && y >= 0 && y < nodes[0].length) {
					int absDistance = Math.abs(xNode - x) + Math.abs(yNode - y);
					if(absDistance <= distance && !movableIDs.contains(getNodeIDFromLoc(x,y))) {
						ArrayList<PathNode> curPath = findPath(xNode, yNode, x, y);
						if(curPath.size() > 0 && curPath.size() <= distance) {
							//add all of the nodes in the path
							for(PathNode curPathNode : curPath) {
								if(!movableIDs.contains(curPathNode.getID())) {
									movableNodes.add(curPathNode);
									movableIDs.add(curPathNode.getID());
								}
							}
						}
					}
				}
			}
		}
		return movableNodes;
	}
	
	/**
	 * This is a way to see what a node's ID would be given an X and Y location
	 * @return String of ID
	 */
	public String getNodeIDFromLoc(int x, int y) {
		PathNode tempNode = new PathNode(x,y);
		return tempNode.getID();
	}
	
	/**
	 * This is something I copied and pasted from the internet
	 * it finds all of the tiles being intersected by the line of sight line
	 * @param startingX - point value
	 * @param startingY - point value
	 * @param endingX	- point value
	 * @param endingY	- point value
	 */
	public ArrayList<Node> getLineOfSightNodes(int startingX, int startingY, int endingX, int endingY) {
		ArrayList<Node> lineOfSightNodes = new ArrayList<Node>();
		int dx = (int) Math.abs(startingX - endingX);
	    int dy = (int) Math.abs(startingY - endingY);
	    int x = (int) startingX;
	    int y = (int) startingY;
	    int n = 1 + dx + dy;
	    int x_inc = (endingX > startingX) ? 1 : -1;
	    int y_inc = (endingY > startingY) ? 1 : -1;
	    int error = dx - dy;
	    dx *= 2;
	    dy *= 2;
	    
	    boolean sightBlocked = false;

	    for (; n > 0; --n) {
	    	int xNode = (int) Math.floor(x/this.tileWidth);
	    	int yNode = (int) Math.floor(y/this.tileHeight);
	    	if(xNode >= 0 && yNode >= 0 && xNode < this.nodes.length && yNode < this.nodes[0].length) {
		    	Node startingNode = this.nodes[(int) Math.floor(startingX/this.tileWidth)][(int) Math.floor(startingY/this.tileHeight)];
	    		Node curNode = this.nodes[(int) Math.floor(x/this.tileWidth)][(int) Math.floor(y/this.tileHeight)];
		    	if(!sightBlocked) {
		    		//if the current node isn't the starting node and it's collidable or occupied, sight is now blocked
		    		if(!curNode.equals(startingNode) && curNode.getCollidable()) {
		    			sightBlocked = true;
		    		}
		    		lineOfSightNodes.add(curNode);
			        //visit(x, y);
			        if (error > 0) {
			            x += x_inc;
			            error -= dy;
			        } else {
			            y += y_inc;
			            error += dx;
			        }
		    	} else {
		    		return lineOfSightNodes;
		    	}
	    	}
	    }
	    return lineOfSightNodes;
	}
}
