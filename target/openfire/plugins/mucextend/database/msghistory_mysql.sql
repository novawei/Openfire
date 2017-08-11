CREATE TABLE ofMsgHistoryChat (
  messageID  	BIGINT 			NOT NULL AUTO_INCREMENT,
  sender  		VARCHAR(50) 	NOT NULL,
  receiver  	VARCHAR(50) 	NOT NULL,
  createdDate  	CHAR(15) 		NOT NULL,
  body  		TEXT 			NOT NULL,
  PRIMARY KEY (messageID),
  INDEX ofChatMsgHistory_date_idx (createdDate),
  INDEX ofChatMsgHistory_sender_idx (sender),
  INDEX ofChatMsgHistory_receiver_idx (receiver)
);

CREATE TABLE ofMsgHistoryGroupChat (
  messageID  	BIGINT 			NOT NULL AUTO_INCREMENT,
  roomID		BIGINT			NOT NULL,
  roomName 		VARCHAR(50) 	NOT NULL,
  sender  		VARCHAR(50) 	NOT NULL,
  createdDate  	CHAR(15) 		NOT NULL,
  body  		TEXT 			NOT NULL,
  nickname		VARCHAR(255),
  PRIMARY KEY (messageID),
  INDEX ofGroupChatMsgHistory_date_idx (createdDate),
  INDEX ofGroupChatMsgHistory_room_idx (roomName)
);

INSERT INTO ofVersion(name, version) values('msghistory', 0);