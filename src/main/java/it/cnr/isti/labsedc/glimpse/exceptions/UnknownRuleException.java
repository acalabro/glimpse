 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/

package it.cnr.isti.labsedc.glimpse.exceptions;

import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

/**
 * This exception will be thrown when the Monitoring Manager<br />
 * is unable to complete the request of unloading a rule.
 * 
 * @author Antonello Calabr&ograve;
 * @version 3.2
 */

public class UnknownRuleException extends Exception {

	private static final long serialVersionUID = -8325934056030280854L;

	public UnknownRuleException()
	{
		DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(), "Unable to found a rule with provided ruleName");
	}
	
	
}
