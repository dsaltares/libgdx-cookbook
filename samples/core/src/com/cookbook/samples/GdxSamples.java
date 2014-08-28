/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.cookbook.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/** List of GdxSample classes. To be used by the test launchers. If you write your own test, add it in here!
 * 
 * @author badlogicgames@gmail.com */
public class GdxSamples {
	public static final List<Class<? extends GdxSample>> tests = new ArrayList<Class<? extends GdxSample>>(Arrays.asList(
			AshleySample.class,
			FrustumCullingSample.class,
			SpineSample.class,
			ArtificialIntelligenceSample.class,
			TweenEngineSample.class,
			Box2DLightsSample.class,
			LocalizationSample.class,
			PlatformSpecificSample.class,
			Box2DFixedTimeStepSample.class,
			Box2DDeferredRaycasterSample.class,
			Box2DMapPopulatorSample.class,
			Box2DQuerySample.class,
			Box2DCollisionFilteringSample.class,
			Box2DCollisionReactionSample.class,
			Box2DBikeSimulatorSample.class,
			Box2DJointsSample.class,
			Box2DComplexShapesSample.class,
			Box2DSimpleSample.class,
			ActorSample.class,
			WidgetsSample.class,
			MainMenuSample.class,
			SkinCustomizationSample.class,
			CustomWidgetSample.class,
			GroupingAssetsSample.class,
			CustomLoaderSample.class,
			ProgressBarSample.class,
			AssetManagerSample.class,
			SpriteBatchSample.class,
			TextureAtlasSample.class,
			OrthographicCameraSample.class,
			AnimatedSpriteSample.class,
			ParticleEffectsSample.class,
			FrameBufferParticleEffectSample.class,
			ShapeRendererSample.class,
			FrameBufferSample.class,
			PooledEffectsSample.class,
			ShaderSample.class,
			ShaderUniformSample.class,
			BlurSample.class,
			SpriteSample.class,
			InputPollingSample.class,
			InputListeningSample.class,
			InputMultiplexerSample.class,
			GestureDetectorSample.class,
			GamepadSample.class,
			SoundEffectSample.class,
			MusicSample.class,
			InputMappingSample.class,
			ViewportSample.class,
			CarEngineSample.class,
			SpatialAudioSample.class,
			FileHandlingSample.class,
			PreferencesSample.class,
			XmlParsingSample.class,
			JsonParsingSample.class,
			BitmapFontSample.class,
			HieroFontEffectsSample.class,
			DistanceFieldFontSample.class,
			DistanceFieldEffectsSample.class,
			TiledMapSample.class,
			TiledMapObjectsSample.class
		));

	public static List<String> getNames () {
		List<String> names = new ArrayList<String>(tests.size());
		for (Class clazz : tests)
			names.add(clazz.getSimpleName());
		Collections.sort(names);
		return names;
	}

	private static Class<? extends GdxSample> forName (String name) {
		for (Class clazz : tests)
			if (clazz.getSimpleName().equals(name)) return clazz;
		return null;
	}

	public static GdxSample newSample (String testName) {
		try {
			return forName(testName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
