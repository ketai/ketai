/*
 * 
 */
package ketai.net.nfc.record;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import android.net.Uri;
import android.nfc.NdefRecord;

/**
 * A parsed record containing a Uri.
 */
public class UriRecord implements ParsedNdefRecord {

	/** The Constant RECORD_TYPE. */
	public static final String RECORD_TYPE = "UriRecord";

	/**
	 * NFC Forum "URI Record Type Definition"
	 * 
	 * This is a mapping of "URI Identifier Codes" to URI string prefixes, per
	 * section 3.2.2 of the NFC Forum URI Record Type Definition document.
	 */
	@SuppressWarnings("serial")
	private static final HashMap<Byte, String> URI_PREFIX_MAP = new HashMap<Byte, String>() {
		{
			put(new Byte((byte) 0x00), "");
			put(new Byte((byte) 0x01), "http://www.");
			put(new Byte((byte) 0x02), "https://www.");
			put(new Byte((byte) 0x03), "http://");
			put(new Byte((byte) 0x04), "https://");
			put(new Byte((byte) 0x05), "tel:");
			put(new Byte((byte) 0x06), "mailto:");
			put(new Byte((byte) 0x07), "ftp://anonymous:anonymous@");
			put(new Byte((byte) 0x08), "ftp://ftp.");
			put(new Byte((byte) 0x09), "ftps://");
			put(new Byte((byte) 0x0A), "sftp://");
			put(new Byte((byte) 0x0B), "smb://");
			put(new Byte((byte) 0x0C), "nfs://");
			put(new Byte((byte) 0x0D), "ftp://");
			put(new Byte((byte) 0x0E), "dav://");
			put(new Byte((byte) 0x0F), "news:");
			put(new Byte((byte) 0x10), "telnet://");
			put(new Byte((byte) 0x11), "imap:");
			put(new Byte((byte) 0x12), "rtsp://");
			put(new Byte((byte) 0x13), "urn:");
			put(new Byte((byte) 0x14), "pop:");
			put(new Byte((byte) 0x15), "sip:");
			put(new Byte((byte) 0x16), "sips:");
			put(new Byte((byte) 0x17), "tftp:");
			put(new Byte((byte) 0x18), "btspp://");
			put(new Byte((byte) 0x19), "btl2cap://");
			put(new Byte((byte) 0x1A), "btgoep://");
			put(new Byte((byte) 0x1B), "tcpobex://");
			put(new Byte((byte) 0x1C), "irdaobex://");
			put(new Byte((byte) 0x1D), "file://");
			put(new Byte((byte) 0x1E), "urn:epc:id:");
			put(new Byte((byte) 0x1F), "urn:epc:tag:");
			put(new Byte((byte) 0x20), "urn:epc:pat:");
			put(new Byte((byte) 0x21), "urn:epc:raw:");
			put(new Byte((byte) 0x22), "urn:epc:");
			put(new Byte((byte) 0x23), "urn:nfc:");
		}
	};

	/** The m uri. */
	private final Uri mUri;

	/**
	 * Instantiates a new uri record.
	 *
	 * @param uri the uri
	 */
	private UriRecord(Uri uri) {
		if (uri != null)
			this.mUri = uri;
		else
			this.mUri = Uri.EMPTY;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public Uri getUri() {
		return mUri;
	}

	/**
	 * Convert {@link android.nfc.NdefRecord} into a {@link android.net.Uri}.
	 * This will handle both TNF_WELL_KNOWN / RTD_URI and TNF_ABSOLUTE_URI.
	 *
	 * @param record the record
	 * @return the uri record
	 */
	public static UriRecord parse(NdefRecord record) {
		short tnf = record.getTnf();
		if (tnf == NdefRecord.TNF_WELL_KNOWN) {
			return parseWellKnown(record);
		} else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
			return parseAbsolute(record);
		}
		throw new IllegalArgumentException("Unknown TNF " + tnf);
	}

	/**
	 * Parse and absolute URI record.
	 *
	 * @param record the record
	 * @return the uri record
	 */
	private static UriRecord parseAbsolute(NdefRecord record) {
		byte[] payload = record.getPayload();
		Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
		return new UriRecord(uri);
	}

	/**
	 * Parse an well known URI record.
	 *
	 * @param record the record
	 * @return the uri record
	 */
	private static UriRecord parseWellKnown(NdefRecord record) {

		if (!Arrays.equals(record.getType(), NdefRecord.RTD_URI))
			return new UriRecord(Uri.EMPTY);

		byte[] payload = record.getPayload();
		/*
		 * payload[0] contains the URI Identifier Code, per the NFC Forum
		 * "URI Record Type Definition" section 3.2.2.
		 * 
		 * payload[1]...payload[payload.length - 1] contains the rest of the
		 * URI.
		 */
		String prefix = URI_PREFIX_MAP.get(payload[0]);
		byte[] fullUri = Arrays.copyOf(prefix.getBytes(),
				prefix.getBytes(Charset.forName("UTF-8")).length
						+ payload.length);
		int k = 0;
		for (int i = prefix.getBytes(Charset.forName("UTF-8")).length; (i < prefix
				.getBytes(Charset.forName("UTF-8")).length + payload.length && (k < payload.length)); i++) {
			fullUri[i] = payload[k];
			k++;
		}

		Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
		return new UriRecord(uri);
	}

	/**
	 * Checks if is uri.
	 *
	 * @param record the record
	 * @return true, if is uri
	 */
	public static boolean isUri(NdefRecord record) {
		try {
			parse(record);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see ketai.net.nfc.record.ParsedNdefRecord#getTag()
	 */
	public String getTag() {
		return mUri.toString();
	}
}
