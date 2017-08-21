CREATE TABLE ofMucUser (
  roomID	  	BIGINT 			NOT NULL,
  roomName		VARCHAR(255)	NOT NULL,
  jid		  	VARCHAR(255) 	NOT NULL,
  nickname  	VARCHAR(255) 	NOT NULL,
  PRIMARY KEY (roomID, jid),
  INDEX ofMucUser_roomName_idx (roomName),
);
INSERT INTO ofVersion(name, version) values('mucextend', 0);