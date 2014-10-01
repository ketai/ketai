/*
 * 
 */
package ketai.net.nfc.record;

import java.io.UnsupportedEncodingException;

import processing.core.PApplet;
import android.nfc.NdefRecord;

/**
 * An NFC Text Record.
 */
public class TextRecord implements ParsedNdefRecord {

	/** ISO/IANA language code. */
	private final String mLanguageCode;

	/** The m text. */
	private final String mText;

	/**
	 * Instantiates a new text record.
	 *
	 * @param languageCode the language code
	 * @param text the text
	 */
	private TextRecord(String languageCode, String text) {
		mLanguageCode = languageCode;
		mText = text;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return mText;
	}

	/**
	 * Returns the ISO/IANA language code associated with this text element.
	 *
	 * @return the language code
	 */
	public String getLanguageCode() {
		return mLanguageCode;
	}

	// TODO: deal with text fields which span multiple NdefRecords
	/**
	 * Parses the.
	 *
	 * @param record the record
	 * @return the text record
	 */
	public static TextRecord parse(NdefRecord record) {
		try {
			byte[] payload = record.getPayload();

			PApplet.println("TextRecord parsed and NdefRecord with a payload of "
					+ payload.length + " bytes.");

			// if (payload.length < 2)
			// if(true)
			// throw new IllegalArgumentException(
			// "Not enough Payload to parse TextRecord");
			/*
			 * payload[0] contains the "Status Byte Encodings" field, per the
			 * NFC Forum "Text Record Type Definition" section 3.2.1.
			 * 
			 * bit7 is the Text Encoding Field.
			 * 
			 * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
			 * The text is encoded in UTF16
			 * 
			 * Bit_6 is reserved for future use and must be set to zero.
			 * 
			 * Bits 5 to 0 are the length of the IANA language code.
			 */
			String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
					: "UTF-16";
			int languageCodeLength = payload[0] & 0077;
			String languageCode = new String(payload, 1, languageCodeLength,
					"US-ASCII");
			String text = new String(payload, languageCodeLength + 1,
					payload.length - languageCodeLength - 1, textEncoding);

			PApplet.println("TextRecord parsing: " + payload);
			PApplet.println("\t parsed text:" + text);
			return new TextRecord(languageCode, text);
		} catch (UnsupportedEncodingException e) {
			// should never happen unless we get a malformed tag.
			throw new IllegalArgumentException(e);
		} catch (Exception x) {
			throw new IllegalArgumentException(
					"Error parsing as a TextRecord: " + x.getMessage());
		}
	}

	/**
	 * Checks if is text.
	 *
	 * @param record the record
	 * @return true, if is text
	 */
	public static boolean isText(NdefRecord record) {
		try {
			parse(record);
			PApplet.println("TextRecord.isText is true!");
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see ketai.net.nfc.record.ParsedNdefRecord#getTag()
	 */
	public String getTag() {
		return getText();
	}
}
