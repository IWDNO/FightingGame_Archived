package com.mygdx.game.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.FightingGame;
import com.mygdx.game.GameContactListener;
import com.mygdx.game.animator.Animator;
import com.mygdx.game.bodies.Player;
import com.mygdx.game.characters.Character;
import com.mygdx.game.characters.TestCharacter;
import com.mygdx.game.controller.ControlScheme;
import com.mygdx.game.controller.InputController;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.BodyFactory.BodyFactory.createWorldBounds;

public class MainScreen implements Screen {
    public static final float WORLD_HEIGHT = 20;
    public static final float WORLD_WIDTH = 20;

    public FightingGame parent;
    public GameContactListener contactListener;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private InputAdapter controller;
    private SpriteBatch sb;
    private Sprite mapSprite;

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    public Player player1;
    public Player player2;
    private ControlScheme p1cs = new ControlScheme(
            Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.V, Input.Keys.Z, Input.Keys.X, Input.Keys.C);
    private ControlScheme p2cs = new ControlScheme(
            Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.M, Input.Keys.SLASH, Input.Keys.COMMA, Input.Keys.PERIOD);

    private Texture background;
    private Animator animator = new Animator();

    public MainScreen(FightingGame fg) {
        parent = fg;
        contactListener = new GameContactListener(this);

        animator.create();
        mapSprite = new Sprite(new Texture(Gdx.files.internal("images/bg.png")));
        mapSprite.setSize(36f, 20f);
        mapSprite.setPosition(-18,-10);
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -20), false);
        debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(contactListener);

        camera = new OrthographicCamera(WORLD_WIDTH * 16/9, WORLD_HEIGHT);
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
        createPlatforms();

        TestCharacter p1 = new TestCharacter(world, 1, this);
        TestCharacter p2 = new TestCharacter(world, 2, this);

        player1 = new Player(p1, -15, -2, p1cs, 1);
        player2 = new Player(p2, 15, -2, p2cs, 2);


    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);
        sb.begin();
        mapSprite.draw(sb);
        Texture platform = new Texture("map/platform.png");
        sb.draw(platform, 0, 260, 190, 25);
        sb.draw(platform, 1600, 260, -190, 25);
        sb.draw(platform, 800, 390, -320, 30);
        sb.draw(platform, 800, 390, 320, 30);


        animator.render(sb);
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
        animator.dispose();
    }

    public void endGame() {
        parent.changeScreen(FightingGame.MENU);
    }

    public void createBounds() {
        //WORLD_HEIGHT * 16/9 / 2f, WORLD_HEIGHT/2f
        float halfWorldWidth = WORLD_HEIGHT * camera.viewportWidth / camera.viewportHeight / 2f,
                halfWorldHeight = WORLD_HEIGHT / 2f - WORLD_HEIGHT / 10;
        createWorldBounds(halfWorldWidth, halfWorldHeight, world);
    }

    private void createPlatforms() {
        // Массив позиций платформ
        Vector2[] positions = {
                new Vector2(-17, -4),
                new Vector2(17, -4),
                new Vector2(0, -1)
        };

        float[] widths = {
                7,
                7,
                14
        };

        // Создание фикстуры платформы (общая для всех платформ)
        FixtureDef platformFixtureDef = new FixtureDef();

        for (int i = 0; i < positions.length; i++) {
            // Создание формы платформы
            PolygonShape platformShape = new PolygonShape();
            platformShape.setAsBox(widths[i] / 2f, .5f / 2f);

            // Настройка фикстуры
            platformFixtureDef.shape = platformShape;

            // Создание тела платформы
            BodyDef platformDef = new BodyDef();
            platformDef.position.set(positions[i]);
            platformDef.type = BodyDef.BodyType.StaticBody;
            Body platformBody = world.createBody(platformDef);

            // Создание фикстуры платформы
            platformBody.createFixture(platformFixtureDef);

            // Удаление формы платформы
            platformShape.dispose();
        }
    }


}
