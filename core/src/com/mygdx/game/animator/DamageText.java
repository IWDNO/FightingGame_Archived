package com.mygdx.game.animator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class DamageText {
    private static final float MAX_LIFE_TIME = 2f;
    private static Array<DamageTextInstance> instances = new Array<>();
    private final int playerNumber;


    private BitmapFont font;

    public DamageText(int playerNumber) {
        font = new BitmapFont(Gdx.files.internal("font_.fnt"), false);
        this.playerNumber = playerNumber;
    }

    public void spawn(Vector2 position, float damage) {
        instances.add(new DamageTextInstance(position, damage, playerNumber));
    }

    public void render(SpriteBatch batch) {
        for (int i = instances.size - 1; i >= 0; i--) {
            DamageTextInstance instance = instances.get(i);
            instance.render(batch, font);
            if (instance.isExpired()) {
                instances.removeIndex(i);
            }
        }
    }

    private static class DamageTextInstance {
        private final int playerNumber;
        private Vector2 position;
        private float damage;
        private float lifeTime;

        public DamageTextInstance(Vector2 position, float damage, int playerNumber) {
            Random random = new Random();
            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            this.position = new Vector2(
                    w/2 + position.x * w/20 * 9/16 + (random.nextInt(16) - 10) * w/500,
                    h/2 + position.y * h/20 + h/10
            );

            this.playerNumber = playerNumber;
            this.damage = damage;
            this.lifeTime = 0f;
        }

        public void render(SpriteBatch batch, BitmapFont font) {
            lifeTime += Gdx.graphics.getDeltaTime();

            float alpha = 1 - (lifeTime / MAX_LIFE_TIME);
            if (playerNumber == 1) {
                font.setColor(1, 0, 0, alpha); // Red color for player 1
            } else {
                font.setColor(0, 0, 1, alpha); // Blue color for player 2
            }

            font.draw(batch, String.valueOf((int) damage), position.x, position.y);
        }

        public boolean isExpired() {
            return lifeTime >= MAX_LIFE_TIME;
        }
    }
}