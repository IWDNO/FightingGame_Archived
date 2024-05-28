package com.mygdx.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class GameAssetManager {
    public final AssetManager manager = new AssetManager();
    public final String playerImage = "images/player.png";
    public final String punchSound = "sounds/punch.wav";
    public final String bgMusic = "music/background-music.mp3";
    public final String skin = "skin/glassy-ui.json";

    public void queueAddSkin(){
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void queueAddImages(String[] textures) {
        for (String texture: textures) {
            manager.load(texture, Texture.class);
        }
    }

    public void queueAddSounds() {
        manager.load(punchSound, Sound.class);
    }

    public void queueAddMusic() {
        manager.load(bgMusic, Music.class);
    }
}
