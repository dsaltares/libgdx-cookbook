package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DBikeSimulatorSample extends GdxSample {
	private static final String TAG = "Box2DBikeSimulatorSample";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private Vector3 point;
	private SpriteBatch batch;

	// General Box2D
	Box2DDebugRenderer debugRenderer;
	BodyDef defaultDynamicBodyDef;
	World world;
	
	Body groundBody;

	// Bike components
	Body frameBody, frontDamperBody, backDamperBody, backWheelBody, frontWheelBody;
	Texture wheelTex, backDamperTex, frontDamperTex, frameTex;
	final float WHEEL_WIDTH = 1.25f;
	final float BACKDAMPER_WIDTH = 1.13f, BACKDAMPER_HEIGHT = .39f;
	final float FRONTDAMPER_WIDTH = .71f, FRONTDAMPER_HEIGHT = 1.069f;
	final float FRAME_WIDTH = 1.8f, FRAME_HEIGHT = 1.53f;
	

	// It will allow us to interact with the physics bodies
	MouseJoint mouseJoint;
	Body hitBody = null;
	
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		// Center camera to get (0,0) as the origin of the Box2D world
		viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f, 
				0);
		viewport.getCamera().update();
		
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-9.8f), true);

		debugRenderer = new Box2DDebugRenderer();

		// Creates a ground to avoid objects falling forever
		createGround();

		point = new Vector3();
		createBike();

		debugRenderer = new Box2DDebugRenderer();

	}

	private void createBike() {
		// Define images		
		frameTex = new Texture(Gdx.files.internal("data/box2D/frame.png"));
		backDamperTex = new Texture(Gdx.files.internal("data/box2D/backDamper.png"));
		frontDamperTex = new Texture(Gdx.files.internal("data/box2D/frontDamper.png"));
		wheelTex = new Texture(Gdx.files.internal("data/box2D/wheel.png"));		
		
		frontWheelBody = createSphere(BodyType.DynamicBody, 0f, 1f, 0.8f, 0f, 1.0f, WHEEL_WIDTH * .5f);
		backWheelBody = createSphere(BodyType.DynamicBody, -1.4f, 1f, 0.8f, 0f, 1.0f, WHEEL_WIDTH * .5f);		
		
		frameBody = createPolygon(BodyType.DynamicBody, 0f, 3f, 1f, 0f, 0f, FRAME_WIDTH * .5f, FRAME_HEIGHT * .5f);
		frontDamperBody = createPolygon(BodyType.DynamicBody, 0f, 2f, 1f, 0f, 0f, FRONTDAMPER_WIDTH * .5f, FRONTDAMPER_HEIGHT *.5f);
		backDamperBody = createPolygon(BodyType.DynamicBody, -1f, 2f, 1f, 0f, 0f, BACKDAMPER_WIDTH *.5f, BACKDAMPER_HEIGHT *.5f);
		
		// Crete joints for the shapes
		// Connects front damper with frame
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
		prismaticJointDef.initialize(frontDamperBody, frameBody, new Vector2(0f,0f), new Vector2(-0.5f,1f));
		prismaticJointDef.lowerTranslation =-.2f;
		prismaticJointDef.upperTranslation = 0f;
		prismaticJointDef.enableLimit = true;
		prismaticJointDef.localAnchorA.set(-FRONTDAMPER_WIDTH * .5f + 0.065f,FRONTDAMPER_HEIGHT *.5f);
		prismaticJointDef.localAnchorB.set(FRAME_WIDTH * .5f - 0.165f,0.1f);
		prismaticJointDef.collideConnected=false;
		Joint fDamperFrameJoint = world.createJoint(prismaticJointDef);
		
		// Connects front damper with front wheel
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = frontDamperBody;
		revoluteJointDef.bodyB = frontWheelBody;
		revoluteJointDef.localAnchorA.set(FRONTDAMPER_WIDTH * .5f - 0.065f,-FRONTDAMPER_HEIGHT *.5f);
		revoluteJointDef.localAnchorB.set(0,0);
		revoluteJointDef.collideConnected=false;
		Joint fDamperFwheelJoint = world.createJoint(revoluteJointDef);
		
		//Connects backdamper with frame
		RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef();
		revoluteJointDef2.bodyA=backDamperBody;
		revoluteJointDef2.bodyB=frameBody;
		revoluteJointDef2.collideConnected=false;
		revoluteJointDef2.localAnchorA.set(BACKDAMPER_WIDTH *.5f,0f);
		revoluteJointDef2.localAnchorB.set(-FRAME_WIDTH * .5f + 0.5f,-FRAME_HEIGHT * .5f + 0.25f);
		revoluteJointDef2.lowerAngle = -4 * MathUtils.degreesToRadians;
		revoluteJointDef2.upperAngle = 26 * MathUtils.degreesToRadians;
		revoluteJointDef2.enableLimit = true;
		Joint bDamperFrameJoint = world.createJoint(revoluteJointDef2);
		
		//Connects backdamper with backWheel
		RevoluteJointDef revoluteJointDef3 = new RevoluteJointDef();
		revoluteJointDef3.bodyA=backDamperBody;
		revoluteJointDef3.bodyB=backWheelBody;
		revoluteJointDef3.collideConnected=false;
		revoluteJointDef3.localAnchorA.set(-BACKDAMPER_WIDTH * .5f,-0.1f);
		revoluteJointDef3.localAnchorB.set(0,0);
		revoluteJointDef3.enableMotor = true;
		revoluteJointDef3.maxMotorTorque = 100f;
		revoluteJointDef3.motorSpeed = -135f * MathUtils.degreesToRadians;
		Joint bDamperBwheelJoint = world.createJoint(revoluteJointDef3);
		
		
		// Disable contact between certain bodies
		MouseJointDef mjd1 = new MouseJointDef();
		mjd1.bodyA = frameBody;
		mjd1.bodyB = backWheelBody;
		Joint frameBwheelJoint = world.createJoint(mjd1);
		
		MouseJointDef mjd2 = new MouseJointDef();
		mjd2.bodyA = frameBody;
		mjd2.bodyB = frontWheelBody;
		Joint frameFwheelJoint = world.createJoint(mjd2);
		
	}

	private void createGround() {

		float halfGroundWidth = SCENE_WIDTH;
		float halfGroundHeight = 0.5f; // 1 meter high

		// Create a static body definition
		BodyDef groundBodyDef = new BodyDef();  
		groundBodyDef.type = BodyType.StaticBody;

		// Set the ground position to (0,0) -> libgdx coordinates system
		//groundBodyDef.position.set(new Vector2(0, -(VIRTUAL_HEIGHT*0.5f) + halfGroundHeight));
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

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		wheelTex.dispose();
		backDamperTex.dispose();
		frontDamperTex.dispose();
		frameTex.dispose();
		
		debugRenderer.dispose();
		
		batch.dispose();
		world.dispose();
	}

	private Body createPolygon(BodyType type, float x, float y, float d, float r, float f, float halfwidth, float halfheight) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(SCENE_WIDTH*0.5f+x,y);
		bodyDef.angle=0;
		Body square = world.createBody(bodyDef);
		
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
	
	private Body createSphere(BodyType type, float x, float y, float d, float r, float f, float radius) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(SCENE_WIDTH*0.5f+x,y);
		bodyDef.angle=0;
		Body ball = world.createBody(bodyDef);
		
		FixtureDef fixtureDef=new FixtureDef();
 		fixtureDef.density=d;
 		fixtureDef.restitution=r;
 		fixtureDef.friction=f;
 		fixtureDef.shape=new CircleShape();
 		fixtureDef.shape.setRadius(radius);
 		
 		ball.createFixture(fixtureDef);
		fixtureDef.shape.dispose();
		
		return ball;
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.step(1/60f, 6, 2);
		
		batch.begin();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		batch.draw(
				wheelTex, 
				frontWheelBody.getPosition().x - (WHEEL_WIDTH*.5f), frontWheelBody.getPosition().y - (WHEEL_WIDTH*.5f),
				WHEEL_WIDTH*.5f, WHEEL_WIDTH*.5f, 
				WHEEL_WIDTH, WHEEL_WIDTH, 
				1f, 1f, 
				frontWheelBody.getAngle() * MathUtils.radDeg, 
				0, 0, 
				wheelTex.getWidth(), wheelTex.getHeight(), 
				false, false);
		
		batch.draw(
				wheelTex, 
				backWheelBody.getPosition().x - (WHEEL_WIDTH*.5f), backWheelBody.getPosition().y - (WHEEL_WIDTH*.5f),
				WHEEL_WIDTH*.5f, WHEEL_WIDTH*.5f, 
				WHEEL_WIDTH, WHEEL_WIDTH, 
				1f, 1f, 
				backWheelBody.getAngle() * MathUtils.radDeg, 
				0, 0, 
				wheelTex.getWidth(), wheelTex.getHeight(), 
				false, false);
		
		batch.draw(
				backDamperTex, 
				backDamperBody.getPosition().x - (BACKDAMPER_WIDTH*.5f), backDamperBody.getPosition().y - (BACKDAMPER_HEIGHT*.5f),
				BACKDAMPER_WIDTH*.5f, BACKDAMPER_HEIGHT*.5f, 
				BACKDAMPER_WIDTH, BACKDAMPER_HEIGHT, 
				1f, 1f, 
				backDamperBody.getAngle() * MathUtils.radDeg, 
				0, 0, 
				backDamperTex.getWidth(), backDamperTex.getHeight(), 
				false, false);
		
		batch.draw(
				frontDamperTex, 
				frontDamperBody.getPosition().x - (FRONTDAMPER_WIDTH*.5f), frontDamperBody.getPosition().y - (FRONTDAMPER_HEIGHT*.5f),
				FRONTDAMPER_WIDTH*.5f, FRONTDAMPER_HEIGHT*.5f, 
				FRONTDAMPER_WIDTH, FRONTDAMPER_HEIGHT, 
				1f, 1f, 
				frontDamperBody.getAngle() * MathUtils.radDeg, 
				0, 0, 
				frontDamperTex.getWidth(), frontDamperTex.getHeight(), 
				false, false);
		
		batch.draw(
				frameTex, 
				frameBody.getPosition().x - (FRAME_WIDTH*.5f), frameBody.getPosition().y - (FRAME_HEIGHT*.5f),
				FRAME_WIDTH*.5f, FRAME_HEIGHT*.5f, 
				FRAME_WIDTH, FRAME_HEIGHT, 
				1f, 1f, 
				frameBody.getAngle() * MathUtils.radDeg, 
				0, 0, 
				frameTex.getWidth(), frameTex.getHeight(), 
				false, false);
		
		batch.end();

		debugRenderer.render(world, viewport.getCamera().combined);
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Mouse coordinates to world coordinates
		viewport.getCamera().unproject(point.set(x, y, 0));
		
		if(button == Input.Buttons.RIGHT) {
			hitBody = null;
			
			// Check if a interactive body has been right-clicked
			world.QueryAABB(callback, point.x - 0.0001f, point.y - 0.0001f, point.x + 0.0001f, point.y + 0.0001f);
			
			if (hitBody == groundBody) hitBody = null;
			
			// if we hit an interactive body we create a new mouse joint
			// and attach it to the hit body.
			if (hitBody != null) {
				MouseJointDef def = new MouseJointDef();
				def.bodyA = groundBody;
				def.bodyB = hitBody;
				def.collideConnected = true;
				def.target.set(point.x, point.y);
				def.maxForce = 1000.0f * hitBody.getMass();

				mouseJoint = (MouseJoint)world.createJoint(def);
				hitBody.setAwake(true);
			}
		}
		return false;
	}

	/** another temporary vector **/
	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		// To keep the object responsive after clicking the first time
		// It will just update the target coordinates
		if (mouseJoint != null) {
			viewport.getCamera().unproject(point.set(x, y, 0));
			mouseJoint.setTarget(target.set(point.x, point.y));
		}
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		// if a mouse joint exists we simply destroy it
		if(button == Input.Buttons.RIGHT) {
			if (mouseJoint != null) {
				world.destroyJoint(mouseJoint);
				mouseJoint = null;
			}
		}
		return false;
	}
	
	
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(point.x, point.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};
}
