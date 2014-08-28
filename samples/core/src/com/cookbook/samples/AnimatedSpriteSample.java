package com.cookbook.samples;


import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AnimatedSpriteSample extends GdxSample {
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	private static final float FRAME_DURATION = 1.0f / 30.0f;

	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private TextureAtlas cavemanAtlas;
	private TextureAtlas dinosaurAtlas;
	private Texture background;
	
	private Animation dinosaurWalk;
	private Animation cavemanWalk;
	private float animationTime;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		
		batch = new SpriteBatch();
		animationTime = 0.0f;
		
		// Load atlases and textures
		cavemanAtlas = new TextureAtlas(Gdx.files.internal("data/caveman.atlas"));
		dinosaurAtlas = new TextureAtlas(Gdx.files.internal("data/trex.atlas"));
		background = new Texture(Gdx.files.internal("data/jungle-level.png"));
		
		// Load animations
		Array<AtlasRegion> cavemanRegions = new Array<AtlasRegion>(cavemanAtlas.getRegions());
		cavemanRegions.sort(new RegionComparator());
		
		Array<AtlasRegion> dinosaurRegions = new Array<AtlasRegion>(dinosaurAtlas.getRegions());
		dinosaurRegions.sort(new RegionComparator());
		
		cavemanWalk = new Animation(FRAME_DURATION, cavemanRegions, PlayMode.LOOP);
		dinosaurWalk = new Animation(FRAME_DURATION, dinosaurRegions, PlayMode.LOOP);
		
		// Position the camera
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
	}

	@Override
	public void dispose() {
		batch.dispose();
		cavemanAtlas.dispose();
		dinosaurAtlas.dispose();
		background.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Update animationTime
		animationTime += Gdx.graphics.getDeltaTime();
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		// Render background
		int width = background.getWidth();
		int height = background.getHeight();
		
		batch.draw(background,
				   0.0f, 0.0f,
				   0.0f, 0.0f,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		TextureRegion cavemanFrame = cavemanWalk.getKeyFrame(animationTime);
		width = cavemanFrame.getRegionWidth();
		height = cavemanFrame.getRegionHeight();
		float originX = width * 0.5f;
		float originY = height * 0.5f;
		
		batch.draw(cavemanFrame,
				   1.0f - originX, 3.70f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f);
		
		batch.draw(cavemanWalk.getKeyFrame(animationTime), 100.0f, 275.0f);
		
		TextureRegion dinosaurFrame = dinosaurWalk.getKeyFrame(animationTime);
		width = dinosaurFrame.getRegionWidth();
		height = dinosaurFrame.getRegionHeight();
		originX = width * 0.5f;
		originY = height * 0.5f;
		
		batch.draw(dinosaurFrame,
				   6.75f - originX, 4.70f - originY,
				   originX, originY,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
	}

	private static class RegionComparator implements Comparator<AtlasRegion> {
		@Override
		public int compare(AtlasRegion region1, AtlasRegion region2) {
			return region1.name.compareTo(region2.name);
		}
	}
}