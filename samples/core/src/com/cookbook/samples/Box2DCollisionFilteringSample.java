package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Box2DCollisionFilteringSample extends GdxSample implements ContactListener {
	private static final String TAG = "Box2DCollisionFilteringSample";

	private static final float WORLD_WIDTH = 12.80f; // 12.8 metres wide
	private static final float WORLD_HEIGHT = 7.20f; // 7.2 metres high

	private static final float TEXTURE_SCALE = 0.01f;
	
	private Viewport viewport;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;
	Body groundBody;

	//To avoid creating new vectors everytime
	Vector3 point = new Vector3();
	Vector2 pos = new Vector2();
	
	// Collision filtering stuff
	final short CATEGORY_PLAYER = 0x0001;
	final short CATEGORY_ENEMY = 0x0002;
	final short CATEGORY_GROUND = 0x0004;
	final short CATEGORY_SENSOR = 0x0008;
	
	final short MASK_PLAYER = ~CATEGORY_PLAYER; // Cannot collide with player objects
	final short MASK_ENEMY =  ~CATEGORY_ENEMY; // Cannot collide with enemy objects
	final short MASK_SENSOR =  CATEGORY_PLAYER; // Can collide only with players
	final short MASK_GROUND = -1; // Can collide with everything
	
	// Scene actors
	Enemy dinosaur1, dinosaur2;
	Player player1, player2;
	Texture playerTex, dinoTex;
	
	// Dinosaur class
	private class Enemy {
		private final float ENEMY_HEIGHT = 1f;
		private final float ENEMY_WIDTH = 0.6f;
		
		boolean angry = false;
		Body enemyBody;
		Color defaultColor;
		Texture texture;
		
		public Enemy(Texture texture, float x, float y) {
			this.texture = texture;
			
			enemyBody = createPolygon(BodyType.DynamicBody, x, y, 1f, 0f, 0.8f, ENEMY_WIDTH * 0.5f,
					ENEMY_HEIGHT * 0.5f, CATEGORY_ENEMY, MASK_ENEMY);
			
			//To identify the specific instance when collisions happen 
			enemyBody.setUserData(this);
			
			// Sensor shape to detect other bodies getting close to him
			CircleShape circle = new CircleShape();
			circle.setRadius(1f);

			FixtureDef sensorFD = new FixtureDef();
			sensorFD.isSensor = true;
			sensorFD.shape = circle;
			sensorFD.filter.categoryBits = CATEGORY_SENSOR;
			sensorFD.filter.maskBits = MASK_SENSOR;
			enemyBody.createFixture(sensorFD);
		}
		
		public void setAngry(boolean b) {
			angry = b;
		}
		
		public void draw(SpriteBatch batch) {
			if(angry) {
				defaultColor = batch.getColor();
				batch.setColor(Color.RED);
				batch.draw(
					texture, 
					enemyBody.getPosition().x - (ENEMY_WIDTH * .5f), enemyBody.getPosition().y - (ENEMY_HEIGHT * .5f), 
					ENEMY_WIDTH, ENEMY_HEIGHT);
				batch.setColor(defaultColor);
			}
			else {
				batch.draw(
						texture, 
						enemyBody.getPosition().x - (ENEMY_WIDTH * .5f), enemyBody.getPosition().y - (ENEMY_HEIGHT * .5f), 
						ENEMY_WIDTH, ENEMY_HEIGHT);
			}
			
		}
	}
	
	
	/* Caveman class */
	private class Player {
		private final float PLAYER_HEIGHT = 0.6f;
		private final float PLAYER_WIDTH = 0.35f;
		
		private Body playerBody;
		private Texture texture;
		
		public Player(Texture texture, float x, float y) {						
			this.texture = texture;
			
			playerBody = createPolygon(BodyType.DynamicBody, x, y, 1f, 0f, 0.8f, PLAYER_WIDTH * 0.5f,
					PLAYER_HEIGHT * 0.5f, CATEGORY_PLAYER, MASK_PLAYER);
		}
		
		public Body getBody() {
			return playerBody;
		}
		
		public void draw(SpriteBatch batch) {
			batch.draw(
					texture, 
					playerBody.getPosition().x - (PLAYER_WIDTH * .5f), playerBody.getPosition().y - (PLAYER_HEIGHT * .5f), 
					PLAYER_WIDTH, PLAYER_HEIGHT);
		}
		
	}
	
	
	// Once actors are defined, just make use of them!
	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
		// Center camera to get (0,0) as the origin of the Box2D world
		viewport.getCamera().position.set(viewport.getCamera().position.x + WORLD_WIDTH*0.5f, 
				viewport.getCamera().position.y + WORLD_HEIGHT*0.5f
				, 0);
		viewport.getCamera().update();
		
		batch = new SpriteBatch();

		Gdx.input.setInputProcessor(this);
		
		// Create Physics World
		world = new World(new Vector2(0,-10), true);

		// Instantiate DebugRenderer for rendering shapes
		debugRenderer = new Box2DDebugRenderer();

		// Creates a ground to avoid objects falling forever
		createGround();
		
		dinoTex = new Texture(Gdx.files.internal("data/blur/dinosaur.png"));
		playerTex = new Texture(Gdx.files.internal("data/caveman.png"));
		
		dinosaur1 = new Enemy(dinoTex, 7f,2f);
		dinosaur2 = new Enemy(dinoTex, 9f,2f);
		player1 = new Player(playerTex, 3f, 2f);
		player2 = new Player(playerTex, 5f, 2f);
		
		world.setContactListener(this);

	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Screen coordinates to world coordinates
		viewport.getCamera().unproject(point.set(x, y, 0));
		
		if (button == Input.Buttons.LEFT) {
			float force = 100f * player1.getBody().getMass();
			Vector2 target = new Vector2(point.x, point.y);
			Vector2 direction = target.sub(player1.getBody().getPosition());
			player1.getBody().applyForceToCenter(direction.scl(force), true);
		}
		return false;
	}
	
	private void createGround() {

		ChainShape chainShape = new ChainShape();
		
		chainShape.createLoop(new Vector2[] {
				new Vector2(.0f, .0f), 
				new Vector2(WORLD_WIDTH, .0f), 
				new Vector2(WORLD_WIDTH, 7f),
				new Vector2(WORLD_WIDTH-1f, 7f),
				new Vector2(WORLD_WIDTH-1f, 1.5f),
				new Vector2(1f, 1.5f),
				new Vector2(1f, 7f),
				new Vector2(0f, 7f),});
		
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		chainBodyDef.position.set(.0f, .0f);
		groundBody = world.createBody(chainBodyDef);
		
		FixtureDef groundFD = new FixtureDef();
		groundFD.filter.categoryBits = CATEGORY_GROUND;
		groundFD.filter.maskBits = MASK_GROUND;
		groundFD.shape = chainShape;
		groundFD.density = 0;
		
		groundBody.createFixture(groundFD);
		chainShape.dispose();
	}
	
	private Body createPolygon(BodyType type, float x, float y, float d, float r, float f, float halfwidth, float halfheight, short category, short maskBits) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x,y);
		bodyDef.angle=0;
		bodyDef.fixedRotation = true;
		
		Body square = world.createBody(bodyDef);
		
		FixtureDef fixtureDef=new FixtureDef();
 		fixtureDef.density=d;
 		fixtureDef.restitution=r;
 		fixtureDef.friction=f;
 		fixtureDef.shape=new PolygonShape();
 		((PolygonShape) fixtureDef.shape).setAsBox(halfwidth, halfheight);
 		fixtureDef.filter.categoryBits = category;
 		fixtureDef.filter.maskBits = maskBits;
 		
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
		playerTex.dispose();
		dinoTex.dispose();
		
		debugRenderer.dispose();
		
		batch.dispose();
		world.dispose();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.step(1/60f, 6, 2);

		batch.begin();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		
		// Draw actors
		player1.draw(batch);
		player2.draw(batch);
		dinosaur1.draw(batch);
		dinosaur2.draw(batch);

		batch.end();
		
		debugRenderer.render(world, viewport.getCamera().combined);
	}
	
	// Manage collisions, in this example, only for sensors
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();

		if (fixtureA.isSensor()) {
			Enemy e = (Enemy) bodyA.getUserData();
			e.setAngry(true);
			//Gdx.app.log(TAG, "T-Rex is angry!!!");
		}
		else if(fixtureB.isSensor()) {
			Enemy e = (Enemy) bodyB.getUserData();
			e.setAngry(true);
			//Gdx.app.log(TAG, "T-Rex is angry!!!");
		}
			
	}

	/* Check if any of the two cavemen are within the sensor radius */
	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();

		if (fixtureA.isSensor()) {
			Enemy e = (Enemy) bodyA.getUserData();
			e.setAngry(false);
			
			for(Contact c : world.getContactList()) {
				// If it is the sensor in question
				if(c.getFixtureA() == contact.getFixtureA()
						|| c.getFixtureB() == contact.getFixtureA()) {
					// If contacting another caveman
					if(c.getFixtureB().getFilterData().maskBits == MASK_PLAYER &&
							c.getFixtureB() != fixtureB) {
						e.setAngry(true);
						break;
					}
					else if(c.getFixtureA().getFilterData().maskBits == MASK_PLAYER &&
						c.getFixtureA() != fixtureB) {
						e.setAngry(true);
						break;
					}
				}
			}
		}
		else if(fixtureB.isSensor()) {
			Enemy e = (Enemy) bodyB.getUserData();
			e.setAngry(false);
			
			for(Contact c : world.getContactList()) {
				// If it is the sensor in question
				if(c.getFixtureA() == contact.getFixtureB()
						|| c.getFixtureB() == contact.getFixtureB()) {
					// If contacting another caveman
					if(c.getFixtureB().getFilterData().maskBits == MASK_PLAYER &&
							c.getFixtureB() != fixtureA) {
						e.setAngry(true);
						break;
					}
					else if(c.getFixtureA().getFilterData().maskBits == MASK_PLAYER &&
						c.getFixtureA() != fixtureA) {
						e.setAngry(true);
						break;
					}
				}
			}
		}
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
