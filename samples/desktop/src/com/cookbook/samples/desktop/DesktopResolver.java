package com.cookbook.samples.desktop;

import com.badlogic.gdx.Gdx;
import com.cookbook.platforms.PlatformResolver;

public class DesktopResolver implements PlatformResolver {

	@Override
	public void rateGame() {
		// TODO Auto-generated method stub
		System.out.println("Desktop");
		Gdx.net.openURI("https://www.facebook.com");
	}

}
