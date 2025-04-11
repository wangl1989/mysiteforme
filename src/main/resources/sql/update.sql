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
    update_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remarks varchar(255) DEFAULT NULL COMMENT '分组描述',
    del_flag tinyint DEFAULT NULL COMMENT '删除标志'
);

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(40) DEFAULT NULL COMMENT '权限名称',
  `permission_code` varchar(2000) DEFAULT NULL COMMENT '权限编码',
  `permission_type` INT(20) DEFAULT NULL COMMENT '权限类型',
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
    common tinyint DEFAULT 0 COMMENT '是否为公共接口，默认false',
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

-- ----------------------------
-- Table structure for user_device
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_device`;
CREATE TABLE `sys_user_device` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_name` VARCHAR(50) NOT NULL COMMENT '用户ID',
    `user_agent` TEXT NOT NULL COMMENT '用户ID',
    `device_id` VARCHAR(100) NOT NULL COMMENT '设备ID',
    `device_type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
    `device_model` VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
    `os_version` TEXT DEFAULT NULL COMMENT '操作系统版本',
    `browser_info` TEXT DEFAULT NULL COMMENT '浏览器信息',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_location` VARCHAR(100) DEFAULT NULL COMMENT '最后登录位置',
    `device_fingerprint` TEXT DEFAULT NULL COMMENT '设备指纹',
    `login_out_date` datetime DEFAULT NULL COMMENT '登出时间',
    `create_by` bigint DEFAULT NULL COMMENT '创建者',
    `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新者',
    `update_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
    `del_flag` tinyint DEFAULT NULL COMMENT '删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';


ALTER TABLE sys_user ADD COLUMN color VARCHAR(200) AFTER icon COMMENT '图标颜色';
ALTER TABLE sys_user ADD COLUMN path VARCHAR(200) AFTER permission COMMENT '前端路由地址';
ALTER TABLE sys_user ADD COLUMN component VARCHAR(200) AFTER path COMMENT '前端组件地址';
ALTER TABLE sys_user ADD COLUMN title VARCHAR(200) AFTER component COMMENT '菜单标题';
ALTER TABLE sys_user ADD COLUMN show_badge tinyint DEFAULT 0 AFTER title COMMENT '是否显示徽标（菜单右侧的红色小圆点）';
ALTER TABLE sys_user ADD COLUMN show_text_badge VARCHAR(200) AFTER show_badge COMMENT '是否显示文字徽标（菜单右侧的红色文字标签）';
ALTER TABLE sys_user ADD COLUMN is_hide tinyint AFTER show_text_badge COMMENT '是否在菜单中隐藏（在左侧菜单栏中不显示）';
ALTER TABLE sys_user ADD COLUMN is_hide_tab tinyint AFTER is_hide COMMENT '是否在标签页中隐藏 （在顶部标签栏中不显示）';
ALTER TABLE sys_user ADD COLUMN link varchar(2000) AFTER is_hide_tab COMMENT '外链链接地址';
ALTER TABLE sys_user ADD COLUMN is_iframe tinyint AFTER link COMMENT '是否为iframe';
ALTER TABLE sys_user ADD COLUMN keep_alive tinyint AFTER is_iframe COMMENT '是否缓存';
ALTER TABLE sys_user ADD COLUMN is_in_main_container tinyint AFTER keep_alive COMMENT '是否在主容器中';




