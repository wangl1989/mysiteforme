create table if not exists analytics_click_events
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_id       bigint       null comment '用户ID，未登录用户为NULL',
    login_name    varchar(200) null comment '登录账号',
    nick_name     varchar(200) null comment '用户昵称',
    device_id     varchar(100) not null comment '设备ID',
    page_url      varchar(200) null comment '当前页面地址',
    element_id    varchar(255) null comment '点击元素ID',
    element_class varchar(255) null comment '点击元素类名',
    element_text  varchar(255) null comment '元素文字内容',
    element_path  varchar(255) null comment '元素的DOM路径',
    element_href  varchar(255) null comment '元素链接路径',
    event_type    varchar(255) null comment '元素类型(button/link/image等)',
    click_time    timestamp    not null comment '访问时间',
    create_date   datetime     null comment '创建时间',
    create_by     bigint       null comment '创建人',
    update_date   datetime     null comment '修改时间',
    update_by     bigint       null comment '修改人',
    remarks       varchar(255) null comment '备注',
    del_flag      bit          null comment '删除标记'
)
    comment '点击事件表';

create index idx_device_id
    on analytics_click_events (device_id);

create index idx_user_id
    on analytics_click_events (user_id);

create table if not exists analytics_daily_stats
(
    id                 bigint auto_increment comment '主键'
        primary key,
    stat_date          date         null comment '统计日期',
    total_visits       int          null comment '总访问量',
    unique_visitors    int          null comment '独立访客数',
    new_users          int          null comment '新用户',
    total_clicks       int          null comment '总点击量',
    bounce_rate        decimal(5)   null comment '跳出率',
    avg_visit_duration int          null comment '平均访问时长(秒)',
    create_date        datetime     null comment '创建时间',
    create_by          bigint       null comment '创建人',
    update_date        datetime     null comment '修改时间',
    update_by          bigint       null comment '修改人',
    remarks            varchar(255) null comment '备注',
    del_flag           tinyint      null comment '删除标记'
)
    comment '统计汇总表(按天)' charset = utf8mb3;

create table if not exists analytics_visit_logs
(
    id           bigint auto_increment comment '主键'
        primary key,
    user_id      bigint       null comment '用户ID，未登录用户为NULL',
    login_name   varchar(200) null comment '登录账号',
    nick_name    varchar(200) null comment '用户昵称',
    device_id    varchar(100) not null comment '设备ID',
    ip_address   varchar(100) null comment 'IP地址',
    user_agent   varchar(255) null comment '浏览器UA信息',
    referrer     varchar(255) null comment '来源页面',
    entry_page   varchar(255) null comment '当前页面',
    visit_time   timestamp    not null comment '访问时间',
    title        varchar(255) null comment '页面标题',
    screen_size  varchar(255) null comment '当前页面长宽比',
    time_on_page int          null comment '上一个页面停留时间(毫秒数)',
    language     varchar(200) null comment '当前页面的语言',
    country      varchar(100) null comment '国家',
    region       varchar(100) null comment '地区',
    city         varchar(100) null comment '城市',
    province     varchar(100) null comment '省',
    os           varchar(20)  null comment '系统类型',
    browser      varchar(255) null comment '浏览器',
    create_date  datetime     null comment '创建时间',
    create_by    bigint       null comment '创建人',
    update_date  datetime     null comment '修改时间',
    update_by    bigint       null comment '修改人',
    remarks      varchar(255) null comment '备注',
    del_flag     bit          null comment '删除标记'
)
    comment '访问记录表' charset = utf8mb3;

create index idx_user_id
    on analytics_visit_logs (user_id);

create table if not exists blog_article
(
    id           bigint auto_increment comment '主键'
        primary key,
    title        varchar(255) not null comment '标题',
    sub_title    varchar(255) null comment '副标题',
    marks        varchar(255) null comment '摘要',
    show_pic     varchar(255) null comment '显示图片',
    category     varchar(255) null comment '文章类型',
    out_link_url varchar(200) null comment '外链地址',
    resources    varchar(255) null comment '来源',
    publist_time datetime(1)  null comment '发布时间',
    content      text         not null comment '内容',
    text         text         null comment '纯文字文章内容',
    click        int          null comment '浏览量',
    channel_id   bigint       not null comment '栏目ID',
    sort         int          null comment '排序值',
    is_top       bit          null comment '是否置顶',
    is_recommend bit          null comment '是否推荐',
    status       int          null comment '文章状态',
    create_date  datetime     null comment '创建时间',
    create_by    bigint       null comment '创建人',
    update_date  datetime     null comment '修改时间',
    update_by    bigint       null comment '修改人',
    remarks      varchar(255) null comment '备注',
    del_flag     tinyint      null comment '删除标记'
)
    comment '博客内容' charset = utf8mb3;

create table if not exists blog_article_tags
(
    id         bigint auto_increment comment '主键'
        primary key,
    article_id bigint not null comment '文章ID,0,NO,false,false,false',
    tags_id    bigint not null comment '标签ID,0,NO,false,false,false'
)
    comment '博客标签' charset = utf8mb3;

create table if not exists blog_channel
(
    id              bigint auto_increment comment '主键'
        primary key,
    name            varchar(255)  not null comment '名称',
    site_id         bigint        null comment '站点ID',
    href            varchar(500)  null comment '链接地址',
    logo            varchar(255)  null comment '栏目图标',
    is_base_channel bit           null comment '是否为主栏目',
    can_comment     bit           null comment '是否能够评论',
    is_no_name      bit           null comment '是否匿名',
    is_can_aduit    bit           null comment '是否开启审核',
    seo_title       varchar(255)  null comment '网页title(seo)',
    seo_keywords    varchar(255)  null comment '网页关键字(seo) ',
    seo_description varchar(255)  null comment '网页描述(seo)',
    parent_id       bigint        null comment '父节点ID',
    parent_ids      varchar(2000) null comment '父节点联集',
    level           bigint        null comment '层级深度',
    sort            smallint      null comment '排序',
    create_date     datetime      null comment '创建时间',
    create_by       bigint        null comment '创建人',
    update_date     datetime      null comment '修改时间',
    update_by       bigint        null comment '修改人',
    remarks         varchar(255)  null comment '备注',
    del_flag        tinyint       null comment '删除标记'
)
    comment '博客栏目' charset = utf8mb3;

create table if not exists blog_comment
(
    id             bigint auto_increment comment '主键'
        primary key,
    content        text         not null comment '评论内容',
    type           int          null comment 'ip',
    ip             varchar(255) null comment 'ip',
    `system`       varchar(255) null comment '操作系统',
    browser        varchar(255) null comment '浏览器',
    floor          int          null comment '楼层',
    channel_id     bigint       null comment '栏目ID',
    article_id     int          null comment '文章ID',
    reply_id       bigint       null comment '回复评论ID',
    is_admin_reply bit          null comment '管理员是否回复',
    reply_content  text         null comment '管理员回复内容',
    create_date    datetime     null comment '创建时间',
    create_by      bigint       null comment '创建人',
    update_date    datetime     null comment '修改时间',
    update_by      bigint       null comment '修改人',
    remarks        varchar(255) null comment '备注',
    del_flag       tinyint      null comment '删除标记'
)
    comment '博客评论' charset = utf8mb3;

create table if not exists blog_tags
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        varchar(255) null comment '标签名字',
    sort        int          not null comment '排序',
    create_date datetime     null comment '创建时间',
    create_by   bigint       null comment '创建人',
    update_date datetime     null comment '修改时间',
    update_by   bigint       null comment '修改人',
    remarks     varchar(255) null comment '备注',
    del_flag    tinyint      null comment '删除标记'
)
    comment '博客标签' charset = utf8mb3;

create table if not exists gw_channel
(
    id          bigint auto_increment comment '主键'
        primary key,
    name        longblob      null comment '栏目名称',
    icon        tinytext      null comment '栏目图标',
    parent_id   bigint        null comment '父节点ID',
    parent_ids  varchar(2000) null comment '父节点联集',
    level       bigint        null comment '层级深度',
    sort        smallint      null comment '排序',
    create_date datetime      null comment '创建时间',
    create_by   bigint        null comment '创建人',
    update_date datetime      null comment '修改时间',
    update_by   bigint        null comment '修改人',
    remarks     varchar(255)  null comment '备注',
    del_flag    tinyint       null comment '删除标记'
)
    comment '官网栏目' charset = utf8mb3;

create table if not exists hat_area
(
    id     int          not null
        primary key,
    areaID varchar(255) null,
    area   varchar(255) null,
    father varchar(255) null
)
    charset = utf8mb3;

create table if not exists qrtz_calendars
(
    SCHED_NAME    varchar(120) not null,
    CALENDAR_NAME varchar(200) not null,
    CALENDAR      blob         not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
);

create table if not exists qrtz_fired_triggers
(
    SCHED_NAME        varchar(120) not null,
    ENTRY_ID          varchar(95)  not null,
    TRIGGER_NAME      varchar(200) not null,
    TRIGGER_GROUP     varchar(200) not null,
    INSTANCE_NAME     varchar(200) not null,
    FIRED_TIME        bigint       not null,
    SCHED_TIME        bigint       not null,
    PRIORITY          int          not null,
    STATE             varchar(16)  not null,
    JOB_NAME          varchar(200) null,
    JOB_GROUP         varchar(200) null,
    IS_NONCONCURRENT  varchar(1)   null,
    REQUESTS_RECOVERY varchar(1)   null,
    primary key (SCHED_NAME, ENTRY_ID)
);

create index idx_qrtz_ft_inst_job_req_rcvry
    on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index idx_qrtz_ft_j_g
    on qrtz_fired_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index idx_qrtz_ft_jg
    on qrtz_fired_triggers (SCHED_NAME, JOB_GROUP);

create index idx_qrtz_ft_t_g
    on qrtz_fired_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create index idx_qrtz_ft_tg
    on qrtz_fired_triggers (SCHED_NAME, TRIGGER_GROUP);

create index idx_qrtz_ft_trig_inst_name
    on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME);

create table if not exists qrtz_job_details
(
    SCHED_NAME        varchar(120) not null,
    JOB_NAME          varchar(200) not null,
    JOB_GROUP         varchar(200) not null,
    DESCRIPTION       varchar(250) null,
    JOB_CLASS_NAME    varchar(250) not null,
    IS_DURABLE        varchar(1)   not null,
    IS_NONCONCURRENT  varchar(1)   not null,
    IS_UPDATE_DATA    varchar(1)   not null,
    REQUESTS_RECOVERY varchar(1)   not null,
    JOB_DATA          blob         null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create index idx_qrtz_j_grp
    on qrtz_job_details (SCHED_NAME, JOB_GROUP);

create index idx_qrtz_j_req_recovery
    on qrtz_job_details (SCHED_NAME, REQUESTS_RECOVERY);

create table if not exists qrtz_locks
(
    SCHED_NAME varchar(120) not null,
    LOCK_NAME  varchar(40)  not null,
    primary key (SCHED_NAME, LOCK_NAME)
);

create table if not exists qrtz_paused_trigger_grps
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_GROUP varchar(200) not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
);

create table if not exists qrtz_scheduler_state
(
    SCHED_NAME        varchar(120) not null,
    INSTANCE_NAME     varchar(200) not null,
    LAST_CHECKIN_TIME bigint       not null,
    CHECKIN_INTERVAL  bigint       not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
);

create table if not exists qrtz_triggers
(
    SCHED_NAME     varchar(120) not null,
    TRIGGER_NAME   varchar(200) not null,
    TRIGGER_GROUP  varchar(200) not null,
    JOB_NAME       varchar(200) not null,
    JOB_GROUP      varchar(200) not null,
    DESCRIPTION    varchar(250) null,
    NEXT_FIRE_TIME bigint       null,
    PREV_FIRE_TIME bigint       null,
    PRIORITY       int          null,
    TRIGGER_STATE  varchar(16)  not null,
    TRIGGER_TYPE   varchar(8)   not null,
    START_TIME     bigint       not null,
    END_TIME       bigint       null,
    CALENDAR_NAME  varchar(200) null,
    MISFIRE_INSTR  smallint     null,
    JOB_DATA       blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_triggers_ibfk_1
        foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references qrtz_job_details (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create table if not exists qrtz_blob_triggers
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(200) not null,
    TRIGGER_GROUP varchar(200) not null,
    BLOB_DATA     blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_blob_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table if not exists qrtz_cron_triggers
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(200) not null,
    TRIGGER_GROUP   varchar(200) not null,
    CRON_EXPRESSION varchar(200) not null,
    TIME_ZONE_ID    varchar(80)  null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_cron_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table if not exists qrtz_simple_triggers
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(200) not null,
    TRIGGER_GROUP   varchar(200) not null,
    REPEAT_COUNT    bigint       not null,
    REPEAT_INTERVAL bigint       not null,
    TIMES_TRIGGERED bigint       not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_simple_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table if not exists qrtz_simprop_triggers
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(200)   not null,
    TRIGGER_GROUP varchar(200)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   varchar(1)     null,
    BOOL_PROP_2   varchar(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_simprop_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create index idx_qrtz_t_c
    on qrtz_triggers (SCHED_NAME, CALENDAR_NAME);

create index idx_qrtz_t_g
    on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP);

create index idx_qrtz_t_j
    on qrtz_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index idx_qrtz_t_jg
    on qrtz_triggers (SCHED_NAME, JOB_GROUP);

create index idx_qrtz_t_n_g_state
    on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index idx_qrtz_t_n_state
    on qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index idx_qrtz_t_next_fire_time
    on qrtz_triggers (SCHED_NAME, NEXT_FIRE_TIME);

create index idx_qrtz_t_nft_misfire
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index idx_qrtz_t_nft_st
    on qrtz_triggers (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index idx_qrtz_t_nft_st_misfire
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index idx_qrtz_t_nft_st_misfire_grp
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index idx_qrtz_t_state
    on qrtz_triggers (SCHED_NAME, TRIGGER_STATE);

create table if not exists quartz_task
(
    id           bigint auto_increment comment '主键'
        primary key,
    name         varchar(255) null comment '任务名称,input,YES,false,true,true',
    cron         varchar(255) null comment '任务表达式,input,YES,false,true,false',
    group_name   varchar(200) null comment '任务组名称',
    target_bean  varchar(255) null comment '执行的类,input,YES,false,true,false',
    trget_method varchar(255) null comment '执行方法,input,YES,false,true,false',
    params       varchar(255) null comment '执行参数,textarea,YES,false,false,false',
    status       int          null comment '任务状态,radio,YES,false,true,true',
    create_date  datetime     null comment '创建时间',
    create_by    bigint       null comment '创建人',
    update_date  datetime     null comment '修改时间',
    update_by    bigint       null comment '修改人',
    remarks      varchar(255) null comment '备注',
    del_flag     tinyint      null comment '删除标记'
)
    comment '定时任务' charset = utf8mb3;


create table if not exists quartz_task_log
(
    id           bigint auto_increment comment '主键'
        primary key,
    job_id       bigint       null comment '任务ID,0,YES,false,false,false',
    name         varchar(255) null comment '定时任务名称,input,YES,false,true,true',
    cron         varchar(255) null comment '任务表达式',
    group_name   varchar(200) null comment '任务组名称',
    target_bean  varchar(255) null comment '定制任务执行类,input,YES,false,true,false',
    trget_method varchar(255) null comment '定时任务执行方法,input,YES,false,true,false',
    params       varchar(255) null comment '执行参数,input,YES,false,true,false',
    status       int          null comment '任务状态,0,YES,false,false,false',
    error        text         null comment '异常消息,textarea,YES,false,false,false',
    times        int          null comment '执行时间,input,YES,false,true,false',
    create_date  datetime     null comment '创建时间',
    create_by    bigint       null comment '创建人',
    update_date  datetime     null comment '修改时间',
    update_by    bigint       null comment '修改人',
    remarks      varchar(255) null comment '备注',
    del_flag     tinyint      null comment '删除标记'
)
    comment '任务执行日志' charset = utf8mb3;


create table if not exists sys_dict
(
    id          bigint auto_increment comment '编号'
        primary key,
    value       varchar(100)            null comment '数据值',
    label       varchar(100)            null comment '标签名',
    type        varchar(100)            null comment '类型',
    description varchar(100)            null comment '描述',
    sort        int                     null comment '排序（升序）',
    parent_id   varchar(64) default '0' null comment '父级编号',
    create_by   varchar(64)             null comment '创建者',
    create_date datetime                null comment '创建时间',
    update_by   varchar(64)             null comment '更新者',
    update_date datetime                null comment '更新时间',
    remarks     varchar(255)            null comment '备注信息',
    del_flag    char        default '0' not null comment '删除标记'
)
    comment '字典表,测试表' collate = utf8mb3_bin;

create index sys_dict_del_flag
    on sys_dict (del_flag);

create index sys_dict_label
    on sys_dict (label);

create index sys_dict_value
    on sys_dict (value);

INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, '0', '否', 'is_or_not', '系统字段', 2, '0', '1', '2018-01-05 20:38:12', '1', '2025-05-02 20:27:26', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, '1', '是', 'is_or_not', '系统字段', 3, '0', '1', '2018-01-05 20:38:40', '1', '2025-05-09 18:50:36', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 'qiniu', '七牛云存储', 'sys_rescource_source', '系统字段', 3, '0', '1', '2018-01-14 06:39:57', '1', '2025-05-06 12:16:39', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 'oss', '阿里云存储', 'sys_rescource_source', '系统字段', 1, '0', '1', '2018-01-14 06:39:57', '1', '2018-01-14 06:39:57', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (108, '2333', '我的4444', 'my_sign', '', 2, '0', '1', '2018-01-16 16:03:12', '1', '2025-04-17 22:41:59', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (121, '1', '本站文章', 'blog_article_category', '博客内容-文章类型', 2, '0', '1', '2018-01-17 16:05:45', '1', '2025-05-08 21:51:55', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (122, '2', '外链', 'blog_article_category', '博客内容-文章类型', 3, '0', '1', '2018-01-17 16:05:45', '1', '2025-04-23 23:25:05', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (123, '0', '正常', 'quartz_task_status', '定时任务-任务状态', 2, '0', '1', '2018-01-24 23:41:56', '1', '2025-04-23 23:25:18', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (124, '1', '暂停', 'quartz_task_status', '定时任务-任务状态', 3, '0', '1', '2018-01-24 23:41:56', '1', '2025-04-23 23:25:23', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (125, 'local', '本地存储', 'sys_rescource_source', '系统字段', 2, '0', '1', '2025-02-08 21:18:36', '1', '2025-04-23 23:25:39', null, '0');
INSERT INTO mysiteforme.sys_dict (id, value, label, type, description, sort, parent_id, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (126, '12313', '他的444', 'my_sign', '', 3, '0', '1', '2025-04-17 23:05:03', '1', '2025-04-17 23:05:03', null, '0');

create table if not exists sys_group
(
    id          bigint auto_increment
        primary key,
    name        varchar(40)   null comment '分组名称',
    parent_id   bigint        null comment '父分组ID',
    level       bigint        null,
    parent_ids  varchar(2000) null comment '分组序列号',
    sort        smallint      null comment '分组排序值',
    create_by   bigint        null,
    create_date datetime      null,
    update_by   bigint        null,
    update_date datetime      null,
    remarks     varchar(255)  null,
    del_flag    tinyint       null
)
    charset = utf8mb3;

create table if not exists sys_group_ur
(
    group_id bigint not null comment '分组ID'
        primary key,
    user_id  bigint null comment '用户ID',
    role_id  bigint null comment '角色ID'
)
    charset = utf8mb3;

create table if not exists sys_log
(
    id           bigint auto_increment comment '编号'
        primary key,
    type         varchar(20)             null comment '请求类型',
    title        varchar(255) default '' null comment '日志标题',
    remote_addr  varchar(255)            null comment '操作IP地址',
    username     varchar(255)            null comment '操作用户昵称',
    request_uri  varchar(255)            null comment '请求URI',
    http_method  varchar(10)             null comment '操作方式',
    class_method varchar(255)            null comment '请求类型.方法',
    params       text                    null comment '操作提交的数据',
    session_id   varchar(255)            null comment 'sessionId',
    response     longtext                null comment '返回内容',
    use_time     int                     null comment '方法执行时间',
    browser      varchar(255)            null comment '浏览器信息',
    area         varchar(255)            null comment '地区',
    province     varchar(255)            null comment '省',
    city         varchar(255)            null comment '市',
    isp          varchar(255)            null comment '网络服务提供商',
    exception    text                    null comment '异常信息',
    create_by    varchar(64)             null comment '创建者',
    create_date  datetime                null comment '创建时间',
    update_by    bigint                  null,
    update_date  datetime                null,
    remarks      varchar(255)            null,
    del_flag     bit                     null
)
    comment '系统日志' collate = utf8mb3_bin;

create index sys_log_create_by
    on sys_log (create_by);

create index sys_log_create_date
    on sys_log (create_date);

create index sys_log_request_uri
    on sys_log (request_uri);

create index sys_log_type
    on sys_log (type);

create table if not exists sys_menu
(
    id                   bigint auto_increment
        primary key,
    name                 varchar(40)                        null comment '菜单名称',
    parent_id            bigint                             null comment '父菜单',
    level                bigint                             null comment '菜单层级',
    parent_ids           varchar(2000)                      null comment '父菜单联集',
    sort                 smallint                           null comment '排序',
    href                 varchar(2000)                      null comment '链接地址',
    target               varchar(20)                        null comment '打开方式',
    icon                 varchar(100)                       null comment '菜单图标',
    color                varchar(200)                       null comment '图标颜色',
    bg_color             varchar(255)                       null comment '显示背景色',
    is_show              tinyint                            null comment '是否显示',
    permission           varchar(200)                       null comment '权限标识',
    path                 varchar(200)                       null comment '前端路由地址',
    component            varchar(200)                       null comment '前端组件',
    title                varchar(200)                       null comment '菜单标题',
    show_badge           tinyint                            null comment '是否显示徽标（菜单右侧的红色小圆点）',
    show_text_badge      varchar(100)                       null comment '是否显示新徽标（菜单右侧的红色小字提醒标签）',
    is_hide              tinyint                            null comment '是否在菜单中隐藏（在左侧菜单栏中不显示）',
    is_hide_tab          tinyint                            null comment '是否在标签页中隐藏 （在顶部标签栏中不显示 ）',
    link                 varchar(2000)                      null comment '外链链接地址',
    is_iframe            tinyint                            null comment '是否为iframe',
    keep_alive           tinyint                            null comment '是否缓存',
    is_in_main_container tinyint                            null comment '是否在主容器中',
    create_by            bigint                             null,
    create_date          datetime default CURRENT_TIMESTAMP null,
    update_by            bigint                             null,
    update_date          datetime default CURRENT_TIMESTAMP null,
    remarks              varchar(255)                       null,
    del_flag             tinyint                            null
)
    charset = utf8mb3;


create index parent_id_pk
    on sys_menu (parent_id);

INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, '系统管理', null, 1, '1,', 20, '', null, '', '#FF69B4', null, 1, '', '/system', '/index/index', '系统管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:29:46', 1, '2025-05-08 00:46:22', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, '系统用户管理', 1, 2, '1,2,', 90, '/admin/system/user/list', null, '', '#002FA7', '#47e69c', 1, 'sys:user:list', '/user/account', '/system/user/Account', '系统用户管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:31:18', 1, '2025-05-01 13:55:46', '1231313', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, '系统角色管理', 1, 2, '1,3,', 80, '/admin/system/role/list', null, '', '#E85827', '#c23ab9', 1, 'sys:role:list', '/role/role', '/system/role/Role', '系统角色管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:32:33', 1, '2025-04-28 14:17:22', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, '系统权限管理', 1, 2, '1,4,', 70, '/admin/system/menu/list', null, '', '#800020', '#d4573b', 1, 'sys:menu:list', '/menu', '/system/menu/Menu', '菜单权限管理', 0, '火爆', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:33:19', 1, '2025-05-12 12:22:38', 'dfawdaw', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, '系统资源管理', 1, 2, '1,5,', 30, '/admin/system/rescource/list', null, '', '#B05923', '#f5e42a', 1, 'sys:rescource:list', '/resources/resourcesList', '/system/resources/ResourcesList', '系统资源管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:34:48', 1, '2025-04-28 14:31:47', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, '系统日志管理', 1, 2, '1,6,', 40, '/admin/system/log/list', null, '', '#81D8D0', '#b56c18', 1, 'sys:log:list', '/log/log', '/system/log/Log', '系统日志管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 11:35:31', 1, '2025-04-28 14:17:48', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, '网站基本信息', 1, 1, '7,', 3, '/admin/system/site/show', null, '', '#492D22', '#95deb9', 1, 'sys:site:list', '/site/setting', '/system/site/Setting', '网站基本信息', 0, '', null, 0, '', 0, 1, 0, 1, '2018-01-16 11:36:50', 1, '2025-04-28 14:35:26', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, '数据库管理', 1, 1, '8,', 24, '/admin/system/table/list', null, '', '#003153', '#369e16', 1, 'sys:table:list', '/table/table', '/system/table/Table', '数据库管理', 0, '', null, 0, '', 0, 1, 0, 1, '2018-01-16 11:38:29', 1, '2025-04-28 14:34:41', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (9, '系统字典管理', 1, 2, '1,9,', 60, '/admin/system/dict/list', null, '', '#E60000', '#1bbcc2', 1, 'sys:dict:list', '/dict/dict', '/system/dict/Dict', '系统字典管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-16 14:51:52', 1, '2025-04-27 14:31:58', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (10, '博客管理', null, 1, '10,', 10, '', null, '', '#00CED1', null, 1, '', '/blog', '/index/index', '博客管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2018-01-17 13:21:53', 1, '2025-05-11 22:11:02', '', 1);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (11, '栏目管理', 10, 2, '10,11,', 6, '/admin/blogChannel/list', null, '', null, null, 1, 'blog:channel:list', null, null, '栏目管理', null, null, null, null, null, null, null, null, 1, '2018-01-17 13:22:57', 1, '2025-05-11 22:10:53', null, 1);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (12, '博客评论', 10, 2, '10,12,', 7, '/admin/blogComment/list', null, '', null, '#c8e332', 1, 'blog:comment:list', null, null, '博客评论', null, null, null, null, null, null, null, null, 1, '2018-01-17 13:23:52', 1, '2025-05-11 22:10:55', null, 1);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (13, '博客文章', 10, 2, '10,13,', 8, '/admin/blogArticle/list', null, '', null, '#1962b5', 1, 'blog:article:list', null, null, '博客文章', null, null, null, null, null, null, null, null, 1, '2018-01-17 16:02:07', 1, '2025-05-11 22:10:57', null, 1);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (14, '定时任务', null, 1, '14,', 5, '', null, '', '#B0E0E6', null, 1, '', '/quartz', '/index/index', '定时任务', 0, '', null, 0, '', 0, 1, 0, 1, '2018-01-26 22:39:35', 1, '2025-05-01 13:42:34', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (15, '任务列表', 14, 2, '14,15,', 15, '/admin/quartzTask/list', null, '', '#0004FF', '#d6d178', 1, 'quartz:task:list', '/quartzTask', '/quartz/QuartzTask', '任务列表', 0, '', null, 0, '', 0, 1, 0, 1, '2018-01-26 22:41:25', 1, '2025-04-27 21:56:39', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (16, '任务执行日志', 14, 2, '14,16,', 10, '/admin/quartzTaskLog/list', null, '', '#B9FAFF', '#5158d6', 1, 'quartz:log:list', '/quartzTaskLog', '/quartz/QuartzTaskLog', '任务执行日志', 0, '', null, 0, '', 0, 1, 0, 1, '2018-01-27 01:07:11', 1, '2025-04-25 02:52:25', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (60, 'Druid数据监控', 1, 2, '1,60,', 25, '/admin/druid/list', null, '', '#008C8C', '#7e8755', 1, 'sys:druid:list', '/outside/iframe/elementui', '', 'Druid数据监控', 0, '', null, 0, 'http://localhost:8080/druid', 1, 1, 0, 1, '2018-06-16 05:06:17', 1, '2025-04-27 14:33:08', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (66, '上传基本信息', 1, 1, '66,', 2, null, null, '', '#FFC0CB', null, null, null, '/upload/uploadBaseInfo', '/system/upload/UploadBaseInfo', '上传基本信息', 0, '', 0, 0, '', 0, 1, 0, 1, '2025-04-14 14:42:03', 1, '2025-04-28 14:34:58', '上传基本信息', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (67, '数据库配置', 1, 1, '67,', 23, null, null, '', '#F9DC24', null, null, null, '/table/tableConfig', '/system/table/TableConfig', '数据库配置', 0, '', 0, 0, '', 0, 1, 0, 1, '2025-04-19 14:02:08', 1, '2025-04-28 14:34:49', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (68, 'Redis管理', 1, 1, '68,', 1, null, null, '', '#D559C6', null, null, null, '/redis/redis', '/system/redis/Redis', 'Redis管理', 0, '', 0, 0, '', 0, 1, 0, 1, '2025-04-23 18:25:06', 1, '2025-04-28 14:35:09', '', 0);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (76, '测试实施', null, 1, '76,', 3, null, null, '', null, null, null, null, '/test', '', '测试实施', 0, '', 0, 0, 'https://www.baidu.com', 0, 1, 0, 1, '2025-04-25 01:58:21', 1, '2025-05-11 22:10:42', '', 1);
INSERT INTO mysiteforme.sys_menu (id, name, parent_id, level, parent_ids, sort, href, target, icon, color, bg_color, is_show, permission, path, component, title, show_badge, show_text_badge, is_hide, is_hide_tab, link, is_iframe, keep_alive, is_in_main_container, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (80, '菜单权限分配', 1, 2, '1,80,', 2, null, null, '', null, null, null, null, '/menu/menuPermission', '/system/menu/MenuPermission', '菜单权限分配', 0, '', 1, 0, '', 0, 0, 0, 1, '2025-05-04 13:26:08', 1, '2025-05-05 05:17:37', '', 0);



create table if not exists sys_permission
(
    id              bigint auto_increment
        primary key,
    permission_name varchar(40)                        null comment '权限名称',
    permission_code varchar(2000)                      null comment '权限编码',
    permission_type int                                null comment '权限类型',
    menu_id         bigint                             null comment '权限分组ID',
    icon            varchar(100)                       null comment '权限图标',
    color           varchar(200)                       null comment '图标颜色',
    sort            smallint                           null comment '排序',
    create_by       bigint                             null comment '创建者',
    create_date     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by       bigint                             null comment '更新者',
    update_date     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remarks         varchar(255)                       null comment '权限备注',
    del_flag        tinyint                            null comment '删除标志'
);

create index meun_id_pk
    on sys_permission (menu_id);

INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, '用户列表页面', 'user:list:page', 1, 2, null, null, 1, 1, '2025-02-14 15:14:18', null, '2025-04-09 11:59:58', '前端路由地址：用户列表页面', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, '用户列表API', 'user:list:api', 3, 2, '', null, 10, 1, '2025-02-14 15:16:43', 1, '2025-04-30 22:20:57', '获取后端列表数据的API地址', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, '搜索用户按钮', 'user:search:button', 2, 2, '', null, 8, 1, '2025-02-14 15:18:23', 1, '2025-04-12 18:58:24', '前端搜索按钮权限', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, '新增用户按钮', 'user:add:button', 2, 2, '', null, 6, 1, '2025-02-14 15:18:36', 1, '2025-04-12 18:58:09', '前端用户新增按钮权限', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, '编辑用户按钮', 'user:edit:button', 2, 2, '', null, 8, 1, '2025-02-14 15:20:34', 1, '2025-04-12 18:58:38', '前端用户编辑按钮权限', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, '删除用户按钮', 'user:delete:button', 2, 2, '', null, 3, 1, '2025-02-14 15:21:15', 1, '2025-04-12 17:33:07', '前端用户删除按钮权限', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, '用户新增API', 'user:add:api', 3, 2, '&#xe820;', '#171665', 11, 1, '2025-02-14 15:22:43', 1, '2025-05-09 17:59:52', '新增用户数据到后端的API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, '角色列表页面', 'role:list:page', 1, 3, null, null, 1, 1, '2025-02-14 15:23:31', null, '2025-04-09 11:56:38', '前端路由地址：角色列表页面', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (9, '获取权限树API', 'permission:tree:api', 3, 4, '&#xe82b;', '#90EE90', 8, 1, '2025-02-15 21:41:23', 1, '2025-05-09 17:54:19', '权限树接口', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (10, '获取当前用户详细信息API', 'user:detailInfo:api', 3, 2, null, null, 1, 1, '2025-02-17 16:17:33', null, '2025-04-12 01:59:29', '当前登录用户详细信息API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (11, '权限列表API', 'permission:list:api', 3, 4, '&#xe83d;', '#E6A23C', 5, 1, '2025-03-11 01:05:39', 1, '2025-04-30 22:22:21', '权限列表页数据API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (12, '权限新增', 'permission:add:api', 3, 4, '&#xe602;', '#409EFF', 1, 1, '2025-03-11 01:08:23', 1, '2025-04-12 18:28:11', '权限新增API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (19, '上传基础信息列表API', 'upload:list:api', 3, 5, null, null, 3, 1, '2025-03-11 02:43:21', 1, '2025-04-19 14:06:24', null, 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (20, '上传基础信息新增API', 'upload:add:api', 3, 5, null, null, 5, 1, '2025-03-11 02:48:00', 1, '2025-04-19 14:06:21', null, 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (21, '当前用户API', 'current:user:api', 3, 2, null, null, 2, 1, '2025-04-06 20:41:40', null, '2025-04-12 01:57:50', '当前用户API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (22, '当前用户菜单API', 'currentuser:menu:api', 3, 2, null, null, 2, 1, '2025-04-08 13:07:04', null, '2025-04-12 01:57:50', '当前用户菜单API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (23, '当前用户角色列表API', 'currentuser:role:api', 3, 3, null, null, 1, 1, '2025-04-09 11:46:34', null, '2025-04-12 01:57:51', '当前用户角色列表API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (24, '角色分页列表API', 'role:list:api', 3, 3, '', null, 7, 1, '2025-04-09 11:53:37', 1, '2025-04-30 22:21:45', '角色分页列表API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (25, '用户详情API', 'user:detail:api', 3, 2, '', null, 9, 1, '2025-04-09 12:17:19', 1, '2025-04-30 22:20:49', '用户详情API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (26, '编辑用户API', 'user:edit:api', 3, 2, '&#xe823;', '#171665', 2, 1, '2025-04-09 15:07:09', 1, '2025-05-09 17:59:37', '编辑用户API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (27, '角色菜单权限集合API', 'role:menuPer:api', 3, 3, '', null, 6, 1, '2025-04-09 22:19:50', 1, '2025-04-30 22:21:23', '角色菜单权限集合API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (28, '新增菜单API', 'add:menu:api', 3, 4, '&#xe602;', '#00CED1', 2, 1, '2025-04-11 00:53:16', 1, '2025-04-12 20:44:09', null, 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (29, '编辑菜单API', 'edit:menu:api', 3, 4, '&#xe626;', '#E6A23C', 3, 1, '2025-04-11 00:53:38', 1, '2025-04-12 20:43:41', null, 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (31, 'sss', 'sdd', 1, 4, '&#xe690;', null, 1, 1, '2025-04-12 00:47:06', 1, '2025-04-12 22:02:15', null, 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (32, 'asdad', 'adawd', 1, 6, '&#xe603;', '#90EE90', 1, 1, '2025-04-12 01:29:12', 1, '2025-04-12 22:01:51', 'sfsefesfdfrbdrbdrb', 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (34, '编辑权限API', 'edit:permission:api', 3, 4, '&#xe642;', '#E6A23C', 2, 1, '2025-04-12 02:04:54', 1, '2025-04-12 18:27:39', null, 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (35, '权限删除API', 'delete:permission:api', 3, 4, '&#xe66d;', '#F56C6C', 7, 1, '2025-04-12 21:59:49', 1, '2025-04-12 21:59:49', '权限删除API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (36, '菜单删除API', 'menu:delete:api', 3, 4, '&#xe66d;', '#F56C6C', 9, 1, '2025-04-12 22:01:06', 1, '2025-04-12 22:01:06', '权限删除API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (37, '新增子菜单按钮', 'add:submenu:button', 2, 4, '&#xe83f;', '#1C7F9D', 12, 1, '2025-04-12 23:00:57', 1, '2025-05-01 16:20:18', '展示权限列表按钮', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (38, '增加主菜单按钮', 'root:menu:add', 2, 4, '', null, 13, 1, '2025-04-12 23:50:19', 1, '2025-04-12 23:50:19', '增加主菜单按钮', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (39, '资源列表页分页API', 'resourcee:pagelist:api', 3, 5, '&#xe690;', '#B0E6E6', 6, 1, '2025-04-13 01:10:40', 1, '2025-05-02 19:06:38', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (40, '日志列表页API', 'log:pagelist:api', 3, 6, '&#xe814;', '#E6A23C', 3, 1, '2025-04-13 23:40:10', 1, '2025-04-30 22:22:59', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (41, '删除日志API', 'delete:log:api', 3, 6, '&#xe800;', '#EF0C14', 2, 1, '2025-04-13 23:41:00', 1, '2025-05-08 23:53:23', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (42, '日志删除按钮', 'log:button:delete', 2, 6, '&#xe87e;', '#9370DB', 1, 1, '2025-04-13 23:43:44', 1, '2025-04-13 23:43:44', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (43, '上传基本信息列表页API', 'uploadinfo:pagelist:api', 3, 66, '', null, 1, 1, '2025-04-14 14:44:31', 1, '2025-04-14 14:44:31', '上传基本信息列表页API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (45, '新增上传信息API', 'add:uploadinfo:api', 3, 66, '&#xe89e;', '#90EE90', 2, 1, '2025-04-14 15:01:10', 1, '2025-05-09 18:14:54', '新增上传信息API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (46, '编辑上传信息API', 'edit:uploadinfo:api', 3, 66, '&#xe797;', '#279AA7', 3, 1, '2025-04-14 16:10:00', 1, '2025-05-09 18:05:37', '编辑上传信息API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (47, '获取本地上传测试图片API', 'get:localimg:api', 3, 66, '&#xe801;', '#FFC0CB', 4, 1, '2025-04-14 18:06:40', 1, '2025-05-09 17:57:31', '获取本地上传测试图片API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (48, '图片上传通用API', 'file:upload:api', 3, 66, '&#xe878;', '#29B7D3', 5, 1, '2025-04-15 00:45:55', 1, '2025-05-03 20:53:20', '图片上传通用API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (49, '字典分页列表API', 'dict:pagelist:api', 3, 9, '', null, 7, 1, '2025-04-17 20:28:43', 1, '2025-04-30 22:22:49', '字典分页列表API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (50, '添加字段API', 'add:dict:api', 3, 9, '', null, 2, 1, '2025-04-17 20:29:34', 1, '2025-04-17 20:29:34', '添加字段API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (51, '编辑字典API', 'edit:dict:api', 3, 9, '&#xe805;', '#9370DB', 3, 1, '2025-04-17 20:30:20', 1, '2025-05-08 23:21:24', '编辑字典API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (52, '编辑字典类型API', 'edit:type:api', 3, 9, '&#xe84b;', '#67C23A', 4, 1, '2025-04-17 20:31:55', 1, '2025-05-09 18:01:06', '删除字典API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (53, '删除字典API', 'delete:dict:api', 3, 9, '&#xe665;', '#F56C6C', 5, 1, '2025-04-17 22:43:15', 1, '2025-04-17 22:43:15', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (54, '当前站点API', 'current:sit:api', 3, 7, '', null, 1, 1, '2025-04-17 23:43:32', 1, '2025-05-01 13:55:34', '当前站点API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (55, '获取上传信息集合API', 'system:uploadtype:api', 3, 7, '', null, 2, 1, '2025-04-18 00:38:46', 1, '2025-05-01 13:55:34', '获取上传信息集合API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (56, '编辑系统信息API', 'edit:system:api', 3, 7, '', null, 3, 1, '2025-04-18 00:39:28', 1, '2025-05-01 13:55:34', '编辑系统信息API', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (57, 'Druid页面路由', 'druid:list:page', 1, 60, '', null, 1, 1, '2025-04-18 16:55:14', 1, '2025-04-18 21:35:37', '', 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (58, '数据库配置分页列表API', 'tableConfig:pagelist:api', 3, 67, '&#xe638;', '#90EE90', 1, 1, '2025-04-19 14:14:41', 1, '2025-04-19 14:14:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (59, '数据库配置新增API', 'add:tableConfig:api', 3, 67, '&#xe83e;', '#FFC0CB', 2, 1, '2025-04-19 14:16:26', 1, '2025-04-19 14:16:26', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (60, '数据库配置编辑API', 'edit:tableConfig:api', 3, 67, '&#xe64f;', '#FFD000', 3, 1, '2025-04-19 14:17:30', 1, '2025-04-19 14:17:30', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (61, '表格配置删除API', 'delete:tableConfig:api', 3, 67, '&#xe665;', '#FF0033', 4, 1, '2025-04-19 14:18:33', 1, '2025-04-19 14:18:33', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (62, '数据库配置恢复API', 'recover:tableConfig:api', 3, 67, '&#xe614;', '#2CB595', 5, 1, '2025-04-19 14:20:46', 1, '2025-05-09 18:05:00', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (63, '获取数据库表名称列表API', 'get:tablelist:api', 3, 67, '', null, 6, 1, '2025-04-19 14:31:50', 1, '2025-04-19 14:49:09', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (64, '获取数据库名称API', 'get:tableName:api', 3, 67, '', null, 7, 1, '2025-04-19 14:49:43', 1, '2025-04-19 14:49:43', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (65, '同步表字段数据API', 'sync:fieldname:api', 3, 67, '&#xe79b;', '#2C4EB5', 8, 1, '2025-04-19 21:03:48', 1, '2025-05-09 18:04:28', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (66, '编辑字段配置API', 'edit:fieldconfig:api', 3, 67, '&#xe897;', '#FFC0CB', 9, 1, '2025-04-21 09:35:00', 1, '2025-05-09 18:04:16', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (67, '获取所有字典类型API', 'all:dicttype:api', 3, 9, '', null, 6, 1, '2025-04-21 16:28:53', 1, '2025-04-30 22:22:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (68, '获取表字段简单信息集合API', 'simple:field:api', 3, 67, '', null, 10, 1, '2025-04-21 16:37:57', 1, '2025-04-21 16:37:57', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (69, '根据表配置ID获取字段集合API', 'get:fields:api', 3, 67, '', null, 11, 1, '2025-04-21 20:28:44', 1, '2025-05-03 12:38:06', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (70, '表字段排序API', 'sort:field:api', 3, 67, '&#xe793;', '#B0E0E6', 12, 1, '2025-04-21 21:28:48', 1, '2025-05-09 18:04:03', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (71, '获取表分页列表API', 'table:pagelist:api', 3, 8, '', null, 1, 1, '2025-04-22 23:47:55', 1, '2025-04-22 23:47:55', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (72, '新增表格API', 'add:table:api', 3, 8, '&#xe759;', '#90EE90', 2, 1, '2025-04-23 13:40:15', 1, '2025-05-09 18:03:28', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (73, '编辑表格API', 'edit:table:api', 3, 8, '&#xe76f;', '#FFC0CB', 3, 1, '2025-04-23 13:40:44', 1, '2025-05-09 18:03:19', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (74, '删除表API', 'delet:table:api', 3, 8, '&#xe83a;', '#F21322', 4, 1, '2025-04-23 13:42:22', 1, '2025-05-09 18:03:02', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (75, '获取字段列表（分页）API', 'get:tablepage:api', 3, 8, '', null, 5, 1, '2025-04-23 13:42:53', 1, '2025-04-23 13:42:53', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (76, '获取字段列表（不分页）API', 'show:tablelist:api', 3, 8, '', null, 6, 1, '2025-04-23 13:43:41', 1, '2025-04-23 13:43:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (77, '新增字段API', 'add:field:api', 3, 8, '&#xe732;', '#409EFF', 7, 1, '2025-04-23 13:44:26', 1, '2025-05-09 18:02:51', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (78, '编辑字段API', 'edit:field:api', 3, 8, '&#xe73b;', '#9370DB', 8, 1, '2025-04-23 13:44:49', 1, '2025-05-09 18:02:37', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (79, '判断字段是否存在API', 'field:exist:api', 3, 8, '', null, 9, 1, '2025-04-23 13:45:33', 1, '2025-04-30 22:23:28', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (80, '删除字段API', 'delete:field:api', 3, 8, '&#xe83a;', '#E30F24', 10, 1, '2025-04-23 13:45:59', 1, '2025-05-09 18:02:27', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (81, '获取所有RedisAPI', 'get:allredis:api', 3, 68, '', null, 1, 1, '2025-04-23 18:26:06', 1, '2025-04-23 18:26:06', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (82, 'Redis值删除', 'redis:delete:api', 3, 68, '&#xe87e;', '#C5103A', 2, 1, '2025-04-23 21:03:29', 1, '2025-05-09 18:15:24', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (83, '定时任务分页列表API', 'quartztask:pagelist:api', 3, 15, '', null, 8, 1, '2025-04-24 12:42:10', 1, '2025-04-24 12:51:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (84, '新增定时任务API', 'add:quartztask:api', 3, 15, '', null, 2, 1, '2025-04-24 12:42:49', 1, '2025-04-24 12:42:49', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (85, '编辑定时任务API', 'eidt:quartzTask:api', 3, 15, '', null, 3, 1, '2025-04-24 12:43:18', 1, '2025-04-24 12:43:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (86, '删除定时任务API', 'delete:quartztask:api', 3, 15, '', null, 4, 1, '2025-04-24 12:44:05', 1, '2025-04-24 12:44:05', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (87, '暂停选中的定时任务API', 'paush:quartztask:api', 3, 15, '', null, 5, 1, '2025-04-24 12:44:45', 1, '2025-04-24 12:44:45', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (88, '恢复选中的定时任务运行API', 'resume:quartztask:api', 3, 15, '', null, 6, 1, '2025-04-24 12:45:16', 1, '2025-04-24 12:45:16', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (89, '立即执行选中的定时任务API', 'run:quartztask:api', 3, 15, '', null, 7, 1, '2025-04-24 12:45:43', 1, '2025-04-24 12:45:43', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (90, '分页获取定时任务日志API', 'quartztasklog:pagelist:api', 3, 16, '', null, 1, 1, '2025-04-24 13:04:29', 1, '2025-04-24 13:04:29', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (91, '定时任务日志删除API', 'delete:tasklog:api', 3, 16, '', null, 2, 1, '2025-04-25 01:43:51', 1, '2025-04-25 01:43:51', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (92, '编辑角色API', 'edit:role:api', 3, 3, '&#xe603;', '#E6A23C', 2, 1, '2025-04-27 13:28:46', 1, '2025-05-09 00:08:14', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (93, '新增角色API', 'add:role:api', 3, 3, '&#xe73b;', '#9370DB', 3, 1, '2025-04-27 13:29:45', 1, '2025-05-09 18:00:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (94, '保存角色权限API', 'save:roleper:api', 3, 3, '&#xe838;', '#B0E0E6', 4, 1, '2025-04-27 13:30:53', 1, '2025-05-09 18:00:31', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (95, '下载源码API', 'down:code:api', 3, 67, '&#xe89f;', '#90EE90', 6, 1, '2025-04-27 22:03:01', 1, '2025-05-10 23:37:02', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (96, '额外分配用户权限', 'assign:UserPermission:api', 3, 2, '&#xe816;', '#3E9C8C', 3, 1, '2025-04-28 13:05:55', 1, '2025-05-09 17:59:25', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (97, '获取单独分配的权限ID集合', 'get:AssignedPermission:api', 3, 2, '', null, 4, 1, '2025-04-28 13:06:53', 1, '2025-04-28 13:06:53', '根据用户ID获取已经给他单独分配的权限ID集合', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (98, '删除角色API', 'delete:role:api', 3, 3, '&#xe71a;', '#701F1F', 5, 1, '2025-04-28 22:59:47', 1, '2025-05-09 18:00:20', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (99, '删除用户API', 'delete:user:api', 3, 2, '&#xe71a;', '#F56C6C', 5, 1, '2025-04-29 22:13:42', 1, '2025-05-09 17:59:08', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (100, '获取当前定位API', 'get:location:api', 3, 2, '', null, 6, 1, '2025-04-30 14:50:45', 1, '2025-04-30 14:50:45', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (101, '更新当前用户API', 'edit:currentUser:api', 3, 2, '&#xe74a;', '#E6A23C', 7, 1, '2025-04-30 20:48:34', 1, '2025-05-09 17:58:28', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (102, '当前用户修改密码API', 'change:password:api', 3, 2, '&#xe70a;', '#B0E0E6', 8, 1, '2025-04-30 21:49:10', 1, '2025-05-09 17:58:13', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (104, '新增角色按钮', 'add:role:button', 2, 3, '', null, 1, 1, '2025-05-01 16:04:56', 1, '2025-05-01 16:04:56', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (105, '编辑角色按钮', 'edit:role:button', 2, 3, '', null, 2, 1, '2025-05-01 16:11:29', 1, '2025-05-01 16:11:29', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (106, '删除角色按钮', 'delete:role:button', 2, 3, '', null, 3, 1, '2025-05-01 16:12:59', 1, '2025-05-01 16:12:59', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (107, '角色分配权限按钮', 'role:assper:button', 2, 3, '', null, 4, 1, '2025-05-01 16:14:52', 1, '2025-05-01 16:14:52', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (108, '单独分配权限按钮', 'ass:user:button', 2, 2, '', null, 9, 1, '2025-05-01 16:15:41', 1, '2025-05-01 16:15:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (109, '删除菜单按钮', 'delete:menu:button', 2, 4, '', null, 14, 1, '2025-05-01 16:21:32', 1, '2025-05-01 16:21:32', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (110, '编辑菜单按钮', 'edit:menu:button', 2, 4, '', null, 15, 1, '2025-05-01 16:22:16', 1, '2025-05-01 16:22:16', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (111, '权限编辑按钮', 'edit:permission:button', 2, 4, '', null, 16, 1, '2025-05-01 16:23:10', 1, '2025-05-01 16:23:10', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (112, '权限删除按钮', 'delete:permission:button', 2, 4, '', null, 17, 1, '2025-05-01 16:23:57', 1, '2025-05-01 16:23:57', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (113, '新增字典按钮', 'add:dict:button', 2, 9, '', null, 1, 1, '2025-05-01 16:27:04', 1, '2025-05-01 16:27:04', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (114, '编辑字典按钮', 'edit:dict:button', 2, 9, '', null, 2, 1, '2025-05-01 16:27:35', 1, '2025-05-01 16:27:35', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (115, '删除字典按钮', 'delete:dict:button', 2, 9, '', null, 3, 1, '2025-05-01 16:28:15', 1, '2025-05-01 16:28:15', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (116, '编辑字典类型按钮', 'edit:dicttype:button', 2, 9, '', null, 4, 1, '2025-05-01 16:30:50', 1, '2025-05-01 16:30:50', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (117, '添加该类型字典按钮', 'add:typedict:button', 2, 9, '', null, 5, 1, '2025-05-01 16:31:47', 1, '2025-05-01 16:31:47', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (119, '资源预览按钮', 'preview:resource:button', 2, 5, '&#xe6f4;', '#09144E', 3, 1, '2025-05-01 16:40:22', 1, '2025-05-02 19:30:55', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (120, '资源复制链接按钮', 'copylink:resource:button', 2, 5, '', null, 2, 1, '2025-05-01 16:41:26', 1, '2025-05-01 16:41:26', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (121, '新增表格按钮', 'add:table:button', 2, 8, '', null, 1, 1, '2025-05-01 16:42:54', 1, '2025-05-01 16:42:54', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (122, '批量删除表格按钮', 'batchdelete:table:button', 2, 8, '', null, 2, 1, '2025-05-01 16:43:57', 1, '2025-05-01 16:51:31', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (123, '编辑表格按钮', 'edit:table:button', 2, 8, '', null, 3, 1, '2025-05-01 16:45:16', 1, '2025-05-01 16:45:16', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (124, '删除表格按钮', 'delete:table:button', 2, 8, '', null, 4, 1, '2025-05-01 16:45:51', 1, '2025-05-01 16:45:51', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (125, '管理字段按钮', 'manage:field:button', 2, 8, '', null, 5, 1, '2025-05-01 16:46:57', 1, '2025-05-01 16:46:57', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (126, '新增字段按钮', 'add:field:button', 2, 8, '', null, 6, 1, '2025-05-01 16:47:48', 1, '2025-05-01 16:47:48', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (127, '编辑字段按钮', 'edit:field:button', 2, 8, '', null, 7, 1, '2025-05-01 16:48:22', 1, '2025-05-01 16:48:22', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (128, '删除字段按钮', 'delete:field:button', 2, 8, '', null, 8, 1, '2025-05-01 16:48:51', 1, '2025-05-01 16:48:51', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (129, '批量删除字段按钮', 'batchdelete:field:button', 2, 8, '', null, 9, 1, '2025-05-01 16:49:44', 1, '2025-05-01 16:51:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (130, '新增表格配置按钮', 'add:tableconfig:button', 2, 67, '', null, 1, 1, '2025-05-01 16:53:32', 1, '2025-05-01 16:53:32', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (131, '批量删除表格配置按钮', 'batchdelete:tableconfig:button', 2, 67, '', null, 2, 1, '2025-05-01 16:54:35', 1, '2025-05-01 16:54:35', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (132, '下载源码按钮', 'download:source:button', 2, 67, '', null, 3, 1, '2025-05-01 16:55:14', 1, '2025-05-01 16:55:14', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (133, '编辑表格配置按钮', 'edit:tableconfig:button', 2, 67, '', null, 4, 1, '2025-05-01 16:55:56', 1, '2025-05-01 16:55:56', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (134, '删除表格配置按钮', 'delete:tableconfig:button', 2, 67, '', null, 5, 1, '2025-05-01 16:56:35', 1, '2025-05-01 16:56:35', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (135, '同步字段按钮', 'sync:field:button', 2, 67, '', null, 6, 1, '2025-05-01 16:57:35', 1, '2025-05-01 16:57:35', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (136, '可配置字段按钮', 'config:field:button', 2, 67, '', null, 7, 1, '2025-05-01 16:59:22', 1, '2025-05-01 16:59:22', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (137, '字段排序按钮', 'config:fieldsort:button', 2, 67, '', null, 8, 1, '2025-05-01 17:00:18', 1, '2025-05-01 17:00:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (138, '预览表单按钮', 'perview:form:button', 2, 67, '', null, 9, 1, '2025-05-01 17:01:22', 1, '2025-05-01 17:01:22', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (139, '编辑字段配置按钮', 'edit:fieldconfig:button', 2, 67, '', null, 10, 1, '2025-05-01 17:02:37', 1, '2025-05-01 17:02:37', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (140, '删除字段配置按钮', 'delete:fieldconfig:button', 2, 67, '', null, 11, 1, '2025-05-01 17:03:17', 1, '2025-05-01 17:03:17', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (141, '站点编辑按钮', 'edit:site:button', 2, 7, '', null, 1, 1, '2025-05-01 17:04:26', 1, '2025-05-01 17:04:26', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (142, '站点信息保存按钮', 'save:site:button', 2, 7, '', null, 2, 1, '2025-05-01 17:05:48', 1, '2025-05-01 17:05:48', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (143, '取消编辑按钮', 'cancle:edit:button', 2, 7, '', null, 3, 1, '2025-05-01 17:06:18', 1, '2025-05-01 17:06:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (144, '新增上传方式按钮', 'add:uploadtype:button', 2, 66, '', null, 1, 1, '2025-05-01 17:07:36', 1, '2025-05-01 17:07:36', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (145, '编辑上传方式按钮', 'edit:uploadtype:button', 2, 66, '', null, 2, 1, '2025-05-01 17:08:33', 1, '2025-05-01 17:08:33', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (146, '上传方式禁启用按钮', 'uploadtype:enable:button', 2, 66, '', null, 3, 1, '2025-05-01 17:10:48', 1, '2025-05-01 17:10:48', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (147, '展示测试图片按钮', 'show:test:button', 2, 66, '', null, 4, 1, '2025-05-01 17:13:03', 1, '2025-05-01 17:13:03', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (148, '批量删除按钮', 'batch:delete:button', 2, 68, '', null, 1, 1, '2025-05-01 17:20:09', 1, '2025-05-01 17:20:09', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (149, '删除redis缓存按钮', 'delete:redis:button', 2, 68, '', null, 2, 1, '2025-05-01 17:21:45', 1, '2025-05-01 17:21:45', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (150, 'redis查看值按钮', 'redis:showvalue:button', 2, 68, '', null, 3, 1, '2025-05-01 17:23:30', 1, '2025-05-01 17:23:30', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (151, '新增定时任务按钮', 'add:quartzjob:button', 2, 15, '', null, 1, 1, '2025-05-01 17:30:08', 1, '2025-05-01 17:30:08', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (152, '删除定时任务按钮', 'delete:quartzjob:button', 2, 15, '', null, 2, 1, '2025-05-01 17:30:44', 1, '2025-05-01 17:30:44', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (153, '暂停定时任务按钮', 'pause:quartzjob:button', 2, 15, '', null, 3, 1, '2025-05-01 17:31:52', 1, '2025-05-01 17:31:52', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (154, '恢复定时任务按钮', 'resume:quartzjob:button', 2, 15, '', null, 4, 1, '2025-05-01 17:32:41', 1, '2025-05-01 17:32:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (155, '运行定时任务按钮', 'run:quartzjob:button', 2, 15, '', null, 5, 1, '2025-05-01 17:33:29', 1, '2025-05-01 17:33:29', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (156, '批量删除定时任务日志按钮', 'batch:deletejoblog:button', 2, 16, '', null, 1, 1, '2025-05-01 17:34:31', 1, '2025-05-01 17:34:31', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (157, '删除定时任务按钮', 'delete:joblog:button', 2, 16, '', null, 2, 1, '2025-05-01 17:35:00', 1, '2025-05-01 17:35:00', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (158, '可操作权限按钮', 'manage:permission:button', 2, 4, '', null, 18, 1, '2025-05-01 17:39:00', 1, '2025-05-01 17:39:00', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (159, '恢复表格配置按钮', 'recover:tableconfig:button', 2, 67, '', null, 12, 1, '2025-05-01 18:06:18', 1, '2025-05-01 18:06:18', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (160, '编辑定时任务按钮', 'edit:quartzjob:button', 2, 15, '', null, 6, 1, '2025-05-01 22:55:15', 1, '2025-05-01 22:55:15', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (161, '彻底删除按钮', 'complete:deleteconfig:button', 2, 67, '', null, 13, 1, '2025-05-03 15:14:15', 1, '2025-05-03 15:14:15', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (162, '彻底删除表配置API', 'completelyDelete:config:api', 3, 67, '&#xe665;', '#EE1010', 5, 1, '2025-05-03 15:15:57', 1, '2025-05-10 23:36:48', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (163, '字典搜索按钮', 'search:dict:button', 2, 9, '', null, 6, 1, '2025-05-03 17:03:41', 1, '2025-05-03 17:03:41', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (164, '日志搜索按钮', 'search:log:button', 2, 6, '', null, 2, 1, '2025-05-03 17:09:15', 1, '2025-05-03 17:09:15', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (165, '搜索角色按钮', 'search:role:button', 2, 3, '', null, 5, 1, '2025-05-03 17:20:07', 1, '2025-05-03 17:20:07', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (166, '富文本图片上传API', 'wangEditor:upload:api', 3, 66, '&#xe864;', '#0D914D', 6, 1, '2025-05-03 20:52:49', 1, '2025-05-03 20:53:12', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (168, '富文本框黏贴处理API', 'wangEditor:docontent:api', 3, 66, '&#xe78f;', '#B0E0E6', 7, 1, '2025-05-03 20:54:45', 1, '2025-05-03 20:55:02', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (169, '获取今日实时统计', 'get:dailyStats:api', 3, 1, '', null, 3, 1, '2025-05-07 23:47:04', 1, '2025-05-08 00:17:32', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (170, '获取近几条天访问数据API', 'get:dayTrend:api', 3, 1, '', null, 2, 1, '2025-05-08 00:01:27', 1, '2025-05-08 00:17:46', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (171, '获取年度访问趋势数据API', 'get:monthTrend:api', 3, 1, '', null, 4, 1, '2025-05-08 01:08:27', 1, '2025-05-08 01:08:27', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (172, '首页获取用户系统数据API', 'index:userSystemData:api', 3, 1, '', null, 5, 1, '2025-05-08 19:56:49', 1, '2025-05-08 19:57:42', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (173, '首页获取用户操作日志API', 'index:getUserLog:api', 3, 1, '', null, 6, 1, '2025-05-08 19:57:36', 1, '2025-05-08 19:57:36', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (174, '首页获取最新用户列表API', 'index:newUser:api', 3, 1, '', null, 7, 1, '2025-05-08 19:58:27', 1, '2025-05-08 19:58:27', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (175, '首页用户更多按钮', 'index:usermore:button', 2, 1, '', null, 1, 1, '2025-05-08 22:17:11', 1, '2025-05-08 22:19:28', '', 1);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (176, '用户当前设备API', 'user:currentDevice:api', 3, 2, '', null, 12, 1, '2025-05-09 21:57:21', 1, '2025-05-09 21:57:21', '', 0);
INSERT INTO mysiteforme.sys_permission (id, permission_name, permission_code, permission_type, menu_id, icon, color, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (177, '删除字段配置API', 'delet:fieldConfig:api', 3, 67, '&#xe619;', '#D01C40', 13, 1, '2025-05-10 23:37:40', 1, '2025-05-10 23:38:08', '', 0);


create table if not exists sys_permission_api
(
    id            bigint auto_increment
        primary key,
    permission_id bigint                             not null comment '关联权限表ID',
    api_url       varchar(200)                       not null comment 'API接口URL',
    http_method   varchar(10)                        null comment 'HTTP请求方法',
    common        tinyint  default 0                 null comment '是否为公共接口，默认false',
    sort          smallint                           null comment '排序',
    create_by     bigint                             null comment '创建者',
    create_date   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     bigint                             null comment '更新者',
    update_date   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remarks       varchar(255)                       null comment 'API备注',
    del_flag      tinyint                            null comment '删除标志'
);

create index permission_id_pk
    on sys_permission_api (permission_id);

INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, 2, '/api/admin/user/list', 'GET', 1, 10, 1, '2025-02-14 15:25:05', 1, '2025-04-30 22:20:57', '获取后端列表数据的API地址', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 7, '/api/admin/user/add', 'POST', 0, 11, 1, '2025-02-14 15:25:41', 1, '2025-05-09 17:59:52', '新增用户数据到后端的API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 9, '/api/admin/menu/tree', 'GET', 1, 8, 1, '2025-02-15 21:42:02', 1, '2025-05-09 17:54:19', '权限树接口', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 10, '/api/auth/currentUser', 'GET', 1, 4, 1, '2025-02-17 16:18:15', null, '2025-02-17 16:18:21', '当前用户详细信息API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, 11, '/api/admin/permission/list', 'GET', 1, 5, 1, '2025-03-11 01:09:25', 1, '2025-04-30 22:22:21', '权限列表页数据API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, 12, '/api/admin/permission/add', 'POST', 0, 1, 1, '2025-03-11 01:10:11', 1, '2025-04-12 18:28:11', '权限新增API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, 19, '/api/admin/uploadBaseInfo', 'GET', 0, 3, 1, '2025-03-11 02:43:35', 1, '2025-05-09 14:29:57', null, 1);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, 20, '/api/admin/uploadBaseInfo/add', 'POST', 0, 5, 1, '2025-03-11 02:48:04', 1, '2025-05-09 14:30:04', null, 1);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (9, 21, '/api/admin/user/currentUser', 'GET', 1, 5, 1, '2025-04-06 20:42:49', null, '2025-04-08 13:08:40', '当前用户详情API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (10, 22, '/api/admin/user/currentMenu', 'GET', 1, 6, 1, '2025-04-08 13:08:19', null, '2025-04-08 13:09:05', '当前用户菜单API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (11, 23, '/api/admin/role/userAllRole', 'GET', 1, 1, 1, '2025-04-09 11:48:08', null, '2025-04-09 18:51:11', '当前用户角色列表API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (12, 24, '/api/admin/role/list', 'GET', 1, 7, 1, '2025-04-09 12:05:28', 1, '2025-04-30 22:21:45', '角色分页列表API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (13, 25, '/api/admin/user/detail', 'GET', 1, 9, 1, '2025-04-09 12:20:00', 1, '2025-04-30 22:20:49', '用户详情API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (14, 26, '/api/admin/user/edit', 'PUT', 0, 2, 1, '2025-04-09 15:08:28', 1, '2025-05-09 17:59:37', '编辑用户API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (15, 27, '/api/admin/role/getRoleMenusPers', 'GET', 1, 6, 1, '2025-04-09 22:22:25', 1, '2025-04-30 22:21:23', '角色菜单权限集合API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (16, 28, '/api/admin/menu/add', 'POST', 0, 2, 1, '2025-04-11 00:54:44', 1, '2025-04-12 20:44:09', null, 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (17, 29, '/api/admin/menu/edit', 'PUT', 0, 3, 1, '2025-04-11 00:55:08', 1, '2025-04-12 20:43:41', null, 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (18, 34, '/api/admin/permission/edit', 'PUT', 0, 2, 1, '2025-04-12 02:04:54', 1, '2025-04-12 18:27:39', '编辑权限API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (19, 35, '/api/admin/permission/delete', 'DELETE', 0, 7, 1, '2025-04-12 21:59:49', 1, '2025-04-12 21:59:49', '权限删除API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (20, 36, '/api/admin/menu/delete', 'DELETE', 0, 9, 1, '2025-04-12 22:01:06', 1, '2025-04-12 22:01:06', '权限删除API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (21, 39, '/api/admin/rescource/list', 'GET', 1, 6, 1, '2025-04-13 01:10:40', 1, '2025-05-02 19:06:38', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (22, 40, '/api/admin/log/list', 'GET', 1, 3, 1, '2025-04-13 23:40:10', 1, '2025-04-30 22:22:59', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (23, 41, '/api/admin/log/delete', 'DELETE', 0, 2, 1, '2025-04-13 23:41:00', 1, '2025-05-08 23:53:23', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (24, 43, '/api/admin/uploadBaseInfo/list', 'GET', 0, 1, 1, '2025-04-14 14:44:31', 1, '2025-04-14 14:44:31', '上传基本信息列表页API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (26, 45, '/api/admin/uploadBaseInfo/add', 'POST', 0, 2, 1, '2025-04-14 15:01:10', 1, '2025-05-09 18:14:54', '新增上传信息API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (27, 46, '/api/admin/uploadBaseInfo/edit', 'PUT', 0, 3, 1, '2025-04-14 16:10:00', 1, '2025-05-09 18:05:37', '编辑上传信息API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (28, 47, '/api/admin/uploadBaseInfo/localTestImg', 'GET', 0, 4, 1, '2025-04-14 18:06:40', 1, '2025-05-09 17:57:31', '获取本地上传测试图片API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (29, 48, '/api/admin/file/upload', 'POST', 1, 5, 1, '2025-04-15 00:45:55', 1, '2025-05-03 20:53:20', '图片上传通用API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (30, 49, '/api/admin/dict/list', 'GET', 1, 7, 1, '2025-04-17 20:28:43', 1, '2025-04-30 22:22:49', '字典分页列表API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (31, 50, '/api/admin/dict/add', 'POST', 0, 2, 1, '2025-04-17 20:29:34', 1, '2025-04-17 20:29:34', '添加字段API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (32, 51, '/api/admin/dict/edit', 'PUT', 0, 3, 1, '2025-04-17 20:30:20', 1, '2025-05-08 23:21:24', '编辑字典API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (33, 52, '/api/admin/dict/editType', 'PUT', 0, 4, 1, '2025-04-17 20:31:55', 1, '2025-05-09 18:01:06', '删除字典API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (34, 53, '/api/admin/dict/delete', 'DELETE', 0, 5, 1, '2025-04-17 22:43:15', 1, '2025-04-17 22:43:15', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (35, 54, '/api/admin/site/current', 'GET', 0, 1, 1, '2025-04-17 23:43:32', 1, '2025-04-17 23:43:32', '当前站点API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (36, 55, '/api/admin/site/uploadTypeList', 'GET', 0, 2, 1, '2025-04-18 00:38:46', 1, '2025-04-18 00:38:46', '获取上传信息集合API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (37, 56, '/api/admin/site/edit', 'PUT', 0, 3, 1, '2025-04-18 00:39:28', 1, '2025-04-18 00:39:28', '编辑系统信息API', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (38, 58, '/api/admin/tableConfig/list', 'GET', 0, 1, 1, '2025-04-19 14:14:41', 1, '2025-04-19 14:14:41', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (39, 59, '/api/admin/tableConfig/add', 'POST', 0, 2, 1, '2025-04-19 14:16:26', 1, '2025-04-19 14:16:26', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (40, 60, '/api/admin/tableConfig/edit', 'PUT', 0, 3, 1, '2025-04-19 14:17:30', 1, '2025-04-19 14:17:30', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (41, 61, '/api/admin/tableConfig/delete', 'DELETE', 0, 4, 1, '2025-04-19 14:18:33', 1, '2025-04-19 14:18:33', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (42, 62, '/api/admin/tableConfig/recover', 'POST', 0, 5, 1, '2025-04-19 14:20:46', 1, '2025-05-09 18:05:00', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (43, 63, '/api/admin/tableConfig/getTableNameList', 'GET', 0, 6, 1, '2025-04-19 14:31:50', 1, '2025-04-19 14:49:09', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (44, 64, '/api/admin/tableConfig/getSchemaNameList', 'GET', 0, 7, 1, '2025-04-19 14:49:43', 1, '2025-04-19 14:49:43', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (45, 65, '/api/admin/tableFieldConfig/syncFieldsByTableName', 'POST', 0, 8, 1, '2025-04-19 21:03:48', 1, '2025-05-09 18:04:28', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (46, 66, '/api/admin/tableFieldConfig/updateFieldConfig', 'PUT', 0, 9, 1, '2025-04-21 09:35:00', 1, '2025-05-09 18:04:16', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (47, 67, '/api/admin/dict/getDictTypeList', 'GET', 1, 6, 1, '2025-04-21 16:28:53', 1, '2025-04-30 22:22:41', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (48, 68, '/api/admin/tableFieldConfig/getSimpleTableField', 'GET', 0, 10, 1, '2025-04-21 16:37:57', 1, '2025-04-21 16:37:57', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (49, 69, '/api/admin/tableFieldConfig/list', 'GET', 0, 11, 1, '2025-04-21 20:28:44', 1, '2025-05-03 12:38:06', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (50, 70, '/api/admin/tableFieldConfig/sortFields', 'POST', 0, 12, 1, '2025-04-21 21:28:48', 1, '2025-05-09 18:04:03', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (51, 71, '/api/admin/table/list', 'GET', 0, 1, 1, '2025-04-22 23:47:55', 1, '2025-04-22 23:47:55', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (52, 72, '/api/admin/table/add', 'POST', 0, 2, 1, '2025-04-23 13:40:15', 1, '2025-05-09 18:03:28', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (53, 73, '/api/admin/table/edit', 'PUT', 0, 3, 1, '2025-04-23 13:40:44', 1, '2025-05-09 18:03:19', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (54, 74, '/api/admin/table/delete', 'DELETE', 0, 4, 1, '2025-04-23 13:42:22', 1, '2025-05-09 18:03:02', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (55, 75, '/api/admin/table/getFieldList', 'GET', 0, 5, 1, '2025-04-23 13:42:53', 1, '2025-04-23 13:42:53', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (56, 76, '/api/admin/table/showFields', 'GET', 0, 6, 1, '2025-04-23 13:43:41', 1, '2025-04-23 13:43:41', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (57, 77, '/api/admin/table/addField', 'POST', 0, 7, 1, '2025-04-23 13:44:26', 1, '2025-05-09 18:02:51', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (58, 78, '/api/admin/table/editField', 'PUT', 0, 8, 1, '2025-04-23 13:44:49', 1, '2025-05-09 18:02:37', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (59, 79, '/api/admin/table/fieldIsExist', 'GET', 0, 9, 1, '2025-04-23 13:45:33', 1, '2025-04-30 22:23:28', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (60, 80, '/api/admin/table/deleteField', 'DELETE', 0, 10, 1, '2025-04-23 13:45:59', 1, '2025-05-09 18:02:27', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (61, 81, '/api/admin/redis/list', 'GET', 0, 1, 1, '2025-04-23 18:26:06', 1, '2025-04-23 18:26:06', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (62, 82, '/api/admin/redis/delete', 'DELETE', 0, 2, 1, '2025-04-23 21:03:29', 1, '2025-05-09 18:15:24', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (63, 83, '/api/admin/quartzTask/list', 'GET', 0, 8, 1, '2025-04-24 12:42:10', 1, '2025-04-24 12:51:18', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (64, 84, '/api/admin/quartzTask/add', 'POST', 0, 2, 1, '2025-04-24 12:42:49', 1, '2025-04-24 12:42:49', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (65, 85, '/api/admin/quartzTask/edit', 'PUT', 0, 3, 1, '2025-04-24 12:43:18', 1, '2025-04-24 12:43:18', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (66, 86, '/api/admin/quartzTask/delete', 'DELETE', 0, 4, 1, '2025-04-24 12:44:05', 1, '2025-04-24 12:44:05', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (67, 87, '/api/admin/quartzTask/paush', 'POST', 0, 5, 1, '2025-04-24 12:44:45', 1, '2025-04-24 12:44:45', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (68, 88, '/api/admin/quartzTask/resume', 'POST', 0, 6, 1, '2025-04-24 12:45:16', 1, '2025-04-24 12:45:16', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (69, 89, '/api/admin/quartzTask/run', 'POST', 0, 7, 1, '2025-04-24 12:45:43', 1, '2025-04-24 12:45:43', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (70, 90, '/api/admin/quartzTaskLog/list', 'GET', 0, 1, 1, '2025-04-24 13:04:29', 1, '2025-04-24 13:04:29', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (71, 91, '/api/admin/quartzTaskLog/delete', 'DELETE', 0, 2, 1, '2025-04-25 01:43:51', 1, '2025-04-25 01:43:51', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (72, 92, '/api/admin/role/edit', 'PUT', 0, 2, 1, '2025-04-27 13:28:46', 1, '2025-05-09 00:08:14', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (73, 93, '/api/admin/role/add', 'POST', 0, 3, 1, '2025-04-27 13:29:45', 1, '2025-05-09 18:00:41', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (74, 94, '/api/admin/role/saveRoleMenusPers', 'POST', 0, 4, 1, '2025-04-27 13:30:53', 1, '2025-05-09 18:00:31', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (75, 95, '/api/admin/tableConfig/downloadCode', 'POST', 0, 6, 1, '2025-04-27 22:03:01', 1, '2025-05-10 23:37:02', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (76, 96, '/api/admin/user/assignUserPermission', 'POST', 0, 3, 1, '2025-04-28 13:05:55', 1, '2025-05-09 17:59:25', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (77, 97, '/api/admin/user/userPermission', 'GET', 1, 4, 1, '2025-04-28 13:06:53', 1, '2025-04-28 13:06:53', '根据用户ID获取已经给他单独分配的权限ID集合', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (78, 98, '/api/admin/role/delete', 'DELETE', 0, 5, 1, '2025-04-28 22:59:47', 1, '2025-05-09 18:00:20', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (79, 99, '/api/admin/user/delete', 'DELETE', 0, 5, 1, '2025-04-29 22:13:42', 1, '2025-05-09 17:59:08', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (80, 100, '/api/admin/user/location', 'GET', 1, 6, 1, '2025-04-30 14:50:45', 1, '2025-04-30 14:50:45', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (81, 101, '/api/admin/user/editCurrentUser', 'PUT', 1, 7, 1, '2025-04-30 20:48:34', 1, '2025-05-09 17:58:28', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (82, 102, '/api/admin/user/changePassword', 'POST', 1, 8, 1, '2025-04-30 21:49:10', 1, '2025-05-09 17:58:13', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (83, 162, '/api/admin/tableConfig/completelyDelete', 'DELETE', 0, 5, 1, '2025-05-03 15:15:57', 1, '2025-05-10 23:36:48', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (84, 166, '/api/admin/file/uploadWang', 'POST', 1, 6, 1, '2025-05-03 20:52:49', 1, '2025-05-03 20:53:12', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (85, 168, '/api/admin/file/doContent', 'POST', 1, 7, 1, '2025-05-03 20:54:45', 1, '2025-05-03 20:55:02', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (86, 169, '/api/admin/dailyStats/today', 'GET', 1, 3, 1, '2025-05-07 23:47:04', 1, '2025-05-08 00:17:32', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (87, 170, '/api/admin/dailyStats/day/trend', 'GET', 1, 2, 1, '2025-05-08 00:01:27', 1, '2025-05-08 00:17:46', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (88, 171, '/api/admin/dailyStats/month/trend', 'GET', 1, 4, 1, '2025-05-08 01:08:27', 1, '2025-05-08 01:08:27', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (89, 172, '/api/admin/dailyStats/user/systemData', 'GET', 1, 5, 1, '2025-05-08 19:56:49', 1, '2025-05-08 19:57:42', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (90, 173, '/api/admin/dailyStats/log/list', 'GET', 1, 6, 1, '2025-05-08 19:57:36', 1, '2025-05-08 19:57:36', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (91, 174, '/api/admin/dailyStats/user/list', 'GET', 1, 7, 1, '2025-05-08 19:58:27', 1, '2025-05-08 19:58:27', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (92, 176, '/api/admin/userDevice/userDevices', 'GET', 1, 12, 1, '2025-05-09 21:57:21', 1, '2025-05-09 21:57:21', '', 0);
INSERT INTO mysiteforme.sys_permission_api (id, permission_id, api_url, http_method, common, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (93, 177, '/api/admin/tableFieldConfig/delete', 'DELETE', 0, 13, 1, '2025-05-10 23:37:40', 1, '2025-05-10 23:38:08', '', 0);

create table if not exists sys_permission_button
(
    id            bigint auto_increment
        primary key,
    permission_id bigint                             not null comment '关联权限表ID',
    button_key    varchar(50)                        not null comment '按钮标识',
    button_name   varchar(50)                        not null comment '按钮名称',
    sort          smallint                           null comment '排序',
    create_by     bigint                             null comment '创建者',
    create_date   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     bigint                             null comment '更新者',
    update_date   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remarks       varchar(255)                       null comment '按钮备注',
    del_flag      tinyint                            null comment '删除标志'
);

create index button_permission_id_pk
    on sys_permission_button (permission_id);

INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, 3, 'user_search', '搜索', 8, 1, '2025-02-14 15:27:07', 1, '2025-04-12 18:58:24', '前端搜索按钮权限', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 4, 'user_add', '新增', 6, 1, '2025-02-14 15:27:50', 1, '2025-04-12 18:58:09', '前端用户新增按钮权限', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 5, 'user_edit', '编辑', 8, 1, '2025-02-14 15:28:18', 1, '2025-04-12 18:58:38', '前端用户编辑按钮权限', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 6, 'user_delete', '删除', 3, 1, '2025-02-14 15:28:53', 1, '2025-04-12 17:33:07', '前端用户删除按钮权限', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, 37, 'sub_menu_add', '新增子菜单', 12, 1, '2025-04-12 23:00:57', 1, '2025-05-01 16:20:18', '展示权限列表按钮', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, 38, 'root_menu_add', '增加主菜单API', 13, 1, '2025-04-12 23:50:19', 1, '2025-04-12 23:50:19', '增加主菜单按钮', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, 42, 'log_delete', '日志删除按钮', 1, 1, '2025-04-13 23:43:44', 1, '2025-04-13 23:43:44', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, 104, 'role_add', '新增角色', 1, 1, '2025-05-01 16:04:56', 1, '2025-05-01 16:04:56', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (9, 105, 'role_edit', '编辑角色', 2, 1, '2025-05-01 16:11:29', 1, '2025-05-01 16:11:29', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (10, 106, 'role_delete', '删除角色', 3, 1, '2025-05-01 16:12:59', 1, '2025-05-01 16:12:59', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (11, 107, 'role_assign', '分配权限', 4, 1, '2025-05-01 16:14:52', 1, '2025-05-01 16:14:52', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (12, 108, 'user_assign', '单独分配权限', 9, 1, '2025-05-01 16:15:41', 1, '2025-05-01 16:15:41', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (13, 109, 'menu_delete', '删除菜单', 14, 1, '2025-05-01 16:21:32', 1, '2025-05-01 16:21:32', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (14, 110, 'menu_edit', '编辑菜单', 15, 1, '2025-05-01 16:22:16', 1, '2025-05-01 16:22:16', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (15, 111, 'permission_edit', '权限编辑', 16, 1, '2025-05-01 16:23:10', 1, '2025-05-01 16:23:10', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (16, 112, 'permission_delete', '权限删除', 17, 1, '2025-05-01 16:23:57', 1, '2025-05-01 16:23:57', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (17, 113, 'dict_add', '新增字典', 1, 1, '2025-05-01 16:27:04', 1, '2025-05-01 16:27:04', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (18, 114, 'dict_edit', '编辑字典', 2, 1, '2025-05-01 16:27:35', 1, '2025-05-01 16:27:35', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (19, 115, 'dict_delete', '删除字典', 3, 1, '2025-05-01 16:28:15', 1, '2025-05-01 16:28:15', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (20, 116, 'dict_edit_type', '编辑字典类型', 4, 1, '2025-05-01 16:30:50', 1, '2025-05-01 16:30:50', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (21, 117, 'dict_type_add', '添加该类型字典', 5, 1, '2025-05-01 16:31:47', 1, '2025-05-01 16:31:47', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (22, 119, 'resource_preview', '资源预览', 3, 1, '2025-05-01 16:40:22', 1, '2025-05-02 19:30:55', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (23, 120, 'resource_copylink', '资源复制链接', 2, 1, '2025-05-01 16:41:26', 1, '2025-05-01 16:41:26', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (24, 121, 'table_add', '新增表格', 1, 1, '2025-05-01 16:42:54', 1, '2025-05-01 16:42:54', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (25, 122, 'table_batch_delete', '批量删除表格', 2, 1, '2025-05-01 16:43:57', 1, '2025-05-01 16:51:31', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (26, 123, 'table_edit', '编辑表格', 3, 1, '2025-05-01 16:45:16', 1, '2025-05-01 16:45:16', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (27, 124, 'table_delete', '删除表格', 4, 1, '2025-05-01 16:45:51', 1, '2025-05-01 16:45:51', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (28, 125, 'field_manage', '管理字段', 5, 1, '2025-05-01 16:46:57', 1, '2025-05-01 16:46:57', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (29, 126, 'field_add', '新增字段', 6, 1, '2025-05-01 16:47:48', 1, '2025-05-01 16:47:48', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (30, 127, 'field_edit', '编辑字段', 7, 1, '2025-05-01 16:48:22', 1, '2025-05-01 16:48:22', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (31, 128, 'field_delete', '删除字段', 8, 1, '2025-05-01 16:48:51', 1, '2025-05-01 16:48:51', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (32, 129, 'field_batch_delete', '批量删除字段', 9, 1, '2025-05-01 16:49:44', 1, '2025-05-01 16:51:18', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (33, 130, 'tableconfig_add', '新增表格配置', 1, 1, '2025-05-01 16:53:32', 1, '2025-05-01 16:53:32', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (34, 131, 'tableconfig_batch_delete', '批量删除表格配置', 2, 1, '2025-05-01 16:54:35', 1, '2025-05-01 16:54:35', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (35, 132, 'tableconfig_download', '下载源码', 3, 1, '2025-05-01 16:55:14', 1, '2025-05-01 16:55:14', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (36, 133, 'tableconfig_edit', '编辑表格配置', 4, 1, '2025-05-01 16:55:56', 1, '2025-05-01 16:55:56', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (37, 134, 'tableconfig_delete', '删除表格配置', 5, 1, '2025-05-01 16:56:35', 1, '2025-05-01 16:56:35', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (38, 135, 'tableconfig_sync_field', '同步字段', 6, 1, '2025-05-01 16:57:35', 1, '2025-05-01 16:57:35', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (39, 136, 'tableconfig_config_field', '可配置字段', 7, 1, '2025-05-01 16:59:22', 1, '2025-05-01 16:59:22', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (40, 137, 'tableconfig_field_sort', '字段排序', 8, 1, '2025-05-01 17:00:18', 1, '2025-05-01 17:00:18', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (41, 138, 'tableconfig_perview_form', '预览表单', 9, 1, '2025-05-01 17:01:22', 1, '2025-05-01 17:01:22', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (42, 139, 'tableconfig_edit_field', '编辑字段配置', 10, 1, '2025-05-01 17:02:37', 1, '2025-05-01 17:02:37', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (43, 140, 'tableconfig_field_delete', '删除字段配置', 11, 1, '2025-05-01 17:03:17', 1, '2025-05-01 17:03:17', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (44, 141, 'site_edit', '站点编辑', 1, 1, '2025-05-01 17:04:26', 1, '2025-05-01 17:04:26', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (45, 142, 'site_save', '站点信息保存', 2, 1, '2025-05-01 17:05:48', 1, '2025-05-01 17:05:48', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (46, 143, 'site_cancle', '取消编辑', 3, 1, '2025-05-01 17:06:18', 1, '2025-05-01 17:06:18', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (47, 144, 'uploadtype_add', '新增上传方式', 1, 1, '2025-05-01 17:07:36', 1, '2025-05-01 17:07:36', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (48, 145, 'uploadtype_edit', '编辑上传方式', 2, 1, '2025-05-01 17:08:33', 1, '2025-05-01 17:08:33', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (49, 146, 'uploadtype_enable', '上传方式禁启用', 3, 1, '2025-05-01 17:10:48', 1, '2025-05-01 17:10:48', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (50, 147, 'uploadtype_show_test', '展示测试图片', 4, 1, '2025-05-01 17:13:03', 1, '2025-05-01 17:13:03', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (51, 148, 'redis_batch_delete', '批量删除按钮', 1, 1, '2025-05-01 17:20:09', 1, '2025-05-01 17:20:09', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (52, 149, 'redis_delete', '删除redis缓存', 2, 1, '2025-05-01 17:21:45', 1, '2025-05-01 17:21:45', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (53, 150, 'redis_show_value', 'redis查看值', 3, 1, '2025-05-01 17:23:30', 1, '2025-05-01 17:23:30', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (54, 151, 'quartzjob_add', '新增定时任务', 1, 1, '2025-05-01 17:30:08', 1, '2025-05-01 17:30:08', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (55, 152, 'quartzjob_delete', '删除定时任务', 2, 1, '2025-05-01 17:30:44', 1, '2025-05-01 17:30:44', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (56, 153, 'quartzjob_pause', '暂停定时任务', 3, 1, '2025-05-01 17:31:52', 1, '2025-05-01 17:31:52', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (57, 154, 'quartzjob_resume', '恢复定时任务', 4, 1, '2025-05-01 17:32:41', 1, '2025-05-01 17:32:41', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (58, 155, 'quartzjob_run', '运行定时任务', 5, 1, '2025-05-01 17:33:29', 1, '2025-05-01 17:33:29', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (59, 156, 'joblog_batch_delete', '批量删除定时任务日志', 1, 1, '2025-05-01 17:34:31', 1, '2025-05-01 17:34:31', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (60, 157, 'joblog_delete', '删除定时任务', 2, 1, '2025-05-01 17:35:00', 1, '2025-05-01 17:35:00', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (61, 158, 'permission_manage', '可操作权限', 18, 1, '2025-05-01 17:39:00', 1, '2025-05-01 17:39:00', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (62, 159, 'tableconfig_recover', '恢复表格配置', 12, 1, '2025-05-01 18:06:18', 1, '2025-05-01 18:06:18', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (63, 160, 'quartzjob_edit', '编辑定时任务', 6, 1, '2025-05-01 22:55:15', 1, '2025-05-01 22:55:15', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (64, 161, 'tableconfig_complete_delete', '彻底删除', 13, 1, '2025-05-03 15:14:15', 1, '2025-05-03 15:14:15', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (65, 163, 'dict_search', '字典搜索', 6, 1, '2025-05-03 17:03:41', 1, '2025-05-03 17:03:41', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (66, 164, 'log_search', '日志搜索', 2, 1, '2025-05-03 17:09:15', 1, '2025-05-03 17:09:15', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (67, 165, 'role_search', '搜索角色', 5, 1, '2025-05-03 17:20:07', 1, '2025-05-03 17:20:07', '', 0);
INSERT INTO mysiteforme.sys_permission_button (id, permission_id, button_key, button_name, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (68, 175, 'index_user_more', '首页用户更多', 1, 1, '2025-05-08 22:17:11', 1, '2025-05-08 22:19:28', '', 1);


create table if not exists sys_permission_group
(
    id          bigint auto_increment
        primary key,
    group_code  varchar(50)                        not null comment '分组编码',
    group_name  varchar(100)                       not null comment '分组名称',
    parent_id   bigint                             null comment '父分组ID',
    parent_ids  varchar(2000)                      null comment '父菜单联集',
    sort        int      default 0                 null comment '排序号',
    level       int      default 0                 null comment '层级',
    create_by   bigint                             null comment '创建者',
    create_date datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   bigint                             null comment '更新者',
    update_date datetime default CURRENT_TIMESTAMP null comment '更新时间',
    remarks     varchar(255)                       null comment '分组描述',
    del_flag    tinyint                            null comment '删除标志'
);

INSERT INTO mysiteforme.sys_permission_group (id, group_code, group_name, parent_id, parent_ids, sort, level, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, 'p_user', '用户组', null, null, 0, 0, 1, '2025-02-14 15:09:55', null, '2025-02-14 15:10:33', null, 0);
INSERT INTO mysiteforme.sys_permission_group (id, group_code, group_name, parent_id, parent_ids, sort, level, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 'p_role', '角色组', null, null, 0, 0, 1, '2025-02-14 15:10:30', null, '2025-02-14 15:10:30', null, 0);
INSERT INTO mysiteforme.sys_permission_group (id, group_code, group_name, parent_id, parent_ids, sort, level, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 'p_permission', '权限组', null, null, 0, 0, 1, '2025-02-15 21:51:21', null, '2025-02-15 21:51:21', null, 0);


create table if not exists sys_permission_page
(
    id            bigint auto_increment
        primary key,
    permission_id bigint                             not null comment '关联权限表ID',
    page_url      varchar(200)                       not null comment '页面URL',
    sort          smallint                           null comment '排序',
    create_by     bigint                             null comment '创建者',
    create_date   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     bigint                             null comment '更新者',
    update_date   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remarks       varchar(255)                       null comment '页面备注',
    del_flag      tinyint                            null comment '删除标志'
);

create index page_permission_id_pk
    on sys_permission_page (permission_id);


INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, 1, '/front/user/list', 1, 1, '2025-02-14 15:15:15', null, '2025-02-14 15:30:41', '系统用户页面', 0);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 8, '/front/role/list', 2, 1, '2025-02-14 15:30:37', null, '2025-02-14 15:30:45', '系统角色页面', 0);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 31, 'asdds', 1, 1, '2025-04-12 00:47:06', 1, '2025-04-12 22:02:15', '', 1);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 32, 'dawdwad', 1, 1, '2025-04-12 01:29:12', 1, '2025-04-12 22:01:51', 'sfsefesfdfrbdrbdrb', 1);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, 32, '/aaaaaaaaaaaax', 1, 1, '2025-04-12 16:49:39', 1, '2025-04-12 16:49:39', null, 0);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, 32, '/aaaaaaaaaadscsfc', 1, 1, '2025-04-12 16:50:30', 1, '2025-04-12 16:50:30', null, 0);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, 32, '/ghjgh', 1, 1, '2025-04-12 16:53:43', 1, '2025-04-12 16:53:43', null, 0);
INSERT INTO mysiteforme.sys_permission_page (id, permission_id, page_url, sort, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, 57, '/page/druid', 1, 1, '2025-04-18 16:55:14', 1, '2025-04-18 21:35:37', '', 1);


create table if not exists sys_rescource
(
    id               bigint auto_increment comment '主键'
        primary key,
    file_name        varchar(255) null comment '文件名称',
    source           varchar(255) null comment '来源',
    web_url          varchar(500) null comment '资源网络地址',
    hash             varchar(255) null comment '文件标识',
    file_size        varchar(50)  null comment '文件大小',
    file_type        varchar(255) null comment '文件类型',
    original_net_url text         null,
    create_date      datetime     null comment '创建时间',
    create_by        bigint       null comment '创建人',
    update_date      datetime     null comment '修改时间',
    update_by        bigint       null comment '修改人',
    remarks          varchar(255) null comment '备注',
    del_flag         tinyint      null comment '删除标记'
)
    comment '系统资源' charset = utf8mb3;

INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (29, 'd64aaf61-b3c5-4a06-98d5-63e03d578e7f.png', 'local', '/static/upload/d64aaf61-b3c5-4a06-98d5-63e03d578e7f.png', 'FtV6l-OYlq-pQRZ04Khozsb7xMLA', '13kb', 'image/png', null, '2025-02-12 01:30:58', 1, '2025-02-12 01:30:58', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (30, 'b3940187-4baf-457b-9c65-326c3668dfcf.jpg', 'local', '/static/upload/b3940187-4baf-457b-9c65-326c3668dfcf.jpg', 'Foyb-OM9yBiVEI4VZtHSMsuj9CZf', '0kb', '.jpg', 'https://d00.paixin.com/thumbs/1093689/42768051/staff_1024.jpg', '2025-02-12 01:33:12', 1, '2025-02-12 01:33:12', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (31, '120e965c-17b9-44f0-a7a3-8457fe873442.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'local', '/static/upload/120e965c-17b9-44f0-a7a3-8457fe873442.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'FhjogcwisIrY6nOfRRFxeMTNV6_p', '0kb', '.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2025%2F0211%2F00665edbj00sricij0016d000j600dtg.jpg&thumbnail=660x2147483647&quality=80&type=jpg', '2025-02-12 01:34:26', 1, '2025-02-12 01:34:26', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (32, '530913d9-002f-40fc-974c-eda48ddc7c5b.ico', 'local', '/static/upload/530913d9-002f-40fc-974c-eda48ddc7c5b.ico', 'FtvsctbuJGFzeq_5NppjCW-laS5W', '64kb', 'image/x-icon', null, '2025-02-12 01:35:32', 1, '2025-02-12 01:35:32', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (33, '808e8f98-e902-40c2-b81f-fd1cf44a4ef6.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'local', '/static/upload/808e8f98-e902-40c2-b81f-fd1cf44a4ef6.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'FrJITif0uPlScIJ8yZve81Dt4u4a', '0kb', '.jpg&thumbnail=660x2147483647&quality=80&type=jpg', 'https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2025%2F0211%2F5847f514j00sriz3s00jld000md00elm.jpg&thumbnail=660x2147483647&quality=80&type=jpg', '2025-02-12 01:51:03', 1, '2025-02-12 01:51:03', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (34, '8c61b22e-e1bf-42e2-9c63-56bae0b4b24f.png', 'local', '/static/upload/8c61b22e-e1bf-42e2-9c63-56bae0b4b24f.png', 'Fs-w0P8IARVoi8QWMBNZ3muWzPO5', '26kb', 'image/png', null, '2025-02-12 01:53:58', 1, '2025-02-12 01:53:58', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (35, 'b2fed33a-a929-42c9-8f80-377c228d7da5.png', 'local', '/static/upload/b2fed33a-a929-42c9-8f80-377c228d7da5.png', 'Ftad-rpkKDUSlnv7Lq7AQQg7OtYp', '540kb', 'image/png', null, '2025-02-12 03:39:26', 1, '2025-02-12 03:39:26', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (36, 'bd04bdd3-abde-4973-9f88-860f4c6a9515.gif', 'local', '/static/upload/bd04bdd3-abde-4973-9f88-860f4c6a9515.gif', 'FgdVBz9Jao7NYggIfNeYfGNlPT4N', '0kb', '.gif', 'http://dingyue.ws.126.net/2025/0211/8988d428g00srio8z02hwd000aq0060g.gif', '2025-02-12 03:40:38', 1, '2025-02-12 03:40:38', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (39, '397a26c4-42db-4a03-b837-9b0111031e54.jpg', 'local', 'static/upload/397a26c4-42db-4a03-b837-9b0111031e54.jpg', 'FtvFGfAbP41HWySysW4QmtoUrtCf', '84kb', 'image/jpeg', null, '2025-04-15 22:41:30', 1, '2025-04-15 22:41:30', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (40, '27308fd9-b784-43f6-b907-3af50d3cfe65.png', 'local', 'static/upload/27308fd9-b784-43f6-b907-3af50d3cfe65.png', 'FldCbUtgk7T2oP-f1XRnmYyusZEi', '101kb', 'image/png', null, '2025-04-15 22:41:57', 1, '2025-04-15 22:41:57', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (41, '4dd53790-1cec-454b-bcfc-35f1ecddedf1.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload//4dd53790-1cec-454b-bcfc-35f1ecddedf1.png', 'Fpf-T5voH53QEnWGHdkMWtj_rLVD', '90kb', '.png', null, '2025-04-15 22:57:55', 1, '2025-04-15 22:57:55', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (42, '2ab61693-f630-404c-ae0c-c4bf6d35328a.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/2ab61693-f630-404c-ae0c-c4bf6d35328a.png', 'FnkaEzMY3EkJl_uu9libCv0UUf9k', '98kb', '.png', null, '2025-04-16 00:12:07', 1, '2025-04-16 00:12:07', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (43, 'aef5ab8d-06ce-4a68-bae0-fa6154cb3272.jpg', 'local', 'static/upload/aef5ab8d-06ce-4a68-bae0-fa6154cb3272.jpg', 'FtKl_8kTcHCrtdBDj69NSiNvMsc3', '194kb', 'image/jpeg', null, '2025-04-17 19:14:18', 1, '2025-04-17 19:14:18', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (44, '2e94ef2c-dc7b-4145-b06c-63263a228064.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/2e94ef2c-dc7b-4145-b06c-63263a228064.png', 'FldCbUtgk7T2oP-f1XRnmYyusZEi', '101kb', 'image/png', null, '2025-04-17 19:15:37', 1, '2025-04-17 19:15:37', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (45, '045efb13-80bf-42dd-b1b8-116ebd687db8.jpg', 'oss', 'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/045efb13-80bf-42dd-b1b8-116ebd687db8.jpg', 'FtKl_8kTcHCrtdBDj69NSiNvMsc3', '194kb', 'image/jpeg', null, '2025-04-17 19:58:04', 1, '2025-04-17 19:58:04', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (46, 'f81eb476-600c-4861-adf8-25ad555a8df2.jpg', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/f81eb476-600c-4861-adf8-25ad555a8df2.jpg', 'FtvFGfAbP41HWySysW4QmtoUrtCf', '84kb', 'image/jpeg', null, '2025-04-17 19:59:34', 1, '2025-04-17 19:59:34', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (47, 'a1f5b0aa-df16-4de2-99c3-b4c9f6f24df1.png', 'local', 'static/aaa/a1f5b0aa-df16-4de2-99c3-b4c9f6f24df1.png', 'Fv3FSgGORiOOlc7nKKDxsEYqh0GJ', '55kb', 'image/png', null, '2025-04-17 20:13:10', 1, '2025-04-17 20:13:10', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (48, 'd8aa0bdc-54e1-4a06-93b8-8d1b1676f262.jpg', 'oss', 'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/d8aa0bdc-54e1-4a06-93b8-8d1b1676f262.jpg', 'FtvFGfAbP41HWySysW4QmtoUrtCf', '84kb', 'image/jpeg', null, '2025-04-18 12:22:21', 1, '2025-04-18 12:22:21', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (49, 'e18a05e4-9467-4d22-a9b9-97f41921337d.png', 'oss', 'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/e18a05e4-9467-4d22-a9b9-97f41921337d.png', 'Fk76nSXQpZqOWksy_-Vppwoba08X', '422kb', 'image/png', null, '2025-04-18 12:22:59', 1, '2025-04-18 12:22:59', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (50, '4ec5d1c3-0b74-408a-a019-cc22d8f666f8.png', 'local', 'upload/aaa/4ec5d1c3-0b74-408a-a019-cc22d8f666f8.png', 'Fk8uUFRKb6zeXV7TSbNNzEyX2pH5', '77kb', 'image/png', null, '2025-04-28 21:29:24', 1, '2025-04-28 21:29:24', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (51, 'c2f7cecf-65a6-43bf-b91a-d13023fd564e.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/c2f7cecf-65a6-43bf-b91a-d13023fd564e.png', 'FkclVqwkG3K_YFFa-pljBI1Fw5nj', '408kb', 'image/png', null, '2025-04-28 21:29:55', 1, '2025-04-28 21:29:55', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (52, '330b7b07-f138-4002-b5df-d5fbf7a285fe.png', 'local', 'upload/aaa/330b7b07-f138-4002-b5df-d5fbf7a285fe.png', 'Fpf-T5voH53QEnWGHdkMWtj_rLVD', '90kb', 'image/png', null, '2025-04-28 21:31:21', 1, '2025-04-28 21:31:21', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (53, '9f8bd5d7-f5dc-4f81-bd10-5057ebc35616.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/9f8bd5d7-f5dc-4f81-bd10-5057ebc35616.png', 'Fgp-SUwsVhrvPXpPFMLHwlWcyOxB', '95kb', 'image/png', null, '2025-04-28 21:36:09', 1, '2025-04-28 21:36:09', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (54, 'f4a7d874-0838-4864-961b-63bddb660370.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/f4a7d874-0838-4864-961b-63bddb660370.png', 'Fv3FSgGORiOOlc7nKKDxsEYqh0GJ', '55kb', 'image/png', null, '2025-05-03 20:01:03', 1, '2025-05-03 20:01:03', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (55, 'a286173b-57c1-4b08-9072-8627fd9e8878.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/a286173b-57c1-4b08-9072-8627fd9e8878.png', 'Fk8uUFRKb6zeXV7TSbNNzEyX2pH5', '77kb', 'image/png', null, '2025-05-03 21:45:12', 1, '2025-05-03 21:45:12', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (56, '70ad0523-e837-440d-8af7-59e6de40b57f.png', 'qiniu', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/70ad0523-e837-440d-8af7-59e6de40b57f.png', 'FukuyU2oDzl5VmjRLgtCsYBhYa_s', '93kb', 'image/png', null, '2025-05-03 21:47:01', 1, '2025-05-03 21:47:01', 1, null, 0);
INSERT INTO mysiteforme.sys_rescource (id, file_name, source, web_url, hash, file_size, file_type, original_net_url, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (57, 'd713fa7e-8fce-49bd-8758-509c1d9e192c.png', 'oss', 'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/d713fa7e-8fce-49bd-8758-509c1d9e192c.png', 'FqN9uAaArULmafHHyDw7GOIBxipF', '79kb', 'image/png', null, '2025-05-03 21:50:57', 1, '2025-05-03 21:50:57', 1, null, 0);

create table if not exists sys_role
(
    id          bigint auto_increment
        primary key,
    name        varchar(40)      null comment '角色名称',
    is_default  bit default b'0' null comment '是否默认角色',
    create_date datetime         null,
    create_by   bigint           null,
    update_date datetime         null,
    update_by   bigint           null,
    remarks     varchar(255)     null,
    del_flag    tinyint          null
)
    charset = utf8mb3;

INSERT INTO mysiteforme.sys_role (id, name, is_default, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (1, '仅浏览', true, '2017-11-02 14:19:07', 1, '2025-05-11 22:04:54', 1, '只能仅浏览', 0);
INSERT INTO mysiteforme.sys_role (id, name, is_default, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (2, '系统管理员', false, '2017-11-29 19:36:37', 1, '2025-04-27 13:47:11', 1, '123', 0);
INSERT INTO mysiteforme.sys_role (id, name, is_default, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (3, '测试角色', false, '2025-04-28 14:39:45', 1, '2025-05-11 22:05:39', 1, '测试角色', 1);
INSERT INTO mysiteforme.sys_role (id, name, is_default, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (4, 'aaa', false, '2025-04-28 22:19:59', 1, '2025-04-28 23:08:54', 1, '', 1);
INSERT INTO mysiteforme.sys_role (id, name, is_default, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (5, '依旧测试', false, '2025-05-09 18:19:01', 1, '2025-05-11 22:04:23', 1, '这还是一个测试角色', 1);

create table if not exists sys_role_menu
(
    role_id bigint not null,
    menu_id bigint not null,
    primary key (role_id, menu_id)
)
    charset = utf8mb3;

INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 3);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 4);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 5);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 7);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 8);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 9);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 14);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 15);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 16);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 60);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 66);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 67);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 68);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (1, 80);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 1);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 2);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 3);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 4);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 5);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 6);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 7);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 8);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 9);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 12);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 13);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 60);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 66);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 67);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 68);
INSERT INTO mysiteforme.sys_role_menu (role_id, menu_id) VALUES (2, 80);


create table if not exists sys_role_permission
(
    id            bigint auto_increment
        primary key,
    role_id       bigint                             not null comment '角色ID',
    permission_id bigint                             not null comment '权限ID',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_role_permission
        unique (role_id, permission_id)
);

create index idx_permission_id
    on sys_role_permission (permission_id);

INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180200, 2, 2, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180201, 2, 3, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180202, 2, 4, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180203, 2, 5, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180204, 2, 6, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180205, 2, 7, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180206, 2, 9, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180207, 2, 10, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180208, 2, 12, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180209, 2, 24, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180210, 2, 25, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180211, 2, 26, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180212, 2, 27, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180213, 2, 39, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180214, 2, 40, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180215, 2, 41, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180216, 2, 42, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180217, 2, 43, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180218, 2, 45, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180219, 2, 46, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180220, 2, 47, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180221, 2, 48, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180222, 2, 49, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180223, 2, 50, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180224, 2, 51, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180225, 2, 52, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180226, 2, 53, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180227, 2, 54, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180228, 2, 55, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180229, 2, 56, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180230, 2, 58, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180231, 2, 59, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180232, 2, 60, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180233, 2, 61, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180234, 2, 62, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180235, 2, 63, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180236, 2, 64, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180237, 2, 65, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180238, 2, 66, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180239, 2, 67, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180240, 2, 68, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180241, 2, 69, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180242, 2, 70, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180243, 2, 75, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180244, 2, 76, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180245, 2, 81, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180246, 2, 92, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180247, 2, 93, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180248, 2, 94, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180249, 2, 99, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180250, 2, 100, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180251, 2, 101, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180252, 2, 102, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180253, 2, 108, '2025-05-04 17:39:35');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180482, 1, 128, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180483, 1, 129, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180484, 1, 130, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180485, 1, 3, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180486, 1, 131, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180487, 1, 4, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180488, 1, 132, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180489, 1, 5, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180490, 1, 133, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180491, 1, 6, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180492, 1, 134, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180493, 1, 135, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180494, 1, 136, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180495, 1, 137, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180496, 1, 138, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180497, 1, 139, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180498, 1, 140, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180499, 1, 141, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180500, 1, 142, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180501, 1, 143, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180502, 1, 144, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180503, 1, 145, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180504, 1, 146, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180505, 1, 147, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180506, 1, 150, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180507, 1, 158, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180508, 1, 159, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180509, 1, 161, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180510, 1, 163, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180511, 1, 164, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180512, 1, 37, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180513, 1, 165, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180514, 1, 38, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180515, 1, 42, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180516, 1, 43, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180517, 1, 47, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180518, 1, 54, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180519, 1, 55, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180520, 1, 58, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180521, 1, 63, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180522, 1, 64, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180523, 1, 68, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180524, 1, 69, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180525, 1, 71, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180526, 1, 75, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180527, 1, 76, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180528, 1, 79, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180529, 1, 81, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180530, 1, 83, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180531, 1, 88, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180532, 1, 89, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180533, 1, 95, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180534, 1, 104, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180535, 1, 105, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180536, 1, 106, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180537, 1, 107, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180538, 1, 108, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180539, 1, 109, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180540, 1, 110, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180541, 1, 111, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180542, 1, 112, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180543, 1, 113, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180544, 1, 114, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180545, 1, 115, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180546, 1, 116, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180547, 1, 117, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180548, 1, 119, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180549, 1, 120, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180550, 1, 121, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180551, 1, 122, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180552, 1, 123, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180553, 1, 124, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180554, 1, 125, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180555, 1, 126, '2025-05-11 23:37:13');
INSERT INTO mysiteforme.sys_role_permission (id, role_id, permission_id, create_time) VALUES (180556, 1, 127, '2025-05-11 23:37:13');


create table if not exists sys_site
(
    id               bigint       not null
        primary key,
    name             varchar(255) null,
    url              varchar(500) null comment '系统网址',
    open_message     bit          null comment '是否开放评论',
    is_no_name       bit          null comment '是否匿名评论',
    version          varchar(255) null,
    author           varchar(255) null,
    author_icon      varchar(255) null,
    file_upload_type varchar(255) null,
    weibo            varchar(255) null,
    qq               varchar(255) null,
    git              varchar(255) null,
    github           varchar(255) null,
    phone            varchar(255) null,
    email            varchar(255) null,
    address          varchar(255) null,
    logo             varchar(255) null,
    web_service_key  varchar(200) null comment 'webservicekey',
    server           varchar(255) null,
    my_database      varchar(255) null,
    max_upload       int          null,
    keywords         varchar(255) null,
    description      varchar(255) null,
    powerby          varchar(255) null,
    record           varchar(255) null,
    create_by        bigint       null,
    create_date      datetime     null,
    update_by        bigint       null,
    update_date      datetime     null,
    remarks          text         null,
    del_flag         bit          null
)
    charset = utf8mb3;

INSERT INTO mysiteforme.sys_site (id, name, url, open_message, is_no_name, version, author, author_icon, file_upload_type, weibo, qq, git, github, phone, email, address, logo, web_service_key, server, my_database, max_upload, keywords, description, powerby, record, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, '浪子回头', '暂无', true, false, '1.0', '', 'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/d8aa0bdc-54e1-4a06-93b8-8d1b1676f262.jpg', 'local', 'https://weibo.com/u/2173866382', '111111', 'https://gitee.com/wanglingxiao/', 'https://github.com/wangl1989', '13776055111', '111111111@qq.com', 'avss', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/9f8bd5d7-f5dc-4f81-bd10-5057ebc35616.png', 'EECBZ-AN3L4-SMRUX-K3VVE-GPHCZ-N3FEO', 'Windows', 'mysql', 3, '默认', '网站描述', '', '苏ICP备17063650号', 1, '2017-12-30 22:46:15', 1, '2025-05-06 13:48:45', '<p>北京时间2月11日，2025年WTA1000多哈站女单第二轮，中国一姐郑钦文迎来澳网后首秀，她作为7号种子对阵贾巴尔。郑钦文表现低迷以4-6和2-6连丢两盘，其中首盘挥霍3-1领先优势，最终总分0-2爆冷遭贾巴尔横扫止步次轮，也是生涯首次输给贾巴尔。</p><p><img src=\'https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2025%2F0211%2F5847f514j00sriz3s00jld000md00elm.jpg&thumbnail=660x2147483647&quality=80&type=jpg\' alt=\'\' data-href=\'\' style=\'\'/><img src=\'http://sup683keh.hn-bkt.clouddn.com/base/upload/70ad0523-e837-440d-8af7-59e6de40b57f.png\' alt=\'QQ图片20240627013652.png\' data-href=\'\' style=\'\'/></p><p><img src=\'https://mysiteforme.oss-cn-beijing.aliyuncs.com/upload/d713fa7e-8fce-49bd-8758-509c1d9e192c.png\' alt=\'地图页-区域详情.png\' data-href=\'\' style=\'\'/></p><p><strong>【世界排名】</strong></p><p><strong>郑钦文世界排名第8名，是本站赛事女单7号种子，贾巴尔世界排名第35名。</strong></p><p><strong>【交锋情况】</strong></p><p><strong>双方过往生涯有过两次交锋，郑钦文取得两战全胜战绩。</strong></p><p><br></p><p><strong>【比赛回顾】</strong></p><p><strong>在今年的澳网赛事，作为上届澳网女单亚军的郑钦文，带伤作战总分0-2爆冷输给德国老将西格蒙德。如今郑钦文迎来澳网后首秀，她迎来与贾巴尔的对决。</strong></p><p><strong>本场首盘比赛，郑钦文首局比赛率先破发，贾巴尔立即回破追到1-1，郑钦文连破带保扩大3-1领先优势。贾巴尔连保带破再保发连赢三局比赛，郑钦文第8局保发追到4-4。贾巴尔连保带破再赢两局，郑钦文以4-6被逆转先丢一盘。</strong></p><p><strong>进入第二盘比赛，郑钦文状态依然低迷，被贾巴尔压制连丢三局0-3落后，且已经在比赛中连丢五局比赛。郑钦文第4局保发略微止住颓势，贾巴尔连保带破再赢两局，郑钦文前六局已经1-5落后。郑钦文第6局破发，但贾巴尔随后完成回破，郑钦文以2-6再丢一盘，最终总分0-2爆冷被贾巴尔横扫止步次轮。</strong></p><p><strong> </strong></p><p><br></p><p>&lt;script&gt;alert(1)&lt;/script&gt;</p><p><br></p><p><br></p><p>&lt;html&gt; </p><p style=\'text-align: justify;\'>花灯映月圆，万家共此时！中央广播电视总台《2025年元宵晚会》将于2月12日晚8点档与海内外观众见面。晚会在“欢乐吉祥、喜气洋洋”的总基调中，通过歌曲、舞蹈、相声、小品、戏曲、魔术等各类型的节目，呈上一席兼具传统意蕴与现代活力的团圆家宴，与全球观众共度上元之夜。</p><p style=\'text-align: justify;\'><br></p><p><strong> </strong></p><p style=\'text-align: center;\'><img src=\'https://nimg.ws.126.net/?url=http%3A%2F%2Fcms-bucket.ws.126.net%2F2025%2F0212%2Fea47dc73j00srjs9f00dfc001hc00u0c.jpg&amp;thumbnail=660x2147483647&amp;quality=80&amp;type=jpg\' alt=\'节目单来了！中央广播电视总台《2025年元宵晚会》邀您共度上元夜\' data-href=\'\' style=\'\'></p><p style=\'text-align: justify;\'>此前，美国有线电视新闻网（CNN）2月披露，CIA已向全体员工提出“买断”方案，承诺向主动辞职的员工提供八个月的薪资补偿。此外，美国国家情报总监办公室（ODNI）也已经向雇员提供了名为“分岔路”的提前退休计划，为其持续发放工资至9月30日。据悉，目前已有超过100人选择了该计划。</p><p style=\'text-align: center;\'><img src=\'https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2025%2F0501%2Ff91e4074j00svkj8x0021d000m800etg.jpg&amp;thumbnail=660x2147483647&amp;quality=80&amp;type=jpg\' alt=\'\' data-href=\'\' style=\'\'><br><br></p><p style=\'text-align: justify;\'><br></p><p style=\'text-align: justify;\'>当地时间4月30日，马斯克参加特朗普的内阁会议</p><p><br></p>', false);


create table if not exists sys_table_config
(
    id            bigint auto_increment
        primary key,
    table_name    varchar(200)                       null comment '数据库中的表名',
    table_type    int                                null comment '数据表类型：1. 普通数据表，2.树形结构数据表',
    table_prefix  varchar(100)                       null comment '需要移除的表格前缀',
    schema_name   varchar(200)                       null comment '数据库模式名，如果需要',
    business_name varchar(200)                       null comment '业务名称，如“用户管理”',
    module_name   varchar(255)                       null comment '生成代码所属模块',
    package_name  varchar(255)                       null comment '生成代码的包路径',
    author        varchar(255)                       null comment '作者',
    generate_path varchar(255)                       null comment '代码生成路径',
    options       varchar(255)                       null comment 'JSON格式，存储其他表级别的配置，如是否生成Controller、Service等',
    create_by     bigint                             null,
    create_date   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     bigint                             null,
    update_date   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    remarks       varchar(255)                       null,
    del_flag      bit                                null
);

INSERT INTO mysiteforme.sys_table_config (id, table_name, table_type, table_prefix, schema_name, business_name, module_name, package_name, author, generate_path, options, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 'blog_article', 1, '', 'mysiteforme', '博客文章', '', '', '昵称', '', '', 1, '2025-05-03 15:18:13', 1, '2025-05-03 15:18:13', '博客文章代码配置', false);
INSERT INTO mysiteforme.sys_table_config (id, table_name, table_type, table_prefix, schema_name, business_name, module_name, package_name, author, generate_path, options, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 'analytics_visit_logs', 1, 'analytics_', 'mysiteforme', '访问记录表', '', '', '昵称', '', '', 1, '2025-05-04 20:28:23', 1, '2025-05-06 13:03:30', '访问记录表', false);
INSERT INTO mysiteforme.sys_table_config (id, table_name, table_type, table_prefix, schema_name, business_name, module_name, package_name, author, generate_path, options, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 'analytics_daily_stats', 1, 'analytics_', 'mysiteforme', '统计汇总', '', '', '昵称', '', '', 1, '2025-05-05 21:01:46', 1, '2025-05-05 21:01:46', '统计汇总表(按天)', false);

create table if not exists sys_table_field_config
(
    id                     bigint auto_increment
        primary key,
    table_config_id        bigint                             not null comment '表配置ID',
    column_name            varchar(200)                       null comment '数据库中的字段名',
    business_name          varchar(200)                       null comment '业务名称',
    column_type            varchar(200)                       null comment '数据库字段类型',
    column_comment         varchar(200)                       null comment '原始字段注释，可作为参考或默认描述',
    column_length          bigint                             null comment '字段长度',
    java_type              varchar(200)                       null comment '映射的Java类型，如String, Long, Date',
    java_field_name        varchar(200)                       null comment '映射的Java属性名，如userName',
    is_unique              bit                                null comment '是否唯一',
    is_nullable            bit                                null comment '是否必填',
    is_list_visible        bit                                null comment '是否在列表页显示',
    is_add_visible         bit                                null comment '是否在新增表单页显示',
    is_edit_visible        bit                                null comment '是否在编辑表单中展示',
    is_detail_visible      bit                                null comment '是否在详情页展示',
    is_query_field         bit                                null comment '是否作为查询条件',
    query_type             varchar(200)                       null comment '(查询方式，如 ''='', ''LIKE'', ''BETWEEN'')',
    form_component_type    varchar(200)                       null comment '前端表单组件类型，如 ''Input'', ''Select'', ''DatePicker'', ''Radio'', ''Checkbox'', ''TextArea''',
    associated_table       varchar(200)                       null comment '关联表名称',
    associated_table_field varchar(200)                       null comment '关联表字段',
    associated_type        int                                null comment '关联的类型 1.字典类型  2.关联表名',
    associated_dict_type   varchar(200)                       null comment '如果组件是Select/Radio/Checkbox，关联的字典类型，用于获取选项',
    sort                   int                                null comment '字段在表单/列表中的显示顺序',
    validation_rules       varchar(1000)                      null comment 'JSON格式，存储更复杂的校验规则',
    create_by              bigint                             null,
    create_date            datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by              bigint                             null,
    update_date            datetime default CURRENT_TIMESTAMP null comment '更新时间',
    remarks                varchar(255)                       null,
    del_flag               bit                                null
);

create index table_config_id_pk
    on sys_table_field_config (table_config_id);

INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (65, 2, 'title', '标题', 'varchar', '标题', 255, 'String', 'title', false, false, true, true, true, true, false, null, 'INPUT', null, null, null, null, 11, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (66, 2, 'sub_title', '副标题', 'varchar', '副标题', 255, 'String', 'subTitle', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 5, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (67, 2, 'marks', '摘要', 'varchar', '摘要', 255, 'String', 'marks', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 1, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (68, 2, 'show_pic', '显示图片', 'varchar', '显示图片', 255, 'String', 'showPic', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 6, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (69, 2, 'category', '文章类型', 'varchar', '文章类型', 255, 'String', 'category', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 2, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (70, 2, 'out_link_url', '外链地址', 'varchar', '外链地址', 200, 'String', 'outLinkUrl', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 7, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (71, 2, 'resources', '来源', 'varchar', '来源', 255, 'String', 'resources', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 8, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (72, 2, 'publist_time', '发布时间', 'datetime', '发布时间', null, 'LocalDateTime', 'publistTime', false, true, true, true, true, true, false, null, 'DATETIME_PICKER', null, null, null, null, 3, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (73, 2, 'content', '内容', 'text', '内容', 65535, 'String', 'content', false, false, true, true, true, true, false, null, 'TEXTAREA', null, null, null, null, 4, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (74, 2, 'text', '纯文字文章内容', 'text', '纯文字文章内容', 65535, 'String', 'text', false, true, true, true, true, true, false, null, 'TEXTAREA', null, null, null, null, 9, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (75, 2, 'click', '浏览量', 'int', '浏览量', 10, 'Integer', 'click', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 13, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (76, 2, 'channel_id', '栏目ID', 'bigint', '栏目ID', 19, 'Long', 'channelId', false, false, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 14, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (77, 2, 'sort', '排序值', 'int', '排序值', 10, 'Integer', 'sort', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 15, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (78, 2, 'is_top', '是否置顶', 'bit', '是否置顶', 1, 'Boolean', 'isTop', false, true, true, true, true, true, false, null, 'SWITCH', null, null, null, null, 10, null, 1, '2025-05-03 15:18:21', 1, '2025-05-06 13:11:29', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (79, 2, 'is_recommend', '是否推荐', 'bit', '是否推荐', 1, 'Boolean', 'isRecommend', false, true, true, true, true, true, false, null, 'SWITCH', null, null, null, null, 16, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (80, 2, 'status', '文章状态', 'int', '文章状态', 10, 'Integer', 'status', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 12, null, 1, '2025-05-03 15:18:21', 1, '2025-05-03 16:28:55', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (81, 3, 'user_id', '用户ID', 'bigint', '用户ID，未登录用户为NULL', 19, 'Long', 'userId', false, true, false, false, false, true, false, null, null, null, null, null, null, 5, '', 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', '', false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (82, 3, 'device_id', '设备ID', 'varchar', '设备ID', 100, 'String', 'deviceId', false, false, true, true, true, true, false, null, 'INPUT', null, null, null, null, 8, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (83, 3, 'ip_address', 'IP地址', 'varchar', 'IP地址', 100, 'String', 'ipAddress', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 6, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (84, 3, 'user_agent', '浏览器UA信息', 'varchar', '浏览器UA信息', 100, 'String', 'userAgent', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 2, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (85, 3, 'referrer', '来源页面', 'varchar', '来源页面', 255, 'String', 'referrer', false, true, true, true, true, true, false, null, 'IMAGE_UPLOAD', null, null, null, null, 3, '', 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:12:37', '', false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (86, 3, 'entry_page', '访问页面', 'varchar', '访问页面', 255, 'String', 'entryPage', false, false, true, true, true, true, false, null, 'INPUT', null, null, null, null, 4, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (87, 3, 'visit_time', '访问时间', 'timestamp', '访问时间', null, 'LocalDateTime', 'visitTime', false, false, true, true, true, true, false, null, 'DATETIME_PICKER', null, null, null, null, 7, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (88, 3, 'country', '国家', 'varchar', '国家', 100, 'String', 'country', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 9, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (89, 3, 'region', '地区', 'varchar', '地区', 100, 'String', 'region', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 10, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (90, 3, 'city', '城市', 'varchar', '城市', 100, 'String', 'city', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 12, null, 1, '2025-05-04 20:28:25', 1, '2025-05-11 20:09:01', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (91, 3, 'device_type', '设备类型(desktop/mobile/tablet)', 'varchar', '设备类型(desktop/mobile/tablet)', 20, 'String', 'deviceType', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 11, null, 1, '2025-05-04 20:28:25', 1, '2025-05-10 23:39:52', null, true);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (92, 4, 'stat_date', '统计日期', 'date', '统计日期', null, 'LocalDate', 'statDate', false, true, true, true, true, true, false, null, 'DATE_PICKER', null, null, null, null, 1, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (93, 4, 'total_visits', '总访问量', 'int', '总访问量', 10, 'Integer', 'totalVisits', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 2, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (94, 4, 'unique_visitors', '独立访客数', 'int', '独立访客数', 10, 'Integer', 'uniqueVisitors', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 3, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (95, 4, 'new_users', '新用户', 'int', '新用户', 10, 'Integer', 'newUsers', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 4, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (96, 4, 'total_clicks', '总点击量', 'int', '总点击量', 10, 'Integer', 'totalClicks', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 5, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (97, 4, 'bounce_rate', '跳出率', 'decimal', '跳出率', 5, 'BigDecimal', 'bounceRate', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 6, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (98, 4, 'avg_visit_duration', '平均访问时长(秒)', 'int', '平均访问时长(秒)', 10, 'Integer', 'avgVisitDuration', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 7, null, 1, '2025-05-05 21:01:50', 1, '2025-05-05 21:01:50', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (99, 3, 'login_name', '登录账号', 'varchar', '登录账号', 200, 'String', 'loginName', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 12, null, 1, '2025-05-10 23:26:34', 1, '2025-05-10 23:26:34', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (100, 3, 'nick_name', '用户昵称', 'varchar', '用户昵称', 200, 'String', 'nickName', true, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 13, '', 1, '2025-05-10 23:26:34', 1, '2025-05-11 20:06:07', '', false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (101, 3, 'title', '页面标题', 'varchar', '页面标题', 255, 'String', 'title', false, true, true, true, true, true, true, 'ne', 'INPUT', null, null, null, null, 14, '', 1, '2025-05-10 23:26:34', 1, '2025-05-11 20:05:56', '', false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (102, 3, 'screen_size', '当前页面长宽比', 'varchar', '当前页面长宽比', 255, 'String', 'screenSize', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 15, null, 1, '2025-05-10 23:26:34', 1, '2025-05-10 23:26:34', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (103, 3, 'time_on_page', '上一个页面停留时间(毫秒数)', 'int', '上一个页面停留时间(毫秒数)', 10, 'Integer', 'timeOnPage', false, true, true, true, true, true, false, null, 'INPUT_NUMBER', null, null, null, null, 16, null, 1, '2025-05-10 23:26:34', 1, '2025-05-10 23:26:34', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (104, 3, 'language', '当前页面的语言', 'varchar', '当前页面的语言', 200, 'String', 'language', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 17, null, 1, '2025-05-10 23:26:34', 1, '2025-05-10 23:26:34', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (105, 3, 'province', '省', 'varchar', '省', 100, 'String', 'province', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 18, null, 1, '2025-05-10 23:26:34', 1, '2025-05-10 23:26:34', null, false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (106, 3, 'os', '系统类型', 'varchar', '系统类型', 20, 'String', 'os', false, true, true, true, true, true, false, null, 'INPUT', null, null, null, null, 19, '', 1, '2025-05-10 23:26:34', 1, '2025-05-11 00:57:28', '', false);
INSERT INTO mysiteforme.sys_table_field_config (id, table_config_id, column_name, business_name, column_type, column_comment, column_length, java_type, java_field_name, is_unique, is_nullable, is_list_visible, is_add_visible, is_edit_visible, is_detail_visible, is_query_field, query_type, form_component_type, associated_table, associated_table_field, associated_type, associated_dict_type, sort, validation_rules, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (107, 3, 'browser', '浏览器', 'varchar', '浏览器', 255, 'String', 'browser', false, true, true, true, true, true, false, null, 'SELECT', null, null, 1, 'blog_article_category', 20, '', 1, '2025-05-10 23:26:34', 1, '2025-05-11 00:20:26', '', false);


create table if not exists sys_upload_base_info
(
    id           bigint auto_increment comment '主键'
        primary key,
    type         varchar(100)                       null comment '类型',
    base_path    varchar(255)                       null comment '前缀路径',
    bucket_name  varchar(255)                       null comment 'bucket的目录名称',
    dir          varchar(255)                       null comment '远程文件存储目录',
    access_key   varchar(255)                       null comment 'AccessKey值',
    secret_key   varchar(255)                       null comment 'SecretKey值',
    endpoint     varchar(255)                       null comment '地域',
    region       varchar(255)                       null comment '区域',
    test_access  tinyint  default 0                 null comment '上传测试结果',
    enable       tinyint  default 0                 null comment '是否启用',
    test_web_url varchar(500)                       null comment '测试文件上传地址',
    create_by    bigint                             null,
    create_date  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by    bigint                             null,
    update_date  datetime default CURRENT_TIMESTAMP null comment '更新时间',
    remarks      varchar(255)                       null comment '备注',
    del_flag     tinyint  default 0                 null
);

INSERT INTO mysiteforme.sys_upload_base_info (id, type, base_path, bucket_name, dir, access_key, secret_key, endpoint, region, test_access, enable, test_web_url, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (1, 'local', 'http://localhost:8080/', '', 'aaa/', '', '', '', '', 1, 1, 'upload/aaa/4ec5d1c3-0b74-408a-a019-cc22d8f666f8.png', 1, '2025-04-14 15:05:54', 1, '2025-05-05 04:57:57', '使用服务器本地存储，无需额外配置，适合小型应用', 0);
create table if not exists sys_upload_info
(
    id                bigint auto_increment comment '主键'
        primary key,
    local_window_url  varchar(255) null comment '本地window系统上传路径,input,YES,false,true,false',
    local_linux_url   varchar(255) null comment '本地linux系统上传路径,input,YES,false,true,false',
    qiniu_base_path   varchar(255) null comment '七牛前缀路径,input,YES,false,true,false',
    qiniu_bucket_name varchar(255) null comment '七牛bucket的目录名称,input,YES,false,true,false',
    qiniu_dir         varchar(255) null comment '七牛文件存储目录,input,YES,false,true,false',
    qiniu_access_key  varchar(255) null comment '七牛qiniuAccess值,input,YES,false,true,false',
    qiniu_secret_key  varchar(255) null comment '七牛qiniuKey的值,input,YES,false,true,false',
    qiniu_test_access bit          null comment '七牛上传测试,switch,YES,true,true,false',
    oss_base_path     varchar(255) null comment '阿里云前缀路径,input,YES,false,true,false',
    oss_bucket_name   varchar(255) null comment '阿里云bucket的目录名称,input,YES,false,true,false',
    oss_dir           varchar(255) null comment '阿里云文件上传目录,input,YES,false,true,false',
    oss_key_id        varchar(255) null comment '阿里云ACCESS_KEY_ID值,input,YES,false,true,false',
    oss_key_secret    varchar(255) null comment '阿里云ACCESS_KEY_SECRET,input,YES,false,true,false',
    oss_endpoint      varchar(255) null comment '阿里云ENDPOINT值,input,YES,false,true,false',
    oss_test_access   bit          null comment '阿里云上传测试,switch,YES,true,true,false',
    create_date       datetime     null comment '创建时间',
    create_by         bigint       null comment '创建人',
    update_date       datetime     null comment '修改时间',
    update_by         bigint       null comment '修改人',
    remarks           varchar(255) null comment '备注',
    del_flag          tinyint      null comment '删除标记'
)
    comment '文件上传配置' charset = utf8mb3;

create table if not exists sys_user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    login_name  varchar(36)   null comment '登录名',
    nick_name   varchar(40)   null comment '昵称',
    icon        varchar(2000) null,
    password    varchar(100)  null comment '密码',
    salt        varchar(40)   null comment 'shiro加密盐',
    tel         varchar(11)   null comment '手机号码',
    email       varchar(200)  null comment '邮箱地址',
    locked      tinyint       null comment '是否锁定',
    location    varchar(200)  null comment '位置信息',
    create_date datetime      null,
    create_by   bigint        null,
    update_date datetime      null,
    update_by   bigint        null,
    remarks     varchar(255)  null,
    del_flag    tinyint       null
)
    charset = utf8mb3;

INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (1, 'test', '超级管理员', 'http://sup683keh.hn-bkt.clouddn.com/base/upload/f4a7d874-0838-4864-961b-63bddb660370.png', '$2a$10$FPnBPlXZY1qqs7T/htZzqukLGve7QGqJNEUkh16tf56LaDri/BnGm', '3fb62b5aeede1bbf', '13776055177', '111@qq.com', 0, '江苏省南通市如皋市', '2017-11-27 21:19:39', 1, '2025-05-12 12:20:59', 1, '121313121313hhhhhh1212', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (2, '22oo00', '我是测试员工', '/static/upload/0f02f26e-ec05-4bd3-94c6-3f48d5d6f614.png', '$2a$10$1XhANfIJwUCsDjmk6tXo/OhIoIYtVlpDYLm68AMhOXcnAWedqaJkG', '3fb62b5aeede1bbf', '13776055179', 'b21313@qq.com', 0, null, '2017-11-27 22:19:39', 1, '2025-04-30 22:19:19', 1, 'ssss', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (3, 'admin', 'admin', '', '$2a$10$e2fcApnMnhOdulkdRRNAdeuDfPnKBCMdBZwwkiX3BKycpqFrT1cea', null, '13776815145', 'weizhengzs2025@163.com', 0, null, '2025-04-09 20:28:12', 1, '2025-05-11 22:05:19', 1, 'dfvdgfnfn', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (4, 'todocat', '用户名', '', '$2a$10$bmLaNgVSdc3Go8fhALGQ/eRMh0ZV/8g8u5oTc8qy5a/eVm1g88uLy', null, '13771055231', '222222@qq.com', 0, null, '2025-04-09 20:30:54', 1, '2025-05-12 12:21:43', 1, 'gfhbfgh', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (11, 'o22oo00', 'dfxgvd', '', '$2a$10$GyYrVgAAUJhAkT.VFsIUgugZ8ctM5iHF3OyCA9KvdohnFSWW.ZkQC', null, '', '1115784675@qq.com', 0, '江苏省南通市', '2025-04-29 22:24:11', null, '2025-05-11 23:28:00', 11, '', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (12, 'ooo2wan', '心愿单', '', '$2a$10$ykKMkjmdLHo1tMxW9XxsH.BNMl8QvNA9xev5wM9mgjzOpo6dijkSW', null, '', '', 0, '江苏省南通市', '2025-04-30 18:36:16', 1, '2025-05-11 22:05:30', 1, '按时到场', 0);
INSERT INTO mysiteforme.sys_user (id, login_name, nick_name, icon, password, salt, tel, email, locked, location, create_date, create_by, update_date, update_by, remarks, del_flag) VALUES (13, 'wangliang', '用户wangliang', '', '$2a$10$vxtjNFZ9OOp.idn3Bl6XYeAQomISaP8sLcVHmL1M7itkOprVelPm2', null, '', 'neyeco3012@exitings.com', 0, '江苏省南通市', '2025-04-30 18:41:03', null, '2025-05-11 23:18:39', 1, '', 0);


create table if not exists sys_user_device
(
    id              bigint auto_increment
        primary key,
    user_id         bigint                             not null comment '用户ID',
    device_id       varchar(100)                       not null comment '设备ID',
    user_agent      varchar(200)                       null comment '用户设备信息',
    last_login_ip   varchar(50)                        null comment '最后登录IP',
    this_login_ip   varchar(50)                        null comment '当前登录IP',
    last_login_time timestamp                          null on update CURRENT_TIMESTAMP comment '上次登录时间',
    this_login_time timestamp                          null on update CURRENT_TIMESTAMP comment '此次登录时间',
    login_out_date  timestamp                          null comment '登出时间',
    first_seen      timestamp                          null on update CURRENT_TIMESTAMP comment '首次出现时间',
    last_seen       timestamp                          null on update CURRENT_TIMESTAMP comment '最后活跃时间',
    create_by       bigint                             null comment '创建者',
    create_date     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by       bigint                             null comment '更新者',
    update_date     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    remarks         varchar(255)                       null comment '备注',
    del_flag        tinyint                            null comment '删除标志'
)
    comment '用户设备表' charset = utf8mb4;

create index idx_device_id
    on sys_user_device (device_id);

create index idx_user_device
    on sys_user_device (user_id, device_id);

create index idx_user_id
    on sys_user_device (user_id);

INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (2, 1, '5196f0043226764076687ceefe1c3a0f', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36', '121.226.41.227', '121.226.41.227', '2025-05-05 17:38:03', '2025-05-05 19:50:44', null, '2025-05-05 04:44:21', '2025-05-06 01:32:49', 1, '2025-05-05 04:14:36', 1, '2025-05-06 01:32:49', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (3, 2, '2fde7360aa59f6869a6a51047b624242', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0', '121.226.41.227', '121.226.41.227', '2025-05-05 05:04:05', '2025-05-05 05:05:20', null, '2025-05-05 05:04:05', '2025-05-05 05:05:20', 2, '2025-05-05 05:04:05', 2, '2025-05-05 05:05:20', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (4, 1, 'af8e8ad5d73551d153e9becc6275a53f', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36', '121.226.41.101', '121.226.41.101', '2025-05-12 03:00:17', '2025-05-12 12:28:39', null, '2025-05-06 11:48:31', '2025-05-12 13:30:59', 1, '2025-05-06 11:48:31', 1, '2025-05-12 13:30:59', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (5, 1, '9631ac09365f296dc8269339ce938a48', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36', '121.226.41.227', '121.226.41.101', '2025-05-07 20:57:40', '2025-05-11 23:45:51', null, '2025-05-07 20:55:33', '2025-05-11 23:45:53', 1, '2025-05-07 20:55:33', 1, '2025-05-11 23:45:53', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (6, 2, 'af8e8ad5d73551d153e9becc6275a53f', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36', '39.144.155.49', '39.144.155.49', '2025-05-08 22:29:11', '2025-05-08 22:46:48', null, '2025-05-08 22:29:11', '2025-05-08 23:16:11', 2, '2025-05-08 22:29:11', 2, '2025-05-08 23:16:11', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (7, 1, '2fde7360aa59f6869a6a51047b624242', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0', '223.104.154.46', '121.226.41.101', '2025-05-09 22:37:06', '2025-05-12 02:31:46', null, '2025-05-09 22:37:06', '2025-05-12 02:31:48', 1, '2025-05-09 22:37:06', 1, '2025-05-12 02:31:48', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (8, 11, 'af8e8ad5d73551d153e9becc6275a53f', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36', '121.226.41.101', '121.226.41.101', '2025-05-11 23:24:16', '2025-05-11 23:37:52', null, '2025-05-11 23:24:16', '2025-05-11 23:37:54', 11, '2025-05-11 23:24:16', 11, '2025-05-11 23:37:54', null, 0);
INSERT INTO mysiteforme.sys_user_device (id, user_id, device_id, user_agent, last_login_ip, this_login_ip, last_login_time, this_login_time, login_out_date, first_seen, last_seen, create_by, create_date, update_by, update_date, remarks, del_flag) VALUES (9, 11, '2fde7360aa59f6869a6a51047b624242', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0', null, '121.226.41.101', '2025-05-12 02:27:38', '2025-05-12 02:27:37', null, '2025-05-12 02:27:37', '2025-05-12 02:27:38', 11, '2025-05-12 02:27:37', 11, '2025-05-12 02:27:39', null, 0);

create table if not exists sys_user_permission
(
    id            bigint auto_increment
        primary key,
    user_id       bigint                             not null comment '用户ID',
    permission_id bigint                             not null comment '权限ID',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_user_permission
        unique (user_id, permission_id)
);

create index idx_permission_id
    on sys_user_permission (permission_id);

create table if not exists sys_user_role
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
)
    charset = utf8mb3;

INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (3, 1);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (4, 1);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (11, 1);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (12, 1);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (12, 2);
INSERT INTO mysiteforme.sys_user_role (user_id, role_id) VALUES (13, 1);
