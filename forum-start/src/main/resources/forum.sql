create table announcement (
    id          bigint                              not null comment '公告ID' 
        primary key,
    title       varchar(100)                        not null comment '公告标题',
    content     text                                not null comment '正文',
    type        int                                 not null comment '公告类型（0系统公告、1学校公告）',
    created_at  timestamp  default CURRENT_TIMESTAMP not null comment '发布时间',
    updated_at  timestamp                            not null on update CURRENT_TIMESTAMP comment '更新时间',
    scheduled_at timestamp                          null comment '定时发布时间，如果为空则立即发布',
    status int                                      not null comment '状态（0：草稿，1：已发布，2：待发布）',
    creator_id  int not                             null comment '创建用户ID',
    updator_id  int not                             null comment '更新用户ID',
    deleted     boolean                             not null comment '是否被删除',
    attribute   text                                null comment '属性列（json string）'
    sticky      boolean default false               not null comment '是否置顶',
);

create table attachment
(
    id          bigint                              not null comment '附件ID'
        primary key,
    user_id     bigint                              not null comment '上传用户',
    file_id     bigint                              not null comment '对应的文件',
    target_type varchar(20)                         not null comment '对象类型',
    target_id   bigint                              not null comment '对象ID',
    type        varchar(20)                         not null comment '附件类型（图像/视频/文档）',
    filename    varchar(255)                        not null comment '原始文件名',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid  bigint                              not null comment '创建用户',
    update_uid  bigint                              not null comment '更新用户',
    deleted     boolean                             not null comment '是否被删除',
    attribute   text                                null comment '属性列（json string）'
);

create table college
(
    id         bigint                              not null comment '学院ID'
        primary key,
    name       varchar(100)                        not null comment '名称',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table comment
(
    id         bigint                              not null comment '评论ID'
        primary key,
    user_id    bigint                              not null comment '用户ID',
    post_id    bigint                              not null comment '帖子ID',
    parent_id  bigint                              null comment '父评论ID',
    target_id  bigint                              null comment '回复对象ID',
    content    varchar(500)                        not null comment '评论内容',
    is_pinned  boolean                             not null comment '是否被置顶',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table faq
(
    id         bigint                              not null comment '常见问题ID'
        primary key,
    category   varchar(20)                         not null comment '板块（账号/学院/帖子/其他）',
    question   varchar(200)                        not null comment '问题描述',
    answer     varchar(500)                        not null comment '问题解答',
    view_count int       default 0                 not null comment '浏览量',
    is_pinned  boolean   default false             not null comment '是否添加到"猜你想问"',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table favorite
(
    id         bigint                              not null comment '收藏ID'
        primary key,
    user_id    bigint                              not null comment '用户ID',
    post_id    bigint                              not null comment '帖子ID',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table feedback
(
    id         bigint                              not null comment '意见反馈ID'
        primary key,
    user_id    bigint                              not null comment '用户ID',
    category   varchar(20)                         not null comment '反馈类别',
    content    varchar(500)                        not null comment '反馈内容',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table file
(
    id         bigint                              not null comment '文件ID'
        primary key,
    blake3     varchar(300)                        not null comment '文件哈希值（使用BLAKE3算法，长度为256）',
    object_key varchar(255)                        not null comment '文件路径',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table follow
(
    id         bigint                              not null comment '关注ID'
        primary key,
    user_id    bigint                              not null comment '用户ID',
    target_id  bigint                              not null comment '关注对象ID',
    type       varchar(20)                         not null comment '关注类型',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table notice
(
    id            bigint                              not null comment '互动通知ID'
        primary key,
    receiver_id   bigint                              not null comment '收件人ID',
    sender_id     bigint                              not null comment '发送人ID',
    type          varchar(20)                         not null comment '消息类型（赞/收藏/评论/at）',
    position_type varchar(20)                         not null comment '位置类型（帖子/评论）',
    position_id   bigint                              not null comment '位置ID',
    comment_id    bigint                              not null comment '评论ID（type为comment或at时有效）',
    created_at    timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at    timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid    bigint                              not null comment '创建用户',
    update_uid    bigint                              not null comment '更新用户',
    deleted       boolean                             not null comment '是否被删除',
    attribute     text                                null comment '属性列（json string）'
);

create table operation_log
(
    id             bigint                              not null comment '修改记录ID'
        primary key,
    type           varchar(20)                         not null comment '修改类型',
    target_id      bigint                              not null comment '修改对象ID',
    user_id        bigint                              not null comment '修改用户ID',
    before_content text                                not null comment '修改前内容',
    after_content  text                                not null comment '修改后内容',
    created_at     timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

create table post
(
    id          bigint                                 not null comment '帖子ID'
        primary key,
    user_id     bigint                                 not null comment '作者用户ID',
    title       varchar(100) default ''                not null comment '标题',
    content     text                                   not null comment '正文',
    category_id bigint                                 not null comment '板块归属',
    is_pinned   boolean                                not null comment '是否置顶（个人主页）',
    created_at  timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp                              not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid  bigint                                 not null comment '创建用户',
    update_uid  bigint                                 not null comment '更新用户',
    deleted     boolean                                not null comment '是否被删除',
    attribute   text                                   null comment '属性列（json string）'
);

create table post_category
(
    id         bigint                              not null comment '帖子板块ID'
        primary key,
    name       varchar(50)                         not null comment '名称',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table post_topic_relation
(
    id         bigint                              not null comment '关联ID'
        primary key,
    post_id    bigint                              not null comment '帖子ID',
    topic_id   bigint                              not null comment '话题ID',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '发布时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table report
(
    id          bigint                              not null comment '举报ID'
        primary key,
    user_id     bigint                              not null comment '用户ID',
    target_type varchar(20)                         not null comment '对象类型(用户/帖子/评论)',
    target_id   bigint                              not null comment '对象ID',
    type        varchar(20)                         not null comment '举报类型(色情/暴力/侵权/违法/涉政/引战/谣言/其他)',
    reason      varchar(500)                        not null comment '理由',
    status      varchar(20)                         not null comment '处理状态(未处理/举报失败/举报成功)',
    result      varchar(300)                        not null comment '处理结论',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at  timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid  bigint                              not null comment '创建用户',
    update_uid  bigint                              not null comment '更新用户',
    deleted     boolean                             not null comment '是否被删除',
    attribute   text                                null comment '属性列（json string）'
);

create table topic
(
    id         bigint                              not null comment '话题ID'
        primary key,
    name       varchar(50)                         not null comment '话题名',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '发布时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table upvote
(
    id         bigint                              not null comment '点赞ID'
        primary key,
    user_id    bigint                              not null comment '用户ID',
    post_id    bigint                              null comment '帖子ID',
    comment_id bigint                              null comment '评论ID',
    status     boolean   default true              not null comment '点赞状态',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at timestamp                           not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid bigint                              not null comment '创建用户',
    update_uid bigint                              not null comment '更新用户',
    deleted    boolean                             not null comment '是否被删除',
    attribute  text                                null comment '属性列（json string）'
);

create table user
(
    id             bigint                                 not null comment '用户ID'
        primary key,
    nickname       varchar(50)                            not null comment '用户昵称',
    realname       varchar(20)                            not null comment '真实姓名',
    student_id     varchar(12)                            not null comment '学号',
    password       varchar(255)                           not null comment '密码哈希',
    college_id     bigint                                 not null comment '学院ID',
    gender         varchar(20)                            not null comment '性别(男,女,保密)',
    phone          varchar(20)  default ''                not null comment '手机号',
    avatar         varchar(255) default ''                not null comment '头像地址',
    upvote_notice  boolean      default true              not null comment '点赞消息开关',
    comment_notice boolean      default true              not null comment '评论消息开关',
    role           varchar(20)                            not null comment '用户角色',
    created_at     timestamp    default CURRENT_TIMESTAMP not null comment '注册时间',
    updated_at     timestamp                              not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid     bigint                                 not null comment '创建用户',
    update_uid     bigint                                 not null comment '更新用户',
    deleted        boolean                                not null comment '是否被删除',
    attribute      text                                   null comment '属性列（json string）',
    constraint uq_user
        unique (nickname, student_id, phone)
);

create table user_detail
(
    user_id          bigint                   not null comment '用户ID'
        primary key,
    signature        varchar(20) default ''   not null comment '个性签名',
    profile          varchar(50) default ''   not null comment '个人简介',
    email            varchar(50) default ''   not null comment '个人邮箱',
    birthday         date                     null comment '生日',
    birthday_visible boolean     default true not null comment '生日可见性',
    college_visible  boolean     default true not null comment '学院可见性',
    realname_visible boolean     default true not null comment '实名可见性'
);

create table user (
    id int not null comment '用户ID' primary key,
    nickname varchar(50) not null comment '用户昵称',
    realname varchar(20) not null comment '真实姓名',
    student_id varchar(12) not null comment '学号',
    password varchar(255) not null comment '密码哈希',
    college_id int not null comment '学院ID',
    gender varchar(20) not null comment '性别(男,女,保密)',
    phone varchar(20) default '' not null comment '手机号',
    avatar varchar(255) default '' not null comment '头像地址',
    upvote_notice boolean default true not null comment '点赞消息开关',
    comment_notice boolean default true not null comment '评论消息开关',
    role varchar(20) not null comment '用户角色',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '注册时间',
    updated_at timestamp not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_uid int not null comment '创建用户',
    update_uid int not null comment '更新用户',
    deleted boolean not null comment '是否被删除',
    attribute text null comment '属性列（json string）',
    constraint uq_user unique (nickname, student_id, phone)
);

create table user_detail (
    user_id int not null comment '用户ID' primary key,
    signature varchar(20) default '' not null comment '个性签名',
    profile varchar(50) default '' not null comment '个人简介',
    email varchar(50) default '' not null comment '个人邮箱',
    birthday date null comment '生日',
    birthday_visible boolean default true not null comment '生日可见性',
    college_visible boolean default true not null comment '学院可见性',
    realname_visible boolean default true not null comment '实名可见性'
);
