package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CarEngineSample extends GdxSample {
	
	private static final int VIRTUAL_WIDTH = 640;
	private static final int VIRTUAL_HEIGHT = 360;
	
	private final static float MAX_SPEED = 200.0f;
	private final static float ACCELERATION = 25.0f;
	private final static float FRICTION = 15.0f;
	private final static float IDLE_THRESHOLD = 0.1f;
	
	private float speed;
	private Sound engine;
	private Sound idle;
	private long soundId;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		font.setColor(Color.WHITE);
		font.setScale(2.5f);
		camera.position.set(VIRTUAL_WIDTH * 0.5f, VIRTUAL_HEIGHT * 0.5f, 0.0f);
		
		idle = Gdx.audio.newSound(Gdx.files.internal("data/sfx/car-idle.wav"));
		engine = Gdx.audio.newSound(Gdx.files.internal("data/sfx/car-engine.wav"));
		soundId = idle.play();
		idle.setLooping(soundId, true);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		engine.dispose();
		idle.dispose();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.39f, 0.58f, 0.92f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		updateEngine(Gdx.graphics.getDeltaTime());
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		font.draw(batch, "Speed: " + speed + "km/h", 20.0f, 200.0f);
		font.draw(batch, "Press SPACE or touch to accelerate", 20.0f, 150.0f);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	private void updateEngine(float delta) {
		boolean wasIdle = speed < IDLE_THRESHOLD;
		
		// Update speed
		float acceleration = -FRICTION;
		
		if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isButtonPressed(Buttons.LEFT)) {
			acceleration = ACCELERATION;
		}
		
		speed = MathUtils.clamp(speed + acceleration * delta, 0.0f, MAX_SPEED);
		
		boolean isIdle =  speed < IDLE_THRESHOLD;
		
		// Update sounds
		if (wasIdle && !isIdle) {
			idle.stop();
			soundId = engine.play();
			engine.setLooping(soundId, true);
		}
		else if (!wasIdle && isIdle) {
			engine.stop();
			soundId = idle.play();
			idle.setLooping(soundId, true);
		}
		
		if (!isIdle) {
			float pitch = 0.5f + speed / MAX_SPEED * 0.5f;
			engine.setPitch(soundId, pitch);
		}
	}
}
