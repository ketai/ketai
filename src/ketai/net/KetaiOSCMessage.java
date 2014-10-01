/*
 * 
 */
package ketai.net;

/*
 * 	This class is a utility class that exposes the parseMessage(byte[] data) 
 * 		protected method from the OscMessage class.  This allows us to parse
 * 		byte arrays into OscMessages.  This is useful over serial links
 * 		where IP is forsaken. It is identical to OscMessage for all usage
 * 		purposes except the constructor and exposing the isValid flag as
 * 		a means to checking if the byte array yielded a valide OSC message.
 */
import oscP5.OscMessage;

/**
 * The Class KetaiOSCMessage.
 */
public class KetaiOSCMessage extends OscMessage {

	/**
	 * Instantiates a new ketai osc message.
	 *
	 * @param _data the _data
	 */
	public KetaiOSCMessage(byte[] _data) {
		super("");
		this.parseMessage(_data);
	}

	/* (non-Javadoc)
	 * @see oscP5.OscPacket#isValid()
	 */
	public boolean isValid() {
		return isValid;
	}

}
