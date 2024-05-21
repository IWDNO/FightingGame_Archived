package com.mygdx.game.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Character;
import com.mygdx.game.characters.TestCharacter;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.utils.PlayerStates;

public class Player {
    private ControlScheme cs;
    private TestCharacter character;
    private final Body player;

    private final float SPEED = 10;
    private final float DASH_DURATION = .2f;
    private final float DASH_SPEED = 20f;

    private int jumpCounter = 0;
    public float currentHealth;
    private boolean isDashing = false;
    private boolean isFacingRight;
    private boolean isDashingAvailable = true;

    private final Sprite playerSprite;
    private float stateTime = 0f;
    private int state = PlayerStates.IDLE;
    private Animation<TextureRegion> currentAnimation;
    private boolean block_animation = false;


    public Player (TestCharacter character, float x, float y, ControlScheme cs, int playerNumber) {
        this.cs = cs;
        this.character = character;

        currentHealth = character.getHP();
        player = character.createPlayer(x, y);
        isFacingRight = playerNumber == 1;

        playerSprite = new Sprite(new Texture(character.getTexture()));
        playerSprite.setSize(1, 2);
    }

    public void update(SpriteBatch sb) {
        updateFacingDirection();

        stateTime += Gdx.graphics.getDeltaTime();
        setAnimation();


        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        float w = 1600;
        float h = 900;
        float width = 900f / 20f * 16f / 9f * 2.5f; //FIXME временно 5x
        float height = 900f / 10f * 2.5f;
        if (isFacingRight)
        sb.draw(currentFrame, w/2 + player.getPosition().x * w/20 * 9/16 - width/2,
                h/2 + player.getPosition().y * h/20 - height/2,
                width, height);
        else
            sb.draw(currentFrame, w/2 + player.getPosition().x * w/20 * 9/16 + width/2,
                    h/2 + player.getPosition().y * h/20 - height/2,
                    -width, height);

        playerSprite.setPosition(player.getPosition().x - playerSprite.getWidth() / 2,
                player.getPosition().y - playerSprite.getHeight() / 2);
        playerSprite.draw(sb);

        character.update(sb);

        handleInput();
    }

    public void handleInput() {
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
        player.setLinearVelocity(player.getLinearVelocity().x, player.getLinearVelocity().y < 10 ? player.getLinearVelocity().y : 10);
        if (!isDashing) {
            player.setLinearVelocity(velX * SPEED, player.getLinearVelocity().y);
        }

        // check for movement
        if (isDashing && player.getLinearVelocity().y == 0) state = PlayerStates.DASH;
        else if (isDashing && player.getLinearVelocity().y != 0) state = PlayerStates.JUMP_DASH;
        else if (player.getLinearVelocity().y > 0) state = PlayerStates.JUMP;
        else if (player.getLinearVelocity().y < 0) state = PlayerStates.FALL;
        else if (player.getLinearVelocity().x != 0) state = PlayerStates.RUN;
        else state = PlayerStates.IDLE;

        // dash
        if (!isDashing && Gdx.input.isKeyJustPressed(cs.dashKey)) {
            dash();
        }

        //attacks
        if (Gdx.input.isKeyJustPressed(cs.attackKey)) {
            if (character.attackCount < 1) {
                currentAnimation = character.attack1Animation;
                stateTime = 0f;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        currentAnimation = character.idleAnimation;
                    }
                }, .8f);
                character.useNormalAttack(player, isFacingRight);
            }
        }
        if (Gdx.input.isKeyJustPressed(cs.eKey)) {
            currentAnimation = character.attack2Animation;
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    currentAnimation = character.idleAnimation;
                }
            }, .6f);
            character.useE(player);
        }
        if (Gdx.input.isKeyJustPressed(cs.qkey)) {
            character.useQ(player);
        }
    }

    private void dash() {
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
        }, DASH_DURATION);
        dashTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                isDashingAvailable = true;
            }
        }, .5f);

        float impulseX = player.getMass() * DASH_SPEED;
        impulseX = isFacingRight ? impulseX : -impulseX;
        player.applyLinearImpulse(new Vector2(impulseX, 0), player.getPosition(), true);
    }

    private void updateFacingDirection() {
        if (isDashing)
            return;

        if (player.getLinearVelocity().x > 0) {
            isFacingRight = true;
        } else if (player.getLinearVelocity().x < 0) {
            isFacingRight = false;
        }

        playerSprite.setFlip(!isFacingRight, false);
    }

    public float generateDamage(int attackType) {
        return character.generateDamage(attackType);
    }

    public void takeDamage(float damage, Vector2 hitPosition) {
        stateTime = 0f;
        currentAnimation = character.hitAnimation;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                currentAnimation = character.idleAnimation;
            }
        }, 0.4f);

        damage = isDashing ? damage / 2 : damage;
        character.takeDamage(damage, hitPosition);
        currentHealth = character.getHP();

        playerSprite.setColor(1,0,0,1);
        new Timer().scheduleTask(new Timer.Task(){
            @Override
            public void run() {
                playerSprite.setColor(1,1,1,1);
            }
        }, 0.1f);
    }

    public void setAnimation() {
        if (currentAnimation == character.hitAnimation
                || currentAnimation == character.attack1Animation
                || currentAnimation == character.attack2Animation) return;


        switch (state) {
            case PlayerStates.RUN:
                currentAnimation = character.runAnimation;
                break;
            case PlayerStates.ATTACK1:
                currentAnimation = character.attack1Animation;
                break;
            case PlayerStates.ATTACK2:
                currentAnimation = character.attack2Animation;
                break;
            case PlayerStates.DEATH:
                break;
            case PlayerStates.FALL:
                currentAnimation = character.fallAnimation;
                break;
            case PlayerStates.HIT:
                currentAnimation = character.hitAnimation;
                break;
            case PlayerStates.JUMP:
                currentAnimation = character.jumpAnimation;
                break;
            case PlayerStates.DASH:
                currentAnimation = character.dashAnimation;
                break;
            case PlayerStates.JUMP_DASH:
                currentAnimation = character.jumpDashAnimation;
                break;
            default:
                currentAnimation = character.idleAnimation;
                break;
        }
    }
}
