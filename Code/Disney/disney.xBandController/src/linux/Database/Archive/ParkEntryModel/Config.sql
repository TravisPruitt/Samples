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
-- Table structure for table `Config`
--

DROP TABLE IF EXISTS `Config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Config` (
  `class` varchar(64) NOT NULL,
  `property` varchar(32) NOT NULL,
  `value` varchar(1024) NOT NULL,
  PRIMARY KEY (`class`,`property`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Scalar configuration parameters for controller';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Config`
--

LOCK TABLES `Config` WRITE;
/*!40000 ALTER TABLE `Config` DISABLE KEYS */;
INSERT INTO `Config` VALUES ('AttractionModelConfig','abandonmenttimeout','3600'),('AttractionModelConfig','loadtype','trailing'),('AttractionModelConfig','metricsperiod','15'),('AttractionModelConfig','onridetimeout','2'),('ControllerInfo','abandonmenttimeout','3600'),('ControllerInfo','conn','jdbc:mysql://localhost/Mayhem?user=$USER&password=$PASSWORD'),('ControllerInfo','discoverynetprefix','10.'),('ControllerInfo','eventdumpfile','eventdump.txt'),('ControllerInfo','eventsperbatch','500'),('ControllerInfo','jmsretryperiod','1000'),('ControllerInfo','loadtype','trailing'),('ControllerInfo','metricsperiod','15'),('ControllerInfo','model','com.disney.xband.xbrc.parkentrymodel.CEP'),('ControllerInfo','onridetimeout','2'),('ControllerInfo','pass','Mayhem!23'),('ControllerInfo','pushmode','true'),('ControllerInfo','updatestreamurl',''),('ControllerInfo','url','http://localhost:8080/ControllerServer'),('ControllerInfo','user','EMUser'),('ControllerInfo','venue','xCoaster'),('ControllerInfo','verbose','true'),('ControllerInfo','wachedbandtimeout','180'),('ControllerInfo','watchedbandtimeout','180'),('ControllerInfo','xviewurl','http://xbrc-test:8090/Xview'),('ESBInfo','jmsbroker','#10.75.6.231:2506'),('ESBInfo','jmscontrolqueue','disney.xband.xbrc.controlqueue'),('ESBInfo','jmsdiscoverytimesec','10'),('ESBInfo','jmsdiscoverytopic','disney.xband.xbrc.discoverytopic'),('ESBInfo','jmspassword','Administrator'),('ESBInfo','jmsqueryqueue','disney.xband.xbrc.queryqueue'),('ESBInfo','jmsrequestqueue','com.synapse.xbrcIn'),('ESBInfo','jmsrser',''),('ESBInfo','jmstopic','com.synapse.xbrc'),('ESBInfo','jmsuser','Administrator'),('EventConfig','amplifiedsignalstrengthmax','63'),('EventConfig','amplifiedsignalstrengthmin','0'),('EventConfig','deltatime','3000'),('ParkEntryModelConfig','abandonmenttimesec','40'),('ParkEntryModelConfig','greenlighttimeoutsec','5'),('ParkEntryModelConfig','maxfpscanretry','3'),('ParkEntryModelConfig','omniticketaddress','localhost'),('ParkEntryModelConfig','omniticketport','33433'),('ParkEntryModelConfig','timetodapartgatesec','30'),('ReaderConfig','gainsliderincrement','0.1'),('ReaderConfig','maximumgain','2.0'),('ReaderConfig','maximumthreshold','63'),('ReaderConfig','minimumgain','-1.0'),('ReaderConfig','minimumthreshold','0'),('ReaderConfig','thresholdsliderincrement','1'),('ReaderConfig','xbrcconfigmodseq','7'),('ServiceLocator','service_implementor_suffix','Imp'),('UIAttractionViewConfig','gridheight','9'),('UIAttractionViewConfig','gridsize','50'),('UIAttractionViewConfig','gridwidth','18'),('UIAttractionViewConfig','guesticonheight','24'),('UIAttractionViewConfig','guesticonspacing','10'),('UIAttractionViewConfig','guesticonstagger','4'),('UIAttractionViewConfig','guesticonwidth','24'),('UIAttractionViewConfig','maxguestspericon','5'),('UIAttractionViewConfig','maxiconspergriditem','3'),('UIConfig','controllerurl','http://localhost:8080/ControllerServer'),('UIConfig','guestxviewcachetimesec','1800'),('UIConfig','xviewurl','http://10.75.3.106:8080/Xview'),('XfpeSimConfig','biomatchthreshold','75'),('XfpeSimConfig','controllerurl','http://localhost:8080/ControllerServer'),('XfpeSimConfig','guestqueuestartx','0'),('XfpeSimConfig','guestqueuestarty','7'),('XfpeSimConfig','hellointervalsec','20'),('XfpeSimConfig','maxscanretry','3'),('XfpeSimConfig','omnithreadport','33433');
/*!40000 ALTER TABLE `Config` ENABLE KEYS */;
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
