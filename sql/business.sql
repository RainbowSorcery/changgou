-- MySQL dump 10.13  Distrib 5.7.26, for Linux (x86_64)
--
-- Host: localhost    Database: changgou_business
-- ------------------------------------------------------
-- Server version	5.7.26-log

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
-- Table structure for table `tb_activity`
--

DROP TABLE IF EXISTS `tb_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(200) DEFAULT NULL COMMENT '活动标题',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `content` text COMMENT '活动内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_activity`
--

LOCK TABLES `tb_activity` WRITE;
/*!40000 ALTER TABLE `tb_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_ad`
--

DROP TABLE IF EXISTS `tb_ad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_ad` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) DEFAULT NULL COMMENT '广告名称',
  `position` varchar(50) DEFAULT NULL COMMENT '广告位置',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '到期时间',
  `status` char(1) DEFAULT NULL COMMENT '状态',
  `image` varchar(100) DEFAULT NULL COMMENT '图片地址',
  `url` varchar(100) DEFAULT NULL COMMENT 'URL',
  `remarks` varchar(1000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_ad`
--

LOCK TABLES `tb_ad` WRITE;
/*!40000 ALTER TABLE `tb_ad` DISABLE KEYS */;
INSERT INTO `tb_ad` VALUES (1,'777777q','web_index_lb','2019-06-28 09:01:43','2019-12-06 07:29:59','1','img/banner1.jpg','://www.badu.com',NULL),(2,'444444qq','web_index_lb','2019-06-28 09:01:43','2019-12-06 07:29:59','1','img/banner2.jpg','://www.badu.com',NULL),(3,'sfsfsfsqqq','web_index_lb','2019-02-06 15:29:59','2019-02-23 15:30:02','1','img/banner3.jpg','img/banner3.jpg',NULL),(6,'wwwww','web_index_lb','2019-06-28 09:01:43','2019-12-06 07:29:59','1','img/banner4.jpg','://www.badu.com',NULL),(7,'qqqwwweee','web_index_lb','2019-06-28 09:01:43','2019-12-06 07:29:59','1','img/ad1.jpg','://www.badu.com',NULL);
/*!40000 ALTER TABLE `tb_ad` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-28 16:40:38
