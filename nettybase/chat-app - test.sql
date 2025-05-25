-- ----------------------------
-- 创建数据库（若不存在）
-- 字符集：utf8mb4 支持 emoji，排序规则：utf8mb4_0900_ai_ci（兼容 MySQL 8.0+ 性能优化）
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `chat_app` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

USE `chat_app`;


-- ----------------------------
-- Table: 会话表（conversations）
-- 功能：管理私聊/群聊会话，记录会话基本信息及最后一条消息引用
-- ----------------------------
DROP TABLE IF EXISTS `conversations`;
CREATE TABLE `conversations` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '会话ID（主键）',
  `type` ENUM('private', 'group') NOT NULL COMMENT '会话类型：private=私聊，group=群聊',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '会话名称（群聊必填，私聊为空）',
  `creator_id` INT(11) NOT NULL COMMENT '创建者ID（外键，关联用户表users.id）',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动填充）',
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  `last_message_id` INT(11) DEFAULT NULL COMMENT '最后一条消息ID（外键，关联消息表messages.id）',
  PRIMARY KEY (`id`),
  -- 外键约束：创建者必须是存在的用户
  KEY `fk_conversations_creator_id` (`creator_id`),
  -- 外键约束：最后一条消息需存在（删除消息时设为NULL）
  KEY `fk_conversations_last_message` (`last_message_id`),
  -- 索引：按类型快速筛选私聊/群聊
  KEY `idx_type` (`type`),
  -- 索引：按创建者查询其创建的会话
  KEY `idx_creator_id` (`creator_id`),
  CONSTRAINT `conversations_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_conversations_last_message` FOREIGN KEY (`last_message_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='会话表';


-- ----------------------------
-- Table: 好友关系表（friendships）
-- 功能：管理用户好友关系（加好友、接受、拉黑）
-- 设计特点：双向关系，通过(user_id, friend_id)唯一标识
-- ----------------------------
DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships` (
  `user_id` INT(11) NOT NULL COMMENT '用户ID（发起方）',
  `friend_id` INT(11) NOT NULL COMMENT '好友ID（接收方）',
  `status` ENUM('pending', 'accepted', 'blocked') DEFAULT 'pending' COMMENT '关系状态：pending=待接受，accepted=已好友，blocked=已拉黑',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（自动填充）',
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  PRIMARY KEY (`user_id`, `friend_id`),
  -- 索引：按用户查询其所有好友关系
  KEY `idx_user_id` (`user_id`),
  -- 索引：按好友ID查询反向关系（如B的好友请求）
  KEY `idx_friend_id` (`friend_id`),
  -- 索引：按状态筛选（如查询待处理的好友请求）
  KEY `idx_status` (`status`),
  -- 外键约束：用户和好友必须是存在的用户
  CONSTRAINT `friendships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `friendships_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB COMMENT='好友关系表';


-- ----------------------------
-- Table: 群聊禁言表（group_mute_list）
-- 功能：记录群聊中的禁言用户及规则
-- 设计特点：(conversation_id, user_id)唯一标识，支持永久禁言（muted_until为NULL）
-- ----------------------------
DROP TABLE IF EXISTS `group_mute_list`;
CREATE TABLE `group_mute_list` (
  `conversation_id` INT(11) NOT NULL COMMENT '群聊ID（外键，关联会话表conversations.id）',
  `user_id` INT(11) NOT NULL COMMENT '被禁言用户ID（外键，关联用户表users.id）',
  `muted_until` DATETIME DEFAULT NULL COMMENT '禁言截止时间（NULL表示永久禁言）',
  `created_by` INT(11) NOT NULL COMMENT '禁言执行者ID（外键，关联用户表users.id，通常为管理员）',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '禁言时间（自动填充）',
  PRIMARY KEY (`conversation_id`, `user_id`),
  -- 索引：按执行者查询其执行的禁言记录
  KEY `idx_created_by` (`created_by`),
  -- 索引：按群聊ID查询所有禁言用户
  KEY `idx_conversation_id` (`conversation_id`),
  -- 索引：按用户ID查询其被禁言的群聊
  KEY `idx_user_id` (`user_id`),
  -- 外键约束：群聊删除时级联删除禁言记录
  CONSTRAINT `group_mute_list_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  -- 外键约束：用户/执行者删除时级联删除禁言记录
  CONSTRAINT `group_mute_list_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `group_mute_list_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='群聊禁言表';


-- ----------------------------
-- Table: 消息已读状态表（message_read_status）
-- 功能：记录用户对消息的已读状态（私聊/群聊均适用）
-- 设计特点：(message_id, user_id)唯一标识，read_at为NULL表示未读
-- ----------------------------
DROP TABLE IF EXISTS `message_read_status`;
CREATE TABLE `message_read_status` (
  `message_id` INT(11) NOT NULL COMMENT '消息ID（外键，关联消息表messages.id）',
  `user_id` INT(11) NOT NULL COMMENT '用户ID（外键，关联用户表users.id）',
  `read_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '已读时间（NULL表示未读，自动填充当前时间表示已读）',
  PRIMARY KEY (`message_id`, `user_id`),
  -- 索引：按消息ID查询所有已读用户
  KEY `idx_message_id` (`message_id`),
  -- 索引：按用户ID查询其所有已读消息
  KEY `idx_user_id` (`user_id`),
  -- 外键约束：消息/用户删除时级联删除已读状态
  CONSTRAINT `message_read_status_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
  CONSTRAINT `message_read_status_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='消息已读状态表';


-- ----------------------------
-- Table: 消息表（messages）
-- 功能：存储消息内容及元数据，支持文本、图片、文件、系统消息
-- 设计特点：支持消息回复（reply_to_id自关联），文件存储通过URL引用
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID（主键）',
  `sender_id` INT(11) NOT NULL COMMENT '发送者ID（外键，关联用户表users.id）',
  `conversation_id` INT(11) NOT NULL COMMENT '所属会话ID（外键，关联会话表conversations.id）',
  `content` TEXT NOT NULL COMMENT '消息内容（文本类型存储内容，其他类型存储描述）',
  `type` ENUM('text', 'image', 'file', 'system') DEFAULT 'text' COMMENT '消息类型',
  `sent_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间（自动填充）',
  `is_read` TINYINT(1) DEFAULT '0' COMMENT '是否已读（0=未读，1=已读，私聊场景可优化为使用message_read_status表）',
  `edited` TINYINT(1) DEFAULT '0' COMMENT '是否编辑过（0=未编辑，1=已编辑）',
  `reply_to_id` INT(11) DEFAULT NULL COMMENT '回复的消息ID（自关联外键，支持消息链）',
  `file_url` VARCHAR(255) DEFAULT NULL COMMENT '文件/图片URL（type为image/file时有效）',
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '文件名（仅type为file时有效）',
  `file_size` INT(11) DEFAULT NULL COMMENT '文件大小（字节，仅type为file时有效）',
  PRIMARY KEY (`id`),
  -- 联合索引：按会话ID和发送时间排序，用于分页查询消息
  KEY `idx_conversation_sent` (`conversation_id`, `sent_at`),
  -- 索引：按回复的消息ID查询引用链
  KEY `fk_messages_reply_to` (`reply_to_id`),
  -- 索引：按发送者ID查询其发送的所有消息
  KEY `idx_sender_id` (`sender_id`),
  -- 索引：按发送时间查询近期消息
  KEY `idx_sent_at` (`sent_at`),
  -- 外键约束：回复的消息需存在（删除时设为NULL）
  CONSTRAINT `fk_messages_reply_to` FOREIGN KEY (`reply_to_id`) REFERENCES `messages` (`id`) ON DELETE SET NULL,
  -- 外键约束：发送者/会话需存在
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`)
) ENGINE=InnoDB COMMENT='消息表';


-- ----------------------------
-- Table: 用户会话关联表（user_conversations）
-- 功能：记录用户加入的会话及权限，区分群聊管理员和已读状态
-- 设计特点：私聊自动加入（通过好友关系隐含），群聊需显式记录
-- ----------------------------
DROP TABLE IF EXISTS `user_conversations`;
CREATE TABLE `user_conversations` (
  `user_id` INT(11) NOT NULL COMMENT '用户ID（外键，关联用户表users.id）',
  `conversation_id` INT(11) NOT NULL COMMENT '会话ID（外键，关联会话表conversations.id）',
  `joined_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间（自动填充）',
  `is_admin` TINYINT(1) DEFAULT '0' COMMENT '是否为管理员（仅群聊有效，1=是）',
  `last_read_at` DATETIME DEFAULT NULL COMMENT '最后已读时间（用于计算未读消息数）',
  PRIMARY KEY (`user_id`, `conversation_id`),
  -- 索引：按用户ID查询其所有会话
  KEY `idx_user_id` (`user_id`),
  -- 索引：按会话ID查询所有成员
  KEY `idx_conversation_id` (`conversation_id`),
  -- 外键约束：用户/会话删除时级联操作（需根据业务需求设置ON DELETE规则）
  CONSTRAINT `user_conversations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_conversations_ibfk_2` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`)
) ENGINE=InnoDB COMMENT='用户会话关联表';


-- ----------------------------
-- Table: 用户表（users）
-- 功能：存储用户账号信息，支持登录验证和用户资料
-- 设计特点：密码使用哈希值存储，邮箱/用户名唯一
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一标识，用于登录）',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值（需配合BCrypt等算法加密）',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱（唯一标识，用于找回密码）',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `last_seen` DATETIME DEFAULT NULL COMMENT '最后在线时间（用于显示活跃状态）',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间（自动填充）',
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动更新）',
  `is_active` TINYINT(1) DEFAULT '1' COMMENT '账号状态（1=活跃，0=禁用）',
  `bio` TEXT COMMENT '个人简介',
  PRIMARY KEY (`id`),
  -- 唯一索引：确保用户名和邮箱不重复
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  -- 索引：加速用户名/邮箱模糊查询
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB COMMENT='用户表';
