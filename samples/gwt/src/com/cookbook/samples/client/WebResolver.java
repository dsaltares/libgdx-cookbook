package com.cookbook.samples.client;

import com.badlogic.gdx.Gdx;
import com.cookbook.platforms.PlatformResolver;

public class WebResolver implements PlatformResolver {

	@Override
	public void rateGame() {
		// TODO Auto-generated method stub
		System.out.println("Web");
		Gdx.net.openURI("http://www.facebook.com");
	}

}
