SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

ALTER TABLE `Mayhem`.`TransmitCommand` ADD COLUMN `locationId` INTEGER  NOT NULL AFTER `id`,
 ADD COLUMN `threshold` INTEGER  NOT NULL DEFAULT -127 AFTER `mode`,
 ADD COLUMN `enableThreshold` TINYINT  NOT NULL DEFAULT 0 AFTER `threshold`;

ALTER TABLE `Mayhem`.`TransmitCommand` ADD CONSTRAINT `fk_TransmitCommand_Location` FOREIGN KEY `fk_TransmitCommand_Location` (`locationId`)
    REFERENCES `Location` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

drop procedure if exists `Mayhem`.`upgrade_transmitCommands`;

DELIMITER ;;
CREATE PROCEDURE `Mayhem`.`upgrade_transmitCommands`()
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION 
  BEGIN
    ROLLBACK;
  END;

  START TRANSACTION;

	update `Mayhem`.`TransmitCommand` tc set locationId = (select locationId from `Mayhem`.`Reader` rdr, `Mayhem`.`TransmitPayload` tp where rdr.id = tp.readerId and tc.id = tp.commandId);
	update `Mayhem`.`TransmitCommand` tc set threshold = (select signalStrengthTransitThreshold from `Mayhem`.`Reader` rdr, `Mayhem`.`TransmitPayload` tp where rdr.id = tp.readerId and tc.id = tp.commandId);
	update `Mayhem`.`TransmitCommand` tc set enableThreshold = true where exists (select signalStrengthTransitThreshold from `Mayhem`.`Reader` rdr, `Mayhem`.`TransmitPayload` tp where rdr.id = tp.readerId and tc.id = tp.commandId and signalStrengthTransitThreshold > -127);

  COMMIT;
END ;;

DELIMITER ;

call `Mayhem`.`upgrade_transmitCommands`;

drop procedure if exists `Mayhem`.`upgrade_transmitCommands`;

DROP TABLE `Mayhem`.`TransmitPayload`;

ALTER TABLE `Mayhem`.`Reader` ADD COLUMN `transmitterHaPriority` INTEGER  NOT NULL DEFAULT 1 AFTER `hardwareType`;
ALTER TABLE `Mayhem`.`Reader` DROP COLUMN `signalStrengthTransitThreshold`;

ALTER TABLE `Mayhem`.`Location` ADD COLUMN `transmitZoneGroup` CHAR  AFTER `tapTimeout`;


--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.19','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
