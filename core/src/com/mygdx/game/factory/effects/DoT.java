package com.mygdx.game.factory.effects;

import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.DamageResult;

public class DoT extends Effect {
    private final float DPT;

    public DoT(float duration, float damagePerTick) {
        super(duration);
        this.DPT = damagePerTick;
    }

    @Override
    public void run(final Player player) {
        Timer.schedule(new Timer.Task() {
            private float elapsed = 0;
            public void run() {
                if (elapsed < duration) {
                    DamageResult d = new DamageResult(DPT, false);
                    d.effectType = Constants.EFFECT_TYPE.DoT;
                    player.takeDamage(d);
                    elapsed += 1;
                } else {
                    this.cancel();
                }
            }
        }, 1, 1);
    }
}
