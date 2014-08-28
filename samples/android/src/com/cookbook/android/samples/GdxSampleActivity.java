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

package com.cookbook.android.samples;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cookbook.samples.GdxSample;
import com.cookbook.samples.GdxSamples;

public class GdxSampleActivity extends AndroidApplication {

	public void onCreate (Bundle bundle) {
		super.onCreate(bundle);

		Bundle extras = getIntent().getExtras();
		String testName = (String)extras.get("sample");

		GdxSample test = GdxSamples.newSample(testName);
		test.setPlatformResolver(new AndroidResolver());
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(test, config);
	}
}
