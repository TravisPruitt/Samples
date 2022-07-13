SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

--
-- Updating Reader table
--
ALTER TABLE `Mayhem`.`Reader` ADD COLUMN `signalStrengthTransitThreshold` INTEGER  NOT NULL DEFAULT -90 AFTER `transmitPayload`;

--
-- Creating TransmitRecipients table
--
CREATE TABLE `Mayhem`.`TransmitRecipients` (
  `commandId` INTEGER  NOT NULL,
  `recipientId` INTEGER  NOT NULL,
  PRIMARY KEY (`commandId`, `recipientId`),
  CONSTRAINT `command_fk_constraint` FOREIGN KEY `command_fk_constraint` (`commandId`)
    REFERENCES `TransmitCommand` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT
)
ENGINE = InnoDB;

--
-- Updating TransmitCommands table
--
ALTER TABLE `Mayhem`.`TransmitCommand` DROP COLUMN `recipient`;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.4','spacemodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

