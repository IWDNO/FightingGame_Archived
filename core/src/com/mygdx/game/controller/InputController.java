package com.mygdx.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputController implements InputProcessor {
    public boolean left, right, up;
    private ControlScheme cs;


    public InputController(ControlScheme cs) {
        this.cs = cs;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        if (keycode == cs.moveLeftKey) {
            left = true;
            keyProcessed = true;
        } else if (keycode == cs.moveRightKey) {
            right = true;
            keyProcessed = true;
        } else if (keycode == cs.jumpKey) {
            up = true;
            keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        if (keycode == cs.moveLeftKey) {
            left = false;
            keyProcessed = true;
        } else if (keycode == cs.moveRightKey) {
            right = false;
            keyProcessed = true;
        } else if (keycode == cs.jumpKey) {
            up = false;
            keyProcessed = true;
        }

        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
