CREATE DATABASE IF NOT EXISTS `Tolkeadministration` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `Tolkeadministration`;

DROP TABLE IF EXISTS `Tilknyttet`;
DROP TABLE IF EXISTS `Modtager`;
DROP TABLE IF EXISTS `Opgave`;
DROP TABLE IF EXISTS `Bevilling`;
DROP TABLE IF EXISTS `Bevillinggiver`;
DROP TABLE IF EXISTS `Bruger`;
DROP TABLE IF EXISTS `Tolk`;
-- DROP TABLE IF EXISTS `offices`;

CREATE TABLE `Bruger` (  
    `BrugerID` int(10) NOT NULL AUTO_INCREMENT, 
    `Navn` varchar(50) NOT NULL, 
    `Efternavn` varchar(50) NOT NULL, 
    `Telefon` varchar(50), 
    `Email` varchar(50), 
    `Adresse` varchar(50), 
    `Postnr` varchar(50), 
    PRIMARY KEY (`BrugerID`));


CREATE TABLE `Tolk` (
  `TolkID` int(11) NOT NULL AUTO_INCREMENT,
  `Navn` varchar(50) NOT NULL,
  `Efternavn` varchar(50) NOT NULL,
  `Telefon` varchar(10) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Adresse` varchar(50) NOT NULL,
  `Postnr` int(4),
  PRIMARY KEY (`TolkID`));

CREATE TABLE `Bevillinggiver` (
  `GiverID` int(11) NOT NULL AUTO_INCREMENT,
  `Navn` varchar(50) NOT NULL,
  `Telefon` varchar(10) NOT NULL,
  `Email` varchar(100) NOT NULL,
  PRIMARY KEY (`giverID`));

CREATE TABLE `Bevilling` (
  `Bevillingsnummer` int(11) NOT NULL AUTO_INCREMENT,
  `Bruger` int(10) NOT NULL,
  `Giver` int(11) NOT NULL,
  `Timer` int(5) NOT NULL,
  `BrugtTimer` int(5) DEFAULT NULL,
  PRIMARY KEY (`Bevillingsnummer`),
  KEY `Bruger` (`Bruger`),
  KEY `Giver` (`Giver`),
  CONSTRAINT `FK_BevillingBruger` FOREIGN KEY (`Bruger`) REFERENCES `Bruger` (`BrugerID`),
  CONSTRAINT `FK_BevillingGiver` FOREIGN KEY (`Giver`) REFERENCES `Bevillinggiver` (`GiverID`)
); 

CREATE TABLE `Opgave` (
  `Opgavenummer` int(11) NOT NULL AUTO_INCREMENT,
  `Bestiller` int(10) NOT NULL,
  `Bevillingsnummer` int(11) DEFAULT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `StartDatoTid` date NOT NULL,
  `Længde` int(4) DEFAULT NULL,
  `Adresse` varchar(50) DEFAULT NULL,
  `Postnr` int(4) DEFAULT NULL,
  `Lokal` varchar(50) DEFAULT NULL,
  `Ekstra` text DEFAULT NULL,
  PRIMARY KEY (`Opgavenummer`),
  KEY `Bestiller` (`Bestiller`),
  KEY `Bevillingsnummer` (`Bevillingsnummer`),
  CONSTRAINT `FK_OpgaveBevilling` FOREIGN KEY (`Bevillingsnummer`) REFERENCES `Bevilling` (`Bevillingsnummer`),
  CONSTRAINT `FK_OpgaveBestiller` FOREIGN KEY (`Bestiller`) REFERENCES `Bruger` (`BrugerID`)
);

CREATE TABLE `Modtager` (
    `Opgavenummer` int(11) NOT NULL,
    `BrugerID` int(10) NOT NULL,
    PRIMARY KEY (`Opgavenummer`, `BrugerID`),
    KEY `Opgavenummer` (`Opgavenummer`),
    KEY `BrugerID` (`BrugerID`),
    CONSTRAINT `FK_ModtagerOpgavenummer` FOREIGN KEY (`Opgavenummer`) REFERENCES `Opgave` (`Opgavenummer`),
    CONSTRAINT `FK_ModtagerBrugerID` FOREIGN KEY (`BrugerID`) REFERENCES `Bruger` (`BrugerID`)  
);

CREATE TABLE `Tilknyttet` (
    `Opgavenummer` int(11) NOT NULL,
    `TolkID` int(10) NOT NULL,
    PRIMARY KEY (`Opgavenummer`, `TolkID`),
    KEY `Opgavenummer` (`Opgavenummer`),
    KEY `TolkID` (`TolkID`),
    CONSTRAINT `FK_Tilknyttetopgave` FOREIGN KEY (`Opgavenummer`) REFERENCES `Opgave` (`Opgavenummer`),
    CONSTRAINT `FK_TilknyttetTolk` FOREIGN KEY (`TolkID`) REFERENCES `Tolk` (`TolkID`)  
);
