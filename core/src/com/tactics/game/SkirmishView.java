package com.tactics.game;

import java.awt.Point;
import java.util.ArrayList;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.game.ui.Ui;
import com.tactics.map.AStar;
import com.tactics.map.CollidableObject;
import com.tactics.map.Map;
import com.tactics.map.Node;
import com.tactics.map.PathNode;
import com.tactics.unit.Side;
import com.tactics.unit.Unit;
import com.tactics.unit.UnitBuilder;

public class SkirmishView extends GameView {
    public boolean devMode = true;
    public ArrayList<Unit> units = new ArrayList<Unit>();
    protected AStar aStar;
    private SkirmishRenderer renderer;
    private Unit selectedUnit = null;
    private ArrayList<Side> turnOrder = new ArrayList<Side>();
    private ArrayList<Unit> unitOrder = new ArrayList<Unit>();
    private Side curTurnSide;
    private ArrayList<Unit> cpuUnits = new ArrayList<Unit>();
    private ArrayList<Unit> playerUnits = new ArrayList<Unit>();
    // all collidable objects on the map
    private ArrayList<CollidableObject> collidableObjects = new ArrayList<CollidableObject>();
    // all nodes the currently selected unit can move to
    private ArrayList<PathNode> movableNodes = new ArrayList<PathNode>();
    // all nodes in the player's current line of vision
    private ArrayList<Node> lineOfSightNodes = new ArrayList<Node>();
    // current map, renderer for maps
    private Map curMap;
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;
    private Ai ai;
    private UnitBuilder unitBuilder;

    @Override
    //TODO: Need to break out UI into a factory class for different views and control it in the superclass
    public void initialize() {
        ui = new Ui(this);
        super.initialize();
        initializeMap("greenMap");
        renderer = new SkirmishRenderer(this, camera, curMap);
        ai = new Ai(this);
        initializeUnits();
        initializeTurnOrder();
        nextTurn();
    }

    @Override
    public void render() {
        camera.update();
        renderer.render(selectedUnit);
        for (Unit curUnit : units) {
            renderer.drawUnit(curUnit);
        }
        ui.render();
        handleCursorPos();
    }

    @Override
    public void update() {
        super.update();
        updateUnits();
        setOccupiedNodes();
        this.ui.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.update();
        ui.getStage().getViewport().setScreenSize(width, height);
    }

    // initializes map
    private void initializeMap(String mapName) {
        TiledMap tmpMap = new TmxMapLoader().load("map/maps/" + mapName + ".tmx");
        // get map properties
        this.tileWidth = (Integer) tmpMap.getProperties().get("tilewidth");
        this.tileHeight = (Integer) tmpMap.getProperties().get("tileheight");
        this.mapWidth = (Integer) tmpMap.getProperties().get("width");
        this.mapHeight = (Integer) tmpMap.getProperties().get("height");
        curMap = new Map(tmpMap);
        // get collision properties of map
        try {
            addCollidableTiles();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        aStar = new AStar(collidableObjects, new int[this.mapWidth][this.mapHeight], this.tileWidth, this.tileHeight);
    }

    /**
     * Handle all of the unit initialization that needs to be here
     */
    private void initializeUnits() {
        unitBuilder = new UnitBuilder(this.tileWidth, this.tileHeight);
        this.units = unitBuilder.initializeUnits();
        for (Unit curUnit : units) {
            Node curNode = getNode(curUnit.getXOffset(), curUnit.getYOffset());
            curNode.setOccupied(true);
            curNode.setOccupier(curUnit);
            curUnit.setNode(curNode);
            if (curUnit.getSide() == Side.CPU)
                cpuUnits.add(curUnit);
            else if (curUnit.getSide() == Side.PLAYER)
                playerUnits.add(curUnit);
        }
    }

    // set up the turn order for sides
    private void initializeTurnOrder() {
        turnOrder = new ArrayList<Side>();
        turnOrder.add(Side.PLAYER);
        turnOrder.add(Side.CPU);
    }

    private void nextTurn() {
        if (unitOrder.size() > 0) {
            unitOrder.remove(0);
        }
        if (unitOrder.size() == 0) {
            setUnitOrder();
        }
        // set selected unit to the next unit
        Unit nextUnit = unitOrder.get(0);
        nextUnit.setActionPoints(nextUnit.getMaxActionPoints());
        curTurnSide = nextUnit.getSide();
        this.selectedUnit = nextUnit;
        setMovableNodes(this.selectedUnit);
        if (nextUnit.getSide() == Side.PLAYER) {
            this.ui.createHud(this.selectedUnit);
        } else {
            this.ui.clearHud();
            this.ai.processComputerTurn(nextUnit);
        }
    }

    /**
     * method to set nodes in line from wherever the player is, to the current cursor position
     */
    void handleCursorPos() {
        if (selectedUnit != null && input.cursorPos != null && curTurnSide == Side.PLAYER) {
            Vector3 cursorPos = new Vector3(input.cursorPos.x, input.cursorPos.y, 0);
            camera.unproject(cursorPos);
            showAccuracy(cursorPos, selectedUnit);
            // get line of sight nodes
            lineOfSightNodes.clear();
            lineOfSightNodes = aStar.getLineOfSightNodes((int) selectedUnit.getCenterXOffset(), (int) selectedUnit.getCenterYOffset(), (int) cursorPos.x, (int) cursorPos.y);
            renderer.drawLineOfSight(cursorPos, lineOfSightNodes);
        }
    }

    public void handleRightClick(Point point) {
        if (selectedUnit != null) {
            Vector3 cursorPos = new Vector3(input.cursorPos.x, input.cursorPos.y, 0);
            camera.unproject(cursorPos);
            Node destinationNode = getNode(cursorPos.x, cursorPos.y);
            if (destinationNode != null) {
                // see if the selected node is available to move to
                for (PathNode curNode : movableNodes) {
                    Unit curNodeOccupier = curNode.getOccupier();
                    if (curNode.getID().equals(destinationNode.getID()) && (curNodeOccupier == null || !curNodeOccupier.equals(selectedUnit))) {
                        setUnitDestination(destinationNode);
                        break;
                    }
                }
            }
            for (Node curNode : lineOfSightNodes) {
                if (curNode.getOccupied() && !curNode.getOccupier().equals(selectedUnit)) {
                    Unit attackedUnit = curNode.getOccupier();
                    attackUnit(selectedUnit, attackedUnit);
                }
            }
        }
    }
    
    /**
     * Callback method from Input class to handle non-directional keyup events
     */
    public void handleKeyUp(int keycode) {
        if(keycode == Keys.ESCAPE) {
            TacticsGame.switchView(OverworldView.class);
        }
    }

    /**
     * Set destination point for all selected units
     * This will need to be altered to take into account camera position as well
     */
    public void setUnitDestination(Node curNode) {
        int startingNodeX = getXNode(selectedUnit.getCenterXOffset());
        int startingNodeY = getYNode(selectedUnit.getCenterYOffset());
        ArrayList<PathNode> newPath = aStar.findPath(startingNodeX, startingNodeY, curNode.getX(), curNode.getY());
        setUnitPath(selectedUnit, newPath);
    }

    /**
     * See if the cursor is hovering over an enemy unit
     */
    private void showAccuracy(Vector3 cursorPos, Unit selectedUnit) {
        Node curNode = getNode(cursorPos.x, cursorPos.y);
        if (curNode != null && curNode.getOccupied() && curNode.getOccupier().getSide() != selectedUnit.getSide()) {
            Unit curEnemy = curNode.getOccupier();
            if (selectedUnit.getAction() != null) {
                String accuracy = String.valueOf(selectedUnit.getAction().getBaseAccuracy());
                renderer.drawText(accuracy, curEnemy.getXOffset() + curEnemy.getWidth(), curEnemy.getYOffset() + curEnemy.getHeight());
            }
        }
    }

    // this could be improved upon in picking what units to add rather than randomly
    private void setUnitOrder() {
        Side curSide = turnOrder.get(0);
        while (unitOrder.size() < units.size()) {
            for (Unit curUnit : units) {
                if (curUnit.getSide() == curSide && !unitOrder.contains(curUnit)) {
                    unitOrder.add(curUnit);
                    curSide = getNextTurnSide(curSide);
                }
            }
            if (this.cpuUnits.size() == 0 || this.playerUnits.size() == 0) {
                endGame();
            }
        }
    }

    /**
     * Logic to handle somebody winning the game, just prints out something right now
     */
    private void endGame() {
        if (this.cpuUnits.size() == 0) {
            System.out.println("You Win");
        } else if (this.playerUnits.size() == 0) {
            System.out.println("You Lose");
        }
        System.exit(0);
    }

    // get the side for the next turn given the current side
    private Side getNextTurnSide(Side curSide) {
        for (int i = 0; i < turnOrder.size(); i++) {
            if (turnOrder.get(i) == curSide) {
                if (turnOrder.size() == i + 1) {
                    return turnOrder.get(0);
                } else {
                    return turnOrder.get(i + 1);
                }
            }
        }
        // shouldn't ever reach this
        return null;
    }

    // handle unit movement/logic
    private void updateUnits() {
        for (Unit curUnit : units) {
            if (curUnit.getDestination() != null) {
                // determine what speed is right to reach the destination
                // x positions
                float curUnitXPos = curUnit.getXOffset() + curUnit.getWidth() / 2;
                curUnit.setDx(getUnitPosChange((int) curUnitXPos, curUnit.getDestination().x, curUnit.getSpeed()));
                // y positions
                float curUnitYPos = curUnit.getYOffset() + curUnit.getHeight() / 2;
                curUnit.setDy(getUnitPosChange((int) curUnitYPos, curUnit.getDestination().y, curUnit.getSpeed()));
            }
            if (curUnit.getDx() == 0 && curUnit.getDy() == 0 && curUnit.getPath().size() > 0) {
                // remove current destination and add the next point along the path
                curUnit.getPath().remove(0);
                curUnit.setActionPoints(curUnit.getActionPoints() - 1);
                if (curUnit.getPath().size() > 0) {
                    setNextDestination(curUnit);
                    // reached the end of the path
                } else {
                    setMovableNodes(curUnit);
                    if (curUnit.getActionPoints() <= 0) {
                        curUnit.setDestination(null);
                        nextTurn();
                    }
                }
            }
            curUnit.update();
        }
    }

    /**
     * Get dx or dy given the current position value and the destination value
     */
    private int getUnitPosChange(int curPos, int destPos, int baseSpeed) {
        int returnVal = 0;
        int newSpeed = (int) Math.min(baseSpeed, Math.abs(destPos - curPos));
        if (curPos < destPos) {
            returnVal = newSpeed;
        } else if (curPos > destPos) {
            returnVal = (newSpeed * -1);
        }
        return returnVal;
    }

    /**
     * Common method to handle units attacking each other
     * @param attacker - attacking unit
     * @param defender - defending unit
     */
    public void attackUnit(Unit attacker, Unit defender) {
        defender.takeDamage(attacker.getDamage());
        if (defender.getHealth() <= 0) {
            this.units.remove(defender);
            this.unitOrder.remove(defender);
            this.cpuUnits.remove(defender);
            this.playerUnits.remove(defender);
        }
        handleAction(attacker, attacker.getAttackAPCost());
    }

    /**
     * Determine whether all of the current unit's action points have been used
     * find the moveable nodes
     * @param curUnit
     * @param actionPoints
     */
    private void handleAction(Unit curUnit, int actionPoints) {
        curUnit.setActionPoints(curUnit.getActionPoints() - actionPoints);
        if (curUnit.getActionPoints() <= 0) {
            nextTurn();
        }
    }

    /**
     * Logic to look through all of the units
     * and set the current nodes they're on to occupied
     */
    private void setOccupiedNodes() {
        // clear occupied nodes
        for (int x = 0; x < aStar.nodes.length; x++) {
            for (int y = 0; y < aStar.nodes[0].length; y++) {
                aStar.nodes[x][y].setOccupied(false);
                aStar.nodes[x][y].setOccupier(null);
            }
        }
        // loop through all units, find corresponding node, and set to occupied
        for (Unit curUnit : units) {
            Node curNode = getNode(curUnit.getCenterXOffset(), curUnit.getCenterYOffset());
            curNode.setOccupied(true);
            curNode.setOccupier(curUnit);
        }
    }

    /**
     * find all collidable tiles, add them to collidable tiles arraylist to handle player collisions
     * @throws Exception
     */
    private void addCollidableTiles() throws Exception {
        collidableObjects.clear();
        // get the collidable map layer, find all of the objects on that layer
        MapLayer collidableLayer = curMap.getLayers().get("collidable");
        if (collidableLayer != null) {
            MapObjects collidableLayerObjects = collidableLayer.getObjects();
            for (RectangleMapObject collidableObject : collidableLayerObjects.getByType(RectangleMapObject.class)) {
                // initialize object and add to the arraylist
                // constructor here is object's x, y, width, height, and map properties
                CollidableObject curObj = new CollidableObject(collidableObject.getRectangle().x, collidableObject.getRectangle().y, collidableObject.getRectangle().width, collidableObject.getRectangle().height, collidableObject.getProperties());
                collidableObjects.add(curObj);
            }
        } else {
            throw new Exception("Can't find collidable layer in map");
        }
    }

    /**
     * Common things that need to happen when setting a unit's path
     * @param curUnit
     * @param curPath
     */
    public void setUnitPath(Unit curUnit, ArrayList<PathNode> curPath) {
        if (curPath.size() > 0) {
            curUnit.setPath(curPath);
            Node lastNode = curPath.get(curPath.size() - 1);
            curUnit.setFinalDestination(new Point((lastNode.getX() * this.tileWidth) + tileWidth / 2, (lastNode.getY() * this.tileHeight) + tileHeight / 2));
            curUnit.setNode(aStar.nodes[lastNode.getX()][lastNode.getY()]);
            setNextDestination(curUnit);
        }
    }

    /**
     * Set next destination along a path
     * @param unit
     */
    private void setNextDestination(Unit curUnit) {
        Node firstDestNode = curUnit.getPath().get(0);
        Point firstDestPoint = new Point((firstDestNode.getX() * this.tileWidth) + this.tileWidth / 2, (firstDestNode.getY() * this.tileHeight) + this.tileHeight / 2);
        curUnit.setDestination(firstDestPoint);
    }

    /**
     * Common method to set movable nodes given the current unit
     * @param curUnit
     */
    private void setMovableNodes(Unit curUnit) {
        this.movableNodes = aStar.getMovableNodes(getXNode(curUnit.getXOffset() + (curUnit.getWidth() / 2)), getYNode(curUnit.getYOffset() + (curUnit.getHeight() / 2)), curUnit.getActionPoints());
    }

    /**
     * Combination of getXNode and getYNode, but also pulls actual node from aStar
     */
    protected Node getNode(float offsetX, float offsetY) {
        int xNode = (int) Math.floor(offsetX / this.tileWidth);
        int yNode = (int) Math.floor(offsetY / this.tileHeight);
        if (xNode >= 0 && xNode < aStar.nodes.length && yNode >= 0 && yNode < aStar.nodes[0].length) {
            return this.aStar.nodes[xNode][yNode];
        } else {
            return null;
        }
    }

    /**
     * Get X node (tile) number given an X coordinate
     */
    protected int getXNode(float offsetX) {
        return (int) Math.floor(offsetX / this.tileWidth);
    }

    /**
     * Get Y node (tile) number given an X coordinate
     */
    protected int getYNode(float offsetY) {
        return (int) Math.floor(offsetY / this.tileHeight);
    }

    public ArrayList<PathNode> getMovableNodes() {
        return this.movableNodes;
    }

    // get currently selected unit
    public Unit getSelectedUnit() {
        return this.selectedUnit;
    }

    public void setLOSNodes(ArrayList<Node> lineOfSightNodes) {
        this.lineOfSightNodes = lineOfSightNodes;
    }

    public ArrayList<Node> getLOSNodes() {
        return this.lineOfSightNodes;
    }
}
