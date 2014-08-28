package com.cookbook.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.ObjectMap;

/**
* @class AnimationData
* 
* @brief Holds animation frame sequences and reads them from JSON files and a spritesheet-like texture
* 
* It will look for a name.json and name.png files for loading.
*
*/
public class SpriteAnimationData {
	
	// Package private members for the AnimationLoader ease of access
	Texture texture = null;
	int rows = 0;
	int columns = 0;
	float frameDuration = 0.0f;
	ObjectMap<String, Animation> animations = new ObjectMap<String, Animation>();
	Animation defaultAnimation = null;

	
	public SpriteAnimationData() {}
	
	/**
	 * @param animationName name of the desired animation
	 * @return animation object containing the sequence of frames, null if not found
	 */
	public Animation getAnimation(String animationName) {
		Animation animation = animations.get(animationName);
		
		if (animation == null) {
			Gdx.app.log("SpriteAnimationData", "Animation: " + animationName + " not found returning default");
			
			return defaultAnimation;
		}
		
		return animation;
	}
	
	public Texture getTexture() {
		return texture;
	}
}