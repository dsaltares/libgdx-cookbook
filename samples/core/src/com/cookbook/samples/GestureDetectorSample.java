package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GestureDetectorSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280.0f;
	private static final float SCENE_HEIGHT = 720.0f;
	
	private static final int MESSAGE_MAX = 20;
	private static final float HALF_TAP_SQUARE_SIZE = 20.0f;
	private static final float TAP_COUNT_INTERVAL = 0.4f;
	private static final float LONG_PRESS_DURATION = 1.1f;
	private static final float MAX_FLING_DELAY = 0.15f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	private GestureDetector gestureDetector;	
	private Array<String> messages;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/fonts/oswald-32.fnt"));
		messages = new Array<String>();
		
		font.setColor(Color.WHITE);
		font.getData().setScale(0.8f);
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		gestureDetector = new GestureDetector(HALF_TAP_SQUARE_SIZE,
											  TAP_COUNT_INTERVAL,
											  LONG_PRESS_DURATION,
											  MAX_FLING_DELAY,
											  new GestureHandler());
		
		Gdx.input.setInputProcessor(gestureDetector);
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.39f, 0.58f, 0.92f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for (int i = 0; i < messages.size; ++i) {
			font.draw(batch, messages.get(i), 20.0f, SCENE_HEIGHT - 30.0f * (i + 1));
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	public class GestureHandler implements GestureListener
	{
		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			addMessage("touchDown: x(" + x + ") y(" + y + ") pointer(" + pointer + ") button(" + button +")");
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			addMessage("tap: x(" + x + ") y(" + y + ") count(" + count + ") button(" + button +")");
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			addMessage("longPress: x(" + x + ") y(" + y + ")");
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			addMessage("fling: velX(" + velocityX + ") velY(" + velocityY + ") button(" + button +")");
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			addMessage("pan: x(" + x + ") y(" + y + ") deltaX(" + deltaX + ") deltaY(" + deltaY +")");
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			addMessage("panStop: x(" + x + ") y(" + y + ") pointer(" + pointer + ") button(" + button +")");
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			addMessage("zoom: initialDistance(" + initialDistance + ") distance(" + distance + ")");
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			addMessage("pinch: initialP1(" + initialPointer1 + ") initialP2(" + initialPointer2 + ") p1(" + pointer1 + ") p2(" + pointer2 +")");
			return false;
		}
		
	}
	
	private void addMessage(String message) {
		messages.add(message + " time: " + System.currentTimeMillis());
		
		if (messages.size > MESSAGE_MAX) {
			messages.removeIndex(0);
		}
	}
}

