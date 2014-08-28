package com.cookbook.audio;

import com.badlogic.gdx.math.Vector2;

public class Listener {
	private Vector2 position = new Vector2();
	private Vector2 direction = new Vector2();
	
	public Listener() {
		this(new Vector2(0.0f, 0.0f), new Vector2(0.0f, 1.0f));
	}
	
	public Listener(Vector2 position, Vector2 direction) {
		update(position, direction);
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public Vector2 getDirection() {
		return direction;
	}
	
	public void update(Vector2 position, Vector2 direction) {
		this.position.set(position);
		this.direction.set(direction);
	}
}
