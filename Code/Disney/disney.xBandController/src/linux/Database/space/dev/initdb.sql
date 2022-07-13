-- MySQL dump 10.13  Distrib 5.1.54, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: Mayhem
-- ------------------------------------------------------
-- Server version	5.1.54-1ubuntu4

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Config`
--

DROP TABLE IF EXISTS `Config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Config` (
  `class` varchar(64) NOT NULL,
  `property` varchar(32) NOT NULL,
  `value` varchar(1024) NOT NULL,
  PRIMARY KEY (`class`,`property`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Scalar configuration parameters for controller';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Config`
--

LOCK TABLES `Config` WRITE;
/*!40000 ALTER TABLE `Config` DISABLE KEYS */;
INSERT INTO `Config` VALUES ('ControllerInfo','adjusteventtimes','true'),('ControllerInfo','bioimagecapure','false'),('ControllerInfo','dateformat','yyyy-MM-dd HH:mm:ss z'),('ControllerInfo','discoverynetprefix',''),('ControllerInfo','eventdumpfile','/var/log/xbrc/eventdump.txt'),('ControllerInfo','eventprocessingperiod_msec','200'),('ControllerInfo','httpport','8080'),('ControllerInfo','httpsport','0'),('ControllerInfo','idmscachetime_sec','1800'),('ControllerInfo','jmsretryperiod','1000'),('ControllerInfo','maxreadsbeforepurge','8'),('ControllerInfo','messagestorageperiod','3600'),('ControllerInfo','model','com.disney.xband.xbrc.spacemodel.CEP'),('ControllerInfo','name','xBRC'),('ControllerInfo','ownipprefix','192.'),('ControllerInfo','perfmetricsperiod','300'),('ControllerInfo','preferredguestidtype',''),('ControllerInfo','readerdatasendperiod_msec','100'),('ControllerInfo','readerhellotimeoutsec','90'),('ControllerInfo','readerhttptimeout_msec','3000'),('ControllerInfo','reducedataslicing','true'),('ControllerInfo','reversetapids','false'),('ControllerInfo','rfidtestmode','false'),('ControllerInfo','securetapid','false'),('ControllerInfo','setreadertime','true'),('ControllerInfo','singulationalgorithm','max'),('ControllerInfo','snmp_enabled','false'),('ControllerInfo','statesaveperiod_sec','1000'),('ControllerInfo','transmitxbrperiod_msec','100'),('ControllerInfo','updatestreamhttptimeout_msec','3000'),('ControllerInfo','updatestreamurl',''),('ControllerInfo','venue','xBRC'),('ControllerInfo','verbose','false'),('ControllerInfo','watchedbandtimeout','180'),('ControllerInfo','wwwdir','/usr/share/xbrc/www'),('ControllerInfo','xviewurl','http://localhost:8090/Xview'),('ESBInfo','jmsbroker','#'),('ESBInfo','jmsdiscoverytimesec','60'),('ESBInfo','jmspassword',''),('ESBInfo','jmstopic',''),('ESBInfo','jmsuser',''),('SpaceModelConfig','abandonmenttimeout_msec','3000');
/*!40000 ALTER TABLE `Config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GST`
--

DROP TABLE IF EXISTS `GST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GST` (
  `GuestId` varchar(32) NOT NULL,
  `HasXPass` tinyint(1) NOT NULL,
  `State` varchar(16) NOT NULL,
  `LastReader` varchar(32) NOT NULL,
  `TimeEarliestAtReader` bigint(20) NOT NULL,
  `TimeLatestAtReader` bigint(20) NOT NULL,
  `TimeEntered` bigint(20) NOT NULL,
  `TimeMerged` bigint(20) NOT NULL,
  `TimeLoaded` bigint(20) NOT NULL,
  `TimeExited` bigint(20) NOT NULL,
  `CarID` varchar(64) NOT NULL,
  `HasDeferredEntry` tinyint(1) NOT NULL,
  PRIMARY KEY (`GuestId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Snapshot of guest status table used in Controller';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GST`
--

LOCK TABLES `GST` WRITE;
/*!40000 ALTER TABLE `GST` DISABLE KEYS */;
/*!40000 ALTER TABLE `GST` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GridItem`
--

DROP TABLE IF EXISTS `GridItem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GridItem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ItemType` varchar(32) NOT NULL,
  `XGrid` int(11) NOT NULL,
  `YGrid` int(11) NOT NULL,
  `State` varchar(16) NOT NULL,
  `Label` varchar(256) NOT NULL,
  `Description` varchar(512) NOT NULL,
  `Image` varchar(256) DEFAULT NULL,
  `Sequence` int(11) NOT NULL DEFAULT '0',
  `XPassOnly` tinyint(4) NOT NULL,
  `LocationId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=latin1 COMMENT='Items that together make up the flow chart of the attraction';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GridItem`
--

LOCK TABLES `GridItem` WRITE;
/*!40000 ALTER TABLE `GridItem` DISABLE KEYS */;
INSERT INTO `GridItem` VALUES (27,'Gate',2,3,'INDETERMINATE','Entry 1','','Gate.png',0,0,1),(29,'Gate',6,3,'INDETERMINATE','Entry Exit','','Gate.png',0,0,3),(30,'Gate',10,3,'INDETERMINATE','Monitor 1','','Gate.png',0,0,4),(31,'Gate',14,3,'INDETERMINATE','Monitor 2','','Gate.png',0,0,5),(32,'ESTurn',9,2,'INDETERMINATE','','',NULL,8,0,4),(33,'ENTurn',9,4,'INDETERMINATE','','',NULL,6,0,4),(34,'WNTurn',11,4,'INDETERMINATE','','',NULL,4,0,4),(35,'WSTurn',11,2,'INDETERMINATE','','',NULL,2,0,4),(36,'HPath',10,2,'INDETERMINATE','','',NULL,1,0,4),(37,'HPath',10,4,'INDETERMINATE','','',NULL,5,0,4),(38,'VPath',11,3,'INDETERMINATE','','',NULL,3,0,4),(39,'VPath',9,3,'INDETERMINATE','','',NULL,7,0,4),(40,'ENTurn',13,4,'INDETERMINATE','','',NULL,6,0,5),(41,'WNTurn',15,4,'INDETERMINATE','','',NULL,4,0,5),(42,'HPath',14,4,'INDETERMINATE','','',NULL,5,0,5),(43,'HPath',14,2,'INDETERMINATE','','',NULL,1,0,5),(44,'ESTurn',13,2,'INDETERMINATE','','',NULL,8,0,5),(45,'WSTurn',15,2,'INDETERMINATE','','',NULL,2,0,5),(46,'VPath',15,3,'INDETERMINATE','','',NULL,3,0,5),(47,'VPath',13,3,'INDETERMINATE','','',NULL,7,0,5),(48,'ESTurn',1,2,'INDETERMINATE','','',NULL,8,0,1),(49,'WSTurn',3,2,'INDETERMINATE','','',NULL,2,0,1),(50,'HPath',2,2,'INDETERMINATE','','',NULL,1,0,1),(51,'ENTurn',1,4,'INDETERMINATE','','',NULL,6,0,1),(52,'WNTurn',3,4,'INDETERMINATE','','',NULL,4,0,1),(53,'HPath',2,4,'INDETERMINATE','','',NULL,5,0,1),(54,'VPath',3,3,'INDETERMINATE','','',NULL,3,0,1),(55,'VPath',1,3,'INDETERMINATE','','',NULL,7,0,1),(64,'ENTurn',5,4,'INDETERMINATE','','',NULL,6,0,3),(65,'WNTurn',7,4,'INDETERMINATE','','',NULL,4,0,3),(66,'VPath',7,3,'INDETERMINATE','','',NULL,3,0,3),(67,'HPath',6,2,'INDETERMINATE','','',NULL,1,0,1),(68,'ESTurn',5,2,'INDETERMINATE','','',NULL,8,0,3),(69,'WSTurn',7,2,'INDETERMINATE','','',NULL,2,0,3),(70,'VPath',5,3,'INDETERMINATE','','',NULL,7,0,3),(71,'HPath',6,4,'INDETERMINATE','','',NULL,5,0,3);
/*!40000 ALTER TABLE `GridItem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GuestPosition`
--

DROP TABLE IF EXISTS `GuestPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GuestPosition` (
  `BandID` varchar(32) DEFAULT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `HasxPass` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Simulated or inferred guest location';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GuestPosition`
--

LOCK TABLES `GuestPosition` WRITE;
/*!40000 ALTER TABLE `GuestPosition` DISABLE KEYS */;
/*!40000 ALTER TABLE `GuestPosition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Image`
--

DROP TABLE IF EXISTS `Image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `filename` (`filename`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Image`
--

LOCK TABLES `Image` WRITE;
/*!40000 ALTER TABLE `Image` DISABLE KEYS */;
/*!40000 ALTER TABLE `Image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ImageBlob`
--

DROP TABLE IF EXISTS `ImageBlob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ImageBlob` (
  `imageId` bigint(20) NOT NULL,
  `bytes` mediumblob NOT NULL,
  PRIMARY KEY (`imageId`) USING BTREE,
  CONSTRAINT `image_fk` FOREIGN KEY (`imageId`) REFERENCES `Image` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ImageBlob`
--

LOCK TABLES `ImageBlob` WRITE;
/*!40000 ALTER TABLE `ImageBlob` DISABLE KEYS */;
/*!40000 ALTER TABLE `ImageBlob` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Location`
--

DROP TABLE IF EXISTS `Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `locationTypeId` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `section` varchar(32) NOT NULL,
  `x` double NOT NULL,
  `y` double NOT NULL,
  `singulationTypeId` int(11) NOT NULL,
  `eventGenerationTypeId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LocationType` (`locationTypeId`),
  CONSTRAINT `FK_LocationType` FOREIGN KEY (`locationTypeId`) REFERENCES `LocationType` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (0,0,'UNKNOWN','',0,0,0,0),(1,1,'Entry1','',15,0,0,2),(2,1,'Entry2','',15,3,0,1),(3,2,'Entrance-Exit','',25,15,0,1),(4,2,'Monitor1','',40,15,0,1),(5,2,'Monitor2','',50,15,0,1);
/*!40000 ALTER TABLE `Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `LocationType`
--

DROP TABLE IF EXISTS `LocationType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LocationType` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `LocationTypeName` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LocationType`
--

LOCK TABLES `LocationType` WRITE;
/*!40000 ALTER TABLE `LocationType` DISABLE KEYS */;
INSERT INTO `LocationType` VALUES (0,'UNKNOWN'),(1,'Entry'),(2,'Waypoint'),(3,'Exit'),(4,'Load'),(5,'InCar'),(6,'Merge'),(7,'xPass Entry'),(8,'Combo');
/*!40000 ALTER TABLE `LocationType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Messages`
--

DROP TABLE IF EXISTS `Messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `MessageType` varchar(32) NOT NULL,
  `Timestamp` bigint(20) NOT NULL,
  `Payload` varchar(4096) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2790 DEFAULT CHARSET=latin1 COMMENT='XML payloads for outgoing JMS or HTTP messages';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Messages`
--

LOCK TABLES `Messages` WRITE;
/*!40000 ALTER TABLE `Messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Reader`
--

DROP TABLE IF EXISTS `Reader`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Reader` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL,
  `readerId` varchar(32) NOT NULL,
  `positionX` int(11) DEFAULT NULL,
  `positionY` int(11) DEFAULT NULL,
  `group` varchar(16) DEFAULT NULL,
  `singulationGroup` varchar(16) DEFAULT NULL,
  `signalStrengthThreshold` int(11) NOT NULL DEFAULT '-90',
  `gain` double NOT NULL DEFAULT '1',
  `macAddress` varchar(32) DEFAULT NULL,
  `ipAddress` varchar(32) NOT NULL,
  `port` int(11) NOT NULL DEFAULT '80',
  `lastIdReceived` bigint(20) NOT NULL DEFAULT '-1',
  `locationId` int(11) NOT NULL,
  `timeLastHello` bigint(20) NOT NULL,
  `lane` int(11) NOT NULL,
  `deviceId` int(11) NOT NULL DEFAULT '0',
  `version` varchar(32) DEFAULT NULL,
  `minXbrcVersion` varchar(32) DEFAULT NULL,
  `isTransmitter` tinyint(1) DEFAULT '0',
  `transmitPayload` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `reader_id_index` (`readerId`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=latin1 COMMENT='Information about configured readers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reader`
--

LOCK TABLES `Reader` WRITE;
/*!40000 ALTER TABLE `Reader` DISABLE KEYS */;
INSERT INTO `Reader` VALUES (1,1,'entry1',0,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8011,-1,1,1320768790231,0,0,NULL,NULL,0,NULL),(2,1,'entry2',3,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8012,-1,1,1320768789409,0,0,NULL,NULL,0,NULL),(3,1,'entry3',0,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8013,-1,1,1320768788552,0,0,NULL,NULL,0,NULL),(4,1,'entry4',3,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8014,-1,1,1320768787346,0,0,NULL,NULL,0,NULL),(5,2,'entrance-exit',15,9,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8051,-1,3,1320768786701,0,0,NULL,NULL,0,NULL),(6,1,'monitor1-1',50,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8071,-1,4,1320768785989,0,0,NULL,NULL,0,NULL),(7,1,'monitor1-2',53,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8072,-1,4,1320768785196,0,0,NULL,NULL,0,NULL),(8,1,'monitor1-3',50,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8073,-1,4,1320768784282,0,0,NULL,NULL,0,NULL),(9,1,'monitor1-4',53,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8074,-1,4,1320768783468,0,0,NULL,NULL,0,NULL),(10,1,'monitor2-1',85,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8081,-1,5,1320768782686,0,0,NULL,NULL,0,NULL),(11,1,'monitor2-2',88,7,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8082,-1,5,1320768781814,0,0,NULL,NULL,0,NULL),(12,1,'monitor2-3',85,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8083,-1,5,1320768780818,0,0,NULL,NULL,0,NULL),(13,1,'monitor2-4',88,10,NULL,NULL,0,1,'00:00:00:00:00:00','127.0.0.1',8084,-1,5,1320768773823,0,0,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `Reader` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SchemaVersion`
--

DROP TABLE IF EXISTS `SchemaVersion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SchemaVersion` (
  `version` varchar(40) NOT NULL,
  `model` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Mayhem database schema version';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.0','spacemodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Status`
--

DROP TABLE IF EXISTS `Status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Status` (
  `Property` varchar(32) NOT NULL,
  `Value` varchar(1024) NOT NULL,
  PRIMARY KEY (`Property`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Persists execution status of controller';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Status`
--

LOCK TABLES `Status` WRITE;
/*!40000 ALTER TABLE `Status` DISABLE KEYS */;
INSERT INTO `Status` VALUES ('LastStateStore','1970-01-01T00:00:00.000'),('LastMessageIdToJMS','-1'),('LastMessageIdToPostStream','-1');
/*!40000 ALTER TABLE `Status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `StoredConfigurations`
--

DROP TABLE IF EXISTS `StoredConfigurations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `StoredConfigurations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `description` varchar(1024) NOT NULL,
  `model` varchar(256) NOT NULL,
  `xml` longtext NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='Holds configuration sets serialized in XML form';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `StoredConfigurations`
--

LOCK TABLES `StoredConfigurations` WRITE;
/*!40000 ALTER TABLE `StoredConfigurations` DISABLE KEYS */;
/*!40000 ALTER TABLE `StoredConfigurations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Walls`
--

DROP TABLE IF EXISTS `Walls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Walls` (
  `x0` double NOT NULL,
  `y0` double NOT NULL,
  `x1` double NOT NULL,
  `y1` double NOT NULL,
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COMMENT='Simulated "walls" in attration';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Walls`
--

LOCK TABLES `Walls` WRITE;
/*!40000 ALTER TABLE `Walls` DISABLE KEYS */;
INSERT INTO `Walls` VALUES (15,0,120,0,1),(120,0,120,20,2),(120,20,15,20,3),(15,20,15,15,4),(15,0,15,5,5);
/*!40000 ALTER TABLE `Walls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'Mayhem'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-06-14 18:09:04
