package it.cnr.isti.labsedc.glimpse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;

import it.cnr.isti.labsedc.glimpse.BPMN.BPMNPathExplorer;

public abstract class BPMNPathExplorerImpl implements BPMNPathExplorer {

	public List<String[]> lastExploredBPMN;
	
	public List<String[]> getUnfoldedBPMN(Document theBusinessProcessToUnfold) {
		
		//call the software provided by third parties
		
		
		//TODO: REPLACE THIS CODE WITH THE  THIRDY PARTIES COMPONENT IN CHARGE TO EXPLORE THE BPMN AND PROVIDE A LIST 
		lastExploredBPMN = new ArrayList<String[]>();
		lastExploredBPMN.add(new String[]
						{"Check Application","Validate elegibility", "Send rejection letter"});
		lastExploredBPMN.add(new String[]
						{"Check Application","Validate elegibility","Invite for interview",
									"Make interview", "Decide application", "Send rejection letter"});
		lastExploredBPMN.add(new String[]{"Check Application","Validate elegibility",
									"Invite for interview", "Make interview", "Decide application", "Decide fee", "Send acceptance letter"});
		
		//END FAKE EXPLORATION
		
		return lastExploredBPMN;
	}

	public void setUnfoldedBPMN(Vector<String[]> theUnfoldedBusinessProcess) {
		lastExploredBPMN = theUnfoldedBusinessProcess;
	}

}
