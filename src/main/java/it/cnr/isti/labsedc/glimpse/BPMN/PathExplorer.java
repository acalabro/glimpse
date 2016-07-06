package it.cnr.isti.labsedc.glimpse.BPMN;

import java.util.Vector;

import org.w3c.dom.Document;

import it.cnr.isti.labsedc.glimpse.coverage.Activity;

public interface PathExplorer {
	
	Vector<Activity[]> getUnfoldedBPMN(Document theBusinessProcessToUnfold);
	void setUnfoldedBPMN(Vector<Activity[]> theUnfoldedBusinessProcess);
	
}
