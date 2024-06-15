package com.mygdx.game.utils;

import com.badlogic.gdx.Input;

public class Constants {
    public static final int SAI_HAN = 0;
    public static final int KING = 1;
    public static final int HUNTRESS = 2;
    public static final int HERO_KNIGHT = 3;

    public static final float RATIO = 16f / 9f;
    public static final float WORLD_HEIGHT = 20;
    public static final float WORLD_WIDTH = WORLD_HEIGHT * RATIO;

    public static final float PLAYER_HEIGHT = WORLD_HEIGHT / 7;
    public static final float PLAYER_WIDTH = PLAYER_HEIGHT / 2;

    public static final ControlScheme PLAYER1_CONTROL_SCHEME = new ControlScheme(
            Input.Keys.D, Input.Keys.G, Input.Keys.R, Input.Keys.E, Input.Keys.Q, Input.Keys.W, Input.Keys.C
    );
    public static final ControlScheme PLAYER2_CONTROL_SCHEME = new ControlScheme(
            Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.M, Input.Keys.SLASH, Input.Keys.PERIOD, Input.Keys.COMMA
    );

    public enum ATTACK_TYPE {
        NORMAL_ATTACK,
        E_ATTACK,
        Q_ATTACK
    }

    public enum EFFECT_TYPE {
        DoT
    }
}
