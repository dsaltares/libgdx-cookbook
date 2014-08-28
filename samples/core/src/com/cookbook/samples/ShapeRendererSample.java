package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShapeRendererSample extends GdxSample {
	private static final float SCENE_WIDTH = 40.0f;
	private static final float SCENE_HEIGHT = 22.50f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private ShapeRenderer shapeRenderer;
	
	private boolean drawGrid = true;
	private boolean drawFunction = true;
	private boolean drawCircles = true;
	private boolean drawRectangles = true;
	private boolean drawPoints = true;
	private boolean drawTriangles = true;
	
	private float debugFunction[]; 
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		shapeRenderer = new ShapeRenderer();
		
		debugFunction = new float[40];
		
		for (int x = -10; x < 10; ++x) {
			int i = (x + 10) * 2; 
			debugFunction[i] = x;
			debugFunction[i + 1] = x * x;
		}
		
		camera.position.set(0.0f, 0.0f, 0.0f);
		
		Gdx.app.log("ShapeRendererSample", "G: toggle grid");
		Gdx.app.log("ShapeRendererSample", "F: toggle function y = x^2");
		Gdx.app.log("ShapeRendererSample", "C: toggle circles");
		Gdx.app.log("ShapeRendererSample", "R: toggle rectangles");
		Gdx.app.log("ShapeRendererSample", "P: toggle points");
		Gdx.app.log("ShapeRendererSample", "T: toggle triangles");
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		drawDebugGraphics();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	private void drawDebugGraphics() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		// Draw grid
		if (drawGrid) {
			shapeRenderer.begin(ShapeType.Line);

			shapeRenderer.setColor(Color.RED);
			shapeRenderer.line(-SCENE_WIDTH, 0.0f, SCENE_WIDTH, 0.0f);
			shapeRenderer.line(0.0f, -SCENE_HEIGHT, 0.0f, SCENE_HEIGHT);
			
			shapeRenderer.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a);
			
			for (int i = -100; i <= 100; ++i) {
				if (i == 0)
					continue;
				
				shapeRenderer.line(-SCENE_WIDTH, i, SCENE_WIDTH, i);
			}
			
			for (int i = -100; i <= 100; ++i) {
				if (i == 0)
					continue;
				
				shapeRenderer.line(i, -SCENE_HEIGHT, i, SCENE_HEIGHT);
			}
			
			shapeRenderer.end();
		}
		
		if (drawFunction) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.ORANGE);
			shapeRenderer.polyline(debugFunction);
			shapeRenderer.end();
		}
		
		if (drawCircles) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.CYAN);
			
			shapeRenderer.circle(5.2f, 3.1f, 2.3f, 30);
			shapeRenderer.circle(-5.3f, 7.1f, 1.1f, 30);
			shapeRenderer.circle(12.4f, -6.4f, 1.75f, 30);
			shapeRenderer.circle(-9.1f, -5.8f, 2.25f, 30);
			
			shapeRenderer.end();
		}
		
		/*if (drawRectangles) {
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
						
			shapeRenderer.rect(7.2f, 2.4f, 3.3f, 2.8f, 0.0f, 0.0f, 45.0f);
			shapeRenderer.rect(-8.4f, 3.8f, 6.1f, 2.3f, 0.0f, 0.0f, 75.0f);
			shapeRenderer.rect(-4.2f, -3.4f, 3.3f, 2.8f, 0.0f, 0.0f, 25.0f);
			shapeRenderer.rect(3.2f, -6.4f, 3.9f, 1.8f, 0.0f, 0.0f, 60.0f);
			
			shapeRenderer.end();
		}*/
		
		if (drawPoints) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.MAGENTA);
			
			shapeRenderer.x(-5.0f, 0.0f, 0.25f);
			shapeRenderer.x(3.0f, 8.0f, 0.25f);
			shapeRenderer.x(-7.0f, 2.0f, 0.25f);
			shapeRenderer.x(7.0f, -3.0f, 0.25f);
			
			shapeRenderer.end();
		}
		
		if (drawTriangles) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLUE);
			
			shapeRenderer.triangle(-16.1f, -5.2f, -14.0f, -2.1f, -13.4f, 3.8f);
			
			shapeRenderer.end();
		}
	}
	
	public boolean keyDown (int keycode) {
		if (keycode == Keys.G) {
			drawGrid = !drawGrid;
		}
		else if (keycode == Keys.F) {
			drawFunction = !drawFunction;
		}
		else if (keycode == Keys.C) {
			drawCircles = !drawCircles;
		}
		else if (keycode == Keys.R) {
			drawRectangles = !drawRectangles;
		}
		else if (keycode == Keys.P) {
			drawPoints = !drawPoints;
		}
		else if (keycode == Keys.T) {
			drawTriangles = !drawTriangles;
		}
		
		return true;
	}
} 