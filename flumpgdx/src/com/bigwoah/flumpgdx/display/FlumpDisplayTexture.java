package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bigwoah.flumpgdx.library.FlumpTextureRegion;

public class FlumpDisplayTexture extends TextureRegion {

	final float xOrigin;
	final float yOrigin;

	public FlumpDisplayTexture(Texture texture, FlumpTextureRegion region) {
		super(texture, region.rect[0], region.rect[1], region.rect[2], region.rect[3]);
		this.xOrigin = region.origin[0];
		this.yOrigin = region.origin[1];
	}
	
}
