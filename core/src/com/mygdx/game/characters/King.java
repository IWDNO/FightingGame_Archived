package com.mygdx.game.characters;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.animator.AnimationFactory;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.views.MainScreen;

public class King extends BaseCharacter {

    public King(World world, int playerNumber, MainScreen screen, ControlScheme cs, int x, int y) {
        super(world, playerNumber, screen, cs, x, y);
        this.MAX_HP = 100;
        this.ATK = 500;
        this.DEF_SCALE = 1.1f;
        this.NORMAL_ATTACK_SCALE = .75f;
        this.E_ATTACK_SCALE = 1.25f;
        this.Q_ATTACK_SCALE = 1f;
        this.zoom = 1.25f;
        this.attackDelay = .4f;
        this.eAttackDelay = 5f;
        this.eAttackAnimationDelay = .4f;
        this.currentHealth = MAX_HP;
        this.HIT_ANIMATION_TIME = .4f;
    }
    @Override
    protected void setAnimations() {
        idleAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Idle.png");
        runAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Run.png");
        jumpAnimation = AnimationFactory.create(2, .1f, 1, "images/King/Jump.png");
        fallAnimation = AnimationFactory.create(2, .1f, 1, "images/King/Fall.png");
        hitAnimation = AnimationFactory.create(4, .1f, 1, "images/King/Hit.png");
        attack1Animation = AnimationFactory.create(4, .1f, 1, "images/King/Attack1.png");
        attack2Animation = AnimationFactory.create(4, .1f, 1, "images/King/Attack2.png");
        dashAnimation = AnimationFactory.create(8, .1f, 1, "images/King/Dash.png");
        jumpDashAnimation = AnimationFactory.create(2, .1f, 1, "images/King/JumpDash.png");
        deathAnimation = AnimationFactory.create(6, .15f, 1, "images/King/Death.png");
    }
}
