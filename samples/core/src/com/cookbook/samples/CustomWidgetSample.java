package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.scene2d.LevelSelector;

public class CustomWidgetSample extends GdxSample {
	private static final String TAG = "Custom Widget";
	private static final int SCENE_WIDTH = 1280;
	private static final int SCENE_HEIGHT = 720;
	
	private SpriteBatch batch;
	
	private Viewport viewport;
	
	private Skin skin;
	
	private Table table;
	private Stage stage;
	
	LevelSelector levelSelector;
	Texture jungleTex, mountainsTex;
	
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		batch = new SpriteBatch();
		
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		skin = new Skin(Gdx.files.internal("data/scene2d/customUI.json"));
		
		// Create table
		table = new Table();
		
		//Level selection menu
		Label level_menu = new Label("Level Selection Menu", skin);
		
		jungleTex = new Texture(Gdx.files.internal("data/jungle-level.png"));
		mountainsTex = new Texture(Gdx.files.internal("data/blur/mountains.png"));
		
		// Populate level container
		Array<LevelSelector.Level> levels = new Array<LevelSelector.Level>();
		
		LevelSelector.Level level1 = new LevelSelector.Level("Level1", skin);
		level1.setImage( new Image(new TextureRegionDrawable(new TextureRegion(jungleTex))));
		
		LevelSelector.Level level2 = new LevelSelector.Level("Level2", skin);
		level2.setImage(new Image(new TextureRegionDrawable(new TextureRegion(mountainsTex))));
		
		LevelSelector.Level level3 = new LevelSelector.Level("Level3", skin);
		level3.setImage(new Image(new TextureRegionDrawable(new TextureRegion(jungleTex))));
		
		levels.addAll(level1, level2, level3);
		
		levelSelector = new LevelSelector(skin);
		levelSelector.addLevels(levels);
		
		table.row();
		table.add(level_menu).padBottom(20f);
		table.row();
		table.add(levelSelector);
		
		table.setFillParent(true);
		table.pack();
		
		stage.addActor(table);
		
		// Start game button listener
		levelSelector.getButton().addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "START LEVEL " + levelSelector.getCurrentLevel());
			};
		});
		
		table.debug();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		jungleTex.dispose();
		mountainsTex.dispose();
		batch.dispose();
		skin.dispose();
		stage.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		stage.draw();
	}
}
