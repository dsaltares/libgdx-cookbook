package com.cookbook.samples;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SpriteSample extends GdxSample {
	private static final float SCENE_WIDTH = 1280f;
	private static final float SCENE_HEIGHT = 720f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private TextureAtlas atlas;
	private Sprite background;
	private Sprite dinosaur;
	private Sprite caveman;
	private Array<Color> colors;
	private int currentColor;
	private Vector3 tmp;
	private BitmapFont font;
	private ShapeRenderer shapeRenderer;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		
		atlas = new TextureAtlas(Gdx.files.internal("data/prehistoric.atlas"));
		background = new Sprite(atlas.findRegion("background"));
		caveman = new Sprite(atlas.findRegion("caveman"));
		dinosaur = new Sprite(atlas.findRegion("trex"));
		
		background.setPosition(-background.getWidth() * 0.5f, -background.getHeight() * 0.5f);
		caveman.setOrigin(caveman.getWidth() * 0.5f, caveman.getHeight() * 0.5f);
		dinosaur.setPosition(100.0f, -85.0f);
		
		currentColor = 0;
		colors = new Array<Color>();
		colors.add(new Color(Color.WHITE));
		colors.add(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		colors.add(new Color(1.0f, 0.0f, 0.0f, 1.0f));
		colors.add(new Color(0.0f, 1.0f, 0.0f, 1.0f));
		colors.add(new Color(0.0f, 0.0f, 1.0f, 1.0f));
		
		tmp = new Vector3();
		
		font = new BitmapFont(Gdx.files.internal("data/fonts/oswald-64.fnt"));
		font.setColor(Color.BLACK);
		
		shapeRenderer = new ShapeRenderer();
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void dispose() {
		batch.dispose();
		atlas.dispose();
		font.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void render() {
		Rectangle cavemanRect = caveman.getBoundingRectangle();
		Rectangle dinosaurRect = dinosaur.getBoundingRectangle();
		boolean overlap = cavemanRect.overlaps(dinosaurRect);
		
		tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
		camera.unproject(tmp);
		caveman.setPosition(tmp.x - caveman.getWidth() * 0.5f, tmp.y - caveman.getHeight() * 0.5f);
		
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		background.draw(batch);
		caveman.draw(batch);
		dinosaur.draw(batch);
		
		if (overlap) {
			font.draw(batch, "Collision between caveman and dinosaur!", -SCENE_WIDTH * 0.5f + 20.0f, SCENE_HEIGHT * 0.5f - 20.0f);
		}
		
		batch.end();
		
		if (overlap) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(cavemanRect.x, cavemanRect.y, cavemanRect.width, cavemanRect.height);
			shapeRenderer.rect(dinosaurRect.x, dinosaurRect.y, dinosaurRect.width, dinosaurRect.height);
			shapeRenderer.end();
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.LEFT) {
			currentColor = (currentColor + 1) % colors.size;
			dinosaur.setColor(colors.get(currentColor));
		}
		
		return true;
	}
	
	@Override
	public boolean scrolled (int amount) {
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			caveman.scale(amount * 0.5f);
		}
		else {
			caveman.rotate(amount * 5.0f);
		}
		
		return true;
	}
}

