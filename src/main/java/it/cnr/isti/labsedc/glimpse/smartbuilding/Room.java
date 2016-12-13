package it.cnr.isti.labsedc.glimpse.smartbuilding;

import java.util.Date;

public class Room {

	private String roomID;
	private Float temperature;
	private Float occupancy;
	private Float humidity;
	private Float noise;
	private Float socketpower;
	private Float lightpower;
	private Date updateDateTime;

	public Room(String roomID, Float temperature, Float occupancy, Float humidity, Float noise, Float socketpower, Float lightpower, Date updateDateTime) {
		this.roomID = roomID;
		this.temperature = temperature;
		this.occupancy = occupancy;
		this.humidity = humidity;
		this.noise = noise;
		this.socketpower = socketpower;
		this.lightpower = lightpower;
		this.updateDateTime = updateDateTime;
	}
	
	public Float getSocketpower() {
		return socketpower;
	}

	public void setSocketpower(Float socketpower) {
		this.socketpower = socketpower;
	}

	public Float getLightpower() {
		return lightpower;
	}

	public void setLightpower(Float lightpower) {
		this.lightpower = lightpower;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public Float getOccupancy() {
		return occupancy;
	}

	public void setOccupancy(Float occupancy) {
		this.occupancy = occupancy;
	}

	public Float getHumidity() {
		return humidity;
	}

	public void setHumidity(Float humidity) {
		this.humidity = humidity;
	}

	public Float getNoise() {
		return noise;
	}

	public void setNoise(Float noise) {
		this.noise = noise;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}
}
