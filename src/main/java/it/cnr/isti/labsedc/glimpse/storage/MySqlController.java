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

public class MySqlController implements DBController {

	private Properties connectionProp;
	private Connection conn;	  
    private PreparedStatement preparedStmt;
    private ResultSet resultsSet;
	
	public MySqlController(Properties databaseConnectionProperties) {
		connectionProp = databaseConnectionProperties;
	}

	
	public boolean connectToDB() {
		String url = "jdbc:mysql://"
				+ connectionProp.getProperty("database.host") +
				":" + connectionProp.getProperty("database.port")+"/";
		
		String dbName = connectionProp.getProperty("database.name");
		String userName = connectionProp.getProperty("username"); 
		String password = connectionProp.getProperty("password");
		try { 
			conn = DriverManager.getConnection(url+dbName,userName,password);
			
			DebugMessages.print(System.currentTimeMillis(),
					MySqlController.class.getSimpleName(),
					"Connection to MySql DB instance: " + connectionProp.getProperty("database.host"));
			DebugMessages.ok();
		} catch (SQLException e) {
			DebugMessages.println(System.currentTimeMillis(),
					MySqlController.class.getSimpleName(),
					"Could not connect to db " + connectionProp.getProperty("database.host"));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public boolean disconnectFromDB() {
		return false;
	}
	
	
	public Vector<Path> getBPMNPaths(String idBPMN) {
		String query = "select * from path where id_bpmn = \'"+idBPMN+"';";
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
					"Extracted paths loaded from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNPaths ");
			System.err.println(e.getMessage());
		}
        return retrievedPath;
	}

	
	public void cleanDB() {
		String query = "delete glimpse.BPMN; delete glimpse.BPMN_LEARNER; "
						+ "delete glimpse.CATEGORY; delete glimpse.LEARNER; "
						+ "delete glimpse.PATH; delete glimpse.PATH_LEARNER;";
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery(); 

		} catch (SQLException e) {
			System.err.println("Exception during cleanDB");
			System.err.println(e.getMessage());
		}
	}
	
	
	public float getLearnerSessionScore(String idLearner, String idPath, String idBPMN) {
		return 0;
	}
	
	
	public int setLearnerSessionScore(String idLearner, String idPath, String idBPMN, float sessionScore, java.sql.Date scoreUpdatingDate) {
	      String query = " insert into path_learner (id_learner, id_path, id_bpmn, session_score, execution_date)"
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
		DebugMessages.println(
				System.currentTimeMillis(), 
				this.getClass().getSimpleName(),
				"PathLearner SessionScore Saved");
		return 0;
	}
	
	
	public int saveBPMN(Bpmn theBPMN) {

	      String query = " insert into bpmn (id_bpmn, extraction_date, id_category, absolute_bp_score, paths_cardinality)"
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
		DebugMessages.println(
				System.currentTimeMillis(), 
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
		String query = "insert into learner"
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
			"Learner Saved");
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
		 String query = " insert into path (id_bpmn, absolute_session_score, path_rule)"
	    	        + " values (?, ?, ?)";
	    	 
		try {
			preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setString(1, thePath.getIdBpmn());
		    preparedStmt.setFloat(2,thePath.getAbsoluteSessionScore());
		    preparedStmt.setString(3, thePath.getPathRule());

		    // execute the prepared statement
		    preparedStmt.execute();
		} catch (SQLException e) {
			return 1;
		}  
		DebugMessages.println(
				System.currentTimeMillis(), 
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
		String query = "select * from path where id_bpmn = \'"+idBPMN+"';";
			
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
		 String query = " insert into bpmn_learner (id_learner, id_bpmn, bp_score, relative_bp_score, bp_coverage)"
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
		DebugMessages.println(
				System.currentTimeMillis(), 
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
				query = "select * from learner where id_learner = \'"+learnersIDs.get(i)+"';";
					
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
		String query = "SELECT * FROM glimpse.path where id IN( "
				+ "SELECT distinct id_path FROM glimpse.path_learner where id_learner = " +
				learnerID +	")";
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
					"Extracted paths loaded from DB");
		} catch (SQLException e) {
			System.err.println("Exception during getBPMNPaths ");
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
		DebugMessages.println(
				System.currentTimeMillis(), 
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
		String query = "SELECT bp_score " + " FROM bpmn_learner"
				+ " where id_learner = " + learnerID + "";
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
				+ " FROM path_learner"
				+ " where id_learner = "+ learnerID
				+ " and EXISTS (select distinct id_path from path_learner where id_bpmn = '" + idBPMN + "') group by id_path";

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
			if (resultsSet.getFetchSize() != 0) {  
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
			query = "select * from learner where id_learner = \'"+learnerID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update learner set global_score = "+
							learnerGlobalScore + ",  relative_global_score = "+
									learnerRelativeGlobalScore + ", absolute_global_score = "+
									 learnerAbsoluteGLobalScore + " where id_learner = "+
									learnerID + ";";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
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
			query = "select * from bpmn_learner where id_learner = \'"+learnerID+"';";
					preparedStmt = conn.prepareStatement(query);
					resultsSet = preparedStmt.executeQuery(); 
						
						if (resultsSet.first()) {
							
							query = "update bpmn_learner set bp_score = "+
							learnerBPScore + ",  relative_bp_score = "+
									learnerRelativeBPScore + ", bp_coverage = "+
									 learnerCoverage + " where id_learner = "+
									learnerID + ";";
						 
							preparedStmt = conn.prepareStatement(query);

								// execute the prepared statement
							preparedStmt.execute();
						}
						else {
							 query = " insert into bpmn_learner (id_learner, id_bpmn, bp_score, relative_bp_score, bp_coverage)"
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
		String query = "SELECT COUNT(*) FROM path where id_bpmn = \'"+idBPMN+"';";
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
						"The BPMN has been already extracted, loading values");
				theAbsBPScore = resultsSet.getFloat(0); 
				} 				
			}	catch(SQLException asd) {
				System.err.println("Exception during checkIfBPHasBeenAlreadyExtracted ");
				return 0f;
			}
		return theAbsBPScore;
	}

	
	public Float getLastPathAbsoluteSessionScoreExecutedByLearner(String idLearner, String idBPMN) {
		
		String query = "SELECT distinct id_path, execution_date FROM glimpse.path_learner where id_learner = \'"+idLearner+"' and IDBPMN = \'"+idBPMN+"' order by execution_date;";
		String idPath;
		float theAbsBPScoreExec = 0f;
		try {
			preparedStmt = conn.prepareStatement(query);
			resultsSet = preparedStmt.executeQuery();
			if (resultsSet.first()) { 
				DebugMessages.println(
						System.currentTimeMillis(), 
						this.getClass().getSimpleName(),
						"getting LastPathAbsoluteSessionScoreExecutedByLearner");
				idPath = resultsSet.getString(0); 
				
				query = "SELECT ABSOLUTE_SESSION_SCORE from glimpse.path where id_path = \'"+idPath+"';";

				preparedStmt = conn.prepareStatement(query);
				resultsSet = preparedStmt.executeQuery();
				theAbsBPScoreExec = resultsSet.getFloat(0);
				
				} 				
			}	catch(SQLException asd) {
				System.err.println("Exception during getLastPathAbsoluteSessionScoreExecutedByLearner ");
				return 0f;
			}
		return theAbsBPScoreExec;
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
		return null;
	}

	
	public boolean checkIfIamAllowedToUpdateRoomIntrusionStatus(Long id, String roomID) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean getIntrusionStatus(int telegramID) {
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