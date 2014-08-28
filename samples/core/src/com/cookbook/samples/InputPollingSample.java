package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputPollingSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/fonts/oswald-32.fnt"));
		font.setScale(0.8f);
		font.setColor(Color.WHITE);
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
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
		
		// Mouse/touch
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		boolean leftPressed = Gdx.input.isButtonPressed(Buttons.LEFT);
		boolean rightPressed = Gdx.input.isButtonPressed(Buttons.RIGHT);
		boolean middlePressed = Gdx.input.isButtonPressed(Buttons.MIDDLE);
		
		font.draw(batch, "Mouse/Touch: x=" + mouseX + " y=" + mouseY, 20.0f, SCENE_HEIGHT - 20.0f);
		font.draw(batch, leftPressed ? "Left button pressed" : "Left button not pressed", 20.0f, SCENE_HEIGHT - 50.0f);
		font.draw(batch, rightPressed ? "Right button pressed" : "Right button not pressed", 20.0f, SCENE_HEIGHT - 80.0f);
		font.draw(batch, middlePressed ? "Middle button pressed" : "Middle button not pressed", 20.0f, SCENE_HEIGHT - 110.0f);
		
		boolean wPressed = Gdx.input.isKeyPressed(Keys.W);
		boolean aPressed = Gdx.input.isKeyPressed(Keys.A);
		boolean sPressed = Gdx.input.isKeyPressed(Keys.S);
		boolean dPressed = Gdx.input.isKeyPressed(Keys.D);
		
		// Keys
		font.draw(batch, wPressed? "W is pressed" : "W is not pressed", 20.0f, SCENE_HEIGHT - 160.0f);
		font.draw(batch, aPressed? "A is pressed" : "A is not pressed", 20.0f, SCENE_HEIGHT - 190.0f);
		font.draw(batch, sPressed? "S is pressed" : "S is not pressed", 20.0f, SCENE_HEIGHT - 220.0f);
		font.draw(batch, dPressed? "D is pressed" : "D is not pressed", 20.0f, SCENE_HEIGHT - 250.0f);
		
		// Accelerometer
		float accelerometerX = Gdx.input.getAccelerometerX();
		float accelerometerY = Gdx.input.getAccelerometerY();
		float accelerometerZ = Gdx.input.getAccelerometerZ();
		font.draw(batch, "Accelerometer x=" + accelerometerX, 20.0f, SCENE_HEIGHT - 300.0f);
		font.draw(batch, "Accelerometer y=" + accelerometerY, 20.0f, SCENE_HEIGHT - 330.0f);
		font.draw(batch, "Accelerometer z=" + accelerometerZ, 20.0f, SCENE_HEIGHT - 360.0f);
		
		// Compass
		float pitch = Gdx.input.getPitch();
		float roll = Gdx.input.getRoll();
		float azimuth = Gdx.input.getAzimuth();
		font.draw(batch, "Compass pich: " + pitch, 20.0f, SCENE_HEIGHT - 410.0f);
		font.draw(batch, "Compass roll: " + roll, 20.0f, SCENE_HEIGHT - 440.0f);
		font.draw(batch, "Compass azimuth: " + azimuth, 20.0f, SCENE_HEIGHT - 470.0f);
		
		// Available systems
		boolean accelerometer = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
		boolean compass = Gdx.input.isPeripheralAvailable(Peripheral.Compass);
		boolean hardwareKeyboard = Gdx.input.isPeripheralAvailable(Peripheral.HardwareKeyboard);
		boolean multitouch = Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen);
		boolean screenKeyboard = Gdx.input.isPeripheralAvailable(Peripheral.OnscreenKeyboard);
		boolean vibrator = Gdx.input.isPeripheralAvailable(Peripheral.Vibrator);
		
		font.draw(batch, accelerometer ? "Accelerometer available" : "Accelerometer unavailable", 20.0f, SCENE_HEIGHT - 520.0f);
		font.draw(batch, compass ? "Compass available" : "Compass unavailable", 20.0f, SCENE_HEIGHT - 550.0f);
		font.draw(batch, hardwareKeyboard ? "Hardware keyboard available" : "Hardware keyboard unavailable", 20.0f, SCENE_HEIGHT - 580.0f);
		font.draw(batch, multitouch ? "Multitouch available" : "Multitouch unavailable", 20.0f, SCENE_HEIGHT - 610.0f);
		font.draw(batch, screenKeyboard ? "On screen keyboard available" : "On screen keyboard unavailable", 20.0f, SCENE_HEIGHT - 640.0f);
		font.draw(batch, vibrator ? "Vibrator available" : "Vibrator unavailable", 20.0f, SCENE_HEIGHT - 670.0f);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}

