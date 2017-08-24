package com.tactics.game;

import java.awt.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OverworldView extends GameView {

    Texture overworldTexture;
    Pixmap lookupPixMmap;
    SpriteBatch overworldBatch;
    
    @Override
    public void initialize() {
        super.initialize();
        overworldTexture = new Texture(Gdx.files.internal("map/overworld/overworld_geo.png"));
        Texture lookupMapTexture = new Texture(Gdx.files.internal("map/overworld/overworld_lookup.png"));
        lookupMapTexture.getTextureData().prepare();
        lookupPixMmap = lookupMapTexture.getTextureData().consumePixmap();
        overworldBatch = new SpriteBatch();
    }

    @Override
    public void render() {
        overworldBatch.setProjectionMatrix(camera.combined);
        overworldBatch.begin();
        overworldBatch.draw(overworldTexture, 0, 0);
        overworldBatch.end();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void handleKeyUp(int keycode) {
        if(keycode == Keys.ESCAPE) {
            TacticsGame.switchView(SkirmishView.class);
        }
    }

    @Override
    public void handleRightClick(Point point) {
        String hexColor = lookupMapColor(point);
        System.out.println(hexColor);
    }
    
    @Override
    public void handleCursorPos() {
    }
    
    public String lookupMapColor(Point point) {
        Color color = new Color(lookupPixMmap.getPixel(point.x, point.y));
        String hexVal = color.toString().substring(0, 6);
        return hexVal;
    }
}
