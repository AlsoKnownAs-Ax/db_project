-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: quackstagram
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `follows`
--

DROP TABLE IF EXISTS `follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follows` (
  `id` int NOT NULL AUTO_INCREMENT,
  `following_user_id` int DEFAULT NULL,
  `followed_user_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `following_user_id` (`following_user_id`),
  KEY `followed_user_id` (`followed_user_id`),
  CONSTRAINT `follows_ibfk_1` FOREIGN KEY (`following_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `follows_ibfk_2` FOREIGN KEY (`followed_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,1,2,'2024-05-01 08:00:00'),(2,1,3,'2024-05-01 08:05:00'),(3,1,4,'2024-05-01 08:10:00'),(4,1,5,'2024-05-01 08:15:00'),(5,2,1,'2024-05-02 08:00:00'),(6,2,3,'2024-05-02 08:05:00'),(7,2,4,'2024-05-02 08:10:00'),(8,2,5,'2024-05-02 08:15:00'),(9,3,1,'2024-05-03 08:00:00'),(10,3,2,'2024-05-03 08:05:00'),(11,3,4,'2024-05-03 08:10:00'),(12,3,5,'2024-05-03 08:15:00'),(13,4,1,'2024-05-04 08:00:00'),(14,4,2,'2024-05-04 08:05:00'),(15,4,3,'2024-05-04 08:10:00'),(16,4,5,'2024-05-04 08:15:00'),(17,5,1,'2024-05-05 08:00:00'),(18,5,2,'2024-05-05 08:05:00'),(19,5,3,'2024-05-05 08:10:00'),(20,5,4,'2024-05-05 08:15:00');
/*!40000 ALTER TABLE `follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `target_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `posts` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,1,2,1,'2024-05-01 08:00:00'),(2,1,3,1,'2024-05-01 08:01:00'),(3,1,4,1,'2024-05-01 08:02:00'),(4,2,1,2,'2024-05-02 08:00:00'),(5,2,3,2,'2024-05-02 08:01:00'),(6,2,4,2,'2024-05-02 08:02:00'),(7,3,1,3,'2024-05-03 08:00:00'),(8,3,2,3,'2024-05-03 08:01:00'),(9,3,4,3,'2024-05-03 08:02:00'),(10,4,1,4,'2024-05-04 08:00:00'),(11,4,2,4,'2024-05-04 08:01:00'),(12,4,3,4,'2024-05-04 08:02:00'),(13,5,1,5,'2024-05-05 08:00:00'),(14,5,2,5,'2024-05-05 08:01:00'),(15,5,3,5,'2024-05-05 08:02:00'),(16,1,5,1,'2024-05-01 08:03:00'),(17,2,5,2,'2024-05-02 08:03:00'),(18,3,5,3,'2024-05-03 08:03:00'),(19,4,5,4,'2024-05-04 08:03:00'),(20,5,4,5,'2024-05-05 08:03:00');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `post_id` int NOT NULL,
  `likes` int DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `bio` text COMMENT 'Description',
  `user_id` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `backdrop_path` varchar(255) DEFAULT NULL COMMENT 'path of the iamge',
  PRIMARY KEY (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,120,'Beautiful Sunset','Description of a beautiful sunset.',1,'Active','2024-05-01 16:30:00','/images/sunset.jpg'),(2,95,'Morning Routine','Talk about the morning routine.',2,'Active','2024-05-02 05:15:00','/images/morning.jpg'),(3,75,'Tech Review','Detailed review of the latest tech gadgets.',3,'Active','2024-05-03 13:20:00','/images/tech.jpg'),(4,200,'Fitness Goals','Sharing personal fitness goals for the year.',4,'Active','2024-05-04 10:00:00','/images/fitness.jpg'),(5,150,'Book Recommendations','List of must-read books.',5,'Active','2024-05-05 18:00:00','/images/books.jpg');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `follwing` varchar(255) DEFAULT NULL,
  `profile_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'jeffreywhitaker','@5ECyIDX2g(b','Decide too four any seat avoid.',NULL,'/staff/vote.jpg'),(2,'susangibbs','3_vBOs)L)P4B','Speak marriage turn work practice.',NULL,'/sign/entire.jpg'),(3,'juliaharrington','^(5INwpOoJ&E','With tough from model student.',NULL,'/nothing/important.jpg'),(4,'gbrown','A(I85(+zOBU%','Suddenly trial identify over group computer fact.',NULL,'/member/relate.jpg'),(5,'zgonzales','7!(*','Student upon reflect.',NULL,'/strong/any.jpg');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'quackstagram'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-08 22:31:52
