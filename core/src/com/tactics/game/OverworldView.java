package com.tactics.game;

import java.awt.Point;
import com.badlogic.gdx.Input.Keys;

public class OverworldView extends GameView {
    @Override
    public void initialize() {
        
    }

    @Override
    public void render() {
    }

    @Override
    public void update() {
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
    }

    @Override
    public void handleCursorPos() {
    }
}
