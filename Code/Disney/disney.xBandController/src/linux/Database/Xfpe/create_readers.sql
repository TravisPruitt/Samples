drop procedure if exists create_readers;

CREATE PROCEDURE create_readers (nCount INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE readerid VARCHAR(32);
    SET i = 1;
    WHILE i <= nCount DO
	SET readerid = concat("reader-", i);
	insert into Reader(type,readerId, positionX, positionY,macAddress,ipAddress,port,locationId,lane,deviceId,version,minXbrcVersion) values
		(2, readerid, 60 + (i div 10) * 10, (i MOD 6) * 3, i, "127.0.0.1", 8080, 1, i, i, "0.0.0.0", "1.0.0.0");
    	SET i = i + 1;
    END WHILE;
END
