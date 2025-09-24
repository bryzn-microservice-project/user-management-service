CREATE TABLE IF NOT EXISTS user_management.users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(20) NOT NULL,
    reward_points INT CHECK (reward_points >= 0),
    credit_card VARCHAR(19) CHECK (credit_card ~ '^[0-9]{13,19}$'),
    cvc VARCHAR(4) CHECK (cvc ~ '^[0-9]{3,4}$')
);