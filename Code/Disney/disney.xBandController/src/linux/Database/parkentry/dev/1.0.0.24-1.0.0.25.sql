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
-- Dumping data for table `SchedulerItem`
--

LOCK TABLES `SchedulerItem` WRITE;
/*!40000 ALTER TABLE `SchedulerItem` DISABLE KEYS */;
INSERT INTO `SchedulerItem` VALUES ('c3b18ff4-a0ad-4aeb-8b91-3309c8e84b05','Scheduler Log Database Cleanup','com.disney.xband.xbrc.scheduler.SchedulerLogCleanup','0 30 5 * * ?',NULL,'admin','2013-06-27 17:16:08',1),('f1b9c0ee-f5cc-4c15-96f9-7a1cd264b566','Park Entry Guest Data Database Cleanup','com.disney.xband.xbrc.parkentrymodel.scheduler.DatabaseCleanupTask','0 0 5 * * ?',NULL,'admin','2013-06-27 17:01:55',1);
/*!40000 ALTER TABLE `SchedulerItem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SchedulerItemParameter`
--

LOCK TABLES `SchedulerItemParameter` WRITE;
/*!40000 ALTER TABLE `SchedulerItemParameter` DISABLE KEYS */;
INSERT INTO `SchedulerItemParameter` VALUES ('c3b18ff4-a0ad-4aeb-8b91-3309c8e84b05','keep.days','120',1),('f1b9c0ee-f5cc-4c15-96f9-7a1cd264b566','keep.days','120',1);
/*!40000 ALTER TABLE `SchedulerItemParameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.25','parkentrymodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-06-27 17:20:10
