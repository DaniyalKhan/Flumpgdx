package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.bigwoah.flumpgdx.library.FlumpLayer;


public class FlumpAnimation extends FlumpDisplayBundle {

	private int frameRate;
	private float frame;

	//User defined scale/rotation, negative values for scale will flip animation
	protected Matrix3 transformation;
	protected boolean dirtyTransformation = false;
	//User defined translation, do this separately since this is usually applied
	protected Vector2 translation;

	public FlumpAnimation(FlumpLayer layer) {
		super(layer);
		this.frame = 0;
		transformation = new Matrix3();
		translation = new Vector2();
	}

	public void setframeRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public void resetTransformation() {
		transformation.idt();
		dirtyTransformation = false;
	}

	public void scaleX(float scaleX) {
		transformation.scale(scaleX, 1);
		dirtyTransformation = true;
	}

	public void scaleY(float scaleY) {
		transformation.scale(1, scaleY);
		dirtyTransformation = true;
	}

	public void scale(float scaleX, float scaleY) {
		transformation.scale(scaleX, scaleY);
		dirtyTransformation = true;
	}

	public void scale(float scale) {
		transformation.scale(scale, scale);
		dirtyTransformation = true;
	}

	public void rotate(float radians) {
		transformation.rotateRad(radians);
		dirtyTransformation = true;
	}

	public void translate(float x, float y) {
		translation.add(x, y);
	}

	public void translateX(float x) {
		translation.x += x;
	}

	public void translateY(float y) {
		translation.y += y;
	}

	public void setPosition(float x, float y) {
		translation.set(x, y);
	}

	public void update(float delta) {
		frame += delta * frameRate;
		if (frame > maxFrameCount()) frame = 0;
		super.update((int)frame);
		if (dirtyTransformation) {
			applyTransformation(transformation);
			dirtyTransformation = false;
		}
		applyTranslation(translation);
	}

	public void draw(SpriteBatch batch) {
		super.draw(batch);
	}

}
