package com.cookbook.samples;


import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DLightsSample extends GdxSample {
	private static final String TAG = "Box2DLightsSample";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private Vector3 point = new Vector3();
	private SpriteBatch batch;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private RayHandler rayHandler;
	private Light light;
	
	ShapeRenderer sr;
	
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		// Center camera
		viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f
				, 0);
		viewport.getCamera().update();
		
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-9.8f), true);
		// Instantiate the class in charge of drawing physics shapes
		debugRenderer = new Box2DDebugRenderer();
		// To add some color to the ground
		sr = new ShapeRenderer();

		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
		light = new PointLight(rayHandler, 32);
		light.setActive(false);
		light.setColor(Color.PURPLE);
		light.setDistance(1.5f);
		
		createBodies();
		Light conelight = new ConeLight(rayHandler, 32, Color.WHITE, 15, SCENE_WIDTH*0.5f, SCENE_HEIGHT-1, 270, 45);
	}

	private void createBodies() {

		// Create a static body definition
		BodyDef staticBodyDef = new BodyDef();  
		staticBodyDef.type = BodyType.StaticBody;

		//GROUND	
		Body groundBody = world.createBody(staticBodyDef);  
		PolygonShape groundBox = new PolygonShape();  
		groundBox.setAsBox(SCENE_WIDTH * 0.5f, 0.5f);
		groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();
	
		groundBody.setTransform(new Vector2(SCENE_WIDTH*0.5f, 0.5f), groundBody.getAngle());
		
		// BOX
		Body boxBody = world.createBody(staticBodyDef);
		PolygonShape box = new PolygonShape();  
		box.setAsBox(.5f, .5f);
		boxBody.createFixture(box, 0.0f);
		box.dispose();
		
		boxBody.setTransform(new Vector2(SCENE_WIDTH*0.5f, SCENE_HEIGHT*0.5f), groundBody.getAngle());
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			
			//Translate screen coordinates into world units
			viewport.getCamera().unproject(point.set(screenX, screenY, 0));

			light.setPosition(point.x, point.y);
			light.setActive(true);
			
			return true;     
		}
		return false;
	}
	
	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			
			light.setActive(false);
			
			return true;     
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		viewport.getCamera().unproject(point.set(x, y, 0));
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			light.setPosition(point.x, point.y);
		}
		return false;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();

		batch.dispose();
		rayHandler.dispose();
		world.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.step(1/60f, 6, 2);

		sr.setProjectionMatrix(viewport.getCamera().combined);
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.RED);
		sr.rect(0, 0, SCENE_WIDTH, 1f);
		sr.end();
		
		rayHandler.setCombinedMatrix(viewport.getCamera().combined);
		rayHandler.updateAndRender();
		
		debugRenderer.render(world, viewport.getCamera().combined);
	}


}
