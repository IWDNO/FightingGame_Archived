package com.mygdx.game.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.BodyFactory.BodyFactory.createDefaultAttack;
import static com.mygdx.game.BodyFactory.BodyFactory.createDefaultPlayer;
import static com.mygdx.game.views.MainScreen.WORLD_HEIGHT;

public class TestCharacter extends Character {
    public static final float MAX_HP = 1000;
    public static final float ATK = 100;
    public static final float DEF_SCALE = .1f;
    public static final float NORMAL_ATTACK_SCALE = .75f;
    public static final float E_ATTACK_SCALE = 1f;
    public static final float Q_ATTACK_SCALE = 1f;

    private final World world;
    private final MainScreen screen;

    private final float attackDelay = 0.2f;
    private final int playerNumber;
    private float attackCount = 0;
    private float currentHealth = MAX_HP;

    public TestCharacter(World world, int playerNumber, MainScreen screen) {
        this.world = world;
        this.playerNumber = playerNumber;
        this.screen = screen;
    }

    @Override
    public Body createPlayer(float x, float y) {
        Body player = createDefaultPlayer(x, y, world);
        player.setUserData(String.format("Player%d", playerNumber));

        return player;
    }

    @Override
    public void useNormalAttack(Body player) {
        if (attackCount < 1) {
            attackCount++;
            Body attack = createDefaultAttack(player.getPosition().x, player.getPosition().y, 1.25f, world);
            attack.setUserData(String.format("Player%d-attack", playerNumber));

            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    world.destroyBody(attack);
                }
            }, 0.05f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    attackCount--;
                }
            }, attackDelay);
        }
    }

    @Override
    public void useE(Body player) {
        Body attack = createDefaultAttack(player.getPosition().x, player.getPosition().y, 1f, world);
        attack.setUserData(String.format("Player%d-attack", playerNumber));

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                world.destroyBody(attack);
            }
        }, 0.05f);
    }

    @Override
    public void useQ(Body player) {
        Body attack = createDefaultAttack(player.getPosition().x, player.getPosition().y, 1f, world);
        attack.setUserData(String.format("Player%d-attack", playerNumber));

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                world.destroyBody(attack);
            }
        }, 0.05f);
    }

    @Override
    public float generateDamage(int attackType) {
        return getAttackScale(attackType) * ATK;
    }

    @Override
    public void takeDamage(float damage) {
        currentHealth -= (damage - (damage * DEF_SCALE));
        if (currentHealth <= 0) {
            screen.endGame();
        }
    }

    @Override
    public float getHP() {
        return currentHealth;
    }

    @Override
    public String getTexture() {
        return "images/player2.png";
    }

    @Override
    public void update(SpriteBatch sb) {

    }


    public float getAttackScale(int attack) {
        switch (attack) {
            case NORMAL_ATTACK:
                return NORMAL_ATTACK_SCALE;
            case E_ATTACK:
                return E_ATTACK_SCALE;
            case Q_ATTACK:
                return Q_ATTACK_SCALE;
            default:
                return 1f;
        }
    }
}
