package com.cookbook.assetmanager;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class Asset implements Json.Serializable  {
	public Class<?> type;
	public String path;
	public AssetLoaderParameters parameters;
	
	@Override
	public void write(Json json) {
		json.writeValue("assetType", type.getName());
		json.writeValue("path", path);
		json.writeValue("parameters", parameters);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		try {
			type = ClassReflection.forName(jsonData.get("type").asString());
		} catch (Exception e) {
			type = null;
		}
		
		path = jsonData.get("path").asString();
		
		JsonValue parametersValue = jsonData.get("parameters");
		parameters = parametersValue != null ? json.fromJson(AssetLoaderParameters.class, parametersValue.toString()) : null;
	}
}