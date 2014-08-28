package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class OrthographicCameraSample extends GdxSample {
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private static final float CAMERA_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_SPEED = 2.0f;
	private static final float CAMERA_ZOOM_MAX = 1.0f;
	private static final float CAMERA_ZOOM_MIN = 0.01f;
	private static final float CAMERA_MOVE_EDGE = 0.2f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture levelTexture;
	private Vector3 touch;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		touch = new Vector3();
		
		levelTexture = new Texture(Gdx.files.internal("data/jungle-level.png"));
		levelTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		camera.position.x = SCENE_WIDTH * 0.5f;
		camera.position.y = SCENE_HEIGHT * 0.5f;
	}

	@Override
	public void dispose() {
		batch.dispose();
		levelTexture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		// Arrow keys move the camera
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			camera.position.x -= CAMERA_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			camera.position.x += CAMERA_SPEED * deltaTime;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			camera.position.y += CAMERA_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			camera.position.y -= CAMERA_SPEED * deltaTime;
		}
		
		// Touching on the edges also moves the camera
		if (Gdx.input.isTouched()) {
			touch.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
			camera.unproject(touch);
			
			if (touch.x > SCENE_WIDTH * (1.0f - CAMERA_MOVE_EDGE)) {
				camera.position.x += CAMERA_SPEED * deltaTime;
			}
			else if (touch.x < SCENE_WIDTH * CAMERA_MOVE_EDGE) {
				camera.position.x -= CAMERA_SPEED * deltaTime;
			}
			
			if (touch.y > SCENE_HEIGHT * (1.0f - CAMERA_MOVE_EDGE)) {
				camera.position.y += CAMERA_SPEED * deltaTime;
			}
			else if (touch.y < SCENE_HEIGHT * CAMERA_MOVE_EDGE) {
				camera.position.y -= CAMERA_SPEED * deltaTime;
			}
		}
		
		// Page up/down control the zoom
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom -= CAMERA_ZOOM_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom += CAMERA_ZOOM_SPEED * deltaTime;
		}
		
		//Clamp position
		float halfWidth = SCENE_WIDTH * 0.5f;
		float halfHeight = SCENE_HEIGHT * 0.5f; 
		
		camera.position.x = MathUtils.clamp(camera.position.x, 
											halfWidth * camera.zoom,
											levelTexture.getWidth() * WORLD_TO_SCREEN - halfWidth * camera.zoom);
		camera.position.y = MathUtils.clamp(camera.position.y,
											halfHeight * camera.zoom,
											levelTexture.getHeight() * WORLD_TO_SCREEN - halfHeight * camera.zoom);
		
		// Clamp zoom
		camera.zoom = MathUtils.clamp(camera.zoom, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
		
		// Log position and zoom
		Gdx.app.log("position", camera.position.toString());
		Gdx.app.log("zoom", Float.toString(camera.zoom));
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		// Render the jungle level bottom left corner at (0, 0)
		batch.begin();
		batch.draw(levelTexture,
				   0.0f, 0.0f,
				   0.0f, 0.0f,
				   levelTexture.getWidth(), levelTexture.getHeight(),
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   levelTexture.getWidth(), levelTexture.getHeight(),
				   false, false);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}


