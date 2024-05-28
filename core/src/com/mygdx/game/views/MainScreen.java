package com.mygdx.game.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.FightingGame;
import com.mygdx.game.GameContactListener;
import com.mygdx.game.animator.Animator;
import com.mygdx.game.characters.*;

import static com.mygdx.game.utils.Constants.*;
import static com.mygdx.game.BodyFactory.BodyFactory.createWorldBounds;

public class MainScreen implements Screen {
    public FightingGame parent;
    public GameContactListener contactListener;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private InputAdapter controller;
    private SpriteBatch sb;
    private Sprite mapSprite;
    private Sprite platformSprite;

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    public Player player1;
    public Player player2;

    private Texture platform = new Texture("map/platform.png");
    private Animator animator = new Animator();
    private Texture texture;
    private TextureRegion healthRegion;
    private TextureRegion healthOutline;

    public MainScreen(FightingGame fg) {
        parent = fg;

        animator.create();
        mapSprite = new Sprite(new Texture(Gdx.files.internal("map/bg.png")));
        mapSprite.setSize(36f, 20f);
        mapSprite.setPosition(-18, -10);

        texture = new Texture("images/HealthBar.png");
        healthRegion = new TextureRegion(texture, 0, 0, 10, 80);
        healthOutline = new TextureRegion(texture, 26, 0, 10, 80);
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, -20), false);
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
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

        player1 = new SaiHan(world, 1, this, PLAYER1_CONTROL_SCHEME, -15, -2);
        player2 = new King(world, 2, this, PLAYER2_CONTROL_SCHEME, 15, -2);
        contactListener = new GameContactListener(this);

        world.setContactListener(contactListener);

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);

        sb.begin();

        //render background & platforms
//        mapSprite.draw(sb);
//        animator.render(sb);
        sb.draw(platform, -WORLD_WIDTH / 2, -WORLD_HEIGHT / 5 - platform.getHeight() / WORLD_HEIGHT,
                WORLD_WIDTH / 4f / 2f, .6f);
        sb.draw(platform, WORLD_WIDTH / 2, -WORLD_HEIGHT / 5 - platform.getHeight() / WORLD_HEIGHT,
                -WORLD_WIDTH / 4f / 2f, .6f);

        sb.draw(platform, 0, -WORLD_HEIGHT / 20 - platform.getHeight() / WORLD_HEIGHT,
                -WORLD_WIDTH / 2f / 2f, .6f);
        sb.draw(platform, 0, -WORLD_HEIGHT / 20 - platform.getHeight() / WORLD_HEIGHT,
                WORLD_WIDTH / 2f / 2f, .6f);

        // update players
        player1.update(sb);
        player2.update(sb);

        // hp drawing
        float xPos = WORLD_WIDTH / 2 * .9f;
        float yPos = WORLD_HEIGHT / 2 * .4f;
        float w = WORLD_WIDTH / 40;
        float h = WORLD_HEIGHT / 4;
        // player1 hp
        float height = player1.getHP() * h / player1.getMaxHp();
        sb.draw(healthOutline, -xPos - healthOutline.getRegionWidth() / WORLD_WIDTH, yPos, w, h);
        sb.draw(healthRegion, -xPos - healthOutline.getRegionWidth() / WORLD_WIDTH, yPos, w, height);
        // player2 hp
        height = player2.getHP() * h / player2.getMaxHp();
        sb.draw(healthOutline, xPos, yPos, w, h);
        sb.draw(healthRegion, xPos, yPos, w, height);

        sb.end();

        world.step(1 / 60f, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
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
                new Vector2(-WORLD_WIDTH / 2, -WORLD_HEIGHT / 5),
                new Vector2(WORLD_WIDTH / 2, -WORLD_HEIGHT / 5),
                new Vector2(0, -WORLD_HEIGHT / 20)
        };

        float[] widths = {
                WORLD_WIDTH / 4f,
                WORLD_WIDTH / 4f,
                WORLD_WIDTH / 2f
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
