package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.platforms.PlatformResolver;

public class PlatformSpecificSample extends GdxSample {
	private static final String TAG = "PlatformSpecificSample";
	
	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;

	private TextButton textButton;
	private BitmapFont font;
	private Stage stage;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		
		batch = new SpriteBatch();
		
		stage = new Stage(viewport, batch);
		
		Gdx.input.setInputProcessor(stage);
		
		font = new BitmapFont(Gdx.files.internal("data/default.fnt"));
		
		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.font = font;
		TextureRegion buttonRegion = new TextureRegion(new Texture(Gdx.files.internal("data/scene2d/myactor.png")));
		tbs.up = new TextureRegionDrawable(buttonRegion);
		textButton = new TextButton("Rate my game!", tbs);
		float ratio = textButton.getWidth() / textButton.getHeight();
		textButton.setWidth(300f);
		textButton.setHeight(300f/ratio);
		textButton.setPosition(SCENE_WIDTH * 0.5f - textButton.getWidth()*0.5f, SCENE_HEIGHT * 0.5f);
		textButton.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getPlatformResolver().rateGame();
			};
		});
		
		stage.addActor(textButton);
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();

	}

	@Override
	public void render() {
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

}

