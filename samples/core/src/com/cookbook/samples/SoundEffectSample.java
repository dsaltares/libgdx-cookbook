package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;

public class SoundEffectSample extends GdxSample {
	private IntMap<Sound> sounds;
	
	@Override
	public void create() {		
		sounds = new IntMap<Sound>();
		sounds.put(Keys.NUM_1, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_01.wav")));
		sounds.put(Keys.NUM_2, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_02.wav")));
		sounds.put(Keys.NUM_3, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_03.mp3")));
		sounds.put(Keys.NUM_4, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_04.wav")));
		sounds.put(Keys.NUM_5, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_05.mp3")));
		sounds.put(Keys.NUM_6, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_06.mp3")));
		sounds.put(Keys.NUM_7, Gdx.audio.newSound(Gdx.files.internal("data/sfx/sfx_07.wav")));
		
		Gdx.input.setInputProcessor(this);
		
		Gdx.app.log("SoundEffectSample", "Instructions");
		Gdx.app.log("SoundEffectSample", "- Press keys 1-0 to play sounds");
		Gdx.app.log("SoundEffectSample", "- Press s to stop all sounds");
		Gdx.app.log("SoundEffectSample", "- Press p to pause all sounds");
		Gdx.app.log("SoundEffectSample", "- Press r to resume all soud");
	}

	@Override
	public void dispose() {
		for (Sound sound : sounds.values()) {
			sound.dispose();
		}
	}
	
	@Override
	public boolean keyDown (int keycode) {
		
		if (keycode == Keys.S) {
			for (Sound sound : sounds.values()) {
				sound.stop();
			}
			
			Gdx.app.log("SoundEffectSample", "Sounds stopped");
		}
		else if (keycode == Keys.P) {
			for (Sound sound : sounds.values()) {
				sound.pause();
			}
			
			Gdx.app.log("SoundEffectSample", "Sounds paused");
		}
		else if (keycode == Keys.R) {
			for (Sound sound : sounds.values()) {
				sound.resume();
			}
			
			Gdx.app.log("SoundEffectSample", "Sounds resumed");
		}
		else {
			Sound sound = sounds.get(keycode);
			
			if (sound != null)
			{
				sound.play();
				Gdx.app.log("SoundEffectSample", "Playing sound");
			}
		}
		
		return true;
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		Sound sound = sounds.get(MathUtils.random(sounds.size - 1));
		sound.play();
		
		Gdx.app.log("SoundEffectSample", "Playing sound");
		return true;
	}
}
