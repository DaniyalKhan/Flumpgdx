package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.Array;
import com.bigwoah.flumpgdx.library.FlumpLayer;


public class FlumpAnimation extends FlumpDisplay {

	private final Array<FlumpDisplay> displayLayers;
	private final Matrix3 layerTransform; //used to temporary calculate any user defined transforms
	private float tx, ty; //animation might recursively apply translations if animations hold other animations
	private int frameRate;
	private float frame;

	public FlumpAnimation(FlumpLayer layer) {
		super(layer);
		this.frame = 0;
		displayLayers = new Array<FlumpDisplay>();
		layerTransform = new Matrix3();
	}

	public void addDisplay(FlumpDisplay display) {
		displayLayers.add(display);
	}

	public void setframeRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public void setLayerTransform() {
		/* TODO if animation recursively applies transformations, get them here
		(right now we only handle translation) */
		getAttributes(this, 0);
		tx = xPivot + xLoc;
		ty = -yPivot + yLoc;
	}

	/**
	 * Update the animation
	 * @param delta The time between update events, in seconds
	 */
	public void update(float delta) {
		frame += delta * frameRate;
		if (frame > maxFrameCount()) frame = 0;
		update((int)frame, null);
	}

	/**
	 * Update the animation, optionally providing a transformation matrix so scale/rotate/translate the animation
	 * @param delta The time between update events, in seconds
	 * @param transform An optional transformation matrix for user defined transformations
	 */
	public void update(float delta, Matrix3 transform) {
		frame += delta * frameRate;
		if (frame > maxFrameCount()) frame = 0;
		update((int)frame, transform);
	}

	@Override
	protected void update(int frame, Matrix3 transform) {
		/* Add the user defined transformation to recursive translation on this animation, if any.
		 * Do not modify the original transform instance given, since it may affect other layers, if some are
		 * animations and some are regular layers!
		 */
		/* NOTE: Since the display layer class currently multiples the given transform matrix at the end,
 		 * its ok to simply combine the layer and user transform matrix when the layer matrix only translates,
 		 * and do everything in one single update call.
 		 * When animations recursively scale and rotate, we might have to do 2 passes.
 		 * The first calculates all the flump coordinates, then a second pass to apply any user transforms
		 */
		if (transform != null) {
			layerTransform.set(transform).translate(tx, ty);
		} else {
			layerTransform.setToTranslation(tx, ty);
		}
		for (FlumpDisplay display: displayLayers) {
			display.update(frame, layerTransform);
		}
	}

	/**
	 * Render the animation
	 * @param batch The SpriteBatch instance used to do the rendering
	 */
	@Override
	public void draw(SpriteBatch batch) {
		for (FlumpDisplay display: displayLayers) {
			display.draw(batch);
		}
	}

	/**
	 * Calculate the maximum number of frames any of the display layers last
	 * @return The max frame number
	 */
	@Override
	public int maxFrameCount() {
		if (displayLayers == null || displayLayers.size == 0) return 0;
		if (numFrames < 0) {
			for (FlumpDisplay display : displayLayers) {
				if (display.maxFrameCount() > numFrames)
					numFrames = display.maxFrameCount();
			}
		}
		return numFrames;
	}

}
