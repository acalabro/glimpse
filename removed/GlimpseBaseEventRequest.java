package it.cnr.isti.labsedc.glimpse.event;

import java.util.Vector;

public class GlimpseBaseEventRequest <T> extends GlimpseBaseEventAbstract<T> {

	private static final long serialVersionUID = -8212388749967580502L;

	public GlimpseBaseEventRequest(T data, Long timeStamp, String eventName, boolean isException) {
			super(data, timeStamp, eventName, isException);
			// TODO Auto-generated constructor stub
		}
	
	public SubjectSession<String> getSubjectSection() {
	
		Vector<Vector<String>> localHash = (Vector<Vector<String>>) this.data;
		return (SubjectSession<String>) localHash.get(0);
	}
	
	public ResourcesSection<String> getResourcesSection() {
	
		Vector<Vector<String>> localHash = (Vector<Vector<String>>) this.data;
		return (ResourcesSection<String>) localHash.get(1);
	}
	
	public ActionSection<String> getActionsSection() {
		
		Vector<Vector<String>> localHash = (Vector<Vector<String>>) this.data;
		return (ActionSection<String>) localHash.get(2);
	}
	
	public EnvironmentSection<String> getEnvironmentSection() {
	
		Vector<Vector<String>> localHash = (Vector<Vector<String>>) this.data;
		return (EnvironmentSection<String>) localHash.get(3);
	}
}
