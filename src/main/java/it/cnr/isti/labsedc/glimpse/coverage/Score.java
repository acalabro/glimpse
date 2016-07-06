package it.cnr.isti.labsedc.glimpse.coverage;

public class Score {

	private ScoreType type;
	private float value;

	public Score(ScoreType scoreType, float scoreValue) {
		this.type = scoreType;
		this.value = scoreValue;
	}
	
	@Override
	public String toString() {
		return this.type.toString() + ":" + Float.toString(value);
	}
}
