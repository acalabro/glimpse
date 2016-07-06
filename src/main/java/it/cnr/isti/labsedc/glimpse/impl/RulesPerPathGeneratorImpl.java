package it.cnr.isti.labsedc.glimpse.impl;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;
import it.cnr.isti.labsedc.glimpse.coverage.Activity;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.coverage.Path;
import it.cnr.isti.labsedc.glimpse.rules.RuleElements;
import it.cnr.isti.labsedc.glimpse.rules.generator.RulesPerPath;
import it.cnr.isti.labsedc.glimpse.utils.ComputeLearnerScore;

import java.util.Vector;

import org.apache.xmlbeans.XmlException;

public class RulesPerPathGeneratorImpl implements RulesPerPath {

	ComplexEventRuleActionListDocument rulesToLoad;
	
	
	@Override
	public Vector<Path> generateAllPaths(
			Vector<Activity[]> theUnfoldedBusinessProcess, String idBpmn) {
		
		Vector<Path> thePathOfTheBPMN = new Vector<Path>();
		
		for (int i =0; i<theUnfoldedBusinessProcess.size();i++) {
					
			Path theCompletePathObject = new Path(idBpmn + "-" + i, idBpmn, ComputeLearnerScore.absoluteSession(theUnfoldedBusinessProcess.get(i)),
													"", theUnfoldedBusinessProcess.get(i));
			thePathOfTheBPMN.add(theCompletePathObject);
		}	
		return thePathOfTheBPMN;
	}
	
	@Override
	public Vector<Path> generatePathsRules(Vector<Path> thePaths) {

		Vector<Path> local = thePaths;
		ComplexEventRuleType aInsert = ComplexEventRuleType.Factory.newInstance();
		
		for(int i = 0; i<local.size(); i++) {
			
			ComplexEventRuleType generated = generateRuleForSinglePath(local.get(i).getActivities(), local.get(i).getId(), local.get(i).getIdBpmn().toString(), local.get(i).getId());
			aInsert.setRuleBody(generated.getRuleBody());
			aInsert.setRuleName(generated.getRuleName());
			aInsert.setRuleType(generated.getRuleType());
			local.get(i).setPathRule(aInsert.xmlText());
		}
		return local;
	}
	
	@Override
	public ComplexEventRuleType generateRuleForSinglePath(
			Activity[] anActivitiesSet, String rulesName, String idBPMN, String idPath) {

		ComplexEventRuleType aInsert = ComplexEventRuleType.Factory.newInstance();
		aInsert.setRuleName(rulesName);
		aInsert.setRuleType("drools");
				
		String concat = "";
		for(int j = 0; j<anActivitiesSet.length; j++) {
			
			if (j == 0) {
				concat = "\t\t\t$"+j+"Event : GlimpseBaseEventBPMN("+
						"this.isConsumed == false, this.getEvent().simulationsessionid == \"##SESSIONIDPLACEHOLDER##\""
						+", this.getEvent().involvedusers.get(0).toString() == \"##USERSINVOLVEDIDS##\""
						+", this.getEvent.type == EventType.TASK_END.toString()"
						+", this.isException == false"
						+", this.getEventName == \"" + anActivitiesSet[j].getName() +"\");\n";
			} else {
			
				if (j == anActivitiesSet.length-1) {
					
					concat +="\t\t\t$"+j+"Event : GlimpseBaseEventBPMN(" +
							"this.isConsumed == false, this.getEvent().simulationsessionid == \"##SESSIONIDPLACEHOLDER##\""
							+", this.getEvent().involvedusers.get(0).toString() == \"##USERSINVOLVEDIDS##\""
							+", this.getEvent.type == EventType.SESSION_SCORE_UPDATE.toString()"
							+", this.isException == false"
							+", this after $" + (j-1) + "Event);\n";
						
					concat +="\t\t\t$"+(j+1)+"Event : GlimpseBaseEventBPMN(" +
							"this.isConsumed == false, this.getEvent().simulationsessionid == \"##SESSIONIDPLACEHOLDER##\""
							+", this.getEvent().involvedusers.get(0).toString() == \"##USERSINVOLVEDIDS##\""
							+", this.getEvent.type == EventType.TASK_END.toString()"
							+", this.isException == false"
							+", this.getEventName == \"" + anActivitiesSet[j].getName() +"\""
							+", this after $" + (j) + "Event);\n";
					} else {
						concat +="\t\t\t$"+(j)+"Event : GlimpseBaseEventBPMN(" +
							"this.isConsumed == false, this.getEvent().simulationsessionid == \"##SESSIONIDPLACEHOLDER##\""
							+", this.getEvent().involvedusers.get(0).toString() == \"##USERSINVOLVEDIDS##\""
							+", this.getEvent.type == EventType.TASK_END.toString()"
							+", this.isException == false"
							+", this.getEventName == \"" + anActivitiesSet[j].getName() +"\""
							+", this after $" + (j-1) + "Event);\n";
					}			
			}			
		}
		aInsert.setRuleBody(RuleElements.getHeader(aInsert.getRuleName(),  "java") +
				RuleElements.getWhenClause() + 
				concat + 
				RuleElements.getThenClauseForCoverageWithLearnersScoreUpdateAndProcessStartNotification(anActivitiesSet, idBPMN, idPath) +
				RuleElements.getEnd());
		return aInsert;
	}

/*	@Override
	public Vector<Path> generatePaths(ComplexEventRuleActionListDocument generatedRules, Vector<Activity[]> theUnfoldedBPMN, String theBPMNidentifier) {
		
		Vector<Path> thePathOfTheBPMN = new Vector<Path>();
		
		for (int i =0; i<theUnfoldedBPMN.size();i++) {
					
			Path theCompletePathObject = new Path(theUnfoldedBPMN.get(i) + "-" + i, theBPMNidentifier, ComputeScore.absoluteSession(theUnfoldedBPMN.get(i)),
													generatedRules.getComplexEventRuleActionList().getInsertArray()[i].toString(), theUnfoldedBPMN.get(i));
			thePathOfTheBPMN.add(theCompletePathObject);
		}	
		return thePathOfTheBPMN;
	}*/
	
	@Override
	public ComplexEventRuleActionListDocument instantiateRulesSetForUsersInvolved(
								Vector<Path> thePathsToInstantiate,
								Vector<Learner> usersInvolved, String sessionID) {
		
		rulesToLoad = ComplexEventRuleActionListDocument.Factory.newInstance();
		
		String updatedPath;
		ComplexEventRuleType[] rules = new ComplexEventRuleType[thePathsToInstantiate.size()];
		
		for (int i = 0; i<thePathsToInstantiate.size(); i++) {
			
			updatedPath = thePathsToInstantiate.get(i).getPathRule().replaceAll("##SESSIONIDPLACEHOLDER##", sessionID);
			
			String usersInvolvedText = "";
			String usersInvolvedList = "";
			
			if (usersInvolved.size() > 1) {
				for (int j=0; j< usersInvolved.size()-1;j++) {
					usersInvolvedText += usersInvolved.get(j).getId() + "\" || this.getEvent().involvedusers.get(##I##).toString() == \"";
					usersInvolvedList += usersInvolved.get(j).getId() + ","; 
					usersInvolvedText = usersInvolvedText.replaceAll("##I##", String.valueOf(j+1));
				}
				usersInvolvedText += usersInvolved.get(usersInvolved.size()-1).getId();
				usersInvolvedList +=usersInvolved.get(usersInvolved.size()-1).getId();
			}
			else {
				usersInvolvedText = usersInvolved.get(0).getId();				
				usersInvolvedList = usersInvolved.get(0).getId() + "\");}}";
			}
			
			updatedPath = updatedPath.replaceAll("##USERSINVOLVEDIDS##", usersInvolvedText);
			updatedPath = updatedPath.replaceAll("##LEARNERSINVOLVEDID##", usersInvolvedList);
			
			try {
				
				ComplexEventRuleType rule = ComplexEventRuleType.Factory.parse(updatedPath);
				rules[i]= rule;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}
		rulesToLoad.addNewComplexEventRuleActionList().setInsertArray(rules);
		return rulesToLoad;
	}
}
