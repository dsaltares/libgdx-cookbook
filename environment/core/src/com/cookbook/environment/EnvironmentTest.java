package com.cookbook.environment;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

public class EnvironmentTest implements ApplicationListener {
	private SpriteBatch batch;
	private Texture img;
	private Logger logger;
	private boolean renderInterrupted = true;
	
	@Override
	public void create () {
		logger = new Logger("Application lifecycle", Logger.INFO);
		
		logger.info("create");
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		if (renderInterrupted) {
			logger.info("render");
			renderInterrupted = false;
		}

		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		logger.info("resize");
	    renderInterrupted = true;
	}

	@Override
	public void pause() {
		logger.info("pause");
	    renderInterrupted = true;
	}

	@Override
	public void resume() {
		logger.info("resume");
	    renderInterrupted = true;
	}

	@Override
	public void dispose() {
		logger.info("dispose");
	}
}
