package com.cookbook.samples;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.ashley.MovementComponent;
import com.cookbook.ashley.MovementSystem;
import com.cookbook.ashley.RenderSystem;
import com.cookbook.ashley.SizeComponent;
import com.cookbook.ashley.TextureComponent;
import com.cookbook.ashley.TransformComponent;
import com.cookbook.ashley.UserControlledComponent;
import com.cookbook.ashley.UserControlledSystem;

public class AshleySample extends GdxSample {
	private static final String TAG = "AshleySample";

	private static final float SCENE_WIDTH = 12.8f;
	private static final float SCENE_HEIGHT = 7.2f;

	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Engine ashleyEngine;
	private Entity caveman;


	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		// Centers camera
		viewport.getCamera().position.set(
				viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
				0);
		viewport.getCamera().update();
		viewport.update((int)SCENE_WIDTH, (int)SCENE_HEIGHT);
		camera.update();
		
		// Initializes ashely engine
		ashleyEngine = new Engine();
		
		// Creates systems and add them to the engine
		MovementSystem movementSystem = new MovementSystem();
		RenderSystem renderSystem = new RenderSystem(camera);
		UserControlledSystem userControlledSystem = new UserControlledSystem(camera);
		ashleyEngine.addSystem(movementSystem);
		ashleyEngine.addSystem(renderSystem);
		ashleyEngine.addSystem(userControlledSystem);
		
		// Creates a caveman entity from several components
		caveman = new Entity();

		TextureComponent texture = new TextureComponent();
		TransformComponent transform = new TransformComponent();
		SizeComponent size = new SizeComponent();
		MovementComponent movement = new MovementComponent();
		UserControlledComponent userControlled = new UserControlledComponent();
		
		texture.region = new TextureRegion(new Texture(Gdx.files.internal("data/caveman.png")));
		size.width = 1f;
		size.height = 1.5f;
		transform.pos.set(SCENE_WIDTH*.5f - size.width*.5f, SCENE_HEIGHT*.5f - size.height*.5f);

		caveman.add(texture);
		caveman.add(transform);
		caveman.add(size);
		caveman.add(movement);
		caveman.add(userControlled);
		ashleyEngine.addEntity(caveman);
		
		// updates entities references to render
		renderSystem.addedToEngine(ashleyEngine);
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		ashleyEngine.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

}

