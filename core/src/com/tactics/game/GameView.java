package com.tactics.game;

import java.awt.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.ui.Ui;
import com.tactics.input.Input;

public abstract class GameView {
    
    OrthographicCamera camera;
    Input input;
    Ui ui;
    private final int CAMERA_SCROLL_SPEED = 5;
    
    abstract void render();
    abstract void resize(int width, int height);
    abstract void handleCursorPos();
    public abstract void handleKeyUp(int keycode);
    public abstract void handleRightClick(Point point);
    
    protected void initialize() {
        initializeCamera();
        initializeInput();
    }
    
    protected void update() {
        updateCamera();
    }

    // initializes camera
    private void initializeCamera() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
        camera.setToOrtho(false, w, h);
        camera.update();
    }

    private void initializeInput() {
        input = new Input(this);
        if(ui != null) {
            InputMultiplexer inputMultiplexer = new InputMultiplexer(ui.getStage(), input);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }
    
    public OrthographicCamera getCamera() {
        return this.camera;
    }

    // handle camera movement
    private void updateCamera() {
        camera.update();
        // left/right
        if (input.leftHeld && !input.rightHeld) {
            camera.translate(-CAMERA_SCROLL_SPEED, 0);
        } else if (input.rightHeld && !input.leftHeld) {
            camera.translate(CAMERA_SCROLL_SPEED, 0);
        }
        // up/down
        if (input.downHeld && !input.upHeld) {
            camera.translate(0, -CAMERA_SCROLL_SPEED);
        } else if (input.upHeld && !input.downHeld) {
            camera.translate(0, CAMERA_SCROLL_SPEED);
        }
    }
    
}
