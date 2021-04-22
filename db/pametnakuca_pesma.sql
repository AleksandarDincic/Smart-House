-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pametnakuca
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pesma`
--

DROP TABLE IF EXISTS `pesma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pesma` (
  `idPes` int NOT NULL AUTO_INCREMENT,
  `naziv` varchar(45) NOT NULL,
  `idKor` int NOT NULL,
  PRIMARY KEY (`idPes`),
  UNIQUE KEY `idPes_UNIQUE` (`idPes`),
  KEY `FK_idKor_Korisnik_idx` (`idKor`),
  CONSTRAINT `FK_idKor_Korisnik` FOREIGN KEY (`idKor`) REFERENCES `korisnik` (`idKor`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pesma`
--

LOCK TABLES `pesma` WRITE;
/*!40000 ALTER TABLE `pesma` DISABLE KEYS */;
INSERT INTO `pesma` VALUES (1,'sibirski plavac',4),(2,'despacito',2),(3,'all star',2),(4,'crazy frog',4),(6,'macarena',4),(7,'macarena',4),(9,'dve ajkule',4),(10,'dve ajkule',4),(11,'macarena',4),(12,'dark souls trilogija',2),(13,'dark souls trilogija',2),(14,'despacito',4),(15,'crazy frog',2),(16,'crazy frog',2),(17,'witch doctor',4),(18,'africa',2),(19,'despacito',4),(20,'despacito',4);
/*!40000 ALTER TABLE `pesma` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-24 21:49:21
