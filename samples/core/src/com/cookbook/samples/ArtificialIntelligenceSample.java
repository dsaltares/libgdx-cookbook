package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.Agent;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cookbook.ai.Caveman;
import com.cookbook.ai.Dinosaur;

public class ArtificialIntelligenceSample extends GdxSample {
	private static final String TAG = "AISample";
	
	private Agent caveman, dinosaur;
	
	@Override
	public void create () {
		super.create();

		caveman = new Caveman();
		dinosaur = new Dinosaur((Caveman) caveman);
	}
	
	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		caveman.update(Gdx.graphics.getDeltaTime());
		dinosaur.update(Gdx.graphics.getDeltaTime());
		
	}


}
