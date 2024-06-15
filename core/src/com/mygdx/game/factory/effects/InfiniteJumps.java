package com.mygdx.game.factory.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;

public class InfiniteJumps extends Effect{
    public InfiniteJumps(float duration) {
        super(duration);
        this.effectTexture = new Texture("images/effects/InfiniteJump.png");
    }

    @Override
    public void run(Player player) {
        player.setMaxJumps(69);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                player.setMaxJumps(2);
            }
        }, duration);
    }
}
