package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FrustumCullingSample extends GdxSample {
	private static final String TAG = "FrustumCullingSample";

	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;

	private OrthographicCamera camera;
	private OrthographicCamera uiCamera;
	private Viewport viewport;
	private Viewport uiViewport;
	private Vector3 point1 = new Vector3();
	private Vector3 point2 = new Vector3();
	private Vector2 direction;
	private static final float CAMERA_SPEED = 2.0f;
	
	private SpriteBatch batch;
	private SpriteBatch batch2;

	private Array<Entity> entities;
	private static final int NUM_OF_ENTITIES = 20;
	private int renderCount;

	private BitmapFont font;
	private StringBuilder stringBuilder;
	
	private ShapeRenderer shapeRenderer;
	
	private Texture cavemanTex, boxTex;

	@Override
	public void create() {	
		
		// GAME CAMERA
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		// Center camera
		viewport.getCamera().position.set(
				viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
				0);
		viewport.getCamera().update();
		
		direction = new Vector2();
		
		// UI CAMERA
		
		uiCamera = new OrthographicCamera();
		uiViewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, uiCamera);
		// Center camera
		uiViewport.getCamera().position.set(
				uiViewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				uiViewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
				0);
		uiViewport.getCamera().update();
		uiViewport.update((int)SCENE_WIDTH, (int)SCENE_HEIGHT);

		
		batch = new SpriteBatch();
		batch2 = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("data/verdana39.fnt"));
		font.setColor(Color.WHITE);
		stringBuilder = new StringBuilder();
		
		Gdx.input.setInputProcessor(this);

		// Initialize entities
		entities = new Array<Entity>(false, NUM_OF_ENTITIES);
		cavemanTex = new Texture(Gdx.files.internal("data/caveman.png"));
		boxTex = new Texture(Gdx.files.internal("data/box2D/box.png"));
		
		TextureRegion cavemanRegion = new TextureRegion(cavemanTex);
		TextureRegion boxRegion = new TextureRegion(boxTex);
		Vector2 scale = new Vector2(1f,1f);
		int i;
		final int NUM_OF_ROWS = 4;
		final int NUM_OF_COLS = NUM_OF_ENTITIES/NUM_OF_ROWS;
		for(i = 0; i < NUM_OF_ENTITIES; i++) {
			float x = i % NUM_OF_ROWS;
			float y = i % NUM_OF_COLS;
			if(i<10)
				entities.add(new Entity(cavemanRegion, x, y, 0.4f, 0.75f, 0f, scale));
			else
				entities.add(new Entity(boxRegion, x, y, 0.5f, 0.5f, 0f, scale));
		}
		
		// Initialize ShapeRenderer for debug purposes
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(Color.RED);
	}

	@Override
	public void dispose() {
		cavemanTex.dispose();
		boxTex.dispose();
		
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
		entities.clear();
	}

	private void updateCamera() {
		direction.set(0.0f, 0.0f);

		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		if (Gdx.input.isKeyPressed(Keys.LEFT) || (Gdx.input.isTouched() && mouseX < width * 0.25f)) {
			direction.x = -1;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) || (Gdx.input.isTouched() && mouseX > width * 0.75f)) {
			direction.x = 1;
		}

		if (Gdx.input.isKeyPressed(Keys.UP) || (Gdx.input.isTouched() && mouseY < height * 0.25f)) {
			direction.y = 1;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN) || (Gdx.input.isTouched() && mouseY > height * 0.75f)) {
			direction.y = -1;
		}

		direction.nor().scl(CAMERA_SPEED * Gdx.graphics.getDeltaTime());;

		camera.position.x += direction.x;
		camera.position.y += direction.y;

		camera.update();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Deal with camera movement
		updateCamera();
		
		// Render only visible objects
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		renderCount = 0;
		for(Entity entity : entities)
			if(entity.isVisible(camera)) {
				entity.render(batch);				
				renderCount++;
			}

		
		batch.end();
		
		//DEBUG INFO: Show entities' center
		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Filled);
		for(Entity e : entities)
			shapeRenderer.circle(e.position.x, e.position.y, 0.05f,20);
		shapeRenderer.end();
		
		
		// Show n of currently rendered entities
		batch2.begin();
		stringBuilder.setLength(0);
		stringBuilder.append("Rendering: " ).append(renderCount).append("/").append(NUM_OF_ENTITIES);
		uiViewport.getCamera().project(point1.set(0.5f, 6.8f, 0));
		font.draw(batch2, stringBuilder, point1.x, point1.y);
		batch2.end();

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		uiViewport.update(width, height);
	}

	/* Base class for any game entity */
	public class Entity {
		public Vector2 position;
		public float rotation;
		public Vector2 scale;
		public Vector2 dimensions;
		public TextureRegion region;
		public float diagonal;
		

		public Entity(TextureRegion region, float x, float y, float width, float height, float rotation, Vector2 scale) {
			this.dimensions = new Vector2(width, height);
			this.position = new Vector2(x + (width*.5f), y + (height*.5f));
			this.region = region;
			this.rotation = rotation;
			this.scale = new Vector2(scale);
			this.diagonal = (float) Math.sqrt(Math.pow(width*scale.x, 2f) + Math.pow(height*scale.y, 2f));
		}

		public boolean isVisible(final Camera cam) {
			return cam.frustum.sphereInFrustum(point1.set(position,0), diagonal*.5f);
		}
		
		public void render(final SpriteBatch batch) {
			batch.draw(region, position.x-dimensions.x*.5f, position.y-dimensions.y*.5f, position.x, position.y, dimensions.x, dimensions.y, scale.x, scale.y, rotation);
		}
	}

}

