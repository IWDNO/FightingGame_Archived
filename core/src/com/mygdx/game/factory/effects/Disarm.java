package com.mygdx.game.factory.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;

public class Disarm extends Effect{
    public Disarm(float duration) {
        super(duration);
        this.effectTexture = new Texture("images/effects/Disarm.png");
    }

    @Override
    public void run(Player player) {
        player.setAttackCount(69);
        player.setEAttackCount(69);
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                player.setAttackCount(0);
                player.setEAttackCount(0);
            }
        }, duration);
    }
}
