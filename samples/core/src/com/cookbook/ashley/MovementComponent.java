package com.cookbook.ashley;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component {
	public final Vector2 velocity = new Vector2();
}