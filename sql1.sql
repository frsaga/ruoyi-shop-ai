-- Schema-only public edition: original row data and seed accounts removed.
create table gen_table
(
    table_id          bigint auto_increment comment '编号'
        primary key,
    table_name        varchar(200) default ''     null comment '表名称',
    table_comment     varchar(500) default ''     null comment '表描述',
    sub_table_name    varchar(64)                 null comment '关联子表的表名',
    sub_table_fk_name varchar(64)                 null comment '子表关联的外键名',
    class_name        varchar(100) default ''     null comment '实体类名称',
    tpl_category      varchar(200) default 'crud' null comment '使用的模板（crud单表操作 tree树表操作）',
    tpl_web_type      varchar(30)  default ''     null comment '前端模板类型（element-ui模版 element-plus模版）',
    package_name      varchar(100)                null comment '生成包路径',
    module_name       varchar(30)                 null comment '生成模块名',
    business_name     varchar(30)                 null comment '生成业务名',
    function_name     varchar(50)                 null comment '生成功能名',
    function_author   varchar(50)                 null comment '生成功能作者',
    gen_type          char         default '0'    null comment '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path          varchar(200) default '/'    null comment '生成路径（不填默认项目路径）',
    options           varchar(1000)               null comment '其它生成选项',
    create_by         varchar(64)  default ''     null comment '创建者',
    create_time       datetime                    null comment '创建时间',
    update_by         varchar(64)  default ''     null comment '更新者',
    update_time       datetime                    null comment '更新时间',
    remark            varchar(500)                null comment '备注'
)
    comment '代码生成业务表';


create table gen_table_column
(
    column_id      bigint auto_increment comment '编号'
        primary key,
    table_id       bigint                    null comment '归属表编号',
    column_name    varchar(200)              null comment '列名称',
    column_comment varchar(500)              null comment '列描述',
    column_type    varchar(100)              null comment '列类型',
    java_type      varchar(500)              null comment 'JAVA类型',
    java_field     varchar(200)              null comment 'JAVA字段名',
    is_pk          char                      null comment '是否主键（1是）',
    is_increment   char                      null comment '是否自增（1是）',
    is_required    char                      null comment '是否必填（1是）',
    is_insert      char                      null comment '是否为插入字段（1是）',
    is_edit        char                      null comment '是否编辑字段（1是）',
    is_list        char                      null comment '是否列表字段（1是）',
    is_query       char                      null comment '是否查询字段（1是）',
    query_type     varchar(200) default 'EQ' null comment '查询方式（等于、不等于、大于、小于、范围）',
    html_type      varchar(200)              null comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
    dict_type      varchar(200) default ''   null comment '字典类型',
    sort           int                       null comment '排序',
    create_by      varchar(64)  default ''   null comment '创建者',
    create_time    datetime                  null comment '创建时间',
    update_by      varchar(64)  default ''   null comment '更新者',
    update_time    datetime                  null comment '更新时间'
)
    comment '代码生成业务表字段';


create table qrtz_calendars
(
    sched_name    varchar(120) not null comment '调度名称',
    calendar_name varchar(200) not null comment '日历名称',
    calendar      blob         not null comment '存放持久化calendar对象',
    primary key (sched_name, calendar_name)
)
    comment '日历信息表';


create table qrtz_fired_triggers
(
    sched_name        varchar(120) not null comment '调度名称',
    entry_id          varchar(95)  not null comment '调度器实例id',
    trigger_name      varchar(200) not null comment 'qrtz_triggers表trigger_name的外键',
    trigger_group     varchar(200) not null comment 'qrtz_triggers表trigger_group的外键',
    instance_name     varchar(200) not null comment '调度器实例名',
    fired_time        bigint       not null comment '触发的时间',
    sched_time        bigint       not null comment '定时器制定的时间',
    priority          int          not null comment '优先级',
    state             varchar(16)  not null comment '状态',
    job_name          varchar(200) null comment '任务名称',
    job_group         varchar(200) null comment '任务组名',
    is_nonconcurrent  varchar(1)   null comment '是否并发',
    requests_recovery varchar(1)   null comment '是否接受恢复执行',
    primary key (sched_name, entry_id)
)
    comment '已触发的触发器表';


create table qrtz_job_details
(
    sched_name        varchar(120) not null comment '调度名称',
    job_name          varchar(200) not null comment '任务名称',
    job_group         varchar(200) not null comment '任务组名',
    description       varchar(250) null comment '相关介绍',
    job_class_name    varchar(250) not null comment '执行任务类名称',
    is_durable        varchar(1)   not null comment '是否持久化',
    is_nonconcurrent  varchar(1)   not null comment '是否并发',
    is_update_data    varchar(1)   not null comment '是否更新数据',
    requests_recovery varchar(1)   not null comment '是否接受恢复执行',
    job_data          blob         null comment '存放持久化job对象',
    primary key (sched_name, job_name, job_group)
)
    comment '任务详细信息表';


create table qrtz_locks
(
    sched_name varchar(120) not null comment '调度名称',
    lock_name  varchar(40)  not null comment '悲观锁名称',
    primary key (sched_name, lock_name)
)
    comment '存储的悲观锁信息表';


create table qrtz_paused_trigger_grps
(
    sched_name    varchar(120) not null comment '调度名称',
    trigger_group varchar(200) not null comment 'qrtz_triggers表trigger_group的外键',
    primary key (sched_name, trigger_group)
)
    comment '暂停的触发器表';


create table qrtz_scheduler_state
(
    sched_name        varchar(120) not null comment '调度名称',
    instance_name     varchar(200) not null comment '实例名称',
    last_checkin_time bigint       not null comment '上次检查时间',
    checkin_interval  bigint       not null comment '检查间隔时间',
    primary key (sched_name, instance_name)
)
    comment '调度器状态表';


create table qrtz_triggers
(
    sched_name     varchar(120) not null comment '调度名称',
    trigger_name   varchar(200) not null comment '触发器的名字',
    trigger_group  varchar(200) not null comment '触发器所属组的名字',
    job_name       varchar(200) not null comment 'qrtz_job_details表job_name的外键',
    job_group      varchar(200) not null comment 'qrtz_job_details表job_group的外键',
    description    varchar(250) null comment '相关介绍',
    next_fire_time bigint       null comment '上一次触发时间（毫秒）',
    prev_fire_time bigint       null comment '下一次触发时间（默认为-1表示不触发）',
    priority       int          null comment '优先级',
    trigger_state  varchar(16)  not null comment '触发器状态',
    trigger_type   varchar(8)   not null comment '触发器的类型',
    start_time     bigint       not null comment '开始时间',
    end_time       bigint       null comment '结束时间',
    calendar_name  varchar(200) null comment '日程表名称',
    misfire_instr  smallint     null comment '补偿执行的策略',
    job_data       blob         null comment '存放持久化job对象',
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_triggers_ibfk_1
        foreign key (sched_name, job_name, job_group) references qrtz_job_details (sched_name, job_name, job_group)
)
    comment '触发器详细信息表';


create table qrtz_blob_triggers
(
    sched_name    varchar(120) not null comment '调度名称',
    trigger_name  varchar(200) not null comment 'qrtz_triggers表trigger_name的外键',
    trigger_group varchar(200) not null comment 'qrtz_triggers表trigger_group的外键',
    blob_data     blob         null comment '存放持久化Trigger对象',
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_blob_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
)
    comment 'Blob类型的触发器表';


create table qrtz_cron_triggers
(
    sched_name      varchar(120) not null comment '调度名称',
    trigger_name    varchar(200) not null comment 'qrtz_triggers表trigger_name的外键',
    trigger_group   varchar(200) not null comment 'qrtz_triggers表trigger_group的外键',
    cron_expression varchar(200) not null comment 'cron表达式',
    time_zone_id    varchar(80)  null comment '时区',
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_cron_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
)
    comment 'Cron类型的触发器表';


create table qrtz_simple_triggers
(
    sched_name      varchar(120) not null comment '调度名称',
    trigger_name    varchar(200) not null comment 'qrtz_triggers表trigger_name的外键',
    trigger_group   varchar(200) not null comment 'qrtz_triggers表trigger_group的外键',
    repeat_count    bigint       not null comment '重复的次数统计',
    repeat_interval bigint       not null comment '重复的间隔时间',
    times_triggered bigint       not null comment '已经触发的次数',
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simple_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
)
    comment '简单触发器的信息表';


create table qrtz_simprop_triggers
(
    sched_name    varchar(120)   not null comment '调度名称',
    trigger_name  varchar(200)   not null comment 'qrtz_triggers表trigger_name的外键',
    trigger_group varchar(200)   not null comment 'qrtz_triggers表trigger_group的外键',
    str_prop_1    varchar(512)   null comment 'String类型的trigger的第一个参数',
    str_prop_2    varchar(512)   null comment 'String类型的trigger的第二个参数',
    str_prop_3    varchar(512)   null comment 'String类型的trigger的第三个参数',
    int_prop_1    int            null comment 'int类型的trigger的第一个参数',
    int_prop_2    int            null comment 'int类型的trigger的第二个参数',
    long_prop_1   bigint         null comment 'long类型的trigger的第一个参数',
    long_prop_2   bigint         null comment 'long类型的trigger的第二个参数',
    dec_prop_1    decimal(13, 4) null comment 'decimal类型的trigger的第一个参数',
    dec_prop_2    decimal(13, 4) null comment 'decimal类型的trigger的第二个参数',
    bool_prop_1   varchar(1)     null comment 'Boolean类型的trigger的第一个参数',
    bool_prop_2   varchar(1)     null comment 'Boolean类型的trigger的第二个参数',
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simprop_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
)
    comment '同步机制的行锁表';


create index sched_name
    on qrtz_triggers (sched_name, job_name, job_group);


create table shop_address
(
    address_id     bigint auto_increment comment '地址ID'
        primary key,
    user_id        bigint                  not null comment '用户ID',
    receiver_name  varchar(50)             not null comment '收货人姓名',
    receiver_phone varchar(20)             not null comment '收货电话',
    province       varchar(50) default ''  null comment '省份',
    city           varchar(50) default ''  null comment '城市',
    district       varchar(50) default ''  null comment '区县',
    address        varchar(200)            not null comment '详细地址',
    is_default     char        default '0' null comment '是否默认（0否 1是）',
    del_flag       char        default '0' null comment '删除标志（0存在 2删除）',
    create_time    datetime                null comment '创建时间',
    update_time    datetime                null comment '更新时间'
)
    comment '收货地址表';


create index idx_user_id
    on shop_address (user_id);


create table shop_cart
(
    cart_id     bigint auto_increment comment '购物车ID'
        primary key,
    user_id     bigint           not null comment '用户ID',
    product_id  bigint           not null comment '商品ID',
    quantity    int  default 1   null comment '数量',
    checked     char default '1' null comment '是否选中（0否 1是）',
    create_time datetime         null comment '创建时间',
    update_time datetime         null comment '更新时间',
    constraint uk_user_product
        unique (user_id, product_id)
)
    comment '购物车表';


create index idx_product_id
    on shop_cart (product_id);


create index idx_user_id
    on shop_cart (user_id);


create table shop_category
(
    category_id    bigint auto_increment comment '分类ID'
        primary key,
    parent_id      bigint       default 0   null comment '父分类ID',
    category_name  varchar(50)              not null comment '分类名称',
    category_level int          default 1   null comment '分类层级',
    sort_order     int          default 0   null comment '排序',
    category_icon  varchar(200) default ''  null comment '分类图标',
    status         char         default '0' null comment '状态（0正常 1停用）',
    del_flag       char         default '0' null comment '删除标志（0存在 2删除）',
    create_by      varchar(64)  default ''  null comment '创建者',
    create_time    datetime                 null comment '创建时间',
    update_by      varchar(64)  default ''  null comment '更新者',
    update_time    datetime                 null comment '更新时间',
    remark         varchar(500)             null comment '备注'
)
    comment '商品分类表';


create table shop_chat
(
    chat_id      bigint auto_increment comment '聊天ID'
        primary key,
    buyer_id     bigint           not null comment '买家ID',
    seller_id    bigint           not null comment '卖家ID',
    store_id     bigint           null comment '店铺ID（可选，关联到具体店铺）',
    order_id     bigint           null comment '订单ID（可选，关联到具体订单）',
    message_type char default '0' null comment '消息类型（0文本 1图片 2系统消息）',
    content      text             null comment '消息内容',
    sender_type  char             not null comment '发送者类型（0买家 1卖家）',
    sender_id    bigint           not null comment '发送者ID',
    is_read      char default '0' null comment '是否已读（0未读 1已读）',
    del_flag     char default '0' null comment '删除标志（0存在 2删除）',
    create_time  datetime         null comment '创建时间',
    update_time  datetime         null comment '更新时间'
)
    comment '客服聊天记录表';


create index idx_buyer_id
    on shop_chat (buyer_id);


create index idx_create_time
    on shop_chat (create_time);


create index idx_order_id
    on shop_chat (order_id);


create index idx_seller_id
    on shop_chat (seller_id);


create index idx_store_id
    on shop_chat (store_id);


create table shop_favorite
(
    favorite_id bigint auto_increment comment '收藏ID'
        primary key,
    user_id     bigint   not null comment '用户ID',
    product_id  bigint   not null comment '商品ID',
    create_time datetime null comment '创建时间',
    constraint uk_user_product
        unique (user_id, product_id)
)
    comment '商品收藏表';


create index idx_product_id
    on shop_favorite (product_id);


create index idx_user_id
    on shop_favorite (user_id);


create table shop_order
(
    order_id          bigint auto_increment comment '订单ID'
        primary key,
    order_no          varchar(32)                 not null comment '订单号',
    user_id           bigint                      not null comment '用户ID',
    store_id          bigint                      not null comment '店铺ID',
    receiver_name     varchar(50)                 not null comment '收货人姓名',
    receiver_phone    varchar(20)                 not null comment '收货电话',
    receiver_province varchar(50)    default ''   null comment '省份',
    receiver_city     varchar(50)    default ''   null comment '城市',
    receiver_district varchar(50)    default ''   null comment '区县',
    receiver_address  varchar(200)                not null comment '详细地址',
    total_amount      decimal(20, 2)              not null comment '订单总金额',
    payment_amount    decimal(20, 2) default 0.00 null comment '实付金额',
    freight           decimal(20, 2) default 0.00 null comment '运费',
    payment_type      int            default 1    null comment '支付方式（1在线支付 2货到付款）',
    payment_time      datetime                    null comment '支付时间',
    send_time         datetime                    null comment '发货时间',
    end_time          datetime                    null comment '完成时间',
    close_time        datetime                    null comment '关闭时间',
    status            int            default 0    null comment '订单状态（0待支付 1已支付 2已发货 3已完成 4已关闭 5已取消）',
    del_flag          char           default '0'  null comment '删除标志（0存在 2删除）',
    create_by         varchar(64)    default ''   null comment '创建者',
    create_time       datetime                    null comment '创建时间',
    update_by         varchar(64)    default ''   null comment '更新者',
    update_time       datetime                    null comment '更新时间',
    remark            varchar(500)                null comment '备注',
    constraint uk_order_no
        unique (order_no)
)
    comment '订单表';


create index idx_status
    on shop_order (status);


create index idx_store_id
    on shop_order (store_id);


create index idx_user_id
    on shop_order (user_id);


create table shop_order_item
(
    order_item_id bigint auto_increment comment '订单明细ID'
        primary key,
    order_id      bigint                  not null comment '订单ID',
    product_id    bigint                  not null comment '商品ID',
    product_name  varchar(200)            not null comment '商品名称',
    product_image varchar(500) default '' null comment '商品图片',
    current_price decimal(20, 2)          not null comment '当前价格',
    quantity      int                     not null comment '数量',
    total_price   decimal(20, 2)          not null comment '小计',
    create_time   datetime                null comment '创建时间',
    update_time   datetime                null comment '更新时间'
)
    comment '订单明细表';


create index idx_order_id
    on shop_order_item (order_id);


create index idx_product_id
    on shop_order_item (product_id);


create table shop_product
(
    product_id       bigint auto_increment comment '商品ID'
        primary key,
    store_id         bigint                   not null comment '店铺ID',
    category_id      bigint                   not null comment '分类ID',
    product_name     varchar(200)             not null comment '商品名称',
    product_subtitle varchar(200) default ''  null comment '商品副标题',
    main_image       varchar(500) default ''  null comment '主图',
    sub_images       text                     null comment '商品图片（多张，逗号分隔）',
    detail           text                     null comment '商品详情',
    price            decimal(20, 2)           not null comment '价格',
    stock            int          default 0   null comment '库存数量',
    sales_count      int          default 0   null comment '销量',
    status           char         default '0' null comment '状态（0待审核 1在售 2下架）',
    is_recommended   char         default '0' null comment '是否推荐（0否 1是）',
    del_flag         char         default '0' null comment '删除标志（0存在 2删除）',
    create_by        varchar(64)  default ''  null comment '创建者',
    create_time      datetime                 null comment '创建时间',
    update_by        varchar(64)  default ''  null comment '更新者',
    update_time      datetime                 null comment '更新时间',
    remark           varchar(500)             null comment '备注'
)
    comment '商品表';


create index idx_category_id
    on shop_product (category_id);


create index idx_store_id
    on shop_product (store_id);


create table shop_review
(
    review_id   bigint auto_increment comment '评价ID'
        primary key,
    product_id  bigint                    not null comment '商品ID',
    order_id    bigint                    not null comment '订单ID',
    user_id     bigint                    not null comment '用户ID',
    rating      int           default 5   null comment '评分（1-5星）',
    content     varchar(500)  default ''  null comment '评价内容',
    images      varchar(1000) default ''  null comment '评价图片',
    status      char          default '0' null comment '状态（0待审核 1已发布 2已屏蔽）',
    del_flag    char          default '0' null comment '删除标志（0存在 2删除）',
    create_time datetime                  null comment '创建时间',
    update_time datetime                  null comment '更新时间'
)
    comment '商品评价表';


create index idx_order_id
    on shop_review (order_id);


create index idx_product_id
    on shop_review (product_id);


create index idx_user_id
    on shop_review (user_id);


create table shop_store
(
    store_id          bigint auto_increment comment '店铺ID'
        primary key,
    user_id           bigint                    not null comment '店主用户ID',
    store_name        varchar(100)              not null comment '店铺名称',
    store_logo        varchar(200)  default ''  null comment '店铺Logo',
    store_description varchar(500)  default ''  null comment '店铺描述',
    store_address     varchar(200)  default ''  null comment '店铺地址',
    store_phone       varchar(20)   default ''  null comment '联系电话',
    business_license  varchar(200)  default ''  null comment '营业执照',
    status            char          default '0' null comment '状态（0待审核 1正常 2停用）',
    credit_score      decimal(3, 1) default 5.0 null comment '信用评分',
    sales_volume      bigint        default 0   null comment '总销量',
    del_flag          char          default '0' null comment '删除标志（0存在 2删除）',
    create_by         varchar(64)   default ''  null comment '创建者',
    create_time       datetime                  null comment '创建时间',
    update_by         varchar(64)   default ''  null comment '更新者',
    update_time       datetime                  null comment '更新时间',
    remark            varchar(500)              null comment '备注'
)
    comment '店铺表';


create index idx_user_id
    on shop_store (user_id);


create table sys_config
(
    config_id    int auto_increment comment '参数主键'
        primary key,
    config_name  varchar(100) default ''  null comment '参数名称',
    config_key   varchar(100) default ''  null comment '参数键名',
    config_value varchar(500) default ''  null comment '参数键值',
    config_type  char         default 'N' null comment '系统内置（Y是 N否）',
    create_by    varchar(64)  default ''  null comment '创建者',
    create_time  datetime                 null comment '创建时间',
    update_by    varchar(64)  default ''  null comment '更新者',
    update_time  datetime                 null comment '更新时间',
    remark       varchar(500)             null comment '备注'
)
    comment '参数配置表';


create table sys_dept
(
    dept_id     bigint auto_increment comment '部门id'
        primary key,
    parent_id   bigint      default 0   null comment '父部门id',
    ancestors   varchar(50) default ''  null comment '祖级列表',
    dept_name   varchar(30) default ''  null comment '部门名称',
    order_num   int         default 0   null comment '显示顺序',
    leader      varchar(20)             null comment '负责人',
    phone       varchar(11)             null comment '联系电话',
    email       varchar(50)             null comment '邮箱',
    status      char        default '0' null comment '部门状态（0正常 1停用）',
    del_flag    char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by   varchar(64) default ''  null comment '创建者',
    create_time datetime                null comment '创建时间',
    update_by   varchar(64) default ''  null comment '更新者',
    update_time datetime                null comment '更新时间'
)
    comment '部门表';


create table sys_dict_data
(
    dict_code   bigint auto_increment comment '字典编码'
        primary key,
    dict_sort   int          default 0   null comment '字典排序',
    dict_label  varchar(100) default ''  null comment '字典标签',
    dict_value  varchar(100) default ''  null comment '字典键值',
    dict_type   varchar(100) default ''  null comment '字典类型',
    css_class   varchar(100)             null comment '样式属性（其他样式扩展）',
    list_class  varchar(100)             null comment '表格回显样式',
    is_default  char         default 'N' null comment '是否默认（Y是 N否）',
    status      char         default '0' null comment '状态（0正常 1停用）',
    create_by   varchar(64)  default ''  null comment '创建者',
    create_time datetime                 null comment '创建时间',
    update_by   varchar(64)  default ''  null comment '更新者',
    update_time datetime                 null comment '更新时间',
    remark      varchar(500)             null comment '备注'
)
    comment '字典数据表';


create table sys_dict_type
(
    dict_id     bigint auto_increment comment '字典主键'
        primary key,
    dict_name   varchar(100) default ''  null comment '字典名称',
    dict_type   varchar(100) default ''  null comment '字典类型',
    status      char         default '0' null comment '状态（0正常 1停用）',
    create_by   varchar(64)  default ''  null comment '创建者',
    create_time datetime                 null comment '创建时间',
    update_by   varchar(64)  default ''  null comment '更新者',
    update_time datetime                 null comment '更新时间',
    remark      varchar(500)             null comment '备注',
    constraint dict_type
        unique (dict_type)
)
    comment '字典类型表';


create table sys_job
(
    job_id          bigint auto_increment comment '任务ID',
    job_name        varchar(64)  default ''        not null comment '任务名称',
    job_group       varchar(64)  default 'DEFAULT' not null comment '任务组名',
    invoke_target   varchar(500)                   not null comment '调用目标字符串',
    cron_expression varchar(255) default ''        null comment 'cron执行表达式',
    misfire_policy  varchar(20)  default '3'       null comment '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    concurrent      char         default '1'       null comment '是否并发执行（0允许 1禁止）',
    status          char         default '0'       null comment '状态（0正常 1暂停）',
    create_by       varchar(64)  default ''        null comment '创建者',
    create_time     datetime                       null comment '创建时间',
    update_by       varchar(64)  default ''        null comment '更新者',
    update_time     datetime                       null comment '更新时间',
    remark          varchar(500) default ''        null comment '备注信息',
    primary key (job_id, job_name, job_group)
)
    comment '定时任务调度表';


create table sys_job_log
(
    job_log_id     bigint auto_increment comment '任务日志ID'
        primary key,
    job_name       varchar(64)               not null comment '任务名称',
    job_group      varchar(64)               not null comment '任务组名',
    invoke_target  varchar(500)              not null comment '调用目标字符串',
    job_message    varchar(500)              null comment '日志信息',
    status         char          default '0' null comment '执行状态（0正常 1失败）',
    exception_info varchar(2000) default ''  null comment '异常信息',
    create_time    datetime                  null comment '创建时间'
)
    comment '定时任务调度日志表';


create table sys_logininfor
(
    info_id        bigint auto_increment comment '访问ID'
        primary key,
    user_name      varchar(50)  default ''  null comment '用户账号',
    ipaddr         varchar(128) default ''  null comment '登录IP地址',
    login_location varchar(255) default ''  null comment '登录地点',
    browser        varchar(50)  default ''  null comment '浏览器类型',
    os             varchar(50)  default ''  null comment '操作系统',
    status         char         default '0' null comment '登录状态（0成功 1失败）',
    msg            varchar(255) default ''  null comment '提示消息',
    login_time     datetime                 null comment '访问时间'
)
    comment '系统访问记录';


create index idx_sys_logininfor_lt
    on sys_logininfor (login_time);


create index idx_sys_logininfor_s
    on sys_logininfor (status);


create table sys_menu
(
    menu_id     bigint auto_increment comment '菜单ID'
        primary key,
    menu_name   varchar(50)              not null comment '菜单名称',
    parent_id   bigint       default 0   null comment '父菜单ID',
    order_num   int          default 0   null comment '显示顺序',
    path        varchar(200) default ''  null comment '路由地址',
    component   varchar(255)             null comment '组件路径',
    query       varchar(255)             null comment '路由参数',
    route_name  varchar(50)  default ''  null comment '路由名称',
    is_frame    int          default 1   null comment '是否为外链（0是 1否）',
    is_cache    int          default 0   null comment '是否缓存（0缓存 1不缓存）',
    menu_type   char         default ''  null comment '菜单类型（M目录 C菜单 F按钮）',
    visible     char         default '0' null comment '菜单状态（0显示 1隐藏）',
    status      char         default '0' null comment '菜单状态（0正常 1停用）',
    perms       varchar(100)             null comment '权限标识',
    icon        varchar(100) default '#' null comment '菜单图标',
    create_by   varchar(64)  default ''  null comment '创建者',
    create_time datetime                 null comment '创建时间',
    update_by   varchar(64)  default ''  null comment '更新者',
    update_time datetime                 null comment '更新时间',
    remark      varchar(500) default ''  null comment '备注'
)
    comment '菜单权限表';


create table sys_notice
(
    notice_id      int auto_increment comment '公告ID'
        primary key,
    notice_title   varchar(50)             not null comment '公告标题',
    notice_type    char                    not null comment '公告类型（1通知 2公告）',
    notice_content longblob                null comment '公告内容',
    status         char        default '0' null comment '公告状态（0正常 1关闭）',
    create_by      varchar(64) default ''  null comment '创建者',
    create_time    datetime                null comment '创建时间',
    update_by      varchar(64) default ''  null comment '更新者',
    update_time    datetime                null comment '更新时间',
    remark         varchar(255)            null comment '备注'
)
    comment '通知公告表';


create table sys_oper_log
(
    oper_id        bigint auto_increment comment '日志主键'
        primary key,
    title          varchar(50)   default '' null comment '模块标题',
    business_type  int           default 0  null comment '业务类型（0其它 1新增 2修改 3删除）',
    method         varchar(200)  default '' null comment '方法名称',
    request_method varchar(10)   default '' null comment '请求方式',
    operator_type  int           default 0  null comment '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name      varchar(50)   default '' null comment '操作人员',
    dept_name      varchar(50)   default '' null comment '部门名称',
    oper_url       varchar(255)  default '' null comment '请求URL',
    oper_ip        varchar(128)  default '' null comment '主机地址',
    oper_location  varchar(255)  default '' null comment '操作地点',
    oper_param     varchar(2000) default '' null comment '请求参数',
    json_result    varchar(2000) default '' null comment '返回参数',
    status         int           default 0  null comment '操作状态（0正常 1异常）',
    error_msg      varchar(2000) default '' null comment '错误消息',
    oper_time      datetime                 null comment '操作时间',
    cost_time      bigint        default 0  null comment '消耗时间'
)
    comment '操作日志记录';


create index idx_sys_oper_log_bt
    on sys_oper_log (business_type);


create index idx_sys_oper_log_ot
    on sys_oper_log (oper_time);


create index idx_sys_oper_log_s
    on sys_oper_log (status);


create table sys_post
(
    post_id     bigint auto_increment comment '岗位ID'
        primary key,
    post_code   varchar(64)            not null comment '岗位编码',
    post_name   varchar(50)            not null comment '岗位名称',
    post_sort   int                    not null comment '显示顺序',
    status      char                   not null comment '状态（0正常 1停用）',
    create_by   varchar(64) default '' null comment '创建者',
    create_time datetime               null comment '创建时间',
    update_by   varchar(64) default '' null comment '更新者',
    update_time datetime               null comment '更新时间',
    remark      varchar(500)           null comment '备注'
)
    comment '岗位信息表';


create table sys_role
(
    role_id             bigint auto_increment comment '角色ID'
        primary key,
    role_name           varchar(30)             not null comment '角色名称',
    role_key            varchar(100)            not null comment '角色权限字符串',
    role_sort           int                     not null comment '显示顺序',
    data_scope          char        default '1' null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly tinyint(1)  default 1   null comment '菜单树选择项是否关联显示',
    dept_check_strictly tinyint(1)  default 1   null comment '部门树选择项是否关联显示',
    status              char                    not null comment '角色状态（0正常 1停用）',
    del_flag            char        default '0' null comment '删除标志（0代表存在 2代表删除）',
    create_by           varchar(64) default ''  null comment '创建者',
    create_time         datetime                null comment '创建时间',
    update_by           varchar(64) default ''  null comment '更新者',
    update_time         datetime                null comment '更新时间',
    remark              varchar(500)            null comment '备注'
)
    comment '角色信息表';


create table sys_role_dept
(
    role_id bigint not null comment '角色ID',
    dept_id bigint not null comment '部门ID',
    primary key (role_id, dept_id)
)
    comment '角色和部门关联表';


create table sys_role_menu
(
    role_id bigint not null comment '角色ID',
    menu_id bigint not null comment '菜单ID',
    primary key (role_id, menu_id)
)
    comment '角色和菜单关联表';


create table sys_user
(
    user_id         bigint auto_increment comment '用户ID'
        primary key,
    dept_id         bigint                    null comment '部门ID',
    user_name       varchar(30)               not null comment '用户账号',
    nick_name       varchar(30)               not null comment '用户昵称',
    user_type       varchar(2)   default '00' null comment '用户类型（00系统用户）',
    email           varchar(50)  default ''   null comment '用户邮箱',
    phonenumber     varchar(11)  default ''   null comment '手机号码',
    sex             char         default '0'  null comment '用户性别（0男 1女 2未知）',
    avatar          varchar(100) default ''   null comment '头像地址',
    password        varchar(100) default ''   null comment '密码',
    status          char         default '0'  null comment '账号状态（0正常 1停用）',
    del_flag        char         default '0'  null comment '删除标志（0代表存在 2代表删除）',
    login_ip        varchar(128) default ''   null comment '最后登录IP',
    login_date      datetime                  null comment '最后登录时间',
    pwd_update_date datetime                  null comment '密码最后更新时间',
    create_by       varchar(64)  default ''   null comment '创建者',
    create_time     datetime                  null comment '创建时间',
    update_by       varchar(64)  default ''   null comment '更新者',
    update_time     datetime                  null comment '更新时间',
    remark          varchar(500)              null comment '备注'
)
    comment '用户信息表';


create table sys_user_post
(
    user_id bigint not null comment '用户ID',
    post_id bigint not null comment '岗位ID',
    primary key (user_id, post_id)
)
    comment '用户与岗位关联表';


create table sys_user_role
(
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (user_id, role_id)
)
    comment '用户和角色关联表';
