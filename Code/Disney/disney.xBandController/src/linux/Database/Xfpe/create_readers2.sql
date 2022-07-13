drop procedure if exists create_readers;

DELIMITER //
CREATE PROCEDURE `create_readers`(nCount INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE readerid VARCHAR(32);
	DECLARE locationName VARCHAR(32);
    DECLARE iloc INT DEFAULT 1;
    DECLARE bLeft INT DEFAULT 1;
	DECLARE locationId INT DEFAULT 0;
	DECLARE nId INT DEFAULT 0;
	DECLARE nOmniServerId INT DEFAULT 0;

	-- create the localhost OmniServer if necessary
	select id from OmniServer where hostname = "localhost" into @nOmniServerId;
	if (@nOmniServerId is NULL)
	then
		insert into OmniServer(hostname, port, description, active) values ("localhost", 9920, "Created by create_readers script", 1);
		select id from OmniServer where hostname = "localhost" into @nOmniServerId;
	end if;

    SET i = 1;
    WHILE i <= nCount DO
		-- create a new location for every 2 readers
		if ((i-1) % 2 = 0)
		then
			set locationName = concat("MK",iLoc);			
			if (bLeft = 1)
			then
				set locationName = concat(locationName,"-Left");
				-- next time create right 
				set bLeft = 0;
			else
				set iLoc = iLoc + 1;
				set locationName = concat(locationName, "-Right");
				-- next time create left
				set bLeft = 1;
			end if;
			insert into Location(locationTypeId, name, section, x, y, singulationTypeId, eventGenerationTypeId) values
				(1, locationName, "", 0, 0, 0, 2); 
			select max(id) from Location into @locationId;
		end if;
	    
		-- generate front/back reader name
		if ((i-1) % 2 = 0)
		then
			SET readerid = concat(locationName, "-front");
		else
			SET readerid = concat(locationName, "-back");
		end if;
		insert into Reader(type,readerId, positionX, positionY,macAddress,ipAddress,port,locationId,lane,deviceId,version,minXbrcVersion) values
			(2, readerid, 60 + (i div 10) * 10, (i MOD 6) * 3, i, "127.0.0.1", 8080, @locationId, i, i, "0.0.0.0", "1.0.0.0");
		select max(id) from Reader into @nId;
	    SET i = i + 1;

		insert into ReaderOmniServer(readerid, omniserverid, priority) values (@nId, @nOmniServerId, 1);
    END WHILE;
END//
