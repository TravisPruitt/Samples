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
-- Table structure for table `CMST`
--

DROP TABLE IF EXISTS `CMST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CMST` (
  `bandId` varchar(16) DEFAULT NULL,
  `State` varchar(16) NOT NULL,
  `locationName` varchar(32) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Cast Member Status';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CMST`
--

LOCK TABLES `CMST` WRITE;
/*!40000 ALTER TABLE `CMST` DISABLE KEYS */;
/*!40000 ALTER TABLE `CMST` ENABLE KEYS */;
UNLOCK TABLES;

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
INSERT INTO `Config` VALUES ('AttractionModelConfig','abandonmenttimeout_msec','3600'),('ControllerInfo','abandonmenttimeout','3600'),('ControllerInfo','conn','jdbc:mysql://localhost/Mayhem'),('ControllerInfo','discoverynetprefix','10.'),('ControllerInfo','eventdumpfile','eventdump.txt'),('ControllerInfo','eventsperbatch','500'),('ControllerInfo','httpport','8080'),('ControllerInfo','httpsport','0'),('ControllerInfo','jmsretryperiod','1000'),('ControllerInfo','loadtype','trailing'),('ControllerInfo','maxreadersbeforepurge','6'),('ControllerInfo','metricsperiod','15'),('ControllerInfo','model','com.disney.xband.xbrc.spacemodel.CEP'),('ControllerInfo','onridetimeout','2'),('ControllerInfo','ownipprefix','192.'),('ControllerInfo','pass','Mayhem!23'),('ControllerInfo','pushmode','true'),('ControllerInfo','readerhellotimeoutsec','90'),('ControllerInfo','readertestmode','false'),('ControllerInfo','setreadertime','true'),('ControllerInfo','updatestreamurl',''),('ControllerInfo','url','http://localhost:8080'),('ControllerInfo','user','EMUser'),('ControllerInfo','venue','DCL'),('ControllerInfo','verbose','false'),('ControllerInfo','watchedbandtimeout','180'),('ControllerInfo','xviewurl','http://localhost:8090/Xview'),('ESBInfo','jmsbroker','#em-esb.synapsedev.com:2506'),('ESBInfo','jmsdiscoverytimesec','60'),('ESBInfo','jmspassword','Administrator'),('ESBInfo','jmsqueryqueue','disney.xband.xbrc.queryqueue'),('ESBInfo','jmsrequestqueue','com.synapse.xbrcIn'),('ESBInfo','jmsrser',''),('ESBInfo','jmstopic','com.synapse.xbrc'),('ESBInfo','jmsuser','Administrator'),('ReaderConfig','gainsliderincrement','0.1'),('ReaderConfig','maximumgain','2.0'),('ReaderConfig','maximumthreshold','63'),('ReaderConfig','minimumgain','-1.0'),('ReaderConfig','minimumthreshold','0'),('ReaderConfig','thresholdsliderincrement','1'),('ReaderConfig','xbrcconfigmodseq','0'),('ServiceLocator','service_implementor_suffix','Imp'),('SpaceModelConfig','abandonmenttimeout_msec','3000'),('UIAttractionViewConfig','gridheight','9'),('UIAttractionViewConfig','gridsize','50'),('UIAttractionViewConfig','gridwidth','18'),('UIAttractionViewConfig','guesticonheight','24'),('UIAttractionViewConfig','guesticonspacing','10'),('UIAttractionViewConfig','guesticonstagger','4'),('UIAttractionViewConfig','guesticonwidth','24'),('UIAttractionViewConfig','maxguestspericon','5'),('UIAttractionViewConfig','maxiconspergriditem','3'),('UIConfig','controllerurl','http://localhost:8080'),('UIConfig','guestxviewcachetimesec','1800'),('UIConfig','xviewurl','http://localhost:8090/Xview');
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
  `LastReader` varchar(16) NOT NULL,
  `TimeEarliestAtReader` bigint(20) NOT NULL,
  `TimeLatestAtReader` bigint(20) NOT NULL,
  `TimeEntered` bigint(20) NOT NULL,
  `TimeMerged` bigint(20) NOT NULL,
  `TimeLoaded` bigint(20) NOT NULL,
  `TimeExited` bigint(20) NOT NULL,
  `CarID` varchar(16) NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1 COMMENT='Items that together make up the flow chart of the attraction';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GridItem`
--

LOCK TABLES `GridItem` WRITE;
/*!40000 ALTER TABLE `GridItem` DISABLE KEYS */;
INSERT INTO `GridItem` VALUES (72,'Gate',1,4,'INDETERMINATE','CDA Registration','','GateRed.png',0,0,1),(73,'Gate',3,4,'INDETERMINATE','Troll Embarkation','','GateRed.png',0,0,2),(74,'Gate',5,4,'INDETERMINATE','Digital Signage','','GateRed.png',0,0,3),(75,'Gate',7,4,'INDETERMINATE','CDA In Care','','GateRed.png',0,0,4),(76,'Gate',9,4,'INDETERMINATE','Self Service Station','','GateRed.png',0,0,5),(77,'Gate',7,1,'INDETERMINATE','Oceaneer Lab 1','','GateRed.png',0,0,6),(78,'Gate',9,1,'INDETERMINATE','Oceaneer Lab 2','','GateRed.png',0,0,7);
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
-- Table structure for table `KnownBandIDs`
--

DROP TABLE IF EXISTS `KnownBandIDs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KnownBandIDs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `BandID` varchar(16) NOT NULL,
  `IsTap` tinyint(1) NOT NULL DEFAULT '0',
  `BandType` varchar(16) NOT NULL DEFAULT 'CAR' COMMENT 'Allowed values are ''CAR'' and ''CAST''',
  `AssociatedData` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KnownBandIDs`
--

LOCK TABLES `KnownBandIDs` WRITE;
/*!40000 ALTER TABLE `KnownBandIDs` DISABLE KEYS */;
/*!40000 ALTER TABLE `KnownBandIDs` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (0,8,'UNKNOWN','',0,0,0,0),(1,2,'CDA Registration','CDA',0,0,0,1),(2,2,'Troll Embarkation','Embarkation',5,0,0,1),(3,2,'Digital Signage','Lounge',10,0,0,1),(4,2,'CDA In Care','CDA',15,0,0,1),(5,2,'Self Service Station','Lounge',20,0,0,1),(6,2,'Oceaneer Lab1','Oceaneer Lab',25,0,0,1),(7,2,'Oceaneer Lab2','Oceaneer Lab',30,0,0,1);
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
INSERT INTO `LocationType` VALUES (1,'Entry'),(2,'Waypoint'),(3,'Exit'),(4,'Load'),(5,'InCar'),(6,'Merge'),(7,'xPass Entry'),(8,'UNKNOWN');
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
) ENGINE=InnoDB AUTO_INCREMENT=3055 DEFAULT CHARSET=latin1 COMMENT='XML payloads for outgoing JMS or HTTP messages';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Messages`
--

LOCK TABLES `Messages` WRITE;
/*!40000 ALTER TABLE `Messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `Messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PEGuestAction`
--

DROP TABLE IF EXISTS `PEGuestAction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PEGuestAction` (
  `id` int(11) NOT NULL,
  `guestId` int(11) NOT NULL,
  `seq` int(11) NOT NULL DEFAULT '0',
  `desc` varchar(150) NOT NULL,
  `type` varchar(32) NOT NULL,
  `delaySec` int(11) NOT NULL DEFAULT '0',
  `fireAfterEvent` tinyint(4) NOT NULL DEFAULT '0',
  `data` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_PEGuest` (`guestId`),
  CONSTRAINT `fk_PEGuest` FOREIGN KEY (`guestId`) REFERENCES `PEGuestTest` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PEGuestAction`
--

LOCK TABLES `PEGuestAction` WRITE;
/*!40000 ALTER TABLE `PEGuestAction` DISABLE KEYS */;
INSERT INTO `PEGuestAction` VALUES (1,1,1,'Tap','TAP',4,0,''),(2,2,1,'Tap','TAP',4,0,''),(3,2,2,'Scan','SCAN',4,1,'LUKE'),(4,3,3,'Scan again with good result','SCAN',4,1,'HANS'),(5,3,2,'Illegible scan','SCAN',4,1,'BADSCAN'),(6,3,1,'Tap','TAP',4,0,''),(7,3,4,'Wait','WAIT',2,1,''),(8,1,2,'Wait','WAIT',2,1,''),(9,2,3,'Wait','WAIT',2,1,''),(10,4,1,'Tap','TAP',4,0,''),(11,4,2,'Illegible scan','SCAN',4,1,'BADSCAN'),(12,4,3,'Illegible scan','SCAN',4,1,'BADSCAN2'),(13,4,4,'Illegible scan','SCAN',4,1,'BADSCAN3'),(14,4,4,'Wait','WAIT',2,1,'');
/*!40000 ALTER TABLE `PEGuestAction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PEGuestTest`
--

DROP TABLE IF EXISTS `PEGuestTest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PEGuestTest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `seq` int(11) NOT NULL DEFAULT '0',
  `name` varchar(64) NOT NULL,
  `bandId` varchar(16) NOT NULL,
  `child` tinyint(4) NOT NULL DEFAULT '0',
  `validBand` tinyint(4) NOT NULL DEFAULT '1',
  `reason` varchar(384) NOT NULL,
  `bioTemplate` varchar(32) NOT NULL,
  `omniLevel` int(11) NOT NULL DEFAULT '1',
  `finalResult` varchar(32) NOT NULL,
  `desc` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PEGuestTest`
--

LOCK TABLES `PEGuestTest` WRITE;
/*!40000 ALTER TABLE `PEGuestTest` DISABLE KEYS */;
INSERT INTO `PEGuestTest` VALUES (1,1,'R2','00801FB3AA873E04',1,1,'','R2',1,'ENTERED','Orrin who is a child taps once and enters.'),(2,2,'Luke','00801fb3aa862404',0,1,'','LUKE',1,'ENTERED','Luke taps then scans and enters.'),(3,3,'Hans','00801fb3aa883704',0,1,'','HANS',2,'ENTERED','Hans taps then scans badly then scans again and enters.'),(4,4,'Chewy','00801fb3aa873904',0,1,'Bad scan','CHEWY',1,'BLUELANE','Chewy scans badly three times.');
/*!40000 ALTER TABLE `PEGuestTest` ENABLE KEYS */;
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
  `signalStrengthThreshold` int(11) NOT NULL DEFAULT '0',
  `gain` double NOT NULL DEFAULT '1',
  `macAddress` varchar(32) DEFAULT NULL,
  `ipAddress` varchar(32) NOT NULL,
  `port` int(11) NOT NULL DEFAULT '80',
  `lastIdReceived` bigint(20) NOT NULL DEFAULT '-1',
  `locationId` int(11) NOT NULL,
  `timeLastHello` bigint(20) NOT NULL,
  `lane` int(11) NOT NULL,
  `omniDeviceId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reader_id_index` (`readerId`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=latin1 COMMENT='Information about configured readers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reader`
--

LOCK TABLES `Reader` WRITE;
/*!40000 ALTER TABLE `Reader` DISABLE KEYS */;
INSERT INTO `Reader` VALUES (1,2,'xFP1',0,0,'0','0',0,1,'00:91:FA:00:00:04','',8080,-1,1,0,0,0),(2,2,'xFP2',5,0,'0','0',0,1,'00:91:FA:00:00:37','',8080,-1,2,0,0,0),(3,2,'xFP3',10,0,'0','0',0,1,'00:91:FA:00:00:38','',8080,-1,3,0,0,0),(4,2,'xFP4',15,0,'0','0',0,1,'00:91:FA:00:00:39','',8080,-1,4,0,0,0),(5,2,'xFP5',20,0,'0','0',0,1,'00:91:FA:00:00:40','',8080,-1,5,0,0,0),(6,1,'Oceaneer1-1',0,10,'0','0',0,1,'02:03:90:01:0A:C4','',8080,-1,6,0,0,0),(7,1,'Oceaneer1-2',1,10,'0','0',0,1,'02:08:80:01:0B:C4','',8080,-1,6,0,0,0),(8,1,'Oceaneer1-3',0,11,'0','0',0,1,'02:03:60:01:0A:C4','',8080,-1,6,0,0,0),(9,1,'Oceaneer1-4',1,11,'0','0',0,1,'02:0A:F0:01:0C:C4','',8080,-1,6,0,0,0),(10,1,'Oceaneer2-1',20,10,'0','0',0,1,'02:18:80:00:0C:C4','',8080,-1,7,0,0,0),(11,1,'Oceaneer2-2',21,10,'0','0',0,1,'02:03:10:01:0A:C4','',8080,-1,7,0,0,0),(12,1,'Oceaneer2-3',20,11,'0','0',0,1,'02:14:A0:01:0A:C4','',8080,-1,7,0,0,0),(13,1,'Oceaneer2-4',21,11,'0','0',0,1,'02:16:E0:00:0C:C4','',8080,-1,7,0,0,0);
/*!40000 ALTER TABLE `Reader` ENABLE KEYS */;
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
-- Table structure for table `xBioDiagnosticPacket`
--

DROP TABLE IF EXISTS `xBioDiagnosticPacket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xBioDiagnosticPacket` (
  `xBioDiagnosticPacketId` int(11) NOT NULL AUTO_INCREMENT,
  `xFPEId` varchar(255) DEFAULT NULL,
  `v500DiagnosticPacket` text,
  `xFPEDiagnosticPacket` text,
  `xFP1DiagnosticPacket` text,
  `DateTimeStamp` datetime NOT NULL,
  PRIMARY KEY (`xBioDiagnosticPacketId`)
) ENGINE=MyISAM AUTO_INCREMENT=401 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xBioDiagnosticPacket`
--

LOCK TABLES `xBioDiagnosticPacket` WRITE;
/*!40000 ALTER TABLE `xBioDiagnosticPacket` DISABLE KEYS */;
/*!40000 ALTER TABLE `xBioDiagnosticPacket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xBioTransaction`
--

DROP TABLE IF EXISTS `xBioTransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xBioTransaction` (
  `xBioTransactionID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sensorID` int(10) unsigned NOT NULL,
  `sensorFWID` int(10) unsigned NOT NULL,
  `imageQuality` int(10) unsigned NOT NULL,
  `imageArea` int(10) unsigned NOT NULL,
  `minutiaeCount` int(10) unsigned NOT NULL,
  `liftOff` int(10) unsigned NOT NULL,
  `movement` int(10) unsigned NOT NULL,
  `position` int(10) unsigned NOT NULL,
  `template` blob,
  PRIMARY KEY (`xBioTransactionID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xBioTransaction`
--

LOCK TABLES `xBioTransaction` WRITE;
/*!40000 ALTER TABLE `xBioTransaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `xBioTransaction` ENABLE KEYS */;
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

-- Dump completed on 2011-11-15 16:49:25
