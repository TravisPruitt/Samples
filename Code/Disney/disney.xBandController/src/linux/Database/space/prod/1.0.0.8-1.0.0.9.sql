SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

--
-- Table structure for table `ReaderAntenna`
--

DROP TABLE IF EXISTS `ReaderAntenna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReaderAntenna` (
  `readerId`    INT(11) NOT NULL,
  `antenna` INT(11) NOT NULL,
  `power` TINYINT(1) NOT NULL DEFAULT TRUE,
  INDEX (readerId),
  INDEX (antenna),
  FOREIGN KEY (readerId) REFERENCES Reader(id),
  PRIMARY KEY (readerId, antenna)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Configured reader antennas';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.9','spacemodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



