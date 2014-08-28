package com.cookbook.samples;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.tween.ActorAccessor;

public class TweenEngineSample extends GdxSample {
	private static final String TAG = "TweenEngineSample";

	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;

	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;

	private Vector3 point = new Vector3();

	private Stage stage;
	private BitmapFont font;
	private Label auxLbl2, auxLbl1, auxLbl3;
	private Image packtImg;
	private Container<Label> wrapper;
	private Texture packtLogo;

	private TweenManager manager;


	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();

		stage = new Stage(viewport, batch);

		Tween.registerAccessor(Actor.class, new ActorAccessor());
		Tween.setWaypointsLimit(10);
		
		manager = new TweenManager();

		font = new BitmapFont(Gdx.files.internal("data/fonts/play.fnt"), false);	
		Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

		auxLbl1 = new Label("DAVID SALTARES MARQUEZ", ls);
		auxLbl1.setPosition(SCENE_WIDTH*.5f, SCENE_HEIGHT+100);

		auxLbl2 = new Label("ALBERTO CEJAS SANCHEZ", ls);
		auxLbl2.setPosition(SCENE_WIDTH*.5f, -100);

		auxLbl3 = new Label("AND", ls);
		wrapper = new Container<Label>(auxLbl3);
		wrapper.setTransform(true);
		wrapper.setPosition(-100, SCENE_HEIGHT*.5f);

		stage.addActor(auxLbl1);
		stage.addActor(auxLbl2);
		stage.addActor(wrapper);

		// Names
		Timeline.createSequence()
		.setUserData("section1")
		.pushPause(.5f)

		// Names start invisible
		.push(Tween.set(auxLbl1, ActorAccessor.OPACITY).target(0))
		.push(Tween.set(auxLbl2, ActorAccessor.OPACITY).target(0))

		// Names come in from the upper and lower limits while fading in
		.beginParallel()
		.push(Tween.to(auxLbl1, ActorAccessor.POS_XY, 1.25f).targetRelative(0, -340).ease(Quart.OUT))
		.push(Tween.to(auxLbl2, ActorAccessor.POS_XY, 1.25f).targetRelative(0, 340).ease(Quart.OUT))
		.push(Tween.to(auxLbl1, ActorAccessor.OPACITY, .55f).target(1).ease(Cubic.IN))
		.push(Tween.to(auxLbl2, ActorAccessor.OPACITY, .55f).target(1).ease(Cubic.IN))
		.end()

		.pushPause(0.25f)

		// "AND" size must be smaller than names
		.push(Tween.set(wrapper, ActorAccessor.SCALE_XY).target(0.75f, 0.75f))

		// "AND" comes into scene
		.beginParallel()
		.push(Tween.to(wrapper, ActorAccessor.POS_XY, .5f).targetRelative(100+SCENE_WIDTH*.5f, 0).ease(Quart.OUT))
		.push(Tween.to(wrapper, ActorAccessor.ROTATION, .5f).target(-360*3).ease(Quad.OUT))
		.end()

		.pushPause(1f)

		// Names and "AND" leave the scene
		.beginParallel()
		.push(Tween.to(auxLbl1, ActorAccessor.POS_XY, .75f).target(auxLbl1.getX(), -100).ease(Quart.IN))
		.push(Tween.to(auxLbl2, ActorAccessor.POS_XY, .75f).target(auxLbl2.getX(), -100).ease(Quart.IN))
		.push(Tween.to(wrapper, ActorAccessor.POS_XY, .75f).target(wrapper.getX(), -100).ease(Quart.IN))
		.end()

		// Hide them for the future
		.push(Tween.set(wrapper, ActorAccessor.OPACITY).target(0))
		.push(Tween.set(auxLbl1, ActorAccessor.OPACITY).target(0))
		.push(Tween.set(auxLbl2, ActorAccessor.OPACITY).target(0))
		
		.setCallback(callback)
		.start(manager);
	}

	private final TweenCallback callback = new TweenCallback() {
		@Override
		public void onEvent(int eventType, BaseTween<?> tween) {
			// TODO Auto-generated method stub
			if(eventType == TweenCallback.COMPLETE) {
				if(tween.getUserData().toString().compareTo("section1") == 0) {
					auxLbl1.setText("IN ASSOCIATION WITH");
					auxLbl1.pack();
					auxLbl1.setPosition(SCENE_WIDTH*.5f, SCENE_HEIGHT * .5f + 100);

					auxLbl2.setText("PRESENT");
					auxLbl2.pack();
					auxLbl2.setPosition(SCENE_WIDTH*.5f, SCENE_HEIGHT * .5f + 100);

					packtLogo = new Texture(Gdx.files.internal("data/packt.png"));
					packtImg = new Image(packtLogo);
					packtImg.pack();
					packtImg.setPosition(SCENE_WIDTH *.5f, SCENE_HEIGHT * .5f);
					Color color = packtImg.getColor();
					color.a = 0;
					packtImg.setColor(color);

					stage.addActor(packtImg);

					Timeline.createSequence()

					.setUserData("section2")

					.pushPause(.5f)

					.beginParallel()
					.beginSequence()
					// "IN ASSOCIATION WITH" appears
					.push(Tween.to(auxLbl1, ActorAccessor.OPACITY, .2f).target(1).ease(Quad.OUT))
					.push(Tween.to(auxLbl1, ActorAccessor.OPACITY, 2f).target(0).ease(Cubic.IN))
					.end()
					.push(Tween.to(auxLbl1, ActorAccessor.POS_XY, 2.5f).targetRelative(0, -32).ease(Quart.OUT))
					.end()

					// Logo becomes visible
					.push(Tween.to(packtImg, ActorAccessor.OPACITY, 2f).target(1).ease(Cubic.IN))

					.pushPause(1f)

					.push(Tween.to(packtImg, ActorAccessor.OPACITY, 1.5f).target(0).ease(Cubic.IN))

					.beginParallel()
					.beginSequence()
					// "PRESENT" appears
					.push(Tween.to(auxLbl2, ActorAccessor.OPACITY, .2f).target(1).ease(Quad.OUT))
					.push(Tween.to(auxLbl2, ActorAccessor.OPACITY, 2f).target(0).ease(Cubic.IN))
					.end()
					.push(Tween.to(auxLbl2, ActorAccessor.POS_XY, 2.5f).targetRelative(0, -32).ease(Quart.OUT))
					.end()

					.setCallback(callback)	
					.start(manager);
				}
				else if(tween.getUserData().toString().compareTo("section2") == 0) {

					auxLbl1.setText("LIBGDX FOR CROSS PLATFORM GAME");
					auxLbl1.pack();
					auxLbl1.setPosition(SCENE_WIDTH * .5f, SCENE_HEIGHT + 100f);

					auxLbl3.setText("DEVELOPMENT COOKBOOK");
					auxLbl3.pack();
					wrapper.setPosition(SCENE_WIDTH * .5f, SCENE_HEIGHT * .5f);
					
					Timeline.createSequence()

						.setUserData("section3")
	
						.push(Tween.set(auxLbl1, ActorAccessor.OPACITY).target(1))
						.push(Tween.set(wrapper, ActorAccessor.SCALE_XY).target(.5f, .5f))
	
						.push(Tween.to(auxLbl1, ActorAccessor.POS_XY, 1.5f).targetRelative(0, - SCENE_HEIGHT * .5f).ease(Quart.OUT))
	
						// Book title appears
						.push(Tween.to(wrapper, ActorAccessor.OPACITY, .5f).target(1).ease(Quad.OUT))
						
						.push(Tween.to(wrapper, ActorAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
						
					.setCallback(callback)
					.start(manager);
				}
				else if(tween.getUserData().toString().compareTo("section3") == 0) {
					
					Timeline t = Timeline.createSequence()

						.setUserData("section4-1")
						
						.push(Tween.to(wrapper, ActorAccessor.POS_XY, .1f).targetRelative(0, 5).ease(Quart.OUT))
						
						.beginSequence()
							.push(Tween.to(wrapper, ActorAccessor.POS_XY, .05f).targetRelative(0, -10).ease(Quart.OUT))
							.push(Tween.to(wrapper, ActorAccessor.POS_XY, .05f).targetRelative(0, 10).ease(Quart.OUT))
						.end().repeat(15, 0f)			
						
					.start(manager);
					
					Timeline.createSequence()
					
						.setUserData("section4-2")

						.pushPause(.1f)
						// Color from 0 to 1
						.push(Tween.to(auxLbl3, ActorAccessor.TINT, t.getFullDuration()).target(0.4f,0,0).ease(Quad.OUT))
						.push(Tween.to(wrapper, ActorAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.6f).target(1.4f, 1.4f).ease(Cubic.OUT))
						.push(Tween.to(auxLbl3, ActorAccessor.TINT, .75f).target(0.6f,0,0).ease(Quad.OUT))
					
					.start(manager);
					
				}
			}

		}
	};

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();
		
		manager.update(delta);

		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

}

