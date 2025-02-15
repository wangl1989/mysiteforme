/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80041
Source Host           : localhost:3306
Source Database       : mysiteforme

Target Server Type    : MYSQL
Target Server Version : 80041
File Encoding         : 65001

Date: 2025-02-14 00:23:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission_group`;
CREATE TABLE sys_permission_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_code VARCHAR(50) NOT NULL COMMENT '分组编码',
    group_name VARCHAR(100) NOT NULL COMMENT '分组名称',
    parent_id BIGINT COMMENT '父分组ID',
    parent_ids varchar(2000) DEFAULT NULL COMMENT '父菜单联集',
    sort INT DEFAULT 0 COMMENT '排序号',
    `level` INT DEFAULT 0 COMMENT '层级',
    create_by bigint DEFAULT NULL COMMENT '创建者',
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by bigint DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remarks varchar(255) DEFAULT NULL COMMENT '分组描述',
    del_flag tinyint DEFAULT NULL COMMENT '删除标志'
);

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(40) DEFAULT NULL COMMENT '权限名称',
  `permission_code` varchar(2000) DEFAULT NULL COMMENT '权限编码',
  `permission_type` varchar(2000) DEFAULT NULL COMMENT '权限类型',
  `group_id` BIGINT COMMENT '权限分组ID',
  `icon` varchar(100) DEFAULT NULL COMMENT '权限图标',
  `sort` smallint DEFAULT NULL COMMENT '排序',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '权限备注',
  `del_flag` tinyint DEFAULT NULL COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB 

DROP TABLE IF EXISTS `sys_permission_page`;
CREATE TABLE sys_permission_page (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_id BIGINT NOT NULL COMMENT '关联权限表ID',
    page_url VARCHAR(200) NOT NULL COMMENT '页面URL',
    `sort` smallint DEFAULT NULL COMMENT '排序',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remarks` varchar(255) DEFAULT NULL COMMENT '页面备注',
    `del_flag` tinyint DEFAULT NULL COMMENT '删除标志'
);

DROP TABLE IF EXISTS `sys_permission_button`;
CREATE TABLE sys_permission_button (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_id BIGINT NOT NULL COMMENT '关联权限表ID',
    button_key VARCHAR(50) NOT NULL COMMENT '按钮标识',
    button_name VARCHAR(50) NOT NULL COMMENT '按钮名称',
    `sort` smallint DEFAULT NULL COMMENT '排序',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remarks` varchar(255) DEFAULT NULL COMMENT '按钮备注',
    `del_flag` tinyint DEFAULT NULL COMMENT '删除标志'
);

DROP TABLE IF EXISTS `sys_permission_api`;
CREATE TABLE sys_permission_api (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_id BIGINT NOT NULL COMMENT '关联权限表ID',
    api_url VARCHAR(200) NOT NULL COMMENT 'API接口URL',
    http_method VARCHAR(10) COMMENT 'HTTP请求方法',
    `sort` smallint DEFAULT NULL COMMENT '排序',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remarks` varchar(255) DEFAULT NULL COMMENT 'API备注',
    `del_flag` tinyint DEFAULT NULL COMMENT '删除标志'
);

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_permission_id (permission_id)
);

DROP TABLE IF EXISTS `sys_user_permission`;
CREATE TABLE sys_user_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
    UNIQUE KEY uk_user_permission (user_id, permission_id),
    KEY idx_permission_id (permission_id)
);

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `login_name` varchar(36) DEFAULT NULL COMMENT '登录名',
    `nick_name` varchar(40) DEFAULT NULL COMMENT '昵称',
    `icon` varchar(2000) DEFAULT NULL,
    `password` varchar(40) DEFAULT NULL COMMENT '密码',
    `salt` varchar(40) DEFAULT NULL COMMENT 'shiro加密盐',
    `tel` varchar(11) DEFAULT NULL COMMENT '手机号码',
    `email` varchar(200) DEFAULT NULL COMMENT '邮箱地址',
    `locked` tinyint DEFAULT NULL COMMENT '是否锁定',
    `create_date` datetime DEFAULT NULL,
    `create_by` bigint DEFAULT NULL,
    `update_date` datetime DEFAULT NULL,
    `update_by` bigint DEFAULT NULL,
    `remarks` varchar(255) DEFAULT NULL,
    `del_flag` tinyint DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(40) DEFAULT NULL COMMENT '角色名称',
    `create_date` datetime DEFAULT NULL,
    `create_by` bigint DEFAULT NULL,
    `update_date` datetime DEFAULT NULL,
    `update_by` bigint DEFAULT NULL,
    `remarks` varchar(255) DEFAULT NULL,
    `del_flag` tinyint DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB
