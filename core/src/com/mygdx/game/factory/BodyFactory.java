package com.mygdx.game.factory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.characters.Player;
import com.mygdx.game.utils.UserData;

import static com.mygdx.game.utils.Constants.*;

public class BodyFactory {

    public static Body createDefaultPlayer(float x, float y, World world, int playerNumber) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        PolygonShape boxShape = new PolygonShape();

        boxShape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0f;

        Body player = world.createBody(bodyDef);
        Fixture playerBody = player.createFixture(fixtureDef);
        playerBody.setUserData(new UserData("Player" + playerNumber));

        // Установка фильтра столкновений для игрока
        Filter filter = new Filter();
        filter.groupIndex = -1; // Устанавливаем одинаковый groupIndex для игроков
        player.getFixtureList().get(0).setFilterData(filter);

        boxShape.dispose();

        return player;
    }

    public static void addAttackSensor(Player player, float radius, float offsetX, float offsetY, ATTACK_TYPE attackType) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;

        offsetX *= player.isFacingRight() ? 1 : -1;
        circleShape.setPosition(new Vector2(offsetX, offsetY));

        Fixture sensorFixture = player.getBody().createFixture(fixtureDef);
        sensorFixture.setUserData(new UserData("Player" + player.getPlayerNumber() + "-" + attackType));

        circleShape.dispose();
    }


//    public static void addAttackSensor1(Body player, float radius, boolean isFacingRight, int playerNumber, float offsetX, float offsetY) {
//        CircleShape circleShape = new CircleShape();
//        circleShape.setRadius(radius);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = circleShape;
//        fixtureDef.isSensor = true;
//
//        offsetX += isFacingRight ? 2f : -2f;
//        circleShape.setPosition(new Vector2(offsetX, offsetY));
//
//        Fixture sensorFixture = player.createFixture(fixtureDef);
//        sensorFixture.setUserData(new UserData("Player" + playerNumber + "-eAttack"));
//
//        circleShape.dispose();
//    }

    public static void removeAttackSensor(Player player, ATTACK_TYPE attackType) {
        for (Fixture fixture : player.getBody().getFixtureList()) {
            if (("Player" + player.getPlayerNumber() + "-" + attackType).equals(((UserData) fixture.getUserData()).getName())) {
                player.getBody().destroyFixture(fixture);
                break;
            }
        }
    }

//    public static void removeAttackSensor1(Body player, int playerNumber) {
//        for (Fixture fixture : player.getFixtureList()) {
//            if (("Player" + playerNumber + "-eAttack").equals(((UserData) fixture.getUserData()).getName())) {
//                player.destroyFixture(fixture);
//                break;
//            }
//        }
//    }

    public static void removeDeadFixtures(Player player) {
        for (Fixture fixture : player.getBody().getFixtureList()) {
            UserData fixtureData = (UserData) fixture.getUserData();
            if (fixtureData.isDead())
                player.getBody().destroyFixture(fixture);
        }
    }

    public static Body createDefaultAttack(float x, float y, float radius, World world, boolean isFacingRight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(isFacingRight ? x + 2f : x - 2f, y);

        Body sensorBody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        fixtureDef.isSensor = true;
        fixtureDef.shape = circleShape;

        sensorBody.createFixture(fixtureDef);

        circleShape.dispose();

        return sensorBody;
    }

    public static Body createDefaultEAttack(float x, float y, float radius, World world, boolean isFacingRight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(isFacingRight ? x + 3f : x - 3f, y);

        Body sensorBody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        fixtureDef.isSensor = true;
        fixtureDef.shape = circleShape;

        sensorBody.createFixture(fixtureDef);

        circleShape.dispose();

        return sensorBody;
    }

    public static void createWorldBounds(float halfWorldWidth, float halfWorldHeight, World world) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        Vector2 upLeft = new Vector2(-halfWorldWidth, halfWorldHeight),
                upRight = new Vector2(halfWorldWidth, halfWorldHeight),
                bottomLeft = new Vector2(-halfWorldWidth, -halfWorldHeight),
                bottomRight = new Vector2(halfWorldWidth, -halfWorldHeight);

        ChainShape shape = new ChainShape();
        shape.createChain(new Vector2[]{upLeft, bottomLeft, bottomRight, upRight, upLeft});
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0;

        world.createBody(bodyDef).createFixture(fixtureDef);

        shape.dispose();
    }
}
