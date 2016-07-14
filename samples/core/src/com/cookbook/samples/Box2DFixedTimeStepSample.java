package com.cookbook.samples;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.box2d.RayCastManager;

public class Box2DFixedTimeStepSample extends GdxSample {
	private static final String TAG = "Box2DFixedTimeStepSample";

	static final float PIXELS_TO_METRES = 0.01f;
	static final float METRES_TO_PIXELS = PIXELS_TO_METRES * 10000f;

	private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
	private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high

	private Viewport viewport;
	private SpriteBatch batch;

	//General Box2D
	Box2DDebugRenderer debugRenderer;
	World world;
	Body groundBody;

	// Generated boxes
	Array<Box> activeBoxes;
	Texture boxTexture;

	// last second
	double lastTime;

	//Time-step stuff
	private double accumulator;
	private double currentTime;
	private float step = 1f/60f;
	TimeStep timeStepType;

	// Current time-step
	int lastFPS = 0, frameCount = 0;
	BitmapFont font, font2;
	float titleWidth;

	Vector2 pos = new Vector2();
	
	Camera gameCamera, stageCamera;
	
	Stage stage;
	TextButton btnFixed, btnFixedInt, btnVariable;

	public enum TimeStep { FIXED, FIXED_INTERPOLATION, VARIABLE }

	private class Box {		
		private final Body boxBody;

		private float BOX_WIDTH;
		private float BOX_HEIGHT;
		
		public float x, y;
		public float angle;

		private Texture boxTex;
		
		public Box(float x, float y, float box_width, float box_height, Texture texture) {
			BOX_WIDTH = box_width;
			BOX_HEIGHT = box_height;
			
			boxTex = texture;
			
			boxBody = createPolygon(BodyType.DynamicBody, x, y, 1f, 0.2f, 0.8f, box_width * 0.5f,
					box_height * 0.5f);

			this.x = x - (BOX_WIDTH*.5f);
			this.y = y - (BOX_HEIGHT*.5f);
			this.angle = boxBody.getAngle();

		}

		public Body getBody() {
			return boxBody;
		}

		public void draw(SpriteBatch batch) {
			batch.draw(
				boxTex, 
				x - (BOX_WIDTH*.5f), y - (BOX_HEIGHT*.5f),
				BOX_WIDTH*.5f, BOX_HEIGHT*.5f, 
				BOX_WIDTH, BOX_HEIGHT, 
				1f, 1f, 
				angle * MathUtils.radDeg, 
				0, 0, 
				boxTex.getWidth(), boxTex.getHeight(), 
				false, false);
		}

	}

	@Override
	public void create () {
		super.create();

		gameCamera = new OrthographicCamera();
		stageCamera = new OrthographicCamera();
		
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, gameCamera);
		batch = new SpriteBatch();
		
		Viewport v2 = new FitViewport(SCENE_WIDTH * METRES_TO_PIXELS, SCENE_HEIGHT * METRES_TO_PIXELS, stageCamera);
		stage = new Stage(v2, batch);
		
		// Center camera to get (0,0) as the origin of the Box2D world
		viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f
				, 0);
		viewport.getCamera().update();
		
		// Stage will listen for input events (button click in our sample)
		Gdx.input.setInputProcessor(stage);
		
		// Create Physics World
		world = new World(new Vector2(0,-10), true);

		font = new BitmapFont(Gdx.files.internal("data/verdana39.fnt"), false);
		font2 = new BitmapFont(Gdx.files.internal("data/default.fnt"), false);
		
		// Time-step variables
		accumulator = 0.0;
		currentTime = TimeUtils.millis() / 1000.0;
		lastTime = currentTime;
		timeStepType = TimeStep.VARIABLE;

		// Track active boxes
		activeBoxes = new Array<Box>();
		
		// Retrieve the common box texture
		boxTexture = new Texture(Gdx.files.internal("data/box2D/box.png"));

		// Creates a ground to avoid objects falling forever
		createGround();
		
		// Generate the buttons to switch between time-step types
		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.font = font2;
		TextureRegion buttonImg = new TextureRegion(new Texture(Gdx.files.internal("data/scene2d/myactor.png")));
		tbs.up = new TextureRegionDrawable(buttonImg);
		
		float width = buttonImg.getRegionWidth();
		float height = buttonImg.getRegionHeight();
				
		pos = new Vector2(300f, 50f);
		btnVariable = new TextButton("Variable", tbs);
		btnVariable.setPosition(pos.x, pos.y);
		btnVariable.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(timeStepType != TimeStep.VARIABLE) {
					timeStepType = TimeStep.VARIABLE;
					accumulator = 0;
				}
			};
		});
		
		pos = new Vector2(500f, 50f);
		
		btnFixed = new TextButton("Fixed", tbs);
		btnFixed.setPosition(500, 50);
		btnFixed.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(timeStepType != TimeStep.FIXED) {
					timeStepType = TimeStep.FIXED;
					
				}
			};
		});
		
		pos = new Vector2(700f, 50f);
		
		btnFixedInt = new TextButton("Fixed-Interpolation", tbs);
		btnFixedInt.setPosition(700, 50);
		btnFixedInt.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(timeStepType != TimeStep.FIXED_INTERPOLATION) {
					timeStepType = TimeStep.FIXED_INTERPOLATION;
				}
			};
		});
		
		// Place buttons
		Table table = new Table();
		stage.addActor(table);
		table.setPosition(600, 85);
		table.debug();
		
		table.add(btnFixedInt).width(width).height(height).padRight(25f);
		table.add(btnVariable).width(width).height(height).padRight(25f);
		table.add(btnFixed).width(width).height(height);
		
		// Fill scene with boxes
		for(int i=0; i<10; i++)
			activeBoxes.add(generateRandomBox(0.5f, 0.5f));

		// Instantiate the class in charge of drawing physics shapes
		debugRenderer = new Box2DDebugRenderer();
		
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

	private Body createPolygon(BodyType type, float x, float y, float d, float r, float f, float halfwidth, float halfheight) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x,y);
		bodyDef.angle=45 * MathUtils.degreesToRadians;

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

	private Box generateRandomBox(float boxWidth, float boxHeight) {
		// Create X boxes and place them randomly within the screen
		float minWidth = 1f + (boxWidth*0.5f);
		float maxWidth = SCENE_WIDTH - 1f - (boxWidth*0.5f);
		float maxHeight = SCENE_HEIGHT - (boxHeight*0.5f);
		Random rand = new Random();

		float x;
		x = rand.nextFloat() * ( maxWidth - minWidth) + minWidth;

		return new Box(x,maxHeight, boxWidth, boxHeight, boxTexture);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		boxTexture.dispose();
		debugRenderer.dispose();
		
		stage.dispose();
		batch.dispose();
		world.dispose();
	}

	// Interpolate every single box
	public void interpolate(float alpha) {
		for (Box box : activeBoxes) {
			Body body = box.getBody();
			if(body.isActive()) {
				Transform transform = body.getTransform();
				Vector2 bodyPosition = transform.getPosition();
				float bodyAngle = transform.getRotation();
	
				box.x = bodyPosition.x * alpha + box.x * (1.0f - alpha);
				box.y = bodyPosition.y * alpha + box.y * (1.0f - alpha);
				box.angle = bodyAngle * alpha + box.angle * (1.0f - alpha);
			}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Update boxes position
		for(Box box : activeBoxes) {
			Transform bodyTransform = box.getBody().getTransform();
			Vector2 bodyPosition = bodyTransform.getPosition();
			box.x = bodyPosition.x;
			box.y = bodyPosition.y;
			box.angle = bodyTransform.getRotation();
		}
		
		frameCount++;
		
		double frameTime = 0;
		
		// Step the world according to the selected method
		if(timeStepType == TimeStep.VARIABLE) {
			double newTime = TimeUtils.millis() / 1000.0;
			frameTime = newTime - currentTime;
			currentTime = newTime;
			world.step((float) frameTime, 6, 2);
		}
		else if(timeStepType == TimeStep.FIXED || timeStepType == TimeStep.FIXED_INTERPOLATION) {
			double newTime = TimeUtils.millis() / 1000.0;
			frameTime = Math.min(newTime - currentTime, 0.25);

			currentTime = newTime;
			accumulator += frameTime;
			
			while (accumulator >= step) {
				world.step(step, 6, 2);
				accumulator -= step;
			}
			
			if(timeStepType == TimeStep.FIXED_INTERPOLATION)
				interpolate((float) (accumulator/step));
				
			
		}

		// Draw fps and time-step type
		batch.begin();
		font.draw(batch, "FPS: " + lastFPS, 100, 100);
		font2.draw(batch, timeStepType.name(), 100, 50);
		batch.end();
		
		// Draw boxes
		batch.begin();
		batch.setProjectionMatrix(viewport.getCamera().combined);

		for(Box box : activeBoxes)
			box.draw(batch);
		
		batch.end();
		
		// Draw buttons
		stage.act((float) frameTime);
		stage.draw();
		
		// Make boxes appear every second and get the frames per second(fps)
		if(currentTime>= lastTime+1) {
			lastTime++;
			lastFPS = frameCount;
			frameCount = 0;
			for(int i=0; i<2; i++)
				activeBoxes.add(generateRandomBox(0.25f, 0.25f));
		}

		// Render debug lines
		//debugRenderer.render(world, viewport.getCamera().combined);
	}
}
