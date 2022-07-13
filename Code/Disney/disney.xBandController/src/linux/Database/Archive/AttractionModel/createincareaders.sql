
LOCK TABLES `Location` WRITE;
/*!40000 ALTER TABLE `Location` DISABLE KEYS */;
INSERT INTO `Location` VALUES (9,5,'InCar',82,0,0,1);
/*!40000 ALTER TABLE `Location` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `Reader` WRITE;
/*!40000 ALTER TABLE `Reader` DISABLE KEYS */;
INSERT INTO `Reader` VALUES (32,0,'incar-1',136,9,'incar','0','http://localhost:8051',0,0,'',-1,9),(29,0,'incar-2',136,12,'incar','0','http://localhost:8052',0,0,'',-1,9),(30,0,'incar-3',139,9,'incar','0','http://localhost:8053',0,0,'',-1,9),(31,0,'incar-4',139,12,'incar','0','http://localhost:8054',0,0,'',-1,9);
/*!40000 ALTER TABLE `Reader` ENABLE KEYS */;
UNLOCK TABLES;


