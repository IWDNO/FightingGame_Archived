package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.loader.GameAssetManager;
import com.mygdx.game.views.EndScreen;
import com.mygdx.game.views.LoadingScreen;
import com.mygdx.game.views.MainScreen;
import com.mygdx.game.views.MenuScreen;

public class FightingGame extends Game {

	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private MainScreen mainScreen;
	private EndScreen endScreen;
	public GameAssetManager assetManager = new GameAssetManager();

	public final static int MENU = 0;
	public final static int APPLICATION = 2;
	public final static int ENDGAME = 3;
	private Music playSong;

	@Override
	public void create() {
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);

		assetManager.queueAddMusic();
		assetManager.manager.finishLoading();
		playSong = assetManager.manager.get("music/background-music.mp3");

//		playSong.play();
	}

	public void changeScreen(int screen) {
		switch (screen) {
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case APPLICATION:
				if(mainScreen == null) mainScreen = new MainScreen(this);
				this.setScreen(mainScreen);
				break;
			case ENDGAME:
				if(endScreen == null) endScreen = new EndScreen(this);
				this.setScreen(endScreen);
				break;
		}
	}

	@Override
	public void dispose(){
		playSong.dispose();
		assetManager.manager.dispose();
	}
}
