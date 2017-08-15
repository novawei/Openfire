CREATE TABLE ofMucUser (
  roomID	  	BIGINT 			NOT NULL,
  jid		  	VARCHAR(255) 	NOT NULL,
  nickname  	VARCHAR(255) 	NOT NULL,
  PRIMARY KEY (roomID, jid)
);
INSERT INTO ofVersion(name, version) values('mucextend', 0);