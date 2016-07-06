/**
 * 
 */
package it.cnr.isti.labsedc.glimpse.exceptions;

import org.apache.commons.net.ntp.TimeStamp;

import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

/**
 * 
 * @author Antonello Calabr&ograve;
 *
 * @version 3.2
 */
public class UnknownMethodCallRuleException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownMethodCallRuleException() {
		DebugMessages.println(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Loaded rule may contains unknown method calls");
	}
	
}
