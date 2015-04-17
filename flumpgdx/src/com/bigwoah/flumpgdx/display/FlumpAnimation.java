package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.bigwoah.flumpgdx.library.FlumpLayer;


public class FlumpAnimation extends FlumpDisplay {

	private static final Pool<Matrix3> MATRIX_POOL = new Pool<Matrix3>() {
		@Override
		protected Matrix3 newObject() {
			return new Matrix3();
		}
	};

	private final Array<FlumpDisplay> displayLayers;
	private final Matrix3 layerTransform; //recursive transformation for animation layers
	private Matrix3 userTransform;
	private int frameRate;

	public FlumpAnimation(FlumpLayer layer) {
		super(layer);
		displayLayers = new Array<FlumpDisplay>();
		layerTransform = new Matrix3();
	}

	public void applyTransformation(Matrix3 transformation) {
		this.userTransform = transformation;
	}

	public void setLayerTransform() {
		if (reference != null) {
			/* Right now we only translate recursively,
			 * this might need to be changed if animations recursively apply scales and/or rotations
			 */
			getAttributes(this, 0);
			layerTransform.setToTranslation(xPivot + xLoc, -yPivot + yLoc);
		}
	}

	public Matrix3 getTransformation() {
		return userTransform;
	}

	public void addDisplay(FlumpDisplay display) {
		displayLayers.add(display);
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * Update the animation
	 * @param delta The time between update events, in seconds
	 */
	public void update(float delta) {
		update(delta * frameRate, userTransform);
	}

	@Override
	protected void update(float deltaFrames, Matrix3 transform) {
		if (transform != null) {
			/* Get a temporary matrix so we can multiply by layer matrix in so we don't alter
			 * the given transformation matrix or the layer transformation matrix.
			 * The order of multiplication is important, we need to multiply by the layer transformation BEFORE
			 * we apply any user defined transformations (the final vertices of the displays are left multiplied
			 * by the final product matrix).
			 */
			Matrix3 tmp = MATRIX_POOL.obtain().set(transform).mul(layerTransform);
			for (FlumpDisplay display: displayLayers) {
				display.update(deltaFrames, tmp);
			}
			MATRIX_POOL.free(tmp.idt());
		} else {
			//Only create a temporary matrix if we need to apply any layer changes
			for (FlumpDisplay display: displayLayers) {
				display.update(deltaFrames, transform);
			}
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

}
