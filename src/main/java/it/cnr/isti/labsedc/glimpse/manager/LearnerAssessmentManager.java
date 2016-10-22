package it.cnr.isti.labsedc.glimpse.manager;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.coverage.Path;
import it.cnr.isti.labsedc.glimpse.storage.DBController;

public abstract class LearnerAssessmentManager extends Thread {

	public abstract Document setBPModel(String xmlMessagePayload) throws ParserConfigurationException, SAXException, IOException;
	public abstract ComplexEventRuleActionListDocument elaborateModel(String xmlMessagePayload, Vector<Learner> vector, String sessionID, String bpmnID);
	public abstract DBController getDBController();
	public abstract Vector<Path> setAllAbsoluteSessionScores(Vector<Path> theGeneratedPath);
	public abstract void setPathCompleted(List<String> learnersID, String idPath, String idBPMN);
	public abstract void computeAndPropagateScores(List<String> learnersID, String idBPMN, String simulationSessionID);
}
