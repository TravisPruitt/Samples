-- MySQL dump 10.13  Distrib 5.1.63, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: Mayhem
-- ------------------------------------------------------
-- Server version	5.1.63-0ubuntu0.11.04.1

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
-- Table structure for table `SchedulerItem`
--

DROP TABLE IF EXISTS `SchedulerItem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SchedulerItem` (
  `itemKey` varchar(128) NOT NULL,
  `description` varchar(256) NOT NULL,
  `jobClassName` varchar(256) NOT NULL,
  `schedulingExpression` varchar(64) NOT NULL,
  `runOnceDate` datetime DEFAULT NULL,
  `updatedBy` varchar(64) NOT NULL,
  `updatedDate` datetime NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`itemKey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchedulerItem`
--

LOCK TABLES `SchedulerItem` WRITE;
/*!40000 ALTER TABLE `SchedulerItem` DISABLE KEYS */;
INSERT INTO `SchedulerItem` VALUES ('615d3f39-b1ab-4b40-8342-d4c1a446b190','Park Entry Guest Data Export Task','com.disney.xband.xbrc.parkentrymodel.scheduler.DatabaseDataExport','0 0 3 * * ?',NULL,'admin','2013-06-26 15:13:50',1);
/*!40000 ALTER TABLE `SchedulerItem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SchedulerItemParameter`
--

DROP TABLE IF EXISTS `SchedulerItemParameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SchedulerItemParameter` (
  `itemKey` varchar(128) NOT NULL DEFAULT '',
  `name` varchar(64) NOT NULL DEFAULT '',
  `value` varchar(1024) NOT NULL,
  `sequence` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`itemKey`,`name`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`itemKey`) REFERENCES `SchedulerItem` (`itemKey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchedulerItemParameter`
--

LOCK TABLES `SchedulerItemParameter` WRITE;
/*!40000 ALTER TABLE `SchedulerItemParameter` DISABLE KEYS */;
INSERT INTO `SchedulerItemParameter` VALUES ('615d3f39-b1ab-4b40-8342-d4c1a446b190','database.password','nge.xconnect.xbrc.dbserver.view.pwd',3),('615d3f39-b1ab-4b40-8342-d4c1a446b190','database.user','nge.xconnect.xbrc.dbserver.view.uid',2),('615d3f39-b1ab-4b40-8342-d4c1a446b190','keep.files','3',4),('615d3f39-b1ab-4b40-8342-d4c1a446b190','script.name','exportParkEntryData.sh',1);
/*!40000 ALTER TABLE `SchedulerItemParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SchedulerLog`
--

DROP TABLE IF EXISTS `SchedulerLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SchedulerLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `itemKey` varchar(128) NOT NULL,
  `description` varchar(256) NOT NULL,
  `jobClassName` varchar(256) NOT NULL,
  `parameters` varchar(2048) NOT NULL,
  `startDate` datetime NOT NULL,
  `finishDate` datetime DEFAULT NULL,
  `success` tinyint(1) DEFAULT NULL,
  `statusReport` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `startDate_index` (`startDate`),
  KEY `jobClassName_index` (`jobClassName`)
) ENGINE=InnoDB AUTO_INCREMENT=354 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SchedulerLog`
--

LOCK TABLES `SchedulerLog` WRITE;
/*!40000 ALTER TABLE `SchedulerLog` DISABLE KEYS */;
/*!40000 ALTER TABLE `SchedulerLog` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.24','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-06-26 15:15:39
