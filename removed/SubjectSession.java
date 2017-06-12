package it.cnr.isti.labsedc.glimpse.event;

import java.util.Vector;

public class SubjectSession<String> extends Vector<String>{

	private static final long serialVersionUID = -4814651378631732009L;

	public boolean areValidForPolicySetOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((SubjectSession<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForPolicyOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((SubjectSession<java.lang.String>) this,idTrace.toString());
	}
	
	public boolean areValidForRulesOfTrace(String idTrace) {
		
		return TraceEngine.evaluate((SubjectSession<java.lang.String>) this,idTrace.toString());
	}
}
