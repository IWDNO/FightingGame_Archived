package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.factory.AnimationFactory;
import com.mygdx.game.utils.ControlScheme;
import com.mygdx.game.factory.effects.Disarm;
import com.mygdx.game.factory.effects.Effect;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.factory.BodyFactory.*;
import static com.mygdx.game.utils.Constants.*;

public class HeroKnight extends Player {

    public HeroKnight(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        super(world, playerNumber, screen, cs, x ,y);
        this.MAX_HP = 750;
        this.ATK = 120;
        this.DEF_SCALE = 1.5f;
        this.NORMAL_ATTACK_SCALE = 1.1f;
        this.E_ATTACK_SCALE = 2f;
        this.Q_ATTACK_SCALE = 1f;
        this.zoom = 1.25f;
        this.attackDelay = .7f;
        this.eAttackDelay = 8f;
        this.eAttackAnimationTime = .7f;
        this.currentHealth = MAX_HP;
        this.HIT_ANIMATION_TIME = .3f;

        this.swing1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/heroknight/swing1.mp3"));
        this.swing2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/heroknight/swing2.mp3"));
        this.hit1Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/heroknight/hit1.mp3"));
        this.hit2Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/heroknight/hit2.mp3"));
    }

    @Override
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(11, .1f, 1, "images/HeroKnight/Idle.png");
        runAnimation = AnimationFactory.create(8, .1f, 1, "images/HeroKnight/Run.png");
        jumpAnimation = AnimationFactory.create(3, .1f, 1, "images/HeroKnight/Jump.png");
        fallAnimation = AnimationFactory.create(3, .1f, 1, "images/HeroKnight/Fall.png");
        hitAnimation = AnimationFactory.create(4, .1f, 1, "images/HeroKnight/Hit.png");
        normalAttackAnimation = AnimationFactory.create(7, .1f, 1, "images/HeroKnight/Attack1.png");
        eAttackAnimation = AnimationFactory.create(7, .1f, 1, "images/HeroKnight/Attack2.png");
        dashAnimation = AnimationFactory.create(8, .1f, 1, "images/HeroKnight/Dash.png");
        jumpDashAnimation = AnimationFactory.create(3, .1f, 1, "images/HeroKnight/JumpDash.png");
        deathAnimation = AnimationFactory.create(11, .15f, 1, "images/HeroKnight/Death.png");
    }

    @Override
    protected void createNormalAttack() {
        timer.scheduleTask(new Timer.Task() {
            public void run() {
                addAttackSensor(HeroKnight.this, 1.75f, .75f, .5f, ATTACK_TYPE.NORMAL_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {removeAttackSensor(HeroKnight.this, ATTACK_TYPE.NORMAL_ATTACK);
                    }
                }, 0.2f);
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
                addAttackSensor(HeroKnight.this, 3f, 2.2f, .5f, ATTACK_TYPE.E_ATTACK);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {removeAttackSensor(HeroKnight.this, ATTACK_TYPE.E_ATTACK);
                    }
                }, 0.2f);
                timer.scheduleTask(new Timer.Task() {
                    public void run() {
                        eAttackCount--;
                    }
                }, eAttackDelay);
            }
        }, 0.3f);
    }

    @Override
    protected Effect addEffect(ATTACK_TYPE attackType) {
        if (attackType == ATTACK_TYPE.E_ATTACK) {
            return new Disarm(5);
        }
        return null;
    }
}
