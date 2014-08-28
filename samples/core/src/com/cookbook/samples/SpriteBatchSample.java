package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SpriteBatchSample extends GdxSample {
	private static final Color BACKGROUND_COLOR = new Color(0.39f, 0.58f, 0.92f, 1.0f);
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture cavemanTexture;
	private Color oldColor;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		oldColor = new Color();
		
		cavemanTexture = new Texture(Gdx.files.internal("data/caveman.png"));
		cavemanTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}

	@Override
	public void dispose() {
		batch.dispose();
		cavemanTexture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(BACKGROUND_COLOR.r,
							BACKGROUND_COLOR.g,
							BACKGROUND_COLOR.b,
							BACKGROUND_COLOR.a);
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		int width = cavemanTexture.getWidth();
		int height = cavemanTexture.getHeight();
		float originX = width * 0.5f;
		float originY = height * 0.5f;
		
		// Render caveman centered on the screen
		batch.draw(cavemanTexture,						// Texture
				   -originX, -originY,					// x, y
				   originX, originY,					// originX, originY
				   width, height,						// width, height
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,	// scaleX, scaleY
				   0.0f,								// rotation
				   0, 0,								// srcX, srcY
				   width, height,						// srcWidth, srcHeight
				   false, false);						// flipX, flipY
		
		// Render caveman on the top left corner at 2x size
		batch.draw(cavemanTexture,
				   -4.0f - originX, 1.5f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN * 2.0f, WORLD_TO_SCREEN * 2.0f,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render caveman on the bottom left corner at 0.5x size
		batch.draw(cavemanTexture,
				   -4.0f - originX, -1.5f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN * 0.5f, WORLD_TO_SCREEN * 0.5f,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render caveman on top right corner at 2x size and rotated 45 degrees
		batch.draw(cavemanTexture,
				   4.0f - originX, 1.5f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN * 2.0f, WORLD_TO_SCREEN * 2.0f,
				   45.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render caveman on bottom right corner at 1.5x size and flipped around X and Y
		batch.draw(cavemanTexture,
				   4.0f - originX, -1.5f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN * 1.5f, WORLD_TO_SCREEN * 1.5f,
				   0.0f,
				   0, 0,
				   cavemanTexture.getWidth(), height,
				   true, true);
		
		// Save batch color
		oldColor.set(batch.getColor());
		
		// Render blue caveman
		batch.setColor(Color.CYAN);
		batch.draw(cavemanTexture, 
				   -2.0f - originX, -originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render red caveman
		batch.setColor(Color.RED);
		batch.draw(cavemanTexture, 
				   -originX, -originY + 2.0f,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render green caveman
		batch.setColor(Color.GREEN);
		batch.draw(cavemanTexture, 
				   2.0f - originX, -originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		// Render yellow caveman
		batch.setColor(Color.YELLOW);
		batch.draw(cavemanTexture, 
				   -originX, -originY - 2.0f,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		batch.setColor(oldColor);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
	}
}
