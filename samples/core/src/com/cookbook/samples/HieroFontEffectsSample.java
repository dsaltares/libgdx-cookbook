package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HieroFontEffectsSample extends GdxSample {
	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 720;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont playFont;
	private BitmapFont effectFont;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH  * 0.5f, VIRTUAL_HEIGHT * 0.5f, 0.0f);
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		
		playFont = new BitmapFont(Gdx.files.internal("data/fonts/play.fnt"));
		playFont.setColor(0.0f, 0.56f, 1.0f, 1.0f);
		
		effectFont = new BitmapFont(Gdx.files.internal("data/fonts/lobster.fnt"));
		effectFont.setColor(0.28f, 0.0f, 1.0f, 1.0f);
	}

	@Override
	public void dispose() {
		batch.dispose();
		playFont.dispose();
		effectFont.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		effectFont.draw(batch, "This effect is exciting!", 20.0f, VIRTUAL_HEIGHT - 50.0f);
		playFont.draw(batch, "Pretty boring text", 20.0f, VIRTUAL_HEIGHT - 140.0f);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}

