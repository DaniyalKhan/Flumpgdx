package com.flumpgdx.library;

import com.flumpgdx.display.FlumpAnimation;
import com.flumpgdx.display.FlumpDisplay;

public class MovieMaker {
	
	public static FlumpAnimation make(FlumpLibraryFile file, String name) {
		return make(file, name, null);
	}
	
	private static FlumpAnimation make(FlumpLibraryFile file, String name, FlumpLayer animLayer) {
		if (!file.animations.containsKey(name)) {
			throw new IllegalArgumentException("FlumpLibraryFile does not contain the animation: " + name);
		}
		//newly created animation
		FlumpAnimation animation = new FlumpAnimation(animLayer);
		FlumpMovie movieMold = file.animations.get(name);	
		for (FlumpLayer layer: movieMold.layers) {
			String firstKeyFrameName = layer.keyframes[0].ref;
			if (firstKeyFrameName != null && file.animations.containsKey(firstKeyFrameName)) {
				//the layer is a reference to another animation, recursively create it here
				FlumpAnimation newAnim = make(file, firstKeyFrameName, layer);
				newAnim.setLayerTransform();
				animation.addDisplay(newAnim);
			} else if (firstKeyFrameName != null) {
				//regular layer, add it to the animation
				animation.addDisplay(new FlumpDisplay(layer));
			}
		}
		if (movieMold.frameRate <= 0) animation.setFrameRate(file.frameRate);
		else animation.setFrameRate(movieMold.frameRate);
		return animation;
	}
	

}
