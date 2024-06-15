package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.factory.AnimationFactory;
import com.mygdx.game.utils.ControlScheme;
import com.mygdx.game.factory.effects.Effect;
import com.mygdx.game.factory.effects.SwipeControls;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.factory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

public class King extends Player {

    public King(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        super(world, playerNumber, screen, cs, x, y);
        this.MAX_HP = 1100;
        this.ATK = 90;
        this.DEF_SCALE = 1.1f;
        this.NORMAL_ATTACK_SCALE = 1f;
        this.E_ATTACK_SCALE = 1.5f;
        this.Q_ATTACK_SCALE = 1f;
        this.zoom = 1.25f;
        this.attackDelay = .4f;
        this.eAttackDelay = 8f;
        this.eAttackAnimationTime = .4f;
        this.currentHealth = MAX_HP;
        this.HIT_ANIMATION_TIME = .4f;

        this.swing1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/king/swing1.mp3"));
        this.swing2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/king/swing2.mp3"));
        this.hit1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/king/hit1.mp3"));
        this.hit2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/king/hit2.mp3"));
    }

    @Override
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Idle.png");
        runAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Run.png");
        jumpAnimation = AnimationFactory.create(2, .1f, 1, "images/King/Jump.png");
        fallAnimation = AnimationFactory.create(2, .1f, 1, "images/King/Fall.png");
        hitAnimation = AnimationFactory.create(4, .1f, 1, "images/King/Hit.png");
        normalAttackAnimation = AnimationFactory.create(4, .1f, 1, "images/King/Attack1.png");
        eAttackAnimation = AnimationFactory.create(4, .1f, 1, "images/King/Attack3.png");
        dashAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Dash.png");
        jumpDashAnimation = AnimationFactory.create(2, .1f, 1, "images/King/JumpDash.png");
        deathAnimation = AnimationFactory.create(6, .15f, 1, "images/King/Death.png");
    }

    @Override
    protected void createNormalAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(King.this, 1.6f, 2.85f, 0, ATTACK_TYPE.NORMAL_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        removeAttackSensor(King.this, ATTACK_TYPE.NORMAL_ATTACK);
                    }
                }, 0.1f);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        attackCount--;
                    }
                }, attackDelay);
            }
        }, 0.2f);
    }

    @Override
    protected void createEAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(King.this, 3.25f, 1.5f, 1.4f, ATTACK_TYPE.E_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        removeAttackSensor(King.this, ATTACK_TYPE.E_ATTACK);
                    }
                }, 0.1f);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        eAttackCount--;
                    }
                }, eAttackDelay);
            }
        }, 0.2f);
    }

    @Override
    protected Effect addEffect(ATTACK_TYPE attackType) {
        if (attackType == ATTACK_TYPE.E_ATTACK) {
            return new SwipeControls(5);
        }
        return null;
    }
}
