-- Insert sample users
INSERT INTO users (password, email, role)
VALUES
    ('1234', 'john.doe@example.com', 'customer'),
    ('1234', 'jane.smith@example.com', 'customer'),
    ('1234', 'admin@example.com', 'admin');

-- Insert sample cupcake bottoms
INSERT INTO cupcake_bottoms (bottom_name, price)
VALUES
    ('Chokolade', 5.00),
    ('Vanilje', 5.00),
    ('Muskatnød', 2.00),
    ('Pistacie', 6.00),
    ('Mandel', 7.00);

-- Insert sample cupcake toppings
INSERT INTO cupcake_toppings (topping_name, price)
VALUES
    ('Chokolade', 5.00),
    ('Blåbær', 5.00),
    ('Hindbær', 5.00),
    ('Sprød', 6.00),
    ('Jordbær', 6.00),
    ('Rom/Rosin', 7.00),
    ('Appelsin', 8.00),
    ('Citron', 8.00),
    ('Blåskimmelost', 9.00);

-- Insert sample orders
INSERT INTO orders (user_id, order_date)
VALUES
    (1, '2025-03-24 10:30:00'),
    (2, '2025-03-24 11:45:00'),
    (1, '2025-03-25 09:00:00');

-- Insert sample order details
INSERT INTO order_details (order_id, bottom_id, topping_id, quantity, cupcake_price)
VALUES
    (1, 2, 1, 2, 10.00), -- 2 Vanilje cupcakes med Chokolade topping (5 + 5) * 2
    (1, 1, 3, 1, 10.00), -- 1 Chokolade cupcake med Hindbær topping (5 + 5)
    (2, 3, 4, 3, 24.00), -- 3 Muskatnød cupcakes med Sprød topping (2 + 6) * 3
    (3, 4, 5, 1, 13.00); -- 1 Pistacie cupcake med Jordbær topping (6 + 7)
