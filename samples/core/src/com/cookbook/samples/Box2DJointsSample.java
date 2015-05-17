package com.cookbook.samples;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DJointsSample extends GdxSample {
	private static final String TAG = "Box2DJointsSample";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private Vector3 point = new Vector3();
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;
	
	// It will allow us to interact with the physics bodies
	MouseJoint mouseJoint;
	Body hitBody = null;

	// Title
	String title = null;
	BitmapFont font;
	GlyphLayout layout = new GlyphLayout();
	float titleWidth;

	// Active bodies and joints
	ArrayList<Body> bodies = new ArrayList<Body>();
	ArrayList<Joint> joints = new ArrayList<Joint>();
	
	int currentJoint = 0;
	private static final int NUM_OF_EXAMPLES = 9;
	
	Body groundBody;

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

		font = new BitmapFont(Gdx.files.internal("data/verdana39.fnt"), false);
		
		// Create Physics World
		world = new World(new Vector2(0,-10), true);

		// Instantiate DebugRenderer for rendering shapes
		debugRenderer = new Box2DDebugRenderer();

		// Creates a ground to avoid objects falling forever
		createGround();
		
		Gdx.app.log(TAG, "Use the mouse to interact.\n"
				+ "\tLeft button - Change Joint example\n"
				+ "\tRight button - Interact with bodies\n");

	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Mouse coordinates to box2D coordinates
		viewport.getCamera().unproject(point.set(x, y, 0));
		
		if (button == Input.Buttons.LEFT) {
			
			// Clean previous example
			for(Joint j : joints) {
				world.destroyJoint(j);
			}
			joints.clear();
			
			for(Body b : bodies) {
				for(Fixture f : b.getFixtureList()) {
					b.destroyFixture(f);
				}
			}
			bodies.clear();

			switch(currentJoint) {
			case 0: 
				showRevoluteJoint();
				break;
			case 1:
				showDistanceJoint();
				break;
			case 2:
				showPrismaticJoint();
				break;
			case 3:
				showRopeJoint();
				break;
			case 4:
				showFrictionJoint();
				break;
			case 5:
				showPulleyJoint();
				break;
			case 6:
				showGearJoint();
				break;
			case 7:
				showWeldJoint();
				break;
			case 8:
				showWheelJoint();
				break;
			}
			
			currentJoint = ++currentJoint % NUM_OF_EXAMPLES;

			return true;
		}
		else if(button == Input.Buttons.RIGHT) {
			hitBody = null;
			
			// Check if a interactive body has been right-clicked
			world.QueryAABB(callback, point.x - 0.0001f, point.y - 0.0001f, point.x + 0.0001f, point.y + 0.0001f);
			
			if (hitBody == groundBody) hitBody = null;
			
			// if we hit a interactive body we create a new mouse joint
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
	
	private void showRevoluteJoint() {
		setTitle("Revolute Joint");
		
		Body smallBall = createSphere(BodyType.StaticBody, 0f, 3.75f, 1f, 1f, 0f, .25f);
		Body bigBall = createSphere(BodyType.DynamicBody, 0f, 3.75f, 1f, 1f, 0f, .5f);
		
		// Define the revolute joint
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA=smallBall;
		revoluteJointDef.bodyB=bigBall;
		revoluteJointDef.collideConnected=false;
		revoluteJointDef.localAnchorA.set(0,0);
		revoluteJointDef.localAnchorB.set(-2.0f,0);
		revoluteJointDef.enableMotor=true;
		revoluteJointDef.maxMotorTorque=360;
		revoluteJointDef.motorSpeed=100f*MathUtils.degreesToRadians;

		bodies.add(bigBall);
		bodies.add(smallBall);
		joints.add(world.createJoint(revoluteJointDef));
	}
	
	private void showDistanceJoint() {
		setTitle("Distance Joint");
		
		Body smallBall = createSphere(BodyType.DynamicBody, 0f, 3.75f, .8f, .8f, .4f, .25f);
		Body bigBall = createSphere(BodyType.DynamicBody, 3.0f, 4.5f, .8f, 1f, .4f, .5f);
		
		// Define the distance joint
		DistanceJointDef distanceJointDef = new DistanceJointDef();
		distanceJointDef.bodyA=smallBall;
		distanceJointDef.bodyB=bigBall;
		distanceJointDef.collideConnected=false;
		distanceJointDef.length = 2.0f;
		distanceJointDef.localAnchorA.set(0,0);
		distanceJointDef.localAnchorB.set(0,0);

		bodies.add(bigBall);
		bodies.add(smallBall);
		joints.add(world.createJoint(distanceJointDef));
	}

	private void showRopeJoint() {
		setTitle("Rope Joint");
		
		Body smallBall = createSphere(BodyType.DynamicBody, 0f, 3.75f, .8f, .8f, .4f, .25f);
		Body bigBall = createSphere(BodyType.DynamicBody, 3.0f, 4.5f, .8f, 1f, .4f, .5f);
		
		// Define the rope joint
		RopeJointDef ropeJointDef = new RopeJointDef();
		ropeJointDef.bodyA=smallBall;
		ropeJointDef.bodyB=bigBall;
		ropeJointDef.collideConnected=true;
		ropeJointDef.maxLength = 4.0f;
		ropeJointDef.localAnchorA.set(0,0);
		ropeJointDef.localAnchorB.set(0.0f,0);

		bodies.add(bigBall);
		bodies.add(smallBall);
		joints.add(world.createJoint(ropeJointDef));
	}
	
	private void showPrismaticJoint() {

		setTitle("Prismatic Joint");
		
		Body square = createPolygon(BodyType.DynamicBody, 0, 3.6f, .8f, .8f, .4f, .5f, .5f);
		
		// Define the prismatic joint
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
		prismaticJointDef.initialize(groundBody, square, new Vector2(SCENE_WIDTH*.5f,SCENE_HEIGHT*.5f), new Vector2(SCENE_WIDTH*.5f+1f,0));
		prismaticJointDef.lowerTranslation =-2;
		prismaticJointDef.upperTranslation = 2;
		prismaticJointDef.enableLimit = true;
		prismaticJointDef.enableMotor = true;
		prismaticJointDef.maxMotorForce = 100;
		prismaticJointDef.motorSpeed = 20f * MathUtils.degreesToRadians;

		bodies.add(square);
		joints.add(world.createJoint(prismaticJointDef));
		
	}
	
	private void showFrictionJoint() {
		setTitle("Friction Joint");

		Body square = createPolygon(BodyType.DynamicBody, 0, 3.6f, 1f, 0.1f, .4f, .5f, .5f);
		
		// Define the friction joint
		FrictionJointDef frictionJointDef = new FrictionJointDef();
		frictionJointDef.initialize(groundBody, square, new Vector2(SCENE_WIDTH*.5f,SCENE_HEIGHT*.5f));
		frictionJointDef.collideConnected=true;
		frictionJointDef.maxForce = 6.0f;
		frictionJointDef.maxTorque = -.3f;

		bodies.add(square);
		joints.add(world.createJoint(frictionJointDef));
		
	}
	
	private void showPulleyJoint() {
		setTitle("Pulley Joint");
		
		Body smallBall = createSphere(BodyType.DynamicBody, -1f, 5f, .3333f, .4f, .4f, .25f);
		Body bigBall = createSphere(BodyType.DynamicBody, 1f, 5f, .3f, .4f, .4f, .3f);
		
		// Define the pulley joint
		PulleyJointDef pulleyJointDef = new PulleyJointDef();
		pulleyJointDef.bodyA=smallBall;
		pulleyJointDef.bodyB=bigBall;
		pulleyJointDef.collideConnected=true;
		pulleyJointDef.groundAnchorA.set(SCENE_WIDTH*.5f-1, SCENE_HEIGHT*.5f);
		pulleyJointDef.groundAnchorB.set(SCENE_WIDTH*.5f+1f, SCENE_HEIGHT*.5f);
		pulleyJointDef.localAnchorA.set(0,0);
		pulleyJointDef.localAnchorB.set(0,0);
		pulleyJointDef.lengthA = 0.7f;
		pulleyJointDef.lengthB = 0.7f;
		pulleyJointDef.ratio=1f;

		bodies.add(bigBall);
		bodies.add(smallBall);
		joints.add(world.createJoint(pulleyJointDef));
	}
		
	private void showGearJoint() {
		setTitle("Gear Joint");
		
		Body ball = createSphere(BodyType.DynamicBody, -1.5f, 5f, .3f, .4f, .4f, .25f);
		Body square = createPolygon(BodyType.DynamicBody, 3f, 5f, 1f, 0.1f, .4f, .25f, .75f);
		
		// Active bodies
		bodies.add(ball);
		bodies.add(square);
		
		// RevoluteJoint for ball
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA=groundBody;
		revoluteJointDef.bodyB=ball;
		revoluteJointDef.collideConnected=false;
		revoluteJointDef.localAnchorA.set(0,5f);
		revoluteJointDef.localAnchorB.set(1.0f,0);

		Joint rj = world.createJoint(revoluteJointDef);
		
		
		// PrismaticJoint for square
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
		prismaticJointDef.initialize(groundBody, square, new Vector2(SCENE_WIDTH*.5f,5f), new Vector2(0f,1f));
		prismaticJointDef.lowerTranslation = -2f;
		prismaticJointDef.upperTranslation = .5f;
		prismaticJointDef.enableLimit = true;

		Joint pj = world.createJoint(prismaticJointDef);
		
		
		// Define the gear joint
		GearJointDef gearJointDef = new GearJointDef();
		gearJointDef.bodyA=ball;
		gearJointDef.bodyB=square;
		gearJointDef.joint1 = rj;
		gearJointDef.joint2 = pj;
		gearJointDef.ratio=(float) (2.0f * Math.PI / 0.5f);

		joints.add(world.createJoint(gearJointDef));
		joints.add(pj);
		joints.add(rj);
	}
	
	private void showWeldJoint() {
		setTitle("Weld Joint");
		
		Body smallBall = createSphere(BodyType.DynamicBody, -1f, 5f, .3333f, .4f, .4f, .25f);
		Body bigBall = createSphere(BodyType.DynamicBody, 1f, 5f, .3f, .4f, .4f, .3f);
		
		// Define the weld joint
		WeldJointDef weldJointDef = new WeldJointDef();
		weldJointDef.bodyA=smallBall;
		weldJointDef.bodyB=bigBall;
		weldJointDef.localAnchorA.set(0,0);
		weldJointDef.localAnchorB.set(.55f,0);

		bodies.add(bigBall);
		bodies.add(smallBall);
		joints.add(world.createJoint(weldJointDef));
	}
	
	private void showWheelJoint() {
		setTitle("Wheel Joint");
		
		Body wheel = createSphere(BodyType.DynamicBody, 0, 3f, .4f, .5f, .3f, .5f);
		
		// Define the wheel joint
		WheelJointDef wheelJointDef = new WheelJointDef();
		wheelJointDef.bodyA=groundBody;
		wheelJointDef.bodyB=wheel;
		wheelJointDef.collideConnected=true;
		wheelJointDef.localAnchorA.set(0,0);
		wheelJointDef.localAnchorB.set(0,0);
		wheelJointDef.motorSpeed = 5f;
		wheelJointDef.enableMotor = true;
		wheelJointDef.maxMotorTorque = 50f;
		wheelJointDef.dampingRatio = 0.5f;
		wheelJointDef.frequencyHz = 0.2f;
		

		bodies.add(wheel);
		joints.add(world.createJoint(wheelJointDef));
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

	private void setTitle(String newTitle) {
		title = newTitle;

		layout.setText(font, "title");
		titleWidth = layout.width * 0.5f;
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

		if(title != null) {
			batch.begin();
			viewport.getCamera().project(point.set(SCENE_WIDTH*0.5f, 6.8f, 0));
			font.draw(batch, title, point.x - titleWidth, point.y);
			batch.end();
		}

		debugRenderer.render(world, viewport.getCamera().combined);
	}
}
