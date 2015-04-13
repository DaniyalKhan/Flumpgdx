package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bigwoah.flumpgdx.library.FlumpLayer;

public abstract class FlumpDisplayBundle extends FlumpDisplay {

	private final Array<FlumpDisplay> displayLayers;
	
	public FlumpDisplayBundle(FlumpLayer layer) {
		super(layer);
		displayLayers = new Array<FlumpDisplay>();
	}
	
	@Override
	protected void applyTransformation(Matrix3 transformation) {
		for (FlumpDisplay display: displayLayers) {
			display.applyTransformation(transformation);
		}
	}
	
	@Override
	protected void applyTranslation(Vector2 translation) {
		for (FlumpDisplay display: displayLayers) {
			display.applyTranslation(translation);
		}
	}
	
	@Override
	public int maxFrameCount() {
		if (displayLayers == null || displayLayers.size == 0) return 0;
		int maxFrameCount = displayLayers.get(0).maxFrameCount();
		for (FlumpDisplay display: displayLayers) {
			if (display.maxFrameCount() > maxFrameCount)
				maxFrameCount = display.maxFrameCount();
		}
		return maxFrameCount;
	}

	public void addDisplay(FlumpDisplay display) {
		displayLayers.add(display);
	}

	@Override
	void update(int frame) {
		for (FlumpDisplay display: displayLayers) {
			display.update(frame);
			if (reference != null) { //Animation layer
				getAttributes(this, 0);
				/* TODO if animation recursively applies transformations, need to apply them here
				(right now we only handle translation) */
				display.pos[0].add(xPivot + xLoc, -yPivot + yLoc);
				display.pos[1].add(xPivot + xLoc, -yPivot + yLoc);
				display.pos[2].add(xPivot + xLoc, -yPivot + yLoc);
				display.pos[3].add(xPivot + xLoc, -yPivot + yLoc);
			}
		}
	}

	@Override
	void draw(SpriteBatch batch) {
		for (FlumpDisplay display: displayLayers) {
			display.draw(batch);
		}
	}
	
}
