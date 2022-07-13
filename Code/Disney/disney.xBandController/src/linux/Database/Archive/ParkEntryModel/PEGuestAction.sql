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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-10-01 23:39:34
