

package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FrameBufferParticleEffectSample extends GdxSample {
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 720;

	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture background;
	private ParticleEffectPool pool;
	private Array<PooledEffect> activeEffects;
	private Vector3 touchPos;
	private FrameBuffer particleBuffer;
	private TextureRegion particleRegion;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		touchPos = new Vector3();
		particleRegion = new TextureRegion();
		background = new Texture(Gdx.files.internal("data/jungle-level.png"));
		
		particleBuffer = new FrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		
		ParticleEffect explosionEffect = new ParticleEffect();
		explosionEffect.load(Gdx.files.internal("data/explosion.particle"), Gdx.files.internal("data"));
		pool = new ParticleEffectPool(explosionEffect, 10, 100);
		activeEffects = new Array<PooledEffect>();
		
		Gdx.input.setInputProcessor(this);
		
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		particleBuffer.dispose();
	}

	@Override
	public void render() {		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		camera.update();
		
		particleBuffer.bind();
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for (int i = 0; i < activeEffects.size; ) {
			PooledEffect effect = activeEffects.get(i);
			
			if (effect.isComplete()) {
				pool.free(effect);
				activeEffects.removeIndex(i);
			}
			else {
				effect.draw(batch, deltaTime);
				++i;
			}
		}
		
		particleRegion.setRegion(particleBuffer.getColorBufferTexture());
		particleRegion.flip(false, true);
		
		batch.end();
		
		FrameBuffer.unbind();
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		int width = background.getWidth();
		int height = background.getHeight();
		
		batch.draw(background,
				   0.0f, 0.0f,
				   0.0f, 0.0f,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
		
		width = particleRegion.getRegionWidth();
		height = particleRegion.getRegionHeight();
		
		batch.draw(particleRegion,
				   0.0f, 0.0f,
				   0.0f, 0.0f,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		PooledEffect effect = pool.obtain();
		
		if (effect != null) {
			touchPos.set(screenX, screenY, 0.0f);
			camera.unproject(touchPos);
			
			activeEffects.add(effect);
			effect.setPosition(touchPos.x, touchPos.y);
		}
		
		return true;
	}
}
