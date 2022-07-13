ALTER TABLE `Mayhem`.`CastMember` ENGINE = InnoDB;

ALTER TABLE `Mayhem`.`CMST` ENGINE = InnoDB;


--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.13','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;



