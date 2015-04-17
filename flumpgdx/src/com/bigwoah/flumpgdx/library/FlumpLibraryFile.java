package com.bigwoah.flumpgdx.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;
import com.bigwoah.flumpgdx.display.FlumpDisplayTexture;
import com.bigwoah.flumpgdx.utils.TextureCache;


public class FlumpLibraryFile {

	String pathToJson;
	int frameRate;
	String md5;
	FlumpTextureGroup[] textureGroups;
	FlumpMovie[] movies;
	
	ObjectMap<String, FlumpMovie> animations;
	
	public static FlumpLibraryFile deserialize(String pathToJson) {
		FlumpLibraryFile f = new Json(OutputType.json).fromJson(FlumpLibraryFile.class,  Gdx.files.internal(pathToJson));
		f.pathToJson = pathToJson.substring(0, pathToJson.lastIndexOf('/') + 1);
		return f.init();
	}
	
	public String getDir() {
		return pathToJson;
	}
	
	private FlumpLibraryFile init() {
		//list of all animations in this file
		animations = new ObjectMap<String, FlumpMovie>();
		//create a new TextureRegion for each FlumpRegion in the file 
		for (FlumpTextureGroup group: textureGroups) {
			for (FlumpAtlas atlas: group.atlases) {
				Texture texture = new Texture(Gdx.files.internal(pathToJson + atlas.file));
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				for (FlumpTextureRegion region: atlas.textures) {
					TextureCache.obtain().cache(region.symbol, new FlumpDisplayTexture(texture, region));
					region.rect = null; //avoid loitering
				}
				atlas.textures = null; //avoid loitering
			}
			group.atlases = null; //avoid loitering
		}
		textureGroups = null; //avoid loitering
		//create a new animation for each animation in the file
		for (FlumpMovie movie: movies) animations.put(movie.id, movie);
		return this;
	}
	
}
