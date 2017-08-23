package com.tactics.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class TacticsGame extends ApplicationAdapter {
    public boolean devMode = true;
    private static GameView gameView;

    @Override
    public void create() {
        initializeGame();
    }

    // initialize these before the game starts
    public void initializeGame() {
        gameView = new SkirmishView();
        gameView.initialize();
    }

    @Override
    public void render() {
        // game logic
        gameLoop();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameView.render();
    }

    @Override
    public void resize(int width, int height) {
        gameView.resize(width, height);
    }

    // main game logic
    private void gameLoop() {
        gameView.update();
    }
    
    public static void switchView(Class<?> viewType) {
        try {
            gameView = (GameView) viewType.newInstance();
            gameView.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
