package com.mygdx.game.factory.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.DamageResult;

public class DoT extends Effect {
    private final float DPT;

    public DoT(float duration, float damagePerTick) {
        super(duration);
        this.DPT = damagePerTick;
        this.effectTexture = new Texture("images/effects/DoT.png");
    }

    @Override
    public void run(final Player player) {
        Timer.schedule(new Timer.Task() {
            public void run() {
                if (player.getHP() <= 0 || isDone()) {
                    this.cancel();
                    return;
                }
                DamageResult d = new DamageResult(DPT, false);
                d.effectType = Constants.EFFECT_TYPE.DoT;
                player.takeDamage(d);
            }
        }, .5f, 1);
    }
}
