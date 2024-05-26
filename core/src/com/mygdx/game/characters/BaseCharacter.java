package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.animator.DamageText;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.views.MainScreen;

import java.util.Set;

import static com.mygdx.game.BodyFactory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

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
    protected float eAttackAnimationDelay;
    protected float speed = 10;
    protected float dashDuration = .2f;
    protected float dashSpeed = 20f;
    protected float dashDelay = .5f;

    protected final World world;
    protected final MainScreen screen;
    protected final int playerNumber;
    protected final ControlScheme cs;
    protected final Body player;

    protected float attackCount = 0;
    protected float eAttackCount = 0;
    protected float currentHealth;
    protected int jumpCounter = 0;
    protected boolean isDashing = false;
    protected boolean isFacingRight;
    protected boolean isDashingAvailable = true;

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

    protected Set<Animation<TextureRegion>> unbreakableAnimations;
    protected float stateTime = 0f;
    protected Animation<TextureRegion> currentAnimation;

    protected float HIT_ANIMATION_TIME = .4f;


    public BaseCharacter(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        this.world = world;
        this.screen = screen;
        this.playerNumber = playerNumber;
        this.damageWriter = new DamageText(playerNumber);
        this.cs = cs;
        player = createPlayer(x, y);
        isFacingRight = playerNumber == 1;
        setAnimations();
        currentAnimation = idleAnimation;
        unbreakableAnimations = Set.of(attack1Animation, attack2Animation, hitAnimation, deathAnimation);
    }

    public float getMaxHp() {
        return MAX_HP;
    }

    public float getHP() {
        return currentHealth;
    }


    protected abstract void setAnimations();

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

    public DamageResult generateDamage(int attackType) {
        boolean isCritical = player.getLinearVelocity().y < 0;
        float damage = getAttackScale(attackType) * ATK;
        if (isCritical) damage *= 2;

        return new DamageResult(damage, isCritical);
    }

    public void takeDamage(DamageResult damage, Vector2 hitPosition) {
        damage.value = (damage.value / DEF_SCALE);
        currentHealth -= isDashing ? damage.value / 2f : damage.value;

        damageWriter.spawn(hitPosition, damage);

        stateTime = 0f;
        if (currentHealth <= 0) {
            currentAnimation = deathAnimation;
            currentHealth = 0;
            return;
        }

        currentAnimation = hitAnimation;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                currentAnimation = idleAnimation;
            }
        }, HIT_ANIMATION_TIME);
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

    public void update(SpriteBatch sb) {
        damageWriter.render(sb);
        updateFacingDirection();

        if (unbreakableAnimations.contains(currentAnimation)) {}
        else if (isDashing && player.getLinearVelocity().y == 0) currentAnimation = dashAnimation;
        else if (isDashing && player.getLinearVelocity().y != 0) currentAnimation = jumpDashAnimation;
        else if (player.getLinearVelocity().y > 0) currentAnimation = jumpAnimation;
        else if (player.getLinearVelocity().y < 0) currentAnimation = fallAnimation;
        else if (player.getLinearVelocity().x != 0) currentAnimation = runAnimation;
        else currentAnimation = idleAnimation;

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, !(currentHealth <= 0));

        float width = currentFrame.getRegionWidth() / WORLD_HEIGHT * zoom;
        float height = currentFrame.getRegionHeight() / WORLD_HEIGHT * zoom;
        if (isFacingRight)
            sb.draw(currentFrame, player.getPosition().x - width / 2f,
                    player.getPosition().y - height / 2f,
                    width, height);
        else
            sb.draw(currentFrame, player.getPosition().x + width / 2f,
                    player.getPosition().y - height / 2f,
                    -width, height);

        if (currentHealth <= 0) {
            player.setLinearVelocity(player.getLinearVelocity().y == 0 ? 0 : player.getLinearVelocity().x,
                    player.getLinearVelocity().y);
            return;
        }

        handleInput();
    }


    public void handleInput() {
        if (currentHealth <= 0) return;
        Timer timer = new Timer();

        // left and right
        int velX = 0;
        if (Gdx.input.isKeyPressed(cs.moveLeftKey)) {
            velX = -1;
        } else if (Gdx.input.isKeyPressed(cs.moveRightKey)) {
            velX = 1;
        }
        if (Gdx.input.isKeyPressed(cs.moveLeftKey) && Gdx.input.isKeyPressed(cs.moveRightKey)) {
            velX = 0;
        }

        // up
        if (Gdx.input.isKeyJustPressed(cs.jumpKey) && jumpCounter < 2) {
            jumpCounter++;
            float force = player.getMass() * 18000;
            player.setLinearVelocity(player.getLinearVelocity().x, 0);
            player.applyLinearImpulse(new Vector2(0, force), player.getPosition(), true);
        }
        if (player.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }

        // apply force
        player.setLinearVelocity(player.getLinearVelocity().x, player.getLinearVelocity().y < 10
                ? player.getLinearVelocity().y : 10);
        if (!isDashing) {
            player.setLinearVelocity(velX * speed, player.getLinearVelocity().y);
        }

        // check for movement

        // dash
        if (!isDashing && Gdx.input.isKeyJustPressed(cs.dashKey)) {
            dash();
        }

        //attacks TODO вынести обработку анимаций в метод использования аттак (может быть)
        if (Gdx.input.isKeyJustPressed(cs.attackKey)) {
            if (attackCount < 1) {
                currentAnimation = attack1Animation;
                stateTime = 0f;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        currentAnimation = idleAnimation;
                    }
                }, attackDelay);
                useNormalAttack(player, isFacingRight);
            }
        }
        if (Gdx.input.isKeyJustPressed(cs.eKey)) {
            if (eAttackCount < 1) {
                currentAnimation = attack2Animation;
                stateTime = 0f;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        currentAnimation = idleAnimation;
                    }
                }, eAttackAnimationDelay);
                useE(player, isFacingRight);
            }
        }
        if (Gdx.input.isKeyJustPressed(cs.qkey)) {
            useQ(player);
        }
    }

    void dash() {
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
        }, dashDuration);
        dashTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                isDashingAvailable = true;
            }
        }, dashDelay);

        float impulseX = player.getMass() * dashSpeed;
        impulseX = isFacingRight ? impulseX : -impulseX;
        player.applyLinearImpulse(new Vector2(impulseX, 0), player.getPosition(), true);
    }

    private void updateFacingDirection() {
        if (isDashing) return;
        if (player.getLinearVelocity().x > 0) isFacingRight = true;
        if (player.getLinearVelocity().x < 0) isFacingRight = false;
    }
}
