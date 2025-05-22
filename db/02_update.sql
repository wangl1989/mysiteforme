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

/**
  插入AI聊天API数据
 */
INSERT INTO `mysiteforme`.`sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `menu_id`, `icon`, `color`, `sort`, `create_by`, `create_date`, `update_by`, `update_date`, `remarks`, `del_flag`) VALUES ('178', 'AI聊天API', 'ai:chat:api', '3', '2', '', NULL, '13', '1', '2025-05-22 07:04:20', '1', '2025-05-22 07:04:20', '', '0');
INSERT INTO `mysiteforme`.`sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `menu_id`, `icon`, `color`, `sort`, `create_by`, `create_date`, `update_by`, `update_date`, `remarks`, `del_flag`) VALUES ('179', '获取AI聊天历史记录API', 'ai:history:api', '3', '2', '', NULL, '14', '1', '2025-05-22 07:05:05', '1', '2025-05-22 07:05:05', '', '0');

INSERT INTO `mysiteforme`.`sys_permission_api` (`id`, `permission_id`, `api_url`, `http_method`, `common`, `sort`, `create_by`, `create_date`, `update_by`, `update_date`, `remarks`, `del_flag`) VALUES ('94', '178', '/api/admin/ai/chat', 'POST', '1', '13', '1', '2025-05-22 07:04:20', '1', '2025-05-22 07:04:20', '', '0');
INSERT INTO `mysiteforme`.`sys_permission_api` (`id`, `permission_id`, `api_url`, `http_method`, `common`, `sort`, `create_by`, `create_date`, `update_by`, `update_date`, `remarks`, `del_flag`) VALUES ('95', '179', '/api/admin/ai/getHistoryList', 'GET', '1', '14', '1', '2025-05-22 07:05:05', '1', '2025-05-22 07:05:05', '', '0');

