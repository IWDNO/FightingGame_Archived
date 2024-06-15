package com.mygdx.game.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.utils.DamageResult;

import java.util.Random;

import static com.mygdx.game.utils.Constants.*;

public class DamageText {
    private static final float MAX_LIFE_TIME = 2f;
    private static Array<DamageTextInstance> instances = new Array<>();
    private final int playerNumber;


    private BitmapFont font;

    public DamageText(int playerNumber) {
        font = new BitmapFont(Gdx.files.internal("fonts/DamageNumbers/damage.fnt"), false);
        font.getData().setScale(1f / 32f, 1f / 32f);
        font.setUseIntegerPositions(false);
        this.playerNumber = playerNumber;
    }

    public void spawn(Vector2 position, DamageResult damage) {
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

    public void dispose() {
        font.dispose();
    }

    private static class DamageTextInstance {
        private final int playerNumber;
        private Vector2 position;
        private DamageResult damage;
        private float lifeTime;

        public DamageTextInstance(Vector2 position, DamageResult damage, int playerNumber) {
            Random random = new Random();
            this.position = new Vector2(
                    position.x - 32 / WORLD_HEIGHT / 2 + random.nextFloat(-1f, 1f),
                    position.y + 32 / WORLD_HEIGHT / 2 + PLAYER_HEIGHT * .75f + random.nextFloat(.5f)
            );

            this.playerNumber = playerNumber;
            this.damage = damage;
            this.lifeTime = 0f;
        }

        public void render(SpriteBatch batch, BitmapFont font) {
            lifeTime += Gdx.graphics.getDeltaTime();

            float alpha = 1 - (lifeTime / MAX_LIFE_TIME);

            if (damage.effectType != null) {
                font.setColor(0.721f, 0.219f, 0.321f, alpha);
                font.getData().setScale(1 / 40f);
            }
            else if (playerNumber == 1) {
                font.setColor(1, 0, 0, alpha); // Red color for player 1
            } else {
                font.setColor(0, 0, 1, alpha); // Blue color for player 2
            }

            if (damage.isCritical) {
                font.draw(batch, "Crit", position.x, position.y + 1);
            }
            font.draw(batch, String.valueOf((int) damage.value), position.x, position.y);
        }

        public boolean isExpired() {
            return lifeTime >= MAX_LIFE_TIME;
        }
    }
}
