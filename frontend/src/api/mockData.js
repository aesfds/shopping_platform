export const categories = [
  { id: 1, name: '数码电器', icon: 'Monitor' },
  { id: 2, name: '服饰鞋包', icon: 'Handbag' },
  { id: 3, name: '家居生活', icon: 'House' },
  { id: 4, name: '美妆护理', icon: 'MagicStick' },
  { id: 5, name: '食品生鲜', icon: 'Pear' },
  { id: 6, name: '运动户外', icon: 'Bicycle' }
]

export const products = [
  {
    id: 1,
    name: '无线降噪耳机',
    subtitle: '轻量佩戴，通勤场景推荐',
    description: '主动降噪、长续航和低延迟模式，适合通勤、办公与运动场景。',
    price: 299,
    originalPrice: 399,
    sales: 3180,
    stock: 120,
    tag: '热卖',
    categoryId: 1,
    specs: ['主动降噪', '32 小时续航', '蓝牙 5.3']
  },
  {
    id: 2,
    name: '便携咖啡杯',
    subtitle: '密封防漏，办公室常备',
    description: '一键开合杯盖，杯身轻便，适合咖啡、茶饮和日常出行。',
    price: 59.9,
    originalPrice: 89,
    sales: 2206,
    stock: 260,
    tag: '精选',
    categoryId: 3,
    specs: ['防漏杯盖', '便携杯身', '食品级材质']
  },
  {
    id: 3,
    name: '基础款短袖',
    subtitle: '舒适棉感，多色可选',
    description: '日常百搭版型，亲肤棉感面料，适合春夏通勤与休闲穿搭。',
    price: 79,
    originalPrice: 119,
    sales: 1860,
    stock: 88,
    tag: '新品',
    categoryId: 2,
    specs: ['纯色百搭', '宽松版型', '亲肤棉感']
  },
  {
    id: 4,
    name: '桌面收纳盒',
    subtitle: '模块组合，保持桌面清爽',
    description: '分区收纳设计，可放置文具、数据线、小型数码配件。',
    price: 39.9,
    originalPrice: 59.9,
    sales: 1420,
    stock: 310,
    tag: '好评',
    categoryId: 3,
    specs: ['模块组合', '分格收纳', '耐用材质']
  },
  {
    id: 5,
    name: '温和洁面乳',
    subtitle: '清爽肤感，日常护理',
    description: '温和清洁配方，泡沫细腻，适合日常早晚洁面。',
    price: 49,
    originalPrice: 69,
    sales: 1288,
    stock: 166,
    tag: '补贴',
    categoryId: 4,
    specs: ['温和清洁', '细腻泡沫', '清爽肤感']
  },
  {
    id: 6,
    name: '低脂谷物麦片',
    subtitle: '早餐搭配，轻负担',
    description: '多种谷物混合，搭配牛奶、酸奶或水果都很合适。',
    price: 36.8,
    originalPrice: 49.9,
    sales: 980,
    stock: 220,
    tag: '限时',
    categoryId: 5,
    specs: ['低脂配方', '谷物混合', '早餐常备']
  },
  {
    id: 7,
    name: '速干运动毛巾',
    subtitle: '户外训练便携装',
    description: '速干纤维材质，吸水轻便，适合运动、露营和旅行。',
    price: 29.9,
    originalPrice: 45,
    sales: 870,
    stock: 190,
    tag: '运动',
    categoryId: 6,
    specs: ['速干纤维', '轻量便携', '吸水柔软']
  },
  {
    id: 8,
    name: '智能小夜灯',
    subtitle: '柔光感应，卧室走廊适用',
    description: '人体感应自动点亮，柔和光线适合卧室、走廊和儿童房。',
    price: 69,
    originalPrice: 99,
    sales: 760,
    stock: 140,
    tag: '智能',
    categoryId: 1,
    specs: ['人体感应', '柔和夜光', '低功耗']
  },
  {
    id: 9,
    name: '通勤双肩包',
    subtitle: '多隔层设计，电脑平板都能装',
    description: '通勤场景设计，独立电脑仓和干湿分离口袋提升收纳效率。',
    price: 169,
    originalPrice: 229,
    sales: 640,
    stock: 72,
    tag: '通勤',
    categoryId: 2,
    specs: ['独立电脑仓', '防泼水', '多隔层']
  },
  {
    id: 10,
    name: '鲜果礼盒',
    subtitle: '当季水果组合，礼赠自用都合适',
    description: '精选当季鲜果，冷链配送方案预留，适合家庭分享与礼赠。',
    price: 128,
    originalPrice: 168,
    sales: 520,
    stock: 54,
    tag: '生鲜',
    categoryId: 5,
    specs: ['当季鲜果', '礼盒包装', '冷链预留']
  },
  {
    id: 11,
    name: '家用香薰机',
    subtitle: '细雾扩香，提升居家氛围',
    description: '低噪运行，支持定时关闭，适合卧室、客厅和办公桌。',
    price: 139,
    originalPrice: 189,
    sales: 430,
    stock: 96,
    tag: '家居',
    categoryId: 3,
    specs: ['低噪运行', '定时关闭', '细雾扩香']
  },
  {
    id: 12,
    name: '瑜伽弹力带套装',
    subtitle: '多阻力组合，居家训练可用',
    description: '轻便易收纳，适合拉伸、力量训练和康复辅助训练。',
    price: 45.9,
    originalPrice: 69,
    sales: 390,
    stock: 150,
    tag: '训练',
    categoryId: 6,
    specs: ['多阻力', '易收纳', '居家训练']
  }
]

export const defaultOrders = [
  {
    id: 10001,
    orderNo: 'SP202606160001',
    status: 'pendingPay',
    createdAt: '2026-06-16 10:24',
    totalAmount: 358.9,
    receiver: '张先生',
    items: [
      { productId: 1, name: '无线降噪耳机', price: 299, quantity: 1 },
      { productId: 2, name: '便携咖啡杯', price: 59.9, quantity: 1 }
    ]
  },
  {
    id: 10002,
    orderNo: 'SP202606150018',
    status: 'shipped',
    createdAt: '2026-06-15 18:06',
    totalAmount: 207.8,
    receiver: '李女士',
    items: [
      { productId: 4, name: '桌面收纳盒', price: 39.9, quantity: 1 },
      { productId: 9, name: '通勤双肩包', price: 169, quantity: 1 }
    ]
  },
  {
    id: 10003,
    orderNo: 'SP202606120009',
    status: 'completed',
    createdAt: '2026-06-12 09:35',
    totalAmount: 82.7,
    receiver: '王先生',
    items: [
      { productId: 6, name: '低脂谷物麦片', price: 36.8, quantity: 1 },
      { productId: 12, name: '瑜伽弹力带套装', price: 45.9, quantity: 1 }
    ]
  }
]
