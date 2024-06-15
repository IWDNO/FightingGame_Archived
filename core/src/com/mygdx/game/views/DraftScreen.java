package com.mygdx.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.FightingGame;

import static com.mygdx.game.utils.Constants.WORLD_HEIGHT;
import static com.mygdx.game.utils.Constants.WORLD_WIDTH;

public class DraftScreen implements Screen {
    private final FightingGame parent;
    private final SpriteBatch batch;
    private final Texture[] characterTextures;
    private final String[] characterDescriptions;
    private int player1Index;
    private int player2Index;

    private final BitmapFont font;

    private final Timer timer;
    private float elapsedTime;
    private float timeLimit;
    private boolean isPlayer1Ready, isPlayer2Ready, isBothReady;
    private final GlyphLayout layout;
    private boolean isSwapAvailable;

    public DraftScreen(FightingGame parent) {
        this.parent = parent;
        OrthographicCamera camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        font = new BitmapFont(Gdx.files.internal("fonts/main/bestFontEver.fnt"), false);
        font.setUseIntegerPositions(false);

        characterTextures = new Texture[4];
        characterTextures[0] = new Texture("images/CharacterPreview/SaiHan.png");
        characterTextures[1] = new Texture("images/CharacterPreview/King.png");
        characterTextures[2] = new Texture("images/CharacterPreview/Huntress.png");
        characterTextures[3] = new Texture("images/CharacterPreview/HeroKnight.png");

        characterDescriptions = new String[4];
        characterDescriptions[0] = "Пидрила в маске\nЕще может накладывать яд, пиздец";
        characterDescriptions[1] = "Типо крутой мужик\nно мы знаем, что нет";
        characterDescriptions[2] = "Ахуеть, негры\nтак еще и с копьем";
        characterDescriptions[3] = "Самый, сука, душный персонаж";

        timer = new Timer();
        layout = new GlyphLayout();
    }

    @Override
    public void show() {
        player1Index = 0;
        player2Index = 0;
        isPlayer2Ready = false;
        isPlayer1Ready = false;
        isBothReady = false;
        isSwapAvailable = true;
        elapsedTime = 0f;
        timeLimit = 100 * 60 * 60f;
        InputAdapter controller = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        parent.changeScreen(FightingGame.MENU);
                        break;
                    case Input.Keys.W:
                        if (!isPlayer1Ready) player1Index = (player1Index + 1) % characterTextures.length;
                        break;
                    case Input.Keys.S:
                        if (!isPlayer1Ready)
                            player1Index = (player1Index - 1 + characterTextures.length) % characterTextures.length;
                        break;
                    case Input.Keys.SHIFT_LEFT:
                        if (isSwapAvailable) isPlayer1Ready = !isPlayer1Ready;
                        break;
                    case Input.Keys.UP:
                        if (!isPlayer2Ready) player2Index = (player2Index + 1) % characterTextures.length;
                        break;
                    case Input.Keys.DOWN:
                        if (!isPlayer2Ready)
                            player2Index = (player2Index - 1 + characterTextures.length) % characterTextures.length;
                        break;
                    case Input.Keys.SHIFT_RIGHT:
                        if (isSwapAvailable) isPlayer2Ready = !isPlayer2Ready;
                        break;
                }
                return true;
            }
        };

        Gdx.input.setInputProcessor(controller);

        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                parent.setScreen(new MainScreen(parent, player1Index, player2Index));
            }
        }, timeLimit);
    }

    @Override
    public void render(float v) {
        if (isPlayer1Ready && isPlayer2Ready && !isBothReady) {
            isSwapAvailable = false;
            isBothReady = true;
            elapsedTime = 0f;
            timeLimit = 3f;
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    parent.setScreen(new MainScreen(parent, player1Index, player2Index));
                }
            }, timeLimit);
        }

        elapsedTime += v;
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float textWidth;
        batch.begin();

        font.setColor(1, 1, 1, 1);

        // ready states TODO пиздец что это
        font.getData().setScale(1 / 16f / 8f);
        textWidth = getTextWidth("Не готов");
        if (isPlayer1Ready) {
            textWidth = getTextWidth("Готов");
            font.setColor(0, 1, 0, 1);
            font.draw(batch, "Готов", -WORLD_WIDTH / 4 - textWidth / 2, -6f);
            font.setColor(1, 1, 1, 1);
            textWidth = getTextWidth("Не готов");
        } else font.draw(batch, "Не готов", -WORLD_WIDTH / 4 - textWidth / 2, -6f);
        if (isPlayer2Ready) {
            textWidth = getTextWidth("Готов");
            font.setColor(0, 1, 0, 1);
            font.draw(batch, "Готов", WORLD_WIDTH / 4 - textWidth / 2, -6f);
            font.setColor(1, 1, 1, 1);
        } else font.draw(batch, "Не готов", WORLD_WIDTH / 4 - textWidth / 2, -6f);

        // player1 / player2
        font.getData().setScale(1 / 32f / 8f);
        textWidth = getTextWidth("1 Игрок");
        font.draw(batch, "1 Игрок", -WORLD_WIDTH / 4 - textWidth / 2, 7.5f);
        font.draw(batch, "2 Игрок", WORLD_WIDTH / 4 - textWidth / 2, 7.5f);

        // Описание
        textWidth = getTextWidth(characterDescriptions[player1Index]);
        font.draw(batch, characterDescriptions[player1Index], -WORLD_WIDTH / 4 - textWidth / 2, -2.5f);

        textWidth = getTextWidth(characterDescriptions[player2Index]);
        font.draw(batch, characterDescriptions[player2Index], WORLD_WIDTH / 4 - textWidth / 2, -2.5f);

        // Отображение выбранных персонажей
        batch.draw(characterTextures[player1Index], -WORLD_WIDTH / 4 - 2.5f, 0, 5, 5); // Позиция персонажа 1
        batch.draw(characterTextures[player2Index], WORLD_WIDTH / 4 - 2.5f, 0, 5, 5); // Позиция персонажа 2

        // Time remaining
        font.getData().setScale(1 / 16f / 8f);
        textWidth = getTextWidth(String.valueOf((int) (timeLimit - elapsedTime + 1)));
        if (isBothReady) font.setColor(1, 0, 0, 1);
        font.draw(batch, String.valueOf((int) (timeLimit - elapsedTime + 1)), 0 - textWidth / 2, 8);

        batch.end();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        timer.clear();
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }
}
