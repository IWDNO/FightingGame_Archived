package com.mygdx.game.utils;

import com.mygdx.game.factory.effects.Effect;

public class DamageResult {
    public float value;
    public boolean isCritical;
    public Effect effect;
    public Constants.EFFECT_TYPE effectType;

    public DamageResult(float value, boolean isCritical) {
        this.value = value;
        this.isCritical = isCritical;
    }

    public Effect getEffect() {
        return effect;
    }
}
