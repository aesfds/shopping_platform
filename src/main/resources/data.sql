MERGE INTO home_banners (id, title, subtitle, cta_text, background_color, sort_order, enabled) KEY(id)
VALUES (1, '春季好物上新', '精选家居、数码与日用百货，新季清单一次逛齐', '立即查看', '#1f8a70', 1, TRUE);

MERGE INTO home_banners (id, title, subtitle, cta_text, background_color, sort_order, enabled) KEY(id)
VALUES (2, '会员专属专区', '会员价、优惠券与专属好物', '进入专区', '#d95d39', 2, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (1, '数码电器', 'Monitor', '/products?category=1', 1, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (2, '服饰鞋包', 'Handbag', '/products?category=2', 2, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (3, '家居生活', 'House', '/products?category=3', 3, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (4, '美妆护理', 'MagicStick', '/products?category=4', 4, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (5, '食品生鲜', 'Pear', '/products?category=5', 5, TRUE);

MERGE INTO product_categories (id, name, icon, target_route, sort_order, enabled) KEY(id)
VALUES (6, '运动户外', 'Bicycle', '/products?category=6', 6, TRUE);

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (1, '无线降噪耳机', '轻量佩戴，通勤场景推荐', '主动降噪、长续航和低延迟模式，适合通勤、办公与运动场景。', 299.00, 399.00, 3180, '热卖', '', 120, '主动降噪|32 小时续航|蓝牙 5.3', 1, 1, TRUE, TIMESTAMP '2026-06-01 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (2, '便携咖啡杯', '密封防漏，办公室常备', '一键开合杯盖，杯身轻便，适合咖啡、茶饮和日常出行。', 59.90, 89.00, 2206, '精选', '', 260, '防漏杯盖|便携杯身|食品级材质', 3, 2, TRUE, TIMESTAMP '2026-06-02 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (3, '基础款短袖', '舒适棉感，多色可选', '日常百搭版型，亲肤棉感面料，适合春夏通勤与休闲穿搭。', 79.00, 119.00, 1860, '新品', '', 88, '纯色百搭|宽松版型|亲肤棉感', 2, 3, TRUE, TIMESTAMP '2026-06-03 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (4, '桌面收纳盒', '模块组合，保持桌面清爽', '分区收纳设计，可放置文具、数据线、小型数码配件。', 39.90, 59.90, 1420, '好评', '', 310, '模块组合|分格收纳|耐用材质', 3, 4, TRUE, TIMESTAMP '2026-06-04 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (5, '温和洁面乳', '清爽肤感，日常护理', '温和清洁配方，泡沫细腻，适合日常早晚洁面。', 49.00, 69.00, 1288, '补贴', '', 166, '温和清洁|细腻泡沫|清爽肤感', 4, 5, TRUE, TIMESTAMP '2026-06-05 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (6, '低脂谷物麦片', '早餐搭配，轻负担', '多种谷物混合，搭配牛奶、酸奶或水果都很合适。', 36.80, 49.90, 980, '限时', '', 220, '低脂配方|谷物混合|早餐常备', 5, 6, TRUE, TIMESTAMP '2026-06-06 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (7, '速干运动毛巾', '户外训练便携装', '速干纤维材质，吸水轻便，适合运动、露营和旅行。', 29.90, 45.00, 870, '运动', '', 190, '速干纤维|轻量便携|吸水柔软', 6, 7, TRUE, TIMESTAMP '2026-06-07 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (8, '智能小夜灯', '柔光感应，卧室走廊适用', '人体感应自动点亮，柔和光线适合卧室、走廊和儿童房。', 69.00, 99.00, 760, '智能', '', 140, '人体感应|柔和夜光|低功耗', 1, 8, TRUE, TIMESTAMP '2026-06-08 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (9, '通勤双肩包', '多隔层设计，电脑平板都能装', '通勤场景设计，独立电脑仓和干湿分离口袋提升收纳效率。', 169.00, 229.00, 640, '通勤', '', 72, '独立电脑仓|防泼水|多隔层', 2, 9, TRUE, TIMESTAMP '2026-06-09 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (10, '鲜果礼盒', '当季水果组合，礼赠自用都合适', '精选当季鲜果，冷链配送方案预留，适合家庭分享与礼赠。', 128.00, 168.00, 520, '生鲜', '', 54, '当季鲜果|礼盒包装|冷链预留', 5, 10, TRUE, TIMESTAMP '2026-06-10 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (11, '家用香薰机', '细雾扩香，提升居家氛围', '低噪运行，支持定时关闭，适合卧室、客厅和办公桌。', 139.00, 189.00, 430, '家居', '', 96, '低噪运行|定时关闭|细雾扩香', 3, 11, TRUE, TIMESTAMP '2026-06-11 10:00:00');

MERGE INTO products (id, name, subtitle, description, price, original_price, sales, tag, image_url, stock, specs, category_id, sort_order, available, created_at) KEY(id)
VALUES (12, '瑜伽弹力带套装', '多阻力组合，居家训练可用', '轻便易收纳，适合拉伸、力量训练和康复辅助训练。', 45.90, 69.00, 390, '训练', '', 150, '多阻力|易收纳|居家训练', 6, 12, TRUE, TIMESTAMP '2026-06-12 10:00:00');
