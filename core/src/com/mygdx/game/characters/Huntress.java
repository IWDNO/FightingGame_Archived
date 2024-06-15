package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.factory.AnimationFactory;
import com.mygdx.game.utils.ControlScheme;
import com.mygdx.game.factory.effects.Effect;
import com.mygdx.game.factory.effects.InfiniteJumps;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.factory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

public class Huntress extends Player {

    public Huntress(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        super(world, playerNumber, screen, cs, x ,y);
        this.MAX_HP = 1200;
        this.ATK = 110;
        this.DEF_SCALE = 1.1f;
        this.NORMAL_ATTACK_SCALE = 1f;
        this.E_ATTACK_SCALE = 1.5f;
        this.Q_ATTACK_SCALE = 1f;
        this.zoom = 1.4f;
        this.attackDelay = .5f;
        this.eAttackDelay = 8f;
        this.eAttackAnimationTime = .5f;
        this.currentHealth = MAX_HP;
        this.HIT_ANIMATION_TIME = .3f;

        this.swing1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/huntress/swing1.mp3"));
        this.swing2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/huntress/swing2.mp3"));
        this.hit1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/huntress/hit1.mp3"));
        this.hit2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/huntress/hit2.mp3"));
    }

    @Override
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(8, .1f, 1, "images/Huntress/Idle.png");
        runAnimation = AnimationFactory.create(8, .1f, 1, "images/Huntress/Run.png");
        jumpAnimation = AnimationFactory.create(2, .1f, 1, "images/Huntress/Jump.png");
        fallAnimation = AnimationFactory.create(2, .1f, 1, "images/Huntress/Fall.png");
        hitAnimation = AnimationFactory.create(3, .1f, 1, "images/Huntress/Hit.png");
        normalAttackAnimation = AnimationFactory.create(5, .1f, 1, "images/Huntress/Attack1.png");
        eAttackAnimation = AnimationFactory.create(5, .1f, 1, "images/Huntress/Attack2.png");
        dashAnimation = AnimationFactory.create(8, .1f, 1, "images/Huntress/Dash.png");
        jumpDashAnimation = AnimationFactory.create(2, .1f, 1, "images/Huntress/JumpDash.png");
        deathAnimation = AnimationFactory.create(8, .15f, 1, "images/Huntress/Death.png");
    }

    @Override
    protected void createNormalAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(Huntress.this, 2.25f, 1.3f, .8f, ATTACK_TYPE.NORMAL_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {removeAttackSensor(Huntress.this, ATTACK_TYPE.NORMAL_ATTACK);
                    }
                }, 0.1f);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        attackCount--;
                    }
                }, attackDelay);
            }
        }, 0.3f);
    }

    @Override
    protected void createEAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(Huntress.this, 2.5f, 1f, .75f, ATTACK_TYPE.E_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {removeAttackSensor(Huntress.this, ATTACK_TYPE.E_ATTACK);
                    }
                }, 0.1f);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        eAttackCount--;
                    }
                }, eAttackDelay);
                InfiniteJumps jumps = new InfiniteJumps(5);
                jumps.run(Huntress.this);
                effectList.add(jumps);
            }
        }, 0.3f);
    }

    @Override
    protected Effect addEffect(ATTACK_TYPE attackType) {
        return null;
    }
}
