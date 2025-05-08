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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

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


ALTER TABLE sys_menu ADD COLUMN color VARCHAR(200) COMMENT '图标颜色' AFTER icon;
ALTER TABLE sys_menu ADD COLUMN path VARCHAR(200) COMMENT '前端路由地址' AFTER permission;
ALTER TABLE sys_menu ADD COLUMN component VARCHAR(200) COMMENT '前端组件地址' AFTER path;
ALTER TABLE sys_menu ADD COLUMN title VARCHAR(200) COMMENT '菜单标题' AFTER component;
ALTER TABLE sys_menu ADD COLUMN show_badge tinyint DEFAULT 0 COMMENT '是否显示徽标（菜单右侧的红色小圆点）'AFTER title;
ALTER TABLE sys_menu ADD COLUMN show_text_badge VARCHAR(200) COMMENT '是否显示文字徽标（菜单右侧的红色文字标签）' AFTER show_badge;
ALTER TABLE sys_menu ADD COLUMN is_hide tinyint COMMENT '是否在菜单中隐藏（在左侧菜单栏中不显示）' AFTER show_text_badge;
ALTER TABLE sys_menu ADD COLUMN is_hide_tab tinyint COMMENT '是否在标签页中隐藏 （在顶部标签栏中不显示）' AFTER is_hide;
ALTER TABLE sys_menu ADD COLUMN link varchar(2000) COMMENT '外链链接地址' AFTER is_hide_tab;
ALTER TABLE sys_menu ADD COLUMN is_iframe tinyint COMMENT '是否为iframe' AFTER link;
ALTER TABLE sys_menu ADD COLUMN keep_alive tinyint COMMENT '是否缓存' AFTER is_iframe;
ALTER TABLE sys_menu ADD COLUMN is_in_main_container tinyint COMMENT '是否在主容器中' AFTER keep_alive;


ALTER TABLE quartz_task ADD COLUMN group_name varchar(200) COMMENT '任务组名称' AFTER `cron`;
ALTER TABLE quartz_task_log ADD COLUMN cron varchar(200) COMMENT '任务表达式' AFTER `name`;
ALTER TABLE quartz_task_log ADD COLUMN group_name varchar(200) COMMENT '任务组名称' AFTER `cron`;

ALTER TABLE sys_role ADD COLUMN `is_default` bit(1) DEFAULT 0 COMMENT '是否默认角色' AFTER `name`;

ALTER TABLE sys_site ADD COLUMN `web_service_key` varchar(200) COMMENT 'webservicekey' AFTER `logo`;

ALTER TABLE sys_user ADD COLUMN `location` varchar(200) COMMENT '位置信息' AFTER `locked`;

create table analytics_click_events
(
    id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT  comment '用户ID，未登录用户为NULL',
    login_name    varchar(200) comment '登录账号',
    nick_name     varchar(200) comment '用户昵称',
    device_id     varchar(100) not null comment '设备ID',
    page_url      varchar(200) comment '当前页面地址',
    element_id    varchar(255) comment '点击元素ID',
    element_class varchar(255) comment '点击元素类名',
    element_text  varchar(255) comment '元素文字内容',
    element_path  varchar(255) comment '元素的DOM路径',
    element_href  varchar(255) comment '元素链接路径',
    event_type    varchar(255) comment '元素类型(button/link/image等)',
    click_time    timestamp    not null comment '访问时间',
    create_date   datetime DEFAULT CURRENT_TIMESTAMP    comment '创建时间',
    create_by     bigint       comment '创建人',
    update_date   datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    comment '修改时间',
    update_by     bigint       comment '修改人',
    remarks       varchar(255) comment '备注',
    del_flag      bit          comment '删除标记'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点击事件表';

create index idx_device_id
    on analytics_click_events (device_id);

create index idx_user_id
    on analytics_click_events (user_id);

create table analytics_daily_stats
(
    id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    stat_date          date         comment '统计日期',
    total_visits       int          comment '总访问量',
    unique_visitors    int          comment '独立访客数',
    new_users          int          comment '新用户',
    total_clicks       int          comment '总点击量',
    bounce_rate        decimal(5)   comment '跳出率',
    avg_visit_duration int          comment '平均访问时长(秒)',
    create_date        datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    create_by          bigint       comment '创建人',
    update_date        datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    update_by          bigint       comment '修改人',
    remarks            varchar(255) comment '备注',
    del_flag           bit          comment '删除标记'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计汇总表(按天)';

create index idx_stat_date
    on analytics_daily_stats (stat_date);


create table analytics_visit_logs
(
    id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      bigint       comment '用户ID，未登录用户为NULL',
    login_name   varchar(200) comment '登录账号',
    nick_name    varchar(200) comment '用户昵称',
    device_id    varchar(100) not null comment '设备ID',
    ip_address   varchar(100) comment 'IP地址',
    user_agent   varchar(255) comment '浏览器UA信息',
    referrer     varchar(255) comment '来源页面',
    entry_page   varchar(255) comment '当前页面',
    visit_time   timestamp    not null comment '访问时间',
    title        varchar(255) comment '页面标题',
    screen_size  varchar(255) comment '当前页面长宽比',
    time_on_page int          comment '上一个页面停留时间(毫秒数)',
    language     varchar(200) comment '当前页面的语言',
    country      varchar(100) comment '国家',
    region       varchar(100) comment '地区',
    city         varchar(100) comment '城市',
    province     varchar(100) comment '省',
    os           varchar(20)  comment '系统类型',
    browser      varchar(255) comment '浏览器',
    create_date  datetime DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    create_by    bigint       comment '创建人',
    update_date  datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  comment '修改时间',
    update_by    bigint       comment '修改人',
    remarks      varchar(255) comment '备注',
    del_flag     bit          comment '删除标记'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问记录表';

create index idx_user_id
    on analytics_visit_logs (user_id);
