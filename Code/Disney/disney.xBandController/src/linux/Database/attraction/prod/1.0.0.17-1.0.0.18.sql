SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP TABLE IF EXISTS `EventsLocationConfig`;
CREATE TABLE `EventsLocationConfig` (
  `locationId` int(11) NOT NULL,
  `abandonmentTimeout` int(11) DEFAULT NULL,
  `castmemberDetectDelay` int(11) DEFAULT NULL,
  `puckDetectDelay` int(11) DEFAULT NULL,
  `guestDetectDelay` int(11) DEFAULT NULL,
  `chirpRate` int(11) DEFAULT NULL,
  `confidenceDelta` int(11) DEFAULT NULL,
  `locationEventRatio` int(11) DEFAULT NULL,
  `sendToJMS` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`locationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `Messages` ADD COLUMN `SendToHttp` TINYINT(1) NULL  AFTER `Payload`;
ALTER TABLE `Messages` ADD COLUMN `SendToJMS` TINYINT(1) NULL  AFTER `SendToHttp`;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.18','attractionmodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



