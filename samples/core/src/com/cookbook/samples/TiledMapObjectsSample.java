package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TiledMapObjectsSample extends GdxSample {
	private static final float SCALE = 0.2916f;
	private static final int VIRTUAL_WIDTH = (int)(1280 * SCALE);
	private static final int VIRTUAL_HEIGHT = (int)(720 * SCALE);
	
	private static final float CAMERA_SPEED = 100.0f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer renderer;
	
	private Vector2 direction;
	
	private Array<Sprite> enemies;
	private Array<Sprite> items;
	private Array<Sprite> triggers;
	private Sprite player;
	private TextureAtlas atlas;
	private Music song;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		loader = new TmxMapLoader();
		map = loader.load("data/maps/tiled-objects.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, batch);
		atlas = new TextureAtlas(Gdx.files.internal("data/maps/sprites.atlas"));
		direction = new Vector2();
		
		processMapMetadata();
	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		atlas.dispose();
		batch.dispose();
		song.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		updateCamera();
		
		renderer.setView(camera);
		renderer.render();
		
		batch.begin();
		
		for (Sprite enemy : enemies) {
			enemy.draw(batch);
		}
		
		for (Sprite item : items) {
			item.draw(batch);
		}
		
		for (Sprite trigger : triggers) {
			trigger.draw(batch);
		}
		
		player.draw(batch);
		
		batch.end();
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
		float cameraMaxX = layer.getWidth() * layer.getTileWidth() - cameraMinX;
		float cameraMaxY = layer.getHeight() * layer.getTileHeight() - cameraMinY;
		
		camera.position.x = MathUtils.clamp(camera.position.x, cameraMinX, cameraMaxX);
		camera.position.y= MathUtils.clamp(camera.position.y, cameraMinY, cameraMaxY);
		
		camera.update();
	}
	
	private void processMapMetadata() {
		// Load music
		String songPath = map.getProperties().get("music", String.class);
		song = Gdx.audio.newMusic(Gdx.files.internal(songPath));
		song.setLooping(true);
		song.play();
		
		// Load entities
		System.out.println("Searching for game entities...\n");
		
		enemies = new Array<Sprite>();
		items = new Array<Sprite>();
		triggers = new Array<Sprite>();
		
		MapObjects objects = map.getLayers().get("objects").getObjects();
		
for (MapObject object : objects) {
	String name = object.getName();
	String[] parts = name.split("[.]");
	RectangleMapObject rectangleObject = (RectangleMapObject)object;
	Rectangle rectangle = rectangleObject.getRectangle();
	
	System.out.println("Object found");
	System.out.println("- name: " + name);
	System.out.println("- position: (" + rectangle.x + ", " + rectangle.y + ")");
	System.out.println("- size: (" + rectangle.width + ", " + rectangle.height + ")");
	
	if (name.equals("enemy")) {
		Sprite enemy = new Sprite(atlas.findRegion("enemy"));
		enemy.setPosition(rectangle.x, rectangle.y);
		enemies.add(enemy);
	}
	else if (name.equals("player")) {
		player = new Sprite(atlas.findRegion("player"));
		player.setPosition(rectangle.x, rectangle.y);
	}
	else if (parts.length > 1 && parts[0].equals("item")) {
		Sprite item = new Sprite(atlas.findRegion(parts[1]));
		item.setPosition(rectangle.x, rectangle.y);
		items.add(item);
	}
	else if (parts.length > 0 && parts[0].equals("trigger")) {
		Sprite trigger = new Sprite(atlas.findRegion("pixel"));
		trigger.setColor(1.0f, 1.0f, 1.0f, 0.5f);
		trigger.setScale(rectangle.width, rectangle.height);
		trigger.setPosition(rectangle.x - rectangle.width * 0.5f, rectangle.y + rectangle.height * 0.5f);
		triggers.add(trigger);
	}
}
	}
}
