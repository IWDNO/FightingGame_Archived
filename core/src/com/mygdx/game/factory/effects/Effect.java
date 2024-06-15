package com.mygdx.game.factory.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.characters.Player;

import static com.mygdx.game.utils.Constants.PLAYER_HEIGHT;

public abstract class Effect {
    protected float duration, elapsed;
    protected Texture effectTexture;
    private final Texture texture;
    private final TextureRegion line, outline;
    protected Timer timer;

    public Effect(float duration) {
        this.duration = duration;
        elapsed = 0;

        timer = new Timer();

        texture = new Texture("images/effects/line.png");
        line = new TextureRegion(texture, 0, 10, 10, 10);
        outline = new TextureRegion(texture, 0, 0, 10, 10);
    }

    public boolean isDone() {
        return elapsed >= duration;
    }

    public abstract void run(Player player);

    public void update(SpriteBatch sb, Player player) {
        elapsed += Gdx.graphics.getDeltaTime();

        float width = 1f;
        float lineHeight = width / 10f;
        float remainingTimeFraction = 1 - (elapsed / duration);

        int n = player.getEffectList().size();
        int i = player.getEffectList().indexOf(this);
        float gap = width / 2f;
        float totalWidth = n * width + (n - 1) * gap;
        float startX = player.getBody().getPosition().x - totalWidth / 2f;
        float posX = startX + i * (width + gap);
        float posY = player.getBody().getPosition().y + PLAYER_HEIGHT / 1.5f;

        sb.draw(effectTexture, posX, posY, width, width);
        sb.draw(outline, posX, posY - lineHeight - lineHeight, width, lineHeight);
        sb.draw(line, posX, posY - lineHeight - lineHeight, width * remainingTimeFraction, lineHeight);
    }

    public void dispose() {
        timer.clear();
        effectTexture.dispose();
        texture.dispose();
    }
}
