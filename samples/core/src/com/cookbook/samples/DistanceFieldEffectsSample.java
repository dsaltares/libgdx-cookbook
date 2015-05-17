package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DistanceFieldEffectsSample extends GdxSample {
	private static final int VIRTUAL_WIDTH = 1280;
	private static final int VIRTUAL_HEIGHT = 720;
	
	private static final Color OUTLINE_COLOR = new Color(0x00222b);
	private static final Color GLOW_COLOR = new Color(0xffe680);
	
	private static final Vector2 OUTLINE = new Vector2(0.45f, 0.55f);
	private static final Vector2 GLOW = new Vector2(0.00f, 0.45f);
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShaderProgram fontShader;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH  * 0.5f, VIRTUAL_HEIGHT * 0.5f, 0.0f);
		
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		
		batch = new SpriteBatch();
		
		font = new BitmapFont(Gdx.files.internal("data/fonts/pacifico-distance.fnt"));
		font.setColor(Color.valueOf("5fbcd3"));
		font.getData().setScale(3.0f);
		
		Texture texture = font.getRegion().getTexture();
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		fontShader = new ShaderProgram(Gdx.files.internal("data/fonts/font-effects.vert"),
									   Gdx.files.internal("data/fonts/font-effects.frag"));
		
		if (!fontShader.isCompiled()) {
		    Gdx.app.error(this.getClass().getSimpleName(),
		    			  "Shader compilation failed:\n" + fontShader.getLog());
		}
		
		batch.setShader(fontShader);
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
		fontShader.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		fontShader.setUniformf("u_glow", GLOW);
		fontShader.setUniformf("u_outline", OUTLINE);
		fontShader.setUniformf("u_glowColor", GLOW_COLOR);
		fontShader.setUniformf("u_outlineColor", OUTLINE_COLOR);
		
		fontShader.setUniformi("u_enableGlow", 0);
		fontShader.setUniformi("u_enableOutline", 0);
		font.draw(batch, " No effects", 20.0f, VIRTUAL_HEIGHT - 50.0f);
		batch.flush();
		
		fontShader.setUniformi("u_enableGlow", 0);
		fontShader.setUniformi("u_enableOutline", 1);
		font.draw(batch, "Just outline", 20.0f, VIRTUAL_HEIGHT - 200.0f);
		batch.flush();
		
		fontShader.setUniformi("u_enableGlow", 1);
		fontShader.setUniformi("u_enableOutline", 0);
		font.draw(batch, "Just glow", 20.0f, VIRTUAL_HEIGHT - 350.0f);
		batch.flush();
		
		fontShader.setUniformi("u_enableGlow", 1);
		fontShader.setUniformi("u_enableOutline", 1);
		font.draw(batch, "Outline and glow", 20.0f, VIRTUAL_HEIGHT - 500.0f);
		batch.flush();
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}

