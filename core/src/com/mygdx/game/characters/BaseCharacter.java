package com.mygdx.game.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.animator.DamageText;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.utils.PlayerStates;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.BodyFactory.BodyFactory.*;

public abstract class BaseCharacter {
    protected float MAX_HP;
    protected float ATK;
    protected float DEF_SCALE;
    protected float NORMAL_ATTACK_SCALE;
    protected float E_ATTACK_SCALE;
    protected float Q_ATTACK_SCALE;
    protected float zoom;
    protected float attackDelay;
    protected float eAttackDelay;

    protected final World world;
    protected final MainScreen screen;
    protected final int playerNumber;

    protected float attackCount = 0;
    protected float eAttackCount = 0;
    protected float currentHealth;

    protected DamageText damageWriter;

    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> jumpAnimation;
    protected Animation<TextureRegion> fallAnimation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> attack1Animation;
    protected Animation<TextureRegion> attack2Animation;
    protected Animation<TextureRegion> dashAnimation;
    protected Animation<TextureRegion> jumpDashAnimation;
    protected Animation<TextureRegion> deathAnimation;

    public BaseCharacter(World world, int playerNumber, MainScreen screen) {
        this.world = world;
        this.screen = screen;
        this.playerNumber = playerNumber;
        this.damageWriter = new DamageText(playerNumber);
        setAnimations();
    }

    protected abstract void setAnimations();

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
        return eAttackDelay;
    }

    public float getMaxHp() {
        return MAX_HP;
    }

    public float getDeathAnimationTime() {
        return 1.2f;
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

    public void useQ(Body player) {

    }

    public float generateDamage(int attackType) {
        return getAttackScale(attackType) * ATK;
    }

    public void takeDamage(DamageResult damage, Vector2 hitPosition) {
        damage.damage = (damage.damage - (damage.damage * DEF_SCALE));
        currentHealth -= damage.damage;

        damageWriter.spawn(hitPosition, damage);

        if (currentHealth <= 0) {
            currentHealth = 0;
        }
    }

    public float getHP() {
        return currentHealth;
    }

    public String getTexture() {
        return "images/Wizard Pack/Run.png";
    }

    public void update(SpriteBatch sb) {
        damageWriter.render(sb);
    }

    public float getAttackScale(int attack) {
        switch (attack) {
            case 1:
                return NORMAL_ATTACK_SCALE;
            case 2:
                return E_ATTACK_SCALE;
            case 3:
                return Q_ATTACK_SCALE;
            default:
                return 1f;
        }
    }
}
