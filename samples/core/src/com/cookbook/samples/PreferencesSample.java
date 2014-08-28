package com.cookbook.samples;

import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.math.MathUtils;

public class PreferencesSample extends GdxSample {
	
	private Scanner scanner = new Scanner(System.in);
	
	@Override
	public void create() {
		Preferences preferences = Gdx.app.getPreferences(PreferencesSample.class.getName());
		
		System.out.println("===================");
		System.out.println("Current preferences");
		System.out.println("===================");
		System.out.println("Player profile: " + preferences.getString("playerName", "default"));
		System.out.println("Difficulty: " + preferences.getInteger("difficulty", 5) + "/10");
		System.out.println("Music volume: " + preferences.getFloat("musicVolume", 100) + "/100");
		System.out.println("Effects volume: " + preferences.getFloat("effectsVolume", 100) + "/100");
		System.out.println("Show tips: " + preferences.getBoolean("showTips", true));
		System.out.println();
		
		
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			System.out.println("==================");
			System.out.println("Update preferences");
			System.out.println("==================");
			
			preferences.putString("playerName", getOption("Player profile"));
			preferences.putInteger("difficulty", MathUtils.clamp(Integer.parseInt(getOption("Difficulty")), 0, 10));
			preferences.putFloat("musicVolume", MathUtils.clamp(Float.parseFloat(getOption("Music volume")), 0.0f, 100.0f));
			preferences.putFloat("effectsVolume", MathUtils.clamp(Float.parseFloat(getOption("Effects volume")), 0.0f, 100.0f));
			preferences.putBoolean("showTips", Boolean.parseBoolean(getOption("Show tips (true/false)")));
			preferences.flush();
		}
		
		Gdx.app.exit();
	}
	
	private String getOption(String prompt) {
		System.out.print(prompt + ": ");
		return scanner.nextLine();
	}
}
