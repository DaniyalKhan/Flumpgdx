package com.flumpgdx.library;

import com.flumpgdx.display.FlumpDisplay;
import com.flumpgdx.display.FlumpSprite;

/**
 * Created by Dani on 15-07-30.
 */
public class SpriteMaker {

    public static FlumpSprite make(FlumpLibraryFile file, String name) {
        return make(file, name, null);
    }

    private static FlumpSprite make(FlumpLibraryFile file, String name, FlumpLayer groupLayer) {
        if (!file.animations.containsKey(name)) {
            throw new IllegalArgumentException("FlumpLibraryFile does not contain the animation: " + name);
        }
        //newly created animation
        FlumpSprite image = new FlumpSprite(groupLayer);
        FlumpMovie movieMold = file.animations.get(name);
        for (FlumpLayer layer: movieMold.layers) {
            String firstKeyFrameName = layer.keyframes[0].ref;
            if (firstKeyFrameName != null && file.animations.containsKey(firstKeyFrameName)) {
                //the layer is a reference to another image group, recursively create it here
                FlumpSprite newGroup = make(file, firstKeyFrameName, layer);
                newGroup.setLayerTransform();
                image.addDisplay(newGroup);
            } else if (firstKeyFrameName != null) {
                //regular layer, add it to the animation
                image.addDisplay(new FlumpDisplay(layer));
            }
        }
        return image;
    }

}
