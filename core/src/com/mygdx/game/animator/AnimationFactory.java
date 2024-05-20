package com.mygdx.game.animator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationFactory {
    public static Animation<TextureRegion> create(int frame_cols, int frame_rows, String path) {
        Texture sheet = new Texture(Gdx.files.internal(path));
        TextureRegion[][] tmp = TextureRegion.split(sheet,
                sheet.getWidth() / frame_cols,
                sheet.getHeight() / frame_rows);
        TextureRegion[] walkFrames = new TextureRegion[frame_cols * frame_rows];
        int index = 0;
        for (int i = 0; i < frame_rows; i++) {
            for (int j = 0; j < frame_cols; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        return new Animation<TextureRegion>(.1f, walkFrames);
    }
}
