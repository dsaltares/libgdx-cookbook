package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShaderSample extends GdxSample {
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private static final int NUM_SHADERS = 4;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture background;
	
	private ShaderProgram shaders[];
	private String shaderNames[];
	private int currentShader;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		
		background = new Texture(Gdx.files.internal("data/jungle-level.png"));
		
		shaders = new ShaderProgram[NUM_SHADERS];
		shaderNames = new String[NUM_SHADERS];
		currentShader = 0;
		
		shaders[0] = null;
		shaderNames[0] = "Null";
		shaders[1] = new ShaderProgram(Gdx.files.internal("data/shaders/grayscale.vert"),
									   Gdx.files.internal("data/shaders/grayscale.frag"));
		shaderNames[1] = "Grayscale";
		shaders[2] = new ShaderProgram(Gdx.files.internal("data/shaders/sepia.vert"),
									   Gdx.files.internal("data/shaders/sepia.frag"));
		shaderNames[2] = "Sepia";
		shaders[3] = new ShaderProgram(Gdx.files.internal("data/shaders/inverted.vert"),
									   Gdx.files.internal("data/shaders/inverted.frag"));
		shaderNames[3] = "Inverted";
		
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		
		Gdx.input.setInputProcessor(this);
		
		for (ShaderProgram shader : shaders) {
			if (shader != null && !shader.isCompiled()) {
				Gdx.app.error("ShaderSample: ", shader.getLog());
			}
		}
		
		Gdx.app.log("ShaderSample", "Switching to shader " + shaderNames[currentShader]);
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		
		for (ShaderProgram shader : shaders) {
			if (shader != null) {
				shader.dispose();
			}
		}
	}

	@Override
	public void render() {		
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
		
		batch.end();
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		currentShader = (currentShader + 1) % shaders.length;
		batch.setShader(shaders[currentShader]);
		
		Gdx.app.log("ShaderSample", "Switching to shader " + shaderNames[currentShader]);
		
		return true;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
