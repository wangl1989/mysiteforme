/**
 * @ Author: wangl
 * @ Create Time: 2025-02-13 13:00:52
 * @ Modified by: wangl
 * @ Modified time: 2025-02-18 11:20:31
 * @ Description: 消息常量类 用于存储消息的常量
 */

package com.mysiteforme.admin.util;

public class MessageConstants {
    public static final String SUCCESS = "message.success";
    public static final String OBJECT_NOT_NULL = "message.object.not.null";

    public static class SysLog{
        // 站点相关
        public static final String SITE_EDIT = "message.log.site.edit";

        // 用户相关
        public static final String USER_ADD = "message.log.user.add";
        public static final String USER_EDIT = "message.log.user.edit";
        public static final String USER_DELETE = "message.log.user.delete";
        public static final String USER_CHANGE_PASSWORD = "message.log.user.change.password";

        // 角色相关
        public static final String ROLE_ADD = "message.log.role.add";
        public static final String ROLE_EDIT = "message.log.role.edit";
        public static final String ROLE_DELETE = "message.log.role.delete";

        // 上传相关
        public static final String FILE_UPLOAD = "message.log.file.upload";
        public static final String FILE_UPLOAD_BASE64 = "message.log.file.upload.base64";
        public static final String FILE_UPLOAD_WANG = "message.log.file.upload.wang";
        public static final String FILE_UPLOAD_DO_CONTENT = "message.log.file.upload.do.content";
        public static final String FILE_DOWNLOAD = "message.log.file.download";

        // 字典相关
        public static final String DICT_DELETE = "message.log.dict.delete";
        public static final String DICT_ADD = "message.log.dict.add";
        public static final String DICT_UPDATE = "message.log.dict.update";
        public static final String DICT_UPDATE_TYPE = "message.log.dict.update.type";

        // 日志相关
        public static final String LOG_BATCH_DELETE = "message.log.log.batch.delete";

        // 菜单相关
        public static final String MENU_DELETE = "message.log.menu.delete";
        public static final String MENU_ADD = "message.log.menu.add";
        public static final String MENU_UPDATE = "message.log.menu.update";

        // 权限相关
        public static final String PERMISSION_DELETE = "message.log.permission.delete";
        public static final String PERMISSION_ADD = "message.log.permission.add";
        public static final String PERMISSION_UPDATE = "message.log.permission.update";
        public static final String PERMISSION_ASSIGN_USER = "message.log.permission.assign.user";
        public static final String PERMISSION_ASSIGN_ROLE = "message.log.permission.assign.role";

        // 上传基础信息相关
        public static final String UPLOAD_BASE_INFO_ADD = "message.log.upload.base.info.add";
        public static final String UPLOAD_BASE_INFO_UPDATE = "message.log.upload.base.info.update";
        public static final String UPLOAD_BASE_INFO_DELETE = "message.log.upload.base.info.delete";
        public static final String UPLOAD_BASE_INFO_ENABLE = "message.log.upload.base.info.enable";

        // 定时任务相关
        public static final String QUARTZ_TASK_ADD = "message.log.quartz.task.add";
        public static final String QUARTZ_TASK_EDIT = "message.log.quartz.task.edit";
        public static final String QUARTZ_TASK_DELETE = "message.log.quartz.task.delete";
        public static final String QUARTZ_TASK_RUN = "message.log.quartz.task.run";
        public static final String QUARTZ_TASK_PAUSH = "message.log.quartz.task.paush";
        public static final String QUARTZ_TASK_RESUME = "message.log.quartz.task.resume";

        // 定时任务日志相关
        public static final String QUARTZ_TASK_LOG_DELETE = "message.log.quartz.task.log.delete";

        // Redis相关
        public static final String REDIS_DELETE = "message.log.redis.delete";

        // 创建表相关
        public static final String TABLE_ADD = "message.log.table.add";
        public static final String TABLE_EDIT = "message.log.table.edit";
        public static final String FIELD_ADD = "message.log.field.add";
        public static final String FIELD_EDIT = "message.log.field.edit";
        public static final String FIELD_DELETE = "message.log.field.delete";
        public static final String TABLE_DELETE = "message.log.table.delete";
        public static final String DOWNLOAD_JAVA_CODE = "message.log.download.java.code";

        // 数据表配置相关
        public static final String TABLE_CONFIG_ADD = "message.log.table.config.add";
        public static final String TABLE_CONFIG_UPDATE = "message.log.table.config.update";
        public static final String TABLE_CONFIG_DELETE = "message.log.table.config.delete";
        public static final String TABLE_CONFIG_COMPLETELY_DELETE = "message.log.table.config.completely.delete";
        public static final String TABLE_CONFIG_RECOVER = "message.log.table.config.recover";

        // 表字段配置相关
        public static final String TABLE_FIELD_CONFIG_SYNC = "message.log.table.field.config.sync";
        public static final String TABLE_FIELD_CONFIG_EDIT = "message.log.table.field.config.edit";
        public static final String TABLE_FIELD_CONFIG_SORT = "message.log.table.field.config.sort";
        public static final String TABLE_FIELD_CONFIG_DELETE = "message.log.table.field.config.delete";
    }

    public static class User {
        public static final String OLD_PASSWORD_ERROR = "message.user.old.password.error";
        public static final String OLD_PASSWORD_NOT_NULL = "message.user.old.password.not.null";
        public static final String NEW_PASSWORD_NOT_NULL = "message.user.new.password.not.null";
        public static final String DEVICE_ID_REQUIRED = "message.user.device.id.required";
        public static final String USER_LOGOUT_SUCCESS = "message.user.logout.success";
        public static final String USER_LOGIN_FAILED = "message.user.login.failed";
        public static final String USER_GET_LOGIN_NAME_ERROR = "message.user.get.login.name.error";
        public static final String USER_LOGIN_FAILED_LIMIT = "message.user.login.failed.limit";
        public static final String USER_LOGIN_FAILED_UNAUTHORIZED = "message.user.login.failed.unauthorized";
        public static final String USER_LOGIN_FAILED_FORBIDDEN = "message.user.login.failed.forbidden";
        public static final String USER_CAPTCHA_NULL = "message.user.captcha.null";
        public static final String USER_CAPTCHA_ERROR = "message.user.captcha.error";
        public static final String USER_NOT_FOUND = "message.user.not.found";
        public static final String ASSIGN_ROLE_MORE = "message.user.assign.role.more";
        public static final String LOGIN_NAME_IS_NOT_NULL = "message.user.login.name.is.not.null";
        public static final String PASSWORD_IS_NOT_NULL = "message.user.password.is.not.null";
        public static final String INCORRECT_LOGIN_NAME_FORMAT = "message.user.incorrect.login.name.format";
        public static final String INCORRECT_PASSWORD_FORMAT = "message.user.incorrect.password.format";
        public static final String INCORRECT_EMAIL_FORMAT = "message.user.incorrect.email.format";
        public static final String INCORRECT_TEL_FORMAT = "message.user.incorrect.tel.format";
        public static final String ROLE_MUST_SELECT_ONE = "message.user.role.must.select.one";
        public static final String LOGIN_NAME_HAS_EXIST = "message.user.login.name.has.exist";
        public static final String EMAIL_HAS_EXIST = "message.user.email.has.exist";
        public static final String TEL_HAS_EXIST = "message.user.tel.has.exist";
        public static final String SAVE_USER_ERROR = "message.user.save.user.error";
        public static final String ASSIGN_USER_ID_EMPTY = "message.user.assign.user.id.empty";
        public static final String EMAIL_SEND_FAILED = "message.user.send.email.failed";
        public static final String SYSTEM_USER_CAN_NOT_DELETE = "message.user.system.user.can.not.delete";

        public static final String USER_SEND_EMAIL_EMPTY = "message.user.send.email.empty";
        public static final String USER_CHECK_EMAIL_CODE_EMPTY = "message.user.check.email.code.empty";
        public static final String EMAIL_CODE_LENGTH_ERROR = "message.user.email.code.length.error";
        // 无效的邮箱或验证码
        public static final String EMAIL_OR_CODE_INVALID = "message.user.email.or.code.invalid";
        public static final String EMAIL_NOT_VALIDATE = "message.user.email.not.validate";
        // 用户被禁用
        public static final String USER_DISABLED = "message.user.disabled";
        // 注册失败，请联系管理员
        public static final String REGISTER_FAILED = "message.user.register.failed";

        public static final String GET_LOCATION_ERROR = "message.user.get.location.error";

        public static final String CLICK_EVENT_EMPTY = "message.user.click.event.empty";


    }

    public static class Role {
        public static final String ROLE_ID_CAN_NOT_NULL = "message.role.id.can.not.null";
        public static final String ROLE_HAS_USER = "message.role.has.user";
        public static final String ROLE_NAME_EMPTY = "message.role.name.empty";
        public static final String ROLE_IS_DEFAULT_EMPTY = "message.role.is.default.empty";
        public static final String ROLE_NAME_HAS_EXIST = "message.role.name.has.exist";
        public static final String ROLE_HAS_OTHER_ROLE_IS_DEFAULT = "message.role.has.other.role.is.default";
        public static final String ROLE_MUST_ASSIGN_ONE_MENU = "message.role.must.assign.one.menu";
        public static final String ROLE_MUST_ASSIGN_ONE_PERMISSION = "message.role.must.assign.one.permission";
        public static final String ROLE_CURRENT_ROLE_IS_DEFAULT_DELETE = "message.role.current.role.is.default.delete";
        public static final String ROLE_CURRENT_ROLE_IS_DEFAULT_UPDATE = "message.role.current.role.is.default.update";
    }

    public static class JwtToken{
        public static final String JWT_TOKEN_NULL = "message.jwt.token.null";
        public static final String JWT_TOKEN_INVALID = "message.jwt.token.invalid";
        public static final String JWT_REFRESH_TOKEN_NULL = "message.jwt.refresh.token.null";
        public static final String JWT_REFRESH_TOKEN_INVALID = "message.jwt.refresh.token.invalid";
        public static final String JWT_DEVICE_ID_INVALID = "message.jwt.devicd.id.invalid";
        public static final String JWT_REFRESH_TOKEN_REVOKED = "message.jwt.refresh.token.revoked";
        public static final String JWT_REFRESH_TOKEN_FAILED = "message.jwt.refresh.token.failed";
        public static final String JWT_TOKEN_HAS_BEEN_INVALIDATED = "message.jwt.token.has.been.invalidated";
        public static final String JWT_TOKEN_EXPIRED = "message.jwt.token.expired";
        public static final String JWT_TOKEN_REVOKED = "message.jwt.token.revoked";
        public static final String JWT_TOKEN_MALFORMED = "message.jwt.token.malformed";
        public static final String JWT_TOKEN_UNSUPPORTED = "message.jwt.token.unsupported";
        public static final String JWT_TOKEN_SIGNATURE_FAILED = "message.jwt.token.signature.failed";
        public static final String JWT_TOKEN_INVALID_ARGUMENT = "message.jwt.token.invalid.argument";
    }

    public static class Exception {
        public static final String EXCEPTION_REDIS_DATA = "message.exception.redis.data";
        public static final String EXCEPTION_USER_NOT_FOUND = "message.exception.user.not.found";
        public static final String EXCEPTION_USER_NO_PERMISSION = "message.exception.user.no.permission";
        public static final String EXCEPTION_REQUEST_BODY_ERROR = "message.exception.request.body.error";
        public static final String EXCEPTION_REQUEST_METHOD_NOT_SUPPORTED = "message.exception.request.method.not.supported";
        public static final String EXCEPTION_DATABASE_OPERATION_FAILED = "message.exception.database.operation.failed";
        public static final String EXCEPTION_PATH_NOT_FOUND = "message.exception.path.not.found";
    }

    public static class Redis {
        public static final String REDIS_KEY_CAN_NOT_EMPTY = "message.redis.key.can.not.empty";
        public static final String REDIS_KEY_NOT_FOUND = "message.redis.key.not.found";
        // Redis 不支持的类型
        public static final String REDIS_NOT_SUPPORT_TYPE = "message.redis.not.support.type";
        public static final String REDIS_GET_KEY_VALUE_ERROR = "message.redis.get.key.value.error";
        public static final String REDIS_GET_VALUE_EXCEPTION = "message.redis.get.value.exception";
        public static final String REDIS_DELETE_VALUE_EXCEPTION = "message.redis.delete.value.exception";
        public static final String USER_CAN_NOT_DELETE_REDIS_VALUE = "message.redis.user.can.not.delete.redis.value";

    }

    public static class Api {
        public static final String API_LIMIT_EXCEPTION = "message.api.limit";
    }

    public static class System {
        public static final String SYSTEM_NOT_FOUND = "message.system.not.found";
        public static final String SYSTEM_ERROR = "message.system.error";
        public static final String SYSTEM_ERROR_VIEW = "message.system.error.view";
        public static final String FIELD_CONTENT_HAS_XSS_ERROR = "message.system.field.content.has.xss.error";
        public static final String FIELD_CONTENT_HAS_XSS_PARAM_ERROR = "message.system.field.content.has.xss.param.error";
        public static final String SYSTEM_GET_HTTP_SERVLET_REQUEST_ERROR = "message.system.get.http.servlet.request.error";
        public static final String SYSTEM_SITE_CONFIG_ERROR = "message.system.site.config.error";
    }

    public static class Validate {
        public static final String VALIDATE_ID_ERROR = "message.validate.id.error";
        public static final String VALIDATE_HEADER_AUTH_ERROR = "message.validate.header.auth.error";
    }

    public static class Permission {
        public static final String CODE_EXISTS = "message.permission.code.exists";
        public static final String NAME_NOT_NULL = "message.permission.name.not.null";
        public static final String CODE_NOT_NULL = "message.permission.code.not.null";
        public static final String TYPE_NOT_NULL = "message.permission.type.not.null";
        public static final String TYPE_CAN_NOT_CHANGE = "message.permission.type.can.not.change";
        public static final String DATA_INVALID = "message.permission.data.invalid";
        public static final String MENU_ID_NOT_NULL = "message.permission.menu.id.not.null";
        public static final String MENU_NOT_FOUND = "message.permission.menu.not.found";
        public static final String NOT_EXISTS = "message.permission.not.exists";
        public static final String API_URL_EMPTY = "message.permission.api.url.empty";
        public static final String API_METHOD_EMPTY = "message.permission.api.method.empty";
        public static final String API_URL_EXISTS = "message.permission.api.url.exists";
        public static final String API_NOT_REGISTERED = "message.permission.api.not.registered";
        public static final String PAGE_URL_EMPTY = "message.permission.page.url.empty";
        public static final String PAGE_URL_EXISTS = "message.permission.page.url.exists";
        public static final String BUTTON_KEY_EMPTY = "message.permission.button.key.empty";
        public static final String BUTTON_NAME_EMPTY = "message.permission.button.name.empty";
        public static final String BUTTON_KEY_EXISTS = "message.permission.button.key.exists";
        public static final String ASSIGN_PERMISSION_USER_NOT_FOUND = "message.permission.assign.user.not.found";
        public static final String ASSIGN_PERMISSION_USER_MORE = "message.permission.assign.user.more";
        public static final String ASSIGN_PERMISSION_ROLE_MORE = "message.permission.assign.role.more";
        public static final String API_DATA_INVALID = "message.permission.api.data.invalid";
        public static final String PAGE_DATA_INVALID = "message.permission.page.data.invalid";
        public static final String BUTTON_DATA_INVALID = "message.permission.button.data.invalid";
    }

    public static class Dict {
        public static final String DICT_TYPE_EMPTY = "message.dict.type.empty";

        public static final String DICT_VALUE_EMPTY = "message.dict.value.empty";

        public static final String DICT_LABEL_EMPTY = "message.dict.label.empty";

        public static final String DICT_TYPE_CAN_NOT_UPDATE = "message.dict.type.can.not.update";
        // 无效的ID
        public static final String DICT_ID_INVALID = "message.dict.id.invalid";

        public static final String DICT_NOT_EXIST = "message.dict.not.exist";
        // 已存在唯一的类型对应的标签
        public static final String DICT_EXISTS_TYPE_AND_LABEL = "message.dict.exists.type.and.label";
        // 已存在唯一的类型对应的标签和值
        public static final String DICT_EXISTS_TYPE_AND_LABEL_AND_VALUE = "message.dict.exists.type.and.label.and.value";
        // 原类型不能为空
        public static final String DICT_OLD_TYPE_EMPTY = "message.dict.old.type.empty";
        // 新类型不能为空
        public static final String DICT_NEW_TYPE_EMPTY = "message.dict.new.type.empty";
        // 新老类型名称一致
        public static final String DICT_TYPE_EQUAL = "message.dict.type.equal";
        // 新类型已存在
        public static final String DICT_NEW_TYPE_EXISTS = "message.dict.new.type.exists";

    }

    public static class file {
        // 文件上传相关错误消息
        public static final String FILE_NOT_EXIST = "message.file.not.exist";
        public static final String FILE_DIR_NOT_EXIST = "message.file.dir.not.exist";
        public static final String FOLDER_CREATE_FAILED = "message.folder.create.failed";
        public static final String File_CREATE_FAILED = "message.file.create.failed";
        public static final String WEB_URL_NOT_NULL = "message.web.url.not.null";
        public static final String LOCAL_URL_NOT_NULL = "message.local.url.not.null";
        public static final String FILE_UPLOAD_BASE64_NOT_NULL = "message.file.upload.base64.not.null";
        public static final String FILE_DELETE_ERROR = "message.file.delete.error";
        public static final String SITE_INFO_ERROR = "message.file.site.info.error";
        public static final String UPLOAD_EMPTY = "message.file.upload.empty";
        public static final String FORMAT_ERROR = "message.file.format.error";
        public static final String IMAGE_EMPTY = "message.file.image.empty";
        public static final String IMAGE_NAME_EMPTY = "message.file.image.name.empty";
        public static final String COPY_CONTENT_EMPTY = "message.file.copy.content.empty";
        public static final String DOWNLOAD_URL_EMPTY = "message.file.download.url.empty";
        public static final String DOWNLOAD_NAME_EMPTY = "message.file.download.name.empty";
        public static final String IO_EXCEPTION = "message.file.io.exception";
        public static final String QINIU_UPLOAD_BASE64_EXCEPTION = "message.qiniu.upload.base64.exception";
        public static final String ILLEGAL_STATE_EXCEPTION = "message.file.illegal.state.exception";
        public static final String NO_SUCH_ALGORITHM_EXCEPTION = "message.file.no.such.algorithm.exception";
        public static final String FILE_DOWNLOAD_ENCODING_EXCEPTION = "message.file.download.encoding.exception";
        public static final String FILE_DOWNLOAD_URL_EXCEPTION = "message.file.download.url.exception";
        public static final String FILE_DOWNLOAD_IO_EXCEPTION = "message.file.download.io.exception";
        // 创建基础上传目录失败
        public static final String FILE_CREATE_BASE_UPLOAD_FOLDER_ERROR = "message.file.create.base.upload.folder.error";
    }

    public static class Menu {
        public static final String MENU_NAME_EMPTY = "message.menu.name.empty";
        public static final String MENU_PATH_EMPTY = "message.menu.path.empty";
        public static final String MENU_TITLE_EMPTY = "message.menu.title.empty";
        public static final String PARENT_ID_ERROR = "message.menu.parent.id.error";
        public static final String MENU_NAME_EXISTS = "message.menu.name.exists";
        public static final String MENU_HAS_PERMISSIONS = "message.menu.has_permissions";
        public static final String MENU_HAS_SUBMENU = "message.menu.has_submenu";
        public static final String MENU_PARENT_ID_INVALID = "message.menu.parent.id.invalid";
        public static final String MENU_SORT_EMPTY = "message.menu.sort.empty";
        public static final String MENU_NOT_FOUND = "message.menu.not.found";
    }

    public static class Site {
        public static final String SITE_ID_NOT_CORRECT = "message.site.id.not.correct";
        public static final String SITE_NAME_EMPTY = "message.site.name.empty";
        public static final String FILE_UPLOAD_TYPE_EMPTY = "message.site.file.upload.type.empty";
        public static final String FILE_UPLOAD_TYPE_NOT_CORRECT = "message.site.file.upload.type.not.correct";
        public static final String SITE_CHECK_WEB_SERVICE_KEY_ERROR = "message.site.check.web.service.key.error";
        public static final String SITE_CURRENT_LOCATION_WEB_SERVICE_ERROR = "message.site.current.location.web.service.error";
        public static final String SITE_CURRENT_LOCATION_PCON_LINE_ERROR = "message.site.crrent.location.pcon.line.error";
    }

    public static class UploadBaseInfo {
        public static final String QUERY_PARAMS_NULL = "message.upload.base.info.query.params.null";
        public static final String TYPE_EMPTY = "message.upload.base.info.type.empty";
        public static final String TYPE_INVALID = "message.upload.base.info.type.invalid";
        public static final String BASE_PATH_EMPTY = "message.upload.base.info.base.path.empty";
        public static final String BUCKET_NAME_EMPTY = "message.upload.base.info.bucket.name.empty";
        public static final String DIR_EMPTY = "message.upload.base.info.dir.empty";
        public static final String DIR_RULE_NOT_MATCH = "message.upload.rule.not.match";
        public static final String ACCESS_KEY_EMPTY = "message.upload.base.info.access.key.empty";
        public static final String SECRET_KEY_EMPTY = "message.upload.base.info.secret.key.empty";
        public static final String ENDPOINT_OSS_EMPTY = "message.upload.base.info.endpoint.oss.empty";
        public static final String ENDPOINT_BITIFUL_EMPTY = "message.upload.base.info.endpoint.bitiful.empty";
        public static final String REGION_COS_EMPTY = "message.upload.base.info.region.cos.empty";
        public static final String REGION_BITIFUL_EMPTY = "message.upload.base.info.region.bitiful.empty";
        public static final String TYPE_NOT_USED = "message.upload.base.info.type.not.used";
        public static final String TEST_ACCESS_FAILED = "message.upload.base.info.test.access.failed";
        public static final String ID_NOT_EXIST = "message.upload.base.info.id.not.exist";
        public static final String TYPE_EXISTS = "message.upload.base.info.type.exists";
        public static final String UPLOAD_ENABLE_CAN_NOT_DELETE = "message.upload.enable.can.not.delete";
        public static final String LOCAL_UPLOAD_CAN_NOT_DELETE = "message.upload.local.can.not.delete";
        public static final String LOCAL_UPLOAD_CAN_NOT_DISABLE = "message.upload.local.can.not.disable";
    }

    public static class Sql {
        public static final String SQL_PARAM_CHECK_FAILED = "message.sql.param.check.failed";
        public static final String SQL_INJECTION_ACCEPT_DETECTED = "message.sql.injection.accept.detected";
        public static final String SQL_INVALID_OBJECT_NAME_FORMAT = "message.sql.invalid.object.name.format";
        public static final String SQL_OBJECT_NAME_TOO_LONG = "message.sql.object.name.too.long";
    }

    public static class Table {
        public static final String TABLE_NAME_CAN_NOT_BEGIN_WITH = "message.table.name.can.not.begin.with";
        public static final String TABLE_SCHEMA_NAME_NOT_MATCH = "message.table.schema.name.not.match";
        public static final String TABLE_COMMENT_NOT_EMPTY = "message.table.comment.not.empty";
        public static final String TABLE_TYPE_NOT_NULL = "message.table.type.not.null";
        public static final String TABLE_NAME_CONTAINS_JAVA_KEYWORDS = "message.table.name.contains.java.keywords";
        public static final String TABLE_FIELDS_NOT_EMPTY = "message.table.fields.not.empty";
        public static final String TABLE_EXISTS = "message.table.exists";
        public static final String TABLE_TYPE_INVALID = "message.table.type.invalid";
        public static final String TABLE_CAN_NOT_CHANGE = "message.table.can.not.change";
        public static final String TABLE_FIELD_CAN_NOT_CHANGE = "message.table.field.can.not.change";
        public static final String TABLE_FIELD_CAN_NOT_DELETE = "message.table.field.can.not.delete";
        public static final String TABLE_FIELD_COLUMN_NAME_NOT_EMPTY = "message.table.field.column.name.not.empty";
        public static final String TABLE_FIELD_OLD_COLUMN_NAME_NOT_EMPTY = "message.table.field.old.column.name.not.empty";
        public static final String TABLE_FIELD_COLUMN_LENGTH_NOT_EMPTY = "message.table.field.column.length.not.empty";
        public static final String TABLE_FIELD_COLUMN_TYPE_NOT_EMPTY = "message.table.field.column.type.not.empty";
        public static final String TABLE_FIELD_COLUMN_COMMENT_NOT_EMPTY = "message.table.field.column.comment.not.empty";
        public static final String TABLE_FIELD_ISNUllVALUE_TYPE_NOT_EMPTY = "message.table.field.column.is.null.value.not.empty";
        public static final String TABLE_FIELD_NAME_CONTAINS_JAVA_KEYWORDS = "message.table.field.name.contains.java.keywords";
        public static final String TABLE_FIELD_TYPE_INVALID = "message.table.field.type.invalid";
        public static final String TABLE_FIELD_LEGTH_TOO_LONG = "message.table.field.length.too.long";
        public static final String BASE_TABLE_NOT_CONTAINS_COMMON_FIELD = "message.table.base.table.not.contains.common.field";
        public static final String TREE_TABLE_NOT_CONTAINS_COMMON_FIELD = "message.table.tree.table.not.contains.common.field";
        public static final String FIELD_NAME_NOT_EMPTY = "message.table.field.name.not.empty";
        public static final String FIELD_HAS_EXIST = "message.table.field.has.exist";
        public static final String SYSTEM_TABLE_CAN_NOT_DELETE = "message.table.system.table.can.not.delete";

    }

    public static class TableConfig {
        public static final String TABLE_NAME_NOT_EMPTY = "message.table.name.not.empty";
        public static final String SCHEMA_NAME_NOT_EMPTY = "message.schema.name.not.empty";
        public static final String BUSINESS_NAME_NOT_EMPTY = "message.business.name.not.empty";
        public static final String CURRENT_TABLE_CAN_NOT_GENERATE_CODE = "message.table.config.current.table.can.not.generate.code";

        public static final String TABLE_CONFIG_EXISTS = "message.table.config.exists";
        public static final String TABLE_CONFIG_NO_EXISTS = "message.table.config.no.exists";
        public static final String TABLE_NOT_EXISTS = "message.table.not.exists";
        public static final String TABLE_SCHEMA_NAME_NOT_EXISTS = "message.table.schema.name.not.exists";
        public static final String TABLE_FIELDS_NOT_MATCH = "message.table.fields.not.match";
        public static final String TABLE_PREFIX_NOT_MATCH = "message.table.prefix.not.match";
        public static final String PATH_NOT_VALID_BY_SYSTEM = "message.path.not.valid.by.system";

        public static final String DOWLOAD_CODE_ERROR = "message.table.download.code.error";
        public static final String ZIP_CODE_ERROR = "message.table.zip.code.error";
    }

    public static class TableFieldConfig {
        public static final String BUSINESS_NAME_NOT_EMPTY = "message.table.field.business.name.not.empty";
        public static final String TABLE_CONFIG_ID_NOT_NULL = "message.table.field.table.config.id.not.null";
        public static final String TABLE_NAME_NOT_EMPTY = "message.table.field.config.table.name.not.empty";
        public static final String SCHEMA_NAME_NOT_EMPTY = "message.table.field.config.schema.name.not.empty";
        public static final String FIELD_NOT_FOUND = "message.table.field.config.field.not.found";
        public static final String FIELD_CONFIG_NOT_FOUND = "message.table.field.config.not.found";
        public static final String FORM_COMPONENT_TYPE_INVALID = "message.table.field.config.form.component.type.invalid";
        public static final String ASSOCIATED_TYPE_INVALID = "message.table.field.config.associated.type.invalid";
        public static final String QUERY_TYPE_INVALID = "message.table.field.config.query.type.invalid";
        public static final String QUERY_TYPE_NOT_NULL = "message.table.field.config.query.type.not.null";
        public static final String DICT_TYPE_NOT_FOUND = "message.table.field.config.dict.type.not.found";
        public static final String DICT_TYPE_REQUIRED = "message.table.field.config.dict.type.required";
        public static final String TABLE_TYPE_REQUIRED = "message.table.field.config.table.type.required";
        public static final String ASSOCIATED_TYPE_REQUIRED = "message.table.field.config.associated.type.required";
        public static final String SYNC_SUCCESS = "message.table.field.config.sync.success";
        public static final String SYNC_FAILED = "message.table.field.config.sync.failed";
        public static final String FIELD_NOT_NULLABLE = "message.table.field.not.nullable";
        public static final String FIELD_IDS_EMPTY = "message.table.field.ids.empty";
        public static final String FIELD_IDS_INVALID = "message.table.field.ids.invalid";
        public static final String ALL_FIELDS_MUST_SAME_TABLE = "message.table.field.all.fields.must.same.table";
        public static final String TABLE_FIELD_CONFIG_TYPE_ERROR = "message.table.field.config.type.error";
        public static final String TABLE_FIELD_CONFIG_FRONT_TYPE_NOT_MATCH = "message.table.field.config.front.type.not.match";
    }

    public static class QuartzTask{
        // 定时任务不存在
        public static final String TASK_NOT_FOUND = "message.quartz.task.not.found";
        public static final String TASK_NAME_NOT_EMPTY = "message.quartz.task.name.not.empty";
        public static final String TASK_GROUP_NAME_NOT_EMPTY = "message.quartz.task.group.name.not.empty";
        public static final String TASK_GROUP_NAME_NOT_MATCH = "message.quartz.task.group.name.not.match";
        public static final String TASK_NAME_EXISTS = "message.quartz.task.name.exists";
        public static final String TASK_CRON_NOT_EMPTY = "message.quartz.task.cron.not.empty";
        public static final String TASK_TARGET_BEAN_NOT_EMPTY = "message.quartz.task.target.bean.not.empty";
        public static final String TASK_TRGET_METHOD_NOT_EMPTY = "message.quartz.task.trget.method.not.empty";
        public static final String TASK_PARAMS_NOT_EMPTY = "message.quartz.task.params.not.empty";
        public static final String TASK_STATUS_NOT_EMPTY = "message.quartz.task.status.not.empty";
        public static final String TASK_REMARKS_NOT_EMPTY = "message.quartz.task.remarks.not.empty";
        public static final String TASK_EXCEPTION = "message.quartz.task.exception";
        // 参数解析失败
        public static final String TASK_PARAMS_PARSE_FAILED = "message.quartz.task.params.parse.failed";
        // 执行定时任务失败
        public static final String TASK_EXECUTE_FAILED = "message.quartz.task.execute.failed";

    }

    public static class Chat {

        public static final String CHAT_ID_CAN_NOT_EMPTY = "message.chat.chat_id_can_not_empty";

        public static final String CONTENT_CAN_NOT_EMPTY = "message.chat.content_can_not_empty";
    }

}
