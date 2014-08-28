package com.cookbook.samples.inputmapping;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class InputProfile implements InputProcessor {
	private ArrayMap<String, InputContext> contexts;
	private InputContext context;
	
	public InputProfile(FileHandle handle) {
		contexts = new ArrayMap<String, InputContext>();
		context = null;
		
		try {
			Gdx.app.debug("InputProfile", "Reading file " + handle.path());
			InputStream inputStream = handle.read();
			InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
			XmlReader reader = new XmlReader();
			
			Element root = reader.parse(streamReader);
			int numContexts = root.getChildCount();
			
			for (int i = 0; i < numContexts; ++i) {
				Element contextElement = root.getChild(i);
				InputContext context = new InputContext();
				context.load(contextElement);
				contexts.put(context.getName(), context);
			}
		}
		catch (Exception e) {
			Gdx.app.error("InputProfile", "error loading file " + handle.path() + " " + e.getMessage());
		}
	}
	
	public void setContext(String contextName) {
		context = contexts.get(contextName);
	}
	
	public InputContext getContext() {
		return context;
	}
	
	public InputContext getContextByName(String name) {
		return contexts.get(name);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (context != null) {
			return context.keyDown(keycode);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (context != null) {
			return context.keyUp(keycode);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (context != null) {
			return context.keyTyped(character);
		}
		
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (context != null) {
			return context.touchDown(screenX, screenY, pointer, button);
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (context != null) {
			return context.touchUp(screenX, screenY, pointer, button);
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (context != null) {
			return context.touchDragged(screenX, screenY, pointer);
		}
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (context != null) {
			return context.mouseMoved(screenX, screenY);
		}
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (context != null) {
			return context.scrolled(amount);
		}
		
		return false;
	}
}
