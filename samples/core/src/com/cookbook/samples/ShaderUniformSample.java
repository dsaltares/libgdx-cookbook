package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShaderUniformSample extends GdxSample {
	private enum State {
		TransitionIn,
		TransitionOut,
		Picture,
	}
	
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private static final float TRANSITION_IN_TIME = 1.0f;
	private static final float TRANSITION_OUT_TIME = 0.5f;
	private static final float PICTURE_TIME = 2.0f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture background;
	private ShaderProgram shader;
	private State state;
	private float time;
	private float resolution[];
	private float radius;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		background = new Texture(Gdx.files.internal("data/jungle-level.png"));
		shader = new ShaderProgram(Gdx.files.internal("data/shaders/vignette.vert"), Gdx.files.internal("data/shaders/vignette.frag"));
		resolution = new float[2];
		
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		batch.setShader(shader);
		
		if (!shader.isCompiled()) {
			Gdx.app.error("Shader", shader.getLog());
		}
		
		state = State.TransitionIn;
		time = 0.0f;
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		shader.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		switch(state) {
		case TransitionIn:
			radius = time / TRANSITION_IN_TIME;
			
			if (time > TRANSITION_IN_TIME) {
				time = 0.0f;
				state = State.Picture;
			}
			
			break;
		case TransitionOut:
			radius = 1.0f - time / TRANSITION_OUT_TIME;
			
			if (time > TRANSITION_OUT_TIME) {
				time = 0.0f;
				state = State.Picture;
			}
			
			break;
		case Picture:
			if (time > PICTURE_TIME) {
				time = 0.0f;
				state = radius == 0.0f ? State.TransitionIn : State.TransitionOut;
			}
			break;
		}
		
		radius = MathUtils.clamp(radius, 0.0f, 1.0f);
		time += Gdx.graphics.getDeltaTime();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
        shader.setUniform2fv("resolution", resolution , 0, 2);
        shader.setUniformf("radius", radius);
		
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
	public void resize(int width, int height) {
		viewport.update(width, height);
		
		resolution[0] = width;
		resolution[1] = height;
	}
}
