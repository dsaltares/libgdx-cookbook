package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ParticleEffectsSample extends GdxSample {
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private ParticleEffect[] effects;
	private int currentEffect;
	private Vector3 touchPos;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		touchPos = new Vector3();
		
		effects = new ParticleEffect[3];
		currentEffect = 0;
		
		effects[0] = new ParticleEffect();
		effects[0].load(Gdx.files.internal("data/fire.particle"), Gdx.files.internal("data"));
		
		effects[1] = new ParticleEffect();
		effects[1].load(Gdx.files.internal("data/stars.particle"), Gdx.files.internal("data"));
		
		effects[2] = new ParticleEffect();
		effects[2].load(Gdx.files.internal("data/ice.particle"), Gdx.files.internal("data"));
		
		for (ParticleEffect effect : effects) {
			effect.start();
		}
		
		ParticleEffect explosionEffect = new ParticleEffect();
		explosionEffect.load(Gdx.files.internal("data/explosion.particle"), Gdx.files.internal("data"));
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		batch.dispose();
		
		for (ParticleEffect effect : effects) {
			effect.dispose();
		}
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
		camera.unproject(touchPos);
		
		for (ParticleEffect effect : effects) {
			effect.setPosition(touchPos.x, touchPos.y);
			
			if (effect.isComplete()) {
				effect.reset();
			}
		}
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		effects[currentEffect].draw(batch, Gdx.graphics.getDeltaTime());
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		currentEffect = (currentEffect + 1) % effects.length;
		return true;
	}
}