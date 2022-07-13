SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Mayhem`.`VaLocationConfig` (
  `locationId` INTEGER  NOT NULL,
  `onrideTimeoutSec` INTEGER  NOT NULL,
  `maxAnalyzeGuestEvents` INTEGER  NOT NULL,
  `maxAnalyzeGuestEventsPerVehicle` INTEGER  NOT NULL,
  `useVehicleEventTime` BOOLEAN  NOT NULL,
  `requireVehicleLaserEvent` BOOLEAN NOT NULL,
  `vehicleTimeOffsetMs` INTEGER  NOT NULL,
  `minReadsToAssociate` INTEGER  NOT NULL,
  `trainTimeoutSec` INTEGER  NOT NULL,
  `laserBreaksBeforeVehicle` INTEGER  NOT NULL,
  `laserBreaksAfterVehicle` INTEGER  NOT NULL,
  `vehicleHoldTimeMs` INTEGER  NOT NULL,
  `vaAlgorithm` VARCHAR(40)  NOT NULL,
  PRIMARY KEY (`locationId`),
  CONSTRAINT `location_fk_constraint` FOREIGN KEY `location_fk_constraint` (`locationId`)
    REFERENCES `Location` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)
ENGINE = InnoDB;
/*!40101 SET character_set_client = @saved_cs_client */;

drop procedure if exists `Mayhem`.`upgrade_vaLocationConfig`;

DELIMITER ;;
CREATE PROCEDURE `Mayhem`.`upgrade_vaLocationConfig`()
BEGIN
  DECLARE more BOOLEAN DEFAULT TRUE;	
  DECLARE locId INTEGER;
  DECLARE onrideTimeoutSec INTEGER DEFAULT 2;
  DECLARE maxAnalyzeGuestEvents INTEGER DEFAULT 10000;
  DECLARE maxAnalyzeGuestEventsPerVehicle INTEGER DEFAULT 2;
  DECLARE useVehicleEventTime BOOLEAN DEFAULT FALSE;
  DECLARE requireVehicleLaserEvent BOOLEAN DEFAULT TRUE;
  DECLARE vehicleTimeOffsetMs INTEGER DEFAULT 0;
  DECLARE minReadsToAssociate INTEGER DEFAULT 2;
  DECLARE trainTimeoutSec INTEGER DEFAULT 20;
  DECLARE laserBreaksBeforeVehicle INTEGER DEFAULT 0;
  DECLARE laserBreaksAfterVehicle INTEGER DEFAULT 0;
  DECLARE vehicleHoldTimeMs INTEGER DEFAULT 1000;
  DECLARE vaAlgorithm VARCHAR(40) DEFAULT "closestpeakfallback";

  DECLARE strOnrideTimeoutSec VARCHAR(1024) DEFAULT NULL;
  DECLARE strMaxAnalyzeGuestEvents VARCHAR(1024) DEFAULT NULL;
  DECLARE strMaxAnalyzeGuestEventsPerVehicle VARCHAR(1024) DEFAULT NULL;
  DECLARE strUseVehicleEventTime VARCHAR(1024) DEFAULT NULL;
  DECLARE strRequireVehicleLaserEvent VARCHAR(1024) DEFAULT NULL;
  DECLARE strVehicleTimeOffsetMs VARCHAR(1024) DEFAULT NULL;
  DECLARE strMinReadsToAssociate VARCHAR(1024) DEFAULT NULL;
  DECLARE strTrainTimeoutSec VARCHAR(1024) DEFAULT NULL;
  DECLARE strLaserBreaksBeforeVehicle VARCHAR(1024) DEFAULT NULL;
  DECLARE strLaserBreaksAfterVehicle VARCHAR(1024) DEFAULT NULL;
  DECLARE strVehicleHoldTimeMs VARCHAR(1024) DEFAULT NULL;
  DECLARE strVaAlgorithm VARCHAR(1024) DEFAULT NULL;

  -- find all locations with vehicle association readers
  DECLARE cur1 CURSOR FOR SELECT distinct loc.id FROM Mayhem.Location loc, Mayhem.Reader rdr where loc.name != "UNKNOWN" and loc.id = rdr.locationId and rdr.type = 5;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET more = FALSE;

  DECLARE EXIT HANDLER FOR SQLEXCEPTION 
  BEGIN
    ROLLBACK;
  END;

  -- first read all vehicle association Config values
  SELECT @strOnrideTimeoutSec := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "onridetimeout";
  SELECT @strMaxAnalyzeGuestEvents := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "maxanalyzeguestevents";
  SELECT @strMaxAnalyzeGuestEventsPerVehicle := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "maxanalyzeguesteventspervehicle";
  SELECT @strUseVehicleEventTime := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "usevehicleeventtime";
  SELECT @strRequireVehicleLaserEvent := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "requirevehiclelaserevent";
  SELECT @strVehicleTimeOffsetMs := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vehicletimeoffsetms";
  SELECT @strMinReadsToAssociate := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "minreadstoassociate";
  SELECT @strTrainTimeoutSec := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "traintimeoutsec";
  SELECT @strLaserBreaksBeforeVehicle := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "laserbreaksbeforevehicle";
  SELECT @strLaserBreaksAfterVehicle := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "laserbreaksaftervehicle";
  SELECT @strVehicleHoldTimeMs := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vehicleholdtimems";
  SELECT @strVaAlgorithm := `value` FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vaalgorithm";

  IF (@strOnrideTimeoutSec IS NOT NULL and CHAR_LENGTH(@strOnrideTimeoutSec) > 0) THEN
	SELECT @onrideTimeoutSec := CAST(@strOnrideTimeoutSec as SIGNED);
  END IF;

  IF (@strMaxAnalyzeGuestEvents IS NOT NULL and CHAR_LENGTH(@strMaxAnalyzeGuestEvents) > 0) THEN
	SELECT @maxAnalyzeGuestEvents := CAST(@strMaxAnalyzeGuestEvents as SIGNED);
  END IF;

  IF (@strMaxAnalyzeGuestEventsPerVehicle IS NOT NULL and CHAR_LENGTH(@strMaxAnalyzeGuestEventsPerVehicle) > 0) THEN
	SELECT @maxAnalyzeGuestEventsPerVehicle := CAST(@strMaxAnalyzeGuestEventsPerVehicle as SIGNED);
  END IF;

  IF (@strUseVehicleEventTime IS NOT NULL and CHAR_LENGTH(@strUseVehicleEventTime) > 0) THEN
    IF (@strUseVehicleEventTime = "true" or @strUseVehicleEventTime = "TRUE") THEN
        SELECT @useVehicleEventTime := 1;
    ELSE
        SELECT @useVehicleEventTime := 0;
    END IF;	
  END IF;

  IF (@strRequireVehicleLaserEvent IS NOT NULL and CHAR_LENGTH(@strRequireVehicleLaserEvent) > 0) THEN
    IF (@strRequireVehicleLaserEvent = "true" or @strRequireVehicleLaserEvent = "TRUE") THEN
        SELECT @requireVehicleLaserEvent := 1;
    ELSE
        SELECT @requireVehicleLaserEvent := 0;
    END IF;
  END IF;

  IF (@strVehicleTimeOffsetMs IS NOT NULL and CHAR_LENGTH(@strVehicleTimeOffsetMs) > 0) THEN
	SELECT @vehicleTimeOffsetMs := CAST(@strVehicleTimeOffsetMs as SIGNED);
  END IF;

  IF (@strMinReadsToAssociate IS NOT NULL and CHAR_LENGTH(@strMinReadsToAssociate) > 0) THEN
	SELECT @minReadsToAssociate := CAST(@strMinReadsToAssociate as SIGNED);
  END IF;

  IF (@strTrainTimeoutSec IS NOT NULL and CHAR_LENGTH(@strTrainTimeoutSec) > 0) THEN
	SELECT @trainTimeoutSec := CAST(@strTrainTimeoutSec as SIGNED);
  END IF;

  IF (@strLaserBreaksBeforeVehicle IS NOT NULL and CHAR_LENGTH(@strLaserBreaksBeforeVehicle) > 0) THEN
	SELECT @laserBreaksBeforeVehicle := CAST(@strLaserBreaksBeforeVehicle as SIGNED);
  END IF;

  IF (@strLaserBreaksAfterVehicle IS NOT NULL and CHAR_LENGTH(@strLaserBreaksAfterVehicle) > 0) THEN
	SELECT @laserBreaksAfterVehicle := CAST(@strLaserBreaksAfterVehicle as SIGNED);
  END IF;

  IF (@strVehicleHoldTimeMs IS NOT NULL and CHAR_LENGTH(@strVehicleHoldTimeMs) > 0) THEN
	SELECT @vehicleHoldTimeMs := CAST(@strVehicleHoldTimeMs as SIGNED);
  END IF;

  IF (@strVaAlgorithm IS NOT NULL and CHAR_LENGTH(@strVaAlgorithm) > 0) THEN
	SELECT @vaAlgorithm := @strVaAlgorithm;
  END IF;

  START TRANSACTION;

  SET more = TRUE;
  OPEN cur1;

  FETCH cur1 INTO locId;
  WHILE more DO
	INSERT INTO VaLocationConfig (
    `locationId`,
    `onrideTimeoutSec`,
    `maxAnalyzeGuestEvents`,
    `maxAnalyzeGuestEventsPerVehicle`,
    `useVehicleEventTime`,
    `requireVehicleLaserEvent`,
    `vehicleTimeOffsetMs`,
    `minReadsToAssociate`,
    `trainTimeoutSec`,
    `laserBreaksBeforeVehicle`,
    `laserBreaksAfterVehicle`,
    `vehicleHoldTimeMs`,
    `vaAlgorithm`) VALUES (
    locId, 
    @onrideTimeoutSec,
    @maxAnalyzeGuestEvents,
    @maxAnalyzeGuestEventsPerVehicle,
    @useVehicleEventTime,
    @requireVehicleLaserEvent,
    @vehicleTimeOffsetMs,
    @minReadsToAssociate,
    @trainTimeoutSec,
    @laserBreaksBeforeVehicle,
    @laserBreaksAfterVehicle,
    @vehicleHoldTimeMs,
    @vaAlgorithm);

	FETCH cur1 INTO locId;
  END WHILE;

  CLOSE cur1;

  -- remove the no longer used Config entries
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "onridetimeout";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "maxanalyzeguestevents";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "maxanalyzeguesteventspervehicle";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "usevehicleeventtime";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "requirevehiclelaserevent";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vehicletimeoffsetms";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "minreadstoassociate";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "traintimeoutsec";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "laserbreaksbeforevehicle";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "laserbreaksaftervehicle";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vehicleholdtimems";
  DELETE FROM Mayhem.Config WHERE class = "AttractionModelConfig" and property = "vaalgorithm";

  COMMIT;
END ;;

DELIMITER ;

call `Mayhem`.`upgrade_vaLocationConfig`;

drop procedure if exists `Mayhem`.`upgrade_vaLocationConfig`;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.10','attractionmodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



