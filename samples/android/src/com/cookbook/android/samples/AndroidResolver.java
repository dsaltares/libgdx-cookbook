package com.cookbook.android.samples;

import com.badlogic.gdx.Gdx;
import com.cookbook.platforms.PlatformResolver;

public class AndroidResolver implements PlatformResolver {

	@Override
	public void rateGame() {
		// TODO Auto-generated method stub
		System.out.println("Android");
		Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.facebook.katana&hl=es");
	}

}
