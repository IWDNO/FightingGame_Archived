package com.mygdx.game.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.animator.AnimationFactory;
import com.mygdx.game.animator.DamageText;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.utils.PlayerStates;
import com.mygdx.game.views.MainScreen;

import java.util.Random;

import static com.mygdx.game.BodyFactory.BodyFactory.*;

public class HeroKnight extends TestCharacter {
    public static final float MAX_HP = 900;
    public static final float ATK = 100;
    public static final float DEF_SCALE = .1f;
    public static final float NORMAL_ATTACK_SCALE = .55f;
    public static final float E_ATTACK_SCALE = 1.25f;
    public static final float Q_ATTACK_SCALE = 1f;

    private final World world;
    private final MainScreen screen;

    private final float attackDelay = 0.4f;
    private final float eAttackDelay = 5f;
    private final int playerNumber;
    public float attackCount = 0;
    public float eAttackCount = 0;
    private float currentHealth = MAX_HP;
    private float zoom = 5.6f;

    private DamageText damageWriter;

    protected static Animation<TextureRegion> idleAnimation;
    protected static Animation<TextureRegion> runAnimation;
    protected static Animation<TextureRegion> jumpAnimation;
    protected static Animation<TextureRegion> fallAnimation;
    protected static Animation<TextureRegion> hitAnimation;
    protected static Animation<TextureRegion> attack1Animation;
    protected static Animation<TextureRegion> attack2Animation;
    protected static Animation<TextureRegion> attack3Animation;
    protected static Animation<TextureRegion> dashAnimation;
    protected static Animation<TextureRegion> jumpDashAnimation;
    protected static Animation<TextureRegion> deathAnimation;

    public float getAttackCount() {
        return attackCount;
    }
    public float geteAttackCount() {
        return eAttackCount;
    }
    public float getZoom() {
        return zoom;
    }
    public float getAttackDelay() {
        return attackDelay;
    }

    public float geteAttackDelay() {
        return 0.4f;
    }
    public float getMaxHp() {
        return MAX_HP;
    }
    public float getDeathAnimationTime() {
        return  2.2f;
    }
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(11,.1f, 1, "images/HeroKnight/Idle.png");
        runAnimation = AnimationFactory.create(8,.1f, 1, "images/HeroKnight/Run.png");
        jumpAnimation = AnimationFactory.create(3,.1f, 1, "images/HeroKnight/Jump.png");
        fallAnimation = AnimationFactory.create(3,.1f, 1, "images/HeroKnight/Fall.png");
        hitAnimation = AnimationFactory.create(4,.1f, 1, "images/HeroKnight/Take Hit.png");
        attack1Animation = AnimationFactory.create(7,.1f, 1, "images/HeroKnight/Attack1.png");
        attack2Animation = AnimationFactory.create(7,.1f, 1, "images/HeroKnight/Attack2.png");
        dashAnimation = AnimationFactory.create(8,.1f, 1, "images/HeroKnight/Run.png");
        jumpDashAnimation = AnimationFactory.create(3,.1f, 1, "images/HeroKnight/Fall.png");
        deathAnimation = AnimationFactory.create(11,.2f, 1, "images/HeroKnight/Death.png");
    }

    public HeroKnight(World world, int playerNumber, MainScreen screen) {
        super(world, playerNumber, screen);
        this.world = world;
        this.playerNumber = playerNumber;
        this.screen = screen;
        damageWriter = new DamageText(playerNumber);
        setAnimations();
    }

    public Animation<TextureRegion> getAnimation(int playerState) {
        switch (playerState) {
            case PlayerStates.RUN:
                return runAnimation;
            case PlayerStates.ATTACK1:
                return attack1Animation;
            case PlayerStates.ATTACK2:
                return attack2Animation;
            case PlayerStates.FALL:
                return fallAnimation;
            case PlayerStates.HIT:
                return hitAnimation;
            case PlayerStates.JUMP:
                return jumpAnimation;
            case PlayerStates.DASH:
                return dashAnimation;
            case PlayerStates.JUMP_DASH:
                return jumpDashAnimation;
            case PlayerStates.DEATH:
                return deathAnimation;
            default:
                return idleAnimation;
        }
    }


    @Override
    public Body createPlayer(float x, float y) {
        Body player = createDefaultPlayer(x, y, world);
        player.setUserData(String.format("Player%d", playerNumber));

        return player;
    }


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
                    }, 0.3f);
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


    public void useE(Body player, boolean facingDirection) {
        if (eAttackCount < 1) {
            Timer timer = new Timer();
            eAttackCount++;
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    Body attack = createDefaultEAttack(player.getPosition().x, player.getPosition().y, 2.5f, world, facingDirection);
                    attack.setUserData(String.format("Player%d-eAttack", playerNumber));
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            world.destroyBody(attack);
                        }
                    }, 0.8f);
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            eAttackCount--;
                        }
                    }, eAttackDelay);
                }
            }, 0.2f);

        }
    }

    @Override
    public void useQ(Body player) {

    }

    @Override
    public float generateDamage(int attackType) {
        return getAttackScale(attackType) * ATK;
    }

    @Override
    public void takeDamage(DamageResult damage, Vector2 hitPosition) {
        damage.damage = (damage.damage - (damage.damage * DEF_SCALE));
        currentHealth -= damage.damage;

        damageWriter.spawn(hitPosition, damage);

        if (currentHealth <= 0) {
            currentHealth = 0;
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
