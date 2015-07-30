package com.flumpgdx.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.flumpgdx.library.FlumpKeyFrame;
import com.flumpgdx.library.FlumpLayer;
import com.flumpgdx.utils.TextureCache;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

/**
 * Represents an individual image layer
 */
public class FlumpDisplay {

	private static final int NUM_VERTICES = 20;
	private static final float DEFAULT_VERTEX_COLOR = Color.WHITE.toFloatBits();
	protected static float xPivot, yPivot, xSkew, ySkew, xScale, yScale, xLoc, yLoc;

	private int keyFrame; //current keyFrame
	private float frameNumber; //the frames elapsed in the current keyframe
	private float[] vertices;
	private Vector2[] pos = new Vector2[4];
	protected FlumpLayer reference;
	private boolean dirty;

	//user defined transformations
	protected Matrix3 userTransform;

	protected FlumpDisplay() {
		userTransform = new Matrix3();
	}

	public FlumpDisplay(FlumpLayer layer) {
		this();
		this.reference = layer;
		this.keyFrame = 0;
		this.frameNumber = 0;
		for (int i = 0; i < pos.length; i++) {
			pos[i] = new Vector2();
		}
		vertices = new float[NUM_VERTICES];
		FlumpDisplayTexture t = getDisplayTexture();
		float u = t.getU();
		float u2 = t.getU2();
		float v = t.getV();
		float v2 = t.getV2();
		vertices[U1] = u;
		vertices[V1] = v2;
		vertices[U2] = u;
		vertices[V2] = v;
		vertices[U3] = u2;
		vertices[V3] = v;
		vertices[U4] = u2;
		vertices[V4] = v2;
		setColor(DEFAULT_VERTEX_COLOR);
		flumpUpdate(0);
	}

	protected void setTransform(Matrix3 transformation) {
		userTransform.set(transformation);
	}

	protected void transform(Matrix3 transformation) {
		for (Vector2 p : pos) {
			p.mul(transformation);
		}
		dirty = true;
	}

	protected void setColor(float color) {
		vertices[C1] = color;
		vertices[C2] = color;
		vertices[C3] = color;
		vertices[C4] = color;
	}
	
	protected FlumpDisplayTexture getDisplayTexture() {
		return TextureCache.obtain().get(reference.keyframes[keyFrame].ref);
	}
	
	protected static void getAttributes(FlumpDisplay source, float interpolation) {
		FlumpKeyFrame[] keyframes = source.reference.keyframes;
		int frame = source.keyFrame;
		int nextFrame = (frame + 1) % keyframes.length;
 		FlumpKeyFrame current = keyframes[frame];
		if (interpolation == 0) {
			//in the case where no interpolation occurs, save some computations
			xScale = current.scale[0];
			yScale = current.scale[1];
			xSkew = current.skew[0];
			ySkew = current.skew[1];
			xLoc = current.loc[0];
			yLoc = -current.loc[1];
			xPivot = -current.pivot[0];
			yPivot = -current.pivot[1];
		} else {
			FlumpKeyFrame next = keyframes[nextFrame];
			xScale = current.scale[0] + (next.scale[0] - current.scale[0]) * interpolation;
			yScale = current.scale[1] + (next.scale[1] - current.scale[1]) * interpolation;
			xSkew = current.skew[0] + (next.skew[0] - current.skew[0]) * interpolation;
			ySkew = current.skew[1] + (next.skew[1] - current.skew[1]) * interpolation;
			xLoc = current.loc[0] + (next.loc[0] - current.loc[0]) * interpolation;
			yLoc = -(current.loc[1] + (next.loc[1] - current.loc[1]) * interpolation);
			xPivot = -(current.pivot[0] + (next.pivot[0] - current.pivot[0]) * interpolation);
			yPivot = -(current.pivot[1] + (next.pivot[1] - current.pivot[1]) * interpolation);

		}
	}
	
	protected void flumpUpdate(float deltaFrames) {
		if (reference == null) throw new IllegalStateException("FlumpDisplayLayer is empty on update!");
		dirty = true;
		frameNumber += deltaFrames;
		FlumpKeyFrame keyFrames[] = reference.keyframes;
		while(frameNumber >= keyFrames[keyFrame].duration) {
			frameNumber -= keyFrames[keyFrame].duration;
			keyFrame++;
			if (keyFrame == keyFrames.length) keyFrame = 0;
		}
		float interpolation =  frameNumber / reference.keyframes[keyFrame].duration;
		float ease = reference.keyframes[keyFrame].ease;
		if (ease != 0) {
			float t;
			if (ease < 0) {
				// Ease in
				float inv = 1 - interpolation;
				t = 1 - inv * inv;
				ease = -ease;
			} else {
				// Ease out
				t = interpolation * interpolation;
			}
			interpolation = ease * t + (1 - ease) * interpolation;
		}
        FlumpDisplayTexture display = getDisplayTexture();
		float width = display.getRegionWidth();
		float height = display.getRegionHeight();
		getAttributes(this, interpolation);
		float sx = xPivot;
		float swx = xPivot + width;
		float shy = -(yPivot + height);
		float sy = -yPivot;
		if (xScale != 1 || yScale != 1) { 
			sx *= xScale;
			swx *= xScale;
			shy *= yScale;
			sy *= yScale;
		}
		pos[0].set(sx, shy);
		pos[1].set(sx, sy);
		pos[2].set(swx, sy);
		pos[3].set(swx, shy);
		if (xSkew != 0 || ySkew != 0) {
			float cosx =  MathUtils.cos(xSkew);
			float sinx =  MathUtils.sin(xSkew);
			float cosy;
			float siny;
			if (xSkew != ySkew) {
				cosy =  MathUtils.cos(ySkew);
				siny = MathUtils.sin(ySkew);
			} else {
				siny = sinx;
				cosy = cosx;
			}
			for (Vector2 p: pos) {
				p.set(cosy * p.x + sinx * p.y, - siny * p.x + cosx * p.y);
			}
		}
		if (userTransform != null) {
			for (Vector2 p : pos) {
				p.add(xLoc, yLoc).mul(userTransform);
			}
		} else {
			for (Vector2 p : pos) {
				p.add(xLoc, yLoc);
			}
		}

		/****************************************************
		 * Matrix Multiplication Implementation
		scale.setToScaling(xScale, yScale);
		rotation.val[Matrix3.M00] = MathUtils.cos(ySkew);
		rotation.val[Matrix3.M01] = MathUtils.sin(xSkew);
		rotation.val[Matrix3.M10] = -MathUtils.sin(ySkew);
		rotation.val[Matrix3.M11] = MathUtils.cos(xSkew);
		translation.setToTranslation(xLoc, yLoc);
		Matrix3 tmp = new Matrix3(scale.mul(rotation).mul(translation));
		for (int i = 0; i < pos.length; i++) {
			pos[i].mul(scale).mul(rotation).mul(translation);
		}
		*****************************************************/

	}
	
	void draw(SpriteBatch batch) {
		if (dirty) {
			vertices[X1] = pos[0].x;
			vertices[X2] = pos[1].x;
			vertices[X3] = pos[2].x;
			vertices[X4] = pos[3].x;
			vertices[Y1] = pos[0].y;
			vertices[Y2] = pos[1].y;
			vertices[Y3] = pos[2].y;
			vertices[Y4] = pos[3].y;
		}
		batch.draw(getDisplayTexture().getTexture(), vertices, 0, NUM_VERTICES);
		
	}

}
