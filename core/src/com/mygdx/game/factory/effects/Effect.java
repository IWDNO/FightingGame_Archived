package com.mygdx.game.factory.effects;

import com.mygdx.game.characters.Player;

public abstract class Effect {
    protected float duration;

    public Effect(float duration) {
        this.duration = duration;
    }
    public abstract void run(Player player);

}
