package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.factory.AnimationFactory;
import com.mygdx.game.utils.ControlScheme;
import com.mygdx.game.factory.effects.DoT;
import com.mygdx.game.factory.effects.Effect;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.factory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

public class SaiHan extends Player {

    public SaiHan(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        super(world, playerNumber, screen, cs, x, y);
        this.MAX_HP = 1000;
        this.ATK = 100;
        this.DEF_SCALE = 1f;
        this.NORMAL_ATTACK_SCALE = .75f;
        this.E_ATTACK_SCALE = 1.5f;
        this.Q_ATTACK_SCALE = 1f;
        this.zoom = 1.1f;
        this.attackDelay = .4f;
        this.eAttackDelay = 8f;
        this.eAttackAnimationTime = .4f;
        this.currentHealth = MAX_HP;
        this.HIT_ANIMATION_TIME = .3f;
        this.speed *= 1.25f;
        this.dashSpeed *= 1.25f;

        this.swing1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/saihan/swing1.mp3"));
        this.swing2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/saihan/swing2.mp3"));
        this.hit1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/saihan/hit1.mp3"));
        this.hit2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/saihan/hit2.mp3"));
    }

    @Override
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(4, .1f, 1, "images/SaiHan/Idle.png");
        runAnimation = AnimationFactory.create(8, .1f, 1, "images/SaiHan/Run.png");
        jumpAnimation = AnimationFactory.create(2, .1f, 1, "images/SaiHan/Jump.png");
        fallAnimation = AnimationFactory.create(2, .1f, 1, "images/SaiHan/Fall.png");
        hitAnimation = AnimationFactory.create(3, .1f, 1, "images/SaiHan/Hit.png");
        normalAttackAnimation = AnimationFactory.create(4, .1f, 1, "images/SaiHan/Attack1.png");
        eAttackAnimation = AnimationFactory.create(4, .1f, 1, "images/SaiHan/Attack2.png");
        dashAnimation = AnimationFactory.create(8, .1f, 1, "images/SaiHan/Dash.png");
        jumpDashAnimation = AnimationFactory.create(2, .1f, 1, "images/SaiHan/JumpDash.png");
        deathAnimation = AnimationFactory.create(7, .15f, 1, "images/SaiHan/Death.png");
    }

    @Override
    protected void createNormalAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(SaiHan.this, 1.75f, 2.75f, 0, ATTACK_TYPE.NORMAL_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        removeAttackSensor(SaiHan.this, ATTACK_TYPE.NORMAL_ATTACK);
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
                addAttackSensor(SaiHan.this, 2.9f, 2.1f, 1f, ATTACK_TYPE.E_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        removeAttackSensor(SaiHan.this, ATTACK_TYPE.E_ATTACK);
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
            return new DoT(5f, 33f);
        }
        return null;
    }
}
