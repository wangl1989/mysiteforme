/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50713
Source Host           : localhost:3306
Source Database       : mysiteforme

Target Server Type    : MYSQL
Target Server Version : 50713
File Encoding         : 65001

Date: 2017-12-04 08:16:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '编号',
  `value` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '数据值',
  `label` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '标签名',
  `type` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '类型',
  `description` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '描述',
  `sort` decimal(10,0) NOT NULL COMMENT '排序（升序）',
  `parent_id` varchar(64) COLLATE utf8_bin DEFAULT '0' COMMENT '父级编号',
  `create_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `sys_dict_value` (`value`),
  KEY `sys_dict_label` (`label`),
  KEY `sys_dict_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='字典表';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------

-- ----------------------------
-- Table structure for sys_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_group`;
CREATE TABLE `sys_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL COMMENT '分组名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父分组ID',
  `level` bigint(2) DEFAULT NULL,
  `parent_ids` varchar(2000) DEFAULT NULL COMMENT '分组序列号',
  `sort` smallint(6) DEFAULT NULL COMMENT '分组排序值',
  `create_by` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `del_flag` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_group
-- ----------------------------

-- ----------------------------
-- Table structure for sys_group_ur
-- ----------------------------
DROP TABLE IF EXISTS `sys_group_ur`;
CREATE TABLE `sys_group_ur` (
  `group_id` bigint(20) NOT NULL COMMENT '分组ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_group_ur
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单',
  `level` bigint(2) DEFAULT NULL COMMENT '菜单层级',
  `parent_ids` varchar(2000) DEFAULT NULL COMMENT '父菜单联集',
  `sort` smallint(6) DEFAULT NULL COMMENT '排序',
  `href` varchar(2000) DEFAULT NULL COMMENT '链接地址',
  `target` varchar(20) DEFAULT NULL COMMENT '打开方式',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `is_show` tinyint(2) DEFAULT NULL COMMENT '是否显示',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识',
  `create_by` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `del_flag` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '资源管理', null, '1', '1,', '30', null, null, null, '1', 'sys:rescourse:show', '1', '2017-11-02 14:20:38', '1', '2017-11-02 14:20:44', null, '0');
INSERT INTO `sys_menu` VALUES ('2', '新增资源管理', '1', '2', '1,2,', '30', null, null, null, '1', null, '1', '2017-12-02 15:43:09', '1', '2017-12-02 15:43:12', null, '0');
INSERT INTO `sys_menu` VALUES ('3', '测试资源管理', '2', '3', '1,2,3,', '30', '2', null, null, '1', null, '1', '2017-12-02 15:45:14', '1', '2017-12-02 15:45:18', null, '0');
INSERT INTO `sys_menu` VALUES ('4', '非常长的文字资源管理', '3', '4', '1,2,3,4', '30', null, null, null, '1', null, '1', '2017-12-02 18:44:10', null, null, null, '0');
INSERT INTO `sys_menu` VALUES ('5', '我的新资源', '2', '3', '1,2,5,', '40', null, null, null, '1', null, null, null, null, null, null, '0');
INSERT INTO `sys_menu` VALUES ('6', '系统管理', null, '1', '6,', '60', null, null, null, '1', null, null, null, null, null, null, '0');
INSERT INTO `sys_menu` VALUES ('7', '文章管理', null, '1', '7,', '20', null, null, null, '1', null, null, null, null, null, null, '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL COMMENT '角色名称',
  `create_date` datetime DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `del_flag` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '老司机', '2017-11-02 14:19:07', '1', '2017-12-03 05:00:01', '1', '', '0');
INSERT INTO `sys_role` VALUES ('2', '系统管理员', '2017-11-29 19:36:37', '1', '2017-12-03 05:03:47', '1', '', '0');
INSERT INTO `sys_role` VALUES ('3', '玩玩', '2017-12-02 23:12:29', '1', '2017-12-03 04:32:01', '8', '喂喂喂1', '0');
INSERT INTO `sys_role` VALUES ('4', '的', '2017-12-02 23:17:35', '1', '2017-12-03 00:16:23', '1', '2', '1');
INSERT INTO `sys_role` VALUES ('5', '的发哥', '2017-12-02 23:22:40', '1', '2017-12-03 00:16:23', '1', '塞尔风格', '1');
INSERT INTO `sys_role` VALUES ('6', '23', '2017-12-02 23:26:10', '1', '2017-12-03 00:11:02', '1', '24', '1');
INSERT INTO `sys_role` VALUES ('7', '打的', '2017-12-02 23:29:57', '1', '2017-12-02 23:59:55', '1', '单打独斗', '1');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL,
  `menu_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1');
INSERT INTO `sys_role_menu` VALUES ('1', '2');
INSERT INTO `sys_role_menu` VALUES ('1', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '2');
INSERT INTO `sys_role_menu` VALUES ('2', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '4');
INSERT INTO `sys_role_menu` VALUES ('2', '6');
INSERT INTO `sys_role_menu` VALUES ('2', '7');
INSERT INTO `sys_role_menu` VALUES ('3', '6');
INSERT INTO `sys_role_menu` VALUES ('3', '7');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `login_name` varchar(36) DEFAULT NULL COMMENT '登录名',
  `nick_name` varchar(40) DEFAULT NULL COMMENT '昵称',
  `icon` varchar(2000) DEFAULT NULL,
  `password` varchar(40) DEFAULT NULL COMMENT '密码',
  `salt` varchar(40) DEFAULT NULL COMMENT 'shiro加密盐',
  `tel` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱地址',
  `locked` tinyint(2) DEFAULT NULL COMMENT '是否锁定',
  `create_date` datetime DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `del_flag` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'test', '我是管理员', null, 'dff3217c232572c118d3cef8f8c4cc446bb94ae3', '8a474a64b5c5d748', '13776055179', 'b@qq.com', '0', '2017-11-27 22:19:39', '1', '2017-12-03 03:27:51', '1', null, '0');
INSERT INTO `sys_user` VALUES ('6', 'w', '小W', null, '0803f1c603d94ccc8c20447e393de744ef9ebfb3', '09f625b1f33d2dcb', '', '945293033@qq.com', '0', '2017-11-29 20:32:19', '1', null, null, null, '0');
INSERT INTO `sys_user` VALUES ('7', 'a', '', null, '9552f1e4e2e863a6c8c38e63e87c09b099d01e90', 'dfa86ad5b1bd973a', '', '', '0', '2017-11-29 20:32:34', '1', null, null, null, '1');
INSERT INTO `sys_user` VALUES ('8', 'd', '2', null, '0f8a8fadd02ff7aada1e550c711dc2b9d5092313', '9d5f81fed123b09b', '', '', '0', '2017-11-29 21:01:45', '1', '2017-12-01 20:10:37', '1', null, '0');
INSERT INTO `sys_user` VALUES ('10', 'mW', '3', null, 'd75ac6d6d1936643defdce46745d5956fabdb93f', '7f0ba21eded77ee6', '', '', '0', '2017-12-01 01:46:47', '1', '2017-12-01 20:06:37', '1', null, '0');
INSERT INTO `sys_user` VALUES ('11', '222', '23333', null, '31c2b2ae3980ac5546e898be21edc9d5a2219e11', '1cd65c68ea50de33', '13776055179', '2@qq.com', '0', '2017-11-30 00:38:37', '1', '2017-12-01 20:06:28', '1', null, '1');
INSERT INTO `sys_user` VALUES ('13', 'www', '', null, '8fe0e78265b271421353fedb97bf1fb5feb667ca', '7c4e7d3b6930f472', '', '', '0', '2017-12-01 20:10:49', '1', '2017-12-01 21:23:32', '1', null, '1');
INSERT INTO `sys_user` VALUES ('14', 'h', '', null, 'a1088652594fa274248cac47e2d440dacd95ba93', '5a678b15abcdee20', '', '', '0', '2017-12-01 20:11:14', '1', '2017-12-01 21:23:59', '1', null, '1');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('1', '2');
INSERT INTO `sys_user_role` VALUES ('6', '1');
INSERT INTO `sys_user_role` VALUES ('7', '1');
INSERT INTO `sys_user_role` VALUES ('8', '1');
INSERT INTO `sys_user_role` VALUES ('8', '2');
INSERT INTO `sys_user_role` VALUES ('10', '1');
INSERT INTO `sys_user_role` VALUES ('11', '1');
INSERT INTO `sys_user_role` VALUES ('13', '1');
INSERT INTO `sys_user_role` VALUES ('13', '2');
INSERT INTO `sys_user_role` VALUES ('14', '1');
