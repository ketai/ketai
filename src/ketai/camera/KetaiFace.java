package ketai.camera;

import processing.core.PApplet;
import processing.core.PVector;
import android.hardware.Camera.Face;

/**
 * The Class KetaiFace wrap the internal android Face class.  
 */
public class KetaiFace extends Face {
	
	/** The center. */
	public PVector leftEye, rightEye, mouth, center;
	
	/** The height. */
	public int id, score, width, height;

	/**
	 * Instantiates a new ketai face.
	 *
	 * @param f the face Object
	 * @param frameWidth the frame width to map the Face on
	 * @param frameHeight the frame height to map the Face on
	 */
	public KetaiFace(Face f, int frameWidth, int frameHeight) {
		leftEye = new PVector(PApplet.map(f.leftEye.x, -1000, 1000, 0,
				frameWidth), PApplet.map(f.leftEye.y, -1000, 1000, 0,
				frameHeight));

		rightEye = new PVector(PApplet.map(f.rightEye.x, -1000, 1000, 0,
				frameWidth), PApplet.map(f.rightEye.y, -1000, 1000, 0,
				frameHeight));
		mouth = new PVector(PApplet.map(f.mouth.x, -1000, 1000, 0, frameWidth),
				PApplet.map(f.mouth.y, -1000, 1000, 0, frameHeight));
		id = f.id;
		score = f.score;
		center = new PVector(PApplet.map(f.rect.exactCenterX(), -1000, 1000, 0,
				frameWidth), PApplet.map(f.rect.exactCenterY(), -1000, 1000, 0,
				frameHeight));
		width = f.rect.width();
		height = f.rect.height();
	}
}
