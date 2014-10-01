/*
 * 
 */
package ketai.net.nfc;

import java.util.ArrayList;
import java.util.List;

import ketai.net.nfc.record.ParsedNdefRecord;
import ketai.net.nfc.record.SmartPoster;
import ketai.net.nfc.record.TextRecord;
import ketai.net.nfc.record.UriRecord;
import processing.core.PApplet;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * The Class NdefMessageParser.
 */
public class NdefMessageParser {

	// Utility class
	/**
	 * Instantiates a new ndef message parser.
	 */
	private NdefMessageParser() {

	}

	/**
	 * Parse an NdefMessage.
	 *
	 * @param message the message
	 * @return the list
	 */
	public static List<ParsedNdefRecord> parse(NdefMessage message) {
		return getRecords(message.getRecords());
	}

	/**
	 * Gets the records.
	 *
	 * @param records the records
	 * @return the records
	 */
	public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
		List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
		for (NdefRecord record : records) {
			if (TextRecord.isText(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a text");
				elements.add(TextRecord.parse(record));
			} else if (SmartPoster.isPoster(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a smart poster");
				elements.add(SmartPoster.parse(record));
			} else if (UriRecord.isUri(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a URI");
				elements.add(UriRecord.parse(record));
			}
		}
		return elements;
	}
}
