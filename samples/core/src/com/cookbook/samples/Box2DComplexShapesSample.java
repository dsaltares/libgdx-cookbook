package com.cookbook.samples;


import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DComplexShapesSample extends GdxSample {
	private static final String TAG = "Box2DComplexShapesSample";
	
	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private Vector3 point = new Vector3();
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	BodyDef defaultDynamicBodyDef;
	World world;
	
	// Ball
	CircleShape circle, circle2, circle3;
	FixtureDef circleFixtureDef, circleFixtureDef2, circleFixtureDef3;
	
	// Squares
	FixtureDef boxFixtureDef;
	PolygonShape square;

	// Glass
	private Body glassBody;
	private static final float GLASS_WIDTH = 3.0f;
	
	private static final int NUM_OF_EXAMPLES = 3;
	int currentShape = 0;
	
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		// Center camera to get (0,0) as the origin of the Box2D world
		viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f
				, 0);
		viewport.getCamera().update();
		
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-10), true);

		// Instantiate DebugRenderer for rendering shapes
		debugRenderer = new Box2DDebugRenderer();

		// Creates a ground to avoid objects falling forever
		createChainShape();
		
		createGlass();

		// Default Body Definition
		defaultDynamicBodyDef = new BodyDef();
		defaultDynamicBodyDef.type = BodyType.DynamicBody;
		
		// Shape for circles
		circle = new CircleShape();
		// 0.1 meter for radius
		circle.setRadius(0.1f);
		// Fixture definition for our shapes
		circleFixtureDef = new FixtureDef();
		circleFixtureDef.shape = circle;
		circleFixtureDef.density = 0.8f;
		circleFixtureDef.friction = 0.4f;
		circleFixtureDef.restitution = 0.5f;
		
		// Shape for circles of complex shapes
		circle2 = new CircleShape();
		// 0.12 meter for radius
		circle2.setRadius(0.12f);
		// Fixture definition for our shapes
		circleFixtureDef2 = new FixtureDef();
		circleFixtureDef2.shape = circle2;
		circleFixtureDef2.density = 0.8f;
		circleFixtureDef2.friction = 0.4f;
		circleFixtureDef2.restitution = 0.5f;
		
		// Shape for circles of complex shapes with different position
		circle3 = new CircleShape();
		// 0.1 meter for radius
		circle3.setRadius(0.1f);
		circle3.setPosition(new Vector2(.2f,.0f));
		// Fixture definition for our shapes
		circleFixtureDef3 = new FixtureDef();
		circleFixtureDef3.shape = circle3;
		circleFixtureDef3.density = 0.8f;
		circleFixtureDef3.friction = 0.4f;
		circleFixtureDef3.restitution = 0.5f;
		
		// Shape for square
		square = new PolygonShape();
		// 0.2 meter-sided square
		square.setAsBox(0.1f, 0.1f);
		// Fixture definition for our shapes
		boxFixtureDef = new FixtureDef();
		boxFixtureDef.shape = square;
		boxFixtureDef.density = 0.8f;
		boxFixtureDef.friction = 0.8f;
		boxFixtureDef.restitution = 0.15f;

	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			
			//Translate screen coordinates into world units
			viewport.getCamera().unproject(point.set(screenX, screenY, 0));
			
			switch(currentShape) {
			case 0: 
				createCircle(point.x,point.y);
				break;
			case 1:
				createComplexShape1(point.x,point.y);
				break;
			case 2:
				createComplexShape2(point.x,point.y);
				break;
			}
			
			currentShape = ++currentShape % NUM_OF_EXAMPLES;
			
			return true;     
		}
		return false;
	}

	private void createCircle(float x, float y) {
		defaultDynamicBodyDef.position.set(x,y);
		
		Body body = world.createBody(defaultDynamicBodyDef);

		body.createFixture(circleFixtureDef);
	}

	//Same body, two fixtures, same origin
	private void createComplexShape1(float x, float y) {
		defaultDynamicBodyDef.position.set(x,y);
		
		Body body = world.createBody(defaultDynamicBodyDef);
		body.createFixture(boxFixtureDef);
		body.createFixture(circleFixtureDef2);
	}
	
	//Same body, two fixtures, different position
	private void createComplexShape2(float x, float y) {
		defaultDynamicBodyDef.position.set(x,y);
		
		Body body = world.createBody(defaultDynamicBodyDef);
		body.createFixture(boxFixtureDef);
		body.createFixture(circleFixtureDef3);
	}
	
	private void createChainShape() {
		ChainShape chainShape = new ChainShape();
		
		chainShape.createLoop(new Vector2[] {
				new Vector2(.0f, .0f), 
				new Vector2(SCENE_WIDTH, .0f), 
				new Vector2(SCENE_WIDTH, 1.5f),
				new Vector2(6f, 1.5f),
				new Vector2(3, 5.0f),
				new Vector2(1.5f, 1.5f),
				new Vector2(0, 1.5f),});
		
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		chainBodyDef.position.set(.0f, .0f);
		Body chainBody = world.createBody(chainBodyDef);
		chainBody.createFixture(chainShape, 0);
		chainShape.dispose();
	}
	
	private void createGlass() {
		// Instantiate the loader with the created JSON data
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/box2D/glass.json"));

		// Create the glass body definition and place it in within the world
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(7, 1.5f);

		// Set physics properties
		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.friction = 0.5f;
		fd.restitution = 0.05f;

		// Create the glass body
		glassBody = world.createBody(bd);

		// Magic happens here!! Glass fixture is generated automatically by the loader.
		loader.attachFixture(glassBody, "Glass", fd, GLASS_WIDTH);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();

		batch.dispose();
		circle.dispose();
		circle2.dispose();
		circle3.dispose();
		world.dispose();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// If the game doesn't render at 60fps, the physics will go mental. That'll be covered in Box2DFixedTimeStepSample
		world.step(1/60f, 6, 2);

		debugRenderer.render(world, viewport.getCamera().combined);
	}


}
