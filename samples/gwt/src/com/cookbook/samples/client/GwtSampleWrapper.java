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

package com.cookbook.samples.client;

import com.cookbook.samples.client.WebResolver;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cookbook.samples.*;

public class GwtSampleWrapper extends GdxSample {
	Stage ui;
	Table container;
	Skin skin;
	BitmapFont font;
	GdxSample test;
	boolean dispose = false;

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.log("GdxSampleGwt", "Setting up for " +tests.length+ " tests.");
		
		ui = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"), false);
		container = new Table();
		ui.addActor(container);
		container.debug();
		Table table = new Table();
		ScrollPane scroll = new ScrollPane(table);
		container.add(scroll).expand().fill();
		table.pad(10).defaults().expandX().space(4);
		for (final Instancer instancer : tests) {
			table.row();
			TextButton button = new TextButton(instancer.instance().getClass().getName(), skin);
			button.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					((InputWrapper)Gdx.input).multiplexer.removeProcessor(ui);
					test = instancer.instance();
					Gdx.app.log("GdxSampleGwt", "Clicked on " + test.getClass().getName());
					test.create();
					test.setPlatformResolver(new WebResolver());
					test.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				}
			});
			table.add(button).expandX().fillX();
		}
		container.row();
		container.add(new Label("Click on a test to start it, press ESC to close it.", new LabelStyle(font, Color.WHITE))).pad(5,
			5, 5, 5);

		Gdx.input = new InputWrapper(Gdx.input) {
			@Override
			public boolean keyUp (int keycode) {
				if (keycode == Keys.ESCAPE) {
					if (test != null) {
						Gdx.app.log("GdxSampleGwt", "Exiting current test.");
						dispose = true;
					}
				}
				return false;
			}

			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				if(screenX < Gdx.graphics.getWidth() / 10.0 &&
					screenY < Gdx.graphics.getHeight() / 10.0) {
					if(test != null) {
						dispose = true;
					}
				}
				return false;
			}
		};
		((InputWrapper)Gdx.input).multiplexer.addProcessor(ui);
		
		Gdx.app.log("GdxSampleGwt", "Test picker UI setup complete.");
	}
	
	public void render () {
		if (test == null) {
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			ui.act(Gdx.graphics.getDeltaTime());
			ui.draw();
		} else {
			if (dispose) {
				test.pause();
				test.dispose();
				test = null;
				Gdx.graphics.setVSync(true);
				InputWrapper wrapper = ((InputWrapper)Gdx.input);
				wrapper.multiplexer.addProcessor(ui);
				wrapper.multiplexer.removeProcessor(wrapper.lastProcessor);
				wrapper.lastProcessor = null;
				dispose = false;
			} else {
				test.render();
			}
		}
	}

	public void resize (int width, int height) {
		ui.getViewport().update(width, height);
		container.setSize(width, height);
		if (test != null) {
			test.resize(width, height);
		}
	}

	class InputWrapper extends InputAdapter implements Input {
		Input input;
		InputProcessor lastProcessor;
		InputMultiplexer multiplexer;

		public InputWrapper (Input input) {
			this.input = input;
			this.multiplexer = new InputMultiplexer();
			this.multiplexer.addProcessor(this);
			input.setInputProcessor(multiplexer);
		}

		@Override
		public float getAccelerometerX () {
			return input.getAccelerometerX();
		}

		@Override
		public float getAccelerometerY () {
			return input.getAccelerometerY();
		}

		@Override
		public float getAccelerometerZ () {
			return input.getAccelerometerZ();
		}

		@Override
		public int getX () {
			return input.getX();
		}

		@Override
		public int getX (int pointer) {
			return input.getX(pointer);
		}

		@Override
		public int getDeltaX () {
			return input.getDeltaX();
		}

		@Override
		public int getDeltaX (int pointer) {
			return input.getDeltaX(pointer);
		}

		@Override
		public int getY () {
			return input.getY();
		}

		@Override
		public int getY (int pointer) {
			return input.getY(pointer);
		}

		@Override
		public int getDeltaY () {
			return input.getDeltaY();
		}

		@Override
		public int getDeltaY (int pointer) {
			return input.getDeltaY(pointer);
		}

		@Override
		public boolean isTouched () {
			return input.isTouched();
		}

		@Override
		public boolean justTouched () {
			return input.justTouched();
		}

		@Override
		public boolean isTouched (int pointer) {
			return input.isTouched(pointer);
		}

		@Override
		public boolean isButtonPressed (int button) {
			return input.isButtonPressed(button);
		}

		@Override
		public boolean isKeyPressed (int key) {
			return input.isKeyPressed(key);
		}

		@Override
		public void setOnscreenKeyboardVisible (boolean visible) {
			input.setOnscreenKeyboardVisible(visible);
		}

		@Override
		public void vibrate (int milliseconds) {
			input.vibrate(milliseconds);
		}

		@Override
		public void vibrate (long[] pattern, int repeat) {
			input.vibrate(pattern, repeat);
		}

		@Override
		public void cancelVibrate () {
			input.cancelVibrate();
		}

		@Override
		public float getAzimuth () {
			return input.getAzimuth();
		}

		@Override
		public float getPitch () {
			return input.getPitch();
		}

		@Override
		public float getRoll () {
			return input.getRoll();
		}

		@Override
		public void getRotationMatrix (float[] matrix) {
			input.getRotationMatrix(matrix);
		}

		@Override
		public long getCurrentEventTime () {
			return input.getCurrentEventTime();
		}

		@Override
		public void setCatchBackKey (boolean catchBack) {
			input.setCatchBackKey(catchBack);
		}

		@Override
		public void setCatchMenuKey (boolean catchMenu) {
			input.setCatchMenuKey(catchMenu);
		}

		@Override
		public void setInputProcessor (InputProcessor processor) {
			multiplexer.removeProcessor(lastProcessor);
			multiplexer.addProcessor(processor);
			lastProcessor = processor;
		}

		@Override
		public InputProcessor getInputProcessor () {
			return input.getInputProcessor();
		}

		@Override
		public boolean isPeripheralAvailable (Peripheral peripheral) {
			return input.isPeripheralAvailable(peripheral);
		}

		@Override
		public int getRotation () {
			return input.getRotation();
		}

		@Override
		public Orientation getNativeOrientation () {
			return input.getNativeOrientation();
		}

		@Override
		public void setCursorCatched (boolean catched) {
			input.setCursorCatched(catched);
		}

		@Override
		public boolean isCursorCatched () {
			return input.isCursorCatched();
		}

		@Override
		public void setCursorPosition (int x, int y) {
			setCursorPosition(x, y);
		}

		@Override
		public boolean isKeyJustPressed(int key) {
			return input.isKeyJustPressed(key);
		}

		@Override
		public boolean isCatchBackKey() {
			return input.isCatchBackKey();
		}

		@Override
		public void getTextInput(TextInputListener listener, String title,
				String text, String hint) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isCatchMenuKey() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	interface Instancer {
		public GdxSample instance ();
	}

	Instancer[] tests = {
		new Instancer() {
			public GdxSample instance () {
				return new SpriteBatchSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new TextureAtlasSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new OrthographicCameraSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new AnimatedSpriteSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ParticleEffectsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new FrameBufferParticleEffectSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ShapeRendererSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new FrameBufferSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new PooledEffectsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ShaderSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ShaderUniformSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new BlurSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new SpriteSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new InputPollingSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new InputListeningSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new InputMultiplexerSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new GestureDetectorSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new GamepadSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new SoundEffectSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new MusicSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new InputMappingSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ViewportSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new CarEngineSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new SpatialAudioSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new FileHandlingSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new XmlParsingSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new JsonParsingSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new BitmapFontSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new HieroFontEffectsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new DistanceFieldFontSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new DistanceFieldEffectsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new TiledMapSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new TiledMapObjectsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new PlatformSpecificSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DFixedTimeStepSample();
			}	
		},
		
		/*new Instancer() {
			public GdxSample instance () {
				return new Box2DDeferredRaycasterSample();
			}	
		},*/
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DMapPopulatorSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DQuerySample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DCollisionFilteringSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DCollisionReactionSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DBikeSimulatorSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DJointsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DComplexShapesSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DSimpleSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ActorSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new WidgetsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new MainMenuSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new SkinCustomizationSample();
			}	
		},
		
		/*new Instancer() {
			public GdxSample instance () {
				return new CustomWidgetSample();
			}	
		},*/
		
		/*new Instancer() {
			public GdxSample instance () {
				return new GroupingAssetsSample();
			}	
		}, // "forName" Not compatible with gwt */
		
		new Instancer() {
			public GdxSample instance () {
				return new CustomLoaderSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ProgressBarSample();
			}	
		},
		
		/*new Instancer() {
			public GdxSample instance () {
				return new AssetManagerSample();
			}	
		},*/
		
		new Instancer() {
			public GdxSample instance () {
				return new LocalizationSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new ArtificialIntelligenceSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new Box2DLightsSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new PlatformSpecificSample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new AshleySample();
			}	
		},
		
		new Instancer() {
			public GdxSample instance () {
				return new FrustumCullingSample();
			}	
		}
	};
}
