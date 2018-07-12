/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50713
Source Host           : 127.0.0.1:3306
Source Database       : mysiteforme_server

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2018-07-12 17:45:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for upload_info
-- ----------------------------
DROP TABLE IF EXISTS `upload_info`;
CREATE TABLE `upload_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `local_window_url` varchar(255) DEFAULT NULL COMMENT '本地window系统上传路径,input,YES,false,true,false',
  `local_linux_url` varchar(255) DEFAULT NULL COMMENT '本地LINUX系统上传路径,input,YES,false,true,false',
  `qiniu_base_path` varchar(255) DEFAULT NULL COMMENT '七牛前缀路径,input,YES,false,true,false',
  `qiniu_bucket_name` varchar(255) DEFAULT NULL COMMENT '七牛bucket的目录名称,input,YES,false,true,false',
  `qiniu_dir` varchar(255) DEFAULT NULL COMMENT '七牛文件存储目录,input,YES,false,true,false',
  `qiniu_access_key` varchar(255) DEFAULT NULL COMMENT '七牛qiniuAccess值,input,YES,false,true,false',
  `qiniu_secret_key` varchar(255) DEFAULT NULL COMMENT '七牛qiniuKey的值,input,YES,false,true,false',
  `qiniu_test_access` bit(1) DEFAULT NULL COMMENT '七牛上传测试,switch,YES,true,true,false',
  `oss_base_path` varchar(255) DEFAULT NULL COMMENT '阿里云前缀路径,input,YES,false,true,false',
  `oss_bucket_name` varchar(255) DEFAULT NULL COMMENT '阿里云bucket的目录名称,input,YES,false,true,false',
  `oss_dir` varchar(255) DEFAULT NULL COMMENT '阿里云文件上传目录,input,YES,false,true,false',
  `oss_key_id` varchar(255) DEFAULT NULL COMMENT '阿里云ACCESS_KEY_ID值,input,YES,false,true,false',
  `oss_key_secret` varchar(255) DEFAULT NULL COMMENT '阿里云ACCESS_KEY_SECRET,input,YES,false,true,false',
  `oss_endpoint` varchar(255) DEFAULT NULL COMMENT '阿里云ENDPOINT值,input,YES,false,true,false',
  `oss_test_access` bit(1) DEFAULT NULL COMMENT '阿里云上传测试,switch,YES,true,true,false',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='文件上传配置,1,switch-qiniuTestAccess-YES-true-qiniu_test_access,switch-ossTestAccess-YES-true-oss_test_access';
