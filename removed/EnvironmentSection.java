package it.cnr.isti.labsedc.glimpse.event;

import java.util.Vector;

public class EnvironmentSection<String> extends Vector<String> {
	
	public boolean areValidForPolicySetOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((EnvironmentSection<java.lang.String>) this,idTrace.toString());
	}

	public boolean areValidForPolicyOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((EnvironmentSection<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForRulesOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((EnvironmentSection<java.lang.String>) this,idTrace.toString());
	}
	
}
