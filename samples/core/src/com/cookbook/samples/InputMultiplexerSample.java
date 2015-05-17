package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputMultiplexerSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;
	
	private static final int MESSAGE_MAX = 22;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	private InputMultiplexer multiplexer;
	private Array<ScreenLogMessage> messages;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/fonts/oswald-32.fnt"));
		font.getData().setScale(0.8f);
		messages = new Array<ScreenLogMessage>();
		multiplexer = new InputMultiplexer();
		
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		
		Gdx.input.setInputProcessor(multiplexer);
		multiplexer.addProcessor(new InputHandlerA());
		multiplexer.addProcessor(new InputHandlerB());
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
			ScreenLogMessage message = messages.get(i);
			font.setColor(message.color);
			font.draw(batch, message.message, 20.0f, SCENE_HEIGHT - 30.0f * (i + 1));
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	private void addMessage(String message, Color color) {
		messages.add(new ScreenLogMessage(message, color));
		
		if (messages.size > MESSAGE_MAX) {
			messages.removeIndex(0);
		}
	}
	
	private class InputHandlerA extends InputAdapter {
		@Override
		public boolean keyDown (int keycode) {
			addMessage("InputHandlerA - keyDown: keycode(" + keycode + ")", Color.YELLOW);
			return true;
		}
	
		@Override
		public boolean keyUp (int keycode) {
			addMessage("InputHandlerA - keyUp: keycode(" + keycode + ")", Color.YELLOW);
			return true;
		}
	
		@Override
		public boolean keyTyped (char character) {
			addMessage("InputHandlerA - keyTyped: character(" + character + ")", Color.YELLOW);
			return true;
		}
	}
	
	private class InputHandlerB extends InputAdapter {
		@Override
		public boolean touchDown (int screenX, int screenY, int pointer, int button) {
			addMessage("InputHandlerB - touchDown: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ") button(" + button + ")", Color.GREEN);
			return true;
		}

		@Override
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			addMessage("InputHandlerB - touchUp: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ") button(" + button + ")", Color.GREEN);
			return true;
		}

		@Override
		public boolean touchDragged (int screenX, int screenY, int pointer) {
			addMessage("InputHandlerB - touchDragged: screenX(" + screenX + ") screenY(" + screenY + ") pointer(" + pointer + ")", Color.GREEN);
			return true;
		}

		@Override
		public boolean mouseMoved (int screenX, int screenY) {
			addMessage("InputHandlerB - mouseMoved: screenX(" + screenX + ") screenY(" + screenY + ")", Color.GREEN);
			return true;
		}

		@Override
		public boolean scrolled (int amount) {
			addMessage("InputHandlerB - scrolled: amount(" + amount + ")", Color.GREEN);
			return true;
		}
	}
	
	private class ScreenLogMessage {
		public final String message;
		public final Color color;
		
		public ScreenLogMessage(String message, Color color) {
			this.message = message;
			this.color = color;
		}
	}
}

