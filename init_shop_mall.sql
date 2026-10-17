-- Schema-only public edition: original row data and seed accounts removed.

CREATE TABLE shop_category (
  category_id           BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '分类ID',
  parent_id             BIGINT(20)      DEFAULT 0                  COMMENT '父分类ID',
  category_name         VARCHAR(50)     NOT NULL                   COMMENT '分类名称',
  category_level        INT(2)          DEFAULT 1                  COMMENT '分类层级',
  sort_order            INT(4)          DEFAULT 0                  COMMENT '排序',
  category_icon         VARCHAR(200)    DEFAULT ''                 COMMENT '分类图标',
  status                CHAR(1)         DEFAULT '0'                COMMENT '状态（0正常 1停用）',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by             VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_by             VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time           DATETIME                                   COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (category_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '商品分类表';

CREATE TABLE shop_store (
  store_id              BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '店铺ID',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '店主用户ID',
  store_name            VARCHAR(100)    NOT NULL                   COMMENT '店铺名称',
  store_logo            VARCHAR(200)    DEFAULT ''                 COMMENT '店铺Logo',
  store_description     VARCHAR(500)    DEFAULT ''                 COMMENT '店铺描述',
  store_address         VARCHAR(200)    DEFAULT ''                 COMMENT '店铺地址',
  store_phone           VARCHAR(20)     DEFAULT ''                 COMMENT '联系电话',
  business_license      VARCHAR(200)    DEFAULT ''                 COMMENT '营业执照',
  status                CHAR(1)         DEFAULT '0'                COMMENT '状态（0待审核 1正常 2停用）',
  credit_score          DECIMAL(3,1)    DEFAULT 5.0                COMMENT '信用评分',
  sales_volume          BIGINT(20)      DEFAULT 0                  COMMENT '总销量',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by             VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_by             VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time           DATETIME                                   COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (store_id),
  KEY idx_user_id (user_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '店铺表';

CREATE TABLE shop_product (
  product_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '商品ID',
  store_id              BIGINT(20)      NOT NULL                   COMMENT '店铺ID',
  category_id           BIGINT(20)      NOT NULL                   COMMENT '分类ID',
  product_name          VARCHAR(200)    NOT NULL                   COMMENT '商品名称',
  product_subtitle      VARCHAR(200)    DEFAULT ''                 COMMENT '商品副标题',
  main_image            VARCHAR(500)    DEFAULT ''                 COMMENT '主图',
  sub_images            TEXT                                       COMMENT '商品图片（多张，逗号分隔）',
  detail                TEXT                                       COMMENT '商品详情',
  price                 DECIMAL(20,2)   NOT NULL                   COMMENT '价格',
  stock                 INT(11)         DEFAULT 0                  COMMENT '库存数量',
  sales_count           INT(11)         DEFAULT 0                  COMMENT '销量',
  status                CHAR(1)         DEFAULT '0'                COMMENT '状态（0待审核 1在售 2下架）',
  is_recommended        CHAR(1)         DEFAULT '0'                COMMENT '是否推荐（0否 1是）',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by             VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_by             VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time           DATETIME                                   COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (product_id),
  KEY idx_store_id (store_id),
  KEY idx_category_id (category_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '商品表';

CREATE TABLE shop_cart (
  cart_id               BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '购物车ID',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '用户ID',
  product_id            BIGINT(20)      NOT NULL                   COMMENT '商品ID',
  quantity              INT(11)         DEFAULT 1                  COMMENT '数量',
  checked               CHAR(1)         DEFAULT '1'                COMMENT '是否选中（0否 1是）',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_time           DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (cart_id),
  KEY idx_user_id (user_id),
  KEY idx_product_id (product_id),
  UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '购物车表';

CREATE TABLE shop_order (
  order_id              BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '订单ID',
  order_no              VARCHAR(32)     NOT NULL                   COMMENT '订单号',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '用户ID',
  store_id              BIGINT(20)      NOT NULL                   COMMENT '店铺ID',
  receiver_name         VARCHAR(50)     NOT NULL                   COMMENT '收货人姓名',
  receiver_phone        VARCHAR(20)     NOT NULL                   COMMENT '收货电话',
  receiver_province     VARCHAR(50)     DEFAULT ''                 COMMENT '省份',
  receiver_city         VARCHAR(50)     DEFAULT ''                 COMMENT '城市',
  receiver_district     VARCHAR(50)     DEFAULT ''                 COMMENT '区县',
  receiver_address      VARCHAR(200)    NOT NULL                   COMMENT '详细地址',
  total_amount          DECIMAL(20,2)   NOT NULL                   COMMENT '订单总金额',
  payment_amount        DECIMAL(20,2)   DEFAULT 0.00               COMMENT '实付金额',
  freight               DECIMAL(20,2)   DEFAULT 0.00               COMMENT '运费',
  payment_type          INT(4)          DEFAULT 1                  COMMENT '支付方式（1在线支付 2货到付款）',
  payment_time          DATETIME                                   COMMENT '支付时间',
  send_time             DATETIME                                   COMMENT '发货时间',
  end_time              DATETIME                                   COMMENT '完成时间',
  close_time            DATETIME                                   COMMENT '关闭时间',
  status                INT(4)          DEFAULT 0                  COMMENT '订单状态（0待支付 1已支付 2已发货 3已完成 4已关闭 5已取消）',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_by             VARCHAR(64)     DEFAULT ''                 COMMENT '创建者',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_by             VARCHAR(64)     DEFAULT ''                 COMMENT '更新者',
  update_time           DATETIME                                   COMMENT '更新时间',
  remark                VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_id (user_id),
  KEY idx_store_id (store_id),
  KEY idx_status (status)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '订单表';

CREATE TABLE shop_order_item (
  order_item_id         BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '订单明细ID',
  order_id              BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  product_id            BIGINT(20)      NOT NULL                   COMMENT '商品ID',
  product_name          VARCHAR(200)    NOT NULL                   COMMENT '商品名称',
  product_image         VARCHAR(500)    DEFAULT ''                 COMMENT '商品图片',
  current_price         DECIMAL(20,2)   NOT NULL                   COMMENT '当前价格',
  quantity              INT(11)         NOT NULL                   COMMENT '数量',
  total_price           DECIMAL(20,2)   NOT NULL                   COMMENT '小计',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_time           DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (order_item_id),
  KEY idx_order_id (order_id),
  KEY idx_product_id (product_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '订单明细表';

CREATE TABLE shop_address (
  address_id            BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '地址ID',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '用户ID',
  receiver_name         VARCHAR(50)     NOT NULL                   COMMENT '收货人姓名',
  receiver_phone        VARCHAR(20)     NOT NULL                   COMMENT '收货电话',
  province              VARCHAR(50)     DEFAULT ''                 COMMENT '省份',
  city                  VARCHAR(50)     DEFAULT ''                 COMMENT '城市',
  district              VARCHAR(50)     DEFAULT ''                 COMMENT '区县',
  address               VARCHAR(200)    NOT NULL                   COMMENT '详细地址',
  is_default            CHAR(1)         DEFAULT '0'                COMMENT '是否默认（0否 1是）',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_time           DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (address_id),
  KEY idx_user_id (user_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '收货地址表';

CREATE TABLE shop_review (
  review_id             BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '评价ID',
  product_id            BIGINT(20)      NOT NULL                   COMMENT '商品ID',
  order_id              BIGINT(20)      NOT NULL                   COMMENT '订单ID',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '用户ID',
  rating                INT(2)          DEFAULT 5                  COMMENT '评分（1-5星）',
  content               VARCHAR(500)    DEFAULT ''                 COMMENT '评价内容',
  images                VARCHAR(1000)   DEFAULT ''                 COMMENT '评价图片',
  status                CHAR(1)         DEFAULT '0'                COMMENT '状态（0待审核 1已发布 2已屏蔽）',
  del_flag              CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_time           DATETIME                                   COMMENT '创建时间',
  update_time           DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (review_id),
  KEY idx_product_id (product_id),
  KEY idx_user_id (user_id),
  KEY idx_order_id (order_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '商品评价表';

CREATE TABLE shop_favorite (
  favorite_id           BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '收藏ID',
  user_id               BIGINT(20)      NOT NULL                   COMMENT '用户ID',
  product_id            BIGINT(20)      NOT NULL                   COMMENT '商品ID',
  create_time           DATETIME                                   COMMENT '创建时间',
  PRIMARY KEY (favorite_id),
  KEY idx_user_id (user_id),
  KEY idx_product_id (product_id),
  UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '商品收藏表';

CREATE TABLE shop_chat (
  chat_id             BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '聊天ID',
  buyer_id            BIGINT(20)      NOT NULL                   COMMENT '买家ID',
  seller_id           BIGINT(20)      NOT NULL                   COMMENT '卖家ID',
  store_id            BIGINT(20)                                   COMMENT '店铺ID（可选，关联到具体店铺）',
  order_id            BIGINT(20)                                   COMMENT '订单ID（可选，关联到具体订单）',
  message_type        CHAR(1)         DEFAULT '0'                COMMENT '消息类型（0文本 1图片 2系统消息）',
  content             TEXT                                       COMMENT '消息内容',
  sender_type         CHAR(1)         NOT NULL                   COMMENT '发送者类型（0买家 1卖家）',
  sender_id           BIGINT(20)      NOT NULL                   COMMENT '发送者ID',
  is_read             CHAR(1)         DEFAULT '0'                COMMENT '是否已读（0未读 1已读）',
  del_flag            CHAR(1)         DEFAULT '0'                COMMENT '删除标志（0存在 2删除）',
  create_time         DATETIME                                   COMMENT '创建时间',
  update_time         DATETIME                                   COMMENT '更新时间',
  PRIMARY KEY (chat_id),
  KEY idx_buyer_id (buyer_id),
  KEY idx_seller_id (seller_id),
  KEY idx_store_id (store_id),
  KEY idx_order_id (order_id),
  KEY idx_create_time (create_time)
) ENGINE=INNODB AUTO_INCREMENT=1000 COMMENT = '客服聊天记录表';
