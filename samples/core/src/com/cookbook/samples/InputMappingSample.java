package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.samples.inputmapping.InputActionListener;
import com.cookbook.samples.inputmapping.InputContext;
import com.cookbook.samples.inputmapping.InputProfile;

public class InputMappingSample extends GdxSample implements InputActionListener {
	private static final float SCENE_WIDTH = 1280.0f;
	private static final float SCENE_HEIGHT = 720.0f;
	
	private static final int MESSAGE_MAX = 15;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	
	private InputProfile profile;
	private InputContext gameContext;
	
	private Array<String> messages;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		font = new BitmapFont();
		messages = new Array<String>();
		
		font.getData().setScale(2.0f);
		font.setColor(Color.WHITE);
		camera.position.set(SCENE_WIDTH * 0.5f, SCENE_HEIGHT * 0.5f, 0.0f);
		
		profile = new InputProfile(Gdx.files.internal("data/input/profile.xml"));
		profile.setContext("Game");
		gameContext = profile.getContext();
				
		gameContext.addListener(this);
		
		Gdx.input.setInputProcessor(profile);
	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.39f, 0.58f, 0.92f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		font.draw(batch, gameContext.getState("Crouch") ? "crouching" : "not crouching", 50.0f, SCENE_HEIGHT - 20.0f);
		font.draw(batch, gameContext.getState("LookUp") ? "looking up" : "not looking up", 50.0f, SCENE_HEIGHT - 50.0f);
		font.draw(batch, gameContext.getState("MoveRight") ? "moving right" : "not moving right", 50.0f, SCENE_HEIGHT - 80.0f);
		font.draw(batch, gameContext.getState("MoveLeft") ? "moving left" : "not moving left", 50.0f, SCENE_HEIGHT - 110.0f);
		
		int numMessages = messages.size;
		for (int i = 0; i < numMessages; ++i) {
			font.draw(batch, messages.get(i), 50.0f, SCENE_HEIGHT - 160.0f - 30.0f * i);
		}
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public boolean OnAction(String action) {
		addMessage("Action -> " + action);
		return false;
	}
	
	private void addMessage(String message) {
		messages.add(message);
		
		if (messages.size > MESSAGE_MAX) {
			messages.removeIndex(0);
		}
	}
}

