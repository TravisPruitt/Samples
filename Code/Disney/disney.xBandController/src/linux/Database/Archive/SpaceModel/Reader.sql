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
  `signalStrengthThreshold` int(11) NOT NULL DEFAULT '0',
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
INSERT INTO `Reader` VALUES (1,0,'entry1',0,7,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8011,-1,1,1316460652841,0),(2,0,'entry2',3,7,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8012,-1,1,1316460652964,0),(3,0,'entry3',0,10,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8013,-1,1,1316460652965,0),(4,0,'entry4',3,10,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8014,-1,1,1316460652840,0),(5,1,'entrance-exit',15,9,'entrance-exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8051,-1,3,1316460652839,0),(6,0,'monitor1-1',50,7,'monitor1','monitor1',0,1,'00:00:00:00:00:00','127.0.0.1',8071,-1,4,1316460652972,0),(7,0,'monitor1-2',53,7,'monitor1','monitor1',0,1,'00:00:00:00:00:00','127.0.0.1',8072,-1,4,1316460652972,0),(8,0,'monitor1-3',50,10,'monitor1','monitor1',0,1,'00:00:00:00:00:00','127.0.0.1',8073,-1,4,1316460652878,0),(9,0,'monitor1-4',53,10,'monitor1','monitor1',0,1,'00:00:00:00:00:00','127.0.0.1',8074,-1,4,1316460652880,0),(10,0,'monitor2-1',85,7,'monitor2','monitor2',0,1,'00:00:00:00:00:00','127.0.0.1',8081,-1,5,1316460652939,0),(11,0,'monitor2-2',88,7,'monitor2','monitor2',0,1,'00:00:00:00:00:00','127.0.0.1',8082,-1,5,1316460652879,0),(12,0,'monitor2-3',85,10,'monitor2','monitor2',0,1,'00:00:00:00:00:00','127.0.0.1',8083,-1,5,1316460652781,0),(13,0,'monitor2-4',88,10,'monitor2','monitor2',0,1,'00:00:00:00:00:00','127.0.0.1',8084,-1,5,1316460652973,0);
/*!40000 ALTER TABLE `Reader` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-10-05 17:22:29
