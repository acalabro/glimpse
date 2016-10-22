package it.cnr.isti.labsedc.glimpse.storage;

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.net.ntp.TimeStamp;

import eu.learnpad.sim.rest.event.impl.SessionScoreUpdateEvent;
import it.cnr.isti.labsedc.glimpse.coverage.Learner;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class ScoreTemporaryStorage {

	private static HashMap<String, SessionScoreUpdateEvent> sessionScores;
	private static String sessionID;
	private static SessionScoreUpdateEvent lastScoreUpdateEventSeen = null;
	private static SessionScoreUpdateEvent emptySessionScoreUpdateEvent;

	public ScoreTemporaryStorage(Vector<Learner> theLearnersInvolvedInSession, String sessionID) {
		
		ScoreTemporaryStorage.sessionID = sessionID;
				
		sessionScores = new HashMap<String, SessionScoreUpdateEvent>(theLearnersInvolvedInSession.size());
		emptySessionScoreUpdateEvent = new SessionScoreUpdateEvent(0l, "", null,"",null,"","",0l);
		for (int i = 0; i<theLearnersInvolvedInSession.size(); i++) {
						sessionScores.put(
									theLearnersInvolvedInSession.get(i).getId(), 
									emptySessionScoreUpdateEvent);
		}	
	}
	
	public static String getSessionID() {
		return sessionID;
	}

	public static SessionScoreUpdateEvent getLastScoreUpdateEventSeen() {
		if (lastScoreUpdateEventSeen != null)
			return lastScoreUpdateEventSeen;
		else 
			return emptySessionScoreUpdateEvent;
	}

	public static void setLastScoreUpdateEventSeen(SessionScoreUpdateEvent lastScoreUpdateEventSeen) {
		DebugMessages.print(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getSimpleName(), "Storing LastScoreUpdateEventSeen");
		if (ScoreTemporaryStorage.lastScoreUpdateEventSeen != null && (ScoreTemporaryStorage.lastScoreUpdateEventSeen.timestamp < lastScoreUpdateEventSeen.timestamp)) {
			ScoreTemporaryStorage.lastScoreUpdateEventSeen = lastScoreUpdateEventSeen;
		}
		DebugMessages.ok(); 
	}
	
	public static void setSessionID(String sessionID) {
		ScoreTemporaryStorage.sessionID = sessionID;
	}

	
	public static void setTemporaryLearnerSessionScore(String learnerID, SessionScoreUpdateEvent scoreUpdateEvent) {
		DebugMessages.print(TimeStamp.getCurrentTime(), ScoreTemporaryStorage.class.getSimpleName(), "Storing LearnerSessionScore");
		if (scoreUpdateEvent.timestamp > ScoreTemporaryStorage.sessionScores.get(learnerID).timestamp) {
			ScoreTemporaryStorage.sessionScores.put(learnerID, scoreUpdateEvent);
		}
		DebugMessages.ok();

	}
	
	public static Long getTemporaryLearnerSessionScore(String learnerID) {
		if (ScoreTemporaryStorage.sessionScores.get(learnerID) != null) {
		return ScoreTemporaryStorage.sessionScores.get(learnerID).sessionscore;
		}
		else
			return 0l;
	}
}