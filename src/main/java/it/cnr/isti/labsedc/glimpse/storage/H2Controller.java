package it.cnr.isti.labsedc.glimpse.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

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

public class H2Controller implements DBController {
	 
	 private static Connection conn;
	 private Properties connectionProp;
	 private PreparedStatement preparedStmt;
	 private ResultSet resultsSet;
	 
	public H2Controller(Properties databaseConnectionProperties) {
			connectionProp = databaseConnectionProperties;
			
	}
		    	
	public boolean connectToDB() {
		try {
			DebugMessages.print(System.currentTimeMillis(),H2Controller.class.getSimpleName(),
					"Connection to H2 DB instance: " + connectionProp.getProperty("DB_CONNECTION"));
			         Class.forName(connectionProp.getProperty("DB_DRIVER"));
			         conn = DriverManager.getConnection(
			        		 connectionProp.getProperty("DB_CONNECTION"),
			        		 connectionProp.getProperty("DB_USER"),
			        		 connectionProp.getProperty("DB_PASSWORD"));
					 DebugMessages.ok(); 
					 
		} catch (ClassNotFoundException | SQLException e) {
			DebugMessages.println(System.currentTimeMillis(),
					H2Controller.class.getSimpleName(),
					"Could not connect to db " + connectionProp.getProperty("DB_CONNECTION") + "\n check if db is not already in used or locked by previous instance.");
			return false;
		}
		return true;
	}

	
	public boolean disconnectFromDB() {
		return false;
	}
	
	
	public void cleanDB() {
		boolean done = false;
		String query = "delete glimpse.BPMN; delete glimpse.BPMN_LEARNER; "
						+ "delete glimpse.CATEGORY; delete glimpse.LEARNER; "
						+ "delete glimpse.PATH; delete glimpse.PATH_LEARNER;"
						+ " delete glimpse.ROOM";
		try {
			DebugMessages.print(System.currentTimeMillis(), 
								this.getClass().getSimpleName(), "Cleaning DB");
			preparedStmt = conn.prepareStatement(query);
			done = preparedStmt.execute(); 
			DebugMessages.ok();
		} catch (SQLException e) {
			System.err.println("Exception during cleanDB " + done);
			System.err.println(e.getMessage());
		}
	}
	
	
	public Vector<Path> getBPMNPaths(String idBPMN) {
		String query = "select * from glimpse.path where id_bpmn = \'"+idBPMN+"';";
		Vector<Path> retrievedPath = new Vector<Path>();
		
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
		            
			while ( resultsSet.next() ) {
				retrievedPath.add(new Path(resultsSet.getString("id_path"),
									resultsSet.getString("id_bpmn"),
									resultsSet.getFloat("absolute_session_score"),
									resultsSet.getString("path_rule")));
            }
            DebugMessages.println(
					System.currentTimeMillis(), 
					this.getClass().getSimpleName(),
					"BPMN paths loaded from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNPaths ");
			System.err.println(e.getMessage());
		}
        return retrievedPath;
	}

	
	public float getLearnerSessionScore(String idLearner, String idPath, String idBPMN) {
		return 0;
	}
	
	
	public int setLearnerSessionScore(String idLearner, String idPath, String idBPMN, float sessionScore, java.sql.Date scoreUpdatingDate) {
	      String query = " insert into glimpse.path_learner (id_learner, id_path, id_bpmn, session_score, execution_date)"
	    	        + " values (?, ?, ?, ?, ?) ";
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, idLearner);
			preparedStmt.setString(2, idPath);
		    preparedStmt.setString(3,idBPMN);
		    preparedStmt.setFloat(4, sessionScore);
		    preparedStmt.setDate(5,scoreUpdatingDate);

		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			return 1;
		}  
		DebugMessages.println(System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"PathLearner SessionScore Saved");
		return 0;
	}
	
	
	public int saveBPMN(Bpmn theBPMN) {

	      String query = " insert into glimpse.bpmn (id_bpmn, extraction_date, id_category, absolute_bp_score, paths_cardinality)"
	    	        + " values (?, ?, ?, ?, ?) ";
	    	 
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, theBPMN.getId());
		    preparedStmt.setDate(2, new java.sql.Date(theBPMN.getExtractionDate().getTime()));
		    preparedStmt.setInt(3,theBPMN.getIdCategory());
		    preparedStmt.setFloat(4, theBPMN.getAbsoluteBpScore());
		    preparedStmt.setInt(5, theBPMN.getPathsCardinality());

		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			return 1;
		}  
		DebugMessages.println(System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"BPMN Saved");
		return 0;
	}

	
	public boolean updateBpmn(int theBPMNid, Bpmn theBpmnToUpdate) {
		return false;
	}

	
	public int saveCategory(Category theCategory) {
		return 0;
	}

	
	public Category getCategory(int theCategoryID) {
		return null;
	}

	
	public boolean updateCategory(int theCategoryid, Category theCategoryToUpdate) {
		return false;
	}

	
	public int saveLearnerProfile(Learner theLearner) {
		String query = "insert into glimpse.learner"
				+ "(id_learner, id_role, global_score, relative_global_score, absolute_global_score)"
    	        + " values (?, ?, ?, ?, ?)";
    	 
	try {
		preparedStmt = conn.prepareStatement(query);
	    preparedStmt.setString(1, theLearner.getId());
	    preparedStmt.setInt(2, theLearner.getIdRole());
	    preparedStmt.setFloat(3,theLearner.getGlobalScore());
	    preparedStmt.setFloat(4,theLearner.getRelativeGlobalScore());
	    preparedStmt.setFloat(5,theLearner.getAbsolute_global_score());

	    // execute the prepared statement
	    preparedStmt.execute();
	} catch (SQLException e) {
		return 1;
	}  
	DebugMessages.println(
			System.currentTimeMillis(), 
			this.getClass().getSimpleName(),
			"Learner profile created and saved on database.");
	return 0;
	}

	
	public Learner getLearner(String idLearner) {
		String query = "select * from glimpse.learner where id_learner = \'"+idLearner+"';";
		Learner theLearnerGathered = null;
		
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
				theLearnerGathered =  new Learner(resultsSet.getString("id_learner"),
									resultsSet.getInt("id_role"),
									resultsSet.getFloat("global_score"),
									resultsSet.getFloat("relative_global_score"),
									resultsSet.getFloat("absolute_global_score"));
            DebugMessages.println(
					System.currentTimeMillis(), 
					this.getClass().getSimpleName(),
					"Learner gathered from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getLearner ");
			System.err.println(e.getMessage());
		}
        return theLearnerGathered;
	}

	
	public boolean updateLearner(String idLearner, Learner theLearnerToUpdate) {
		return false;
	}

	
	public int savePath(Path thePath) {
		 String query = " insert into glimpse.path (id_path, id_bpmn, absolute_session_score, path_rule)"
	    	        + " values (?, ?, ?, ?)";
	    	 
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1,thePath.getId());
		    preparedStmt.setString(2, thePath.getIdBpmn());
		    preparedStmt.setFloat(3,thePath.getAbsoluteSessionScore());
		    preparedStmt.setString(4, thePath.getPathRule());

		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			return 1;
		}  
		DebugMessages.println(System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"Path Saved");
		return 0;
	}

	
	public Path getPath(String thePathID) {
		return null;
	}

	
	public boolean updatePath(String thePathId, Path thePathToUpdate) {
		return false;
	}

	
	public int saveRole(Role theRole) {
		return 0;
	}

	
	public Role getRole(int theRoleID) {
		return null;
	}

	
	public boolean updateRole(int theRoleId, Role theRoleToUpdate) {
		return false;
	}

	
	public int saveTopic(Topic theTopic) {
		return 0;
	}

	
	public Topic getTopic(int theTopicID) {
		return null;
	}

	
	public boolean updateTopic(int theTopicId, Topic theTopicToUpdate) {
		return false;
	}

	
	public boolean checkIfBPHasBeenAlreadyExtracted(String idBPMN) {
		String query = "select * from glimpse.path where id_bpmn = \'"+idBPMN+"';";
			
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.first()) { 
				DebugMessages.println(
						System.currentTimeMillis(), 
						this.getClass().getSimpleName(),
						"The BPMN has been already extracted, loading values");
				return true; 
				} 				
			}	catch(SQLException asd) {
				System.err.println("Exception during checkIfBPHasBeenAlreadyExtracted ");
				return false;
			}
		return false;
	}

	
	public Activity[] getAllDistinctActivityOFaBPMN(Bpmn theBpmn) {
		return null;
	}

	
	public Bpmn getBPMN(int theBPMNid, String learnpad_bpmn_id) {
		return null;
	}

	
	public float getLearnerBPScore(String idLearner, String idBPMN) {
		return 0;
	}

	
	public float getLearnerRelativeBPScore(String idLearner, String idBPMN) {
		
		return 0;
	}

	
	public int setLearnerBPScore(String idLearner, String idBPMN, float BPScore) {
		 String query = " insert into glimpse.bpmn_learner (id_learner, id_bpmn, bp_score, relative_bp_score, bp_coverage)"
	    	        + " values (?, ?, ?, ?. ?)";
	    	 
		try {
			preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setString(1, idLearner);
		    preparedStmt.setString(2,idBPMN);
		    preparedStmt.setFloat(3, BPScore);
		    preparedStmt.setFloat(4, 0f);
		    preparedStmt.setFloat(5, 0f);

		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			return 1;
		}  
		DebugMessages.println(System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"learnerBPScore Updated");
		return 0;
	}

	
	public float getLearnerBPCoverage(String idLearner, String idBPMN) {
		return 0;
	}

	
	public int setLearnerBPCoverage(String idLearner, String idBPMN, float BPCoverage) {
		return 0;
	}

	
	public Vector<Learner> getOrSetLearners(List<String> learnersIDs) {
		Vector<Learner> learners = new Vector<Learner>();
		String query;
		Learner aLearner;
		try {
			for (int i = 0; i<learnersIDs.size(); i++) {
				query = "select * from glimpse.learner where id_learner = \'"+learnersIDs.get(i)+"';";
					
						preparedStmt = conn.prepareStatement(query);
						resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							learners.add(new Learner(
								resultsSet.getString("id_learner"),
								Integer.parseInt(resultsSet.getString("id_role")),
								Float.parseFloat(resultsSet.getString("global_score")),
								Float.parseFloat(resultsSet.getString("relative_global_score")),
								Float.parseFloat(resultsSet.getString("absolute_global_score"))));
							DebugMessages.println(System.currentTimeMillis(),this.getClass().getSimpleName(),"Learner found");
							}
						else {
							aLearner = new Learner(learnersIDs.get(i),0,0.0f,0.0f,0.0f);
							learners.add(aLearner);
							saveLearnerProfile(aLearner);
							}	 
						}
		} catch (SQLException e) {
			System.err.println("Exception during getLearners");
			System.err.println(e.getMessage());
		}
		return learners;	
	}

	
	public Vector<Path> savePathsForBPMN(Vector<Path> vector) {

		for (int i = 0; i<vector.size(); i++) {
			savePath(vector.get(i));
		}
		return vector;
	}

	
	public Vector<Path> getPathsExecutedByLearner(String learnerID, String idBPMN) {
		String query = "SELECT * FROM glimpse.path where id_path IN( "
				+ "SELECT distinct id_path FROM glimpse.path_learner where id_learner = \'" +
				learnerID +	"')";
		Vector<Path> retrievedPath = new Vector<Path>();
		
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
			while ( resultsSet.next() ) {
				retrievedPath.add(new Path(resultsSet.getString("id_path"),
									resultsSet.getString("id_bpmn"),
									resultsSet.getFloat("absolute_session_score"),
									resultsSet.getString("path_rule")));
            }
            DebugMessages.println(
					System.currentTimeMillis(), 
					this.getClass().getSimpleName(),
					"Executed paths loaded from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getPathsExecutedByLearner ");
			System.err.println(e.getMessage());
		}
        return retrievedPath;
	}

	
	public void setLearnerGlobalScore(String learnerID, float learnerGlobalScore) {
		 String query = " update glimpse.learner set global_score = "+
				 			learnerGlobalScore + ";";
	    	 
		try {
			preparedStmt = conn.prepareStatement(query);
		    
		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			System.err.println("Exception during setLearnerGlobalScore ");
			System.err.println(e.getMessage());
		}  
		DebugMessages.println(System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"GlobalScore Updated");
		
	}

	
	public void setLearnerRelativeGlobalScore(String learnerID, float learnerRelativeGlobalScore) {
		 String query = " update glimpse.learner set relative_global_score = "+
				 learnerRelativeGlobalScore + ";";
	 
		try {
			preparedStmt = conn.prepareStatement(query);

			// execute the prepared statement
			preparedStmt.execute();
		} catch (SQLException e) {
		}
		DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
				"learnerRelativeGlobalScore Updated");
	}
	
	
	public void setLearnerAbsoluteGlobalScore(String learnerID, float absoluteGlobalScore) {
		 String query = " update glimpse.learner set absolute_global_score = "+
				 absoluteGlobalScore + ";";
	 
		try {
			preparedStmt = conn.prepareStatement(query);

			// execute the prepared statement
			preparedStmt.execute();
		} catch (SQLException e) {
		}
		DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
				"absoluteGlobalScore Updated");
	}

	
	public Vector<Float> getLearnerBPMNScores(String learnerID) {
		String query = "SELECT bp_score " + " FROM glimpse.bpmn_learner"
				+ " where id_learner = \'" + learnerID + "';";
		Vector<Float> retrievedScores = new Vector<Float>();
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			while (resultsSet.next()) {
				retrievedScores.add(resultsSet.getFloat("bp_score"));
				}
			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(), "BPMN scores retrieved");
		} catch (SQLException e) {
			System.err.println("Exception during getLearnerBPMNScores");
			System.err.println(e.getMessage());
		}
		return retrievedScores;
	}

	
	public int setLearnerRelativeBPScore(String idLearner, String idBPMN, float relativeBPScore) {
		return 0;
	}

	
	public float getLearnerGlobalScore(String learnerID) {
		return 0;
	}

	
	public float getLearnerRelativeGlobalScore(String learnerID) {
		return 0;
	}

	
	public float setLearnerAbsoluteGlobalScore(String learnerID) {
		return 0;
	}

	
	public Vector<Float> getLearnerRelativeBPScores(String learnerID) {
		String query = "SELECT relative_bp_score "
				+ " FROM glimpse.bpmn_learner" 
				+ " where id_learner = '" + learnerID +
				"'";
		Vector<Float> retrievedScores = new Vector<Float>();
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
			while ( resultsSet.next() ) {
					retrievedScores.add(resultsSet.getFloat("relative_bp_score"));
			}
			DebugMessages.println(System.currentTimeMillis(), 
					this.getClass().getSimpleName(),"BPMN scores retrieved");
		} catch (SQLException e) {
			System.err.println("Exception during getLearnerBPMNScores");
			System.err.println(e.getMessage());
		}
		return retrievedScores;
	}

	
	public Vector<Float> getBPMNScoresExecutedByLearner(String learnerID) {
		String query = "SELECT bpmn_learner.bp_score"
				+ " FROM glimpse.bpmn, glimpse.bpmn_learner" + " where bpmn_learner.id_learner = '" + learnerID + "'";

		Vector<Float> retrievedScores = new Vector<Float>();

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();

			while (resultsSet.next()) {
				retrievedScores.add(resultsSet.getFloat("bp_score"));
			}
			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Extracted bpmn loaded from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNExecutedByLearner");
			System.err.println(e.getMessage());
		}
		return retrievedScores;
	}

	
	public Vector<Float> getMaxSessionScores(String learnerID, String idBPMN) {
		
		String query = "SELECT max(session_score)"
				+ " FROM glimpse.path_learner"
				+ " where id_learner = '"+ learnerID
				+ "' and EXISTS (select distinct id_path from glimpse.path_learner where id_bpmn = '" + idBPMN + "') group by id_path";

		Vector<Float> retrievedScores = new Vector<Float>();

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();

			while (resultsSet.next()) {
				retrievedScores.add(resultsSet.getFloat("MAX(session_score)"));
			}
			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Selected session_score");
		} catch (SQLException e) {
			System.err.println("Exception during getMaxSessionScores");
			System.err.println(e.getMessage());
		}
		return retrievedScores;
	}

	
	public Vector<Float> getBPMNAbsoluteScoresExecutedByLearner(String learnerID) {
		String query = "SELECT bpmn.absolute_bp_score"
				+ " FROM glimpse.bpmn, glimpse.bpmn_learner"
				+ " where bpmn_learner.id_learner = '" + learnerID + "'";

		Vector<Float> retrievedScores = new Vector<Float>();

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.wasNull() == false) {  
				while (resultsSet.next()) {
					retrievedScores.add(resultsSet.getFloat("absolute_bp_score"));
				}
			} else {
				retrievedScores.add(0f);
			}
			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Selected absolute_bp_scores");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNAbsoluteScoresExecutedByLearner");
			System.err.println(e.getMessage());
		}
		return retrievedScores;
	}

	
	public void updateLearnerScores(String learnerID, float learnerGlobalScore, 
			float learnerRelativeGlobalScore, float learnerAbsoluteGLobalScore) {
		String query;
		Learner aLearner;
		try {
			query = "select * from glimpse.learner where id_learner = \'"+learnerID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.learner set global_score = "+
							learnerGlobalScore + ",  relative_global_score = "+
									learnerRelativeGlobalScore + ", absolute_global_score = "+
									 learnerAbsoluteGLobalScore + " where id_learner = \'"+
									learnerID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Learner Scores updated");
						}
						else {
							aLearner = new Learner(learnerID,0,learnerGlobalScore,learnerRelativeGlobalScore,learnerAbsoluteGLobalScore);
							saveLearnerProfile(aLearner);
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateLearnerScores");
			System.err.println(e.getMessage());
		}
		
	}

	
	public void updateBpmnLearnerScores(String learnerID, String idBPMN, float learnerBPScore,
			float learnerRelativeBPScore, float learnerCoverage) {
		String query;
		try {
			query = "select * from glimpse.bpmn_learner where id_learner = \'"+learnerID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.bpmn_learner set bp_score = "+
							learnerBPScore + ",  relative_bp_score = "+
									learnerRelativeBPScore + ", bp_coverage = "+
									 learnerCoverage + " where id_learner = \'"+
									learnerID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"BPMN scores updated");
						}
						else {
							 query = " insert into glimpse.bpmn_learner (id_learner, id_bpmn, bp_score, relative_bp_score, bp_coverage)"
						    	        + " values (?, ?, ?, ?, ?)";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, learnerID);
							    preparedStmt.setString(2,idBPMN);
							    preparedStmt.setFloat(3, learnerBPScore);
							    preparedStmt.setFloat(4, learnerRelativeBPScore);
							    preparedStmt.setFloat(5, learnerCoverage);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateBpmnLearnerScores");
			System.err.println(e.getMessage());
		}
		
	}

	
	public int getBPMNPathsCardinality(String idBPMN) {
		String query = "SELECT COUNT(*) FROM glimpse.path where id_bpmn = \'"+idBPMN+"';";
		int result = 0;

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
			
			while ( resultsSet.next() ) {
				result = resultsSet.getInt("COUNT(*)");
            }
            DebugMessages.println(
					System.currentTimeMillis(), 
					this.getClass().getSimpleName(),
					"Paths counted ");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNPathsCardinality ");
			System.err.println(e.getMessage());
		}
		return result;
	}

	
	public Float getAbsoluteBPScore(String idBPMN) {
		String query = "select ABSOLUTE_BP_SCORE from glimpse.bpmn where id_bpmn = \'"+idBPMN+"';";
		float theAbsBPScore = 0f;
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.first()) { 
				DebugMessages.println(
						System.currentTimeMillis(), 
						this.getClass().getSimpleName(),
						"GetAbsoluteBPScore");
				theAbsBPScore = resultsSet.getFloat("ABSOLUTE_BP_SCORE"); 
				} 				
			}	catch(SQLException asd) {
				System.err.println("Exception during getAbsoluteBPScore ");
				return 0f;
			}
		return theAbsBPScore;
	}
	
	
	public Float getLastPathAbsoluteSessionScoreExecutedByLearner(String idLearner, String idBPMN) {
		
		String query = "SELECT distinct ID_PATH, EXECUTION_DATE FROM glimpse.path_learner where ID_LEARNER = \'"+idLearner+"' and ID_BPMN = \'"+idBPMN+"' order by execution_date;";
//		String query = "select * from glimpse.path_learner;";
		String idPath = "";
		float theAbsBPScoreExec = 00f;
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 
				DebugMessages.println(
						System.currentTimeMillis(), 
						this.getClass().getSimpleName(),
						"getting LastPathAbsoluteSessionScoreExecutedByLearner");
				System.out.println(resultsSet.next());
				idPath = resultsSet.getString("ID_PATH"); 
				query = "SELECT ABSOLUTE_SESSION_SCORE from glimpse.path where id_path = \'"+idPath+"';";

				preparedStmt = conn.prepareStatement(query);
				resultsSet = preparedStmt.executeQuery();
				resultsSet.next();
				theAbsBPScoreExec = resultsSet.getFloat("ABSOLUTE_SESSION_SCORE");	
			}	catch(SQLException asd) {
				System.err.println("Exception during getLastPathAbsoluteSessionScoreExecutedByLearner ");
				return 0f;
			}
		return theAbsBPScoreExec;
	}

	
	public Room getRoomStatus(String roomID) {
		
		String query = "SELECT *"
				+ " FROM glimpse.room"
				+ " where room.id_room = '" + roomID + "'";

		Room retrieveddata = null;

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.wasNull() == false) {  
				while (resultsSet.next()) {
							retrieveddata = new Room(
									roomID,
									resultsSet.getFloat("temperature"),
									resultsSet.getFloat("occupancy"),
									resultsSet.getFloat("humidity"),
									resultsSet.getFloat("noise"),
									resultsSet.getFloat("socketpower"),
									resultsSet.getFloat("lightpower"),
									resultsSet.getDate("updateDateTime"));
				}
			}
//			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
//					"Selected data related to the room");
		} catch (SQLException e) {
			System.err.println("Exception during getRoomStatus");
		}
		return retrieveddata;
	}

	
	public void updateTemperature(String roomID, Float temperature) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set temperature = "+
							temperature + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Temperature updated");
							DebugMessages.line();
						}
						else {
							query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
					    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, temperature);
							    preparedStmt.setFloat(3, 0f);
							    preparedStmt.setFloat(4, 0f);
							    preparedStmt.setFloat(5, 0f);
							    preparedStmt.setFloat(6, 0f);
							    preparedStmt.setFloat(7, 0f);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateTemperature");
			System.err.println(e.getMessage());
		}
		
	}

	
	public void updateOccupancy(String roomID, Float occupancy) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set occupancy = "+
							occupancy + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Occupancy updated");
							DebugMessages.line();
						}
						else {
							query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
					    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, 0f);
							    preparedStmt.setFloat(3, occupancy);
							    preparedStmt.setFloat(4, 0f);
							    preparedStmt.setFloat(5, 0f);
							    preparedStmt.setFloat(6, 0f);
							    preparedStmt.setFloat(7, 0f);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateOccupancy");
			System.err.println(e.getMessage());
		}
	}
	
	
	public void updateHumidity(String roomID, Float humidity) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set humidity = "+
							humidity + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Humidity updated");
							DebugMessages.line();
						}
						else {
							query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
					    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, 0f);
							    preparedStmt.setFloat(3, 0f);
							    preparedStmt.setFloat(4, humidity);
							    preparedStmt.setFloat(5, 0f);
							    preparedStmt.setFloat(6, 0f);
							    preparedStmt.setFloat(7, 0f);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateHumidity");
			System.err.println(e.getMessage());
		}
				
	}

	
	public void updateNoise(String roomID, Float noise) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set noise = "+
							noise + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Noise updated");
							DebugMessages.line();
						}
						else {
							query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
					    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, 0f);
							    preparedStmt.setFloat(3, 0f);
							    preparedStmt.setFloat(4, 0f);
							    preparedStmt.setFloat(5, noise);
							    preparedStmt.setFloat(6, 0f);
							    preparedStmt.setFloat(7, 0f);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateNoise");
			System.err.println(e.getMessage());
		}
		
	}

	
	public void updateSocketPower(String roomID, Float socketPower) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set socketpower = "+
									socketPower + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"SocketPower updated");
							DebugMessages.line();
						}
						else {
							 query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
						    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, 0f);
							    preparedStmt.setFloat(3, 0f);
							    preparedStmt.setFloat(4, 0f);
							    preparedStmt.setFloat(5, 0f);
							    preparedStmt.setFloat(6, socketPower);
							    preparedStmt.setFloat(7, 0f);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateSocketPower");
			System.err.println(e.getMessage());
		}	
	}

	
	public void createRoom(String roomID) {
		// TODO Auto-generated method stub
		
	}

	
	public void updateLightPower(String roomID, Float lightPower) {
		String query = "";
		try {
			query = "select * from glimpse.room where id_room = \'"+roomID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update glimpse.room set lightpower = "+
									lightPower + " where id_room = \'"+
									roomID + "';";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"LightPower updated");
							DebugMessages.line();
						}
						else {
							 query = " insert into glimpse.room (id_room, temperature, occupancy, humidity, noise, socketpower, lightpower, updateDateTime)"
						    	        + " values (?, ?, ?, ?, ?, ?, ?, NOW())";
								preparedStmt = conn.prepareStatement(query);

							    preparedStmt.setString(1, roomID);
							    preparedStmt.setFloat(2, 0f);
							    preparedStmt.setFloat(3, 0f);
							    preparedStmt.setFloat(4, 0f);
							    preparedStmt.setFloat(5, 0f);
							    preparedStmt.setFloat(6, 0f);
							    preparedStmt.setFloat(7, lightPower);

							    // execute the prepared statement
							    preparedStmt.execute();
							}	 
		} catch (SQLException e) {
			System.err.println("Exception during updateLightPower");
			System.err.println(e.getMessage());
		}
		
	}

	
	public String setIntrusionStatus(int telegramID, boolean intrusion, boolean intrusion_setbyuser) {
		String query = "";
		String room_id = "0";
		try {
				query = "SELECT *"
					+ " FROM glimpse.smartcampus_user"
					+ " where smartcampus_user.telegram_id = '" + telegramID + "'";

				preparedStmt = conn.prepareStatement(query);
				resultsSet = preparedStmt.executeQuery(); 
				
					if (resultsSet.first()) {
							
						room_id = resultsSet.getString("room_id");
						query = "update glimpse.smartcampus_user set intrusion_mode = "+
								intrusion + " where telegram_id= \'"+
								telegramID + "';";
					 
						preparedStmt = conn.prepareStatement(query);

							// execute the prepared statement
						preparedStmt.execute();
							
						query = "update glimpse.smartcampus_user set intrusion_setbyuser = "+
								intrusion_setbyuser + " where telegram_id= \'"+
								telegramID + "';";
					 
						preparedStmt = conn.prepareStatement(query);

							// execute the prepared statement
						preparedStmt.execute();
							
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Intrusion mode updated by CEP");
							DebugMessages.line();
						}	
					return room_id;
		} catch (SQLException e) {
			System.err.println("Exception during setIntrusionStatus");
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	
	public boolean getIntrusionStatus(int telegramID) {
		boolean status = false;
		try {
			String query = "SELECT *"
					+ " FROM glimpse.smartcampus_user"
					+ " where smartcampus_user.telegram_id = '" + telegramID + "'";

					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery();
					if (resultsSet.first()) {
						
							status = resultsSet.getBoolean("intrusion_mode");
						
							DebugMessages.println(
									System.currentTimeMillis(), 
									this.getClass().getSimpleName(),
									"Intrusion mode retrieved");
							DebugMessages.line();
						}	 
		} catch (SQLException e) {
			System.err.println("Exception during setIntrusionStatus");
			System.err.println(e.getMessage());
		}
		return status;
	}
	
	
	public List<SmartCampusUser> getUsersForTheRoom(String roomID) {
		
		Vector<SmartCampusUser> smartCampusUsers = new Vector<SmartCampusUser>();
		
		try {
			String query = "SELECT *"
					+ " FROM glimpse.smartcampus_user"
					+ " where smartcampus_user.room_id = '" + roomID.toString().toLowerCase() + "' order by smartcampus_user.name";

			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();

				while (resultsSet.next()) {
					smartCampusUsers.add(new SmartCampusUser(
							resultsSet.getInt("id"),
							resultsSet.getString("name"),
							resultsSet.getString("surname"),
							resultsSet.getString("telegram_id"),
							resultsSet.getString("room_id"),
							resultsSet.getBoolean("intrusion_mode"),
							resultsSet.getBoolean("intrusion_setbyuser")));
			}
		} catch (SQLException e) {
		System.err.println("Exception during getUsersForTheRoom");
		System.err.println(e.getMessage());
		}
		return smartCampusUsers;	
	}

	
	public boolean checkIfIamAllowedToUpdateRoomIntrusionStatus(Long id, String roomID) {
		String query = "SELECT *"
				+ " FROM glimpse.smartcampus_user"
				+ " where smartcampus_user.room_id = '" + roomID.toLowerCase() 
				+ "' and smartcampus_user.telegram_id = '" + id.toString() + "'";

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.wasNull() == false) {  
				while (resultsSet.next()) {
					return true;
				}
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Exception during getRoomStatus");
		}
		return false;
	}


	
	public String getRoomIDforTelegramUser(int telegramID) {
		String query = "SELECT *"
				+ " FROM glimpse.smartcampus_user"
				+ " where smartcampus_user.telegram_id = '" + telegramID + "'";

		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.wasNull() == false) {  
				while (resultsSet.next()) {
					return resultsSet.getString("room_id");
				}
			}
		} catch (SQLException e) {
			System.err.println("Exception during getRoomIDforTelegramUser");
		}
		return null;
	}
	
	
}
