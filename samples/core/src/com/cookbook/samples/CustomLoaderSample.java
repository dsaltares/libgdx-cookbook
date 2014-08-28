package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cookbook.animation.SpriteAnimationData;
import com.cookbook.animation.SpriteAnimationLoader;


public class CustomLoaderSample extends GdxSample {
	private static final String TAG = "CustomLoaderSample";
	
	private OrthographicCamera camera;

	private SpriteBatch batch;

	private Texture background, logo, progressBarImg, progressBarBaseImg;

	private SpriteAnimationData cavemanAnims;
	private Animation cavemanWalk;
	private int cavemanX;
	private int cavemanSpeed;
	private TextureRegion currentFrame;
	private boolean goingRight;
	private float animationTime;

	private Vector2 logoPos, pbPos;

	private boolean loaded = false;

	private AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		animationTime = 0.0f;

		// Position the camera at 0,0 with up-growing Y
		camera = new OrthographicCamera();
		camera.setToOrtho(false);

		manager.setLoader(SpriteAnimationData.class, new SpriteAnimationLoader(new InternalFileHandleResolver()));
		manager.load("data/loading_screen/background.png", Texture.class);
		manager.load("data/loading_screen/logo.png", Texture.class);
		manager.load("data/loading_screen/progress_bar.png", Texture.class);
		manager.load("data/loading_screen/progress_bar_base.png", Texture.class);
		manager.finishLoading(); // Blocks until all resources are loaded into memory
		Gdx.app.log(TAG, "Assets loaded");

		// Get Assets
		background = manager.get("data/loading_screen/background.png");
		logo = manager.get("data/loading_screen/logo.png");
		progressBarImg = manager.get("data/loading_screen/progress_bar.png");
		progressBarBaseImg = manager.get("data/loading_screen/progress_bar_base.png");

		// Get logo position
		logoPos = new Vector2();
		// >> bitwise operator bill just divide by 2, the explicitly written times, in this case 1
		logoPos.set((Gdx.graphics.getWidth()-logo.getWidth())>>1, Gdx.graphics.getHeight()>>1);

		// ProgressBar position
		pbPos = new Vector2();
		pbPos.set(logoPos.x, logoPos.y - (logo.getHeight()));

		// Load assets for the next screen
		manager.load("data/caveman-sheet.json", SpriteAnimationData.class);
	}

	@Override
	public void dispose() {
		manager.dispose();
		batch.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update animationTime
		animationTime += Gdx.graphics.getDeltaTime();
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		// Render background image
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();

		// Check if async load is done
		if(!loaded) {
			// Render Logo and Loading Bar
			batch.begin();
			batch.draw(logo, logoPos.x, logoPos.y);
			batch.draw(progressBarBaseImg, pbPos.x, pbPos.y);
			batch.draw(progressBarImg, pbPos.x, pbPos.y, progressBarImg.getWidth()*manager.getProgress(), progressBarImg.getHeight());
			batch.end();
			
			
			if (manager.update()) {
				// Initialize params for caveman
				loaded = true;
				cavemanAnims = manager.get("data/caveman-sheet.json");
				cavemanWalk = cavemanAnims.getAnimation("walk");	
				currentFrame = cavemanWalk.getKeyFrames()[0];
				cavemanX = 0;
				cavemanSpeed = 180;
				goingRight = true;
				
				Gdx.app.log(TAG, "Instructions");
				Gdx.app.log(TAG, "- Press Left key to move left");
				Gdx.app.log(TAG, "- Press Right key to move right");
			}
		}
		else {
			// Caveman resources are loaded... let's have some fun
			updateCaveman();

			batch.begin();
			batch.draw(currentFrame, cavemanX, .0f);
			batch.end();
		}
	}

	// Move caveman sprite along the screen (no limits applied)
	private void updateCaveman() {
		if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) {
			if(goingRight) {
				for(TextureRegion t : cavemanWalk.getKeyFrames())
					t.flip(true, false);
				goingRight = false;
			}
			cavemanX -= Gdx.graphics.getDeltaTime() * cavemanSpeed;
			currentFrame = cavemanWalk.getKeyFrame(animationTime, true);
		}
		if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT)) {
			if(!goingRight) {
				goingRight = true;
				for(TextureRegion t : cavemanWalk.getKeyFrames())
					t.flip(true, false);
			}
			cavemanX += Gdx.graphics.getDeltaTime() * cavemanSpeed;
			currentFrame = cavemanWalk.getKeyFrame(animationTime, true);
		}
	}

}
