ALTER TABLE `Mayhem`.`Location` ADD COLUMN `tapSeq` VARCHAR(256)  DEFAULT NULL AFTER `idleSeq`,
 ADD COLUMN `tapTimeout` INTEGER  DEFAULT NULL AFTER `errorTimeout`;


--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.12','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;



