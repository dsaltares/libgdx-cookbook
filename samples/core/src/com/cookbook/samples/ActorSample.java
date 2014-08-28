package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.math.Interpolation.*;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class ActorSample extends GdxSample {
	private static final String TAG = "ActorSample";
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;

	private Viewport viewport;
	private MyActor myactor;
	private SpriteBatch batch;
	
	private Stage stage;

	public class MyActor extends Actor implements Disposable {
		TextureRegion region = new TextureRegion( new Texture(Gdx.files.internal("data/scene2d/myactor.png")) );

		public MyActor() {
			setPosition(SCENE_WIDTH * .5f, SCENE_HEIGHT * .5f);
			setWidth(1.61f);
			setHeight(0.58f);
		}

		@Override
		public void draw(Batch batch, float alpha){
	        Color color = getColor();
	        batch.setColor(color.r, color.g, color.b, color.a * alpha);
	        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
	            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			region.getTexture().dispose();
		}
	}

	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		batch = new SpriteBatch();
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(this);

		myactor = new MyActor();

		stage.addActor(myactor);
		
		Gdx.app.log(TAG, "Press these keys.\n"
				+ "\t1 - RotatedBy Action\n"
				+ "\t2 - MoveTo Action\n"
				+ "\t3 - FadeOut Action\n"
				+ "\t4 - FadeIn Action\n"
				+ "\t5 - Color Action\n"
				+ "\t6 - Scale Action\n"
				+ "\t7 - Size Action\n"
				+ "\t8 - Hide Action\n"
				+ "\t9 - Visble Action\n"
				+ "\t0 - TimeScale Action\n"
				+ "\tQ - Sequence Action\n"
				+ "\tW - Repeat Action\n"
				+ "\tE - Forever Action\n"
				+ "\tR - Parallel Action\n"
				+ "\tT - After Action\n"
				+ "\tY - Delay Action\n"
				+ "\tU - RemoveActor Action\n");
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public boolean keyDown (int keycode) {
		switch(keycode) {
		
		case Keys.NUM_1:
			Gdx.app.log(TAG, "RotateBy Action");
			myactor.clearActions();
			// Swing interpolation
			myactor.addAction(rotateBy(90f, 1f, swing));
			break;
			
			
		case Keys.NUM_2:
			Gdx.app.log(TAG, "MoveTo Action");
			myactor.clearActions();
			myactor.addAction(moveTo(myactor.getX() + 0.3f, myactor.getY() + 0.3f, 1f));
			break;
			
		case Keys.NUM_3:
			Gdx.app.log(TAG, "FadeOut Action");
			myactor.clearActions();
			myactor.addAction(fadeOut(2f));
			break;
			
		case Keys.NUM_4:
			Gdx.app.log(TAG, "FadeIn Action");
			myactor.clearActions();
			myactor.addAction(fadeIn(2f));
			break;
			
		case Keys.NUM_5:
			Gdx.app.log(TAG, "Color Action");
			myactor.clearActions();
			myactor.addAction(color(Color.RED, 2f));
			break;
			
		case Keys.NUM_6:
			Gdx.app.log(TAG, "Scale Action");
			myactor.clearActions();
			myactor.addAction(scaleTo(2f, 2f, 2f));
			break;
			
		case Keys.NUM_7:
			Gdx.app.log(TAG, "Size Action");
			myactor.clearActions();
			myactor.addAction(sizeTo(0.3f, 0.3f, 2f));
			break;
			
		case Keys.NUM_8:
			Gdx.app.log(TAG, "Hide Action");
			myactor.clearActions();
			myactor.addAction(hide());
			break;
			
		case Keys.NUM_9:
			Gdx.app.log(TAG, "Visible Action");
			myactor.clearActions();
			myactor.addAction(visible(true));
			break;
		
		case Keys.NUM_0:
			Gdx.app.log(TAG, "TimeScale Action");
			myactor.clearActions();
			myactor.addAction(timeScale(1.5f, rotateBy(90f, 2f)));
			break;
			
		case Keys.Q:
			Gdx.app.log(TAG, "Secuence Action");
			myactor.clearActions();
			myactor.addAction(sequence(moveTo(myactor.getX() +0.3f , myactor.getY() + 0.3f, 2f), rotateBy(90f, 2f)));
			break;
			
		case Keys.W:
			Gdx.app.log(TAG, "Repeat Action");
			myactor.clearActions();
			myactor.addAction(repeat(3, rotateBy(90f, 2f)));
			break;
			
		case Keys.E:
			Gdx.app.log(TAG, "Forever Action");
			myactor.clearActions();
			myactor.addAction(forever(rotateBy(90f, 2f)));
			break;
			
		case Keys.R:
			Gdx.app.log(TAG, "Parallel Action3");
			myactor.clearActions();
			myactor.addAction(parallel(moveTo(myactor.getX() -0.3f, myactor.getY(), 1.5f), fadeIn(1.75f)));
			break;
			
		case Keys.T:
			Gdx.app.log(TAG, "After Action");
			myactor.clearActions();
			// Blocks a sequence
			myactor.addAction(sequence(fadeOut(2f), fadeIn(2f), rotateBy(-90f,2f), after(fadeIn(10f)), fadeOut(2f)));
			break;
			
		case Keys.Y:
			Gdx.app.log(TAG, "Delay Action");
			myactor.clearActions();
			myactor.addAction(delay(5f, rotateBy(90f, 2f)));
			break;

		case Keys.U:
			Gdx.app.log(TAG, "RemoveActor Action");
			myactor.clearActions();
			myactor.addAction(removeActor());
			break;

		default:
			Gdx.app.log(TAG, "Unregistered key");
			break;
		}

		return true;
	}
}
