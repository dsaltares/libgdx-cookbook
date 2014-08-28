package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.cookbook.platforms.PlatformResolver;

public class IOSResolver implements PlatformResolver {

	@Override
	public void rateGame() {
		// TODO Auto-generated method stub
		System.out.println("iOS");
		/*if(Float.valueOf(System.getProperty("os.version")) >= 7.0f)*/
			Gdx.net.openURI("http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?id=id284882215&pageNumber=0&sortOrdering=2&type=Purple+Software&mt=8");
	}

}
