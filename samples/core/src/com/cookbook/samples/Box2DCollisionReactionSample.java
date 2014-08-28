package com.cookbook.samples;


import java.util.Iterator;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DCollisionReactionSample extends GdxSample implements ContactListener {
	private static final String TAG = "Box2DCollisionReaction";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private Vector3 point;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;

	// Pre-created vector2 to avoid creating new ones repeatedly
	Vector2 pos = new Vector2();
	// Ground and cursor bodies
	Body groundBody, pointerBody;

	// Current active balloons
	private Array<Body> balloons = new Array<Body>();
	
	// Balloons constraints
	private static final float BALLOON_WIDTH = 0.5f;
	private static final float BALLOON_HEIGHT = 0.664f;
	// To simplify we will consider the balloon as an ellipse (A = PI * semi-major axis * semi-minor axis)
	private static final float BALLOON_AREA = MathUtils.PI * BALLOON_WIDTH * 0.5f * BALLOON_HEIGHT * 0.5f;

	// To get into details, please read: http://www.iforce2d.net/b2dtut/buoyancy
	float airDensity=0.01f, balloonDensity = 0.0099999f, balloonFriction = 0.90f, balloonRestitution = 0.0f;
	float displacedMass = BALLOON_AREA * airDensity;
	Vector2 buoyancyForce = new Vector2(0f, displacedMass * 9.8f);

	// Allowed positions for balloons
	int maxWidth = (int) (SCENE_WIDTH - BALLOON_WIDTH*0.5f);
	int maxHeight = (int) (SCENE_HEIGHT - BALLOON_HEIGHT*0.5f);
	int minWidth = (int) (0 + BALLOON_WIDTH*0.5f);
	int minHeight = (int) (1 + BALLOON_HEIGHT*0.5f);

	// Home-made timer to make balloons appear
	float time = 0;
	
	FixtureDef balloonFD; 

	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		// Center camera to get (0,0) as the origin of the Box2D world
		viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f
				, 0);
		viewport.getCamera().update();
		
		point = new Vector3();
		
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-9.8f), true);

		// Creates a ground to avoid objects falling forever
		createGround();
		pointerBody = createSharpObject();

		// Set physics properties for the balloon
		balloonFD = new FixtureDef();
		balloonFD.density = balloonDensity;
		balloonFD.friction = balloonFriction;
		balloonFD.restitution = balloonRestitution;

		// Box2DCollisionReaction will be able to listen for contacts between fixtures
		world.setContactListener(this);
		
		debugRenderer = new Box2DDebugRenderer();

	}

	private void createGround() {

		float halfGroundWidth = SCENE_WIDTH;
		float halfGroundHeight = 0.5f; // 1 meter high

		// Create a static body definition
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.type = BodyType.StaticBody;

		// Set the ground position to (0,0) -> libgdx coordinates system
		groundBodyDef.position.set(halfGroundWidth*0.5f, halfGroundHeight);

		// Create a body from the defintion and add it to the world
		groundBody = world.createBody(groundBodyDef);  

		// Create a rectangle shape which will fit the virtual_width and 1 meter high
		// (setAsBox takes half-width and half-height as arguments)
		PolygonShape groundBox = new PolygonShape();  
		groundBox.setAsBox(halfGroundWidth * 0.5f, halfGroundHeight);
		// Create a fixture from our rectangle shape and add it to our ground body  
		groundBody.createFixture(groundBox, 0.0f);
		// Free resources
		groundBox.dispose();

	}

	// It will create a balloon in a random position within the screen
	private void createBalloon() {
		float x = MathUtils.random(minWidth, maxWidth);
		float y = MathUtils.random(minHeight, maxHeight);

		// Instantiate the loader with the created JSON data
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/box2D/balloon.json"));

		// Create the balloon body definition and place it in within the world
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(x, y);

		// Create the balloon body
		Body balloonBody = world.createBody(bd);
		balloonBody.setUserData(false); // Set to true if it must be destroyed, false means active

		// Create balloon fixture
		loader.attachFixture(balloonBody, "balloon", balloonFD, BALLOON_WIDTH);
		
		balloons.add(balloonBody);
	}
	
	// Cursor sharp object to break balloons
	private Body createSharpObject() {
		ChainShape chainShape = new ChainShape();
		
		chainShape.createLoop(new Vector2[] {
				new Vector2(.0f, .375f), 
				new Vector2(.125f, .125f), 
				new Vector2(.375f, 0f),
				new Vector2(.125f, -.125f),
				new Vector2(0, -.375f),
				new Vector2(-.125f, -.125f),
				new Vector2(-.375f, 0f),
				new Vector2(-.125f, .125f),});
		
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.KinematicBody;
		chainBodyDef.bullet = true;
		chainBodyDef.position.set(SCENE_WIDTH*.5f, SCENE_HEIGHT*.5f);
		Body chainBody = world.createBody(chainBodyDef);
		chainBody.createFixture(chainShape, 0.8f);
		chainShape.dispose();
		
		chainBody.setAwake(true);
		
		return chainBody;
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

		// Update timer
		float delta = Gdx.graphics.getDeltaTime();
		if((time += delta) >= 1f) { //Every second
			time-=1f;
			createBalloon();
		}

		for(Body balloon : balloons) // Keep balloons flying
			balloon.applyForceToCenter(buoyancyForce, true);

		world.step(1/60f, 6, 2);

		debugRenderer.render(world, viewport.getCamera().combined);

		freeBalloons(); 

	}

	// To achieve that the pointerBody follows the mouse
	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		viewport.getCamera().unproject(point.set(screenX, screenY, 0));
		pointerBody.setTransform(point.x, point.y, pointerBody.getAngle() + 5 * MathUtils.degreesToRadians);

		return true;
	}
	
	// Deletes broken balloons and also those whose coordinates are out of the screen dimensions
	void freeBalloons() {
		Iterator<Body> i = balloons.iterator();
		while (i.hasNext()) {
			Body balloon = i.next();
			boolean broken = (Boolean) balloon.getUserData();
			if(((balloon.getPosition().y - BALLOON_HEIGHT*0.5f) > SCENE_HEIGHT) || // Top limit
					(balloon.getPosition().y + BALLOON_HEIGHT*0.5f) < 1.0f || // Bottom limit
					(balloon.getPosition().x - BALLOON_WIDTH*0.5f) > SCENE_WIDTH || // Right limit
					(balloon.getPosition().x + BALLOON_WIDTH*0.5f) < 0 || // Left limit
					broken) {
				
				world.destroyBody(balloon);
				i.remove();
			}
		}
	}

	/* Next functions must be implemented for ContactListeners */
	/* Instead of letting Box2D manage the contact between the pointerBody and the balloons, 
	 * we set the balloon as broken(true)
	 */
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();
		
		if(bodyA == pointerBody && bodyB != groundBody)
			bodyB.setUserData(true);
		else if(bodyB == pointerBody && bodyA != groundBody)
			bodyA.setUserData(true);
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
