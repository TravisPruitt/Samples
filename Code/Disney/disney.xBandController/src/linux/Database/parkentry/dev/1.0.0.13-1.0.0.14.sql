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
INSERT INTO `SchemaVersion` VALUES ('1.0.0.14','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


