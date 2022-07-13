SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

--
-- Adding sequence columns to location to allow location specific light/sound sequences.
--
ALTER TABLE `Mayhem`.`Location` ADD COLUMN `successSeq` VARCHAR(256) NULL DEFAULT NULL  AFTER `useSecureId` , ADD COLUMN `failureSeq` VARCHAR(256) NULL DEFAULT NULL  AFTER `successSeq` , ADD COLUMN `errorSeq` VARCHAR(256) NULL DEFAULT NULL  AFTER `failureSeq`, ADD COLUMN `idleSeq` VARCHAR(256) NULL DEFAULT NULL  AFTER `errorSeq` ;
ALTER TABLE `Mayhem`.`Location` ADD COLUMN `successTimeout` INT(11) NULL DEFAULT NULL  AFTER `idleSeq` , ADD COLUMN `failureTimeout` INT(11) NULL DEFAULT NULL  AFTER `successTimeout` , ADD COLUMN `errorTimeout` INT(11) NULL DEFAULT NULL  AFTER `failureTimeout` ;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.2','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
