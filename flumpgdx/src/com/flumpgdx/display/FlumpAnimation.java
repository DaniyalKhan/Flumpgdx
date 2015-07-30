package com.flumpgdx.display;

import com.badlogic.gdx.math.Matrix3;
import com.flumpgdx.library.FlumpLayer;

/**
 * A composition of layers used for animation.
 */
public class FlumpAnimation extends FlumpSprite {

	private int frameRate;

	public FlumpAnimation(FlumpLayer layer) {
		super(layer);
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * Set the transformation that will be applied for subsequent update() calls
	 * @param transformation
	 */
	@Override
	public void setTransform(Matrix3 transformation) {
		//use the user transform matrix to also encapsulate any recursive transforms on this layer group
		userTransform.set(transformation).mul(layerTransform);
		for (FlumpDisplay display: displayLayers) {
			display.setTransform(userTransform);
		}
	}

	/**
	 * Update the animation
	 * @param delta The time between update events, in seconds
	 */
	public void update(float delta) {
		flumpUpdate(delta * frameRate);
	}

	@Override
	protected void flumpUpdate(float deltaFrames) {
		for (FlumpDisplay display: displayLayers) {
			display.flumpUpdate(deltaFrames);
		}
	}

}
