SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

LOCK TABLES `Status` WRITE;
/*!40000 ALTER TABLE `Status` DISABLE KEYS */;
UPDATE `Status` SET `Property` = 'LastMessageIdToPostStream' where `Property` = 'LastMessageToPostStream';
/*!40000 ALTER TABLE `Status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.16','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;