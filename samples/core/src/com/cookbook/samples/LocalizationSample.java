package com.cookbook.samples;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.cookbook.localization.LanguageManager;

public class LocalizationSample extends GdxSample {
	private static final String TAG = "LocalisationSample";
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private Vector3 point = new Vector3();
	
	private BitmapFont font;
	private LanguageManager lm;
	
	private FileHandle englishFileHandle, spanishFileHandle;
	private String title, introduction, body;
	private float textWidth, textXOrigin;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		// Center camera
		viewport.getCamera().position.set(
				viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
				0);
		viewport.getCamera().update();
		viewport.update((int)SCENE_WIDTH, (int)SCENE_HEIGHT);
		
		batch = new SpriteBatch();
		
		font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"));
		font.setColor(Color.WHITE);
		
		lm = new LanguageManager();
		
		englishFileHandle = Gdx.files.internal("i18n/strings_en_GB");
		spanishFileHandle = Gdx.files.internal("i18n/strings_es_ES");
		
		lm.loadLanguage("english", englishFileHandle, Locale.UK);
		lm.loadLanguage("spanish", spanishFileHandle, new Locale("es", "ES"));
		lm.setCurrentLanguage("english");
		
		initializeText();
		
		Gdx.input.setInputProcessor(this);
		
	}

	private void initializeText() {
		I18NBundle b = lm.getCurrentBundle();
		title = b.get("bookTitle");
		introduction = b.format("introduction", 81);
		body = b.get("body");
		
		viewport.getCamera().project(point.set(SCENE_WIDTH * .70f, 0, 0));
		textWidth = point.x;
		viewport.getCamera().project(point.set(SCENE_WIDTH * .15f, 0, 0));
		textXOrigin = point.x;
	}
	
	private void translate() {
		if(lm.getCurrentLanguage().compareTo("english") == 0)
			lm.setCurrentLanguage("spanish");
		else
			lm.setCurrentLanguage("english");
		
		initializeText();
	}
	
	@Override
	public void dispose() {
		batch.dispose();

	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			translate();
		}
		return true;
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		batch.begin();
		viewport.getCamera().project(point.set(SCENE_WIDTH*0.5f, 6.8f, 0));
		font.draw(batch, title, textXOrigin, point.y);
		viewport.getCamera().project(point.set(SCENE_WIDTH*0.5f, 6.5f, 0));
		font.draw(batch, introduction, textXOrigin, point.y);
		viewport.getCamera().project(point.set(SCENE_WIDTH*0.5f, 6.2f, 0));
		font.draw(batch, body, textXOrigin, point.y, textWidth, Align.left, true);
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

}

