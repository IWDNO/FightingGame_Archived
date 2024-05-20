package com.mygdx.game.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.animator.AnimationFactory;
import com.mygdx.game.animator.DamageText;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.BodyFactory.BodyFactory.createDefaultAttack;
import static com.mygdx.game.BodyFactory.BodyFactory.createDefaultPlayer;

public class TestCharacter extends Character {
    public static final float MAX_HP = 1000;
    public static final float ATK = 100;
    public static final float DEF_SCALE = .1f;
    public static final float NORMAL_ATTACK_SCALE = .75f;
    public static final float E_ATTACK_SCALE = 1f;
    public static final float Q_ATTACK_SCALE = 1f;

    private final World world;
    private final MainScreen screen;

    private final float attackDelay = 0.7f;
    private final int playerNumber;
    public float attackCount = 0;
    private float currentHealth = MAX_HP;

    private DamageText damageWriter;

    public static Animation<TextureRegion> idleAnimation = AnimationFactory.create(6, 1, "images/Wizard Pack/Idle.png");
    public static Animation<TextureRegion> runAnimation = AnimationFactory.create(8, 1, "images/Wizard Pack/Run.png");
    public static Animation<TextureRegion> jumpAnimation = AnimationFactory.create(2, 1, "images/Wizard Pack/Jump.png");
    public static Animation<TextureRegion> fallAnimation = AnimationFactory.create(2, 1, "images/Wizard Pack/Fall.png");
    public static Animation<TextureRegion> hitAnimation = AnimationFactory.create(4, 1, "images/Wizard Pack/Hit.png");
    public static Animation<TextureRegion> attack1Animation = AnimationFactory.create(8, 1, "images/Wizard Pack/Attack1.png");
    public static Animation<TextureRegion> attack2Animation = AnimationFactory.create(8, 1, "images/Wizard Pack/Attack2.png");
    public static Animation<TextureRegion> dashAnimation = AnimationFactory.create(8, 1, "images/Wizard Pack/Dash.png");
    public static Animation<TextureRegion> jumpDashAnimation = AnimationFactory.create(2, 1, "images/Wizard Pack/JumpDash.png");

    public TestCharacter(World world, int playerNumber, MainScreen screen) {
        this.world = world;
        this.playerNumber = playerNumber;
        this.screen = screen;
        damageWriter = new DamageText(playerNumber);
    }

    @Override
    public Body createPlayer(float x, float y) {
        Body player = createDefaultPlayer(x, y, world);
        player.setUserData(String.format("Player%d", playerNumber));

        return player;
    }

    @Override
    public void useNormalAttack(Body player, boolean facingDirection) {
        if (attackCount < 1) {
            Timer timer = new Timer();
            attackCount++;
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    Body attack = createDefaultAttack(player.getPosition().x, player.getPosition().y, 1.5f, world, facingDirection);
                    attack.setUserData(String.format("Player%d-attack", playerNumber));
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            world.destroyBody(attack);
                        }
                    }, 0.8f);
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            attackCount--;
                        }
                    }, attackDelay);
                }
            }, 0.2f);

        }
    }

    @Override
    public void useE(Body player) {

    }

    @Override
    public void useQ(Body player) {

    }

    @Override
    public float generateDamage(int attackType) {
        return getAttackScale(attackType) * ATK;
    }

    @Override
    public void takeDamage(float damage, Vector2 hitPosition) {
        float final_damage = (damage - (damage * DEF_SCALE));
        currentHealth -= final_damage;

        damageWriter.spawn(hitPosition, final_damage);

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
        return "images/Wizard Pack/Run.png";
    }

    @Override
    public void update(SpriteBatch sb) {
        damageWriter.render(sb);
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
