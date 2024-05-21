package com.mygdx.game.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Character {
    public static final int NORMAL_ATTACK = 1;
    public static final int E_ATTACK = 2;
    public static final int Q_ATTACK = 3;

    public abstract Body createPlayer(float x, float y);

    public abstract void useNormalAttack(Body player, boolean facingDirection);
    public abstract void useE(Body player, boolean facingDirection);
    public abstract void useQ(Body player);
    public abstract float generateDamage(int attackType);
    public abstract void takeDamage(float damage, Vector2 hitPosition);
    public abstract float getHP();
    public abstract String getTexture();
    public abstract void update(SpriteBatch spriteBatch);
}
