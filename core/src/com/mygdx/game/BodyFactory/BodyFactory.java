package com.mygdx.game.BodyFactory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BodyFactory {
    public static Body createDefaultPlayer(float x, float y, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        PolygonShape boxShape = new PolygonShape();
        float height = 1f;
        boxShape.setAsBox(height / 2f, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.density = 1f;

        Body player = world.createBody(bodyDef);
        player.createFixture(fixtureDef);

        // Установка фильтра столкновений для игрока
        Filter filter = new Filter();
        filter.groupIndex = -1; // Устанавливаем одинаковый groupIndex для игроков
        player.getFixtureList().get(0).setFilterData(filter);

        boxShape.dispose();

        return player;
    }

    public static Body createDefaultAttack(float x,  float y, float radius, World world, boolean isFacingRight) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(isFacingRight ? x + 1f : x - 1f, y);

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
        shape.createChain(new Vector2[] {upLeft, bottomLeft, bottomRight, upRight, upLeft});
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0;

        world.createBody(bodyDef).createFixture(fixtureDef);

        shape.dispose();
    }
}
