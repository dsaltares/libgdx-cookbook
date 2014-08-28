package com.cookbook.samples.inputmapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlReader.Element;


public class InputContext extends InputAdapter {
	private String name;
	
	private ArrayMap<String, Integer> keyStates;
	private ArrayMap<Integer, String> keyActions;
	
	private ObjectSet<InputActionListener> listeners;
	
	public InputContext() {
		keyStates = new ArrayMap<String, Integer>();
		keyActions = new ArrayMap<Integer, String>();
		
		listeners = new ObjectSet<InputActionListener>();
	}
	
	public void load(Element contextElement) {
		keyActions.clear();
		keyStates.clear();
		
		try {
			name = contextElement.getAttribute("name");
			
			Element statesElement = contextElement.getChildByName("states");
			int numStates = statesElement != null ? statesElement.getChildCount() : 0;
			
			for (int i = 0; i < numStates; ++i) {
				Element stateElement = statesElement.getChild(i);
				String stateName = stateElement.getAttribute("name");

				Element keyElement = stateElement.getChildByName("key");
				
				if (keyElement != null) {
					int keycode = Keys.valueOf(keyElement.getAttribute("code"));
					keyStates.put(stateName, keycode);
				}
			}
			
			Element actionsElement = contextElement.getChildByName("actions");
			int numActions = actionsElement != null ? actionsElement.getChildCount() : 0;
			
			for (int i = 0; i < numActions; ++i) {
				Element actionElement = actionsElement.getChild(i);
				String actionName = actionElement.getAttribute("name");
				
				Element keyElement = actionElement.getChildByName("key");
				
				if (keyElement != null) {
					int keycode = Keys.valueOf(keyElement.getAttribute("code"));
					keyActions.put(keycode, actionName);
				}
			}
		}
		catch (Exception e) {
			Gdx.app.error("InputContext", "Error loading context element");
		}
	}
	
	public void addListener(InputActionListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(InputActionListener listener) {
		listeners.remove(listener);	
	}
	
	public boolean getState(String state) {
		Integer keycode = keyStates.get(state);
		
		if (keycode != null) {
			return Gdx.input.isKeyPressed(keycode);
		}
		
		return false;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean keyDown(int keycode) {
		boolean processed = false;
		
		String action = keyActions.get(keycode);
		
		if (action != null) {
			for (InputActionListener listener : listeners) {
				processed = listener.OnAction(action);
				
				if (processed) {
					break;
				}
			}
		}
		
		return processed;
	}
}