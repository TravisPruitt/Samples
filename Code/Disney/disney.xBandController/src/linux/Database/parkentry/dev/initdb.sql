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
  `locationName` varchar(32) NOT NULL,
  `omniNumericId` varchar(32) DEFAULT NULL
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
-- Table structure for table `CastMember`
--

DROP TABLE IF EXISTS `CastMember`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CastMember` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `bandId` varchar(16) NOT NULL,
  `externalId` varchar(128) NOT NULL,
  `omniUsername` varchar(15) NOT NULL,
  `omniPassword` varchar(15) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CastMember`
--

LOCK TABLES `CastMember` WRITE;
/*!40000 ALTER TABLE `CastMember` DISABLE KEYS */;
INSERT INTO `CastMember` VALUES (1,'Arek','arek','','800011','11111',1),(6,'Cast Member Five','008023C002AC9004','','800011','11111',1),(5,'Cast Member Four','008023C002ABFC04','','800011','11111',1),(4,'Cast Member Three','008023C002AC2104','','800011','11111',1),(3,'Cast Member Two','008023C002AC7E04','','800011','11111',1),(2,'Cast Member One','008023C002AB7404','','800011','11111',1);
/*!40000 ALTER TABLE `CastMember` ENABLE KEYS */;
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
INSERT INTO `Config` VALUES ('ControllerInfo','adjusteventtimes','true'),('ControllerInfo','bioimagecapure','false'),('ControllerInfo','dateformat','yyyy-MM-dd HH:mm:ss z'),('ControllerInfo','discoverynetprefix',''),('ControllerInfo','eventdumpfile','/var/log/xbrc/eventdump.txt'),('ControllerInfo','eventprocessingperiod_msec','200'),('ControllerInfo','httpport','8080'),('ControllerInfo','httpsport','0'),('ControllerInfo','idmscachetime_sec','1800'),('ControllerInfo','jmsretryperiod','1000'),('ControllerInfo','maxreadsbeforepurge','8'),('ControllerInfo','messagestorageperiod','3600'),('ControllerInfo','model','com.disney.xband.xbrc.parkentrymodel.CEP'),('ControllerInfo','name','xBRC'),('ControllerInfo','ownipprefix','192.'),('ControllerInfo','perfmetricsperiod','300'),('ControllerInfo','preferredguestidtype',''),('ControllerInfo','readerdatasendperiod_msec','100'),('ControllerInfo','readerhellotimeoutsec','90'),('ControllerInfo','readerhttptimeout_msec','3000'),('ControllerInfo','reducedataslicing','true'),('ControllerInfo','reversetapids','false'),('ControllerInfo','rfidtestmode','false'),('ControllerInfo','securetapid','false'),('ControllerInfo','setreadertime','true'),('ControllerInfo','singulationalgorithm','max'),('ControllerInfo','snmp_enabled','false'),('ControllerInfo','statesaveperiod_sec','1000'),('ControllerInfo','transmitxbrperiod_msec','100'),('ControllerInfo','updatestreamhttptimeout_msec','3000'),('ControllerInfo','updatestreamurl',''),('ControllerInfo','venue','xBRC'),('ControllerInfo','verbose','false'),('ControllerInfo','watchedbandtimeout','180'),('ControllerInfo','wwwdir','/usr/share/xbrc/www'),('ControllerInfo','xviewurl','http://localhost:8090/Xview'),('ESBInfo','jmsbroker','#'),('ESBInfo','jmsdiscoverytimesec','60'),('ESBInfo','jmspassword',''),('ESBInfo','jmstopic',''),('ESBInfo','jmsuser',''),('ParkEntryModelConfig','abandonmenttimesec','25'),('ParkEntryModelConfig','castappmessagetimeoutsec','15'),('ParkEntryModelConfig','castappserveraddress','localhost'),('ParkEntryModelConfig','castappserverobserveraddress','localhost'),('ParkEntryModelConfig','castappserverobserverport','33435'),('ParkEntryModelConfig','castappserverport','33434'),('ParkEntryModelConfig','castlogontimeoutsec','45'),('ParkEntryModelConfig','greenlighttimeoutms','2500'),('ParkEntryModelConfig','guestretaptimeoutms','1000'),('ParkEntryModelConfig','maxfpscanretry','3'),('ParkEntryModelConfig','omniconnecttimeoutms','4000'),('ParkEntryModelConfig','omnirequesttimeoutms','15000'),('ParkEntryModelConfig','omniticketaddress','localhost'),('ParkEntryModelConfig','omniticketport','9920'),('ParkEntryModelConfig','readerconnecttimeoutms','2000'),('ParkEntryModelConfig','savebioimages','none'),('ParkEntryModelConfig','savebioimagesfrequency','1'),('SNMPConfig','agentdispatcherpool','10'),('SNMPConfig','agentlistenport','8161'),('SNMPConfig','community','mayhem'),('SNMPConfig','context','public'),('SNMPConfig','dateformat','yyyy-MM-dd HH:mm:ss z'),('SNMPConfig','getretries','0'),('SNMPConfig','gettimeout','50000'),('SNMPConfig','managerdispatcherpool','10'),('SNMPConfig','maxoids','2000'),('SNMPConfig','readerhttpconntimeout','1000'),('SNMPConfig','trapaddress','0.0.0.0/162');
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
  `bandRfid` varchar(32) NOT NULL,
  `HasXPass` tinyint(1) NOT NULL,
  `State` varchar(16) NOT NULL,
  `LastReader` varchar(16) NOT NULL,
  `TimeEarliestAtReader` bigint(20) NOT NULL,
  `TimeLatestAtReader` bigint(20) DEFAULT NULL,
  `TimeArrived` bigint(20) NOT NULL,
  `TimeDeparted` bigint(20) NOT NULL,
  `fpScanCount` int(11) NOT NULL DEFAULT '0',
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Items that together make up the flow chart of the attraction';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GridItem`
--

LOCK TABLES `GridItem` WRITE;
/*!40000 ALTER TABLE `GridItem` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Location`
--

LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (0,0,'UNKNOWN','',0,0,0,0),(1,1,'Entry-New','',1,2,0,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=348 DEFAULT CHARSET=latin1 COMMENT='XML payloads for outgoing JMS or HTTP messages';
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
  `id` int(11) NOT NULL AUTO_INCREMENT,
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
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PEGuestAction`
--

LOCK TABLES `PEGuestAction` WRITE;
/*!40000 ALTER TABLE `PEGuestAction` DISABLE KEYS */;
INSERT INTO `PEGuestAction` VALUES (1,1,1,'Tap','TAP',1,0,''),(2,1,2,'Wait','WAIT',1,1,''),(3,2,1,'Tap','TAP',4,0,''),(4,2,2,'Scan','SCAN',4,1,'BADDATA'),(6,3,1,'Tap','TAP',1,0,''),(7,5,1,'Tap','TAP',1,0,''),(8,5,2,'Wait','WAIT',4,1,''),(9,6,1,'Tap','TAP',1,0,''),(10,6,2,'Wait','WAIT',4,1,''),(11,7,1,'Tap','TAP',1,0,''),(12,8,1,'Tap','TAP',1,0,''),(13,7,2,'Scan','SCAN',4,1,'EEYORE'),(14,8,2,'Scan','SCAN',4,1,'BADDATA'),(16,10,1,'Tap','TAP',1,0,''),(17,8,3,'Wait','WAIT',4,1,''),(19,9,1,'Tap','TAP',1,0,''),(20,4,1,'Tap','TAP',1,0,''),(22,10,2,'Wait','WAIT',4,1,''),(23,4,2,'Scan','SCAN',1,1,'BADDATA'),(24,3,2,'Wait','WAIT',4,1,''),(25,11,1,'Tap','TAP',1,0,''),(26,2,3,'Wait','WAIT',4,1,''),(27,11,2,'Wait','WAIT',4,1,''),(35,4,3,'Scan','SCAN',1,1,'BADDATA'),(37,4,4,'Scan','SCAN',1,1,'BADDATA'),(42,7,3,'Wait','WAIT',4,1,''),(43,4,4,'Wait','WAIT',4,1,''),(44,8,4,'Wait','WAIT',4,1,''),(45,13,1,'Tap','TAP',1,0,''),(46,13,2,'Scan','SCAN',4,1,'AAA'),(47,13,3,'Wait','WAIT',4,1,''),(48,14,1,'Tap','TAP',1,0,''),(49,14,2,'Scan','SCAN',4,1,'BADDATA'),(50,14,3,'Scan','SCAN',4,1,'BBB'),(51,14,4,'Wait','WAIT',4,1,''),(52,15,1,'Tap','TAP',1,0,''),(53,15,2,'Scan','SCAN',4,1,'BADDATA'),(54,15,3,'Scan','SCAN',4,1,'BADDATA'),(55,15,4,'Scan','SCAN',4,1,'CCC'),(56,15,5,'Wait','WAIT',4,1,''),(57,16,1,'Tap','TAP',1,0,''),(58,16,2,'Scan','SCAN',4,1,'BADDATA'),(59,16,3,'Scan','SCAN',4,1,'BADDATA'),(60,16,4,'Scan','SCAN',4,1,'BADDATA'),(61,16,5,'Wait','WAIT',4,1,''),(62,17,1,'Tap','TAP',1,0,''),(63,17,2,'Wait','WAIT',4,1,''),(64,18,1,'Tap','TAP',1,0,''),(65,18,2,'Wait','WAIT',4,1,''),(66,19,1,'Tap','TAP',1,0,''),(67,19,2,'Scan','SCAN',4,1,'BADDATA'),(68,19,3,'Wait','WAIT',4,1,''),(69,20,1,'Tap','TAP',1,0,''),(70,20,2,'Scan','SCAN',4,1,'BADDATA'),(71,20,3,'Scan','SCAN',4,1,'BADDATA'),(72,20,4,'Wait','WAIT',4,1,''),(73,21,1,'Wait','WAIT',4,1,''),(74,22,1,'Wait','WAIT',4,1,''),(75,23,1,'Tap','TAP',1,0,''),(76,23,2,'Wait','WAIT',4,1,''),(77,24,1,'Tap','TAP',1,0,''),(78,24,2,'Wait','WAIT',4,1,''),(79,25,1,'Tap','TAP',1,0,''),(80,26,1,'Tap','TAP',1,0,''),(81,27,1,'Tap','TAP',1,0,''),(82,28,1,'Tap','TAP',1,0,''),(83,28,2,'Scan','SCAN',4,1,'GITA'),(84,29,1,'Tap','TAP',1,0,''),(85,29,2,'Scan','SCAN',4,1,'BADDATA'),(86,30,1,'Tap','TAP',1,0,''),(87,31,1,'Tap','TAP',1,0,''),(88,28,3,'Wait','WAIT',4,1,'');
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
  `suiteId` int(11) NOT NULL DEFAULT '0',
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PEGuestTest`
--

LOCK TABLES `PEGuestTest` WRITE;
/*!40000 ALTER TABLE `PEGuestTest` DISABLE KEYS */;
INSERT INTO `PEGuestTest` VALUES (1,1,1,'R2','008023C002A50904',1,1,'','R2',1,'ENTERED','Orin who is a child taps once and enters.'),(2,1,2,'LUKE','008023C002A50905',0,1,'','LUKE',1,'ABANDONED','Luke is an adult who taps once and has invalid bioscan'),(3,1,3,'HANS','008023C002A50906',0,1,'','HANS',1,'ABANDONED','Hans is an adult who taps and abandons'),(4,1,4,'CHEWY','008023C002A50907',0,1,'','CHEWY',1,'BLUELANE','Chewy scans badly three times'),(5,2,1,'POOH','TEST-EC002A50950',1,1,'','POOH',1,'ENTERED','Pooh, a child, taps reader and enters'),(6,2,2,'PIGLET','TEST-BC002A50951',1,0,'','PIGLET',1,'BLUELANE','Piglet, a child, taps reader with invalid band'),(7,2,3,'EEYORE','TEST-EC002A50952',0,1,'','EEYORE',1,'ENTERED','Eeyore, an adult, taps and scans and enters'),(8,2,6,'KANGA','TEST-BC002A50953',0,1,'','KANGA',1,'BLUELANE','Kanga, an adult, taps once and has invalid bioscan'),(9,2,4,'RABBIT','TEST-AC002A50954',0,1,'','RABBIT',1,'ABANDONED','Rabbit, an adult, who taps and abandons'),(10,2,5,'TIGGER','TEST-BC002A50955',0,0,'','TIGGER',1,'BLUELANE','Tigger, an adult, taps with invalid band'),(11,3,1,'NEMO','TEST-EC002A50960',0,1,'','NEMO',1,'ABANDONED','Nemo, an adult, taps with invalid band and abandons'),(13,5,1,'AAA','0123456789012345',0,1,'','AAA',1,'ENTERED','AAA, an adult, taps & scans OK'),(14,5,2,'BBB','0123456789012346',0,1,'','BBB',1,'ENTERED','BBB, an adult, taps & scans twice (bad ,ok) and enters'),(15,5,3,'CCC','0123456789012347',0,1,'','CCC',1,'ENTERED','CCC, an adult, taps & scans 3 times (bad, bad, good) and enters'),(16,5,4,'DDD','0123456789012348',0,1,'','DDD',1,'BLUELANE','DDD, an adult, taps & scans 3 times (bad, bad, bad) and blue lane'),(17,5,5,'EEE','0123456789012349',0,0,'','EEE',1,'BLUELANE','EEE, an adult, taps with invalid band and bluelane'),(18,5,6,'FFF','0123456789012350',0,1,'','FFF',1,'ABANDONED','FFF, an adult, taps, then abandons'),(19,5,7,'GGG','0123456789012351',0,1,'','GGG',1,'ABANDONED','GGG, an adult, taps, scans and abandons'),(20,5,8,'HHH','0123456789012352',0,1,'','HHH',1,'ABANDONED','HHH, an adult, taps, scans twice (bad, bad) and abandons'),(21,5,9,'III','0123456789012353',0,1,'','III',1,'ABANDONED','III, an adult, neither taps nor scans and abandones'),(22,7,1,'JJJ','0123456789012354',1,1,'','',1,'ABANDONED','JJJ, a child abandons'),(23,6,2,'LLL','0123456789012355',1,1,'','',1,'ENTERED','LLL, a child taps and enters'),(24,6,3,'MMM','0123456789012356',1,0,'','',1,'BLUELANE','MMM, a child with invalid band goes to bluelane'),(25,1,1,'TAPPER','0123456789012357',1,1,'','TAPPER',1,'ENTERED','TAPPER, a child, taps once and enters'),(26,9,1,'GATHA','0123456789012358',1,1,'','',1,'ENTERED','GATHA, child who enters'),(27,9,2,'KAMAL','0123456789012359',1,0,'','KAMAL',1,'BLUELANE','KAMAL, child who goes to BLUELANE'),(28,9,3,'GITA','0123456789012360',0,1,'','GITA',1,'ENTERED','GITA, adult taps , scans, and enters'),(29,9,4,'KAYAN','0123456789012361',0,1,'','KAYAN',1,'BLUELANE','KAYAN, adult taps then invalid scan'),(30,9,5,'HANA','0123456789012362',0,1,'','HANA',1,'BLUELANE','HANA, adult taps and abandons'),(31,9,6,'MASOUD','0123456789012363',0,0,'','MASOUD',1,'BLUELANE','MASOUD, adult taps with invalid band');
/*!40000 ALTER TABLE `PEGuestTest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PEGuestTestSuite`
--

DROP TABLE IF EXISTS `PEGuestTestSuite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PEGuestTestSuite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PEGuestTestSuite`
--

LOCK TABLES `PEGuestTestSuite` WRITE;
/*!40000 ALTER TABLE `PEGuestTestSuite` DISABLE KEYS */;
INSERT INTO `PEGuestTestSuite` VALUES (1,'Internal test intended for simulated Omni server'),(2,'Build Verification Tests'),(3,'Sandbox - intended for trying out new tests'),(5,'Extended Tests - Adult'),(6,'Extended Tests - Child'),(7,'temp'),(8,'TAPPER'),(9,'BVT without WAIT commands');
/*!40000 ALTER TABLE `PEGuestTestSuite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PETransaction`
--

DROP TABLE IF EXISTS `PETransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PETransaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bandId` varchar(16) NOT NULL,
  `readerName` varchar(32) NOT NULL,
  `locationName` varchar(32) NOT NULL,
  `startTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `omniEntitlementDuration` int(11) DEFAULT '0',
  `omniEntitlementCount` int(11) DEFAULT '0',
  `scanDuration` int(11) DEFAULT '0',
  `scanCount` int(11) DEFAULT '0',
  `omniBioMatchDuration` int(11) DEFAULT '0',
  `omniBioMatchCount` int(11) DEFAULT '0',
  `totalDuration` int(11) DEFAULT '0',
  `scanErrorCount` int(11) DEFAULT '0',
  `scanErrorReasons` varchar(128) DEFAULT NULL,
  `blue` tinyint(1) DEFAULT '0',
  `abandoned` tinyint(1) DEFAULT '0',
  `finishTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tdssnSite` varchar(32) DEFAULT NULL,
  `tdssnStation` varchar(32) DEFAULT NULL,
  `tdssnDate` varchar(32) DEFAULT NULL,
  `tdssnTicketId` varchar(32) DEFAULT NULL,
  `omniEntitlementErrorCode` int(11) DEFAULT NULL,
  `omniEntitlementErrorDesc` varchar(64) DEFAULT NULL,
  `omniBioMatchErrorCode` int(11) DEFAULT NULL,
  `omniBioMatchErrorDesc` varchar(64) DEFAULT NULL,
  `decremented` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bandId` (`bandId`),
  KEY `startTime` (`startTime`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PETransaction`
--

LOCK TABLES `PETransaction` WRITE;
/*!40000 ALTER TABLE `PETransaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `PETransaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ProcessQCMetrics`
--

DROP TABLE IF EXISTS `ProcessQCMetrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ProcessQCMetrics` (
  `MetricID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `StartTimeStamp` bigint(20) NOT NULL DEFAULT '0',
  `EndTimeStamp` bigint(20) NOT NULL DEFAULT '0',
  `BlueLaneEventCount` int(10) unsigned NOT NULL,
  `AvgTransactionTime` int(10) unsigned NOT NULL,
  `RetriesCount` int(10) unsigned NOT NULL,
  `TotalTxnsCount` int(10) unsigned NOT NULL,
  `MVAvgBlueLaneEvents` int(10) unsigned NOT NULL,
  `MVAvgTxnTime` int(10) unsigned NOT NULL,
  `MVAvgRetries` int(10) unsigned NOT NULL,
  `AvgTxnTimeLCL` int(10) unsigned NOT NULL,
  `AvgTxnTimeUCL` int(10) unsigned NOT NULL,
  PRIMARY KEY (`MetricID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ProcessQCMetrics`
--

LOCK TABLES `ProcessQCMetrics` WRITE;
/*!40000 ALTER TABLE `ProcessQCMetrics` DISABLE KEYS */;
INSERT INTO `ProcessQCMetrics` VALUES (1,1329519676,1329519736,5,4,12,38,1,1,1,2,8),(2,1329519736,1329519796,1,7,10,35,1,1,1,2,8),(3,1329519796,1329519856,3,8,4,71,1,1,1,2,8),(4,1329519856,1329519916,10,14,12,71,1,1,1,2,8),(5,1329519916,1329519976,9,8,4,95,1,1,1,2,8),(6,1329519976,1329520036,1,9,4,41,1,1,1,2,8),(7,1329520036,1329520096,5,9,16,35,1,1,1,2,8),(8,1329520096,1329520156,5,7,6,52,1,1,1,2,8),(9,1329520156,1329520216,6,13,8,9,1,1,1,2,8),(10,1329520216,1329520276,2,14,20,66,1,1,1,2,8),(11,1329520276,1329520336,4,2,8,42,1,1,1,2,8),(12,1329520336,1329520396,0,17,4,51,1,1,1,2,8),(13,1329520396,1329520456,9,1,8,8,1,1,1,2,8),(14,1329520456,1329520516,1,5,18,81,1,1,1,2,8),(15,1329520516,1329520576,3,5,4,36,1,1,1,2,8),(16,1329520576,1329520636,1,13,16,15,1,1,1,2,8),(17,1329520636,1329520696,3,19,16,36,1,1,1,2,8),(18,1329520696,1329520756,3,8,0,96,1,1,1,2,8),(19,1329520756,1329520816,7,15,12,91,1,1,1,2,8),(20,1329520816,1329520876,6,7,18,46,1,1,1,2,8),(21,1329520876,1329520936,6,13,10,41,1,1,1,2,8),(22,1329520936,1329520996,6,17,6,100,1,1,1,2,8),(23,1329520996,1329521056,1,10,4,34,1,1,1,2,8),(24,1329521056,1329521116,2,0,8,13,1,1,1,2,8),(25,1329521116,1329521176,4,8,16,7,1,1,1,2,8),(26,1329521176,1329521236,8,18,18,4,1,1,1,2,8),(27,1329521236,1329521296,4,16,20,29,1,1,1,2,8),(28,1329521296,1329521356,6,2,12,89,1,1,1,2,8),(29,1329521356,1329521416,6,4,4,44,1,1,1,2,8),(30,1329521416,1329521476,6,15,18,5,1,1,1,2,8),(31,1329521476,1329521536,7,8,18,11,1,1,1,2,8),(32,1329521536,1329521596,10,10,12,38,1,1,1,2,8),(33,1329521596,1329521656,2,16,8,80,1,1,1,2,8),(34,1329521656,1329521716,7,19,14,81,1,1,1,2,8),(35,1329521716,1329521776,9,17,16,28,1,1,1,2,8),(36,1329521776,1329521836,0,8,16,70,1,1,1,2,8),(37,1329521836,1329521896,2,18,18,65,1,1,1,2,8),(38,1329521896,1329521956,6,5,6,92,1,1,1,2,8),(39,1329521956,1329522016,6,4,4,40,1,1,1,2,8),(40,1329522016,1329522076,4,17,2,96,1,1,1,2,8),(41,1329522076,1329522136,4,7,8,98,1,1,1,2,8),(42,1329522136,1329522196,7,9,4,85,1,1,1,2,8),(43,1329522196,1329522256,5,2,18,19,1,1,1,2,8),(44,1329522256,1329522316,3,15,20,58,1,1,1,2,8),(45,1329522316,1329522376,0,6,10,65,1,1,1,2,8),(46,1329522376,1329522436,7,10,10,81,1,1,1,2,8),(47,1329522436,1329522496,7,17,8,23,1,1,1,2,8),(48,1329522496,1329522556,1,11,12,55,1,1,1,2,8),(49,1329522556,1329522616,8,10,0,52,1,1,1,2,8),(50,1329522616,1329522676,6,8,4,63,1,1,1,2,8),(51,1329522676,1329522736,7,10,8,60,1,1,1,2,8),(52,1329522736,1329522796,8,20,14,37,1,1,1,2,8),(53,1329522796,1329522856,9,3,6,93,1,1,1,2,8),(54,1329522856,1329522916,8,1,20,71,1,1,1,2,8),(55,1329522916,1329522976,6,0,4,97,1,1,1,2,8),(56,1329522976,1329523036,2,4,6,92,1,1,1,2,8),(57,1329523036,1329523096,7,14,10,23,1,1,1,2,8),(58,1329523096,1329523156,7,0,18,21,1,1,1,2,8),(59,1329523156,1329523216,5,17,14,88,1,1,1,2,8),(60,1329523216,1329523276,4,5,2,95,1,1,1,2,8),(61,1329523276,1329523336,3,15,18,98,1,1,1,2,8),(62,1329523336,1329523396,3,15,16,57,1,1,1,2,8),(63,1329523396,1329523456,5,20,6,59,1,1,1,2,8),(64,1329523456,1329523516,0,6,10,76,1,1,1,2,8),(65,1329523516,1329523576,2,10,0,58,1,1,1,2,8),(66,1329523576,1329523636,9,11,4,12,1,1,1,2,8),(67,1329523636,1329523696,2,8,10,50,1,1,1,2,8),(68,1329523696,1329523756,9,18,16,26,1,1,1,2,8),(69,1329523756,1329523816,9,19,0,18,1,1,1,2,8),(70,1329523816,1329523876,8,14,18,56,1,1,1,2,8),(71,1329523876,1329523936,0,8,18,53,1,1,1,2,8),(72,1329523936,1329523996,8,9,18,90,1,1,1,2,8),(73,1329523996,1329524056,9,19,18,66,1,1,1,2,8),(74,1329524056,1329524116,6,3,18,22,1,1,1,2,8),(75,1329524116,1329524176,3,13,12,76,1,1,1,2,8),(76,1329524176,1329524236,1,7,6,42,1,1,1,2,8),(77,1329524236,1329524296,2,19,0,18,1,1,1,2,8),(78,1329524296,1329524356,9,20,4,11,1,1,1,2,8),(79,1329524356,1329524416,9,3,2,5,1,1,1,2,8),(80,1329524416,1329524476,9,10,16,30,1,1,1,2,8),(81,1329524476,1329524536,2,3,4,32,1,1,1,2,8),(82,1329524536,1329524596,1,11,10,1,1,1,1,2,8),(83,1329524596,1329524656,4,0,18,31,1,1,1,2,8),(84,1329524656,1329524716,9,15,20,64,1,1,1,2,8),(85,1329524716,1329524776,2,5,10,71,1,1,1,2,8),(86,1329524776,1329524836,1,8,14,14,1,1,1,2,8),(87,1329524836,1329524896,7,3,10,21,1,1,1,2,8),(88,1329524896,1329524956,5,16,12,37,1,1,1,2,8),(89,1329524956,1329525016,2,17,12,43,1,1,1,2,8),(90,1329525016,1329525076,4,14,8,68,1,1,1,2,8),(91,1329525076,1329525136,3,8,4,68,1,1,1,2,8),(92,1329525136,1329525196,8,4,8,39,1,1,1,2,8),(93,1329525196,1329525256,8,16,14,97,1,1,1,2,8),(94,1329525256,1329525316,8,1,18,24,1,1,1,2,8),(95,1329525316,1329525376,6,4,4,22,1,1,1,2,8),(96,1329525376,1329525436,6,12,18,96,1,1,1,2,8),(97,1329525436,1329525496,10,2,10,39,1,1,1,2,8),(98,1329525496,1329525556,3,9,6,26,1,1,1,2,8),(99,1329525556,1329525616,3,16,20,44,1,1,1,2,8),(100,1329525616,1329525676,3,8,18,36,1,1,1,2,8),(101,1329525676,1329525736,1,6,4,18,1,1,1,2,8),(102,1329525736,1329525796,3,17,6,17,1,1,1,2,8),(103,1329525796,1329525856,9,16,10,84,1,1,1,2,8),(104,1329525856,1329525916,8,13,16,91,1,1,1,2,8),(105,1329525916,1329525976,2,7,0,9,1,1,1,2,8),(106,1329525976,1329526036,4,16,14,93,1,1,1,2,8),(107,1329526036,1329526096,7,12,2,45,1,1,1,2,8),(108,1329526096,1329526156,1,20,14,65,1,1,1,2,8),(109,1329526156,1329526216,1,10,2,19,1,1,1,2,8),(110,1329526216,1329526276,6,8,4,72,1,1,1,2,8),(111,1329526276,1329526336,1,3,14,79,1,1,1,2,8),(112,1329526336,1329526396,9,7,0,93,1,1,1,2,8),(113,1329526396,1329526456,6,6,12,26,1,1,1,2,8),(114,1329526456,1329526516,4,6,6,68,1,1,1,2,8),(115,1329526516,1329526576,4,2,6,8,1,1,1,2,8),(116,1329526576,1329526636,6,11,2,62,1,1,1,2,8),(117,1329526636,1329526696,10,18,14,84,1,1,1,2,8),(118,1329526696,1329526756,1,15,10,45,1,1,1,2,8),(119,1329526756,1329526816,6,18,10,87,1,1,1,2,8),(120,1329526816,1329526876,8,9,18,98,1,1,1,2,8),(121,1329526876,1329526936,3,9,8,49,1,1,1,2,8),(122,1329526936,1329526996,4,9,2,94,1,1,1,2,8),(123,1329526996,1329527056,6,19,4,86,1,1,1,2,8),(124,1329527056,1329527116,8,12,10,69,1,1,1,2,8),(125,1329527116,1329527176,10,14,12,84,1,1,1,2,8),(126,1329527176,1329527236,5,16,12,73,1,1,1,2,8),(127,1329527236,1329527296,8,14,4,87,1,1,1,2,8),(128,1329527296,1329527356,8,4,16,15,1,1,1,2,8),(129,1329527356,1329527416,5,18,4,21,1,1,1,2,8),(130,1329527416,1329527476,5,15,4,91,1,1,1,2,8),(131,1329527476,1329527536,9,17,8,57,1,1,1,2,8),(132,1329527536,1329527596,6,6,12,31,1,1,1,2,8),(133,1329527596,1329527656,7,7,16,95,1,1,1,2,8),(134,1329527656,1329527716,3,15,18,9,1,1,1,2,8),(135,1329527716,1329527776,8,16,12,36,1,1,1,2,8),(136,1329527776,1329527836,1,13,16,19,1,1,1,2,8),(137,1329527836,1329527896,4,13,18,44,1,1,1,2,8),(138,1329527896,1329527956,6,14,12,86,1,1,1,2,8),(139,1329527956,1329528016,6,5,10,94,1,1,1,2,8),(140,1329528016,1329528076,1,15,6,47,1,1,1,2,8),(141,1329528076,1329528136,3,5,4,15,1,1,1,2,8),(142,1329528136,1329528196,2,14,18,25,1,1,1,2,8),(143,1329528196,1329528256,6,6,12,3,1,1,1,2,8),(144,1329528256,1329528316,4,1,18,34,1,1,1,2,8),(145,1329528316,1329528376,0,4,18,86,1,1,1,2,8),(146,1329528376,1329528436,6,10,10,31,1,1,1,2,8),(147,1329528436,1329528496,9,12,8,5,1,1,1,2,8),(148,1329528496,1329528556,1,5,0,29,1,1,1,2,8),(149,1329528556,1329528616,4,5,18,71,1,1,1,2,8),(150,1329528616,1329528676,9,9,10,97,1,1,1,2,8),(151,1329528676,1329528736,5,10,0,59,1,1,1,2,8),(152,1329528736,1329528796,9,15,20,69,1,1,1,2,8),(153,1329528796,1329528856,5,10,18,89,1,1,1,2,8),(154,1329528856,1329528916,8,11,4,40,1,1,1,2,8),(155,1329528916,1329528976,4,15,12,56,1,1,1,2,8),(156,1329528976,1329529036,1,18,2,93,1,1,1,2,8),(157,1329529036,1329529096,3,12,4,5,1,1,1,2,8),(158,1329529096,1329529156,8,12,16,12,1,1,1,2,8),(159,1329529156,1329529216,2,16,6,6,1,1,1,2,8),(160,1329529216,1329529276,4,19,8,7,1,1,1,2,8),(161,1329529276,1329529336,2,19,0,36,1,1,1,2,8),(162,1329529336,1329529396,7,8,20,56,1,1,1,2,8),(163,1329529396,1329529456,9,19,2,42,1,1,1,2,8),(164,1329529456,1329529516,9,6,16,13,1,1,1,2,8),(165,1329529516,1329529576,2,13,14,60,1,1,1,2,8),(166,1329529576,1329529636,8,7,8,69,1,1,1,2,8),(167,1329529636,1329529696,4,15,14,13,1,1,1,2,8),(168,1329529696,1329529756,6,12,4,14,1,1,1,2,8),(169,1329529756,1329529816,2,8,8,88,1,1,1,2,8),(170,1329529816,1329529876,2,6,16,35,1,1,1,2,8),(171,1329529876,1329529936,2,4,4,39,1,1,1,2,8),(172,1329529936,1329529996,4,16,16,39,1,1,1,2,8),(173,1329529996,1329530056,7,8,18,14,1,1,1,2,8),(174,1329530056,1329530116,1,20,14,68,1,1,1,2,8),(175,1329530116,1329530176,2,18,18,81,1,1,1,2,8),(176,1329530176,1329530236,4,9,0,77,1,1,1,2,8),(177,1329530236,1329530296,8,17,16,30,1,1,1,2,8),(178,1329530296,1329530356,2,20,8,15,1,1,1,2,8),(179,1329530356,1329530416,5,19,4,24,1,1,1,2,8),(180,1329530416,1329530476,6,5,10,59,1,1,1,2,8),(181,1329530476,1329530536,5,19,0,37,1,1,1,2,8),(182,1329530536,1329530596,8,13,20,92,1,1,1,2,8),(183,1329530596,1329530656,7,15,14,14,1,1,1,2,8),(184,1329530656,1329530716,6,15,16,62,1,1,1,2,8),(185,1329530716,1329530776,8,6,18,84,1,1,1,2,8),(186,1329530776,1329530836,4,8,16,83,1,1,1,2,8),(187,1329530836,1329530896,7,4,16,27,1,1,1,2,8),(188,1329530896,1329530956,0,8,18,24,1,1,1,2,8),(189,1329530956,1329531016,5,17,16,45,1,1,1,2,8),(190,1329531016,1329531076,8,15,6,4,1,1,1,2,8),(191,1329531076,1329531136,4,1,18,33,1,1,1,2,8),(192,1329531136,1329531196,10,18,12,15,1,1,1,2,8),(193,1329531196,1329531256,0,15,10,50,1,1,1,2,8),(194,1329531256,1329531316,9,18,18,63,1,1,1,2,8),(195,1329531316,1329531376,5,17,12,43,1,1,1,2,8),(196,1329531376,1329531436,4,10,10,90,1,1,1,2,8),(197,1329531436,1329531496,0,9,2,31,1,1,1,2,8),(198,1329531496,1329531556,2,20,8,6,1,1,1,2,8),(199,1329531556,1329531616,1,5,18,77,1,1,1,2,8),(200,1329531616,1329531676,2,12,8,14,1,1,1,2,8),(201,1329531676,1329531736,6,10,14,18,1,1,1,2,8),(202,1329531736,1329531796,7,19,14,59,1,1,1,2,8),(203,1329531796,1329531856,9,11,2,81,1,1,1,2,8),(204,1329531856,1329531916,8,13,18,42,1,1,1,2,8),(205,1329531916,1329531976,5,20,12,0,1,1,1,2,8),(206,1329531976,1329532036,2,2,18,31,1,1,1,2,8),(207,1329532036,1329532096,7,15,12,50,1,1,1,2,8),(208,1329532096,1329532156,9,16,8,58,1,1,1,2,8),(209,1329532156,1329532216,7,16,20,42,1,1,1,2,8),(210,1329532216,1329532276,2,10,4,20,1,1,1,2,8),(211,1329532276,1329532336,5,20,8,18,1,1,1,2,8),(212,1329532336,1329532396,6,8,4,76,1,1,1,2,8),(213,1329532396,1329532456,3,19,2,67,1,1,1,2,8),(214,1329532456,1329532516,0,20,20,89,1,1,1,2,8),(215,1329532516,1329532576,5,18,18,92,1,1,1,2,8),(216,1329532576,1329532636,8,9,14,8,1,1,1,2,8),(217,1329532636,1329532696,4,12,18,58,1,1,1,2,8),(218,1329532696,1329532756,3,13,8,89,1,1,1,2,8),(219,1329532756,1329532816,4,5,0,53,1,1,1,2,8),(220,1329532816,1329532876,5,17,18,71,1,1,1,2,8),(221,1329532876,1329532936,10,13,6,81,1,1,1,2,8),(222,1329532936,1329532996,10,11,18,61,1,1,1,2,8),(223,1329532996,1329533056,4,8,12,96,1,1,1,2,8),(224,1329533056,1329533116,9,15,18,19,1,1,1,2,8),(225,1329533116,1329533176,3,1,4,8,1,1,1,2,8),(226,1329533176,1329533236,7,5,2,62,1,1,1,2,8),(227,1329533236,1329533296,9,14,16,77,1,1,1,2,8),(228,1329533296,1329533356,5,7,4,85,1,1,1,2,8),(229,1329533356,1329533416,7,0,18,62,1,1,1,2,8),(230,1329533416,1329533476,3,13,6,38,1,1,1,2,8),(231,1329533476,1329533536,1,11,6,77,1,1,1,2,8),(232,1329533536,1329533596,0,16,20,55,1,1,1,2,8),(233,1329533596,1329533656,8,3,8,68,1,1,1,2,8),(234,1329533656,1329533716,2,15,6,8,1,1,1,2,8),(235,1329533716,1329533776,6,15,18,9,1,1,1,2,8),(236,1329533776,1329533836,9,3,0,69,1,1,1,2,8),(237,1329533836,1329533896,4,18,8,32,1,1,1,2,8),(238,1329533896,1329533956,3,16,16,90,1,1,1,2,8),(239,1329533956,1329534016,10,2,12,50,1,1,1,2,8),(240,1329534016,1329534076,9,15,4,87,1,1,1,2,8),(241,1329534076,1329534136,7,16,20,42,1,1,1,2,8),(242,1329534136,1329534196,2,13,16,5,1,1,1,2,8),(243,1329534196,1329534256,8,19,8,87,1,1,1,2,8),(244,1329534256,1329534316,3,15,20,59,1,1,1,2,8),(245,1329534316,1329534376,0,8,18,24,1,1,1,2,8),(246,1329534376,1329534436,6,1,12,67,1,1,1,2,8),(247,1329534436,1329534496,7,6,10,80,1,1,1,2,8),(248,1329534496,1329534556,4,8,20,55,1,1,1,2,8),(249,1329534556,1329534616,9,15,4,56,1,1,1,2,8),(250,1329534616,1329534676,3,17,6,94,1,1,1,2,8),(251,1329534676,1329534736,8,4,14,62,1,1,1,2,8),(252,1329534736,1329534796,1,17,18,82,1,1,1,2,8),(253,1329534796,1329534856,5,18,0,50,1,1,1,2,8),(254,1329534856,1329534916,4,11,12,10,1,1,1,2,8),(255,1329534916,1329534976,8,16,10,34,1,1,1,2,8),(256,1329534976,1329535036,1,6,8,86,1,1,1,2,8),(257,1329535036,1329535096,2,10,18,97,1,1,1,2,8),(258,1329535096,1329535156,1,15,8,63,1,1,1,2,8),(259,1329535156,1329535216,0,5,2,72,1,1,1,2,8),(260,1329535216,1329535276,3,10,10,14,1,1,1,2,8),(261,1329535276,1329535336,1,3,8,76,1,1,1,2,8),(262,1329535336,1329535396,5,1,14,65,1,1,1,2,8),(263,1329535396,1329535456,0,2,10,27,1,1,1,2,8),(264,1329535456,1329535516,8,1,20,76,1,1,1,2,8),(265,1329535516,1329535576,8,19,4,11,1,1,1,2,8),(266,1329535576,1329535636,10,13,6,35,1,1,1,2,8),(267,1329535636,1329535696,10,16,0,82,1,1,1,2,8),(268,1329535696,1329535756,10,10,8,78,1,1,1,2,8),(269,1329535756,1329535816,6,11,0,55,1,1,1,2,8),(270,1329535816,1329535876,6,9,10,4,1,1,1,2,8),(271,1329535876,1329535936,7,8,18,39,1,1,1,2,8),(272,1329535936,1329535996,2,14,18,55,1,1,1,2,8),(273,1329535996,1329536056,0,7,16,94,1,1,1,2,8),(274,1329536056,1329536116,3,12,2,91,1,1,1,2,8),(275,1329536116,1329536176,1,16,16,47,1,1,1,2,8),(276,1329536176,1329536236,0,12,18,88,1,1,1,2,8),(277,1329536236,1329536296,6,9,6,19,1,1,1,2,8),(278,1329536296,1329536356,1,16,14,3,1,1,1,2,8),(279,1329536356,1329536416,1,12,10,96,1,1,1,2,8),(280,1329536416,1329536476,2,0,10,36,1,1,1,2,8),(281,1329536476,1329536536,4,15,14,43,1,1,1,2,8),(282,1329536536,1329536596,9,3,2,8,1,1,1,2,8),(283,1329536596,1329536656,1,2,4,96,1,1,1,2,8),(284,1329536656,1329536716,0,7,14,36,1,1,1,2,8),(285,1329536716,1329536776,7,12,16,38,1,1,1,2,8),(286,1329536776,1329536836,3,12,2,42,1,1,1,2,8),(287,1329536836,1329536896,9,7,2,18,1,1,1,2,8),(288,1329536896,1329536956,8,4,16,49,1,1,1,2,8),(289,1329536956,1329537016,9,5,10,72,1,1,1,2,8),(290,1329537016,1329537076,1,7,8,95,1,1,1,2,8),(291,1329537076,1329537136,5,18,18,77,1,1,1,2,8),(292,1329537136,1329537196,1,8,14,3,1,1,1,2,8),(293,1329537196,1329537256,2,15,6,3,1,1,1,2,8),(294,1329537256,1329537316,4,16,18,13,1,1,1,2,8),(295,1329537316,1329537376,9,3,20,44,1,1,1,2,8),(296,1329537376,1329537436,3,4,2,95,1,1,1,2,8),(297,1329537436,1329537496,4,3,10,29,1,1,1,2,8),(298,1329537496,1329537556,8,4,10,82,1,1,1,2,8),(299,1329537556,1329537616,7,0,18,71,1,1,1,2,8),(300,1329537616,1329537676,7,10,8,53,1,1,1,2,8),(301,1329537676,1329537736,4,6,8,99,1,1,1,2,8),(302,1329537736,1329537796,8,3,4,48,1,1,1,2,8),(303,1329537796,1329537856,9,18,18,69,1,1,1,2,8),(304,1329537856,1329537916,8,1,16,77,1,1,1,2,8),(305,1329537916,1329537976,5,3,6,1,1,1,1,2,8),(306,1329537976,1329538036,2,18,18,80,1,1,1,2,8),(307,1329538036,1329538096,3,5,6,52,1,1,1,2,8),(308,1329538096,1329538156,9,14,20,71,1,1,1,2,8),(309,1329538156,1329538216,7,3,16,34,1,1,1,2,8),(310,1329538216,1329538276,4,4,14,71,1,1,1,2,8),(311,1329538276,1329538336,6,15,20,62,1,1,1,2,8),(312,1329538336,1329538396,2,2,16,94,1,1,1,2,8),(313,1329538396,1329538456,2,4,6,1,1,1,1,2,8),(314,1329538456,1329538516,1,10,4,36,1,1,1,2,8),(315,1329538516,1329538576,3,8,4,71,1,1,1,2,8),(316,1329538576,1329538636,9,13,6,62,1,1,1,2,8),(317,1329538636,1329538696,2,1,14,55,1,1,1,2,8),(318,1329538696,1329538756,5,18,0,40,1,1,1,2,8),(319,1329538756,1329538816,9,9,10,98,1,1,1,2,8),(320,1329538816,1329538876,5,12,8,22,1,1,1,2,8),(321,1329538876,1329538936,9,18,14,75,1,1,1,2,8),(322,1329538936,1329538996,7,8,16,76,1,1,1,2,8),(323,1329538996,1329539056,4,16,16,65,1,1,1,2,8),(324,1329539056,1329539116,8,19,8,19,1,1,1,2,8),(325,1329539116,1329539176,8,3,12,46,1,1,1,2,8),(326,1329539176,1329539236,5,5,14,68,1,1,1,2,8),(327,1329539236,1329539296,3,11,16,51,1,1,1,2,8),(328,1329539296,1329539356,0,14,8,67,1,1,1,2,8),(329,1329539356,1329539416,3,9,6,7,1,1,1,2,8),(330,1329539416,1329539476,5,10,18,6,1,1,1,2,8),(331,1329539476,1329539536,5,11,0,47,1,1,1,2,8),(332,1329539536,1329539596,3,3,16,64,1,1,1,2,8),(333,1329539596,1329539656,7,15,10,42,1,1,1,2,8),(334,1329539656,1329539716,5,5,16,4,1,1,1,2,8),(335,1329539716,1329539776,9,11,0,41,1,1,1,2,8),(336,1329539776,1329539836,10,14,12,72,1,1,1,2,8),(337,1329539836,1329539896,9,8,4,69,1,1,1,2,8),(338,1329539896,1329539956,10,15,18,17,1,1,1,2,8),(339,1329539956,1329540016,2,7,4,1,1,1,1,2,8),(340,1329540016,1329540076,4,1,0,5,1,1,1,2,8),(341,1329540076,1329540136,1,10,2,91,1,1,1,2,8),(342,1329540136,1329540196,3,17,6,11,1,1,1,2,8),(343,1329540196,1329540256,5,7,4,92,1,1,1,2,8),(344,1329540256,1329540316,0,5,6,56,1,1,1,2,8),(345,1329540316,1329540376,10,6,10,74,1,1,1,2,8),(346,1329540376,1329540436,1,5,0,40,1,1,1,2,8),(347,1329540436,1329540496,9,5,12,34,1,1,1,2,8),(348,1329540496,1329540556,8,2,18,45,1,1,1,2,8),(349,1329540556,1329540616,5,19,6,63,1,1,1,2,8),(350,1329540616,1329540676,3,11,16,50,1,1,1,2,8),(351,1329540676,1329540736,0,14,8,84,1,1,1,2,8),(352,1329540736,1329540796,0,14,6,45,1,1,1,2,8),(353,1329540796,1329540856,3,7,16,77,1,1,1,2,8),(354,1329540856,1329540916,5,7,2,57,1,1,1,2,8),(355,1329540916,1329540976,5,15,8,45,1,1,1,2,8),(356,1329540976,1329541036,2,14,16,98,1,1,1,2,8),(357,1329541036,1329541096,5,9,16,48,1,1,1,2,8),(358,1329541096,1329541156,1,4,12,33,1,1,1,2,8),(359,1329541156,1329541216,9,13,8,92,1,1,1,2,8),(360,1329541216,1329541276,5,17,16,19,1,1,1,2,8),(361,1329541276,1329541336,7,15,16,61,1,1,1,2,8),(362,1329541336,1329541396,7,17,2,1,1,1,1,2,8),(363,1329541396,1329541456,7,6,12,96,1,1,1,2,8),(364,1329541456,1329541516,1,9,2,19,1,1,1,2,8),(365,1329541516,1329541576,6,9,8,54,1,1,1,2,8),(366,1329541576,1329541636,6,7,2,24,1,1,1,2,8),(367,1329541636,1329541696,10,3,16,76,1,1,1,2,8),(368,1329541696,1329541756,3,0,6,43,1,1,1,2,8),(369,1329541756,1329541816,3,2,14,12,1,1,1,2,8),(370,1329541816,1329541876,5,6,16,26,1,1,1,2,8),(371,1329541876,1329541936,8,8,10,21,1,1,1,2,8),(372,1329541936,1329541996,6,8,2,52,1,1,1,2,8),(373,1329541996,1329542056,2,7,2,67,1,1,1,2,8),(374,1329542056,1329542116,10,15,18,9,1,1,1,2,8),(375,1329542116,1329542176,9,0,10,66,1,1,1,2,8),(376,1329542176,1329542236,7,6,8,38,1,1,1,2,8),(377,1329542236,1329542296,6,13,12,98,1,1,1,2,8),(378,1329542296,1329542356,2,17,14,89,1,1,1,2,8),(379,1329542356,1329542416,4,11,8,14,1,1,1,2,8),(380,1329542416,1329542476,7,18,12,17,1,1,1,2,8),(381,1329542476,1329542536,1,20,12,13,1,1,1,2,8),(382,1329542536,1329542596,8,11,8,54,1,1,1,2,8),(383,1329542596,1329542656,4,4,18,80,1,1,1,2,8),(384,1329542656,1329542716,4,10,8,44,1,1,1,2,8),(385,1329542716,1329542776,0,15,16,54,1,1,1,2,8),(386,1329542776,1329542836,4,8,16,62,1,1,1,2,8),(387,1329542836,1329542896,8,4,12,58,1,1,1,2,8),(388,1329542896,1329542956,9,19,0,15,1,1,1,2,8),(389,1329542956,1329543016,7,4,18,87,1,1,1,2,8),(390,1329543016,1329543076,6,11,18,60,1,1,1,2,8),(391,1329543076,1329543136,5,11,4,63,1,1,1,2,8),(392,1329543136,1329543196,4,5,0,35,1,1,1,2,8),(393,1329543196,1329543256,7,4,2,89,1,1,1,2,8),(394,1329543256,1329543316,1,18,4,45,1,1,1,2,8),(395,1329543316,1329543376,5,6,18,75,1,1,1,2,8),(396,1329543376,1329543436,9,9,8,84,1,1,1,2,8),(397,1329543436,1329543496,9,19,20,3,1,1,1,2,8),(398,1329543496,1329543556,3,5,10,70,1,1,1,2,8),(399,1329543556,1329543616,0,18,10,1,1,1,1,2,8),(400,1329543616,1329543676,4,19,12,28,1,1,1,2,8),(401,1329543676,1329543736,5,12,12,97,1,1,1,2,8),(402,1329543736,1329543796,2,2,16,84,1,1,1,2,8),(403,1329543796,1329543856,7,3,10,24,1,1,1,2,8),(404,1329543856,1329543916,6,6,12,8,1,1,1,2,8),(405,1329543916,1329543976,6,19,18,48,1,1,1,2,8),(406,1329543976,1329544036,8,9,18,99,1,1,1,2,8),(407,1329544036,1329544096,4,17,2,6,1,1,1,2,8),(408,1329544096,1329544156,10,12,2,85,1,1,1,2,8),(409,1329544156,1329544216,9,14,0,2,1,1,1,2,8),(410,1329544216,1329544276,0,19,16,5,1,1,1,2,8),(411,1329544276,1329544336,9,4,8,61,1,1,1,2,8),(412,1329544336,1329544396,7,14,8,78,1,1,1,2,8),(413,1329544396,1329544456,8,12,12,28,1,1,1,2,8),(414,1329544456,1329544516,6,0,6,69,1,1,1,2,8),(415,1329544516,1329544576,4,1,18,42,1,1,1,2,8),(416,1329544576,1329544636,4,12,20,93,1,1,1,2,8),(417,1329544636,1329544696,8,4,10,2,1,1,1,2,8),(418,1329544696,1329544756,6,15,2,31,1,1,1,2,8),(419,1329544756,1329544816,2,2,16,94,1,1,1,2,8),(420,1329544816,1329544876,2,0,12,86,1,1,1,2,8),(421,1329544876,1329544936,5,3,2,23,1,1,1,2,8),(422,1329544936,1329544996,7,19,10,82,1,1,1,2,8),(423,1329544996,1329545056,5,3,2,11,1,1,1,2,8),(424,1329545056,1329545116,3,19,18,84,1,1,1,2,8),(425,1329545116,1329545176,4,11,12,12,1,1,1,2,8),(426,1329545176,1329545236,9,5,10,74,1,1,1,2,8),(427,1329545236,1329545296,2,17,12,56,1,1,1,2,8),(428,1329545296,1329545356,9,20,4,84,1,1,1,2,8),(429,1329545356,1329545416,7,1,0,95,1,1,1,2,8),(430,1329545416,1329545476,7,14,10,24,1,1,1,2,8),(431,1329545476,1329545536,7,19,14,37,1,1,1,2,8),(432,1329545536,1329545596,9,6,20,80,1,1,1,2,8),(433,1329545596,1329545656,1,4,14,99,1,1,1,2,8),(434,1329545656,1329545716,8,18,4,42,1,1,1,2,8),(435,1329545716,1329545776,4,16,16,64,1,1,1,2,8),(436,1329545776,1329545836,8,18,6,60,1,1,1,2,8),(437,1329545836,1329545896,2,3,2,1,1,1,1,2,8),(438,1329545896,1329545956,8,1,14,50,1,1,1,2,8),(439,1329545956,1329546016,4,6,8,1,1,1,1,2,8),(440,1329546016,1329546076,9,10,16,49,1,1,1,2,8),(441,1329546076,1329546136,0,15,14,8,1,1,1,2,8),(442,1329546136,1329546196,4,12,18,66,1,1,1,2,8),(443,1329546196,1329546256,6,3,20,33,1,1,1,2,8),(444,1329546256,1329546316,7,13,2,67,1,1,1,2,8),(445,1329546316,1329546376,10,17,6,97,1,1,1,2,8),(446,1329546376,1329546436,9,15,2,98,1,1,1,2,8),(447,1329546436,1329546496,7,13,2,64,1,1,1,2,8),(448,1329546496,1329546556,8,5,16,4,1,1,1,2,8),(449,1329546556,1329546616,9,11,18,90,1,1,1,2,8),(450,1329546616,1329546676,8,5,16,26,1,1,1,2,8),(451,1329546676,1329546736,9,16,6,1,1,1,1,2,8),(452,1329546736,1329546796,2,18,0,40,1,1,1,2,8),(453,1329546796,1329546856,9,7,2,33,1,1,1,2,8),(454,1329546856,1329546916,4,18,8,24,1,1,1,2,8),(455,1329546916,1329546976,0,7,14,49,1,1,1,2,8),(456,1329546976,1329547036,3,4,18,2,1,1,1,2,8),(457,1329547036,1329547096,4,17,2,83,1,1,1,2,8),(458,1329547096,1329547156,9,4,2,88,1,1,1,2,8),(459,1329547156,1329547216,2,3,4,52,1,1,1,2,8),(460,1329547216,1329547276,1,14,8,93,1,1,1,2,8),(461,1329547276,1329547336,4,3,12,55,1,1,1,2,8),(462,1329547336,1329547396,10,3,20,27,1,1,1,2,8),(463,1329547396,1329547456,5,10,4,49,1,1,1,2,8),(464,1329547456,1329547516,8,12,12,14,1,1,1,2,8),(465,1329547516,1329547576,9,3,2,97,1,1,1,2,8),(466,1329547576,1329547636,6,20,4,95,1,1,1,2,8),(467,1329547636,1329547696,2,4,8,26,1,1,1,2,8),(468,1329547696,1329547756,2,4,8,50,1,1,1,2,8),(469,1329547756,1329547816,2,15,18,43,1,1,1,2,8),(470,1329547816,1329547876,4,11,14,0,1,1,1,2,8),(471,1329547876,1329547936,8,18,2,85,1,1,1,2,8),(472,1329547936,1329547996,9,2,12,91,1,1,1,2,8),(473,1329547996,1329548056,6,10,12,53,1,1,1,2,8),(474,1329548056,1329548116,8,10,2,77,1,1,1,2,8),(475,1329548116,1329548176,7,3,14,16,1,1,1,2,8),(476,1329548176,1329548236,6,12,2,70,1,1,1,2,8),(477,1329548236,1329548296,2,19,4,96,1,1,1,2,8),(478,1329548296,1329548356,3,11,18,76,1,1,1,2,8),(479,1329548356,1329548416,1,9,18,96,1,1,1,2,8),(480,1329548416,1329548476,2,0,10,53,1,1,1,2,8),(481,1329548476,1329548536,1,1,16,81,1,1,1,2,8),(482,1329548536,1329548596,7,3,12,59,1,1,1,2,8),(483,1329548596,1329548656,1,16,12,83,1,1,1,2,8),(484,1329548656,1329548716,2,10,0,47,1,1,1,2,8),(485,1329548716,1329548776,4,7,12,96,1,1,1,2,8),(486,1329548776,1329548836,0,4,20,21,1,1,1,2,8),(487,1329548836,1329548896,2,5,14,55,1,1,1,2,8),(488,1329548896,1329548956,8,8,10,40,1,1,1,2,8),(489,1329548956,1329549016,5,3,8,39,1,1,1,2,8),(490,1329549016,1329549076,8,1,14,52,1,1,1,2,8),(491,1329549076,1329549136,4,9,20,60,1,1,1,2,8),(492,1329549136,1329549196,1,10,8,53,1,1,1,2,8),(493,1329549196,1329549256,4,8,14,45,1,1,1,2,8),(494,1329549256,1329549316,1,1,0,5,1,1,1,2,8),(495,1329549316,1329549376,1,10,4,27,1,1,1,2,8),(496,1329549376,1329549436,9,9,16,57,1,1,1,2,8),(497,1329549436,1329549496,5,12,12,26,1,1,1,2,8),(498,1329549496,1329549556,5,12,12,23,1,1,1,2,8),(499,1329549556,1329549616,3,19,16,12,1,1,1,2,8),(500,1329549616,1329549676,2,13,14,48,1,1,1,2,8),(501,1329549676,1329549736,3,4,0,48,1,1,1,2,8),(502,1329549736,1329549796,4,7,16,76,1,1,1,2,8),(503,1329549796,1329549856,5,3,6,11,1,1,1,2,8),(504,1329549856,1329549916,6,15,18,36,1,1,1,2,8),(505,1329549916,1329549976,0,1,2,40,1,1,1,2,8),(506,1329549976,1329550036,7,6,8,11,1,1,1,2,8),(507,1329550036,1329550096,3,6,12,14,1,1,1,2,8),(508,1329550096,1329550156,8,16,8,73,1,1,1,2,8),(509,1329550156,1329550216,4,15,12,74,1,1,1,2,8),(510,1329550216,1329550276,9,5,14,51,1,1,1,2,8),(511,1329550276,1329550336,6,6,16,4,1,1,1,2,8),(512,1329550336,1329550396,8,1,16,70,1,1,1,2,8),(513,1329550396,1329550456,2,17,14,83,1,1,1,2,8),(514,1329550456,1329550516,1,4,10,8,1,1,1,2,8),(515,1329550516,1329550576,8,18,18,91,1,1,1,2,8),(516,1329550576,1329550636,8,7,6,24,1,1,1,2,8),(517,1329550636,1329550696,5,11,10,72,1,1,1,2,8),(518,1329550696,1329550756,1,10,2,84,1,1,1,2,8),(519,1329550756,1329550816,0,12,20,5,1,1,1,2,8),(520,1329550816,1329550876,3,9,8,49,1,1,1,2,8),(521,1329550876,1329550936,3,0,2,72,1,1,1,2,8),(522,1329550936,1329550996,2,14,0,95,1,1,1,2,8),(523,1329550996,1329551056,7,12,18,65,1,1,1,2,8),(524,1329551056,1329551116,6,1,10,33,1,1,1,2,8),(525,1329551116,1329551176,1,13,18,36,1,1,1,2,8),(526,1329551176,1329551236,2,19,2,65,1,1,1,2,8),(527,1329551236,1329551296,10,20,18,76,1,1,1,2,8),(528,1329551296,1329551356,10,13,6,73,1,1,1,2,8),(529,1329551356,1329551416,6,17,8,34,1,1,1,2,8),(530,1329551416,1329551476,6,19,0,17,1,1,1,2,8),(531,1329551476,1329551536,8,12,10,75,1,1,1,2,8),(532,1329551536,1329551596,2,17,12,39,1,1,1,2,8),(533,1329551596,1329551656,2,16,8,48,1,1,1,2,8),(534,1329551656,1329551716,3,20,2,46,1,1,1,2,8),(535,1329551716,1329551776,0,17,2,12,1,1,1,2,8),(536,1329551776,1329551836,2,13,12,27,1,1,1,2,8),(537,1329551836,1329551896,4,7,8,12,1,1,1,2,8),(538,1329551896,1329551956,3,2,14,8,1,1,1,2,8),(539,1329551956,1329552016,3,8,2,24,1,1,1,2,8),(540,1329552016,1329552076,9,16,8,34,1,1,1,2,8),(541,1329552076,1329552136,6,3,18,7,1,1,1,2,8),(542,1329552136,1329552196,6,17,8,51,1,1,1,2,8),(543,1329552196,1329552256,3,1,4,13,1,1,1,2,8),(544,1329552256,1329552316,9,4,4,50,1,1,1,2,8),(545,1329552316,1329552376,8,13,16,98,1,1,1,2,8),(546,1329552376,1329552436,5,14,20,69,1,1,1,2,8),(547,1329552436,1329552496,5,14,14,66,1,1,1,2,8),(548,1329552496,1329552556,1,11,8,19,1,1,1,2,8),(549,1329552556,1329552616,9,15,4,86,1,1,1,2,8),(550,1329552616,1329552676,6,8,6,19,1,1,1,2,8),(551,1329552676,1329552736,1,18,4,46,1,1,1,2,8),(552,1329552736,1329552796,6,11,0,49,1,1,1,2,8),(553,1329552796,1329552856,4,7,14,35,1,1,1,2,8),(554,1329552856,1329552916,7,8,18,21,1,1,1,2,8),(555,1329552916,1329552976,4,11,8,21,1,1,1,2,8),(556,1329552976,1329553036,10,4,0,65,1,1,1,2,8),(557,1329553036,1329553096,1,15,8,60,1,1,1,2,8),(558,1329553096,1329553156,9,10,16,76,1,1,1,2,8),(559,1329553156,1329553216,3,4,2,93,1,1,1,2,8),(560,1329553216,1329553276,3,16,20,60,1,1,1,2,8),(561,1329553276,1329553336,0,8,18,31,1,1,1,2,8),(562,1329553336,1329553396,8,5,14,96,1,1,1,2,8),(563,1329553396,1329553456,6,18,18,90,1,1,1,2,8),(564,1329553456,1329553516,7,16,20,47,1,1,1,2,8),(565,1329553516,1329553576,4,10,8,53,1,1,1,2,8),(566,1329553576,1329553636,4,8,14,59,1,1,1,2,8),(567,1329553636,1329553696,7,14,8,97,1,1,1,2,8),(568,1329553696,1329553756,6,4,4,25,1,1,1,2,8),(569,1329553756,1329553816,7,17,20,45,1,1,1,2,8),(570,1329553816,1329553876,3,2,14,6,1,1,1,2,8),(571,1329553876,1329553936,3,5,6,82,1,1,1,2,8),(572,1329553936,1329553996,2,12,6,63,1,1,1,2,8),(573,1329553996,1329554056,3,15,14,44,1,1,1,2,8),(574,1329554056,1329554116,0,15,14,9,1,1,1,2,8),(575,1329554116,1329554176,4,20,12,88,1,1,1,2,8),(576,1329554176,1329554236,7,17,2,16,1,1,1,2,8),(577,1329554236,1329554296,4,9,20,65,1,1,1,2,8),(578,1329554296,1329554356,3,10,14,12,1,1,1,2,8),(579,1329554356,1329554416,4,11,12,50,1,1,1,2,8),(580,1329554416,1329554476,6,10,14,13,1,1,1,2,8),(581,1329554476,1329554536,4,17,18,8,1,1,1,2,8),(582,1329554536,1329554596,6,17,10,78,1,1,1,2,8),(583,1329554596,1329554656,5,19,6,90,1,1,1,2,8),(584,1329554656,1329554716,5,13,18,62,1,1,1,2,8),(585,1329554716,1329554776,3,14,12,95,1,1,1,2,8),(586,1329554776,1329554836,9,15,18,37,1,1,1,2,8),(587,1329554836,1329554896,1,8,16,70,1,1,1,2,8),(588,1329554896,1329554956,1,10,4,33,1,1,1,2,8),(589,1329554956,1329555016,2,15,8,58,1,1,1,2,8),(590,1329555016,1329555076,8,2,4,82,1,1,1,2,8),(591,1329555076,1329555136,4,13,0,9,1,1,1,2,8),(592,1329555136,1329555196,4,15,12,88,1,1,1,2,8),(593,1329555196,1329555256,5,14,2,51,1,1,1,2,8),(594,1329555256,1329555316,2,7,4,2,1,1,1,2,8),(595,1329555316,1329555376,5,4,14,78,1,1,1,2,8),(596,1329555376,1329555436,8,18,20,7,1,1,1,2,8),(597,1329555436,1329555496,5,7,4,77,1,1,1,2,8),(598,1329555496,1329555556,4,13,20,7,1,1,1,2,8),(599,1329555556,1329555616,4,11,14,97,1,1,1,2,8),(600,1329555616,1329555676,7,10,6,28,1,1,1,2,8),(601,1329555676,1329555736,4,19,16,91,1,1,1,2,8),(602,1329555736,1329555796,2,10,16,53,1,1,1,2,8),(603,1329555796,1329555856,2,9,12,74,1,1,1,2,8),(604,1329555856,1329555916,9,2,18,19,1,1,1,2,8),(605,1329555916,1329555976,2,13,10,57,1,1,1,2,8),(606,1329555976,1329556036,4,1,4,65,1,1,1,2,8),(607,1329556036,1329556096,8,1,16,89,1,1,1,2,8),(608,1329556096,1329556156,0,11,14,60,1,1,1,2,8),(609,1329556156,1329556216,0,7,14,21,1,1,1,2,8),(610,1329556216,1329556276,1,17,0,50,1,1,1,2,8),(611,1329556276,1329556336,5,16,12,72,1,1,1,2,8),(612,1329556336,1329556396,7,11,10,65,1,1,1,2,8),(613,1329556396,1329556456,9,10,16,38,1,1,1,2,8),(614,1329556456,1329556516,6,16,6,85,1,1,1,2,8),(615,1329556516,1329556576,5,20,8,1,1,1,1,2,8),(616,1329556576,1329556636,9,6,16,26,1,1,1,2,8),(617,1329556636,1329556696,8,8,8,5,1,1,1,2,8),(618,1329556696,1329556756,9,10,16,47,1,1,1,2,8),(619,1329556756,1329556816,9,5,8,22,1,1,1,2,8),(620,1329556816,1329556876,9,20,4,0,1,1,1,2,8),(621,1329556876,1329556936,4,2,4,76,1,1,1,2,8),(622,1329556936,1329556996,2,13,16,80,1,1,1,2,8),(623,1329556996,1329557056,7,3,14,87,1,1,1,2,8),(624,1329557056,1329557116,3,2,8,95,1,1,1,2,8),(625,1329557116,1329557176,4,3,12,35,1,1,1,2,8),(626,1329557176,1329557236,1,7,8,1,1,1,1,2,8),(627,1329557236,1329557296,9,6,18,80,1,1,1,2,8),(628,1329557296,1329557356,2,8,10,12,1,1,1,2,8),(629,1329557356,1329557416,2,16,4,76,1,1,1,2,8),(630,1329557416,1329557476,1,7,6,63,1,1,1,2,8),(631,1329557476,1329557536,2,18,18,0,1,1,1,2,8),(632,1329557536,1329557596,2,2,18,37,1,1,1,2,8),(633,1329557596,1329557656,10,17,6,97,1,1,1,2,8),(634,1329557656,1329557716,9,14,14,22,1,1,1,2,8),(635,1329557716,1329557776,2,3,4,69,1,1,1,2,8),(636,1329557776,1329557836,8,16,14,15,1,1,1,2,8),(637,1329557836,1329557896,6,12,6,46,1,1,1,2,8),(638,1329557896,1329557956,5,2,0,72,1,1,1,2,8),(639,1329557956,1329558016,6,16,4,68,1,1,1,2,8),(640,1329558016,1329558076,8,16,14,20,1,1,1,2,8),(641,1329558076,1329558136,8,9,18,23,1,1,1,2,8),(642,1329558136,1329558196,4,2,10,97,1,1,1,2,8),(643,1329558196,1329558256,5,8,12,68,1,1,1,2,8),(644,1329558256,1329558316,7,7,16,75,1,1,1,2,8),(645,1329558316,1329558376,4,18,6,81,1,1,1,2,8),(646,1329558376,1329558436,1,2,2,43,1,1,1,2,8),(647,1329558436,1329558496,7,6,8,3,1,1,1,2,8),(648,1329558496,1329558556,10,17,4,57,1,1,1,2,8),(649,1329558556,1329558616,2,7,0,14,1,1,1,2,8),(650,1329558616,1329558676,6,14,14,27,1,1,1,2,8),(651,1329558676,1329558736,3,12,6,45,1,1,1,2,8),(652,1329558736,1329558796,5,18,4,40,1,1,1,2,8),(653,1329558796,1329558856,3,8,2,8,1,1,1,2,8),(654,1329558856,1329558916,2,16,6,37,1,1,1,2,8),(655,1329558916,1329558976,8,19,6,62,1,1,1,2,8),(656,1329558976,1329559036,2,4,10,62,1,1,1,2,8),(657,1329559036,1329559096,7,15,10,42,1,1,1,2,8),(658,1329559096,1329559156,5,9,12,76,1,1,1,2,8),(659,1329559156,1329559216,10,9,10,89,1,1,1,2,8),(660,1329559216,1329559276,1,16,12,78,1,1,1,2,8),(661,1329559276,1329559336,0,15,12,96,1,1,1,2,8),(662,1329559336,1329559396,9,11,4,12,1,1,1,2,8),(663,1329559396,1329559456,1,3,8,46,1,1,1,2,8),(664,1329559456,1329559516,2,10,0,53,1,1,1,2,8),(665,1329559516,1329559576,6,12,0,26,1,1,1,2,8),(666,1329559576,1329559636,3,15,16,90,1,1,1,2,8),(667,1329559636,1329559696,0,8,18,44,1,1,1,2,8),(668,1329559696,1329559756,4,15,10,30,1,1,1,2,8),(669,1329559756,1329559816,10,0,2,65,1,1,1,2,8),(670,1329559816,1329559876,8,4,10,73,1,1,1,2,8),(671,1329559876,1329559936,3,5,6,94,1,1,1,2,8),(672,1329559936,1329559996,7,12,2,41,1,1,1,2,8),(673,1329559996,1329560056,9,3,0,81,1,1,1,2,8),(674,1329560056,1329560116,9,6,12,4,1,1,1,2,8),(675,1329560116,1329560176,5,3,8,57,1,1,1,2,8),(676,1329560176,1329560236,6,9,6,36,1,1,1,2,8),(677,1329560236,1329560296,8,16,14,4,1,1,1,2,8),(678,1329560296,1329560356,1,11,6,80,1,1,1,2,8),(679,1329560356,1329560416,2,8,12,81,1,1,1,2,8),(680,1329560416,1329560476,2,11,4,26,1,1,1,2,8),(681,1329560476,1329560536,8,19,12,8,1,1,1,2,8),(682,1329560536,1329560596,6,17,10,79,1,1,1,2,8),(683,1329560596,1329560656,5,4,10,80,1,1,1,2,8),(684,1329560656,1329560716,5,6,18,58,1,1,1,2,8),(685,1329560716,1329560776,2,7,20,99,1,1,1,2,8),(686,1329560776,1329560836,9,16,0,91,1,1,1,2,8),(687,1329560836,1329560896,4,4,18,78,1,1,1,2,8),(688,1329560896,1329560956,2,17,14,63,1,1,1,2,8),(689,1329560956,1329561016,2,3,4,44,1,1,1,2,8),(690,1329561016,1329561076,7,19,16,11,1,1,1,2,8),(691,1329561076,1329561136,2,9,16,83,1,1,1,2,8),(692,1329561136,1329561196,6,13,6,51,1,1,1,2,8),(693,1329561196,1329561256,7,19,14,72,1,1,1,2,8),(694,1329561256,1329561316,4,19,10,61,1,1,1,2,8),(695,1329561316,1329561376,6,1,12,71,1,1,1,2,8),(696,1329561376,1329561436,8,20,10,30,1,1,1,2,8),(697,1329561436,1329561496,2,18,18,5,1,1,1,2,8),(698,1329561496,1329561556,4,18,8,7,1,1,1,2,8),(699,1329561556,1329561616,2,19,2,77,1,1,1,2,8),(700,1329561616,1329561676,4,17,0,58,1,1,1,2,8),(701,1329561676,1329561736,8,4,14,66,1,1,1,2,8),(702,1329561736,1329561796,3,15,12,2,1,1,1,2,8),(703,1329561796,1329561856,2,14,2,40,1,1,1,2,8),(704,1329561856,1329561916,7,5,2,71,1,1,1,2,8),(705,1329561916,1329561976,3,8,20,84,1,1,1,2,8),(706,1329561976,1329562036,2,10,16,58,1,1,1,2,8),(707,1329562036,1329562096,5,14,2,19,1,1,1,2,8),(708,1329562096,1329562156,8,6,4,17,1,1,1,2,8),(709,1329562156,1329562216,2,9,16,39,1,1,1,2,8),(710,1329562216,1329562276,7,5,2,94,1,1,1,2,8),(711,1329562276,1329562336,3,14,14,30,1,1,1,2,8),(712,1329562336,1329562396,4,5,20,18,1,1,1,2,8),(713,1329562396,1329562456,9,2,16,53,1,1,1,2,8),(714,1329562456,1329562516,3,0,2,46,1,1,1,2,8),(715,1329562516,1329562576,0,14,10,29,1,1,1,2,8),(716,1329562576,1329562636,10,2,8,92,1,1,1,2,8),(717,1329562636,1329562696,3,15,16,83,1,1,1,2,8),(718,1329562696,1329562756,7,2,6,26,1,1,1,2,8),(719,1329562756,1329562816,4,4,16,33,1,1,1,2,8),(720,1329562816,1329562876,3,11,18,75,1,1,1,2,8),(721,1329562876,1329562936,1,3,12,49,1,1,1,2,8),(722,1329562936,1329562996,7,16,0,69,1,1,1,2,8),(723,1329562996,1329563056,4,0,16,95,1,1,1,2,8),(724,1329563056,1329563116,4,20,16,1,1,1,1,2,8),(725,1329563116,1329563176,7,7,16,80,1,1,1,2,8),(726,1329563176,1329563236,7,18,10,3,1,1,1,2,8),(727,1329563236,1329563296,5,7,6,45,1,1,1,2,8),(728,1329563296,1329563356,4,9,4,76,1,1,1,2,8),(729,1329563356,1329563416,1,4,12,69,1,1,1,2,8),(730,1329563416,1329563476,5,9,12,92,1,1,1,2,8),(731,1329563476,1329563536,7,14,8,74,1,1,1,2,8),(732,1329563536,1329563596,6,17,8,37,1,1,1,2,8),(733,1329563596,1329563656,7,9,4,61,1,1,1,2,8),(734,1329563656,1329563716,4,7,8,4,1,1,1,2,8),(735,1329563716,1329563776,10,15,16,91,1,1,1,2,8),(736,1329563776,1329563836,0,10,8,37,1,1,1,2,8),(737,1329563836,1329563896,7,9,2,11,1,1,1,2,8),(738,1329563896,1329563956,3,3,18,17,1,1,1,2,8),(739,1329563956,1329564016,0,13,6,31,1,1,1,2,8),(740,1329564016,1329564076,7,15,10,31,1,1,1,2,8),(741,1329564076,1329564136,0,3,14,9,1,1,1,2,8),(742,1329564136,1329564196,3,7,14,59,1,1,1,2,8),(743,1329564196,1329564256,8,3,6,13,1,1,1,2,8),(744,1329564256,1329564316,7,5,2,82,1,1,1,2,8),(745,1329564316,1329564376,7,3,12,50,1,1,1,2,8),(746,1329564376,1329564436,7,2,8,84,1,1,1,2,8),(747,1329564436,1329564496,9,17,12,56,1,1,1,2,8),(748,1329564496,1329564556,9,20,4,95,1,1,1,2,8),(749,1329564556,1329564616,2,19,8,90,1,1,1,2,8),(750,1329564616,1329564676,4,9,18,1,1,1,1,2,8),(751,1329564676,1329564736,5,6,0,21,1,1,1,2,8),(752,1329564736,1329564796,0,9,4,59,1,1,1,2,8),(753,1329564796,1329564856,4,3,12,53,1,1,1,2,8),(754,1329564856,1329564916,8,13,14,69,1,1,1,2,8),(755,1329564916,1329564976,3,5,8,39,1,1,1,2,8),(756,1329564976,1329565036,7,3,18,75,1,1,1,2,8),(757,1329565036,1329565096,2,13,16,89,1,1,1,2,8),(758,1329565096,1329565156,1,18,4,44,1,1,1,2,8),(759,1329565156,1329565216,5,2,2,18,1,1,1,2,8),(760,1329565216,1329565276,6,9,8,82,1,1,1,2,8),(761,1329565276,1329565336,8,12,10,73,1,1,1,2,8),(762,1329565336,1329565396,2,13,14,65,1,1,1,2,8),(763,1329565396,1329565456,1,13,18,34,1,1,1,2,8),(764,1329565456,1329565516,1,13,16,88,1,1,1,2,8),(765,1329565516,1329565576,1,1,18,43,1,1,1,2,8),(766,1329565576,1329565636,4,12,16,12,1,1,1,2,8),(767,1329565636,1329565696,3,2,10,41,1,1,1,2,8),(768,1329565696,1329565756,4,20,12,23,1,1,1,2,8),(769,1329565756,1329565816,2,10,14,2,1,1,1,2,8),(770,1329565816,1329565876,0,1,4,3,1,1,1,2,8),(771,1329565876,1329565936,4,19,12,3,1,1,1,2,8),(772,1329565936,1329565996,4,18,4,45,1,1,1,2,8),(773,1329565996,1329566056,6,14,14,38,1,1,1,2,8),(774,1329566056,1329566116,8,15,10,18,1,1,1,2,8),(775,1329566116,1329566176,4,10,6,86,1,1,1,2,8),(776,1329566176,1329566236,5,18,0,44,1,1,1,2,8),(777,1329566236,1329566296,1,4,12,64,1,1,1,2,8),(778,1329566296,1329566356,3,10,12,57,1,1,1,2,8),(779,1329566356,1329566416,0,9,4,83,1,1,1,2,8),(780,1329566416,1329566476,4,10,6,89,1,1,1,2,8),(781,1329566476,1329566536,6,9,6,40,1,1,1,2,8),(782,1329566536,1329566596,10,13,8,7,1,1,1,2,8),(783,1329566596,1329566656,1,5,18,73,1,1,1,2,8),(784,1329566656,1329566716,0,16,2,90,1,1,1,2,8),(785,1329566716,1329566776,3,16,2,26,1,1,1,2,8),(786,1329566776,1329566836,9,15,0,81,1,1,1,2,8),(787,1329566836,1329566896,0,14,10,44,1,1,1,2,8),(788,1329566896,1329566956,6,15,18,38,1,1,1,2,8),(789,1329566956,1329567016,1,8,14,9,1,1,1,2,8),(790,1329567016,1329567076,5,3,8,28,1,1,1,2,8),(791,1329567076,1329567136,4,19,12,6,1,1,1,2,8),(792,1329567136,1329567196,6,15,2,14,1,1,1,2,8),(793,1329567196,1329567256,4,15,10,11,1,1,1,2,8),(794,1329567256,1329567316,1,4,14,72,1,1,1,2,8),(795,1329567316,1329567376,6,1,4,3,1,1,1,2,8),(796,1329567376,1329567436,5,5,16,11,1,1,1,2,8),(797,1329567436,1329567496,3,0,4,21,1,1,1,2,8),(798,1329567496,1329567556,3,19,14,86,1,1,1,2,8),(799,1329567556,1329567616,1,18,2,93,1,1,1,2,8),(800,1329567616,1329567676,3,15,18,14,1,1,1,2,8),(801,1329567676,1329567736,0,15,12,95,1,1,1,2,8),(802,1329567736,1329567796,8,5,18,68,1,1,1,2,8),(803,1329567796,1329567856,7,8,20,61,1,1,1,2,8),(804,1329567856,1329567916,1,17,16,66,1,1,1,2,8),(805,1329567916,1329567976,8,20,10,60,1,1,1,2,8),(806,1329567976,1329568036,5,13,18,32,1,1,1,2,8),(807,1329568036,1329568096,10,19,16,28,1,1,1,2,8),(808,1329568096,1329568156,9,13,12,13,1,1,1,2,8),(809,1329568156,1329568216,8,9,20,42,1,1,1,2,8),(810,1329568216,1329568276,2,19,20,6,1,1,1,2,8),(811,1329568276,1329568336,4,14,8,92,1,1,1,2,8),(812,1329568336,1329568396,4,2,6,27,1,1,1,2,8),(813,1329568396,1329568456,4,8,12,91,1,1,1,2,8),(814,1329568456,1329568516,7,18,4,47,1,1,1,2,8),(815,1329568516,1329568576,7,1,2,59,1,1,1,2,8),(816,1329568576,1329568636,5,16,10,13,1,1,1,2,8),(817,1329568636,1329568696,1,4,14,89,1,1,1,2,8),(818,1329568696,1329568756,3,19,14,75,1,1,1,2,8),(819,1329568756,1329568816,6,16,2,21,1,1,1,2,8),(820,1329568816,1329568876,7,19,12,12,1,1,1,2,8),(821,1329568876,1329568936,8,15,4,79,1,1,1,2,8),(822,1329568936,1329568996,4,8,20,59,1,1,1,2,8),(823,1329568996,1329569056,1,11,12,47,1,1,1,2,8),(824,1329569056,1329569116,5,18,0,59,1,1,1,2,8),(825,1329569116,1329569176,8,3,8,62,1,1,1,2,8),(826,1329569176,1329569236,9,8,10,21,1,1,1,2,8),(827,1329569236,1329569296,6,7,0,10,1,1,1,2,8),(828,1329569296,1329569356,4,11,12,54,1,1,1,2,8),(829,1329569356,1329569416,8,7,6,57,1,1,1,2,8),(830,1329569416,1329569476,9,14,18,36,1,1,1,2,8),(831,1329569476,1329569536,1,10,4,58,1,1,1,2,8),(832,1329569536,1329569596,2,6,18,78,1,1,1,2,8),(833,1329569596,1329569656,1,1,0,97,1,1,1,2,8),(834,1329569656,1329569716,8,20,12,91,1,1,1,2,8),(835,1329569716,1329569776,9,11,2,6,1,1,1,2,8),(836,1329569776,1329569836,9,5,12,30,1,1,1,2,8),(837,1329569836,1329569896,6,7,14,61,1,1,1,2,8),(838,1329569896,1329569956,9,12,8,25,1,1,1,2,8),(839,1329569956,1329570016,10,3,18,82,1,1,1,2,8),(840,1329570016,1329570076,5,4,8,56,1,1,1,2,8),(841,1329570076,1329570136,5,14,20,84,1,1,1,2,8),(842,1329570136,1329570196,3,17,8,40,1,1,1,2,8),(843,1329570196,1329570256,8,20,8,3,1,1,1,2,8),(844,1329570256,1329570316,10,15,18,26,1,1,1,2,8),(845,1329570316,1329570376,6,2,16,80,1,1,1,2,8),(846,1329570376,1329570436,5,5,14,77,1,1,1,2,8),(847,1329570436,1329570496,7,5,4,14,1,1,1,2,8),(848,1329570496,1329570556,1,5,18,70,1,1,1,2,8),(849,1329570556,1329570616,8,0,10,71,1,1,1,2,8),(850,1329570616,1329570676,9,8,8,69,1,1,1,2,8),(851,1329570676,1329570736,3,8,0,6,1,1,1,2,8),(852,1329570736,1329570796,2,13,14,62,1,1,1,2,8),(853,1329570796,1329570856,10,2,12,53,1,1,1,2,8),(854,1329570856,1329570916,9,20,4,15,1,1,1,2,8),(855,1329570916,1329570976,1,17,2,99,1,1,1,2,8),(856,1329570976,1329571036,6,2,14,6,1,1,1,2,8),(857,1329571036,1329571096,3,3,18,18,1,1,1,2,8),(858,1329571096,1329571156,1,4,10,2,1,1,1,2,8),(859,1329571156,1329571216,5,12,10,43,1,1,1,2,8),(860,1329571216,1329571276,8,14,2,35,1,1,1,2,8),(861,1329571276,1329571336,5,8,12,56,1,1,1,2,8),(862,1329571336,1329571396,1,19,8,2,1,1,1,2,8),(863,1329571396,1329571456,10,17,6,10,1,1,1,2,8),(864,1329571456,1329571516,5,5,12,37,1,1,1,2,8),(865,1329571516,1329571576,0,18,12,12,1,1,1,2,8),(866,1329571576,1329571636,9,20,6,54,1,1,1,2,8),(867,1329571636,1329571696,8,10,20,38,1,1,1,2,8),(868,1329571696,1329571756,0,18,10,83,1,1,1,2,8),(869,1329571756,1329571816,6,12,2,65,1,1,1,2,8),(870,1329571816,1329571876,0,3,14,19,1,1,1,2,8),(871,1329571876,1329571936,7,3,12,51,1,1,1,2,8),(872,1329571936,1329571996,7,2,6,23,1,1,1,2,8),(873,1329571996,1329572056,3,12,4,13,1,1,1,2,8),(874,1329572056,1329572116,1,3,8,46,1,1,1,2,8),(875,1329572116,1329572176,2,11,6,67,1,1,1,2,8),(876,1329572176,1329572236,5,13,14,65,1,1,1,2,8),(877,1329572236,1329572296,1,7,12,1,1,1,1,2,8),(878,1329572296,1329572356,2,20,6,47,1,1,1,2,8),(879,1329572356,1329572416,5,4,8,46,1,1,1,2,8),(880,1329572416,1329572476,1,1,18,47,1,1,1,2,8),(881,1329572476,1329572536,6,13,10,36,1,1,1,2,8),(882,1329572536,1329572596,4,18,8,13,1,1,1,2,8),(883,1329572596,1329572656,5,6,18,59,1,1,1,2,8),(884,1329572656,1329572716,3,11,18,81,1,1,1,2,8),(885,1329572716,1329572776,4,12,14,74,1,1,1,2,8),(886,1329572776,1329572836,6,17,6,16,1,1,1,2,8),(887,1329572836,1329572896,8,12,10,92,1,1,1,2,8),(888,1329572896,1329572956,10,1,6,60,1,1,1,2,8),(889,1329572956,1329573016,9,19,18,77,1,1,1,2,8),(890,1329573016,1329573076,1,20,16,99,1,1,1,2,8),(891,1329573076,1329573136,6,19,18,81,1,1,1,2,8),(892,1329573136,1329573196,3,1,8,67,1,1,1,2,8),(893,1329573196,1329573256,2,5,8,45,1,1,1,2,8),(894,1329573256,1329573316,10,8,4,97,1,1,1,2,8),(895,1329573316,1329573376,1,14,2,45,1,1,1,2,8),(896,1329573376,1329573436,9,7,0,2,1,1,1,2,8),(897,1329573436,1329573496,0,20,18,74,1,1,1,2,8),(898,1329573496,1329573556,9,3,2,22,1,1,1,2,8),(899,1329573556,1329573616,7,18,6,71,1,1,1,2,8),(900,1329573616,1329573676,7,10,6,86,1,1,1,2,8),(901,1329573676,1329573736,5,19,6,62,1,1,1,2,8),(902,1329573736,1329573796,2,3,4,52,1,1,1,2,8),(903,1329573796,1329573856,10,8,20,73,1,1,1,2,8),(904,1329573856,1329573916,7,8,16,88,1,1,1,2,8),(905,1329573916,1329573976,10,3,16,79,1,1,1,2,8),(906,1329573976,1329574036,4,16,14,18,1,1,1,2,8),(907,1329574036,1329574096,7,3,10,91,1,1,1,2,8),(908,1329574096,1329574156,2,2,2,5,1,1,1,2,8),(909,1329574156,1329574216,0,19,16,0,1,1,1,2,8),(910,1329574216,1329574276,7,6,10,44,1,1,1,2,8),(911,1329574276,1329574336,8,17,16,22,1,1,1,2,8),(912,1329574336,1329574396,8,10,0,68,1,1,1,2,8),(913,1329574396,1329574456,3,6,14,55,1,1,1,2,8),(914,1329574456,1329574516,7,17,2,18,1,1,1,2,8),(915,1329574516,1329574576,5,15,6,33,1,1,1,2,8),(916,1329574576,1329574636,7,13,2,66,1,1,1,2,8),(917,1329574636,1329574696,9,10,18,98,1,1,1,2,8),(918,1329574696,1329574756,2,20,8,94,1,1,1,2,8),(919,1329574756,1329574816,6,19,4,3,1,1,1,2,8),(920,1329574816,1329574876,6,17,12,25,1,1,1,2,8),(921,1329574876,1329574936,5,18,16,59,1,1,1,2,8),(922,1329574936,1329574996,4,3,14,82,1,1,1,2,8),(923,1329574996,1329575056,1,4,10,94,1,1,1,2,8),(924,1329575056,1329575116,2,6,16,24,1,1,1,2,8),(925,1329575116,1329575176,7,19,10,48,1,1,1,2,8),(926,1329575176,1329575236,1,16,18,15,1,1,1,2,8),(927,1329575236,1329575296,10,10,12,34,1,1,1,2,8),(928,1329575296,1329575356,10,17,6,80,1,1,1,2,8),(929,1329575356,1329575416,2,14,16,98,1,1,1,2,8),(930,1329575416,1329575476,5,8,12,91,1,1,1,2,8),(931,1329575476,1329575536,7,12,0,30,1,1,1,2,8),(932,1329575536,1329575596,4,4,14,95,1,1,1,2,8),(933,1329575596,1329575656,7,8,2,5,1,1,1,2,8),(934,1329575656,1329575716,1,7,10,59,1,1,1,2,8),(935,1329575716,1329575776,3,18,12,14,1,1,1,2,8),(936,1329575776,1329575836,10,8,4,73,1,1,1,2,8),(937,1329575836,1329575896,0,0,18,61,1,1,1,2,8),(938,1329575896,1329575956,3,11,20,13,1,1,1,2,8),(939,1329575956,1329576016,8,9,20,53,1,1,1,2,8),(940,1329576016,1329576076,7,17,6,78,1,1,1,2,8),(941,1329576076,1329576136,1,0,16,20,1,1,1,2,8),(942,1329576136,1329576196,4,13,16,34,1,1,1,2,8),(943,1329576196,1329576256,1,13,18,72,1,1,1,2,8),(944,1329576256,1329576316,8,14,4,78,1,1,1,2,8),(945,1329576316,1329576376,4,10,6,21,1,1,1,2,8),(946,1329576376,1329576436,1,14,6,54,1,1,1,2,8),(947,1329576436,1329576496,7,18,6,91,1,1,1,2,8),(948,1329576496,1329576556,6,8,0,2,1,1,1,2,8),(949,1329576556,1329576616,10,18,10,97,1,1,1,2,8),(950,1329576616,1329576676,2,6,14,63,1,1,1,2,8),(951,1329576676,1329576736,1,8,16,80,1,1,1,2,8),(952,1329576736,1329576796,6,13,8,90,1,1,1,2,8),(953,1329576796,1329576856,4,9,18,34,1,1,1,2,8),(954,1329576856,1329576916,9,8,8,72,1,1,1,2,8),(955,1329576916,1329576976,4,1,20,64,1,1,1,2,8),(956,1329576976,1329577036,3,13,4,28,1,1,1,2,8),(957,1329577036,1329577096,7,11,16,24,1,1,1,2,8),(958,1329577096,1329577156,8,8,12,65,1,1,1,2,8),(959,1329577156,1329577216,5,13,14,69,1,1,1,2,8),(960,1329577216,1329577276,3,4,6,84,1,1,1,2,8),(961,1329577276,1329577336,3,2,12,67,1,1,1,2,8),(962,1329577336,1329577396,5,12,10,63,1,1,1,2,8),(963,1329577396,1329577456,7,11,12,46,1,1,1,2,8),(964,1329577456,1329577516,5,18,0,56,1,1,1,2,8),(965,1329577516,1329577576,7,14,10,30,1,1,1,2,8),(966,1329577576,1329577636,0,6,8,17,1,1,1,2,8),(967,1329577636,1329577696,6,9,10,21,1,1,1,2,8),(968,1329577696,1329577756,5,15,8,71,1,1,1,2,8),(969,1329577756,1329577816,3,10,12,35,1,1,1,2,8),(970,1329577816,1329577876,0,2,6,44,1,1,1,2,8),(971,1329577876,1329577936,2,12,8,10,1,1,1,2,8),(972,1329577936,1329577996,4,15,10,19,1,1,1,2,8),(973,1329577996,1329578056,5,19,4,29,1,1,1,2,8),(974,1329578056,1329578116,8,19,8,6,1,1,1,2,8),(975,1329578116,1329578176,2,16,8,75,1,1,1,2,8),(976,1329578176,1329578236,5,1,16,82,1,1,1,2,8),(977,1329578236,1329578296,8,8,12,1,1,1,1,2,8),(978,1329578296,1329578356,1,13,16,12,1,1,1,2,8),(979,1329578356,1329578416,2,11,4,20,1,1,1,2,8),(980,1329578416,1329578476,5,17,16,29,1,1,1,2,8),(981,1329578476,1329578536,1,17,16,30,1,1,1,2,8),(982,1329578536,1329578596,2,4,6,10,1,1,1,2,8),(983,1329578596,1329578656,5,0,14,50,1,1,1,2,8),(984,1329578656,1329578716,4,8,14,61,1,1,1,2,8),(985,1329578716,1329578776,8,6,20,10,1,1,1,2,8),(986,1329578776,1329578836,5,4,12,31,1,1,1,2,8),(987,1329578836,1329578896,8,18,6,55,1,1,1,2,8),(988,1329578896,1329578956,10,5,4,34,1,1,1,2,8),(989,1329578956,1329579016,1,8,16,80,1,1,1,2,8),(990,1329579016,1329579076,6,10,16,50,1,1,1,2,8),(991,1329579076,1329579136,1,19,10,82,1,1,1,2,8),(992,1329579136,1329579196,5,17,18,87,1,1,1,2,8),(993,1329579196,1329579256,7,15,16,92,1,1,1,2,8),(994,1329579256,1329579316,1,11,12,17,1,1,1,2,8),(995,1329579316,1329579376,2,5,16,27,1,1,1,2,8),(996,1329579376,1329579436,9,15,2,26,1,1,1,2,8),(997,1329579436,1329579496,10,0,4,90,1,1,1,2,8),(998,1329579496,1329579556,9,19,20,1,1,1,1,2,8),(999,1329579556,1329579616,1,14,20,82,1,1,1,2,8),(1000,1329579616,1329579676,2,9,16,46,1,1,1,2,8);
/*!40000 ALTER TABLE `ProcessQCMetrics` ENABLE KEYS */;
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
  KEY `reader_id_index` (`readerId`),
  KEY `locationId_index` (`locationId`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1 COMMENT='Information about configured readers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Reader`
--

LOCK TABLES `Reader` WRITE;
/*!40000 ALTER TABLE `Reader` DISABLE KEYS */;
INSERT INTO `Reader` VALUES (10,2,'entry-1',100,1,NULL,NULL,0,1,'00:00:00:00:00:01','127.0.0.1',18080,4,1,1328742405685,1,1,'0.0.0.0','0.0.0.0',0,NULL),(11,2,'entry-2',100,4,NULL,NULL,0,1,'00:00:00:00:00:02','127.0.0.1',18080,4,1,1328742405694,2,2,'0.0.0.0','0.0.0.0',0,NULL),(12,2,'entry-3',100,8,NULL,NULL,0,1,'00:00:00:00:00:03','127.0.0.1',18080,3,1,1328742405705,3,3,'0.0.0.0','0.0.0.0',0,NULL),(13,2,'entry-4',100,12,NULL,NULL,0,1,'00:00:00:00:00:04','127.0.0.1',18080,4,1,1328742405729,4,4,'0.0.0.0','0.0.0.0',0,NULL);
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
INSERT INTO `SchemaVersion` VALUES ('1.0.0.0','parkentrymodel');
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
INSERT INTO `Status` VALUES ('LastMessageToPostStream','-1'),('LastMessageIdToJMS','-1'),('LastStateStore','1970-01-01T00:00:00.000');
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='Holds configuration sets serialized in XML form';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `StoredConfigurations`
--

LOCK TABLES `StoredConfigurations` WRITE;
/*!40000 ALTER TABLE `StoredConfigurations` DISABLE KEYS */;
INSERT INTO `StoredConfigurations` VALUES (9,'myconfig1','Created on: 2011-11-16T20:05:25.932','class com.disney.xband.xbrc.attractionmodel.CEP','<venue name=\"80010114\" time=\"2011-11-16T20:05:25.932\">\n    <configuration name=\"myconfig1\" type=\"full\">\n        <description/>\n        <properties>\n            <property class=\"AttractionModelConfig\"  name=\"abandonmenttimeout\">60</property>\n            <property class=\"AttractionModelConfig\"  name=\"bluelightduration\">2500</property>\n            <property class=\"AttractionModelConfig\"  name=\"errorlightperiod\">250</property>\n            <property class=\"AttractionModelConfig\"  name=\"errorpattern\">bnb</property>\n            <property class=\"AttractionModelConfig\"  name=\"greenlightduration\">1000</property>\n            <property class=\"AttractionModelConfig\"  name=\"gxpurl\">http://10.75.2.160:8081/dap-controller/services/redemption</property>\n            <property class=\"AttractionModelConfig\"  name=\"loadtype\">trailing</property>\n            <property class=\"AttractionModelConfig\"  name=\"metricsperiod\">15</property>\n            <property class=\"AttractionModelConfig\"  name=\"onridetimeout\">2</property>\n            <property class=\"ControllerInfo\"  name=\"conn\">jdbc:mysql://localhost/Mayhem?user=$USER&amp;password=$PASSWORD</property>\n            <property class=\"ControllerInfo\"  name=\"discoverynetprefix\">10.</property>\n            <property class=\"ControllerInfo\"  name=\"eventdumpfile\">eventdump.txt</property>\n            <property class=\"ControllerInfo\"  name=\"eventsperbatch\">500</property>\n            <property class=\"ControllerInfo\"  name=\"httpport\">8080</property>\n            <property class=\"ControllerInfo\"  name=\"httpsport\">0</property>\n            <property class=\"ControllerInfo\"  name=\"jmsretryperiod\">1000</property>\n            <property class=\"ControllerInfo\"  name=\"loadtype\">trailing</property>\n            <property class=\"ControllerInfo\"  name=\"maxreadersbeforepurge\">6</property>\n            <property class=\"ControllerInfo\"  name=\"metricsperiod\">15</property>\n            <property class=\"ControllerInfo\"  name=\"model\">com.disney.xband.xbrc.attractionmodel.CEP</property>\n            <property class=\"ControllerInfo\"  name=\"onridetimeout\">2</property>\n            <property class=\"ControllerInfo\"  name=\"ownipprefix\">10.</property>\n            <property class=\"ControllerInfo\"  name=\"pass\">Mayhem!23</property>\n            <property class=\"ControllerInfo\"  name=\"pushmode\">true</property>\n            <property class=\"ControllerInfo\"  name=\"readerhellotimeoutsec\">90</property>\n            <property class=\"ControllerInfo\"  name=\"readertestmode\">false</property>\n            <property class=\"ControllerInfo\"  name=\"setreadertime\">true</property>\n            <property class=\"ControllerInfo\"  name=\"updatestreamurl\"></property>\n            <property class=\"ControllerInfo\"  name=\"url\">http://localhost:8080/ControllerServer</property>\n            <property class=\"ControllerInfo\"  name=\"user\">EMUser</property>\n            <property class=\"ControllerInfo\"  name=\"venue\">80010114</property>\n            <property class=\"ControllerInfo\"  name=\"verbose\">false</property>\n            <property class=\"ControllerInfo\"  name=\"watchedbandtimeout\">180</property>\n            <property class=\"ControllerInfo\"  name=\"xviewurl\">http://localhost:8090/Xview</property>\n            <property class=\"ESBInfo\"  name=\"jmsbroker\">#</property>\n            <property class=\"ESBInfo\"  name=\"jmsdiscoverytimesec\">60</property>\n            <property class=\"ESBInfo\"  name=\"jmspassword\">Administrator</property>\n            <property class=\"ESBInfo\"  name=\"jmsqueryqueue\">disney.xband.xbrc.queryqueue</property>\n            <property class=\"ESBInfo\"  name=\"jmsrequestqueue\">com.synapse.xbrcIn</property>\n            <property class=\"ESBInfo\"  name=\"jmsrser\"></property>\n            <property class=\"ESBInfo\"  name=\"jmstopic\">com.synapse.xbrc</property>\n            <property class=\"ESBInfo\"  name=\"jmsuser\">Administrator</property>\n            <property class=\"ReaderConfig\"  name=\"gainsliderincrement\">0.1</property>\n            <property class=\"ReaderConfig\"  name=\"initialized\">false</property>\n            <property class=\"ReaderConfig\"  name=\"maximumgain\">2.0</property>\n            <property class=\"ReaderConfig\"  name=\"maximumthreshold\">63</property>\n            <property class=\"ReaderConfig\"  name=\"minimumgain\">-1.0</property>\n            <property class=\"ReaderConfig\"  name=\"minimumthreshold\">0</property>\n            <property class=\"ReaderConfig\"  name=\"minnormaltemperature\">19.0</property>\n            <property class=\"ReaderConfig\"  name=\"mmaxnormaltemperature\">22.0</property>\n            <property class=\"ReaderConfig\"  name=\"temperaturetoohigh\">25.0</property>\n            <property class=\"ReaderConfig\"  name=\"thresholdsliderincrement\">1</property>\n            <property class=\"ReaderConfig\"  name=\"xbrcconfigmodseq\">2</property>\n            <property class=\"ServiceLocator\"  name=\"service_implementor_suffix\">Imp</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridheight\">9</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridsize\">50</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridwidth\">18</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonheight\">24</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonspacing\">10</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonstagger\">4</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonwidth\">24</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"maxguestspericon\">5</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"maxiconspergriditem\">3</property>\n            <property class=\"UIConfig\"  name=\"controllerurl\">http://localhost:8080</property>\n            <property class=\"UIConfig\"  name=\"guestxviewcachetimesec\">1800</property>\n            <property class=\"UIConfig\"  name=\"xviewurl\">http://localhost:8090/Xview</property>\n        </properties>\n        <readerlocationinfo>\n            <readerlocation>\n                <section></section>\n                <name>Load2</name>\n                <id>8</id>\n                <type>4</type>\n                <x>76.0</x>\n                <y>76.0</y>\n                <readers>\n                    <reader>\n                        <name>load2-4</name>\n                        <id>27</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8084</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>13</y>\n                    </reader>\n                    <reader>\n                        <name>load2-3</name>\n                        <id>26</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8083</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>13</y>\n                    </reader>\n                    <reader>\n                        <name>load2-2</name>\n                        <id>25</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8082</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>load2-1</name>\n                        <id>24</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8081</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>10</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Load1</name>\n                <id>7</id>\n                <type>4</type>\n                <x>76.0</x>\n                <y>76.0</y>\n                <readers>\n                    <reader>\n                        <name>load1-4</name>\n                        <id>23</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8074</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>load1-3</name>\n                        <id>22</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8073</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>load1-2</name>\n                        <id>21</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8072</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>0</y>\n                    </reader>\n                    <reader>\n                        <name>load1-1</name>\n                        <id>20</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8071</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Exit</name>\n                <id>6</id>\n                <type>3</type>\n                <x>101.0</x>\n                <y>101.0</y>\n                <readers>\n                    <reader>\n                        <name>exit-4</name>\n                        <id>19</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8064</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>103</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>exit-3</name>\n                        <id>18</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8063</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>100</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>exit-2</name>\n                        <id>17</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8062</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>103</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>exit-1</name>\n                        <id>16</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8061</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>100</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>xPassEntry</name>\n                <id>5</id>\n                <type>7</type>\n                <x>10.0</x>\n                <y>10.0</y>\n                <readers>\n                    <reader>\n                        <name>xpassentry</name>\n                        <id>15</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8051</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>10</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>xFP1</name>\n                        <id>32</id>\n                        <type>Tap</type>\n                        <macaddress>00:91:FA:00:00:35</macaddress>\n                        <ipaddress>10.75.2.234</ipaddress>\n                        <port>8080</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Merge</name>\n                <id>4</id>\n                <type>6</type>\n                <x>15.0</x>\n                <y>15.0</y>\n                <readers>\n                    <reader>\n                        <name>merge</name>\n                        <id>14</id>\n                        <type>Tap</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8041</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>15</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>xFP2</name>\n                        <id>33</id>\n                        <type>Tap</type>\n                        <macaddress>00:91:FA:00:00:31</macaddress>\n                        <ipaddress>10.75.2.46</ipaddress>\n                        <port>8080</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Queue1</name>\n                <id>3</id>\n                <type>2</type>\n                <x>26.0</x>\n                <y>26.0</y>\n                <readers>\n                    <reader>\n                        <name>queue1-4</name>\n                        <id>13</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8034</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>28</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-3</name>\n                        <id>12</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8033</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>25</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-2</name>\n                        <id>11</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8032</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>28</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-1</name>\n                        <id>10</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8031</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>25</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Queue2</name>\n                <id>2</id>\n                <type>2</type>\n                <x>51.0</x>\n                <y>51.0</y>\n                <readers>\n                    <reader>\n                        <name>queue2-4</name>\n                        <id>9</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8024</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>53</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-3</name>\n                        <id>8</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8023</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>50</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-2</name>\n                        <id>7</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8022</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>53</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-1</name>\n                        <id>6</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8021</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>50</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Entry</name>\n                <id>1</id>\n                <type>1</type>\n                <x>1.0</x>\n                <y>1.0</y>\n                <readers>\n                    <reader>\n                        <name>entry-4</name>\n                        <id>5</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8014</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>3</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>entry-3</name>\n                        <id>4</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8013</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>entry-2</name>\n                        <id>3</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8012</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>3</x>\n                        <y>0</y>\n                    </reader>\n                    <reader>\n                        <name>entry-1</name>\n                        <id>2</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8011</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>UNKNOWN</name>\n                <id>0</id>\n                <type>8</type>\n                <x>0.0</x>\n                <y>0.0</y>\n                <readers>\n                </readers>\n            </readerlocation>\n        </readerlocationinfo>\n        <model>\n        </model>\n    </configuration>\n</venue>\n'),(10,'andreas','another','class com.disney.xband.xbrc.attractionmodel.CEP','<venue name=\"80010114\" time=\"2011-11-17T22:51:09.129\">\n    <configuration name=\"andreas\" type=\"full\">\n        <description/>\n        <properties>\n            <property class=\"AttractionModelConfig\"  name=\"abandonmenttimeout\">60</property>\n            <property class=\"AttractionModelConfig\"  name=\"bluelightduration\">2500</property>\n            <property class=\"AttractionModelConfig\"  name=\"errorlightperiod\">250</property>\n            <property class=\"AttractionModelConfig\"  name=\"errorpattern\">bnb</property>\n            <property class=\"AttractionModelConfig\"  name=\"greenlightduration\">1000</property>\n            <property class=\"AttractionModelConfig\"  name=\"gstsaveperiod\">1000</property>\n            <property class=\"AttractionModelConfig\"  name=\"gxpurl\">http://10.75.2.160:8081/dap-controller/services/redemption</property>\n            <property class=\"AttractionModelConfig\"  name=\"loadtype\">trailing</property>\n            <property class=\"AttractionModelConfig\"  name=\"metricsperiod\">15</property>\n            <property class=\"AttractionModelConfig\"  name=\"onridetimeout\">2</property>\n            <property class=\"ControllerInfo\"  name=\"conn\">jdbc:mysql://localhost/Mayhem?user=$USER&amp;password=$PASSWORD</property>\n            <property class=\"ControllerInfo\"  name=\"discoverynetprefix\">10.</property>\n            <property class=\"ControllerInfo\"  name=\"eventdumpfile\">eventdump.txt</property>\n            <property class=\"ControllerInfo\"  name=\"eventsperbatch\">500</property>\n            <property class=\"ControllerInfo\"  name=\"httpport\">8080</property>\n            <property class=\"ControllerInfo\"  name=\"httpsport\">0</property>\n            <property class=\"ControllerInfo\"  name=\"jmsretryperiod\">1000</property>\n            <property class=\"ControllerInfo\"  name=\"loadtype\">trailing</property>\n            <property class=\"ControllerInfo\"  name=\"maxreadersbeforepurge\">6</property>\n            <property class=\"ControllerInfo\"  name=\"metricsperiod\">15</property>\n            <property class=\"ControllerInfo\"  name=\"model\">com.disney.xband.xbrc.attractionmodel.CEP</property>\n            <property class=\"ControllerInfo\"  name=\"onridetimeout\">2</property>\n            <property class=\"ControllerInfo\"  name=\"ownipprefix\">10.</property>\n            <property class=\"ControllerInfo\"  name=\"pass\">Mayhem!23</property>\n            <property class=\"ControllerInfo\"  name=\"pushmode\">true</property>\n            <property class=\"ControllerInfo\"  name=\"readerhellotimeoutsec\">90</property>\n            <property class=\"ControllerInfo\"  name=\"readertestmode\">false</property>\n            <property class=\"ControllerInfo\"  name=\"setreadertime\">true</property>\n            <property class=\"ControllerInfo\"  name=\"updatestreamurl\"></property>\n            <property class=\"ControllerInfo\"  name=\"url\">http://localhost:8080/ControllerServer</property>\n            <property class=\"ControllerInfo\"  name=\"user\">EMUser</property>\n            <property class=\"ControllerInfo\"  name=\"venue\">80010114</property>\n            <property class=\"ControllerInfo\"  name=\"verbose\">false</property>\n            <property class=\"ControllerInfo\"  name=\"watchedbandtimeout\">180</property>\n            <property class=\"ControllerInfo\"  name=\"xviewurl\">http://localhost:8090/Xview</property>\n            <property class=\"ESBInfo\"  name=\"jmsbroker\">#</property>\n            <property class=\"ESBInfo\"  name=\"jmsdiscoverytimesec\">60</property>\n            <property class=\"ESBInfo\"  name=\"jmspassword\">Administrator</property>\n            <property class=\"ESBInfo\"  name=\"jmsqueryqueue\">disney.xband.xbrc.queryqueue</property>\n            <property class=\"ESBInfo\"  name=\"jmsrequestqueue\">com.synapse.xbrcIn</property>\n            <property class=\"ESBInfo\"  name=\"jmsrser\"></property>\n            <property class=\"ESBInfo\"  name=\"jmstopic\">com.synapse.xbrc</property>\n            <property class=\"ESBInfo\"  name=\"jmsuser\">Administrator</property>\n            <property class=\"ReaderConfig\"  name=\"gainsliderincrement\">0.1</property>\n            <property class=\"ReaderConfig\"  name=\"initialized\">false</property>\n            <property class=\"ReaderConfig\"  name=\"maximumgain\">2.0</property>\n            <property class=\"ReaderConfig\"  name=\"maximumthreshold\">63</property>\n            <property class=\"ReaderConfig\"  name=\"minimumgain\">-1.0</property>\n            <property class=\"ReaderConfig\"  name=\"minimumthreshold\">0</property>\n            <property class=\"ReaderConfig\"  name=\"minnormaltemperature\">19.0</property>\n            <property class=\"ReaderConfig\"  name=\"mmaxnormaltemperature\">22.0</property>\n            <property class=\"ReaderConfig\"  name=\"temperaturetoohigh\">25.0</property>\n            <property class=\"ReaderConfig\"  name=\"thresholdsliderincrement\">1</property>\n            <property class=\"ReaderConfig\"  name=\"xbrcconfigmodseq\">2</property>\n            <property class=\"ServiceLocator\"  name=\"service_implementor_suffix\">Imp</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridheight\">9</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridsize\">50</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"gridwidth\">18</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonheight\">24</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonspacing\">10</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonstagger\">4</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"guesticonwidth\">24</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"maxguestspericon\">5</property>\n            <property class=\"UIAttractionViewConfig\"  name=\"maxiconspergriditem\">3</property>\n            <property class=\"UIConfig\"  name=\"controllerurl\">http://localhost:8080</property>\n            <property class=\"UIConfig\"  name=\"guestxviewcachetimesec\">1800</property>\n            <property class=\"UIConfig\"  name=\"xviewurl\">http://localhost:8090/Xview</property>\n        </properties>\n        <readerlocationinfo>\n            <readerlocation>\n                <section></section>\n                <name>Load2</name>\n                <id>8</id>\n                <type>4</type>\n                <x>76.0</x>\n                <y>76.0</y>\n                <readers>\n                    <reader>\n                        <name>load2-4</name>\n                        <id>27</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8084</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>13</y>\n                    </reader>\n                    <reader>\n                        <name>load2-3</name>\n                        <id>26</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8083</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>13</y>\n                    </reader>\n                    <reader>\n                        <name>load2-2</name>\n                        <id>25</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8082</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>load2-1</name>\n                        <id>24</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8081</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>10</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Load1</name>\n                <id>7</id>\n                <type>4</type>\n                <x>76.0</x>\n                <y>76.0</y>\n                <readers>\n                    <reader>\n                        <name>load1-4</name>\n                        <id>23</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8074</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>load1-3</name>\n                        <id>22</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8073</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>load1-2</name>\n                        <id>21</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8072</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>78</x>\n                        <y>0</y>\n                    </reader>\n                    <reader>\n                        <name>load1-1</name>\n                        <id>20</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8071</port>\n                        <gain>1.0</gain>\n                        <threshold>15</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>75</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Exit</name>\n                <id>6</id>\n                <type>3</type>\n                <x>101.0</x>\n                <y>101.0</y>\n                <readers>\n                    <reader>\n                        <name>exit-4</name>\n                        <id>19</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8064</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>103</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>exit-3</name>\n                        <id>18</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8063</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>100</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>exit-2</name>\n                        <id>17</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8062</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>103</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>exit-1</name>\n                        <id>16</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8061</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>100</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>xPassEntry</name>\n                <id>5</id>\n                <type>7</type>\n                <x>10.0</x>\n                <y>10.0</y>\n                <readers>\n                    <reader>\n                        <name>xpassentry</name>\n                        <id>15</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8051</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>10</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>xFP1</name>\n                        <id>34</id>\n                        <type>Tap</type>\n                        <macaddress>00:91:FA:00:00:27</macaddress>\n                        <ipaddress>10.75.3.74</ipaddress>\n                        <port>8080</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Merge</name>\n                <id>4</id>\n                <type>6</type>\n                <x>15.0</x>\n                <y>15.0</y>\n                <readers>\n                    <reader>\n                        <name>merge</name>\n                        <id>14</id>\n                        <type>Tap</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8041</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>15</x>\n                        <y>10</y>\n                    </reader>\n                    <reader>\n                        <name>xFP2</name>\n                        <id>33</id>\n                        <type>Tap</type>\n                        <macaddress>00:91:FA:00:00:31</macaddress>\n                        <ipaddress>10.75.2.46</ipaddress>\n                        <port>8080</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Queue1</name>\n                <id>3</id>\n                <type>2</type>\n                <x>26.0</x>\n                <y>26.0</y>\n                <readers>\n                    <reader>\n                        <name>queue1-4</name>\n                        <id>13</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8034</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>28</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-3</name>\n                        <id>12</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8033</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>25</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-2</name>\n                        <id>11</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8032</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>28</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>queue1-1</name>\n                        <id>10</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8031</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>25</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Queue2</name>\n                <id>2</id>\n                <type>2</type>\n                <x>51.0</x>\n                <y>51.0</y>\n                <readers>\n                    <reader>\n                        <name>queue2-4</name>\n                        <id>9</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8024</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>53</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-3</name>\n                        <id>8</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8023</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>50</x>\n                        <y>8</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-2</name>\n                        <id>7</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8022</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>53</x>\n                        <y>5</y>\n                    </reader>\n                    <reader>\n                        <name>queue2-1</name>\n                        <id>6</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8021</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>50</x>\n                        <y>5</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>Entry</name>\n                <id>1</id>\n                <type>1</type>\n                <x>1.0</x>\n                <y>1.0</y>\n                <readers>\n                    <reader>\n                        <name>entry-4</name>\n                        <id>5</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8014</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>3</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>entry-3</name>\n                        <id>4</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8013</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>3</y>\n                    </reader>\n                    <reader>\n                        <name>entry-2</name>\n                        <id>3</id>\n                        <type>null</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8012</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>3</x>\n                        <y>0</y>\n                    </reader>\n                    <reader>\n                        <name>entry-1</name>\n                        <id>2</id>\n                        <type>Long Range</type>\n                        <macaddress>00:00:00:00:00:00</macaddress>\n                        <ipaddress>127.0.0.1</ipaddress>\n                        <port>8011</port>\n                        <gain>1.0</gain>\n                        <threshold>0</threshold>\n                        <lane>0</lane>\n                        <omnideviceid>0</omnideviceid>\n                        <x>0</x>\n                        <y>0</y>\n                    </reader>\n                </readers>\n            </readerlocation>\n            <readerlocation>\n                <section></section>\n                <name>UNKNOWN</name>\n                <id>0</id>\n                <type>8</type>\n                <x>0.0</x>\n                <y>0.0</y>\n                <readers>\n                </readers>\n            </readerlocation>\n        </readerlocationinfo>\n        <model>\n        </model>\n    </configuration>\n</venue>\n');
/*!40000 ALTER TABLE `StoredConfigurations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TestResults`
--

DROP TABLE IF EXISTS `TestResults`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TestResults` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `suiteId` int(11) NOT NULL DEFAULT '0',
  `guestId` int(11) NOT NULL DEFAULT '0',
  `bandId` varchar(16) NOT NULL,
  `readerName` varchar(32) NOT NULL,
  `endTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expectedResult` varchar(32) NOT NULL,
  `actualResult` varchar(32) NOT NULL,
  `testResult` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=415 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TestResults`
--

LOCK TABLES `TestResults` WRITE;
/*!40000 ALTER TABLE `TestResults` DISABLE KEYS */;
/*!40000 ALTER TABLE `TestResults` ENABLE KEYS */;
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
INSERT INTO `Walls` VALUES (100,0,100,6,1),(100,8,100,15,2);
/*!40000 ALTER TABLE `Walls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XbioImage`
--

DROP TABLE IF EXISTS `XbioImage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XbioImage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transactionId` bigint(20) NOT NULL,
  `templateId` bigint(20) NOT NULL,
  `images` mediumtext,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `transactionId` (`transactionId`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XbioImage`
--

LOCK TABLES `XbioImage` WRITE;
/*!40000 ALTER TABLE `XbioImage` DISABLE KEYS */;
/*!40000 ALTER TABLE `XbioImage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `XbioTemplate`
--

DROP TABLE IF EXISTS `XbioTemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XbioTemplate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transactionId` bigint(20) NOT NULL,
  `template` mediumtext,
  `totalScanDuration` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `transactionId` (`transactionId`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `XbioTemplate`
--

LOCK TABLES `XbioTemplate` WRITE;
/*!40000 ALTER TABLE `XbioTemplate` DISABLE KEYS */;
/*!40000 ALTER TABLE `XbioTemplate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'Mayhem'
--
/*!50003 DROP PROCEDURE IF EXISTS `usp_SeedQCMetrics` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = latin1 */ ;
/*!50003 SET character_set_results = latin1 */ ;
/*!50003 SET collation_connection  = latin1_swedish_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`EMUser`@`%`*/ /*!50003 PROCEDURE `usp_SeedQCMetrics`()
BEGIN
   DECLARE x INT;
   DECLARE utime bigint;
   DECLARE stime bigint;
   DECLARE etime bigint;
   SET x = 1;
   SET utime = UNIX_TIMESTAMP();
   set stime = utime;
   set etime = stime+60;

   REPEAT
     insert ProcessQCMetrics
     select x, stime, etime,ROUND(RAND()*10,0),ROUND(RAND()*20,0),ROUND(RAND()*10,0)*2,
            ROUND(RAND()*100,0),1,1,1,2,8;
     SET x = x + 1;
     set stime = etime;
     set etime = etime+60;
     UNTIL x > 1000
   END REPEAT;
  

 END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-06-14 18:00:12
