SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

--
-- Creating TransmitCommand table.
--
CREATE TABLE `Mayhem`.`TransmitCommand` (
  `id` INTEGER  NOT NULL AUTO_INCREMENT,
  `command` TINYINT  NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

ALTER TABLE `Mayhem`.`TransmitCommand` ADD COLUMN `interval` INTEGER  DEFAULT NULL AFTER `command`;
ALTER TABLE `Mayhem`.`TransmitCommand` MODIFY COLUMN `command` TINYINT(4)  NOT NULL,
 ADD COLUMN `timeout` INTEGER  DEFAULT NULL AFTER `interval`;
ALTER TABLE `Mayhem`.`TransmitCommand` MODIFY COLUMN `timeout` INTEGER  DEFAULT NULL,
 ADD COLUMN `mode` VARCHAR(20)  NOT NULL AFTER `timeout`;
ALTER TABLE `Mayhem`.`TransmitCommand` ADD COLUMN `recipient` INTEGER  NOT NULL AFTER `mode`;
ALTER TABLE `Mayhem`.`TransmitCommand` MODIFY COLUMN `command` VARCHAR(32)  NOT NULL;

--
-- Creating TransmitPayload table.
--
CREATE TABLE `Mayhem`.`TransmitPayload` (
  `readerId` INTEGER  NOT NULL,
  `commandId` INTEGER  NOT NULL,
  PRIMARY KEY (`readerId`, `commandId`),
  CONSTRAINT `reader_fk_constraint` FOREIGN KEY `reader_fk_constraint` (`readerId`)
    REFERENCES `Reader` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)
ENGINE = InnoDB;

ALTER TABLE `Mayhem`.`TransmitPayload` ADD CONSTRAINT `transmitCommand_fk_constraint` FOREIGN KEY `transmitCommand_fk_constraint` (`commandId`)
    REFERENCES `TransmitCommand` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

--
-- Dumping data for table `SchemaVersion`
--

LOCK TABLES `SchemaVersion` WRITE;
/*!40000 ALTER TABLE `SchemaVersion` DISABLE KEYS */;
DELETE FROM `SchemaVersion`;
INSERT INTO `SchemaVersion` VALUES ('1.0.0.3','spacemodel');
/*!40000 ALTER TABLE `SchemaVersion` ENABLE KEYS */;
UNLOCK TABLES;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



