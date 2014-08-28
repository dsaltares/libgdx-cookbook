package com.cookbook.samples;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.box2d.RayCastManager;

public class Box2DDeferredRaycasterSample extends GdxSample {
	private static final String TAG = "Box2DDeferredRaycasterSample";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;
	Body groundBody;
	
	RayCastManager raycastManager;
	
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

		// Creates a ground to avoid objects falling forever
		createGround();
		
		// Fill scene with boxes
		// Create X boxes and place them randomly
		int numOfBoxes = 10;
		float boxWidth = 0.5f;
		float boxHeight = 0.5f;
		float minWidth = 1f + (boxWidth*0.5f);
		float minHeight = 1.5f + (boxHeight*0.5f);
		float maxWidth = SCENE_WIDTH - 1f - (boxWidth*0.5f);
		float maxHeight = SCENE_HEIGHT - (boxHeight*0.5f);
		Random rand = new Random();
		
		float x,y;
		for(int i=0; i<numOfBoxes; i++) {
			x = rand.nextFloat() * ( maxWidth - minWidth) + minWidth;
			y = rand.nextFloat() * ( maxHeight - minHeight) + minHeight;
			
			createPolygon(i, BodyType.DynamicBody, x, y, 1f, 0.2f, 0.8f, boxWidth*0.5f, boxHeight*0.5f);
		}

		// Instantiate the class in charge of drawing physics shapes
		debugRenderer = new Box2DDebugRenderer();
		
		// RayCastManager can take 0.1 seconds for each update tick as maximum
		raycastManager = new RayCastManager(world, 0.1f);
		
		// Generate random raycasts
		int priority;
		Vector2 point1 = new Vector2(), point2 = new Vector2();
		for(int i=0; i<10000; i++) {
			priority = MathUtils.random(0, 10);
			point1.set(MathUtils.random(-(SCENE_WIDTH*0.5f), (SCENE_WIDTH*0.5f)), 
					MathUtils.random(-(SCENE_HEIGHT*0.5f), (SCENE_HEIGHT*0.5f)));
			point2.set(MathUtils.random(-(SCENE_WIDTH*0.5f), (SCENE_WIDTH*0.5f)), 
					MathUtils.random(-(SCENE_HEIGHT*0.5f), (SCENE_HEIGHT*0.5f)));
			
			Gdx.app.log(TAG, " New query, POINT1: " + point1 + ", POINT2: " + point2 + ", priority: " + priority);
			raycastManager.addRequest(priority, point1, point2, callback);
		}
	}

	RayCastCallback callback = new RayCastCallback() {
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			// Print contact info
			Gdx.app.log(TAG, " Contact!! " + fixture.getBody().getUserData() + " at [" + point.x + ";" + point.y + "]");
			return -1; // Continue with the rest of the fixtures
		}
	};

	private void createGround() {

		ChainShape chainShape = new ChainShape();

		chainShape.createLoop(new Vector2[] {
				new Vector2(.0f, .0f), 
				new Vector2(SCENE_WIDTH, .0f), 
				new Vector2(SCENE_WIDTH, 7f),
				new Vector2(SCENE_WIDTH-1f, 7f),
				new Vector2(SCENE_WIDTH-1f, 1.5f),
				new Vector2(1f, 1.5f),
				new Vector2(1f, 7f),
				new Vector2(0f, 7f),});

		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		chainBodyDef.position.set(.0f, .0f);
		groundBody = world.createBody(chainBodyDef);

		FixtureDef groundFD = new FixtureDef();
		groundFD.shape = chainShape;
		groundFD.density = 0;

		groundBody.createFixture(groundFD);
		groundBody.setUserData("Ground Body");
		chainShape.dispose();
	}

	private Body createPolygon(int num, BodyType type, float x, float y, float d, float r, float f, float halfwidth, float halfheight) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x,y);
		bodyDef.angle=0;
		bodyDef.fixedRotation = true;

		Body square = world.createBody(bodyDef);
		square.setUserData("Box"+num);
		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=d;
		fixtureDef.restitution=r;
		fixtureDef.friction=f;
		fixtureDef.shape=new PolygonShape();
		((PolygonShape) fixtureDef.shape).setAsBox(halfwidth, halfheight);

		square.createFixture(fixtureDef);
		fixtureDef.shape.dispose();

		return square;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();

		batch.dispose();
		world.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(1/60f, 6, 2);

		raycastManager.update();
		
		debugRenderer.render(world, viewport.getCamera().combined);
	}
}
