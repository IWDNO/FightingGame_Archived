package com.mygdx.game.factory.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;

public class SwipeControls extends Effect{
    public SwipeControls(float duration) {
        super(duration);
        this.effectTexture = new Texture("images/effects/SwipeControls.png");
    }

    @Override
    public void run(Player player) {
        player.setSpeed(-player.getSpeed());
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                player.setSpeed(-player.getSpeed());
            }
        }, duration);
    }
}
