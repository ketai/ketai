/*
 * 
 */
package ketai.cv.facedetector;

import java.util.ArrayList;

import processing.core.PImage;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

/**
 *  KetaiFaceDetector wraps the android face detection api.  The android face finding relies
 *  	on eye detection and has limited accuracy but provides basic face detection without
 *  	any external dependencies.
 */
public class KetaiFaceDetector {
	
	/**
	 * Find faces.
	 *
	 * @param _image image to find faces in
	 * @param MAX_FACES the maximum number faces to find
	 * @return the ketai simple face[] - array of faces found
	 */
	public static KetaiSimpleFace[] findFaces(PImage _image, int MAX_FACES) {
		ArrayList<KetaiSimpleFace> foundFaces = new ArrayList<KetaiSimpleFace>();
		int numberOfFaces = 0;

		_image.loadPixels();

		android.graphics.Bitmap _bitmap = Bitmap.createBitmap(_image.pixels,
				_image.width, _image.height, Bitmap.Config.RGB_565);

		if (_bitmap != null) {
			FaceDetector _detector = new FaceDetector(_image.width, _image.height,
					MAX_FACES);
			Face[] faces = new Face[MAX_FACES];

			numberOfFaces = _detector.findFaces(_bitmap, faces);

			for (int i = 0; i < numberOfFaces; i++) {
				foundFaces.add(new KetaiSimpleFace(faces[i]));
			}
		}
		KetaiSimpleFace[] f = new KetaiSimpleFace[numberOfFaces];
		for (int i = 0; i < numberOfFaces; i++) {
			f[i] = foundFaces.get(i);
		}
		return f;
	}

	/**
	 * Find faces (max 5).
	 *
	 * @param _image - image to find faces in.
	 * @return the ketai simple face[]
	 */
	public static KetaiSimpleFace[] findFaces(PImage _image) {
		int DEFAULT_MAX_FACES = 5;
		return findFaces(_image, DEFAULT_MAX_FACES);
	}
}
