--建表语句

CREATE TABLE `basic_v2_organization_one` (
  `organization_one_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `organization_one_name` varchar(100) NOT NULL COMMENT '组织名称',
  `belong` varchar(100) DEFAULT NULL COMMENT '所属',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标志位。0存在 1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `remark` varchar(900) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`organization_one_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织表1';

CREATE TABLE `basic_v2_organization_two` (
  `organization_two_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `organization_two_name` varchar(100) NOT NULL COMMENT '组织名称',
  `belong` varchar(100) DEFAULT NULL COMMENT '所属',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标志位。0存在 1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `remark` varchar(900) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`organization_two_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织表2';

CREATE TABLE `basic_v2_organization_three` (
  `organization_three_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `organization_three_name` varchar(100) NOT NULL COMMENT '组织名称',
  `belong` varchar(100) DEFAULT NULL COMMENT '所属',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标志位。0存在 1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `remark` varchar(900) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`organization_three_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织表3';

--related_v2_org1_cross_org2、related_v2_org2_cross_org3


CREATE TABLE `related_v2_org1_cross_org2` (
  `org1_cross_org2_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `organization_one_id` bigint NOT NULL COMMENT '组织1的id',
  `organization_two_id` bigint NOT NULL COMMENT '组织2的id',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标志位。0存在 1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `remark` varchar(900) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`org1_cross_org2_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织1与组织2的关联表';

CREATE TABLE `related_v2_org2_cross_org3` (
  `org2_cross_org3_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `organization_two_id` bigint NOT NULL COMMENT '组织2的id',
  `organization_three_id` bigint NOT NULL COMMENT '组织3的id',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标志位。0存在 1删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `remark` varchar(900) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`org2_cross_org3_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织2与组织3的关联表';