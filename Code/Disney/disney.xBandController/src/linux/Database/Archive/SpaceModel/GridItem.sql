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
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=latin1 COMMENT='Items that together make up the flow chart of the attraction';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GridItem`
--

LOCK TABLES `GridItem` WRITE;
/*!40000 ALTER TABLE `GridItem` DISABLE KEYS */;
INSERT INTO `GridItem` VALUES (27,'Gate',2,3,'INDETERMINATE','Entry 1','','Gate.png',0,0,1),(29,'Gate',6,3,'INDETERMINATE','Entry Exit','','Gate.png',0,0,3),(30,'Gate',10,3,'INDETERMINATE','Monitor 1','','Gate.png',0,0,4),(31,'Gate',14,3,'INDETERMINATE','Monitor 2','','Gate.png',0,0,5),(32,'ESTurn',9,2,'INDETERMINATE','','',NULL,8,0,4),(33,'ENTurn',9,4,'INDETERMINATE','','',NULL,6,0,4),(34,'WNTurn',11,4,'INDETERMINATE','','',NULL,4,0,4),(35,'WSTurn',11,2,'INDETERMINATE','','',NULL,2,0,4),(36,'HPath',10,2,'INDETERMINATE','','',NULL,1,0,4),(37,'HPath',10,4,'INDETERMINATE','','',NULL,5,0,4),(38,'VPath',11,3,'INDETERMINATE','','',NULL,3,0,4),(39,'VPath',9,3,'INDETERMINATE','','',NULL,7,0,4),(40,'ENTurn',13,4,'INDETERMINATE','','',NULL,6,0,5),(41,'WNTurn',15,4,'INDETERMINATE','','',NULL,4,0,5),(42,'HPath',14,4,'INDETERMINATE','','',NULL,5,0,5),(43,'HPath',14,2,'INDETERMINATE','','',NULL,1,0,5),(44,'ESTurn',13,2,'INDETERMINATE','','',NULL,8,0,5),(45,'WSTurn',15,2,'INDETERMINATE','','',NULL,2,0,5),(46,'VPath',15,3,'INDETERMINATE','','',NULL,3,0,5),(47,'VPath',13,3,'INDETERMINATE','','',NULL,7,0,5),(48,'ESTurn',1,2,'INDETERMINATE','','',NULL,8,0,1),(49,'WSTurn',3,2,'INDETERMINATE','','',NULL,2,0,1),(50,'HPath',2,2,'INDETERMINATE','','',NULL,1,0,1),(51,'ENTurn',1,4,'INDETERMINATE','','',NULL,6,0,1),(52,'WNTurn',3,4,'INDETERMINATE','','',NULL,4,0,1),(53,'HPath',2,4,'INDETERMINATE','','',NULL,5,0,1),(54,'VPath',3,3,'INDETERMINATE','','',NULL,3,0,1),(55,'VPath',1,3,'INDETERMINATE','','',NULL,7,0,1),(64,'ENTurn',5,4,'INDETERMINATE','','',NULL,6,0,3),(65,'WNTurn',7,4,'INDETERMINATE','','',NULL,4,0,3),(66,'VPath',7,3,'INDETERMINATE','','',NULL,3,0,3),(67,'HPath',6,2,'INDETERMINATE','','',NULL,1,0,1),(68,'ESTurn',5,2,'INDETERMINATE','','',NULL,8,0,3),(69,'WSTurn',7,2,'INDETERMINATE','','',NULL,2,0,3),(70,'VPath',5,3,'INDETERMINATE','','',NULL,7,0,3),(71,'HPath',6,4,'INDETERMINATE','','',NULL,5,0,3);
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

-- Dump completed on 2011-09-26 14:13:02
