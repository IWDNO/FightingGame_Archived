package com.mygdx.game.utils;

import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.factory.effects.Effect;

public class DamageResult {
    public float value;
    public boolean isCritical;
    public Effect effect;
    public Constants.EFFECT_TYPE effectType;
    public Sound sound;

    public DamageResult(float value, boolean isCritical) {
        this.value = value;
        this.isCritical = isCritical;
    }

    public Effect getEffect() {
        return effect;
    }
}
