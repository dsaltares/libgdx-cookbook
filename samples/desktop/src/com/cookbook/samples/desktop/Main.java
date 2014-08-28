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

package com.cookbook.samples.desktop;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cookbook.samples.GdxSample;
import com.cookbook.samples.GdxSamples;
import com.cookbook.samples.desktop.SampleList.SampleLauncher;

public class Main extends JFrame implements SampleLauncher {
	public Main () throws HeadlessException {
		super("Libgdx Cookbook Samples");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(new SampleList(this));
		pack();
		setSize(Math.max(getWidth(), 600), 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main (String[] argv) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Main();
	}

	@Override
	public boolean launchSample(String testName) {
		GdxSample sample = GdxSamples.newSample(testName);
		
		if (sample != null) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = 1280;
			config.height = 720;
			config.title = testName;
			config.forceExit = false;
			sample.setPlatformResolver(new DesktopResolver());
			
			new LwjglApplication(sample, config);
			
			dispose();
			
			return true;
		}
		
		return false;
	}
}
