package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class SkinCustomizationSample extends GdxSample {
	private static final String TAG = "Skin Customization";
	private static final int SCENE_WIDTH = 1280;
	private static final int SCENE_HEIGHT = 720;
	
	private SpriteBatch batch;
	
	private Viewport viewport;
	
	private Skin skin;
	private Slider slider;
	private TextButton button1, button2;
	private CheckBox checkbox1;
	private Label titleLabel;
	private Touchpad touchpad;
	
	private Table table;
	private Stage stage;
	
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		batch = new SpriteBatch();
		
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);
		
		//Define the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.CYAN);
		textButtonStyle.down = skin.newDrawable("white", Color.ORANGE);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.YELLOW);
		textButtonStyle.font = skin.getFont("default-font");
		skin.add("default2", textButtonStyle);
		
		// Title Label
		titleLabel = new Label("Skin Customization", skin);
		
		// Button
		button1 = new TextButton("SkinDefaultButton", skin);
		button2 = new TextButton("SkinTweakedButton", skin, "default2");
		
		// Checkbox
		checkbox1 = new CheckBox("CheckBox1", skin);
		
		//Tree
		Label node1 = new Label("Root-node", skin);
		Label node2 = new Label("Child-node1", skin);
		Label node3 = new Label("Child-node2", skin);
		Tree tree = new Tree(skin);
		tree.add(new Tree.Node(node1));
		tree.getNodes().get(0).add(new Tree.Node(node2));
		tree.getNodes().get(0).add(new Tree.Node(node3));
		tree.expandAll();
		tree.pack();
		
		//Slider
		slider = new Slider(0f, 100f, 1f, false, skin);
		
		//Touchpad
		touchpad = new Touchpad(2f, skin);
		
		// Create table
		table = new Table();
		table.debug(); //Enables debug
		
		// Set table structure
		table.row();
		table.add(titleLabel).colspan(6).padBottom(10);
		table.row();
		table.add(button1);
		table.add(button2).padLeft(10);
		table.add(slider).padLeft(10);
		table.add(tree).padLeft(10);
		table.add(checkbox1).padLeft(10);
		table.add(touchpad).padLeft(30);
		
		// Pack table
		table.setFillParent(true);
		table.pack();
		
		// Set table's alpha to 0
		table.getColor().a = 0f;
		
		// Adds created table to stage
		stage.addActor(table);

		// To make the table appear smoothly
		table.addAction(fadeIn(2f));
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
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
