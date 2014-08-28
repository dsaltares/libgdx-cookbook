package com.cookbook.audio;

import com.badlogic.gdx.audio.Sound;

public class SoundData {
	private Sound sound;
	private float duration;
	private float falloffStart;
	private float maxDistance;
	
	public SoundData(Sound sound, float duration, float falloffStart, float maxDistance) {
		this.sound = sound;
		this.duration = duration;
		this.falloffStart = falloffStart;
		this.maxDistance = maxDistance;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public float getDuration() {
		return duration;
	}
	
	public float getFalloffStart() {
		return falloffStart;
	}
	
	public float getMaxDistance() {
		return maxDistance;
	}
}
