package com.mygdx.game.views;

import com.badlogic.gdx.Screen;
import com.mygdx.game.FightingGame;

public class LoadingScreen implements Screen {
    private FightingGame parent;
    public LoadingScreen(FightingGame fg) {
        parent = fg;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        parent.changeScreen(FightingGame.MENU);
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
