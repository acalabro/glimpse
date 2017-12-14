package it.cnr.isti.labsedc.glimpse.storage;

import java.sql.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import it.cnr.isti.labsedc.glimpse.coverage.Activity;
import it.cnr.isti.labsedc.glimpse.coverage.Bpmn;
import it.cnr.isti.labsedc.glimpse.coverage.Category;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.coverage.Path;
import it.cnr.isti.labsedc.glimpse.coverage.Role;
import it.cnr.isti.labsedc.glimpse.coverage.Topic;
import it.cnr.isti.labsedc.glimpse.smartbuilding.Room;
import it.cnr.isti.labsedc.glimpse.smartbuilding.SmartCampusUser;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class InfluxDBController implements DBController {

	private Properties connectionProp;
	private static InfluxDB conn;

	public InfluxDBController(Properties databaseConnectionProperties) {
		connectionProp = databaseConnectionProperties;
		
}
	
	
	public boolean connectToDB() {

			DebugMessages.print(System.currentTimeMillis(),InfluxDBController.class.getSimpleName(),
					"Connection to InfluxDB instance: " + connectionProp.getProperty("DB_CONNECTION"));
			         conn = InfluxDBFactory.connect(
			        		 connectionProp.getProperty("DB_CONNECTION") + ":" + connectionProp.getProperty("DB_PORT"),
			        		 connectionProp.getProperty("DB_USER"),
			        		 connectionProp.getProperty("DB_PASSWORD"));
			         conn.setDatabase(connectionProp.getProperty("DB_NAME"));
					 DebugMessages.ok(); 
		return true;
	}

	
	public boolean disconnectFromDB() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean checkIfBPHasBeenAlreadyExtracted(String idBPMN) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Vector<Path> getBPMNPaths(String idBPMN) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Activity[] getAllDistinctActivityOFaBPMN(Bpmn theBpmn) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Float> getLearnerBPMNScores(String learnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Float> getLearnerRelativeBPScores(String learnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int saveBPMN(Bpmn theBPMN) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Bpmn getBPMN(int theBPMNid, String learnpad_bpmn_id) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateBpmn(int theBPMNid, Bpmn theBpmnToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int saveCategory(Category theCategory) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Category getCategory(int theCategoryID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateCategory(int theCategoryid, Category theCategoryToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int saveLearnerProfile(Learner theLearner) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Learner getLearner(String idLearner) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateLearner(String idLearner, Learner theLearnerToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int savePath(Path thePath) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Path getPath(String thePathID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updatePath(String thePathId, Path thePathToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int saveRole(Role theRole) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Role getRole(int theRoleID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateRole(int theRoleId, Role theRoleToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int saveTopic(Topic theTopic) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Topic getTopic(int theTopicID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean updateTopic(int theTopicId, Topic theTopicToUpdate) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Vector<Learner> getOrSetLearners(List<String> learnersIDs) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Path> getPathsExecutedByLearner(String learnerID, String idBPMN) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Path> savePathsForBPMN(Vector<Path> vector) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public float getLearnerSessionScore(String idLearner, String idPath, String idBpmn) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int setLearnerSessionScore(String idLearner, String idPath, String idBpmn, float sessionScore,
			Date scoreUpdatingDate) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setLearnerGlobalScore(String learnerID, float learnerGlobalScore) {
		// TODO Auto-generated method stub

	}

	
	public float getLearnerGlobalScore(String learnerID) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setLearnerRelativeGlobalScore(String learnerID, float learnerRelativeGlobalScore) {
		// TODO Auto-generated method stub

	}

	
	public float getLearnerRelativeGlobalScore(String learnerID) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setLearnerAbsoluteGlobalScore(String learnerID, float absoluteGlobalScore) {
		// TODO Auto-generated method stub

	}

	
	public float setLearnerAbsoluteGlobalScore(String learnerID) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getLearnerBPScore(String idLearner, String idBPMN) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int setLearnerBPScore(String idLearner, String idBPMN, float BPScore) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getLearnerRelativeBPScore(String idLearner, String idBPMN) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int setLearnerRelativeBPScore(String idLearner, String idBPMN, float relativeBPScore) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getLearnerBPCoverage(String idLearner, String idBPMN) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int setLearnerBPCoverage(String idLearner, String idBPMN, float BPCoverage) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Vector<Float> getBPMNScoresExecutedByLearner(String learnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Float> getMaxSessionScores(String learnerID, String idBPMN) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector<Float> getBPMNAbsoluteScoresExecutedByLearner(String learnerID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void updateLearnerScores(String learnerID, float learnerGlobalScore, float learnerRelativeGlobalScore,
			float learnerAbsoluteGLobalScore) {
		// TODO Auto-generated method stub

	}

	
	public void updateBpmnLearnerScores(String learnerID, String idBPMN, float learnerBPScore,
			float learnerRelativeBPScore, float learnerCoverage) {
		// TODO Auto-generated method stub

	}

	
	public int getBPMNPathsCardinality(String idBPMN) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Float getAbsoluteBPScore(String idBPMN) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Float getLastPathAbsoluteSessionScoreExecutedByLearner(String idLearner, String idBPMN) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void cleanDB() {
		// TODO Auto-generated method stub

	}

	
	public Room getRoomStatus(String roomID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void createRoom(String roomID) {
		// TODO Auto-generated method stub

	}

	
	public void updateTemperature(String roomID, Float temperature) {
		// TODO Auto-generated method stub

	}

	
	public void updateHumidity(String roomID, Float humidity) {
		// TODO Auto-generated method stub

	}

	
	public void updateNoise(String roomID, Float noise) {
		// TODO Auto-generated method stub

	}

	
	public void updateSocketPower(String roomID, Float powerConsumption) {
		// TODO Auto-generated method stub

	}

	
	public void updateOccupancy(String roomID, Float occupancy) {
		// TODO Auto-generated method stub

	}

	
	public void updateLightPower(String roomID, Float lightPower) {
		// TODO Auto-generated method stub

	}

	
	public String setIntrusionStatus(int telegramID, boolean intrusion, boolean intrusion_setbyuser) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean getIntrusionStatus(int telegramID) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean checkIfIamAllowedToUpdateRoomIntrusionStatus(Long telegramID, String roomID) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public List<SmartCampusUser> getUsersForTheRoom(String roomID) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getRoomIDforTelegramUser(int telegramID) {
		// TODO Auto-generated method stub
		return null;
	}

}
