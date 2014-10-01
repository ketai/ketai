package ketai.sensors;

import java.lang.reflect.Method;

import processing.core.PApplet;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class KetaiAudioInput implements Runnable {

	private AudioRecord audioRecorder = null;
	private int bufferSize;
	private int samplesPerSec = 16000;
	private Thread thread = null;
	public Object callbackdelegate;
	private Method callbackMethod;
	private boolean isRecording;
	private String LOG_TAG = "KetaiAudioInput";

	public KetaiAudioInput(Object consumer) {
		register(consumer);
	}

	public void start() {
		
		bufferSize = AudioRecord.getMinBufferSize(samplesPerSec,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		if (bufferSize != AudioRecord.ERROR_BAD_VALUE
				&& bufferSize != AudioRecord.ERROR) {
			PApplet.println("Buffer size: " + bufferSize);

			audioRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
					this.samplesPerSec, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, this.bufferSize * 10); // bufferSize
																			// 10x

			if (audioRecorder != null
					&& audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
				Log.i(LOG_TAG, "Audio Recorder created");
				PApplet.println( "Audio Recorder created");

				audioRecorder.startRecording();
				isRecording = true;
				thread = new Thread(this);
				thread.start();

			} else {
				Log.e(LOG_TAG, "Unable to create AudioRecord instance");
				PApplet.println("Unable to create AudioRecord instance");
			}

		} else {
			Log.e(LOG_TAG, "Unable to get minimum buffer size");
		}
	}

	public void stop() {
		isRecording = false;
		if (audioRecorder != null) {
			if (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
				audioRecorder.stop();
			}
			if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
				audioRecorder.release();
			}
		}
	}

	public boolean isActive() {
		return (audioRecorder != null) ? (audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
				: false;
	}

	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		while (isRecording
				&& audioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
			short[] buf = new short[bufferSize];
			audioRecorder.read(buf, 0, buf.length);
			try {
				callbackMethod.invoke(callbackdelegate, new Object[] { buf });
			} catch (Exception e) {
				PApplet.println("OOps... onAudioEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void register(Object o) {
		callbackdelegate = o;

		try {
			callbackMethod = o.getClass().getMethod("onAudioEvent",
					new Class[] { short[].class });

			PApplet.println("Found onAudioEvent callback method...");
		} catch (NoSuchMethodException e) {
			PApplet.println("Failed to find onAudioEvent callback method...");
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("AudioCapturer finalizer");
		if (audioRecorder != null
				&& audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
			audioRecorder.stop();
			audioRecorder.release();
		}
		audioRecorder = null;
		thread = null;
	}

}
