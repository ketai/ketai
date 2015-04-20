/*
 * 
 */
package ketai.cv.facedetector;

import processing.core.PApplet;
import processing.core.PVector;
import android.graphics.PointF;
import android.media.FaceDetector.Face;

/**
 *  KetaiSimpleFace provide a utility class for faces found by
 *  	the android face finding service.
 */
public class KetaiSimpleFace {
	
	/** The location. */
	public PVector location;
	
	/** The distance. */
	public float distance;
	
	/** The confidence. */
	public float confidence;

	/**
	 * Instantiates a new ketai simple face.
	 *
	 * @param f the Face found.
	 */
	public KetaiSimpleFace(Face f) {
		PointF p = new PointF();
		f.getMidPoint(p);
		location = new PVector(p.x, p.y);
		distance = f.eyesDistance();
	}
	
	public KetaiSimpleFace(Face f, int landscapeWidth, int landscapeHeight)
	{
		PointF p = new PointF();
		f.getMidPoint(p);
		location = new PVector(p.y, PApplet.map(p.x, 0, landscapeHeight, landscapeHeight,0));//PApplet.map(p.y,0,landscapeHeight, 0, landscapeWidth), 
				//PApplet.map(p.x, 0, landscapeWidth, 0, landscapeHeight));
		distance = f.eyesDistance();
	}
}
