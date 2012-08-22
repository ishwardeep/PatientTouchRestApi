SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `patienttouch_v1` ;
CREATE SCHEMA IF NOT EXISTS `patienttouch_v1` DEFAULT CHARACTER SET latin1 ;
USE `patienttouch_v1` ;

-- -----------------------------------------------------
-- Table `Practice`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Practice` ;

CREATE  TABLE IF NOT EXISTS `Practice` (
  `practiceid` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `email` VARCHAR(64) NOT NULL ,
  `phone` VARCHAR(16) NOT NULL ,
  `del` TINYINT NULL DEFAULT 0 ,
  PRIMARY KEY (`practiceid`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) ,
  UNIQUE INDEX `phone_UNIQUE` (`phone` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User` ;

CREATE  TABLE IF NOT EXISTS `User` (
  `userid` INT NOT NULL AUTO_INCREMENT ,
  `username` VARCHAR(128) NOT NULL COMMENT '	' ,
  `password` VARCHAR(32) NOT NULL ,
  `firstName` VARCHAR(64) NOT NULL ,
  `lastName` VARCHAR(64) NOT NULL ,
  `practiceid` INT NULL ,
  `role` ENUM('SUPERUSER','ADMIN','WEBUSER') NOT NULL ,
  `firstLogin` DATETIME NULL ,
  `lastLogin` DATETIME NULL ,
  `del` TINYINT NULL DEFAULT 0 ,
  PRIMARY KEY (`userid`) ,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) ,
  CONSTRAINT `fk_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Office`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Office` ;

CREATE  TABLE IF NOT EXISTS `Office` (
  `officeid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NOT NULL ,
  `name` VARCHAR(128) NOT NULL ,
  `streetAddress1` VARCHAR(128) NOT NULL ,
  `streetAddress2` VARCHAR(128) NULL ,
  `city` VARCHAR(32) NOT NULL ,
  `cityShort` VARCHAR(16) NOT NULL ,
  `state` VARCHAR(16) NOT NULL ,
  `zip` VARCHAR(10) NOT NULL ,
  `phone` VARCHAR(16) NOT NULL ,
  `mainOffice` TINYINT(1) NOT NULL DEFAULT false ,
  PRIMARY KEY (`officeid`) ,
  CONSTRAINT `fk_office_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Doctor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Doctor` ;

CREATE  TABLE IF NOT EXISTS `Doctor` (
  `doctorid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NOT NULL ,
  `firstName` VARCHAR(128) NOT NULL ,
  `lastName` VARCHAR(128) NOT NULL ,
  `nickName` VARCHAR(128) NOT NULL ,
  `speciality` VARCHAR(128) NULL ,
  `del` TINYINT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`doctorid`) ,
  CONSTRAINT `fk_doctor_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PracticeOfficeDoctorMapping`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PracticeOfficeDoctorMapping` ;

CREATE  TABLE IF NOT EXISTS `PracticeOfficeDoctorMapping` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `officeid` INT NOT NULL ,
  `doctorid` INT NOT NULL ,
  `del` TINYINT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_office_id`
    FOREIGN KEY (`officeid` )
    REFERENCES `Office` (`officeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_doctor_id`
    FOREIGN KEY (`doctorid` )
    REFERENCES `Doctor` (`doctorid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Billing`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Billing` ;

CREATE  TABLE IF NOT EXISTS `Billing` (
  `billingid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NOT NULL ,
  `contactFirstName` VARCHAR(64) NOT NULL ,
  `contactLastName` VARCHAR(64) NOT NULL ,
  `streetAddress1` VARCHAR(128) NOT NULL ,
  `streetAddress2` VARCHAR(128) NULL ,
  `city` VARCHAR(32) NOT NULL ,
  `state` VARCHAR(16) NOT NULL ,
  `zip` VARCHAR(10) NOT NULL ,
  PRIMARY KEY (`billingid`) ,
  CONSTRAINT `fk_billing_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SmsTemplates`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SmsTemplates` ;

CREATE  TABLE IF NOT EXISTS `SmsTemplates` (
  `smstemplateid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NULL ,
  `name` VARCHAR(64) NOT NULL ,
  `type` ENUM('REMINDER','WAITLIST') NOT NULL ,
  `message` TEXT NOT NULL ,
  `confirmMessage` TEXT NULL ,
  `rescheduleMessage` TEXT NULL ,
  `role` ENUM('SUPERUSER','ADMIN','WEBUSER') NOT NULL ,
  `del` TINYINT NOT NULL ,
  PRIMARY KEY (`smstemplateid`) ,
  CONSTRAINT `fk_smstemplates_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Campaign`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Campaign` ;

CREATE  TABLE IF NOT EXISTS `Campaign` (
  `campaignid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NOT NULL ,
  `name` VARCHAR(64) NOT NULL ,
  `type` ENUM('REMINDER','WAITLIST') NOT NULL ,
  `smstemplateid` INT NULL ,
  `followUpCampaign` TINYINT(1) NOT NULL DEFAULT false ,
  `message` TEXT NULL ,
  `confirmMessage` TEXT NULL ,
  `rescheduleMessage` TEXT NULL ,
  `scheduleTime` DATETIME NOT NULL ,
  `status` ENUM('SCHEDULED','RUNNING','COMPLETE') NOT NULL ,
  `lastUpdateTime` DATETIME NOT NULL ,
  PRIMARY KEY (`campaignid`) ,
  CONSTRAINT `fk_campaign_practiceid`
    FOREIGN KEY (`practiceid` )
    REFERENCES `Practice` (`practiceid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_campaign_smstemplateid`
    FOREIGN KEY (`smstemplateid` )
    REFERENCES `SmsTemplates` (`smstemplateid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Patient`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Patient` ;

CREATE  TABLE IF NOT EXISTS `Patient` (
  `patientid` INT NOT NULL AUTO_INCREMENT ,
  `firstName` VARCHAR(64) NOT NULL ,
  `lastName` VARCHAR(64) NOT NULL ,
  `phone` VARCHAR(16) NOT NULL ,
  PRIMARY KEY (`patientid`) ,
  INDEX `phone_idx` (`phone` ASC) ,
  UNIQUE INDEX `patientinfo_idx` (`phone` ASC, `firstName` ASC, `lastName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `AppointmentInfo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `AppointmentInfo` ;

CREATE  TABLE IF NOT EXISTS `AppointmentInfo` (
  `appointmentinfoid` INT NOT NULL AUTO_INCREMENT ,
  `campaignid` INT NOT NULL ,
  `doctorid` INT NOT NULL ,
  `officeid` INT NOT NULL ,
  `patientid` INT NOT NULL ,
  `appointmentDate` DATE NOT NULL ,
  `appointmentTime` VARCHAR(12) NOT NULL ,
  `lastUpdateTime` TIMESTAMP NOT NULL ,
  `status` ENUM('TRYING','UNABLE_TO_SEND_MESSAGE','MESSAGE_SENT','NOT_DELIVERED','INVALID_PHONE','DELIVERED','ACCEPTED','CANCEL','RESCHEDULE','PROBLEM','NO_RESPONSE') NOT NULL ,
  PRIMARY KEY (`appointmentinfoid`) ,
  CONSTRAINT `fk_appointmentinfo_campaignid`
    FOREIGN KEY (`campaignid` )
    REFERENCES `Campaign` (`campaignid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_appinfo_doctorid`
    FOREIGN KEY (`doctorid` )
    REFERENCES `Doctor` (`doctorid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_appinfo_officeid`
    FOREIGN KEY (`officeid` )
    REFERENCES `Office` (`officeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_appinfo_patientid`
    FOREIGN KEY (`patientid` )
    REFERENCES `Patient` (`patientid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Waitlist`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Waitlist` ;

CREATE  TABLE IF NOT EXISTS `Waitlist` (
  `waitlistid` INT NOT NULL AUTO_INCREMENT ,
  `practiceid` INT NOT NULL ,
  `doctorid` INT NOT NULL ,
  `officeid` INT NOT NULL ,
  `patientid` INT NOT NULL ,
  `priority` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`waitlistid`) ,
  UNIQUE INDEX `unique_waitlist` (`doctorid` ASC, `officeid` ASC, `patientid` ASC) ,
  CONSTRAINT `fk_waitlist_doctorid`
    FOREIGN KEY (`doctorid` )
    REFERENCES `Doctor` (`doctorid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_waitlist_officeid`
    FOREIGN KEY (`officeid` )
    REFERENCES `Office` (`officeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_waitlist_patientid`
    FOREIGN KEY (`patientid` )
    REFERENCES `Patient` (`patientid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SmsMessage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SmsMessage` ;

CREATE  TABLE IF NOT EXISTS `SmsMessage` (
  `smsid` INT NOT NULL AUTO_INCREMENT ,
  `appointmentinfoid` INT NOT NULL ,
  `messageText` TEXT NOT NULL ,
  `smsTimestamp` TIMESTAMP NOT NULL ,
  `phoneNumber` VARCHAR(16) NOT NULL ,
  `triggerId` VARCHAR(16) NULL ,
  `shortCode` VARCHAR(16) NULL ,
  `vendorTransactionid` VARCHAR(64) NULL ,
  `vendorStatusMessage` VARCHAR(128) NULL ,
  `messageDeliveryStatus` VARCHAR(128) NULL ,
  `status` ENUM('SMS_SUBMISSION_PENDING','SMS_SUBMITED_SUCCESSFULLY','SMS_SUBMISSION_ERROR','SMS_DELIVERED','SMS_NOT_DELIVERED') NOT NULL ,
  `type` ENUM('INITIAL','CONFIRM','RESCHEDULE','RESPONSE') NOT NULL ,
  PRIMARY KEY (`smsid`) ,
  CONSTRAINT `fk_smsmessage_appointmentinfoid`
    FOREIGN KEY (`appointmentinfoid` )
    REFERENCES `AppointmentInfo` (`appointmentinfoid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ApplicationProperties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ApplicationProperties` ;

CREATE  TABLE IF NOT EXISTS `ApplicationProperties` (
  `propid` INT NOT NULL AUTO_INCREMENT ,
  `key` VARCHAR(128) NOT NULL ,
  `value` VARCHAR(128) NOT NULL ,
  PRIMARY KEY (`propid`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
