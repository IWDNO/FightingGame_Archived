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
import com.mygdx.game.utils.PlayerStates;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.BodyFactory.BodyFactory.*;

public class SaiHan extends TestCharacter {
    public static final float MAX_HP = 10000;
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
    private float zoom = 6f;

    private DamageText damageWriter;

    protected static Animation<TextureRegion> idleAnimation;
    protected static Animation<TextureRegion> runAnimation;
    protected static Animation<TextureRegion> jumpAnimation;
    protected static Animation<TextureRegion> fallAnimation;
    protected static Animation<TextureRegion> hitAnimation;
    protected static Animation<TextureRegion> attack1Animation;
    protected static Animation<TextureRegion> attack2Animation;
    protected static Animation<TextureRegion> dashAnimation;
    protected static Animation<TextureRegion> jumpDashAnimation;

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
        return 0.8f;
    }
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(4,.1f, 1, "images/Sprites saihan/Idle.png");
        runAnimation = AnimationFactory.create(8,.1f, 1, "images/Sprites saihan/Run.png");
        jumpAnimation = AnimationFactory.create(2,.1f, 1, "images/Sprites saihan/Jump.png");
        fallAnimation = AnimationFactory.create(2,.1f, 1, "images/Sprites saihan/Fall.png");
        hitAnimation = AnimationFactory.create(3,.1f, 1, "images/Sprites saihan/Hit.png");
        attack1Animation = AnimationFactory.create(4,.1f, 1, "images/Sprites saihan/Attack1.png");
        attack2Animation = AnimationFactory.create(4,.1f, 1, "images/Sprites saihan/Attack2.png");
        dashAnimation = AnimationFactory.create(8,.1f, 1, "images/Sprites saihan/Dash.png");
        jumpDashAnimation = AnimationFactory.create(2,.1f, 1, "images/Sprites saihan/JumpDash.png");
    }

    public SaiHan(World world, int playerNumber, MainScreen screen) {
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
