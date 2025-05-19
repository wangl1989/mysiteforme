/*!40101 SET NAMES utf8 */;

/*
UPDATE SQL
*/


/**
  修改用户设备表，将user_id字段改为null
 */
ALTER TABLE sys_user_device MODIFY user_id bigint NULL COMMENT '用户设备ID';

/**
  修改访问日志表，将user_agent字段类型更改为TEXT
 */
ALTER TABLE analytics_visit_logs MODIFY user_agent TEXT NULL COMMENT '用户浏览器信息';
