package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BlurSample extends GdxSample {
	private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 720;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture background;
	private Texture foreground;
	private Texture mountains;
	private Texture rock;
	private Texture dinosaur;
	private Texture caveman;
	private ShaderProgram shader;
	private FrameBuffer fboA;
	private FrameBuffer fboB;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		
		batch = new SpriteBatch();
		
		background = new Texture(Gdx.files.internal("data/blur/background.png"));
		foreground = new Texture(Gdx.files.internal("data/blur/foreground.png"));
		mountains = new Texture(Gdx.files.internal("data/blur/mountains.png"));
		rock = new Texture(Gdx.files.internal("data/blur/rock.png"));
		caveman = new Texture(Gdx.files.internal("data/blur/caveman.png"));
		dinosaur = new Texture(Gdx.files.internal("data/blur/dinosaur.png"));
		
		shader = new ShaderProgram(Gdx.files.internal("data/shaders/blur.vert"), Gdx.files.internal("data/shaders/blur.frag"));
		fboA = new FrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		fboB = new FrameBuffer(Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false);
		
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);

		if (!shader.isCompiled()) {
			Gdx.app.error("Shader", shader.getLog());
			Gdx.app.exit();
		}
		
		shader.begin();
		shader.setUniformf("resolution", VIRTUAL_WIDTH);
		shader.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		background.dispose();
		foreground.dispose();
		mountains.dispose();
		caveman.dispose();
		dinosaur.dispose();
		background.dispose();
		shader.dispose();
		fboA.dispose();
		fboB.dispose();
	}

	@Override
	public void render() {		
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch.begin();
		
		// Draw background as-is
		batch.setShader(null);
		drawTexture(background,  0.0f, 0.0f);
		batch.flush();
		
		// Draw blurred mountains
		fboA.begin();
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(null);
		drawTexture(mountains, 0.0f, 0.0f);
		batch.flush();
		fboA.end();
		applyBlur(3.0f);
		
		// Draw foreground and characters without blur effect
		batch.setShader(null);
		drawTexture(foreground, 0.0f, 0.0f);
		drawTexture(caveman, 1.0f, 1.5f);
		drawTexture(dinosaur, 6.0f, 2.45f);
		batch.flush();
		
		// Draw blurred rock
		fboA.begin();
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(null);
		drawTexture(rock, 0.0f, 0.0f);
		batch.flush();
		fboA.end();
		applyBlur(5.0f);
		
		batch.end();
	}
	
	private void applyBlur(float blur) {		
		// Horizontal blur from FBO A to FBO B
		fboB.begin();
		batch.setShader(shader);
		shader.setUniformf("dir", 1.0f, 0.0f);
		shader.setUniformf("radius", blur);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		drawTexture(fboA.getColorBufferTexture(),  0.0f, 0.0f);
		batch.flush();
		fboB.end();
		
		// Vertical blur from FBO B to the screen
		shader.setUniformf("dir", 0.0f, 1.0f);
		shader.setUniformf("radius", blur);
		drawTexture(fboB.getColorBufferTexture(), 0.0f, 0.0f);
		batch.flush();
	}
	
	private void drawTexture(Texture texture, float x, float y) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		
		batch.draw(texture,
				   x, y,
				   0.0f, 0.0f,
				   width, height,
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   width, height,
				   false, false);
	}
}
