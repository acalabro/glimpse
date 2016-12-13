package it.cnr.isti.labsedc.glimpse.rules.generator;

import java.util.Vector;

import it.cnr.isti.labsedc.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.xml.complexEventRule.ComplexEventRuleType;
import it.cnr.isti.labsedc.glimpse.coverage.Activity;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.coverage.Path;

public interface RulesPerPath {
	//ComplexEventRuleActionListDocument generateAllPathsRules(Vector<Activity[]> theUnfoldedBusinessProcess, String idBpmn);
	Vector<Path> generateAllPaths(Vector<Activity[]> theUnfoldedBusinessProcess, String idBpmn);
	ComplexEventRuleType generateRuleForSinglePath(Activity[] anActivitiesSet, String rulesName, String idBPMN, String idPath); 
	//Vector<Path> generatePaths(ComplexEventRuleActionListDocument generatedRules, Vector<Activity[]> theUnfoldedBPMN, String theBPMNidentifier);
	Vector<Path> generatePathsRules(Vector<Path> thePaths);
	ComplexEventRuleActionListDocument instantiateRulesSetForUsersInvolved(Vector<Path> thePathsToInstantiate, Vector<Learner> usersInvolved, String sessionID);
}
