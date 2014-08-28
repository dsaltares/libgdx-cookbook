package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

public class SpineSample extends GdxSample {
	private static final String TAG = "SpineSample";
	
	private static final float SCENE_WIDTH = 12.80f;
	private static final float SCENE_HEIGHT = 7.20f;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	
	private Vector3 point = new Vector3();
	
	private Stage stage;
	private TextButton runBtn;
	private TextButton punchBtn;
	private TextButton run2idleBtn;
	private BitmapFont font;
	
	private SkeletonRenderer renderer;
	private SkeletonRendererDebug debugRenderer;
	private SkeletonData skeletonData;
	private Skeleton skeleton;
	
	private AnimationStateData stateData;
	private AnimationState state;
	
	private TextureAtlas atlas;
	
	@Override
	public void create() {		
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		// Center camera
		viewport.getCamera().position.set(
				viewport.getCamera().position.x + SCENE_WIDTH*0.5f, 
				viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
				0);
		viewport.getCamera().update();
		viewport.update((int)SCENE_WIDTH, (int)SCENE_HEIGHT);
		
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"));
		
		stage = new Stage(new FitViewport(1280, 720));
		Gdx.input.setInputProcessor(stage);
		
		batch = new SpriteBatch();
		
		renderer = new SkeletonRenderer();
		debugRenderer = new SkeletonRendererDebug();
		debugRenderer.setBones(true);
		debugRenderer.setRegionAttachments(true);
		debugRenderer.setBoundingBoxes(true);
		debugRenderer.setMeshHull(true);
		debugRenderer.setMeshTriangles(true);
		
		loadSkeleton(Gdx.files.internal("data/spine/hero.json"), false);
		
		// Buttons
		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.font = font;
		tbs.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("data/scene2d/myactor.png"))));
		
		runBtn = new TextButton("Run", tbs);
		runBtn.setPosition(50,600);
		runBtn.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				state.clearTracks();
				skeleton.setToSetupPose();
				state.setAnimation(0, "run", true);
			};
		});
		
		punchBtn = new TextButton("Punch", tbs);
		punchBtn.setPosition(50,500);
		punchBtn.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				state.clearTracks();
				skeleton.setToSetupPose();
				state.setAnimation(0, "punch", false);
			};
		});
		
		run2idleBtn = new TextButton("Run2Idle", tbs);
		run2idleBtn.setPosition(50,400);
		run2idleBtn.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				state.clearTracks();
				skeleton.setToSetupPose();
				state.setAnimation(0, "run", true);
				state.setAnimation(0, "idle", true);
			};
		});
		
		stage.addActor(runBtn);
		stage.addActor(punchBtn);
		stage.addActor(run2idleBtn);
		
		
	}
	
	void loadSkeleton (FileHandle skeletonFile, boolean reload) {
		
		if (skeletonFile == null) 
			return;

		String atlasFileName = skeletonFile.nameWithoutExtension();
		
		if (atlasFileName.endsWith(".json"))
			atlasFileName = new FileHandle(atlasFileName).nameWithoutExtension();
		
		FileHandle atlasFile = skeletonFile.sibling(atlasFileName + ".atlas");
	
		TextureAtlasData data = !atlasFile.exists() ? null : new TextureAtlasData(atlasFile, atlasFile.parent(), false);
		atlas = new TextureAtlas(data) {
			public AtlasRegion findRegion (String name) {
				AtlasRegion region = super.findRegion(name);
				return region;
			}
		};

		try {
			String extension = skeletonFile.extension();
			if (extension.equalsIgnoreCase("json")) {
				SkeletonJson json = new SkeletonJson(atlas);
				json.setScale(.5f);
				skeletonData = json.readSkeletonData(skeletonFile);
			} else {
				SkeletonBinary binary = new SkeletonBinary(atlas);
				binary.setScale(.5f);
				skeletonData = binary.readSkeletonData(skeletonFile);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Gdx.app.log(TAG, "Error loading skeleton: " + skeletonFile.name());
			return;
		}

		skeleton = new Skeleton(skeletonData);
		viewport.getCamera().project(point.set(SCENE_WIDTH * .5f, 0, 0));
		skeleton.setX(point.x);
		skeleton.setY(point.y);
		skeleton.updateWorldTransform();
		
		stateData = new AnimationStateData(skeletonData);
		state = new AnimationState(stateData);
		
		skeleton.setSkin(skeletonData.getSkins().first());
		
		stateData.setMix("run", "idle", 5f);
		
		state.setAnimation(0, "idle", true);
		
	}
	
	@Override
	public void dispose() {
		atlas.dispose();
		font.dispose();
		batch.dispose();
		stage.dispose();
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float delta = Gdx.graphics.getDeltaTime();
		
		state.update(delta);
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		
		batch.begin();
		renderer.draw(batch, skeleton);
		batch.end();

		debugRenderer.draw(skeleton);
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
