-- MySQL dump 10.13  Distrib 5.1.54, for debian-linux-gnu (x86_64)
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-09-20 10:29:34
