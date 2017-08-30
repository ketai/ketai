/*
 * 
 */
package ketai.cv.facedetector;

import java.util.ArrayList;

import ketai.camera.KetaiCamera;
import processing.core.PImage;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

/**
 * KetaiFaceDetector wraps the android face detection api. The android face
 * finding relies on eye detection and has limited accuracy but provides basic
 * face detection without any external dependencies.
 */
public class KetaiFaceDetector {

	/**
	 * Find faces.
	 * 
	 * @param _image
	 *            image to find faces in
	 * @param MAX_FACES
	 *            the maximum number faces to find
	 * @return the ketai simple face[] - array of faces found
	 */
	
	public static KetaiSimpleFace[] findFaces(PImage _image, int MAX_FACES) {
		ArrayList<KetaiSimpleFace> foundFaces = new ArrayList<KetaiSimpleFace>();
		int numberOfFaces = 0;

		_image.loadPixels();

		android.graphics.Bitmap _bitmap = Bitmap.createBitmap(_image.pixels,
				_image.width, _image.height, Bitmap.Config.RGB_565);

		if (_bitmap != null) {
			FaceDetector _detector = new FaceDetector(_image.width,
					_image.height, MAX_FACES);
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

	public static KetaiSimpleFace[] findFaces(KetaiCamera _camera, int MAX_FACES) {
		ArrayList<KetaiSimpleFace> foundFaces = new ArrayList<KetaiSimpleFace>();
		int numberOfFaces = 0;

		_camera.loadPixels();

		android.graphics.Bitmap _bitmap = Bitmap.createBitmap(_camera.pixels,
				_camera.width, _camera.height, Bitmap.Config.RGB_565);

		if (_camera.requestedPortraitImage) {
			Matrix matrix = new Matrix();

			matrix.postRotate(90);

			Bitmap scaledBitmap = Bitmap.createScaledBitmap(_bitmap,
					_camera.width, _camera.height, true);

			Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
			if (rotatedBitmap != null) {

				FaceDetector _detector = new FaceDetector(rotatedBitmap.getWidth(),
						rotatedBitmap.getHeight(), MAX_FACES);
				Face[] faces = new Face[MAX_FACES];

				numberOfFaces = _detector.findFaces(rotatedBitmap, faces);

				for (int i = 0; i < numberOfFaces; i++) {
					//	public KetaiSimpleFace(Face f, boolean transposed, int landscapeWidth, int landscapeHeight)

					foundFaces.add(new KetaiSimpleFace(faces[i], _camera.width, _camera.height));
				}
			}
			KetaiSimpleFace[] f = new KetaiSimpleFace[numberOfFaces];
			for (int i = 0; i < numberOfFaces; i++) {
				f[i] = foundFaces.get(i);
			}
			return f;
		} else {
			if (_bitmap != null) {
				FaceDetector _detector = new FaceDetector(_camera.width,
						_camera.height, MAX_FACES);
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
	}

	/**
	 * Find faces (max 5).
	 * 
	 * @param _image
	 *            - image to find faces in.
	 * @return the ketai simple face[]
	 */
	public static KetaiSimpleFace[] findFaces(KetaiCamera _image) {
		int DEFAULT_MAX_FACES = 5;
		return findFaces(_image, DEFAULT_MAX_FACES);
	}
}
