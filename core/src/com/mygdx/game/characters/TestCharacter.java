package com.mygdx.game.characters;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.views.MainScreen;

import static com.mygdx.game.views.MainScreen.WORLD_HEIGHT;

public class TestCharacter extends Character {
    public static final float MAX_HP = 1000;
    public static final float ATK = 100;
    public static final float DEF_SCALE = .1f;
    public static final float NORMAL_ATTACK_SCALE = .75f;
    public static final float E_ATTACK_SCALE = 1f;
    public static final float Q_ATTACK_SCALE = 1f;

    private final World world;
    private final MainScreen screen;

    private final float attackDelay = 0.2f;
    private final int playerNumber;
    private float attackCount = 0;
    private float currentHealth = MAX_HP;

    public TestCharacter(World world, int playerNumber, MainScreen screen) {
        this.world = world;
        this.playerNumber = playerNumber;
        this.screen = screen;
    }

    @Override
    public Body createPlayer(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        PolygonShape boxShape = new PolygonShape();
        float height = WORLD_HEIGHT / 20f;
        boxShape.setAsBox(height / 2f, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.density = 1f;

        Body player = world.createBody(bodyDef);
        player.setUserData(String.format("Player%d", playerNumber));
        player.createFixture(fixtureDef);

        // Установка фильтра столкновений для игрока
        Filter filter = new Filter();
        filter.groupIndex = -1; // Устанавливаем одинаковый groupIndex для игроков
        player.getFixtureList().get(0).setFilterData(filter);

        boxShape.dispose();

        return player;
    }

    @Override
    public void useNormalAttack(Body player) {
        if (attackCount < 1) {
            attackCount++;
            Body attack = createAttack(player.getPosition().x, player.getPosition().y, 1.25f);

            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    world.destroyBody(attack);
                }
            }, 0.05f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    attackCount--;
                }
            }, attackDelay);
        }
    }

    @Override
    public void useE(Body player) {
        Body attack = createAttack(player.getPosition().x, player.getPosition().y, 1f);

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                world.destroyBody(attack);
            }
        }, 0.05f);
    }

    @Override
    public void useQ(Body player) {
        Body attack = createAttack(player.getPosition().x, player.getPosition().y, 1f);

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                world.destroyBody(attack);
            }
        }, 0.05f);
    }

    @Override
    public float generateDamage(int attackType) {
        return getAttackScale(attackType) * ATK;
    }

    @Override
    public void takeDamage(float damage) {
        currentHealth -= (damage - (damage * DEF_SCALE));
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
        return "images/player.png";
    }

    private Body createAttack(float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body sensorBody = world.createBody(bodyDef);
        sensorBody.setUserData(String.format("Player%d-attack", playerNumber));

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        fixtureDef.isSensor = true;
        fixtureDef.shape = circleShape;

        sensorBody.createFixture(fixtureDef);

        circleShape.dispose();

        return sensorBody;
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
