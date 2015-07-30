package com.flumpgdx.display;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.Array;
import com.flumpgdx.library.FlumpLayer;

/**
 * A composition of layers to create a static image
 */
public class FlumpSprite extends FlumpDisplay {

    protected final Array<FlumpDisplay> displayLayers;
    protected final Matrix3 layerTransform; //recursive transformation for animation layers

    public FlumpSprite(FlumpLayer layer) {
        reference = layer;
        displayLayers = new Array<FlumpDisplay>();
        layerTransform = new Matrix3();
    }

    @Override
    public void setColor(float color) {
        for (FlumpDisplay display: displayLayers) {
            display.setColor(color);
        }
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

    /**
     * Set the transformation that will be applied
     * @param transformation
     */
    @Override
    public void transform(Matrix3 transformation) {
        //use the user transform matrix to also encapsulate any recursive transforms on this layer group
        userTransform.set(transformation).mul(layerTransform);
        for (FlumpDisplay display: displayLayers) {
            display.transform(userTransform);
        }
    }

    public void addDisplay(FlumpDisplay display) {
        displayLayers.add(display);
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
