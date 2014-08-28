package com.cookbook.samples;


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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DSimpleSample extends GdxSample {
	private static final String TAG = "Box2DSimpleSample";

	private static final float SCENE_WIDTH = 12.8f; // 13 metres wide
	private static final float SCENE_HEIGHT = 7.2f; // 7 metres high

	private Viewport viewport;
	private Vector3 point = new Vector3();
	private SpriteBatch batch;

	// General Box2D
	Box2DDebugRenderer debugRenderer;
	BodyDef defaultDynamicBodyDef;
	World world;
	
	// Squares
	FixtureDef boxFixtureDef;
	PolygonShape square;
	
	// Circles
	CircleShape circle;
	FixtureDef circleFixtureDef;
	
	//To switch between boxes and balls
	boolean boxMode = true;

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
		world = new World(new Vector2(0,-9.8f), true);

		// Tweak debug information
		debugRenderer = new Box2DDebugRenderer(
				true, /* draw bodies */
				false, /* don't draw joints */
				true, /* draw aabbs */
				true, /* draw inactive bodies */
				false, /* don't draw velocities */
				true /* draw contacts */);
		
		// Creates a ground to avoid objects falling forever
		createGround();

		// Default Body Definition
		defaultDynamicBodyDef = new BodyDef();
		defaultDynamicBodyDef.type = BodyType.DynamicBody;

		// Shape for square
		square = new PolygonShape();
		// 1 meter-sided square
		square.setAsBox(0.5f, 0.5f);
		
		// Shape for circles
		circle = new CircleShape();
		// 0.5 metres for radius
		circle.setRadius(0.5f);
		
		// Fixture definition for our shapes
		boxFixtureDef = new FixtureDef();
		boxFixtureDef.shape = square;
		boxFixtureDef.density = 0.8f;
		boxFixtureDef.friction = 0.8f;
		boxFixtureDef.restitution = 0.15f;
		
		// Fixture definition for our shapes
		circleFixtureDef = new FixtureDef();
		circleFixtureDef.shape = circle;
		circleFixtureDef.density = 0.5f;
		circleFixtureDef.friction = 0.4f;
		circleFixtureDef.restitution = 0.6f;

	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			
			//Translate screen coordinates into world units
			viewport.getCamera().unproject(point.set(screenX, screenY, 0));
			
			if(boxMode)
				createSquare(point.x,point.y);
			else // Circle mode
				createCircle(point.x,point.y);
			boxMode = !boxMode;
			return true;     
		}
		return false;
	}

	private void createSquare(float x, float y) {
		defaultDynamicBodyDef.position.set(x,y);
		
		Body body = world.createBody(defaultDynamicBodyDef);

		body.createFixture(boxFixtureDef);
	}

	private void createCircle(float x, float y) {
		defaultDynamicBodyDef.position.set(x,y);
		
		Body body = world.createBody(defaultDynamicBodyDef);

		body.createFixture(circleFixtureDef);
	}

	
	private void createGround() {

		float halfGroundWidth = SCENE_WIDTH;
		float halfGroundHeight = 0.5f; // 1 meter high

		// Create a static body definition
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.type = BodyType.StaticBody;
		
		// Set the ground position
		groundBodyDef.position.set(halfGroundWidth*0.5f, halfGroundHeight);
		
		// Create a body from the defintion and add it to the world
		Body groundBody = world.createBody(groundBodyDef);  
		
		// Create a rectangle shape which will fit the world_width and 1 meter high
		// (setAsBox takes half-width and half-height as arguments)
		PolygonShape groundBox = new PolygonShape();  
		groundBox.setAsBox(halfGroundWidth * 0.5f, halfGroundHeight);
		// Create a fixture from our rectangle shape and add it to our ground body  
		groundBody.createFixture(groundBox, 0.0f);
		// Free resources
		groundBox.dispose();

	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		batch.dispose();
		square.dispose();
		circle.dispose();
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
