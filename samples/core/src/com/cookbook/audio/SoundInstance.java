package com.cookbook.audio;


import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class SoundInstance {
	private SoundData data;
	private long id;
	private Vector2 position;
	private long startTime;
	
	private Vector2 tmp;
	
	public SoundInstance(SoundData data) {
		this(data, new Vector2(0.0f, 0.0f));
	}
	
	public SoundInstance(SoundData data, Vector2 position) {
		this.data = data;
		this.id = -1;
		this.position = position;
		this.startTime = -1;
		this.tmp = new Vector2();
		
		play();
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 position) {
		this.position.set(position);
	}
	
	public boolean update(Listener listener) {
		if (isFinished()) {
			return true;
		}
		
		// Calculate pan
		Vector2 listenerPos = listener.getPosition();
		Vector2 listenerDir = listener.getDirection();
		
		tmp.set(position);
		tmp.sub(listenerPos);
		tmp.nor();
		
		float angle = Math.abs(listenerDir.angleRad() - tmp.angleRad());
		boolean isRight = tmp.crs(listenerDir) > 0.0f;
		float pan = 0.0f;
		
		if (angle > MathUtils.PI * 0.5f) {
			angle -= MathUtils.PI * 0.5f;
			pan = Interpolation.linear.apply(isRight ? 1.0f : -1.0f, 0.0f, angle / (MathUtils.PI * 0.5f));
		}
		else {
			pan = Interpolation.linear.apply(0.0f, isRight ? 1.0f : -1.0f, angle / (MathUtils.PI * 0.5f));
		}
		
		// Calculate volume
		float distance = position.dst(listenerPos);
		float falloffStart = data.getFalloffStart();
		float volume = MathUtils.clamp(1.0f - (distance - falloffStart) / (data.getMaxDistance() - falloffStart),
									   0.0f,
									   1.0f);
		
		// Apply results
		data.getSound().setPan(id, pan, volume);
		
		return false;
	}
	
	public boolean isFinished() {
		return startTime > 0 && TimeUtils.timeSinceMillis(startTime) / 1000.0f > data.getDuration();
	}
	
	private void play() {
		id = data.getSound().play();
		startTime = TimeUtils.millis();
	}
}
