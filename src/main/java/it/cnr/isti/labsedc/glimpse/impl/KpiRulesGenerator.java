package it.cnr.isti.labsedc.glimpse.impl;

import java.util.Vector;

import it.cnr.isti.labsedc.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.coverage.Activity;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;

public class KpiRulesGenerator {

	public static ComplexEventRuleActionListDocument generateAll(Vector<Learner> usersInvolved, String sessionID,
			String bpmnID, Vector<Activity[]> theUnfoldedBPMN) {
		
		CaptureAndUpdateSessionScoreForUsers(usersInvolved, sessionID, bpmnID);
		
		
		return null;
	}
	
	public static ComplexEventRuleActionListDocument CaptureAndUpdateSessionScoreForUsers(Vector<Learner> usersInvolved, String sessionID, String bpmnID) {
		//TODO: other rules
		
		return null;
	}
}
