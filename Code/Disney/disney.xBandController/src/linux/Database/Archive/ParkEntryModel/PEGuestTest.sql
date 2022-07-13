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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-10-01 23:39:34
