package com.mygdx.game.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.FightingGame;
import com.mygdx.game.GameContactListener;
import com.mygdx.game.animator.Animator;
import com.mygdx.game.characters.*;

import static com.mygdx.game.utils.Constants.*;
import static com.mygdx.game.factory.BodyFactory.*;

public class MainScreen implements Screen {
    private final BitmapFont font;
    public FightingGame parent;
    public GameContactListener contactListener;

    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch sb;

    private final int VELOCITY_ITERATIONS = 8, POSITION_ITERATIONS = 3;

    public Player player1;
    public Player player2;

    private final Texture platform = new Texture("map/platform.png");
    private final Animator animator = new Animator();
    private final TextureRegion healthRegion;
    private final TextureRegion healthOutline;
    private boolean isEndedAlready = false;
    private GlyphLayout layout = new GlyphLayout();
    private Timer.Task backToMenuTack;
    private float timeLimit = 3f, elapsedTime;

    public MainScreen(FightingGame fg, int player1Index, int player2Index) {
        parent = fg;

        font = new BitmapFont(Gdx.files.internal("fonts/main/bestFontEver.fnt"), false);
        font.setUseIntegerPositions(false);

        animator.create();
        Sprite mapSprite = new Sprite(new Texture(Gdx.files.internal("map/bg.png")));
        mapSprite.setSize(36f, 20f);
        mapSprite.setPosition(-18, -10);

        Texture texture = new Texture("images/HealthBar.png");
        healthRegion = new TextureRegion(texture, 0, 0, 10, 80);
        healthOutline = new TextureRegion(texture, 26, 0, 10, 80);

        world = new World(new Vector2(0, -20), false);
        debugRenderer = new Box2DDebugRenderer();

        player1 = createPlayer(player1Index, 1);
        player2 = createPlayer(player2Index, 2);

        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined);

        InputAdapter controller = new InputAdapter() {
            @Override
            public boolean keyDown(int i) {
                if (i == Input.Keys.ESCAPE) {
                    parent.changeScreen(FightingGame.MENU);
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(controller);

        createBounds(world);
        createPlatforms(world);

        contactListener = new GameContactListener(this);

        world.setContactListener(contactListener);

        elapsedTime = 0f;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);

        sb.begin();

        //render background & platforms
//        mapSprite.draw(sb);
        animator.render(sb);
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

        if (player1.getHP() <= 0 || player2.getHP() <= 0) {
            endGame(player1.getHP() <= 0 ? player2 : player1);
        }

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
        if (backToMenuTack != null) backToMenuTack.cancel();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        animator.dispose();
    }

    public void endGame(Player player) {
        if (!isEndedAlready) {
            Timer timer = new Timer();
            backToMenuTack = new Timer.Task() {
                @Override
                public void run() {
                    parent.changeScreen(FightingGame.MENU);

                }
            };
            timer.scheduleTask(backToMenuTack, 3f);
            isEndedAlready = true;
        }
        font.getData().setScale(1 / 12f / 8f);
        float textWidth = getTextWidth(String.valueOf((int) (timeLimit - elapsedTime + 1)));
        font.setColor(1, 0, 0, 1);
        font.draw(sb, String.valueOf((int) (timeLimit - elapsedTime + 1)), 0 - textWidth / 2, WORLD_HEIGHT / 2.5f);

        font.getData().setScale(1 / 6f / 8f);
        font.setColor(1, 1, 1, 1);
        String text = player1.getHP() <= 0 && player2.getHP() <= 0
                ? "Tie!" : "Player " + player.getPlayerNumber() + " wins!";
        textWidth = getTextWidth(text);
        font.draw(sb, text, 0 - textWidth / 2, WORLD_HEIGHT / 4);

        elapsedTime += Gdx.graphics.getDeltaTime();
    }

    private Player createPlayer(int playerIndex, int playerNumber) { //TODO change a lot of things
        if (playerNumber == 1)
            return switch (playerIndex) {
                case 0 -> new SaiHan(world, 1, this, PLAYER1_CONTROL_SCHEME, -15, -2);
                case 1 -> new King(world, 1, this, PLAYER1_CONTROL_SCHEME, -15, -2);
                case 2 -> new Huntress(world, 1, this, PLAYER1_CONTROL_SCHEME, -15, -2);
                case 3 -> new HeroKnight(world, 1, this, PLAYER1_CONTROL_SCHEME, -15, -2);
                default -> null;
            };
        else
            return switch (playerIndex) {
                case 0 -> new SaiHan(world, 2, this, PLAYER2_CONTROL_SCHEME, 15, -2);
                case 1 -> new King(world, 2, this, PLAYER2_CONTROL_SCHEME, 15, -2);
                case 2 -> new Huntress(world, 2, this, PLAYER2_CONTROL_SCHEME, 15, -2);
                case 3 -> new HeroKnight(world, 2, this, PLAYER2_CONTROL_SCHEME, 15, -2);
                default -> null;
            };
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }
}
