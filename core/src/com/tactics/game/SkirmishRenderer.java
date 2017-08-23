package com.tactics.game;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.tactics.map.Map;
import com.tactics.map.Node;
import com.tactics.unit.Side;
import com.tactics.unit.Unit;

public class SkirmishRenderer {
    private final int HEALTH_HEIGHT = 2;
    private BitmapFont defaultFont;
    private SpriteBatch spriteBatch;
    private ShapeRenderer selectorRenderer;
    private ShapeRenderer healthRenderer;
    private OrthographicCamera camera;
    private SkirmishView view;
    private Map map;
    private float elapsedTime = 0;
    private TiledMapRenderer tiledMapRenderer;

    public SkirmishRenderer(SkirmishView view, OrthographicCamera camera, Map map) {
        this.view = view;
        this.camera = camera;
        this.map = map;
        spriteBatch = new SpriteBatch();
        selectorRenderer = new ShapeRenderer();
        healthRenderer = new ShapeRenderer();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map.tiledMap);
        defaultFont = new BitmapFont();
    }

    public void render(Unit selectedUnit) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        spriteBatch.setProjectionMatrix(camera.combined);
        selectorRenderer.setProjectionMatrix(camera.combined);
        healthRenderer.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        renderSelectors(selectedUnit);
    }

    /**
     * Draw selector
     */
    private void renderSelectors(Unit selectedUnit) {
        selectorRenderer.begin(ShapeType.Line);
        // display selected units
        if (selectedUnit != null) {
            if (selectedUnit.getSide() == Side.PLAYER) {
                selectorRenderer.setColor(Color.GREEN);
            } else {
                selectorRenderer.setColor(Color.RED);
            }
            selectorRenderer.rect(selectedUnit.getXOffset(), selectedUnit.getYOffset(), map.getTileWidth(), map.getTileHeight());
            if (selectedUnit.getDestination() != null) {
                selectorRenderer.circle(selectedUnit.getFinalDestination().x, selectedUnit.getFinalDestination().y, 2);
            }
        }
        // highlight movable nodes
        for (Node curNode : view.getMovableNodes()) {
            selectorRenderer.rect(curNode.getX() * map.getTileHeight(), curNode.getY() * map.getTileHeight(), map.getTileWidth(), map.getTileHeight());
        }
        if (view.devMode) {
            renderDevSelector();
        }
        selectorRenderer.end();
    }

    // dev mode method to allow selecting units
    private void renderDevSelector() {
        selectorRenderer.setColor(Color.BLACK);
        for (int i = 0; i < view.aStar.nodes.length; i++) {
            for (int j = 0; j < view.aStar.nodes[0].length; j++) {
                if (view.aStar.nodes[i][j].getCollidable()) {
                    selectorRenderer.rect(view.aStar.nodes[i][j].getX() * map.getTileWidth(), view.aStar.nodes[i][j].getY() * map.getTileHeight(), map.getTileWidth(), map.getTileHeight());
                }
            }
        }
    }

    public void drawUnit(Unit curUnit) {
        spriteBatch.begin();
        spriteBatch.draw(curUnit.getCurAnim().getKeyFrame(elapsedTime, true), curUnit.getXOffset(), curUnit.getYOffset());
        spriteBatch.end();
        renderHealth(curUnit);
    }

    /**
     * Draw player's healthbar
     */
    private void renderHealth(Unit curUnit) {
        // draw rectangle shape
        healthRenderer.begin(ShapeType.Line);
        healthRenderer.setColor(Color.RED);
        healthRenderer.rect(curUnit.getXOffset(), curUnit.getYOffset(), curUnit.getWidth(), this.HEALTH_HEIGHT);
        healthRenderer.end();
        healthRenderer.begin(ShapeType.Filled);
        int curHealthBarFill = (int) ((curUnit.getHealth() / curUnit.getHealthMax()) * curUnit.getWidth());
        healthRenderer.rect(curUnit.getXOffset(), curUnit.getYOffset(), curHealthBarFill, this.HEALTH_HEIGHT);
        healthRenderer.end();
    }

    /**
     * Dev mode method to draw line of sight
     */
    protected void drawLineOfSight(Vector3 cursorPos, ArrayList<Node> lineOfSightNodes) {
        selectorRenderer.begin(ShapeType.Line);
        selectorRenderer.setColor(Color.WHITE);
        selectorRenderer.line(view.getSelectedUnit().getCenterXOffset(), view.getSelectedUnit().getCenterYOffset(), cursorPos.x, cursorPos.y);
        for (Node curNode : lineOfSightNodes) {
            selectorRenderer.rect(curNode.getX() * map.getTileWidth(), curNode.getY() * map.getTileHeight(), map.getTileWidth(), map.getTileHeight());
        }
        selectorRenderer.end();
    }

    /**
     * This has good potential to be included in a superclass
     */
    protected void drawText(String textVal, float xPos, float yPos) {
        spriteBatch.begin();
        defaultFont.draw(spriteBatch, textVal, xPos, yPos);
        spriteBatch.end();
    }
}
