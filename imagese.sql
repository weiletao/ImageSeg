/*
 Navicat Premium Data Transfer

 Source Server         : MySql316
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : imagese

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 03/09/2025 20:11:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for custom_user
-- ----------------------------
DROP TABLE IF EXISTS `custom_user`;
CREATE TABLE `custom_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `birth_date` date NULL DEFAULT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('patient','doctor','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'patient',
  `is_active` tinyint(1) NULL DEFAULT 1,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `is_staff` tinyint(1) NULL DEFAULT 0,
  `is_superuser` tinyint(1) NULL DEFAULT 0,
  `date_joined` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of custom_user
-- ----------------------------
INSERT INTO `custom_user` VALUES (1, 'test', 'test', 'test', '2025-03-17', 'tset', 'tse', 'patient', 1, NULL, 0, 0, '2025-03-17 12:57:36');
INSERT INTO `custom_user` VALUES (5, 'john_doe', '$2a$10$2tZBUpH4W4TuyRqi/UqXOO9YG9Ysdoqnx002N6afr5Mh7O9Nbhh7y', 'John Doe', '2025-01-04', '00000000000', 'john@example.com', 'patient', 1, 'test', 0, 0, '2025-03-18 14:59:18');
INSERT INTO `custom_user` VALUES (9, 'wlt12345', '$2a$10$AiTPbcE5NZbFbWFH7VfU3O.5Hk.ZsBtvikLXiwiJTizEvQyfdKE8.', 'wlt12345', '2025-03-11', '12345670000', 'wlt111@qq.com', 'patient', 1, 'test', 0, 0, '2025-03-18 18:56:23');
INSERT INTO `custom_user` VALUES (11, 'testDoctor', '$2a$10$ScheASyktw8waPBqzjtluu0fFKw4iCBIdlPKILwkVPcoiwNK.qQ.u', 'testDoctor', '2025-03-20', '12345677654', 'testDoctor@gmail.com', 'doctor', 1, 'testDoctor', 0, 0, '2025-03-22 15:59:52');
INSERT INTO `custom_user` VALUES (12, '余星燃医生', '$2a$10$FuPE80S7clHUKv8HX/dS1ePt9B1ph4rQRUMR2CvyDOZHQ/4AeDV9q', '余星燃', '2003-02-07', '17774851632', 'yuxingran@scu.com', 'doctor', 1, '主治医师，擅长乳腺癌诊断。', 0, 0, '2025-04-19 21:52:11');
INSERT INTO `custom_user` VALUES (13, 'Weiletao', '$2a$10$QKtMarKd99ar.vM553e3Mujn1Jb6PEAFYrlRO8RgdSUlvcdVMv4Vm', '韦乐涛', '2003-06-15', '19102849794', '2022141461092@scu.com', 'patient', 1, '我是一名患者，23岁', 0, 0, '2025-04-19 22:07:03');
INSERT INTO `custom_user` VALUES (14, 'Yuxingran', '$2a$10$.wh/y8GTWNI.vdESf1CT6O0Ta0m4gV3pCiJQyzVB895LIOmSUpBra', '余星燃', '2004-02-07', '17774851632', '2022141461060@scu.com', 'doctor', 1, '乳腺外科主治医师，擅长乳腺癌诊断。', 0, 0, '2025-04-19 22:08:10');
INSERT INTO `custom_user` VALUES (16, 'john_doe1', '$2a$10$60jJqrNtnLqTPIfDWm8bAOsVSJ8b0713fnpXJmVYhnrQRdXwc4C0K', 'John Doe1', NULL, '1234567890', 'john1@example.com', 'patient', 1, NULL, 0, 0, '2025-09-03 17:49:23');

-- ----------------------------
-- Table structure for diagnosed_pdf
-- ----------------------------
DROP TABLE IF EXISTS `diagnosed_pdf`;
CREATE TABLE `diagnosed_pdf`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pdf_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `diagnosis_request_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `diagnosis_request_id`(`diagnosis_request_id` ASC) USING BTREE,
  CONSTRAINT `diagnosed_pdf_ibfk_1` FOREIGN KEY (`diagnosis_request_id`) REFERENCES `diagnosis_request` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diagnosed_pdf
-- ----------------------------
INSERT INTO `diagnosed_pdf` VALUES (4, '1/71155ed8d8ec4dedb65bd53158ea8a67.pdf', '2025-04-01 15:12:54', 1);
INSERT INTO `diagnosed_pdf` VALUES (5, '7/19d1bc1de3444fa9896c3d8b6f572f20.pdf', '2025-04-01 23:26:03', 7);
INSERT INTO `diagnosed_pdf` VALUES (6, '10/63f986f67e24428db065b0990b6c844f.pdf', '2025-04-19 22:21:53', 10);
INSERT INTO `diagnosed_pdf` VALUES (7, '13/1c93defc42cd4c5b9f4335c02c896e80.pdf', '2025-09-03 19:29:14', 13);

-- ----------------------------
-- Table structure for diagnosis_request
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_request`;
CREATE TABLE `diagnosis_request`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `birth_date` date NOT NULL,
  `phone` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start_time` date NOT NULL,
  `duration` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `family_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `other_exams` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `current_treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `emergency_diagnosis` tinyint(1) NULL DEFAULT 0,
  `special_attention` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `patient_id` bigint NOT NULL,
  `doctor_id` bigint NOT NULL,
  `status` enum('pending','diagnosed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `doctor_id`(`doctor_id` ASC) USING BTREE,
  CONSTRAINT `diagnosis_request_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `custom_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `diagnosis_request_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `custom_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diagnosis_request
-- ----------------------------
INSERT INTO `diagnosis_request` VALUES (1, '患者姓名', '2000-01-01', '1234567890', '症状描述', '2025-01-01', '持续时间及严重程度', '既往乳腺疾病历史', '家族病史', '其他检查结果', '当前治疗情况', 1, '特别关注区域', '2025-03-22 22:09:00', '2025-04-01 15:12:53', 5, 11, 'diagnosed');
INSERT INTO `diagnosis_request` VALUES (2, '1233', '2025-03-21', '11111111111', 'test', '2025-03-20', 'test', 'test', 'test', 'test', 'test', 1, 'test', '2025-03-22 22:14:36', '2025-03-22 22:14:36', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (3, 'testimage', '2025-03-21', '11111111112', 'testimage', '2025-03-20', 'testimage', 'testimage', 'testimage', 'testimage', 'testimage', 1, 'testimage', '2025-03-23 15:39:51', '2025-03-23 15:39:51', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (4, 'testimage2', '2025-03-23', '11111111113', 'testimage2', '2025-03-23', 'testimage2', 'testimage2', 'testimage2', 'testimage2', 'testimage2', 1, 'testimage2', '2025-03-23 15:45:35', '2025-03-23 15:45:35', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (5, 'testimage3', '2025-03-23', '11111111114', 'testimage3', '2025-03-23', 'testimage3', 'testimage3', 'testimage3', 'testimage3', 'testimage3', 1, 'testimage3', '2025-03-23 15:49:35', '2025-03-23 15:49:35', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (6, 'testimage4', '2025-03-25', '11111111115', 'testimage4', '2025-03-25', 'testimage4', 'testimage4', 'testimage4', 'testimage4', 'testimage4', 1, 'testimage4', '2025-03-25 09:05:52', '2025-03-25 09:05:52', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (7, 'wlt', '2025-04-01', '19102849794', '疼痛、肿块', '2025-04-01', '否', '无', '无', '无', '无', 1, '无', '2025-04-01 23:22:48', '2025-04-01 23:26:02', 5, 11, 'diagnosed');
INSERT INTO `diagnosis_request` VALUES (8, '韦乐涛', '2003-06-15', '19102849794', '疼痛，有肿块', '2025-04-19', '持续3天，症状加重。', '无', '无', '无', '无', 1, '无', '2025-04-19 21:55:25', '2025-04-19 21:55:25', 5, 12, 'pending');
INSERT INTO `diagnosis_request` VALUES (10, '韦乐涛', '2003-06-15', '19102849794', '疼痛，有肿块。', '2025-04-19', '持续3天，症状略微加重。', '无', '无', '无', '无', 1, '无', '2025-04-19 22:16:53', '2025-04-19 22:21:53', 13, 14, 'diagnosed');
INSERT INTO `diagnosis_request` VALUES (11, 'tttt', '2025-05-12', '11111119082', 'test', '2025-05-09', 'test', 'test', 'tset', 'tes', 'tse', 1, 'tse', '2025-05-12 11:36:24', '2025-05-12 11:36:24', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (12, 'TEST', '2025-05-12', '19102849794', 'TEST', '2025-05-12', 'TSET', 'TEST', 'TEST', 'TSET', 'TEST', 1, 'TSET', '2025-05-12 13:16:48', '2025-05-12 13:16:48', 5, 11, 'pending');
INSERT INTO `diagnosis_request` VALUES (13, '韦乐涛', '2025-09-03', '18204315392', '疼痛，肿块', '2025-09-02', '三天，有加重', '无', '无', '无', '无', 1, '无', '2025-09-03 19:20:37', '2025-09-03 19:29:13', 16, 11, 'diagnosed');

-- ----------------------------
-- Table structure for image_file
-- ----------------------------
DROP TABLE IF EXISTS `image_file`;
CREATE TABLE `image_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `diagnosis_request_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `diagnosis_request_id`(`diagnosis_request_id` ASC) USING BTREE,
  CONSTRAINT `image_file_ibfk_1` FOREIGN KEY (`diagnosis_request_id`) REFERENCES `diagnosis_request` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of image_file
-- ----------------------------
INSERT INTO `image_file` VALUES (1, '1/ba6eda86-d697-4aed-a418-48b8cd42b305.jpeg', 'test', '2025-03-22 22:09:00', '2025-03-25 09:02:15', 1);
INSERT INTO `image_file` VALUES (2, '1/8532f992-bbef-4e2e-a7c2-77579bbeaee2.jpeg', 'test', '2025-03-22 22:09:00', '2025-03-25 09:02:24', 1);
INSERT INTO `image_file` VALUES (3, '2/ba7d60da-c217-494b-9ed3-70c80d738845.png', 'test', '2025-03-22 22:14:36', '2025-03-25 09:02:33', 2);
INSERT INTO `image_file` VALUES (4, '2/fc8b0cd1-aac5-41b5-9b57-2070d89fdcaa.png', 'test', '2025-03-22 22:14:36', '2025-03-25 09:02:39', 2);
INSERT INTO `image_file` VALUES (5, '3/2b2766d1-ab0e-4401-9b60-d63a723d8939.png', 'test', '2025-03-23 15:39:51', '2025-03-25 09:02:44', 3);
INSERT INTO `image_file` VALUES (6, '3/db94e69d-17c5-4e9a-bf35-68742e4cc30a.png', 'test', '2025-03-23 15:39:51', '2025-03-25 09:02:48', 3);
INSERT INTO `image_file` VALUES (7, '4/5685e77e-0cef-425b-9c9f-6b2a8040cedf.png', 'test', '2025-03-23 15:45:35', '2025-03-25 09:02:52', 4);
INSERT INTO `image_file` VALUES (8, '4/eabb6cf6-1ff5-492a-a40a-3a00cf833777.png', 'test', '2025-03-23 15:45:35', '2025-03-25 09:02:55', 4);
INSERT INTO `image_file` VALUES (9, '5/3ee66e4c-ee9e-4954-abc2-d086577ed951.png', 'test', '2025-03-23 15:49:35', '2025-03-25 09:02:59', 5);
INSERT INTO `image_file` VALUES (10, '5/ecb5a973-edb0-46d4-a0f9-a16a673bbfe5.png', 'test', '2025-03-23 15:49:35', '2025-03-25 09:03:08', 5);
INSERT INTO `image_file` VALUES (11, '6/971ab3a2-dd06-43cd-91ce-bf59485b5b15.png', 'test', '2025-03-25 09:05:52', '2025-03-25 09:05:52', 6);
INSERT INTO `image_file` VALUES (12, '6/a569ab83-ce0b-40e4-b24f-9236e95baa76.png', 'test', '2025-03-25 09:05:52', '2025-03-25 09:05:52', 6);
INSERT INTO `image_file` VALUES (13, '7/157acc14-fece-42a9-bf1e-10b58da6070d.png', 'test', '2025-04-01 23:22:48', '2025-04-01 23:22:48', 7);
INSERT INTO `image_file` VALUES (14, '7/19b7b5e4-f753-45de-93be-5729e1ff127a.png', 'test', '2025-04-01 23:22:48', '2025-04-01 23:22:48', 7);
INSERT INTO `image_file` VALUES (15, '8/aa8162b2-7690-4960-bbb7-b41928dbb2b6.png', 'test', '2025-04-19 21:55:25', '2025-04-19 21:55:25', 8);
INSERT INTO `image_file` VALUES (16, '8/f70dda68-1e25-4e88-b3cc-ad71d71892e8.png', 'test', '2025-04-19 21:55:25', '2025-04-19 21:55:25', 8);
INSERT INTO `image_file` VALUES (19, '10/29a6582a-6b01-4120-a7b2-be65a8f05dd5.png', 'test', '2025-04-19 22:16:53', '2025-04-19 22:16:53', 10);
INSERT INTO `image_file` VALUES (20, '10/ce6eecb0-9516-422c-afb9-b89291da51d7.png', 'test', '2025-04-19 22:16:53', '2025-04-19 22:16:53', 10);
INSERT INTO `image_file` VALUES (21, '11/5101e16f-8b39-407d-957b-820b45c8bde4.png', 'test', '2025-05-12 11:36:24', '2025-05-12 11:36:24', 11);
INSERT INTO `image_file` VALUES (22, '12/4471e601-4138-4267-b878-9eb4d9ae2010.png', 'test', '2025-05-12 13:16:48', '2025-05-12 13:16:48', 12);
INSERT INTO `image_file` VALUES (23, '12/a23c170b-3e88-4c8c-a14b-2aa57eac5247.png', 'test', '2025-05-12 13:16:48', '2025-05-12 13:16:48', 12);
INSERT INTO `image_file` VALUES (24, '13/197add97-6981-409c-9e2e-512b54778f3b.png', 'test', '2025-09-03 19:20:37', '2025-09-03 19:20:37', 13);
INSERT INTO `image_file` VALUES (25, '13/2afe0f67-1793-487e-9025-f11b5d17c41d.png', 'test', '2025-09-03 19:20:37', '2025-09-03 19:20:37', 13);

-- ----------------------------
-- Table structure for segment_model
-- ----------------------------
DROP TABLE IF EXISTS `segment_model`;
CREATE TABLE `segment_model`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `model_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模型名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `architecture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模型架构',
  `dataset_used` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '训练数据集',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新设计',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of segment_model
-- ----------------------------

-- ----------------------------
-- Table structure for user_message
-- ----------------------------
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `timestamp` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `user_id` bigint NOT NULL,
  `message_type` enum('info','warning','error') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'info',
  `is_urgent` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `custom_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_message
-- ----------------------------
INSERT INTO `user_message` VALUES (3, '您有一条新的诊断申请', '患者 testimage 向您提交了一条诊断申请。', '患者 testimage 向您提交了一条诊断申请。\ntestimage\n要求急诊！', '2025-03-23 15:39:51', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (4, '您有一条新的诊断申请', '患者 testimage2 向您提交了一条诊断申请。', '患者 testimage2 向您提交了一条诊断申请。\ntestimage2\n要求急诊！', '2025-03-23 15:45:35', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (5, '您有一条新的诊断申请', '患者 testimage3 向您提交了一条诊断申请。', '患者 testimage3 向您提交了一条诊断申请。\ntestimage3\n要求急诊！', '2025-03-23 15:49:35', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (6, '您有一条新的诊断申请', '患者 testimage4 向您提交了一条诊断申请。', '患者 testimage4 向您提交了一条诊断申请。\ntestimage4\n要求急诊！', '2025-03-25 09:05:52', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (10, '您的诊断申请状态已更新', '医生 testDoctor 已处理您的诊断申请。', '医生 testDoctor 已处理您诊断id为 1 的诊断申请。', '2025-04-01 15:12:54', 1, 5, 'info', 0);
INSERT INTO `user_message` VALUES (12, '您的诊断申请状态已更新', '医生 testDoctor 已处理您的诊断申请。', '医生 testDoctor 已处理您诊断id为 7 的诊断申请。', '2025-04-01 23:26:03', 0, 5, 'info', 0);
INSERT INTO `user_message` VALUES (13, '您有一条新的诊断申请', '患者 韦乐涛 向您提交了一条诊断申请。', '患者 韦乐涛 向您提交了一条诊断申请。\n疼痛，有肿块\n要求急诊！', '2025-04-19 21:55:25', 0, 12, 'info', 1);
INSERT INTO `user_message` VALUES (14, '您有一条新的诊断申请', '患者 韦乐涛 向您提交了一条诊断申请。', '患者 韦乐涛 向您提交了一条诊断申请。\n疼痛，有肿块。\n要求急诊！', '2025-04-19 22:10:23', 0, 14, 'info', 1);
INSERT INTO `user_message` VALUES (15, '您有一条新的诊断申请', '患者 韦乐涛 向您提交了一条诊断申请。', '患者 韦乐涛 向您提交了一条诊断申请。\n疼痛，有肿块。\n要求急诊！', '2025-04-19 22:16:53', 0, 14, 'info', 1);
INSERT INTO `user_message` VALUES (16, '您的诊断申请状态已更新', '医生 Yuxingran 已处理您的诊断申请。', '医生 Yuxingran 已处理您诊断id为 10 的诊断申请。', '2025-04-19 22:21:53', 0, 13, 'info', 0);
INSERT INTO `user_message` VALUES (17, '您有一条新的诊断申请', '患者 tttt 向您提交了一条诊断申请。', '患者 tttt 向您提交了一条诊断申请。\ntest\n要求急诊！', '2025-05-12 11:36:24', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (18, '您有一条新的诊断申请', '患者 TEST 向您提交了一条诊断申请。', '患者 TEST 向您提交了一条诊断申请。\nTEST\n要求急诊！', '2025-05-12 13:16:48', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (19, '您有一条新的诊断申请', '患者 韦乐涛 向您提交了一条诊断申请。', '患者 韦乐涛 向您提交了一条诊断申请。\n疼痛，肿块\n要求急诊！', '2025-09-03 19:20:37', 0, 11, 'info', 1);
INSERT INTO `user_message` VALUES (20, '您的诊断申请状态已更新', '医生 testDoctor 已处理您的诊断申请。', '医生 testDoctor 已处理您诊断id为 13 的诊断申请。', '2025-09-03 19:29:14', 0, 16, 'info', 0);

-- ----------------------------
-- Table structure for user_model
-- ----------------------------
DROP TABLE IF EXISTS `user_model`;
CREATE TABLE `user_model`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `model_id` int NULL DEFAULT NULL COMMENT '分割模型id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `model_id`(`model_id` ASC) USING BTREE,
  CONSTRAINT `user_model_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `custom_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_model_ibfk_2` FOREIGN KEY (`model_id`) REFERENCES `segment_model` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_model
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
