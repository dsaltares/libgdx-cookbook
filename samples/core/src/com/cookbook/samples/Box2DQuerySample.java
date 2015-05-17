package com.cookbook.samples;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DQuerySample extends GdxSample {
	private static final String TAG = "Box2DQuerySample";

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;
	Body groundBody;

	// To render raycasting
	ShapeRenderer sr;
	Vector2 p1, p2, collision, normal;

	// Contacts
	String title = null;
	BitmapFont font;
	GlyphLayout layout = new GlyphLayout();
	float titleWidth;

	Array<Body> bodiesWithinArea;

	//To avoid creating new vectors everytime
	Vector3 point = new Vector3();
	Vector2 pos = new Vector2();

	float[] aabb = new float[10];
	
	
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

		font = new BitmapFont(Gdx.files.internal("data/verdana39.fnt"), false);

		Gdx.input.setInputProcessor(this);

		// Create Physics World
		world = new World(new Vector2(0,-9.8f), true);

		// Creates a ground to avoid objects falling forever
		createGround();
		
		// aabb vertices
		aabb[0] = 1f;
		aabb[1] = 1.5f;
		aabb[2] = 4f ;
		aabb[3] = 1.5f;
		aabb[4] = 4f ;
		aabb[5] = 4.5f;
		aabb[6] = 1f;
		aabb[7] = 4.5f;
		aabb[8] = 1f;
		aabb[9] = 1.5f;
		
		bodiesWithinArea = new Array<Body>();
		
		// AABB query area
		world.QueryAABB(areaCallback, aabb[0], aabb[1], aabb[4], aabb[5]);
		
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

		// Logical data to represent raycast
		sr = new ShapeRenderer();
		p1 = new Vector2();
		p2 = new Vector2();
		collision = new Vector2();
		normal = new Vector2();

	}

	RayCastCallback callback = new RayCastCallback() {
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			collision.set(point);
			Box2DQuerySample.this.normal.set(normal).add(point);

			// Show contact info
			title = fixture.getBody().getUserData() + " at [" + point.x + ";" + point.y + "]";
			layout.setText(font, title);
			titleWidth = layout.width * 0.5f;
			return 1; // Continue with the rest of the fixtures
		}
	};
	
	QueryCallback areaCallback = new QueryCallback() {

		@Override
		public boolean reportFixture(Fixture fixture) {
			// TODO Auto-generated method stub
			if(fixture.getBody().getType() != BodyType.StaticBody) {
				bodiesWithinArea.add(fixture.getBody());
			}
			return true;
		}

	};


	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		viewport.getCamera().unproject(point.set(x, y, 0));
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if(p1.x != point.x && p1.y != point.y) {
				p2.set(point.x, point.y);
				world.rayCast(callback, p1, p2);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Screen coordinates to World coordinates
		viewport.getCamera().unproject(point.set(x, y, 0));

		if (button == Input.Buttons.LEFT) {
			p1.set(point.x, point.y);
			p2.set(point.x, point.y+0.00001f); // +.00001 because otherwise it will crash
			normal.set(Vector2.Zero);
			collision.set(Vector2.Zero);
			world.rayCast(callback, p1, p2);
			return true;
		}
		return false;
	}

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
			
		// AABB query area
		bodiesWithinArea.clear();
		world.QueryAABB(areaCallback, aabb[0], aabb[1], aabb[4], aabb[5]);
		
		world.step(1/60f, 6, 2);

		debugRenderer.render(world, viewport.getCamera().combined);		

		sr.setProjectionMatrix(viewport.getCamera().combined);
		sr.begin(ShapeType.Line);
		sr.line(p1, p2);
		sr.line(collision, normal);
		sr.polyline(aabb);
		sr.end();
		
		sr.begin(ShapeType.Filled);
		for(Body b : bodiesWithinArea) {
			sr.circle(b.getPosition().x, b.getPosition().y, 0.2f, 20);
		}
		sr.end();
		
		if(title != null) {
			batch.begin();
			viewport.getCamera().project(point.set(SCENE_WIDTH*0.5f, 6.8f, 0));
			font.draw(batch, title, point.x - titleWidth, 680);
			batch.end();
		}
		
	}
}
