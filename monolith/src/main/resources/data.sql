-- 商品演示数据（MySQL），使用主键 + ON DUPLICATE KEY UPDATE 保证可重复执行
INSERT INTO product (id, name, category, price, stock, on_sale) VALUES
    (1, 'Kitty 丝绒口红', '美妆', 99.00, 100, 1),
    (2, 'Kitty 联名连衣裙', '服饰', 299.00, 50, 1),
    (3, 'Kitty 婴儿安抚巾', '母婴', 59.00, 0, 0)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    category = VALUES(category),
    price = VALUES(price),
    stock = VALUES(stock),
    on_sale = VALUES(on_sale);