package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputListeningSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;
	
	private static final int MESSAGE_MAX = 22;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	private Array<String> messages;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/fonts/oswald-32.fnt"));
		messages = new Array<String>();
		
		font.setColor(Color.WHITE);
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		
		Gdx.input.setInputProcessor(this);
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
			font.draw(batch, messages.get(i), 20.0f, SCENE_HEIGHT - 40.0f * (i + 1));
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public boolean keyDown (int keycode) {
		addMessage("keyDown: keycode(" + keycode + ")");
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		addMessage("keyUp: keycode(" + keycode + ")");
		return true;
	}

	@Override
	public boolean keyTyped (char character) {
		addMessage("keyTyped: character(" + character + ")");
		return true;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		addMessage("touchDown: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ") button(" + button + ")");
		return true;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		addMessage("touchUp: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ") button(" + button + ")");
		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		addMessage("touchDragged: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ")");
		return true;
	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		addMessage("mouseMoved: screenX(" + screenX + ") screenY(" + screenY + ")");
		return true;
	}

	@Override
	public boolean scrolled (int amount) {
		addMessage("scrolled: amount(" + amount + ")");
		return true;
	}
	
	private void addMessage(String message) {
		messages.add(message + " time: " + System.currentTimeMillis());
		
		if (messages.size > MESSAGE_MAX) {
			messages.removeIndex(0);
		}
	}
}

