INSERT INTO user_management.users(name, email, username, password, reward_points, credit_card, cvc) VALUES
('Alice Johnson', 'alice@example.com', 'alicej', 'abc123', 1200, '4532015112830366', '123'),
('Bob Smith', 'bob.smith@example.com', 'bobsmith', 'password1', 500, '5500005555555559', '456'),
('Charlie Davis', 'charlie.d@example.com', 'charlied', 'pw456', 0, '340000000000009', '7890'),
('Bryan Nguyen', 'bryzntest@gmail.com', 'bryznnguyen', 'pass123', 300, '6011000990139424', '321')
ON CONFLICT DO NOTHING;