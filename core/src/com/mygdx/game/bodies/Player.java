package com.mygdx.game.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Character;
import com.mygdx.game.controller.ControlScheme;

import static com.mygdx.game.views.MainScreen.WORLD_HEIGHT;

public class Player {
    private ControlScheme cs;
    private Character character;
    private final Body player;

    private final float SPEED = 10;
    private final float DASH_DURATION = 0.1f;
    private final float DASH_SPEED = 20f;

    private int jumpCounter = 0;
    public float currentHealth;
    private boolean isDashing = false;
    private boolean isFacingRight;
    private boolean isDashingAvailable = true;

    private SpriteBatch sb;
    private Texture playerTexture;

    public Player (Character character, float x, float y, ControlScheme cs, int playerNumber, SpriteBatch sb) {
        this.cs = cs;
        this.character = character;

        currentHealth = character.getHP();
        player = character.createPlayer(x, y);
        isFacingRight = playerNumber == 1;

        this.sb = sb;
        playerTexture = new Texture(character.getTexture());
    }

    public void update() {
        sb.begin();

        float textureWidth = WORLD_HEIGHT / 2 / 8;
        float textureHeight = WORLD_HEIGHT / 9.5f;
        if (isFacingRight) {
            sb.draw(playerTexture, player.getPosition().x - 0.5f, player.getPosition().y - 0.8f, textureWidth, textureHeight);
        } else {
            sb.draw(playerTexture, player.getPosition().x - 0.5f + textureWidth, player.getPosition().y - 0.8f,
                    -textureWidth, textureHeight);
        }
        sb.end();
        handleInput();
    }

    public void handleInput() {
        updateFacingDirection();
        if (!isDashing && Gdx.input.isKeyJustPressed(cs.dashKey)) {
            dash();
        }

        if (Gdx.input.isKeyJustPressed(cs.attackKey)) {
            character.useNormalAttack(player);
        }
        if (Gdx.input.isKeyJustPressed(cs.eKey)) {
            character.useE(player);
        }
        if (Gdx.input.isKeyJustPressed(cs.qkey)) {
            character.useQ(player);
        }

        int velX = 0;
        if (Gdx.input.isKeyPressed(cs.moveLeftKey)) {
            velX = -1;
        } else if (Gdx.input.isKeyPressed(cs.moveRightKey)) {
            velX = 1;
        }
        if (Gdx.input.isKeyPressed(cs.moveLeftKey) && Gdx.input.isKeyPressed(cs.moveRightKey)) {
            velX = 0;
        }

        if (Gdx.input.isKeyJustPressed(cs.jumpKey) && jumpCounter < 2) {
            jumpCounter++;
            float force = player.getMass() * 18000;
            player.setLinearVelocity(player.getLinearVelocity().x, 0);
            player.applyLinearImpulse(new Vector2(0, force), player.getPosition(), true);
        }
        if (player.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }

        player.setLinearVelocity(player.getLinearVelocity().x, player.getLinearVelocity().y < 10 ? player.getLinearVelocity().y : 10);
        if (!isDashing) {
            player.setLinearVelocity(velX * SPEED, player.getLinearVelocity().y);
        }
    }

    private void dash() {
        if (!isDashingAvailable) {
            return;
        }
        isDashing = true;
        isDashingAvailable = false;
        Timer dashTimer = new Timer();
        dashTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                isDashing = false;
            }
        }, DASH_DURATION);
        dashTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                isDashingAvailable = true;
            }
        }, .5f);

        float impulseX = player.getMass() * DASH_SPEED;
        impulseX = isFacingRight ? impulseX : -impulseX;
        player.applyLinearImpulse(new Vector2(impulseX, 0), player.getPosition(), true);
    }

    private void updateFacingDirection() {
        if (player.getLinearVelocity().x > 0) {
            isFacingRight = true;
        } else if (player.getLinearVelocity().x < 0) {
            isFacingRight = false;
        }
    }

    public float generateDamage(int attackType) {
        return character.generateDamage(attackType);
    }

    public void takeDamage(float damage) {
        character.takeDamage(damage);
        currentHealth = character.getHP();
    }
}
