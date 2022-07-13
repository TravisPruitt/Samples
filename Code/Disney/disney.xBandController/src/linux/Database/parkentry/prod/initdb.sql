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
INSERT INTO `Config` VALUES ('ControllerInfo','model','com.disney.xband.xbrc.parkentrymodel.CEP'),('ControllerInfo','name','xBRC'),('ControllerInfo','venue','xBRC'),('ControllerInfo','xviewurl','http://localhost:8090/Xview'),('UIAttractionViewConfig','gridheight','10'),('UIAttractionViewConfig','gridsize','50'),('UIAttractionViewConfig','gridwidth','20'),('UIAttractionViewConfig','guesticonheight','24'),('UIAttractionViewConfig','guesticonspacing','10'),('UIAttractionViewConfig','guesticonstagger','4'),('UIAttractionViewConfig','guesticonwidth','24'),('UIAttractionViewConfig','maxguestspericon','5'),('UIAttractionViewConfig','maxiconspergriditem','3'),('UIConfig','attractionviewimagefilename',''),('UIConfig','controllerurl','http://localhost:8080'),('UIConfig','guestxviewcachetimesec','1800'),('UIConfig','producttitle','Walt Disney World'),('UIConfig','showsubwaymap','true');
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-07-19 22:35:52
