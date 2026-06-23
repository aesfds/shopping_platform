USE shopping_platform;

INSERT INTO home_banners (id, title, subtitle, cta_text, background_color, sort_order, enabled) VALUES
(1, '春季好物上新', '精选家居、数码与日用百货，新季清单一次逛齐', '立即查看', '#1f8a70', 1, TRUE),
(2, '会员专属专区', '会员价、优惠券与专属好物', '进入专区', '#d95d39', 2, TRUE)
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  subtitle = VALUES(subtitle),
  cta_text = VALUES(cta_text),
  background_color = VALUES(background_color),
  sort_order = VALUES(sort_order),
  enabled = VALUES(enabled);

INSERT INTO product_categories (id, name, icon, target_route, sort_order, enabled) VALUES
(1, '数码电器', 'Monitor', '/products?category=1', 1, TRUE),
(2, '服饰鞋包', 'Handbag', '/products?category=2', 2, TRUE),
(3, '家居生活', 'House', '/products?category=3', 3, TRUE),
(4, '美妆护理', 'MagicStick', '/products?category=4', 4, TRUE),
(5, '食品生鲜', 'Pear', '/products?category=5', 5, TRUE),
(6, '运动户外', 'Bicycle', '/products?category=6', 6, TRUE)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  icon = VALUES(icon),
  target_route = VALUES(target_route),
  sort_order = VALUES(sort_order),
  enabled = VALUES(enabled);

INSERT INTO products (id, name, subtitle, price, original_price, sales, tag, image_url, category_id, sort_order, available, created_at) VALUES
(1, '无线降噪耳机', '轻量佩戴，通勤场景推荐', 299.00, 399.00, 3180, '热卖', '', 1, 1, TRUE, '2026-06-01 10:00:00'),
(2, '便携咖啡杯', '密封防漏，办公室常备', 59.90, 89.00, 2206, '精选', '', 3, 2, TRUE, '2026-06-02 10:00:00'),
(3, '基础款短袖', '舒适棉感，多色可扩展', 79.00, 119.00, 1860, '新品', '', 2, 3, TRUE, '2026-06-03 10:00:00'),
(4, '桌面收纳盒', '模块组合，保持桌面清爽', 39.90, 59.90, 1420, '好评', '', 3, 4, TRUE, '2026-06-04 10:00:00'),
(5, '温和洁面乳', '清爽肤感，日常护理', 49.00, 69.00, 1288, '补贴', '', 4, 5, TRUE, '2026-06-05 10:00:00'),
(6, '低脂谷物麦片', '早餐搭配，轻负担', 36.80, 49.90, 980, '限时', '', 5, 6, TRUE, '2026-06-06 10:00:00'),
(7, '速干运动毛巾', '户外训练便携装', 29.90, 45.00, 870, '运动', '', 6, 7, TRUE, '2026-06-07 10:00:00'),
(8, '智能小夜灯', '柔光感应，卧室走廊适用', 69.00, 99.00, 760, '智能', '', 1, 8, TRUE, '2026-06-08 10:00:00')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  subtitle = VALUES(subtitle),
  price = VALUES(price),
  original_price = VALUES(original_price),
  sales = VALUES(sales),
  tag = VALUES(tag),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sort_order = VALUES(sort_order),
  available = VALUES(available),
  created_at = VALUES(created_at);
