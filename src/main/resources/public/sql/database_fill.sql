-- Insert sample users
INSERT INTO users (username, password, email, role)
VALUES
    ('John', '1234', 'john.doe@example.com', 'customer'),
    ('Hanne', '1234', 'jane.smith@example.com', 'customer'),
    ('admin_user', '1234', 'admin@example.com', 'admin');

-- Insert sample cupcake bottoms
INSERT INTO cupcake_bottoms (bottom_name, price)
VALUES
    ('Chocplate', 5.00),
    ('Vanilla', 5.00),
    ('Nutmeg', 2.00),
    ('Pistacio', 6.00),
    ('Almond', 7.00);

-- Insert sample cupcake toppings
INSERT INTO cupcake_toppings (topping_name, price)
VALUES
    ('Chocolate', 5.00),
    ('Blueberry', 5.00),
    ('Raspberry', 5.00),
    ('Crispy', 6.00),
    ('Strawberry', 6.00),
    ('Rum/Raisin', 7.00),
    ('Orange', 8.00),
    ('Lemon', 8.00),
    ('Blue cheese', 9.00);

-- Insert sample orders
INSERT INTO orders (user_id, order_date)
VALUES
    (1, '2025-03-24 10:30:00'),
    (2, '2025-03-24 11:45:00'),
    (1, '2025-03-25 09:00:00');

-- Insert sample order details
INSERT INTO order_details (order_id, bottom_id, topping_id, quantity, cupcake_price)
VALUES
    (1, 1, 1, 2, 2.25), -- 2 Vanilla cupcakes with Chocolate Frosting
    (1, 2, 3, 1, 2.00), -- 1 Chocolate cupcake with Sprinkles
    (2, 3, 4, 3, 2.60), -- 3 Red Velvet cupcakes with Caramel Drizzle
    (3, 4, 5, 1, 2.20); -- 1 Carrot Cake cupcake with Whipped Cream