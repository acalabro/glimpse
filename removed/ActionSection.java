package it.cnr.isti.labsedc.glimpse.event;

import java.util.Vector;

public class ActionSection<String> extends Vector<String> {

	private static final long serialVersionUID = 8056366037573242993L;

	public boolean areValidForPolicySetOfTrace(ActionSection<java.lang.String> actSection, String idTrace) {
		
		return TraceEngine.evaluate((ActionSection<java.lang.String>) this,idTrace.toString());
	}

	public boolean areValidForPolicyOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((ActionSection<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForRulesOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((ActionSection<java.lang.String>) this,idTrace.toString());
	}
}
