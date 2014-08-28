package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.audio.SoundInstance;
import com.cookbook.audio.SoundManager;

public class SpatialAudioSample extends GdxSample {
	private static final float UNITS = 1.0f / 32.0f;
	private static final float VIRTUAL_WIDTH = 1280 * UNITS;
	private static final float VIRTUAL_HEIGHT = 720 * UNITS;
	
	private SoundManager soundManager;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private TextureRegion emitterTexture;
	private TextureRegion playerTexture;
	
	private Player player;
	private Array<SoundEmitter> emitters;
	
	@Override
	public void create() {
		soundManager = new SoundManager(Gdx.files.internal("data/sfx/spatial-audio.json"));
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		batch = new SpriteBatch();
		
		emitterTexture = new TextureRegion(new Texture(Gdx.files.internal("data/sfx/emitter.png")));
		playerTexture = new TextureRegion(new Texture(Gdx.files.internal("data/sfx/player.png")));
		
		player = new Player();
		
		createSoundEmitters();
	}
	
	@Override
	public void dispose() {
		soundManager.dispose();
		batch.dispose();
		emitterTexture.getTexture().dispose();
		playerTexture.getTexture().dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		soundManager.updateListener(player.position, player.direction);
		soundManager.update();
		
		player.update(Gdx.graphics.getDeltaTime());
		
		for (SoundEmitter emitter : emitters) {
			emitter.update();
		}
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		float width =  emitterTexture.getRegionWidth();
		float height =  emitterTexture.getRegionHeight();
		float originX = width * 0.5f;
		float originY = height * 0.5f;
		
		for (SoundEmitter emitter : emitters) {
			Color oldColor = batch.getColor();
			batch.setColor(emitter.color);
			batch.draw(emitterTexture,
					   emitter.position.x - originX, emitter.position.y - originY,
					   originX, originY,
					   width, height,
					   1.0f * UNITS, 1.0f * UNITS,
					   0.0f);
			batch.setColor(oldColor);
		}
		
		width =  playerTexture.getRegionWidth();
		height =  playerTexture.getRegionHeight();
		originX = width * 0.5f;
		originY = height * 0.5f;
		
		batch.draw(playerTexture,
				   player.position.x - originX, player.position.y - originY,
				   originX, originY,
				   width, height,
				   1.0f * UNITS, 1.0f * UNITS,
				   player.direction.angle());;
		
		batch.end();
	}
	
	private void createSoundEmitters() {
		emitters = new Array<SoundEmitter>();
		
		SoundEmitter emitter = new SoundEmitter();
		emitter.name = "data/sfx/sfx_01.wav";
		emitter.position.set(-18.0f, 0.0f);
		emitter.color = Color.BLUE;
		emitters.add(emitter);
		
		emitter = new SoundEmitter();
		emitter.name = "data/sfx/sfx_02.wav";
		emitter.position.set(-9.0f, 0.0f);
		emitter.color = Color.GREEN;
		emitters.add(emitter);
		
		emitter = new SoundEmitter();
		emitter.name = "data/sfx/sfx_03.mp3";
		emitter.position.set(0.0f, 0.0f);
		emitter.color = Color.YELLOW;
		emitters.add(emitter);
		
		emitter = new SoundEmitter();
		emitter.name = "data/sfx/sfx_04.wav";
		emitter.position.set(9.0f, 0.0f);
		emitter.color = Color.ORANGE;
		emitters.add(emitter);
		
		emitter = new SoundEmitter();
		emitter.name = "data/sfx/sfx_05.mp3";
		emitter.position.set(18.0f, 0.0f);
		emitter.color = Color.RED;
		emitters.add(emitter);
	}
	
	private class SoundEmitter {
		String name = new String();
		Vector2 position = new Vector2();
		Color color = new Color(Color.WHITE);
		SoundInstance soundInstance;
		
		public void update() {
			if (soundInstance == null || soundInstance.isFinished()) {
				soundInstance = soundManager.play(name);
			}
			
			soundInstance.setPosition(position);
		}
	}
	
	private class Player {
		final float speed = 10.0f;
		
		Vector2 position =  new Vector2();
		Vector2 direction = new Vector2(1.0f, 0.0f);
		Vector2 movement = new Vector2();
		Vector2 mousePos = new Vector2();
		
		public void update(float delta) {
			// Update position
			movement.set(0.0f, 0.0f);
			
			if (Gdx.input.isKeyPressed(Keys.A)) {
				movement.x = -1.0f;
			}
			else if (Gdx.input.isKeyPressed(Keys.D)) {
				movement.x = 1.0f;
			}
			
			if (Gdx.input.isKeyPressed(Keys.S)) {
				movement.y = -1.0f;
			}
			else if (Gdx.input.isKeyPressed(Keys.W)) {
				movement.y = 1.0f;
			}
			
			if (movement.len2() > 0.0f) {
				movement.nor();
				position.add(movement.scl(speed * delta));
			}
			
			// Update direction
			mousePos.set(Gdx.input.getX(), Gdx.input.getY());
			viewport.unproject(mousePos);
			direction.set(mousePos).sub(position).nor();
		}
	}
}
