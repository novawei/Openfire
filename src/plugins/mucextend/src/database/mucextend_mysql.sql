CREATE TABLE ofMucUser (
  roomID	  	BIGINT 			NOT NULL,
  roomName		VARCHAR(255)	NOT NULL,
  jid		  	VARCHAR(255) 	NOT NULL,
  nickname  	VARCHAR(255) 	NOT NULL,
  PRIMARY KEY (roomName, jid)
);
INSERT INTO ofVersion(name, version) values('mucextend', 0);