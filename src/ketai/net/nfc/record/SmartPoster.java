/*
 * 
 */
package ketai.net.nfc.record;

import java.util.Arrays;
import java.util.NoSuchElementException;

import ketai.net.nfc.NdefMessageParser;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * A representation of an NFC Forum "Smart Poster".
 */
public class SmartPoster implements ParsedNdefRecord {

	/**
	 * NFC Forum Smart Poster Record Type Definition section 3.2.1.
	 * 
	 * "The Title record for the service (there can be many of these in
	 * different languages, but a language MUST NOT be repeated). This record is
	 * optional."
	 */
	private final TextRecord mTitleRecord;

	/**
	 * NFC Forum Smart Poster Record Type Definition section 3.2.1.
	 * 
	 * "The URI record. This is the core of the Smart Poster, and all other
	 * records are just metadata about this record. There MUST be one URI record
	 * and there MUST NOT be more than one."
	 */
	private final UriRecord mUriRecord;

	/**
	 * NFC Forum Smart Poster Record Type Definition section 3.2.1.
	 * 
	 * "The Action record. This record describes how the service should be
	 * treated. For example, the action may indicate that the device should save
	 * the URI as a bookmark or open a browser. The Action record is optional.
	 * If it does not exist, the device may decide what to do with the service.
	 * If the action record exists, it should be treated as a strong suggestion;
	 * the UI designer may ignore it, but doing so will induce a different user
	 * experience from device to device."
	 *
	 * @param uri the uri
	 * @param title the title
	 * @param action the action
	 * @param type the type
	 */
	// private final RecommendedAction mAction;

	/**
	 * NFC Forum Smart Poster Record Type Definition section 3.2.1.
	 * 
	 * "The Type record. If the URI references an external entity (e.g., via a
	 * URL), the Type record may be used to declare the MIME type of the entity.
	 * This can be used to tell the mobile device what kind of an object it can
	 * expect before it opens the connection. The Type record is optional."
	 */
	// private final String mType;

	private SmartPoster(UriRecord uri, TextRecord title,
			RecommendedAction action, String type) {
		mUriRecord = uri;
		mTitleRecord = title;
		// mAction = action;
		// mType = type;
	}

	/**
	 * Gets the uri record.
	 *
	 * @return the uri record
	 */
	public UriRecord getUriRecord() {
		return mUriRecord;
	}

	/**
	 * Returns the title of the smart poster. This may be {@code null}.
	 *
	 * @return the title
	 */
	public TextRecord getTitle() {
		return mTitleRecord;
	}

	/**
	 * Parses the.
	 *
	 * @param record the record
	 * @return the smart poster
	 */
	public static SmartPoster parse(NdefRecord record) {
		// Preconditions
		// .checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
		// Preconditions.checkArgument(Arrays.equals(record.getType(),
		// NdefRecord.RTD_SMART_POSTER));
		try {
			NdefMessage subRecords = new NdefMessage(record.getPayload());
			return parse(subRecords.getRecords());
		} catch (FormatException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Parses the.
	 *
	 * @param recordsRaw the records raw
	 * @return the smart poster
	 */
	public static SmartPoster parse(NdefRecord[] recordsRaw) {
		try {
			Iterable<ParsedNdefRecord> records = NdefMessageParser
					.getRecords(recordsRaw);
			// UriRecord uri =
			// Iterables.getOnlyElement(Iterables.filter(records,
			// UriRecord.class));
			TextRecord title = getFirstIfExists(records, TextRecord.class);
			RecommendedAction action = parseRecommendedAction(recordsRaw);
			String type = parseType(recordsRaw);
			// return new SmartPoster(uri, title, action, type);
			return new SmartPoster(null, title, action, type);
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Checks if is poster.
	 *
	 * @param record the record
	 * @return true, if is poster
	 */
	public static boolean isPoster(NdefRecord record) {
		try {
			parse(record);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Returns the first element of {@code elements} which is an instance of.
	 *
	 * @param <T> the generic type
	 * @param elements the elements
	 * @param type the type
	 * @return the first if exists
	 * {@code type}, or {@code null} if no such element exists.
	 */
	private static <T> T getFirstIfExists(Iterable<?> elements, Class<T> type) {
		// Iterable<T> filtered = Iterables.filter(elements, type);
		// T instance = null;
		// if (!Iterables.isEmpty(filtered)) {
		// instance = Iterables.get(filtered, 0);
		// }
		// return instance;
		return null;
	}

	/**
	 * The Enum RecommendedAction.
	 */
	private enum RecommendedAction {
		
		/** The unknown. */
		UNKNOWN((byte) -1), 
 /** The do action. */
 DO_ACTION((byte) 0), 
 /** The save for later. */
 SAVE_FOR_LATER((byte) 1), 
 /** The open for editing. */
 OPEN_FOR_EDITING(
				(byte) 2);

		// private static final ImmutableMap<Byte, RecommendedAction> LOOKUP;
		// static {
		// ImmutableMap.Builder<Byte, RecommendedAction> builder = ImmutableMap
		// .builder();
		// for (RecommendedAction action : RecommendedAction.values()) {
		// builder.put(action.getByte(), action);
		// }
		// LOOKUP = builder.build();
		// }

		/** The m action. */
		private final byte mAction;

		/**
		 * Instantiates a new recommended action.
		 *
		 * @param val the val
		 */
		private RecommendedAction(byte val) {
			this.mAction = val;
		}

		/**
		 * Gets the byte.
		 *
		 * @return the byte
		 */
		private byte getByte() {
			return mAction;
		}
	}

	/**
	 * Gets the by type.
	 *
	 * @param type the type
	 * @param records the records
	 * @return the by type
	 */
	private static NdefRecord getByType(byte[] type, NdefRecord[] records) {
		for (NdefRecord record : records) {
			if (Arrays.equals(type, record.getType())) {
				return record;
			}
		}
		return null;
	}

	/** The Constant ACTION_RECORD_TYPE. */
	private static final byte[] ACTION_RECORD_TYPE = new byte[] { 'a', 'c', 't' };

	/**
	 * Parses the recommended action.
	 *
	 * @param records the records
	 * @return the recommended action
	 */
	private static RecommendedAction parseRecommendedAction(NdefRecord[] records) {
		NdefRecord record = getByType(ACTION_RECORD_TYPE, records);
		if (record == null) {
			return RecommendedAction.UNKNOWN;
		}
		byte action = record.getPayload()[0];
		// if (RecommendedAction.LOOKUP.containsKey(action)) {
		// return RecommendedAction.LOOKUP.get(action);
		// }
		return RecommendedAction.UNKNOWN;
	}

	/** The Constant TYPE_TYPE. */
	private static final byte[] TYPE_TYPE = new byte[] { 't' };

	/**
	 * Parses the type.
	 *
	 * @param records the records
	 * @return the string
	 */
	private static String parseType(NdefRecord[] records) {
		NdefRecord type = getByType(TYPE_TYPE, records);
		if (type == null) {
			return null;
		}
		// return new String(type.getPayload(), Charsets.UTF_8);
		return null;
	}

	/* (non-Javadoc)
	 * @see ketai.net.nfc.record.ParsedNdefRecord#getTag()
	 */
	public String getTag() {
		// TODO Auto-generated method stub
		return null;
	}
}
