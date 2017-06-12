package it.cnr.isti.labsedc.glimpse.event;

import java.util.Vector;

public class ResourcesSection<String> extends Vector<String> {

	private static final long serialVersionUID = 8640747412470376829L;

	public boolean areValidForPolicySetOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((ResourcesSection<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForPolicyOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((ResourcesSection<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForRulesOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((ResourcesSection<java.lang.String>) this,idTrace.toString());
	}
}
