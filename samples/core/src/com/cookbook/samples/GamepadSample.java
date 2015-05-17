package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GamepadSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280.0f;
	private static final float SCENE_HEIGHT = 720.0f;
	
	private static final int MESSAGE_MAX = 26;
	private static final float DEAD_ZONE = 0.4f;
	
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
		font = new BitmapFont();
		messages = new Array<String>();
		
		font.setColor(Color.WHITE);
		font.getData().setScale(1.5f);
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		
		Controllers.addListener(new ControllerEventHandler());
		
		for (Controller controller : Controllers.getControllers()) {
			Gdx.app.log("Controllers: ", controller.toString());
		}
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
			font.draw(batch, messages.get(i), 20.0f, SCENE_HEIGHT - 25.0f * (i + 1));
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	private void addMessage(String message) {
		messages.add(message);
		
		if (messages.size > MESSAGE_MAX) {
			messages.removeIndex(0);
		}
	}
	
	private class ControllerEventHandler implements ControllerListener
	{
		@Override
		public void connected(Controller controller) {
			addMessage("controller connected");
		}

		@Override
		public void disconnected(Controller controller) {
			addMessage("controller disconnected");
		}

		@Override
		public boolean buttonDown(Controller controller, int buttonCode) {
			addMessage("buttonDown controller(" + controller + ") buttonCode(" + buttonCode + ")");			
			return false;
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonCode) {
			addMessage("buttonUp controller(" + controller + ") buttonCode(" + buttonCode + ")");
			return false;
		}

		@Override
		public boolean axisMoved(Controller controller, int axisCode, float value) {
			if (Math.abs(value) > DEAD_ZONE) {
				addMessage("axisMoved controller(" + controller + ") axisCode(" + axisCode + ") value(" + value + ")");
			}
			return false;
		}

		@Override
		public boolean povMoved(Controller controller, int povCode, PovDirection value) {
			addMessage("povMoved controller(" + controller + ") povCode(" + povCode + ") value(" + value + ")");
			return false;
		}

		@Override
		public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
			addMessage("xSliderMoved controller(" + controller + ") sliderCode(" + sliderCode + ") value(" + value + ")");
			return false;
		}

		@Override
		public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
			addMessage("ySliderMoved controller(" + controller + ") sliderCode(" + sliderCode + ") value(" + value + ")");
			return false;
		}

		@Override
		public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
			addMessage("accelerometerMoved controller(" + controller + ") accelerometerCode(" + accelerometerCode + ") value(" + value + ")");
			return false;
		}
	}
}

