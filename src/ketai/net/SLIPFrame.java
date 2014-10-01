/*
 * 
 */
package ketai.net;

/*
 * Simple SLIP utility class based on the SLIP RFC 1055
 * 
 * http://www.ietf.org/rfc/rfc1055.txt
 * 
 * Using these (octal)values: 
 #define END             0300    // indicates end of packet 
 #define ESC             0333    // indicates byte stuffing 
 #define ESC_END         0334    // ESC ESC_END means END data byte 
 #define ESC_ESC         0335    // ESC ESC_ESC means ESC data byte 
 * 
 */

import java.io.ByteArrayOutputStream;

/**
 * The Class SLIPFrame.
 */
public class SLIPFrame {

	/** The end. */
	public static byte END = (byte) 0xC0;
	
	/** The esc. */
	public static byte ESC = (byte) 0xDB;
	
	/** The esc end. */
	public static byte ESC_END = (byte) 0xDC;
	
	/** The esc esc. */
	public static byte ESC_ESC = (byte) 0xDD;

	/*
	 * utility static method that takes a byte array of data and formats it into
	 * a SLIP frame, ready for transmission. The returned frame does NOT contain
	 * the END character used to denote the frame itself. It is typical that the
	 * END character is sent before transmitting the frame and after
	 * transmitting the frame.
	 */
	/**
	 * Creates a SLIP formatted data frame.
	 *
	 * @param _data the _data
	 * @return the byte[]
	 */
	public static byte[] createFrame(byte[] _data) {
		// worst case scenario is we have to sub every byte resulting in a
		// doubled buffer
		ByteArrayOutputStream bout = new ByteArrayOutputStream(_data.length * 2);

		for (int i = 0; i < _data.length; i++) {
			if (_data[i] == END) {
				bout.write((byte) ESC);
				bout.write((byte) ESC_END);
			} else if (_data[i] == ESC) {
				bout.write(ESC);
				bout.write(ESC_ESC);
			} else
				bout.write(_data[i]);
		}

		return bout.toByteArray();
	}

	/*
	 * utility static method that takes a byte array coming from a SLIP frame in
	 * order to "decode" it. The start/stop END characters should be stripped
	 * prior to passing in the frame for processing. A byte array of the
	 * original data will be returned.
	 */

	/**
	 * Parses a SLIP formatted frame and provides the data received.
	 *
	 * @param data the data
	 * @return the byte[]
	 */
	public static byte[] parseFrame(byte[] data) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(data.length);

		for (int i = 0; i < data.length; i++) {
			// if we have and esc and another byte...
			if (data[i] == ESC && i + 1 < data.length) {
				i++;
				if (data[i] == ESC_END)
					bout.write(END);
				else if (data[i] == ESC_ESC)
					bout.write(ESC);
				else
					bout.write(data[i]);
			}
		}

		return bout.toByteArray();

	}
}
