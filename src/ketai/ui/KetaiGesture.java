/*
 * 
 */
package ketai.ui;

import java.lang.reflect.Method;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.TouchEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * Provides gesture recognition services to a processing sketch.  To receive 
 * 		gesture events a sketch can define the following methods:<br /><br />
 * 
 * 		void onTap(float x, float y)  - x, y location of the tap<br />
 * 		void onDoubleTap(float x, float y) - x,y location of double tap<br />
 *		void onFlick(float x, float y, float px, float py, float v) - x,y where flick ended, px,py - where flick began, v - velocity of flick in pixels/sec <br /> 
 *		void onScroll(int x int y) - not currently used<br />
 *		void onLongPress(float x, float y)  - x, y position of long press<br />
 *		void onPinch(float x, float y, float r) - x,y of center, r is the distance change<br />
 *		void onRotate(float x, float y, float a) - x, y of center, a is the angle change in radians<br />
 */
public class KetaiGesture implements OnGestureListener, OnDoubleTapListener {

	/** The parent. */
	PApplet parent;
	
	/** The gestures. */
	GestureDetector gestures;
	
	/** The me. */
	KetaiGesture me;
	
	/** The on rotate method. */
	Method onDoubleTapMethod, onScrollMethod, onFlickMethod, onTapMethod,
			onLongPressMethod, onPinchMethod, onRotateMethod;
	
	/** The cursors. */
	HashMap<Integer, PVector> cursors = new HashMap<Integer, PVector>();
	
	/** The pcursors. */
	HashMap<Integer, PVector> pcursors = new HashMap<Integer, PVector>();

	/**
	 * Instantiates a new ketai gesture.
	 *
	 * @param _parent the PApplet/sketch
	 */
	public KetaiGesture(PApplet _parent) {
		parent = _parent;
		me = this; // self reference for UI-thread constructor hackiness

		parent.runOnUiThread(new Runnable() {
			public void run() {
				gestures = new GestureDetector(parent, me);
			}
		});
		// this stuff is still not working in b7
		parent.registerMethod("touchEvent", this);

		findParentIntentions();
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 */
	public boolean onDown(MotionEvent arg0) {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (onFlickMethod != null) {
			try {
				PVector v = new PVector(arg2, arg3);
				
				onFlickMethod.invoke(parent,
						new Object[] { arg1.getX(), arg1.getY(), arg0.getX(),
								arg0.getY(), v.mag() });
			} catch (Exception e) {
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	public void onLongPress(MotionEvent arg0) {
		if (onLongPressMethod != null) {
			try {
				onLongPressMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY() });
			} catch (Exception e) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	public void onShowPress(MotionEvent arg0) {

	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	public boolean onSingleTapUp(MotionEvent arg0) {
		if (onTapMethod != null) {
			try {
				onTapMethod.invoke(parent,
						new Object[] { arg0.getX(), arg0.getY() });
			} catch (Exception e) {
			}
		}
		return true;
	}

	/**
	 * Touch event.
	 *
	 * @param e the e
	 */
	public void touchEvent(TouchEvent e) {
		PApplet.println("motionEvent called inside kgesture");
		if (e.getNative() instanceof MotionEvent) {
			PApplet.println("KGesture got a MotionEvent!");
			MotionEvent me = (MotionEvent) e.getNative();
			surfaceTouchEvent(me);
		}
	}

	/**
	 * Surface touch event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean surfaceTouchEvent(MotionEvent event) {
		// public boolean touchEvent(TouchEvent event){

		int code = event.getAction() & MotionEvent.ACTION_MASK;
		int index = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		float x = event.getX(index);
		float y = event.getY(index);
		int id = event.getPointerId(index);

		if (code == MotionEvent.ACTION_DOWN
				|| code == MotionEvent.ACTION_POINTER_DOWN) {
			cursors.put(id, new PVector(x, y));
		} else if (code == MotionEvent.ACTION_UP
				|| code == MotionEvent.ACTION_POINTER_UP) {
			if (cursors.containsKey(id))
				cursors.remove(id);
			if (pcursors.containsKey(id))
				pcursors.remove(id);

		} else if (code == MotionEvent.ACTION_MOVE) {
			int numPointers = event.getPointerCount();
			for (int i = 0; i < numPointers; i++) {
				id = event.getPointerId(i);
				x = event.getX(i);
				y = event.getY(i);
				if (cursors.containsKey(id))
					pcursors.put(id, cursors.get(id));
				else
					pcursors.put(id, new PVector(x, y));

				cursors.put(id, new PVector(x, y));
			}
		}
		analyse();
		parent.onTouchEvent(event);
		return gestures.onTouchEvent(event);
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onSingleTapConfirmed(android.view.MotionEvent)
	 */
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTap(android.view.MotionEvent)
	 */
	public boolean onDoubleTap(MotionEvent arg0) {
		if (onDoubleTapMethod != null) {
			try {
				onDoubleTapMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY() });
			} catch (Exception e) {
			}
		}
		return true;
	}

	/**
	 * Find parent intentions.
	 */
	private void findParentIntentions() {

		try {
			onTapMethod = parent.getClass().getMethod("onTap",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onDoubleTapMethod = parent.getClass().getMethod("onDoubleTap",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onFlickMethod = parent.getClass().getMethod(
					"onFlick",
					new Class[] { float.class, float.class, float.class,
							float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onScrollMethod = parent.getClass().getMethod("onScroll",
					new Class[] { int.class, int.class });
		} catch (Exception e) {
		}

		try {
			onLongPressMethod = parent.getClass().getMethod("onLongPress",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onPinchMethod = parent.getClass().getMethod("onPinch",
					new Class[] { float.class, float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onRotateMethod = parent.getClass().getMethod("onRotate",
					new Class[] { float.class, float.class, float.class });
		} catch (Exception e) {
		}
	}

	/**
	 * Analyse.
	 */
	private synchronized void analyse() {
		if (cursors.size() > 1 && pcursors.size() > 1) {
			PVector c1, c2, p1, p2;
			c1 = cursors.get(0);
			p1 = pcursors.get(0);

			c2 = cursors.get(1);
			p2 = pcursors.get(1);

			// only use cursors 1/2 for our gestures...for now
			if (c1 == null || c2 == null || p1 == null || p2 == null)
				return;

			float midx = (c1.x + c2.x) / 2;
			float midy = (c1.y + c2.y) / 2;

			float dp = PApplet.dist(p1.x, p1.y, p2.x, p2.y);
			float dc = PApplet.dist(c1.x, c1.y, c2.x, c2.y);

			float oldangle = PApplet.atan2(PVector.sub(p1, p2).y,
					PVector.sub(p1, p2).x);
			float newangle = PApplet.atan2(PVector.sub(c1, c2).y,
					PVector.sub(c1, c2).x);

			float delta = (newangle - oldangle);

			if (onPinchMethod != null) {
				try {
					onPinchMethod.invoke(parent, new Object[] { midx, midy,
							dc - dp });
				} catch (Exception e) {
				}
			}

			if (onRotateMethod != null) {
				try {
					onRotateMethod.invoke(parent, new Object[] { midx, midy,
							delta });
				} catch (Exception e) {
				}
			}

		}
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTapEvent(android.view.MotionEvent)
	 */
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
