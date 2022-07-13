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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-05-31 14:58:09
