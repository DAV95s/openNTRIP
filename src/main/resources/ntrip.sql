-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: localhost    Database: ntrip
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.13-MariaDB

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
-- Table structure for table `base_stations`
--

DROP TABLE IF EXISTS `base_stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `base_stations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mountpoint` varchar(100) NOT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `format-details` varchar(255) DEFAULT NULL,
  `carrier` int(1) DEFAULT NULL,
  `nav-system` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `lla` point DEFAULT NULL,
  `altitude` decimal(15,10) DEFAULT NULL,
  `bitrate` int(11) NOT NULL DEFAULT 0,
  `misc` varchar(255) DEFAULT '',
  `is_online` int(1) DEFAULT 0,
  `password` varchar(255) NOT NULL DEFAULT '',
  `hz` int(2) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mountpoint` (`mountpoint`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `base_stations`
--

LOCK TABLES `base_stations` WRITE;
/*!40000 ALTER TABLE `base_stations` DISABLE KEYS */;
INSERT INTO `base_stations` VALUES (1,'AL1','1231aa','fafaf','123',2,'dddaGFPS','RUS','\0\0\0\0\0\0\0\0\0\0\0\0€@@\0\0\0\0\0\0F@',0.0000000000,123,'',1,'44444',4),(2,'AL2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'',0,'44111',0);
/*!40000 ALTER TABLE `base_stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `casters`
--

DROP TABLE IF EXISTS `casters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `casters` (
  `id` int(11) NOT NULL,
  `address` varchar(40) NOT NULL,
  `port` int(5) NOT NULL,
  `group_id` int(11) NOT NULL,
  `status` int(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `port` (`port`),
  KEY `group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `casters`
--

LOCK TABLES `casters` WRITE;
/*!40000 ALTER TABLE `casters` DISABLE KEYS */;
INSERT INTO `casters` VALUES (0,'localhost',8500,1,1);
/*!40000 ALTER TABLE `casters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients_log`
--

DROP TABLE IF EXISTS `clients_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `time_mark` datetime NOT NULL DEFAULT current_timestamp(),
  `coordinates` point DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `user-agent` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `time_mark` (`time_mark`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients_log`
--

LOCK TABLES `clients_log` WRITE;
/*!40000 ALTER TABLE `clients_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `clients_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group` varchar(100) NOT NULL,
  `key` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `group` (`group`),
  KEY `key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'fail2ban','attempts','10'),(2,'fail2ban','ban_time_min','30'),(3,'clients','authorization','none'),(4,'stations','authorization','none'),(5,'stations','Hz','1'),(6,'system','time_out_sec','10');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (1,'admin','Administrator'),(2,'members','General User');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_attempts`
--

DROP TABLE IF EXISTS `login_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login_attempts` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(45) NOT NULL,
  `login` varchar(100) NOT NULL,
  `time` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_attempts`
--

LOCK TABLES `login_attempts` WRITE;
/*!40000 ALTER TABLE `login_attempts` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mountpoints`
--

DROP TABLE IF EXISTS `mountpoints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mountpoints` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mountpoint` varchar(100) NOT NULL,
  `identifier` varchar(255) NOT NULL DEFAULT ' ',
  `format` varchar(255) NOT NULL DEFAULT '',
  `format-details` varchar(255) DEFAULT NULL,
  `carrier` int(1) DEFAULT NULL,
  `nav-system` varchar(255) DEFAULT NULL,
  `network` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `nmea` int(1) DEFAULT NULL,
  `solution` int(1) DEFAULT NULL,
  `generator` varchar(255) DEFAULT NULL,
  `compression` varchar(255) DEFAULT NULL,
  `authentication` varchar(50) DEFAULT NULL,
  `fee` int(1) DEFAULT NULL,
  `bitrate` int(11) DEFAULT NULL,
  `misc` varchar(255) DEFAULT NULL,
  `caster_id` int(11) DEFAULT 0,
  `bases_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `available` int(1) NOT NULL DEFAULT 1,
  `plugin_id` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `mountpoint` (`mountpoint`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mountpoints`
--

LOCK TABLES `mountpoints` WRITE;
/*!40000 ALTER TABLE `mountpoints` DISABLE KEYS */;
INSERT INTO `mountpoints` VALUES (1,'test1','Juneau','RTCM 3.1','1004(1),1005(30),1007(30),1033(30)',2,'GPS',NULL,'USA',58.416774365884315,-134.5453031026356,0,NULL,'',NULL,'Basic',NULL,0,NULL,0,NULL,1,NULL),(2,'test2','Juneau','RTCM 3.0','1004(1)',2,'GPS',NULL,'USA',58.416774365884315,-134.5453031026356,1,NULL,'',NULL,'Basic',NULL,0,NULL,0,NULL,1,NULL);
/*!40000 ALTER TABLE `mountpoints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(45) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(254) NOT NULL,
  `activation_selector` varchar(255) DEFAULT NULL,
  `activation_code` varchar(255) DEFAULT NULL,
  `forgotten_password_selector` varchar(255) DEFAULT NULL,
  `forgotten_password_code` varchar(255) DEFAULT NULL,
  `forgotten_password_time` int(11) unsigned DEFAULT NULL,
  `remember_selector` varchar(255) DEFAULT NULL,
  `remember_code` varchar(255) DEFAULT NULL,
  `created_on` int(11) unsigned NOT NULL,
  `last_login` int(11) unsigned DEFAULT NULL,
  `active` tinyint(1) unsigned DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_email` (`email`),
  UNIQUE KEY `uc_activation_selector` (`activation_selector`),
  UNIQUE KEY `uc_forgotten_password_selector` (`forgotten_password_selector`),
  UNIQUE KEY `uc_remember_selector` (`remember_selector`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'127.0.0.1','administrator','$2y$12$c8pG2YTdvnTPbaxafH1aH.NDvcibJ8HbzRrXyYfiDkHB8pShZBxnq','admin@admin.com',NULL,'',NULL,NULL,NULL,NULL,NULL,1268889823,1590842478,1,'Admin','istrator','ADMIN','0');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_groups`
--

DROP TABLE IF EXISTS `users_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `group_id` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_users_groups` (`user_id`,`group_id`),
  KEY `fk_users_groups_users1_idx` (`user_id`),
  KEY `fk_users_groups_groups1_idx` (`group_id`),
  CONSTRAINT `fk_users_groups_groups1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_groups_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_groups`
--

LOCK TABLES `users_groups` WRITE;
/*!40000 ALTER TABLE `users_groups` DISABLE KEYS */;
INSERT INTO `users_groups` VALUES (1,1,1),(2,1,2);
/*!40000 ALTER TABLE `users_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ntrip'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-16 18:43:55
