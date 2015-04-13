package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.utils.ObjectMap;

public class TextureCache {

	private static TextureCache instance;
	private ObjectMap<String, FlumpDisplayTexture> cache;
	
	private TextureCache() {
		cache = new ObjectMap<String, FlumpDisplayTexture>();
	}
	
	public static TextureCache obtain() {
		if (instance == null) instance = new TextureCache();
		return instance;
	}
	
	public FlumpDisplayTexture get(String filename) {
		return cache.get(filename);
	}
	
	public void cache(String filename, FlumpDisplayTexture texture) {
		cache.put(filename, texture);
	}

}
