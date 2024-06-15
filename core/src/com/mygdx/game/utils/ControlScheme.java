package com.mygdx.game.utils;

public class ControlScheme {
    public final int moveLeftKey;
    public final int moveRightKey;
    public final int jumpKey;
    public final int dashKey;
    public final int attackKey;
    public final int eKey;
    public final int qkey;

    public ControlScheme(int moveLeftKey, int moveRightKey, int jumpKey, int dashKey, int attackKey, int eKey, int qkey) {
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;
        this.jumpKey = jumpKey;
        this.attackKey = attackKey;
        this.dashKey = dashKey;
        this.eKey = eKey;
        this.qkey = qkey;
    }
}
