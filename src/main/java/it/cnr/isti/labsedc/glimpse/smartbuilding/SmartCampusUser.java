package it.cnr.isti.labsedc.glimpse.smartbuilding;

public class SmartCampusUser {
	
	private int id;
	private String name;
	private String surname;
	private String telegram_id;
	private String room_id;
	private boolean intrusion_mode;
	
	public SmartCampusUser (int id, String name, String surname, String telegram_id, String room_id, boolean intrusion_mode) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.telegram_id = telegram_id;
		this.room_id = room_id;
		this.intrusion_mode = intrusion_mode;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getTelegram_id() {
		return telegram_id;
	}
	public void setTelegram_id(String telegram_id) {
		this.telegram_id = telegram_id;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public boolean isIntrusion_mode() {
		return intrusion_mode;
	}
	public void setIntrusion_mode(boolean intrusion_mode) {
		this.intrusion_mode = intrusion_mode;
	}
}
