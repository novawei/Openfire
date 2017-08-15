package com.nfs.mucextend;

public class RoomEntity {
	private long roomID;
	private String name;
	private String naturalName;
	private String description;
	private String nickname; //用户在该房间的nickname
	
	public RoomEntity() {
		
	}
	
	public long getRoomID() {
		return roomID;
	}
	public void setRoomID(long roomID) {
		this.roomID = roomID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNaturalName() {
		return naturalName;
	}
	public void setNaturalName(String naturalName) {
		this.naturalName = naturalName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
