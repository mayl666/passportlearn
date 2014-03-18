/*
SQLyog Ultimate v8.8
MySQL - 5.0.95 : Database - sogou_passport
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`sogou_passport` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `test_sogou_passport`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型,0-无密码',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=702 DEFAULT CHARSET=utf8;

/*Table structure for table `account_00` */

DROP TABLE IF EXISTS `account_00`;

CREATE TABLE `account_00` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_01` */

DROP TABLE IF EXISTS `account_01`;

CREATE TABLE `account_01` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_02` */

DROP TABLE IF EXISTS `account_02`;

CREATE TABLE `account_02` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_03` */

DROP TABLE IF EXISTS `account_03`;

CREATE TABLE `account_03` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_04` */

DROP TABLE IF EXISTS `account_04`;

CREATE TABLE `account_04` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_05` */

DROP TABLE IF EXISTS `account_05`;

CREATE TABLE `account_05` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_06` */

DROP TABLE IF EXISTS `account_06`;

CREATE TABLE `account_06` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_07` */

DROP TABLE IF EXISTS `account_07`;

CREATE TABLE `account_07` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_08` */

DROP TABLE IF EXISTS `account_08`;

CREATE TABLE `account_08` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_09` */

DROP TABLE IF EXISTS `account_09`;

CREATE TABLE `account_09` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_10` */

DROP TABLE IF EXISTS `account_10`;

CREATE TABLE `account_10` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_11` */

DROP TABLE IF EXISTS `account_11`;

CREATE TABLE `account_11` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `account_12` */

DROP TABLE IF EXISTS `account_12`;

CREATE TABLE `account_12` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_13` */

DROP TABLE IF EXISTS `account_13`;

CREATE TABLE `account_13` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_14` */

DROP TABLE IF EXISTS `account_14`;

CREATE TABLE `account_14` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_15` */

DROP TABLE IF EXISTS `account_15`;

CREATE TABLE `account_15` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_16` */

DROP TABLE IF EXISTS `account_16`;

CREATE TABLE `account_16` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `account_17` */

DROP TABLE IF EXISTS `account_17`;

CREATE TABLE `account_17` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `account_18` */

DROP TABLE IF EXISTS `account_18`;

CREATE TABLE `account_18` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_19` */

DROP TABLE IF EXISTS `account_19`;

CREATE TABLE `account_19` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_20` */

DROP TABLE IF EXISTS `account_20`;

CREATE TABLE `account_20` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `account_21` */

DROP TABLE IF EXISTS `account_21`;

CREATE TABLE `account_21` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_22` */

DROP TABLE IF EXISTS `account_22`;

CREATE TABLE `account_22` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_23` */

DROP TABLE IF EXISTS `account_23`;

CREATE TABLE `account_23` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_24` */

DROP TABLE IF EXISTS `account_24`;

CREATE TABLE `account_24` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `account_25` */

DROP TABLE IF EXISTS `account_25`;

CREATE TABLE `account_25` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_26` */

DROP TABLE IF EXISTS `account_26`;

CREATE TABLE `account_26` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_27` */

DROP TABLE IF EXISTS `account_27`;

CREATE TABLE `account_27` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_28` */

DROP TABLE IF EXISTS `account_28`;

CREATE TABLE `account_28` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_29` */

DROP TABLE IF EXISTS `account_29`;

CREATE TABLE `account_29` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_30` */

DROP TABLE IF EXISTS `account_30`;

CREATE TABLE `account_30` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_31` */

DROP TABLE IF EXISTS `account_31`;

CREATE TABLE `account_31` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_base_info` */

DROP TABLE IF EXISTS `account_base_info`;

CREATE TABLE `account_base_info` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `passport_id` (`passport_id`),
  KEY `uniqname` (`uniqname`)
) ENGINE=MyISAM AUTO_INCREMENT=463845 DEFAULT CHARSET=utf8;

/*Table structure for table `account_info` */

DROP TABLE IF EXISTS `account_info`;

CREATE TABLE `account_info` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '出生省份',
  `city` varchar(30) default NULL COMMENT '出生市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28033 DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_00` */

DROP TABLE IF EXISTS `account_info_00`;

CREATE TABLE `account_info_00` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_01` */

DROP TABLE IF EXISTS `account_info_01`;

CREATE TABLE `account_info_01` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_02` */

DROP TABLE IF EXISTS `account_info_02`;

CREATE TABLE `account_info_02` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_03` */

DROP TABLE IF EXISTS `account_info_03`;

CREATE TABLE `account_info_03` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_04` */

DROP TABLE IF EXISTS `account_info_04`;

CREATE TABLE `account_info_04` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_05` */

DROP TABLE IF EXISTS `account_info_05`;

CREATE TABLE `account_info_05` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_06` */

DROP TABLE IF EXISTS `account_info_06`;

CREATE TABLE `account_info_06` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_07` */

DROP TABLE IF EXISTS `account_info_07`;

CREATE TABLE `account_info_07` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_08` */

DROP TABLE IF EXISTS `account_info_08`;

CREATE TABLE `account_info_08` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_09` */

DROP TABLE IF EXISTS `account_info_09`;

CREATE TABLE `account_info_09` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_10` */

DROP TABLE IF EXISTS `account_info_10`;

CREATE TABLE `account_info_10` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_11` */

DROP TABLE IF EXISTS `account_info_11`;

CREATE TABLE `account_info_11` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_12` */

DROP TABLE IF EXISTS `account_info_12`;

CREATE TABLE `account_info_12` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_13` */

DROP TABLE IF EXISTS `account_info_13`;

CREATE TABLE `account_info_13` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_14` */

DROP TABLE IF EXISTS `account_info_14`;

CREATE TABLE `account_info_14` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_15` */

DROP TABLE IF EXISTS `account_info_15`;

CREATE TABLE `account_info_15` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_16` */

DROP TABLE IF EXISTS `account_info_16`;

CREATE TABLE `account_info_16` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_17` */

DROP TABLE IF EXISTS `account_info_17`;

CREATE TABLE `account_info_17` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_18` */

DROP TABLE IF EXISTS `account_info_18`;

CREATE TABLE `account_info_18` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_19` */

DROP TABLE IF EXISTS `account_info_19`;

CREATE TABLE `account_info_19` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_20` */

DROP TABLE IF EXISTS `account_info_20`;

CREATE TABLE `account_info_20` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_21` */

DROP TABLE IF EXISTS `account_info_21`;

CREATE TABLE `account_info_21` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_22` */

DROP TABLE IF EXISTS `account_info_22`;

CREATE TABLE `account_info_22` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_23` */

DROP TABLE IF EXISTS `account_info_23`;

CREATE TABLE `account_info_23` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_24` */

DROP TABLE IF EXISTS `account_info_24`;

CREATE TABLE `account_info_24` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_25` */

DROP TABLE IF EXISTS `account_info_25`;

CREATE TABLE `account_info_25` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_26` */

DROP TABLE IF EXISTS `account_info_26`;

CREATE TABLE `account_info_26` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_27` */

DROP TABLE IF EXISTS `account_info_27`;

CREATE TABLE `account_info_27` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_28` */

DROP TABLE IF EXISTS `account_info_28`;

CREATE TABLE `account_info_28` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_29` */

DROP TABLE IF EXISTS `account_info_29`;

CREATE TABLE `account_info_29` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_30` */

DROP TABLE IF EXISTS `account_info_30`;

CREATE TABLE `account_info_30` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_info_31` */

DROP TABLE IF EXISTS `account_info_31`;

CREATE TABLE `account_info_31` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `email` varchar(200) default NULL COMMENT '绑定邮件',
  `question` varchar(200) default NULL COMMENT '密保问题',
  `answer` varchar(200) default NULL COMMENT '密保答案',
  `birthday` date default NULL COMMENT '生日',
  `gender` char(1) default NULL COMMENT '性别',
  `province` varchar(30) default NULL COMMENT '省份',
  `city` varchar(30) default NULL COMMENT '市',
  `fullname` varchar(50) default NULL COMMENT '真实姓名',
  `personalid` varchar(50) default NULL COMMENT '身份证号',
  `modifyip` varchar(200) default NULL COMMENT '修改ip',
  `update_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `create_time` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `account_tmp` */

DROP TABLE IF EXISTS `account_tmp`;

CREATE TABLE `account_tmp` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL,
  `password` varchar(200) default NULL,
  `mobile` varchar(50) default NULL,
  `reg_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `reg_ip` varchar(200) default NULL,
  `flag` char(1) NOT NULL COMMENT '1-正式用户；2-登录账号未激活；3-锁定或封杀用户',
  `passwordtype` char(1) NOT NULL COMMENT '密码加密类型,0-无密码',
  `account_type` tinyint(5) NOT NULL COMMENT '1-email；2-phone;3-qq;4-sina;5-renren;6-taobao;7-baidu;',
  `uniqname` varchar(80) default NULL,
  `avatar` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_passportid` (`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=699 DEFAULT CHARSET=utf8;

/*Table structure for table `account_token` */

DROP TABLE IF EXISTS `account_token`;

CREATE TABLE `account_token` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport的用户唯一id，@形式',
  `access_token` varchar(200) NOT NULL COMMENT '访问token',
  `refresh_token` varchar(200) NOT NULL COMMENT 'access_token失效时换取access_token的token',
  `access_valid_time` bigint(20) NOT NULL COMMENT 'access_token过期时间点',
  `refresh_valid_time` bigint(20) NOT NULL COMMENT 'refresh_token过期时间点',
  `client_id` int(11) NOT NULL COMMENT '应用ID',
  `instance_id` varchar(50) default NULL COMMENT '客户端实例ID',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`client_id`,`instance_id`)
) ENGINE=MyISAM AUTO_INCREMENT=730 DEFAULT CHARSET=utf8;

/*Table structure for table `app_config` */

DROP TABLE IF EXISTS `app_config`;

CREATE TABLE `app_config` (
  `id` bigint(20) NOT NULL auto_increment,
  `client_id` int(11) NOT NULL,
  `sms_text` varchar(500) default NULL,
  `access_token_expiresin` int(11) default NULL COMMENT 'access_token的有效期，单位为秒',
  `refresh_token_expiresin` int(11) default NULL COMMENT 'refresh_token的有效期，单位为秒',
  `server_secret` varchar(200) default NULL COMMENT '服务端密钥',
  `client_secret` varchar(200) default NULL COMMENT '客户端密钥',
  `create_time` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '创建时间',
  `client_name` varchar(100) default NULL COMMENT '客户端名称',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `appid_unique` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `black_item` */

DROP TABLE IF EXISTS `black_item`;

CREATE TABLE `black_item` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(200) NOT NULL,
  `insert_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `duration_time` double NOT NULL,
  `insert_server` varchar(200) default NULL,
  `name_sort` int(11) NOT NULL,
  `limit_sort` int(11) NOT NULL,
  `scope` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `index_insert_time` (`insert_time`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

/*Table structure for table `clientid_level_mapping` */

DROP TABLE IF EXISTS `clientid_level_mapping`;

CREATE TABLE `clientid_level_mapping` (
  `id` int(50) NOT NULL auto_increment,
  `client_id` int(200) NOT NULL COMMENT '应用id',
  `level_info` tinyint(3) NOT NULL COMMENT '接口频次限制的等级',
  `interface_name` varchar(200) default NULL COMMENT '接口名称,为可按接口划分等级所预留的字段',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uniq_client_id` (`client_id`)
) ENGINE=MyISAM AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_config` */

DROP TABLE IF EXISTS `connect_config`;

CREATE TABLE `connect_config` (
  `id` bigint(20) NOT NULL auto_increment,
  `client_id` int(11) NOT NULL COMMENT 'passport的应用id',
  `provider` tinyint(5) default NULL COMMENT '第三方平台，3-qq;4-sina;5-renren;',
  `app_key` varchar(100) default NULL COMMENT '第三方appkey',
  `app_secret` varchar(255) default NULL COMMENT '第三方app密钥',
  `scope` varchar(1000) default NULL COMMENT '第三方授权的域',
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`client_id`,`provider`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `connect_relation` */

DROP TABLE IF EXISTS `connect_relation`;

CREATE TABLE `connect_relation` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_00` */

DROP TABLE IF EXISTS `connect_relation_00`;

CREATE TABLE `connect_relation_00` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_01` */

DROP TABLE IF EXISTS `connect_relation_01`;

CREATE TABLE `connect_relation_01` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_02` */

DROP TABLE IF EXISTS `connect_relation_02`;

CREATE TABLE `connect_relation_02` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_03` */

DROP TABLE IF EXISTS `connect_relation_03`;

CREATE TABLE `connect_relation_03` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_04` */

DROP TABLE IF EXISTS `connect_relation_04`;

CREATE TABLE `connect_relation_04` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_05` */

DROP TABLE IF EXISTS `connect_relation_05`;

CREATE TABLE `connect_relation_05` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_06` */

DROP TABLE IF EXISTS `connect_relation_06`;

CREATE TABLE `connect_relation_06` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_07` */

DROP TABLE IF EXISTS `connect_relation_07`;

CREATE TABLE `connect_relation_07` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_08` */

DROP TABLE IF EXISTS `connect_relation_08`;

CREATE TABLE `connect_relation_08` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_09` */

DROP TABLE IF EXISTS `connect_relation_09`;

CREATE TABLE `connect_relation_09` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_10` */

DROP TABLE IF EXISTS `connect_relation_10`;

CREATE TABLE `connect_relation_10` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_11` */

DROP TABLE IF EXISTS `connect_relation_11`;

CREATE TABLE `connect_relation_11` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_12` */

DROP TABLE IF EXISTS `connect_relation_12`;

CREATE TABLE `connect_relation_12` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_13` */

DROP TABLE IF EXISTS `connect_relation_13`;

CREATE TABLE `connect_relation_13` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_14` */

DROP TABLE IF EXISTS `connect_relation_14`;

CREATE TABLE `connect_relation_14` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_15` */

DROP TABLE IF EXISTS `connect_relation_15`;

CREATE TABLE `connect_relation_15` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_16` */

DROP TABLE IF EXISTS `connect_relation_16`;

CREATE TABLE `connect_relation_16` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_17` */

DROP TABLE IF EXISTS `connect_relation_17`;

CREATE TABLE `connect_relation_17` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_18` */

DROP TABLE IF EXISTS `connect_relation_18`;

CREATE TABLE `connect_relation_18` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_19` */

DROP TABLE IF EXISTS `connect_relation_19`;

CREATE TABLE `connect_relation_19` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_20` */

DROP TABLE IF EXISTS `connect_relation_20`;

CREATE TABLE `connect_relation_20` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_21` */

DROP TABLE IF EXISTS `connect_relation_21`;

CREATE TABLE `connect_relation_21` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_22` */

DROP TABLE IF EXISTS `connect_relation_22`;

CREATE TABLE `connect_relation_22` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_23` */

DROP TABLE IF EXISTS `connect_relation_23`;

CREATE TABLE `connect_relation_23` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_24` */

DROP TABLE IF EXISTS `connect_relation_24`;

CREATE TABLE `connect_relation_24` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_25` */

DROP TABLE IF EXISTS `connect_relation_25`;

CREATE TABLE `connect_relation_25` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_26` */

DROP TABLE IF EXISTS `connect_relation_26`;

CREATE TABLE `connect_relation_26` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_27` */

DROP TABLE IF EXISTS `connect_relation_27`;

CREATE TABLE `connect_relation_27` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_28` */

DROP TABLE IF EXISTS `connect_relation_28`;

CREATE TABLE `connect_relation_28` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_29` */

DROP TABLE IF EXISTS `connect_relation_29`;

CREATE TABLE `connect_relation_29` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_30` */

DROP TABLE IF EXISTS `connect_relation_30`;

CREATE TABLE `connect_relation_30` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_relation_31` */

DROP TABLE IF EXISTS `connect_relation_31`;

CREATE TABLE `connect_relation_31` (
  `id` bigint(20) NOT NULL auto_increment,
  `openid` varchar(255) NOT NULL COMMENT '第三方的用户id',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren, 6-taobao, 7-baidu',
  `passport_id` varchar(255) NOT NULL COMMENT '用户身份的全局唯一ID，包含@域名',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`openid`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token` */

DROP TABLE IF EXISTS `connect_token`;

CREATE TABLE `connect_token` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(50) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当前时间；）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_kie` (`provider`,`app_key`,`passport_id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_00` */

DROP TABLE IF EXISTS `connect_token_00`;

CREATE TABLE `connect_token_00` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_01` */

DROP TABLE IF EXISTS `connect_token_01`;

CREATE TABLE `connect_token_01` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_02` */

DROP TABLE IF EXISTS `connect_token_02`;

CREATE TABLE `connect_token_02` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_03` */

DROP TABLE IF EXISTS `connect_token_03`;

CREATE TABLE `connect_token_03` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_04` */

DROP TABLE IF EXISTS `connect_token_04`;

CREATE TABLE `connect_token_04` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_05` */

DROP TABLE IF EXISTS `connect_token_05`;

CREATE TABLE `connect_token_05` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_06` */

DROP TABLE IF EXISTS `connect_token_06`;

CREATE TABLE `connect_token_06` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_07` */

DROP TABLE IF EXISTS `connect_token_07`;

CREATE TABLE `connect_token_07` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_08` */

DROP TABLE IF EXISTS `connect_token_08`;

CREATE TABLE `connect_token_08` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_09` */

DROP TABLE IF EXISTS `connect_token_09`;

CREATE TABLE `connect_token_09` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_10` */

DROP TABLE IF EXISTS `connect_token_10`;

CREATE TABLE `connect_token_10` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_11` */

DROP TABLE IF EXISTS `connect_token_11`;

CREATE TABLE `connect_token_11` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_12` */

DROP TABLE IF EXISTS `connect_token_12`;

CREATE TABLE `connect_token_12` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_13` */

DROP TABLE IF EXISTS `connect_token_13`;

CREATE TABLE `connect_token_13` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_14` */

DROP TABLE IF EXISTS `connect_token_14`;

CREATE TABLE `connect_token_14` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_15` */

DROP TABLE IF EXISTS `connect_token_15`;

CREATE TABLE `connect_token_15` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_16` */

DROP TABLE IF EXISTS `connect_token_16`;

CREATE TABLE `connect_token_16` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_17` */

DROP TABLE IF EXISTS `connect_token_17`;

CREATE TABLE `connect_token_17` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_18` */

DROP TABLE IF EXISTS `connect_token_18`;

CREATE TABLE `connect_token_18` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_19` */

DROP TABLE IF EXISTS `connect_token_19`;

CREATE TABLE `connect_token_19` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_20` */

DROP TABLE IF EXISTS `connect_token_20`;

CREATE TABLE `connect_token_20` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_21` */

DROP TABLE IF EXISTS `connect_token_21`;

CREATE TABLE `connect_token_21` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_22` */

DROP TABLE IF EXISTS `connect_token_22`;

CREATE TABLE `connect_token_22` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_23` */

DROP TABLE IF EXISTS `connect_token_23`;

CREATE TABLE `connect_token_23` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_24` */

DROP TABLE IF EXISTS `connect_token_24`;

CREATE TABLE `connect_token_24` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_25` */

DROP TABLE IF EXISTS `connect_token_25`;

CREATE TABLE `connect_token_25` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_26` */

DROP TABLE IF EXISTS `connect_token_26`;

CREATE TABLE `connect_token_26` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_27` */

DROP TABLE IF EXISTS `connect_token_27`;

CREATE TABLE `connect_token_27` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_28` */

DROP TABLE IF EXISTS `connect_token_28`;

CREATE TABLE `connect_token_28` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_29` */

DROP TABLE IF EXISTS `connect_token_29`;

CREATE TABLE `connect_token_29` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_30` */

DROP TABLE IF EXISTS `connect_token_30`;

CREATE TABLE `connect_token_30` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `connect_token_31` */

DROP TABLE IF EXISTS `connect_token_31`;

CREATE TABLE `connect_token_31` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) NOT NULL COMMENT 'passport用户身份的全局唯一ID，包含@域名 ',
  `provider` tinyint(5) NOT NULL COMMENT '第三方平台，3-qq，4-sina，5-renren ',
  `app_key` varchar(100) NOT NULL COMMENT '第三方appkey',
  `openid` varchar(255) NOT NULL COMMENT '第三方用户id',
  `access_token` varchar(255) NOT NULL COMMENT '第三方access_token',
  `expires_in` bigint(20) default NULL COMMENT '第三方access_token的有效期',
  `refresh_token` varchar(255) default NULL COMMENT '第三方refresh_token',
  `connect_uniqname` varchar(200) default NULL COMMENT '第三方昵称',
  `avatar_small` varchar(255) default NULL COMMENT '第三方头像（小图）',
  `avatar_middle` varchar(255) default NULL COMMENT '第三方头像（中图）',
  `avatar_large` varchar(255) default NULL COMMENT '第三方头像（大图）',
  `gender` char(1) default NULL COMMENT '性别。 0-女，1-男，默认为1',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '修改时间（当新创建时，为系统当时时间）',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_key` (`passport_id`,`provider`,`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `interface_level_mapping` */

DROP TABLE IF EXISTS `interface_level_mapping`;

CREATE TABLE `interface_level_mapping` (
  `id` int(50) NOT NULL auto_increment,
  `interface_name` varchar(200) NOT NULL COMMENT '接口名称',
  `primary_level` tinyint(3) NOT NULL COMMENT '初级，值为0',
  `primary_level_count` bigint(20) NOT NULL COMMENT '初级对应的接口频次',
  `middle_level` tinyint(3) NOT NULL COMMENT '中级，值为1',
  `middle_level_count` bigint(20) NOT NULL COMMENT '中级对应的接口频次',
  `high_level` tinyint(3) NOT NULL COMMENT '高级，值为2',
  `high_level_count` bigint(20) NOT NULL COMMENT '高级对应的接口频次',
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uniq_interface_name` (`interface_name`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;

/*Table structure for table `mobile_passportid_mapping` */

DROP TABLE IF EXISTS `mobile_passportid_mapping`;

CREATE TABLE `mobile_passportid_mapping` (
  `id` bigint(20) NOT NULL auto_increment,
  `mobile` varchar(50) NOT NULL,
  `passport_id` varchar(200) NOT NULL,
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8;

/*Table structure for table `problem` */

DROP TABLE IF EXISTS `problem`;

CREATE TABLE `problem` (
  `id` bigint(20) NOT NULL auto_increment,
  `passport_id` varchar(200) default NULL,
  `client_id` int(11) default NULL COMMENT '应用ID',
  `sub_time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `status` tinyint(5) NOT NULL COMMENT '0-未回复；1-已回复；2-已关闭',
  `type_id` bigint(20) NOT NULL COMMENT '问题类型ID',
  `title` varchar(100) NOT NULL,
  `content` varchar(300) NOT NULL,
  `email` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `index_problem_subtime` (`sub_time`)
) ENGINE=MyISAM AUTO_INCREMENT=257 DEFAULT CHARSET=utf8;

/*Table structure for table `problem_answer` */

DROP TABLE IF EXISTS `problem_answer`;

CREATE TABLE `problem_answer` (
  `id` bigint(20) NOT NULL auto_increment,
  `problem_id` bigint(20) NOT NULL,
  `ans_passport_id` varchar(200) NOT NULL,
  `ans_content` varchar(300) NOT NULL,
  `ans_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=298 DEFAULT CHARSET=utf8;

/*Table structure for table `problem_type` */

DROP TABLE IF EXISTS `problem_type`;

CREATE TABLE `problem_type` (
  `id` bigint(20) NOT NULL auto_increment,
  `type_name` varchar(200) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=283 DEFAULT CHARSET=utf8;

/*Table structure for table `sname_passportid_mapping` */

DROP TABLE IF EXISTS `sname_passportid_mapping`;

CREATE TABLE `sname_passportid_mapping` (
  `id` bigint(20) NOT NULL auto_increment,
  `sid` bigint(20) NOT NULL,
  `sname` varchar(100) NOT NULL,
  `passport_id` varchar(200) NOT NULL,
  `mobile` varchar(50) default NULL,
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_sname` (`sname`),
  UNIQUE KEY `unique_sid` (`sid`),
  KEY `unique_passportid` (`passport_id`),
  KEY `mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=796843 DEFAULT CHARSET=utf8;

/*Table structure for table `test` */

DROP TABLE IF EXISTS `test`;

CREATE TABLE `test` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(200) NOT NULL,
  `gender` varchar(200) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `uniqname_passportid_mapping` */

DROP TABLE IF EXISTS `uniqname_passportid_mapping`;

CREATE TABLE `uniqname_passportid_mapping` (
  `id` bigint(20) NOT NULL auto_increment,
  `uniqname` varchar(50) NOT NULL,
  `passport_id` varchar(255) NOT NULL,
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`uniqname`),
  UNIQUE KEY `uniq_passportid` (`passport_id`)
) ENGINE=MyISAM AUTO_INCREMENT=410913 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
