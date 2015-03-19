package com.cookbook.ashley;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class UserControlledSystem extends IteratingSystem {
	private ComponentMapper<MovementComponent> mm = ComponentMapper.getFor(MovementComponent.class);
	private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
	private Vector2 src;
	private Vector3 dest;
	private Camera camera;
	
	public UserControlledSystem(Camera camera) {
		super(Family.all(TransformComponent.class, MovementComponent.class, UserControlledComponent.class).get());
		src = new Vector2();
		dest = new Vector3();
		
		this.camera = camera;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		MovementComponent movement = mm.get(entity);
		TransformComponent transform = tm.get(entity);

		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			camera.unproject(dest.set(Gdx.input.getX(), Gdx.input.getY(),0));
			src.set(transform.pos);
			dest.sub(src.x, src.y, 0).nor();
			movement.velocity.set(dest.x, dest.y).scl(3f);
		}
		else
			movement.velocity.setZero();
	}
}