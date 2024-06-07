package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.factory.DamageText;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.factory.effects.Effect;
import com.mygdx.game.utils.DamageResult;
import com.mygdx.game.views.MainScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static com.mygdx.game.factory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

public abstract class Player {
    protected int maxJumps = 2;
    protected final World world;
    protected final MainScreen screen;
    protected final int playerNumber;
    protected final ControlScheme cs;
    protected final Body body;
    protected float MAX_HP;
    protected float ATK;
    protected float DEF_SCALE;
    protected float NORMAL_ATTACK_SCALE;
    protected float E_ATTACK_SCALE;
    protected float Q_ATTACK_SCALE;
    protected float zoom;
    protected float attackDelay;
    protected float eAttackDelay;
    protected float eAttackAnimationTime;
    protected float speed = 10;
    protected float dashDuration = .2f;
    protected float dashSpeed = 20f;
    protected float dashDelay = 1f;
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
    protected Animation<TextureRegion> normalAttackAnimation;
    protected Animation<TextureRegion> eAttackAnimation;
    protected Animation<TextureRegion> dashAnimation;
    protected Animation<TextureRegion> jumpDashAnimation;
    protected Animation<TextureRegion> deathAnimation;

    protected final Set<Animation<TextureRegion>> unbreakableAnimations;
    protected float stateTime = 0f;
    protected Animation<TextureRegion> currentAnimation;
    protected float HIT_ANIMATION_TIME;

    protected Timer timer;
    protected ArrayList<Effect> effectList = new ArrayList<>();


    public Player(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        this.world = world;
        this.screen = screen;
        this.playerNumber = playerNumber;
        this.damageWriter = new DamageText(playerNumber);
        this.cs = cs;
        isFacingRight = playerNumber == 1;
        body = createDefaultPlayer(x, y, world, playerNumber);

        setAnimations();
        currentAnimation = idleAnimation;
        unbreakableAnimations = Set.of(normalAttackAnimation, eAttackAnimation, hitAnimation, deathAnimation);

        timer = new Timer();
    }

    public float getMaxHp() {
        return MAX_HP;
    }

    public float getHP() {
        return currentHealth;
    }

    public Body getBody() {
        return body;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public float getSpeed() {
        return speed;
    }

    public ArrayList<Effect> getEffectList() {
        return effectList;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setAttackCount(float attackCount) {
        this.attackCount = attackCount;
    }

    public void setEAttackCount(float eAttackCount) {
        this.eAttackCount = eAttackCount;
    }

    public void setMaxJumps(int maxJumps) {
        this.maxJumps = maxJumps;
    }


    protected abstract void setAnimations();

    protected abstract void createNormalAttack();    // configurable depending on animations

    protected abstract void createEAttack();    // configurable depending on animations

    protected abstract Effect addEffect(ATTACK_TYPE attackType);


    protected void useNormalAttack() {
        if (currentAnimation == hitAnimation || currentAnimation == eAttackAnimation) return;
        if (attackCount < 1) {
            attackCount++;
            stateTime = 0f;
            currentAnimation = normalAttackAnimation;
            scheduleAnimation(idleAnimation, attackDelay); // assuming that `attackDelay` is equal to the animation time
            createNormalAttack();
        }
    }

    protected void useE() {
        if (currentAnimation == hitAnimation || currentAnimation == normalAttackAnimation) return;
        if (eAttackCount < 1) {
            eAttackCount++;
            stateTime = 0f;
            currentAnimation = eAttackAnimation;
            scheduleAnimation(idleAnimation, eAttackAnimationTime);
            createEAttack();
        }
    }

    protected void useQ() {
    }

    public DamageResult generateDamage(ATTACK_TYPE attackType) {
        boolean isCritical = body.getLinearVelocity().y < 0;
        float damage = getAttackScale(attackType) * ATK;
        if (isCritical) damage *= 2;
        DamageResult damageResult = new DamageResult(damage, isCritical);
        damageResult.effect = addEffect(attackType);
        return damageResult;
    }

    public void takeDamage(DamageResult damage) {
        damage.value = isDashing ? (damage.value / DEF_SCALE) / 2f : (damage.value / DEF_SCALE);
        if (currentHealth > 0) damageWriter.spawn(body.getPosition(), damage);
        currentHealth -= damage.value;

        if (damage.getEffect() != null) {
            damage.getEffect().run(this);
            effectList.add(damage.getEffect());
        }

        if (currentHealth <= 0) {
            if (currentAnimation != deathAnimation) stateTime = 0;
            currentAnimation = deathAnimation;
            currentHealth = 0;
            return;
        }

        if (unbreakableAnimations.contains(currentAnimation) && (currentAnimation != hitAnimation)) return;
        stateTime = 0;
        currentAnimation = hitAnimation;
        scheduleAnimation(idleAnimation, HIT_ANIMATION_TIME);
    }

    protected float getAttackScale(ATTACK_TYPE attack) {
        return switch (attack) {
            case NORMAL_ATTACK -> NORMAL_ATTACK_SCALE;
            case E_ATTACK -> E_ATTACK_SCALE;
            case Q_ATTACK -> Q_ATTACK_SCALE;
        };
    }

    public void update(SpriteBatch sb) {
        for (Iterator<Effect> iterator = effectList.iterator(); iterator.hasNext(); ) {
            Effect effect = iterator.next();
            if (effect.isDone()) iterator.remove();
            else effect.update(sb, this);
        }

        removeDeadFixtures(this);
        damageWriter.render(sb);
        updateFacingDirection();

        // set animation according to player movement if not locked animation
        if (!unbreakableAnimations.contains(currentAnimation)) {
            if (isDashing) {
                if (body.getLinearVelocity().y == 0) currentAnimation = dashAnimation;
                else currentAnimation = jumpDashAnimation;
            } else if (body.getLinearVelocity().y > 0) currentAnimation = jumpAnimation;
            else if (body.getLinearVelocity().y < 0) currentAnimation = fallAnimation;
            else if (body.getLinearVelocity().x != 0) currentAnimation = runAnimation;
            else currentAnimation = idleAnimation;
        }

        // render animation
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, !(currentHealth <= 0));

        float width = currentFrame.getRegionWidth() / WORLD_HEIGHT * zoom;
        float height = currentFrame.getRegionHeight() / WORLD_HEIGHT * zoom;
        if (isFacingRight)
            sb.draw(currentFrame, body.getPosition().x - width / 2f,
                    body.getPosition().y - height / 2f,
                    width, height);
        else
            sb.draw(currentFrame, body.getPosition().x + width / 2f,
                    body.getPosition().y - height / 2f,
                    -width, height);

        // handle input
        if (currentHealth <= 0) {
            body.setLinearVelocity(body.getLinearVelocity().y == 0 ? 0 : body.getLinearVelocity().x,
                    body.getLinearVelocity().y);
            return;
        }
        handleInput();
    }

    protected void handleInput() {
        if (currentHealth <= 0) return;

        // left and right
        int velX = 0;
        if (Gdx.input.isKeyPressed(cs.moveLeftKey)) velX = -1;
        else if (Gdx.input.isKeyPressed(cs.moveRightKey)) velX = 1;
        if (Gdx.input.isKeyPressed(cs.moveLeftKey) && Gdx.input.isKeyPressed(cs.moveRightKey)) velX = 0;

        // up
        if (Gdx.input.isKeyJustPressed(cs.jumpKey) && jumpCounter < maxJumps) {
            jumpCounter++;
            if (playerNumber == 2) {
                System.out.println(jumpCounter + " " + maxJumps);
            }
            float force = body.getMass() * 18000;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
        }
        if (body.getLinearVelocity().y == 0) {
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (body.getLinearVelocity().y == 0) jumpCounter = 0;
                }
            }, Gdx.graphics.getDeltaTime());
        }

        // apply force (wtf is going on here?)
        body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y < 10
                ? body.getLinearVelocity().y : 10);
        if (!isDashing) {
            body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);
        }

        // dash
        if (!isDashing && Gdx.input.isKeyJustPressed(cs.dashKey)) dash();

        //attacks
        if (Gdx.input.isKeyJustPressed(cs.attackKey)) useNormalAttack();
        else if (Gdx.input.isKeyJustPressed(cs.eKey)) useE();
        else if (Gdx.input.isKeyJustPressed(cs.qkey)) useQ();
    }

    protected void dash() {
        if (!isDashingAvailable) return;
        isDashing = true;
        isDashingAvailable = false;
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                isDashing = false;
            }
        }, dashDuration);
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                isDashingAvailable = true;
            }
        }, dashDelay);

        float impulseX = body.getMass() * dashSpeed;
        impulseX = isFacingRight ? impulseX : -impulseX;
        body.applyLinearImpulse(new Vector2(impulseX, 0), body.getPosition(), true);
    }

    protected void updateFacingDirection() {
        if (isDashing || unbreakableAnimations.contains(currentAnimation)) return;
        if (body.getLinearVelocity().x > 0) isFacingRight = true;
        if (body.getLinearVelocity().x < 0) isFacingRight = false;
    }

    protected void scheduleAnimation(Animation<TextureRegion> animation, float delaySec) {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                if (currentAnimation == deathAnimation) return;
                stateTime = 0f;
                currentAnimation = animation;
            }
        }, delaySec - Gdx.graphics.getDeltaTime() * 1.1f);
    }
}
