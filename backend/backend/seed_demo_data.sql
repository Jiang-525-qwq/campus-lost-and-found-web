DELETE FROM comments
WHERE item_id IN (
  SELECT item_id FROM items WHERE description LIKE '[SEED]%'
);

DELETE FROM reports
WHERE item_id IN (
  SELECT item_id FROM items WHERE description LIKE '[SEED]%'
)
OR reporter_id IN (
  SELECT user_id FROM users WHERE student_id LIKE 'SEED2026%'
);

DELETE FROM items
WHERE description LIKE '[SEED]%';

DELETE FROM users
WHERE student_id LIKE 'SEED2026%';

INSERT INTO users (student_id, username, password, phone, email, role, created_at, updated_at, status) VALUES
('SEED2026001', '测试用户A', '123456', '13800001001', 'seed_a@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026002', '测试用户B', '123456', '13800001002', 'seed_b@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026003', '测试用户C', '123456', '13800001003', 'seed_c@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026004', '测试用户D', '123456', '13800001004', 'seed_d@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026005', '测试用户E', '123456', '13800001005', 'seed_e@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026006', '测试用户F', '123456', '13800001006', 'seed_f@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026007', '测试用户G', '123456', '13800001007', 'seed_g@example.com', 'user', NOW(), NOW(), '正常'),
('SEED2026008', '测试用户H', '123456', '13800001008', 'seed_h@example.com', 'user', NOW(), NOW(), '正常');

INSERT INTO items (user_id, category_id, item_name, description, item_type, lost_found_time, location, image_url, status, created_at, updated_at) VALUES
((SELECT user_id FROM users WHERE student_id='SEED2026001'), 1, '校园卡', '[SEED] 蓝色卡套，联系方式: 13800001001', 'lost', '2026-04-16 08:20:00', '图书馆 - 老图书馆', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026002'), 2, 'AirPods 右耳', '[SEED] 白色耳机，联系方式: 13800001002', 'lost', '2026-04-17 12:30:00', '食堂 - 樱花食堂', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026003'), 3, '黑色雨伞', '[SEED] 长柄雨伞，联系方式: 13800001003', 'found', '2026-04-15 18:10:00', '教学楼 - 二教', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026004'), 4, '高数教材', '[SEED] 封面有姓名，联系方式: 13800001004', 'lost', '2026-04-18 09:05:00', '教学楼 - 六教', NULL, 0, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026005'), 2, '充电宝', '[SEED] 黑色 20000mAh，联系方式: 13800001005', 'found', '2026-04-14 14:40:00', '宿舍区 - 明理', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026006'), 1, '身份证', '[SEED] 姓名首字母为L，联系方式: 13800001006', 'found', '2026-04-13 11:50:00', '图书馆 - 数字图书馆', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026007'), 5, '钥匙串', '[SEED] 挂着小熊挂件，联系方式: 13800001007', 'lost', '2026-04-17 21:15:00', '宿舍区 - 兴业', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026008'), 2, '机械键盘', '[SEED] 87键白色键盘，联系方式: 13800001008', 'lost', '2026-04-12 16:25:00', '其他 - 快递点', NULL, 2, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026001'), 3, '水杯', '[SEED] 银色保温杯，联系方式: 13800001001', 'found', '2026-04-11 07:45:00', '运动场 - 风华操场', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026002'), 4, 'U盘', '[SEED] 黑色 64G，联系方式: 13800001002', 'lost', '2026-04-18 10:10:00', '教学楼 - 一教', NULL, 1, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026003'), 3, '钱包', '[SEED] 棕色短款钱包，联系方式: 13800001003', 'lost', '2026-04-10 19:20:00', '食堂 - 中心食堂', NULL, 3, NOW(), NOW()),
((SELECT user_id FROM users WHERE student_id='SEED2026004'), 5, '运动手环', '[SEED] 黑色手环，联系方式: 13800001004', 'found', '2026-04-09 20:05:00', '运动场 - 灯光篮球场', NULL, 4, NOW(), NOW());

INSERT INTO reports (item_id, reporter_id, reason, description, status, created_at, reported_user_id) VALUES
((SELECT item_id FROM items WHERE item_name='高数教材' AND description LIKE '[SEED]%'), (SELECT user_id FROM users WHERE student_id='SEED2026001'), '信息虚假', '[SEED] 内容疑似测试占位', '待处理', CURRENT_TIME(), (SELECT user_id FROM users WHERE student_id='SEED2026004')),
((SELECT item_id FROM items WHERE item_name='充电宝' AND description LIKE '[SEED]%'), (SELECT user_id FROM users WHERE student_id='SEED2026002'), '广告骚扰', '[SEED] 描述里联系方式过多', '待处理', CURRENT_TIME(), (SELECT user_id FROM users WHERE student_id='SEED2026005')),
((SELECT item_id FROM items WHERE item_name='机械键盘' AND description LIKE '[SEED]%'), (SELECT user_id FROM users WHERE student_id='SEED2026006'), '其他', '[SEED] 信息可能已经失效', '已驳回', CURRENT_TIME(), (SELECT user_id FROM users WHERE student_id='SEED2026008'));
