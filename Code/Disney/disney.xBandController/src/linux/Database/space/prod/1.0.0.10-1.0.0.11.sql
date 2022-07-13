SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Mayhem`.`Audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `aId` BIGINT NULL,
  `type` VARCHAR(16) NOT NULL,
  `category` VARCHAR(32) NOT NULL,
  `appClass` VARCHAR(16) NULL,
  `appId` VARCHAR(32) NULL,
  `host` VARCHAR(255) NOT NULL,
  `vHost` VARCHAR(255) NULL,
  `uId` VARCHAR(32) NULL,
  `sId` VARCHAR(32) NULL,
  `description` VARCHAR(255) NULL,
  `rId` VARCHAR(128) NULL,
  `rData` VARCHAR(21844) NULL,	/* equivelent of NVARCHAR(max) */
  `dateTime` DATETIME NOT NULL,
  `dateTimeMillis` BIGINT  NOT NULL,
  `sourceTimeZone` VARCHAR(6)  NOT NULL,
  INDEX (dateTime),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.11','space');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



