-- MySQL dump 10.13  Distrib 5.1.54, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: xbrctest
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
INSERT INTO `Config` VALUES ('AttractionModelConfig','abandonmenttimeout','3600'),('AttractionModelConfig','loadtype','trailing'),('AttractionModelConfig','metricsperiod','15'),('AttractionModelConfig','onridetimeout','2'),('ControllerInfo','abandonmenttimeout','3600'),('ControllerInfo','conn','jdbc:mysql://localhost/Mayhem?user=$USER&password=$PASSWORD'),('ControllerInfo','discoverynetprefix','10.'),('ControllerInfo','eventdumpfile','eventdump.txt'),('ControllerInfo','eventsperbatch','500'),('ControllerInfo','jmsretryperiod','1000'),('ControllerInfo','loadtype','trailing'),('ControllerInfo','metricsperiod','15'),('ControllerInfo','model','com.disney.xband.xbrc.attractionmodel.CEP'),('ControllerInfo','onridetimeout','2'),('ControllerInfo','pass','Mayhem!23'),('ControllerInfo','pushmode','true'),('ControllerInfo','url','http://localhost:8080/ControllerServer'),('ControllerInfo','user','EMUser'),('ControllerInfo','venue','xCoaster'),('ControllerInfo','verbose','false'),('ControllerInfo','xviewurl','http://localhost:8090/Xview'),('ESBInfo','jmsbroker','em-esb.synapsedev.com:2506'),('ESBInfo','jmsdiscoverytimesec','60'),('ESBInfo','jmspassword','Administrator'),('ESBInfo','jmsqueryqueue','disney.xband.xbrc.queryqueue'),('ESBInfo','jmsrequestqueue','com.synapse.xbrcIn'),('ESBInfo','jmsrser',''),('ESBInfo','jmstopic','com.synapse.xbrc'),('ESBInfo','jmsuser','Administrator'),('ReaderConfig','gainsliderincrement','0.1'),('ReaderConfig','maximumgain','2.0'),('ReaderConfig','maximumthreshold','63'),('ReaderConfig','minimumgain','-1.0'),('ReaderConfig','minimumthreshold','0'),('ReaderConfig','thresholdsliderincrement','1'),('ReaderConfig','xbrcconfigmodseq','0'),('ServiceLocator','service_implementor_suffix','Imp'),('UIAttractionViewConfig','gridheight','9'),('UIAttractionViewConfig','gridsize','50'),('UIAttractionViewConfig','gridwidth','18'),('UIAttractionViewConfig','guesticonheight','24'),('UIAttractionViewConfig','guesticonspacing','10'),('UIAttractionViewConfig','guesticonstagger','4'),('UIAttractionViewConfig','guesticonwidth','24'),('UIAttractionViewConfig','maxguestspericon','5'),('UIAttractionViewConfig','maxiconspergriditem','3'),('UIConfig','controllerurl','http://localhost:8080/ControllerServer'),('UIConfig','guestxviewcachetimesec','1800'),('UIConfig','xviewurl','http://localhost:8090/Xview');
/*!40000 ALTER TABLE `Config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Events`
--

DROP TABLE IF EXISTS `Events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Events` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ReaderID` varchar(32) NOT NULL,
  `BandID` varchar(32) NOT NULL COMMENT 'Can be either tap or LRR id',
  `Timestamp` bigint(20) NOT NULL DEFAULT '0',
  `Strength` int(11) NOT NULL DEFAULT '0',
  `PacketSequence` int(11) NOT NULL DEFAULT '0',
  `Frequency` int(11) NOT NULL DEFAULT '0',
  `Channel` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `timestamp_index` (`Timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Events`
--

LOCK TABLES `Events` WRITE;
/*!40000 ALTER TABLE `Events` DISABLE KEYS */;
/*!40000 ALTER TABLE `Events` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1 COMMENT='Items that together make up the flow chart of the attraction';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GridItem`
--

LOCK TABLES `GridItem` WRITE;
/*!40000 ALTER TABLE `GridItem` DISABLE KEYS */;
INSERT INTO `GridItem` VALUES (1,'Gate',2,3,'','Entry','Entry for non-XPass guests',NULL,0,0,1),(2,'HPath',3,3,'','','Guests have entered the attraction',NULL,1,0,1),(3,'HPath',4,3,'','','Guests have entered the attraction',NULL,2,0,1),(4,'Gate',5,1,'','Merge','Merge point',NULL,0,0,4),(5,'Gate',3,1,'','XPass Entry','Entry for XPass guests','GateGreen.png',0,1,5),(6,'HPath',4,1,'','','XPass guests have entered the attraction',NULL,1,1,1),(8,'VPath',5,4,'','','Guests have entered the attraction',NULL,4,0,1),(9,'Gate',5,5,'','Queue1','Queue waypoint 1','GateRed.png',0,0,3),(10,'HPath',6,5,'','','Guests have passed the Queue1 waypoint',NULL,1,0,3),(11,'Gate',7,5,'','Queue2','Queue waypoint 2','GateRed.png',0,0,2),(12,'TWest',8,5,'','','Guests have passed the Queue2 waypoint',NULL,1,0,2),(13,'ESTurn',8,4,'','','Guests have passed the Queue2 waypoint',NULL,2,0,2),(14,'ENTurn',8,6,'','','Guests have passed the Queue2 waypoint','',3,0,2),(15,'Gate',10,4,'','Load One','Loading point one','GateYellow.png',0,0,7),(16,'Gate',10,6,'','Load Two','Loading point two','GateYellow.png',0,0,8),(17,'WSTurn',11,4,'RIDING','','Guests are riding',NULL,1,0,NULL),(18,'WNTurn',11,6,'RIDING','','Guests are riding',NULL,2,0,NULL),(19,'TEast',11,5,'RIDING','','Guests are riding',NULL,3,0,NULL),(20,'HPath',12,5,'RIDING','','Guests are riding',NULL,4,0,NULL),(21,'Gate',13,5,'','Exit','Attraction exit',NULL,0,0,6),(22,'HPath',14,5,'','','Guests left the attaction',NULL,1,0,6),(23,'TWest',5,3,'','','Guests have entered the attraction','',3,0,1),(24,'HPath',9,4,'','','Guests are loading at Load One',NULL,0,0,7),(25,'HPath',9,6,'','','Guests are loading at Load Two','',0,0,8),(26,'VPath',5,2,'','','XPass guests passed the Merge point',NULL,0,1,4);
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
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
  `x` double NOT NULL,
  `y` double NOT NULL,
  `singulationTypeId` int(11) NOT NULL,
  `eventGenerationTypeId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LocationType` (`locationTypeId`),
  CONSTRAINT `FK_LocationType` FOREIGN KEY (`locationTypeId`) REFERENCES `LocationType` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (1,1,'Entry',1,2,0,2),(2,2,'Queue2',51,7,0,1),(3,2,'Queue1',26,7,0,1),(4,6,'Merge',15,10,0,1),(5,7,'xPassEntry',10,10,0,1),(6,3,'Exit',101,7,0,1),(7,4,'Load1',76,2,1,1),(8,4,'Load2',76,12,1,1),(11,8,'UNKNOWN',0,0,0,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=2613 DEFAULT CHARSET=latin1 COMMENT='XML payloads for outgoing JMS or HTTP messages';
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
  `tap` tinyint(1) NOT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1 COMMENT='Information about configured readers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reader`
--

LOCK TABLES `Reader` WRITE;
/*!40000 ALTER TABLE `Reader` DISABLE KEYS */;
INSERT INTO `Reader` VALUES (2,0,'entry-1',0,0,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8011,-1,1,1316460652841,0),(3,0,'entry-2',3,0,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8012,-1,1,1316460652964,0),(4,0,'entry-3',0,3,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8013,-1,1,1316460652965,0),(5,0,'entry-4',3,3,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8014,-1,1,1316460652840,0),(6,0,'queue2-1',50,5,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8021,-1,2,1316460652791,0),(7,0,'queue2-2',53,5,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8022,-1,2,1316460652840,0),(8,0,'queue2-3',50,8,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8023,-1,2,1316460652984,0),(9,0,'queue2-4',53,8,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8024,-1,2,1316460652798,0),(10,0,'queue1-1',25,5,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8031,-1,3,1316460652783,0),(11,0,'queue1-2',28,5,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8032,-1,3,1316460652867,0),(12,0,'queue1-3',25,8,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8033,-1,3,1316460653012,0),(13,0,'queue1-4',28,8,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8034,-1,3,1316460652868,0),(14,1,'merge',15,10,'merge','',0,1,'00:00:00:00:00:00','127.0.0.1',8041,-1,4,1316460652964,0),(15,1,'xpassentry',10,10,'xpassentry','',0,1,'00:00:00:00:00:00','127.0.0.1',8051,-1,5,1316460652839,0),(16,0,'exit-1',100,5,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8061,-1,6,1316460652987,0),(17,0,'exit-2',103,5,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8062,-1,6,1316460652796,0),(18,0,'exit-3',100,8,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8063,-1,6,1316460652987,0),(19,0,'exit-4',103,8,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8064,-1,6,1316460652871,0),(20,0,'load1-1',75,0,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8071,-1,7,1316460652972,0),(21,0,'load1-2',78,0,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8072,-1,7,1316460652972,0),(22,0,'load1-3',75,3,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8073,-1,7,1316460652878,0),(23,0,'load1-4',78,3,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8074,-1,7,1316460652880,0),(24,0,'load2-1',75,10,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8081,-1,8,1316460652939,0),(25,0,'load2-2',78,10,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8082,-1,8,1316460652879,0),(26,0,'load2-3',75,13,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8083,-1,8,1316460652781,0),(27,0,'load2-4',78,13,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8084,-1,8,1316460652973,0),(31,0,'George',NULL,NULL,NULL,NULL,0,1,'00:15:C9:28:E5:83','10.75.2.234',8080,-1,11,1313696019686,0);
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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COMMENT='Simulated "walls" in attration';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Walls`
--

LOCK TABLES `Walls` WRITE;
/*!40000 ALTER TABLE `Walls` DISABLE KEYS */;
INSERT INTO `Walls` VALUES (10,10,100,10,1),(0,5,15,5,2),(0,0,100,0,3);
/*!40000 ALTER TABLE `Walls` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-02-23 17:47:06
