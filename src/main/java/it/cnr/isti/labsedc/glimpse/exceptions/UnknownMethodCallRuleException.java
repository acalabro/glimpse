/**
 * 
 */
package it.cnr.isti.labsedc.glimpse.exceptions;

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
		DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(), "Loaded rule may contains unknown method calls");
	}
	
}
