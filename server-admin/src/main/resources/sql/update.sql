/*
UPDATE SQL
*/


/**
  修改用户设备表，将user_id字段改为null
 */
ALTER TABLE sys_user_device MODIFY user_id bigint NULL;
