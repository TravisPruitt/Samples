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
-- Table structure for table `xBioTransactionImageAttribute`
--

DROP TABLE IF EXISTS `xBioTransactionImageAttribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xBioTransactionImageAttribute` (
  `xBioTransactionID` int(10) unsigned NOT NULL,
  `xBioImageAttributeID` int(10) unsigned NOT NULL,
  KEY `FK_xBioTransactionImageAttribute_xBioImageAttribute` (`xBioImageAttributeID`),
  KEY `FK_xBioTransactionImageAttribute_xBioTransaction` (`xBioTransactionID`),
  CONSTRAINT `FK_xBioTransactionImageAttribute_xBioImageAttribute` FOREIGN KEY (`xBioImageAttributeID`) REFERENCES `xBioImageAttribute` (`xBioImageAttributeID`),
  CONSTRAINT `FK_xBioTransactionImageAttribute_xBioTransaction` FOREIGN KEY (`xBioTransactionID`) REFERENCES `xBioTransaction` (`xBioTransactionID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xBioTransactionImageAttribute`
--

LOCK TABLES `xBioTransactionImageAttribute` WRITE;
/*!40000 ALTER TABLE `xBioTransactionImageAttribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `xBioTransactionImageAttribute` ENABLE KEYS */;
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
