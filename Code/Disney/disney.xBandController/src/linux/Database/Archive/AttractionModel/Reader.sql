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
INSERT INTO `Reader` VALUES (2,0,'entry-1',0,0,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8011,-1,1,1316460652841,0),(3,0,'entry-2',3,0,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8012,-1,1,1316460652964,0),(4,0,'entry-3',0,3,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8013,-1,1,1316460652965,0),(5,0,'entry-4',3,3,'entry','',0,1,'00:00:00:00:00:00','127.0.0.1',8014,-1,1,1316460652840,0),(6,0,'queue2-1',50,5,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8021,-1,2,1316460652791,0),(7,0,'queue2-2',53,5,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8022,-1,2,1316460652840,0),(8,0,'queue2-3',50,8,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8023,-1,2,1316460652984,0),(9,0,'queue2-4',53,8,'queue2','',0,1,'00:00:00:00:00:00','127.0.0.1',8024,-1,2,1316460652798,0),(10,0,'queue1-1',25,5,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8031,-1,3,1316460652783,0),(11,0,'queue1-2',28,5,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8032,-1,3,1316460652867,0),(12,0,'queue1-3',25,8,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8033,-1,3,1316460653012,0),(13,0,'queue1-4',28,8,'queue1','',0,1,'00:00:00:00:00:00','127.0.0.1',8034,-1,3,1316460652868,0),(14,1,'merge',15,10,'merge','',0,1,'00:00:00:00:00:00','127.0.0.1',8041,-1,4,1316460652964,0),(15,1,'xpassentry',10,10,'xpassentry','',0,1,'00:00:00:00:00:00','127.0.0.1',8051,-1,5,1316460652839,0),(16,0,'exit-1',100,5,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8061,-1,6,1316460652987,0),(17,0,'exit-2',103,5,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8062,-1,6,1316460652796,0),(18,0,'exit-3',100,8,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8063,-1,6,1316460652987,0),(19,0,'exit-4',103,8,'exit','',0,1,'00:00:00:00:00:00','127.0.0.1',8064,-1,6,1316460652871,0),(20,0,'load1-1',75,0,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8071,-1,7,1316460652972,0),(21,0,'load1-2',78,0,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8072,-1,7,1316460652972,0),(22,0,'load1-3',75,3,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8073,-1,7,1316460652878,0),(23,0,'load1-4',78,3,'load1','load1',15,1,'00:00:00:00:00:00','127.0.0.1',8074,-1,7,1316460652880,0),(24,0,'load2-1',75,10,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8081,-1,8,1316460652939,0),(25,0,'load2-2',78,10,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8082,-1,8,1316460652879,0),(26,0,'load2-3',75,13,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8083,-1,8,1316460652781,0),(27,0,'load2-4',78,13,'load2','load2',15,1,'00:00:00:00:00:00','127.0.0.1',8084,-1,8,1316460652973,0),(31,0,'George',NULL,NULL,NULL,NULL,0,1,'00:15:C9:28:E5:83','10.75.2.234',8080,-1,11,1313696019686,0);
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

-- Dump completed on 2011-10-05 17:20:54
