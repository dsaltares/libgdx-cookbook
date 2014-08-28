package com.cookbook.ashley;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class RenderSystem extends EntitySystem {
	private ImmutableArray<Entity> entities;
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
	private ComponentMapper<TextureComponent> vm = ComponentMapper.getFor(TextureComponent.class);
	private ComponentMapper<SizeComponent> sm = ComponentMapper.getFor(SizeComponent.class);
	
	public RenderSystem(OrthographicCamera camera){
		batch = new SpriteBatch();
		
		this.camera = camera;
	}

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.getFor(TransformComponent.class, TextureComponent.class, SizeComponent.class));
	}

	@Override
	public void update(float deltaTime) {
		camera.update();
		
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		
		for (int i = 0; i < entities.size(); ++i) {
			
			Entity e = entities.get(i);
			
			TransformComponent transform = tm.get(e);
			TextureComponent visual = vm.get(e);
			SizeComponent size = sm.get(e);
			
			batch.draw(visual.region, 
					transform.pos.x, transform.pos.y, 
					size.width*.5f, size.height*.5f, 
					size.width, size.height, 
					transform.scale.x, transform.scale.y, 
					transform.rotation);
		}
		
		batch.end();
	}
}