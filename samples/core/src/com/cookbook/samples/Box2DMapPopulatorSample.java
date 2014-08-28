package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.box2d.MapBodyManager;

public class Box2DMapPopulatorSample extends GdxSample {
	private static final String TAG = "Box2DMapPopulatorSample";

	private static final float SCREEN_TO_WORLD = 30f;
	private static final float WORLD_TO_SCREEN = 1/SCREEN_TO_WORLD;
	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high
	
	private Viewport viewport;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;

	MapBodyManager mbm;
	private OrthographicCamera camera;
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer renderer;

	private static final float CAMERA_SPEED = SCENE_HEIGHT*.5f;
	private Vector2 direction;
	
	@Override
	public void create () {
		super.create();

		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		
		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-9.8f), true);

		// Instantiate the class in charge of drawing physics shapes
		debugRenderer = new Box2DDebugRenderer();

		loader = new TmxMapLoader();
		map = loader.load("data/box2D/map/tiled.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, WORLD_TO_SCREEN);
		
		mbm = new MapBodyManager(world, SCREEN_TO_WORLD, Gdx.files.internal("data/box2D/materials.json"), Logger.INFO);
		mbm.createPhysics(map);
		
		direction = new Vector2();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
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
		
		direction.nor().scl(CAMERA_SPEED).scl(Gdx.graphics.getDeltaTime());;
		
		camera.position.x += direction.x;
		camera.position.y += direction.y;
		
		TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
		
		float cameraMinX = viewport.getWorldWidth() * 0.5f;
		float cameraMinY = viewport.getWorldHeight() * 0.5f;
		float cameraMaxX = layer.getWidth() * layer.getTileWidth() * WORLD_TO_SCREEN - cameraMinX;
		float cameraMaxY = layer.getHeight() * layer.getTileHeight() * WORLD_TO_SCREEN - cameraMinY;
		
		camera.position.x = MathUtils.clamp(camera.position.x, cameraMinX, cameraMaxX);
		camera.position.y= MathUtils.clamp(camera.position.y, cameraMinY, cameraMaxY);
		
		camera.update();
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();

		batch.dispose();
		mbm.destroyPhysics();
		world.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(1/60f, 6, 2);
		
		updateCamera();

		renderer.setView(camera);
		renderer.render();
		
		debugRenderer.render(world, camera.combined);
		
	}
}
