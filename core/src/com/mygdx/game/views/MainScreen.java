package com.mygdx.game.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.FightingGame;
import com.mygdx.game.GameContactListener;
import com.mygdx.game.bodies.Player;
import com.mygdx.game.characters.Character;
import com.mygdx.game.characters.TestCharacter;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.controller.InputController;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements Screen {
    public static final float WORLD_HEIGHT = 20;

    public FightingGame parent;
    public GameContactListener contactListener;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private InputAdapter controller;
    private SpriteBatch sb;

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    public Player player1;
    public Player player2;
    private ControlScheme p1cs = new ControlScheme(
            Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.V, Input.Keys.Z, Input.Keys.X, Input.Keys.C);
    private ControlScheme p2cs = new ControlScheme(
            Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.M, Input.Keys.SLASH, Input.Keys.COMMA, Input.Keys.PERIOD);

    private Texture background;

    public MainScreen(FightingGame fg) {
        parent = fg;
        contactListener = new GameContactListener(this);
        background = new Texture("images/bg1.jpg");
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -20), false);
        debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(contactListener);

        camera = new OrthographicCamera(WORLD_HEIGHT * 16/9, WORLD_HEIGHT);
        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined);

        controller = new InputAdapter() {
            @Override
            public boolean keyDown(int i) {
                if (i == Input.Keys.ESCAPE) {
                    parent.changeScreen(FightingGame.MENU);
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(controller);

        createBounds();
        createCube(-10, -7, 2, 2);
        TestCharacter p1 = new TestCharacter(world, 1, this);
        TestCharacter p2 = new TestCharacter(world, 2, this);

        player2 = new Player(p2, 5, 0, p2cs, 2);
        player1 = new Player(p1, -5, 0, p1cs, 1);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);
        sb.begin();
        player1.update(sb);
        player2.update(sb);
        sb.end();
        world.step(1/60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
//        dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }

    public void endGame() {
        parent.changeScreen(FightingGame.MENU);
    }

    public void createBounds() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

                    //WORLD_HEIGHT * 16/9 / 2f, WORLD_HEIGHT/2f
        float halfWorldWidth = WORLD_HEIGHT * camera.viewportWidth / camera.viewportHeight / 2f,
              halfWorldHeight = WORLD_HEIGHT / 2f;

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

    private void createCube(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body boxBody = world.createBody(bodyDef);
        boxBody.setUserData("enemy");

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;

        boxBody.createFixture(fixtureDef);
        boxShape.dispose();
    }

}
