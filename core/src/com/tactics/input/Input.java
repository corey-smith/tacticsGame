package com.tactics.input;

import java.awt.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.tactics.game.GameView;

public class Input implements InputProcessor {
    GameView game;
    public boolean rightHeld = false;
    public boolean leftHeld = false;
    public boolean upHeld = false;
    public boolean downHeld = false;
    // selctor values
    public Point cursorPos;

    // initialize
    public Input(GameView gameView) {
        this.game = gameView;
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Mouse Events
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        cursorPos = new Point(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.RIGHT) {
            Point destination = new Point(cursorPos.x, cursorPos.y);
            this.game.handleRightClick(destination);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.LEFT) {
            leftHeld = true;
        } else if (keycode == Keys.RIGHT) {
            rightHeld = true;
        } else if (keycode == Keys.UP) {
            upHeld = true;
        } else if (keycode == Keys.DOWN) {
            downHeld = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.LEFT) {
            leftHeld = false;
        } else if (keycode == Keys.RIGHT) {
            rightHeld = false;
        } else if (keycode == Keys.UP) {
            upHeld = false;
        } else if (keycode == Keys.DOWN) {
            downHeld = false;
        } else {
            game.handleKeyUp(keycode);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        game.getCamera().zoom += ((float) amount) / 5f;
        game.getCamera().update();
        return false;
    }
}
