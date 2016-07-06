package it.cnr.isti.labsedc.glimpse.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.net.ntp.TimeStamp;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.learnpad.sim.rest.event.impl.SessionScoreUpdateEvent;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.BPMN.PathExplorer;
import it.cnr.isti.labsedc.glimpse.coverage.Activity;
import it.cnr.isti.labsedc.glimpse.coverage.Bpmn;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.coverage.Path;
import it.cnr.isti.labsedc.glimpse.impl.PathExplorerImpl;
import it.cnr.isti.labsedc.glimpse.impl.RulesPerPathGeneratorImpl;
import it.cnr.isti.labsedc.glimpse.manager.LearnerAssessmentManager;
import it.cnr.isti.labsedc.glimpse.rules.generator.RulesPerPath;
import it.cnr.isti.labsedc.glimpse.storage.DBController;
import it.cnr.isti.labsedc.glimpse.storage.H2Controller;
import it.cnr.isti.labsedc.glimpse.utils.ComputeLearnerScore;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class LearnerAssessmentManagerImpl extends LearnerAssessmentManager {

	private Document theBPMN;
	private PathExplorer bpmnExplorer;
	private RulesPerPath crossRulesGenerator;
	private DBController databaseController;
	private ComplexEventRuleActionListDocument rulesLists;

	public LearnerAssessmentManagerImpl(DBController databaseController) {
		
		//Creation of the BPMNExplorerEngine
		 bpmnExplorer = new PathExplorerImpl();
		 		
		//the instance of DB used
		this.databaseController = databaseController;
		
		//Creation of the PathCrossingRulesGenerator object
		crossRulesGenerator = new RulesPerPathGeneratorImpl();
	}
		
	public void run() {
		
		databaseController.connectToDB();
	}
	
	@Override
	public DBController getDBController() {
		return this.databaseController;
	}
		
	@Override
	public Document setBPModel(String xmlMessagePayload) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		Document dom = docBuilder.parse(new InputSource(new StringReader(xmlMessagePayload)));
		this.theBPMN = dom;
		return dom;
	}

	@Override
	public ComplexEventRuleActionListDocument elaborateModel(String xmlMessagePayload, Vector<Learner> usersInvolved, String sessionID, String bpmnID) {
		
		try {
			theBPMN = setBPModel(xmlMessagePayload);
			
			if (!databaseController.checkIfBPHasBeenAlreadyExtracted(bpmnID)) {
				
				Date now = new Date();
				
				Vector<Activity[]> theUnfoldedBPMN = bpmnExplorer.getUnfoldedBPMN(theBPMN);
				
				Bpmn newBpmn = new Bpmn(bpmnID,now,0, 0, theUnfoldedBPMN.size());
				
				//rules for coverage
				Vector<Path> theGeneratedPath = crossRulesGenerator.generatePathsRules(
																	crossRulesGenerator.generateAllPaths(theUnfoldedBPMN, newBpmn.getId()));
				
				//rules for other kpi calculation
				ComplexEventRuleActionListDocument rulesForKPI = 
						KpiRulesGenerator.generateAll(usersInvolved, sessionID, bpmnID, theUnfoldedBPMN);
				
				
				System.out.println();
				theGeneratedPath = setAllAbsoluteSessionScores(theGeneratedPath);
				
				this.rulesLists = crossRulesGenerator.instantiateRulesSetForUsersInvolved(
						databaseController.savePathsForBPMN(theGeneratedPath),usersInvolved, sessionID);
				
				newBpmn.setAbsoluteBpScore(ComputeLearnerScore.absoluteBP(theGeneratedPath));
				
				databaseController.saveBPMN(newBpmn);
			} else {
				this.rulesLists = crossRulesGenerator.instantiateRulesSetForUsersInvolved(
						databaseController.getBPMNPaths(bpmnID),
						usersInvolved, sessionID);
			}			
		} catch (ParserConfigurationException | SAXException | IOException e ) {
			e.printStackTrace();
			DebugMessages.println(TimeStamp.getCurrentTime(), 
					this.getClass().getSimpleName(),e.getCause().toString());
			DebugMessages.println(TimeStamp.getCurrentTime(), 
					this.getClass().getSimpleName(),"The message contains an INVALID BPMN");
		}
		return rulesLists;
	}

	@Override
	public Vector<Path> setAllAbsoluteSessionScores(Vector<Path> theGeneratedPath) {

		for (int i =0; i< theGeneratedPath.size(); i++) {
			theGeneratedPath.get(i).setAbsoluteSessionScore(ComputeLearnerScore.absoluteSession(theGeneratedPath.get(i).getActivities()));
		}
		return theGeneratedPath;
		
	}

	@Override
	public void computeAndSaveScores(List<String> learnersID, String idBPMN, String idPath, SessionScoreUpdateEvent sessionScore) {
		
		//TODO:
		//calculate all the scores defined within package eu.learnpad.simulator.mon.coverage.ScoreType;
		
		int pathsCardinality = databaseController.getBPMNPathsCardinality(idBPMN);
		
		for(int i = 0; i<learnersID.size(); i++) {
			
			Date now = new Date();
			
			//save sessionScore
			databaseController.setLearnerSessionScore(learnersID.get(i).toString(), idPath, idBPMN, sessionScore.sessionscore, new java.sql.Date(now.getTime()));
			//RestNotifier.getCoreFacade().notifyScoreUpdateEvent(new ScoreUpdateEvent(timestamp, simulationsessionid, involvedusers, modelsetid, simulationSessionData, processartifactid, user, scoreUpdateName, scoreUpdateValue));
			
			//compute learnerBP SCORE
			float learnerBPScore = ComputeLearnerScore.learnerBP(
					databaseController.getMaxSessionScores(learnersID.get(i), idBPMN)); 
			
			
			Vector<Path> pathsExecutedByLearner = databaseController.getPathsExecutedByLearner(learnersID.get(i), idBPMN); 
			
			//compute relativeBPScore
			float learnerRelativeBPScore = ComputeLearnerScore.learnerRelativeBP(pathsExecutedByLearner);

			//compute coverage percentage
			float learnerCoverage = ComputeLearnerScore.BPCoverage(
					pathsExecutedByLearner,pathsCardinality);

			databaseController.updateBpmnLearnerScores(learnersID.get(i), idBPMN, learnerBPScore, learnerRelativeBPScore, learnerCoverage);
			
			//compute globalScore
			float learnerGlobalScore = ComputeLearnerScore.learnerGlobal(
					databaseController.getLearnerBPMNScores(learnersID.get(i)));
		
			//compute relativeGlobalScore
			float learnerRelativeGlobalScore = ComputeLearnerScore.learnerRelativeGlobal(
					databaseController.getLearnerRelativeBPScores(learnersID.get(i)));
			
			
			//compute absoluteGlobalScore
			float learnerAbsoluteGlobalScore = ComputeLearnerScore.learnerAbsoluteGlobal(
					databaseController.getBPMNAbsoluteScoresExecutedByLearner(learnersID.get(i)));
			
			databaseController.updateLearnerScores(learnersID.get(i), learnerGlobalScore, learnerRelativeGlobalScore, learnerAbsoluteGlobalScore);
				
			//TODO: propagate all the scores?
			
		}
	}
	
	public static void main(String[] args)
	{
		Properties asd = new Properties();
		asd.setProperty("DB_DRIVER", "org.h2.Driver");
		asd.setProperty("DB_CONNECTION", "jdbc:h2:./data/glimpse");
		asd.setProperty("DB_USER", "");
		asd.setProperty("DB_PASSWORD", "");

		H2Controller c2 = new H2Controller(asd);
		c2.connectToDB();
		
		LearnerAssessmentManager test = new LearnerAssessmentManagerImpl(c2);

		
		List<String> ciccio = new ArrayList<>();
		ciccio.add("1");
		
		Map<String, Object> asdasd = new HashMap<String, Object>();
		
		SessionScoreUpdateEvent up = new SessionScoreUpdateEvent(
				System.currentTimeMillis(),
				"simulationsessionid",
				ciccio,
				"modelsetid",
				asdasd,
				"processartifactid",
				"user",
				new Long(30));
		
		test.computeAndSaveScores(ciccio, "a23748293649", "a23748293649-1",up);
		
	}
}
