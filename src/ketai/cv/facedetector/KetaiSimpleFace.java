/*
 * 
 */
package ketai.cv.facedetector;

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
}
