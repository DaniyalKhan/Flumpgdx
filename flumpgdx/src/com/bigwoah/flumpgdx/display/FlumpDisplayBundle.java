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
//			if (reference != null) {
//				getAttributes(this, 0);
//
//				//TODO check that this still works
//				pos[0].set(xPivot + xLoc, -xPivot + yLoc);
//				pos[1].set(xPivot + xLoc, -xPivot + yLoc);
//				pos[2].set(xPivot + xLoc, -xPivot + yLoc);
//				pos[3].set(xPivot + xLoc, -xPivot + yLoc);
//
////				bindToPivots(display);
////				display.x1 += xLoc;
////				display.y1 += yLoc;
////				display.x2 += xLoc;
////				display.y2 += yLoc;
////				display.x3 += xLoc;
////				display.y3 += yLoc;
////				display.x4 += xLoc;
////				display.y4 += yLoc;
//			}
		}
	}

	@Override
	void draw(SpriteBatch batch) {
		for (FlumpDisplay display: displayLayers) {
			display.draw(batch);
		}
	}
	
}
