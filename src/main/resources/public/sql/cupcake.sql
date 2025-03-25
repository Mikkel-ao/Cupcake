-- Create Users table
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       password VARCHAR(255) NOT NULL, -- Hashed passwords
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(10) DEFAULT 'customer', -- 'customer' or 'admin'
                       balance DECIMAL(8, 2) DEFAULT 200.00
);

-- Create CupcakeBottoms table
CREATE TABLE cupcake_bottoms (
                                bottom_id SERIAL PRIMARY KEY,
                                bottom_name VARCHAR(50) NOT NULL,
                                price DECIMAL(10, 2) NOT NULL
);

-- Create CupcakeToppings table
CREATE TABLE cupcake_toppings (
                                 topping_id SERIAL PRIMARY KEY,
                                 topping_name VARCHAR(50) NOT NULL,
                                 price DECIMAL(10, 2) NOT NULL
);

-- Create Orders table
CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(user_id),
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create OrderDetails table
CREATE TABLE order_details (
                              order_detail_id SERIAL PRIMARY KEY,
                              order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                              bottom_id INT NOT NULL REFERENCES cupcake_bottoms(bottom_id),
                              topping_id INT NOT NULL REFERENCES cupcake_toppings(topping_id),
                              quantity INT NOT NULL CHECK (quantity > 0),
                              cupcake_price DECIMAL(10, 2) NOT NULL
);