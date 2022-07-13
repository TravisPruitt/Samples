ALTER TABLE `Mayhem`.`GST` MODIFY COLUMN `LastReader` VARCHAR(32) NOT NULL;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.10','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;



