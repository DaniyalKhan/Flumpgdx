package com.bigwoah.flumpgdx.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.bigwoah.flumpgdx.library.FlumpKeyFrame;
import com.bigwoah.flumpgdx.library.FlumpLayer;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class FlumpDisplay {

	private static final float[] DISPLAY_VERTICES = new float[20];
	private static final float DEFAULT_VERTEX_COLOR = Color.WHITE.toFloatBits(); 
	
	protected static float xPivot, yPivot, xSkew, ySkew, xScale, yScale, xLoc, yLoc; 
	
	protected Vector2[] pos = new Vector2[4];
	private int keyframe; //current keyframe
	protected int numFrames;
	boolean overElapsed = false;

	protected final FlumpLayer reference;
		
	public FlumpDisplay(FlumpLayer layer) {
		this.reference = layer;
		this.numFrames = -1;
		for (int i = 0; i < pos.length; i++) {
			pos[i] = new Vector2();
		}
	}
	
	protected FlumpDisplayTexture getDisplayTexture() {
		return TextureCache.obtain().get(reference.keyframes[keyframe].ref);
	}

	/**
	 * Get the duration of this layer by summing all the frame durations
	 * @return The sum of the frame durations for this layer
	 */
	public int maxFrameCount() {
		if (numFrames < 0) {
			for (FlumpKeyFrame keyframe : reference.keyframes) {
				numFrames += keyframe.duration;
			}
		}
		return numFrames;
	}
	
	protected static void getAttributes(FlumpDisplay source, float interpolation) {
		FlumpKeyFrame[] keyframes = source.reference.keyframes;
		int frame = source.keyframe;
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
	
	protected void update(int frame, Matrix3 transform) {
		if (reference == null) throw new IllegalStateException("FlumpDisplayLayer is empty on update!");
		keyframe = 0;
		overElapsed = frame > maxFrameCount();
		if (overElapsed) return;
		while(frame >= reference.keyframes[keyframe].duration && keyframe < reference.keyframes.length - 1) {
			frame -= reference.keyframes[keyframe].duration;
			keyframe++;
		}
		float interpolation = ((float) frame) / reference.keyframes[keyframe].duration;
		//last frame in the animation, no interpolation
		if (keyframe == reference.keyframes.length - 1) {
			interpolation = 0;
		}
		float ease = reference.keyframes[keyframe].ease;
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
		for (Vector2 p: pos) {
			p.add(xLoc, yLoc).mul(transform);
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
		if (overElapsed) return;
		FlumpDisplayTexture t = getDisplayTexture();
		float u = t.getU();
		float u2 = t.getU2();
		float v = t.getV();
		float v2 = t.getV2();
		
		DISPLAY_VERTICES[U1] = u;
		DISPLAY_VERTICES[V1] = v2;
		DISPLAY_VERTICES[U2] = u;
		DISPLAY_VERTICES[V2] = v;
		DISPLAY_VERTICES[U3] = u2;
		DISPLAY_VERTICES[V3] = v;
		DISPLAY_VERTICES[U4] = u2;
		DISPLAY_VERTICES[V4] = v2;
		DISPLAY_VERTICES[C1] = DEFAULT_VERTEX_COLOR;
		DISPLAY_VERTICES[C2] = DEFAULT_VERTEX_COLOR;
		DISPLAY_VERTICES[C3] = DEFAULT_VERTEX_COLOR;
		DISPLAY_VERTICES[C4] = DEFAULT_VERTEX_COLOR;
		DISPLAY_VERTICES[X1] = pos[0].x;
		DISPLAY_VERTICES[X2] = pos[1].x;
		DISPLAY_VERTICES[X3] = pos[2].x;
		DISPLAY_VERTICES[X4] = pos[3].x;
		DISPLAY_VERTICES[Y1] = pos[0].y;
		DISPLAY_VERTICES[Y2] = pos[1].y;
		DISPLAY_VERTICES[Y3] = pos[2].y;
		DISPLAY_VERTICES[Y4] = pos[3].y;
		
		batch.draw(t.getTexture(), DISPLAY_VERTICES, 0, DISPLAY_VERTICES.length);
		
	}

}
