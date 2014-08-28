package com.cookbook.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

/**
* @class AnimationLoader
* 
* @brief Asynchronous asset loader for AnimationData objects
*
*/
public class SpriteAnimationLoader extends AsynchronousAssetLoader<SpriteAnimationData, SpriteAnimationLoader.AnimationParameter > {

	static public class AnimationParameter extends AssetLoaderParameters<SpriteAnimationData> {
	}
	
	private SpriteAnimationData animationData = null;
	
	/**
	 * Creates a new AnimationLoader
	 * 
	 * @param resolver file resolver to be used
	 */
	public SpriteAnimationLoader(FileHandleResolver resolver) {
		super(resolver);
	}
	
	/**
	 * Aynchronously loads the animation data animations
	 */
	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AnimationParameter parameter) {
		Gdx.app.log("SpriteAnimationLoader", "loading " + fileName);
		
		animationData = new SpriteAnimationData();
		
		// Retrieve texture
		animationData.texture = manager.get(stripExtension(fileName) + ".png", Texture.class);
		
		try {
			JsonReader reader = new JsonReader();
			JsonValue root = reader.parse(file);
			
			animationData.rows = root.getInt("rows");
			animationData.columns = root.getInt("columns");
			animationData.frameDuration = root.getFloat("frameDuration");
			
			JsonValue animations = root.get("animations");
			JsonIterator animationsIt = animations.iterator();
			boolean first = true;
			
			while (animationsIt.hasNext()) {
				JsonValue animationValue = animationsIt.next();
				
				String name = animationValue.getString("name");
				String frames = animationValue.getString("frames");
				Animation animation = new Animation(animationData.frameDuration,
													getAnimationFrames(animationData.texture, frames),
													getPlayMode(animationValue.getString("mode", "normal")));
				animationData.animations.put(name, animation);
				
				Gdx.app.log("SpriteAnimationLoader", "" + fileName + " loaded animation " + name);
				if (first) {
					animationData.defaultAnimation = animation;
					first = false;
				}
				
			}
		} catch (Exception e) {
			Gdx.app.log("SpriteAnimationLoader", "error loading file " + fileName + " " + e.getMessage());
		}
	}

	/**
	 * Retrieves the animation data as it is (without loading anything, this is strictly asynchronous)
	 */
	@Override
	public SpriteAnimationData loadSync(AssetManager manager, String fileName, FileHandle file, AnimationParameter parameter) {
		return animationData;
	}

	/**
	 * Gets animation data dependencies, this is, the spreadsheet texture to load 
	 */
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AnimationParameter parameter) {
		Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
		dependencies.add(new AssetDescriptor<Texture>(stripExtension(fileName) + ".png", Texture.class));
		
		return dependencies;
	}
	
	private String stripExtension (String fileName) {
       if (fileName == null) return null;
       int pos = fileName.lastIndexOf(".");
       if (pos == -1) return fileName;
       return fileName.substring(0, pos);
   }
	
	private PlayMode getPlayMode(String mode) {
		if (mode.equals("normal")) {
			return PlayMode.NORMAL; 
		}
		else if (mode.equals("loop")) {
			return PlayMode.LOOP;
		}
		else if (mode.equals("loop_pingpong")) {
			return PlayMode.LOOP_PINGPONG;
		}
		else if (mode.equals("loop_random")) {
			return PlayMode.LOOP_RANDOM;
		}
		else if (mode.equals("loop_reversed")) {
			return PlayMode.LOOP_REVERSED;
		}
		else if (mode.equals("reversed")) {
			return PlayMode.REVERSED;
		}
		else {
			return PlayMode.NORMAL;
		}
	}
	
	private Array<TextureRegion> getAnimationFrames(Texture texture, String frames) {
		Array<TextureRegion> regions = new Array<TextureRegion>();
		
		if (frames != null) {
			String[] framesArray = frames.replaceAll(" ", "").split(",");
			int numFrames = framesArray.length;
			int width = texture.getWidth() / animationData.columns;
			int height = texture.getHeight() / animationData.rows;
			
			for (int i = 0; i < numFrames; i++) {
				int frame = Integer.parseInt(framesArray[i]);
				int x = (frame % animationData.columns) * width;
				int y = (frame / animationData.columns) * height;
				
				regions.add(new TextureRegion(texture, x, y, width, height));
			}
		}
		
		return regions;
	}
}